package com.xesnet.runui;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.xesnet.runui.model.Run;
import com.xesnet.runui.model.RunState;
import com.xesnet.runui.model.SshRun;
import com.xesnet.runui.model.SshServer;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * @author Pierre PINON
 */
public class RunExecutor {

    private ScheduledExecutorService executor;

    private final int numberOfThreads;
    private final int outputPollInterval;
    private final int runCleanInterval;
    private final int runRetention;

    private HashSet<Run> runs;

    public RunExecutor(int numberOfThreads, int outputPollInterval, int runCleanInterval, int runRetention) {
        this.numberOfThreads = numberOfThreads;
        this.outputPollInterval = outputPollInterval;
        this.runCleanInterval = runCleanInterval;
        this.runRetention = runRetention;
    }

    public void init() {
        executor = Executors.newScheduledThreadPool(numberOfThreads);
        this.runs = new HashSet<>();

        executor.scheduleAtFixedRate(this::cleanRun, this.runCleanInterval, this.runCleanInterval, TimeUnit.SECONDS);
    }

    public Run execute(SshRun sshRun, SshServer sshServer) {
        Run run = new Run();
        run.setId(UUID.randomUUID().toString());
        run.setRunName(sshRun.getName());
        run.setState(RunState.INIT);
        run.updateLocalDateTime();
        setRun(run);

        Runnable runnable = () -> {
            JSch jsch = new JSch();
            try {
                Session session = jsch.getSession(sshServer.getLogin(), sshServer.getHost(), sshServer.getPort());
                java.util.Properties jschConfig = new java.util.Properties();
                jschConfig.put("StrictHostKeyChecking", "no");
                session.setConfig(jschConfig);
                session.setPassword(sshServer.getPassword());

                session.connect();
                run.setState(RunState.CONNECT);
                setRun(run);

                ChannelExec channel = (ChannelExec) session.openChannel("exec");
                channel.setCommand(String.join(";", sshRun.getCommands()));

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                channel.setOutputStream(outputStream);

                channel.connect();
                run.setState(RunState.SUBMIT);
                run.updateLocalDateTime();
                setRun(run);

                while (!channel.isClosed()) {
                    try {
                        Thread.sleep(outputPollInterval);
                    } catch (InterruptedException e) {
                        //Do nothing
                    }
                }
                String responseString = new String(outputStream.toByteArray());

                run.setState(RunState.SUCCESS);
                run.setOutput(responseString);
                run.setExitCode(channel.getExitStatus());
                run.updateLocalDateTime();
                setRun(run);

                channel.disconnect();
                session.disconnect();

            } catch (JSchException e) {
                e.printStackTrace();
                run.setState(RunState.FAILED);
                run.updateLocalDateTime();
                setRun(run);
            }
        };

        executor.submit(runnable);

        return run;
    }

    public synchronized Run getRun(String id) {
        return runs.stream()
                .filter(run -> run.getId().equals(id))
                .findFirst()
                .map(Run::clone)
                .orElse(null);
    }

    private synchronized void setRun(Run run) {
        runs.removeIf(ci -> ci.getId().equals(run.getId()));
        runs.add(run.clone());
    }

    public synchronized void cleanRun() {
        LocalDateTime limit = LocalDateTime.now().minusSeconds(this.runRetention);
        runs.removeIf(run -> run.getLocalDateTime().isBefore(limit));
    }
}
