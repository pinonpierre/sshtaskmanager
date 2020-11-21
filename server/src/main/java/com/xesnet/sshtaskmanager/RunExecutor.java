package com.xesnet.sshtaskmanager;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.xesnet.sshtaskmanager.model.Run;
import com.xesnet.sshtaskmanager.model.RunState;
import com.xesnet.sshtaskmanager.model.SshRun;
import com.xesnet.sshtaskmanager.model.SshServer;

import java.io.ByteArrayOutputStream;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * @author Pierre PINON
 */
public class RunExecutor {
    private final Logger LOG = Logger.getLogger(RunExecutor.class.getName());

    private ScheduledExecutorService executor;

    private final int numberOfThreads;
    private final int statusPollInterval;
    private final int cleanInterval;
    private final int retention;
    private final int timeout;

    private HashSet<Run> runs;

    public RunExecutor(int numberOfThreads, int statusPollInterval, int timeout, int cleanInterval, int retention) {
        this.numberOfThreads = numberOfThreads;
        this.statusPollInterval = statusPollInterval;
        this.timeout = timeout;
        this.cleanInterval = cleanInterval;
        this.retention = retention;
    }

    public void init() {
        executor = Executors.newScheduledThreadPool(numberOfThreads);
        this.runs = new HashSet<>();

        executor.scheduleAtFixedRate(this::cleanRun, this.cleanInterval, this.cleanInterval, TimeUnit.SECONDS);
    }

    public Run execute(SshRun sshRun, SshServer sshServer, String login) {
        Run run = new Run();
        run.setId(UUID.randomUUID().toString());
        run.setName(sshRun.getName());
        run.setState(RunState.INIT);
        run.updateLocalDateTime();
        setRun(run);
        LOG.fine(MessageFormat.format("[RunExecutor] [{0}] Run \"{1}\" from Server \"{2}\" by User \"{3}\"", run.getState(), sshRun.getName(), sshServer.getName(), login));

        LocalDateTime limit = LocalDateTime.now().plusSeconds(this.timeout);

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
                LOG.finer(MessageFormat.format("[RunExecutor] [{0}] [{1}] Run \"{2}\" from Server \"{3}\" by User \"{4}\"", run.getId(), run.getState(), sshRun.getName(), sshServer.getName(), login));

                ChannelExec channel = (ChannelExec) session.openChannel("exec");
                channel.setCommand(String.join(";", sshRun.getCommands()));

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                channel.setOutputStream(outputStream);

                channel.connect();
                run.setState(RunState.SUBMIT);
                run.updateLocalDateTime();
                setRun(run);
                LOG.finer(MessageFormat.format("[RunExecutor] [{0}] [{1}]", run.getId(), run.getState()));

                boolean noTimeout = false;
                while (!channel.isClosed() && (noTimeout = limit.isAfter(LocalDateTime.now()))) {
                    try {
                        Thread.sleep(statusPollInterval);
                    } catch (InterruptedException e) {
                        //Do nothing
                    }
                }
                String responseString = new String(outputStream.toByteArray());

                RunState newState = noTimeout ? RunState.SUCCESS : RunState.TIMEOUT;
                run.setState(newState);
                run.setOutput(responseString);
                run.setExitCode(channel.getExitStatus());
                run.updateLocalDateTime();
                setRun(run);
                LOG.finer(MessageFormat.format("[RunExecutor] [{0}] [{1}]", run.getId(), run.getState()));

                channel.disconnect();
                session.disconnect();
            } catch (JSchException e) {
                run.setState(RunState.FAILED);
                run.updateLocalDateTime();
                LOG.log(Level.SEVERE, MessageFormat.format("[RunExecutor] [{0}] [{1}]}", run.getId(), run.getState()), e);
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

    private synchronized void cleanRun() {
        LocalDateTime limit = LocalDateTime.now().minusSeconds(this.retention);
        int total = runs.size();
        runs.removeIf(run -> run.getLocalDateTime().isBefore(limit));
        int cleaned = total - runs.size();
        if (cleaned != total) {
            LOG.finer(MessageFormat.format("[RunExecutor] [Clean] {0}/{1} removed", cleaned, total));
        }
    }
}
