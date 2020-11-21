package com.xesnet.sshtaskmanager;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.xesnet.sshtaskmanager.model.Process;
import com.xesnet.sshtaskmanager.model.Run;
import com.xesnet.sshtaskmanager.model.RunManagerConfig;
import com.xesnet.sshtaskmanager.model.RunState;
import com.xesnet.sshtaskmanager.model.Server;

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
public class RunManager {

    private final Logger LOG = Logger.getLogger(RunManager.class.getName());

    private ScheduledExecutorService executor;

    private final RunManagerConfig config;

    private HashSet<Run> runs;

    public RunManager(RunManagerConfig config) {
        this.config = config;
    }

    public void init() {
        executor = Executors.newScheduledThreadPool(config.getNumberOfThreads());
        this.runs = new HashSet<>();

        executor.scheduleAtFixedRate(this::cleanRun, config.getCleanInterval(), config.getCleanInterval(), TimeUnit.SECONDS);
    }

    public Run execute(Process process, Server server, String login) {
        Run run = new Run();
        run.setId(UUID.randomUUID().toString());
        run.setName(process.getName());
        run.setState(RunState.INIT);
        run.updateLocalDateTime();
        setRun(run);
        LOG.fine(MessageFormat.format("[RunManager] [{0}] Run \"{1}\" from Server \"{2}\" by User \"{3}\"", run.getState(), process.getName(), server.getName(), login));

        LocalDateTime limit = LocalDateTime.now().plusSeconds(config.getTimeout());

        Runnable runnable = () -> {
            JSch jsch = new JSch();
            try {
                if (server.getPublicKey() != null && server.getPrivateKey() != null) {
                    jsch.addIdentity(null, server.getPrivateKey().getBytes(), server.getPublicKey().getBytes(), server.getPassphrase() == null ? null : server.getPassphrase().getBytes());
                }

                Session session = jsch.getSession(server.getLogin(), server.getHost(), server.getPort());
                java.util.Properties jschConfig = new java.util.Properties();
                jschConfig.put("StrictHostKeyChecking", "no");
                session.setConfig(jschConfig);
                if (server.getPassword() != null) {
                    session.setPassword(server.getPassword());
                }

                session.connect();
                run.setState(RunState.CONNECT);
                setRun(run);
                LOG.finer(MessageFormat.format("[RunManager] [{0}] [{1}] Run \"{2}\" from Server \"{3}\" by User \"{4}\"", run.getId(), run.getState(), process.getName(), server.getName(), login));

                ChannelExec channel = (ChannelExec) session.openChannel("exec");
                channel.setCommand(String.join(";", process.getCommands()));

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                channel.setOutputStream(outputStream);

                channel.connect();
                run.setState(RunState.SUBMIT);
                run.updateLocalDateTime();
                setRun(run);
                LOG.finer(MessageFormat.format("[RunManager] [{0}] [{1}]", run.getId(), run.getState()));

                boolean noTimeout = false;
                while (!channel.isClosed() && (noTimeout = limit.isAfter(LocalDateTime.now()))) {
                    try {
                        Thread.sleep(config.getStatusPollInterval());
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
                LOG.finer(MessageFormat.format("[RunManager] [{0}] [{1}]", run.getId(), run.getState()));

                channel.disconnect();
                session.disconnect();
            } catch (JSchException e) {
                run.setState(RunState.FAILED);
                run.updateLocalDateTime();
                LOG.log(Level.SEVERE, MessageFormat.format("[RunManager] [{0}] [{1}]", run.getId(), run.getState()), e);
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
        LocalDateTime limit = LocalDateTime.now().minusSeconds(config.getRetention());
        int total = runs.size();
        runs.removeIf(run -> run.getLocalDateTime().isBefore(limit));
        int cleaned = total - runs.size();
        if (cleaned != total) {
            LOG.finer(MessageFormat.format("[RunManager] [Clean] {0}/{1} removed", cleaned, total));
        }
    }
}
