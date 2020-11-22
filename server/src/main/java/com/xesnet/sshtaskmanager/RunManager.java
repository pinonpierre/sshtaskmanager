package com.xesnet.sshtaskmanager;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.xesnet.sshtaskmanager.model.Condition;
import com.xesnet.sshtaskmanager.model.ConditionOperator;
import com.xesnet.sshtaskmanager.model.ConditionType;
import com.xesnet.sshtaskmanager.model.Job;
import com.xesnet.sshtaskmanager.model.Process;
import com.xesnet.sshtaskmanager.model.ProcessRun;
import com.xesnet.sshtaskmanager.model.ProcessRunState;
import com.xesnet.sshtaskmanager.model.RunManagerConfig;
import com.xesnet.sshtaskmanager.model.Sequence;
import com.xesnet.sshtaskmanager.model.SequenceRun;
import com.xesnet.sshtaskmanager.model.SequenceRunState;
import com.xesnet.sshtaskmanager.model.Server;
import com.xesnet.sshtaskmanager.yaml.Yaml;
import com.xesnet.sshtaskmanager.yaml.YamlContext;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.io.ByteArrayOutputStream;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
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
    private final Yaml yaml;

    private HashSet<ProcessRun> processRuns;
    private HashSet<SequenceRun> sequenceRuns;

    public RunManager(RunManagerConfig config, Yaml yaml) {
        this.config = config;
        this.yaml = yaml;
    }

    public void init() {
        executor = Executors.newScheduledThreadPool(config.getNumberOfThreads());
        this.processRuns = new HashSet<>();
        this.sequenceRuns = new HashSet<>();

        executor.scheduleAtFixedRate(() -> {
            cleanProcessRun();
            cleanSequenceRun();
        }, config.getCleanInterval(), config.getCleanInterval(), TimeUnit.SECONDS);
    }

    public ProcessRunExecution execute(Process process, String login) {
        ProcessRun processRun = new ProcessRun();
        processRun.setId(UUID.randomUUID().toString());
        processRun.setName(process.getName());
        processRun.setState(ProcessRunState.INIT);
        processRun.updateLocalDateTime();
        setProcessRun(processRun);

        Server tempServer;
        try {
            tempServer = yaml.readServers().getServers().stream()
                    .filter(ss -> ss.getName().equals(process.getServerName()))
                    .findFirst()
                    .orElseThrow(() -> new WebApplicationException(Response.Status.NOT_FOUND));
        } catch (YamlContext.YamlContextException e) {
            processRun.setState(ProcessRunState.FAILED);
            processRun.updateLocalDateTime();
            LOG.log(Level.SEVERE, MessageFormat.format("[RunManager] [Process] [{0}] [{1}]", processRun.getId(), processRun.getState()), e);
            setProcessRun(processRun);
            return new ProcessRunExecution(processRun, null);
        }
        Server server = tempServer;

        LOG.fine(MessageFormat.format("[RunManager] [Process] [{0}] [{1}] Run \"{2}\" from Server \"{3}\" by User \"{4}\"", processRun.getId(), processRun.getState(), process.getName(), tempServer.getName(), login));

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
                processRun.setState(ProcessRunState.CONNECT);
                setProcessRun(processRun);
                LOG.finer(MessageFormat.format("[RunManager] [Process] [{0}] [{1}] Run \"{2}\" from Server \"{3}\" by User \"{4}\"", processRun.getId(), processRun.getState(), process.getName(), server.getName(), login));

                ChannelExec channel = (ChannelExec) session.openChannel("exec");
                channel.setCommand(String.join(";", process.getCommands()));

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                channel.setOutputStream(outputStream);

                channel.connect();
                processRun.setState(ProcessRunState.SUBMIT);
                processRun.updateLocalDateTime();
                setProcessRun(processRun);
                LOG.finer(MessageFormat.format("[RunManager] [Process] [{0}] [{1}]", processRun.getId(), processRun.getState()));

                boolean noTimeout = false;
                while (!channel.isClosed() && (noTimeout = limit.isAfter(LocalDateTime.now()))) {
                    try {
                        Thread.sleep(config.getStatusPollInterval());
                    } catch (InterruptedException e) {
                        //Do nothing
                    }
                }
                String responseString = new String(outputStream.toByteArray());

                ProcessRunState newState = noTimeout ? ProcessRunState.SUCCESS : ProcessRunState.TIMEOUT;
                processRun.setState(newState);
                processRun.setOutput(responseString);
                processRun.setExitCode(channel.getExitStatus());
                processRun.updateLocalDateTime();
                setProcessRun(processRun);
                LOG.finer(MessageFormat.format("[RunManager] [Process] [{0}] [{1}]", processRun.getId(), processRun.getState()));

                channel.disconnect();
                session.disconnect();
            } catch (JSchException e) {
                processRun.setState(ProcessRunState.FAILED);
                processRun.updateLocalDateTime();
                LOG.log(Level.SEVERE, MessageFormat.format("[RunManager] [Process] [{0}] [{1}]", processRun.getId(), processRun.getState()), e);
                setProcessRun(processRun);
            }
        };

        Future<?> future = executor.submit(runnable);

        return new ProcessRunExecution(processRun, future);
    }

    public synchronized ProcessRun getProcessRun(String id) {
        return processRuns.stream()
                .filter(run -> run.getId().equals(id))
                .findFirst()
                .map(ProcessRun::clone)
                .orElse(null);
    }

    private synchronized void setProcessRun(ProcessRun processRun) {
        processRuns.removeIf(ci -> ci.getId().equals(processRun.getId()));
        processRuns.add(processRun.clone());
    }

    private synchronized void cleanProcessRun() {
        LocalDateTime limit = LocalDateTime.now().minusSeconds(config.getRetention());
        int total = processRuns.size();
        processRuns.removeIf(run -> run.getLocalDateTime().isBefore(limit));
        int cleaned = total - processRuns.size();
        if (cleaned != total) {
            LOG.finer(MessageFormat.format("[RunManager] [Process] [Clean] {0}/{1} removed", cleaned, total));
        }
    }

    public SequenceRun execute(Sequence sequence, String login) {
        SequenceRun sequenceRun = new SequenceRun();
        sequenceRun.setId(UUID.randomUUID().toString());
        sequenceRun.setName(sequence.getName());
        sequenceRun.setState(SequenceRunState.INIT);
        sequenceRun.updateLocalDateTime();
        setSequenceRun(sequenceRun);
        LOG.fine(MessageFormat.format("[RunManager] [Sequence] [{0}] [{1}] Sequence \"{2}\" by User \"{3}\"", sequenceRun.getId(), sequenceRun.getState(), sequence.getName(), login));

        Runnable runnable = () -> {
            sequenceRun.setState(SequenceRunState.SUBMIT);
            sequenceRun.updateLocalDateTime();
            setSequenceRun(sequenceRun);
            LOG.finer(MessageFormat.format("[RunManager] [Sequence] [{0}] [{1}]", sequenceRun.getId(), sequenceRun.getState()));

            Job job = sequence.getJob();

            try {
                while (job != null) {
                    Job finalJob = job;
                    Process process = yaml.readProcesses().getProcesses().stream()
                            .filter(p -> p.getName().equals(finalJob.getProcessName()))
                            .findFirst()
                            .orElseThrow(() -> new WebApplicationException(Response.Status.NOT_FOUND));

                    ProcessRunExecution processRunExecution = execute(process, login);
                    ProcessRun processRun = processRunExecution.getProcessRun();
                    LOG.finer(MessageFormat.format("[RunManager] [Sequence] [{0}] Job \"{1}\" ({2})", sequenceRun.getId(), job.getName(), processRun.getId()));
                    Future<?> future = processRunExecution.getFuture();
                    if (future != null) {
                        future.get();
                    }
                    //Update
                    processRun = getProcessRun(processRun.getId());

                    sequenceRun.addJob(job.getName());
                    sequenceRun.setOutput(processRun.getOutput());
                    sequenceRun.setExitCode(processRun.getExitCode());
                    sequenceRun.updateLocalDateTime();
                    setSequenceRun(sequenceRun);

                    List<Condition> conditions = job.getConditions();
                    job = null;
                    if (conditions != null) {
                        for (Condition condition: conditions) {
                            if (condition.getType() == ConditionType.RETURN_CODE) {
                                if ((condition.getOperator() == ConditionOperator.EQUAL && condition.getValue().equals(processRun.getExitCode().toString()))
                                || (condition.getOperator() == ConditionOperator.NOT_EQUAL && !condition.getValue().equals(processRun.getExitCode().toString()))) {
                                    LOG.finer(MessageFormat.format("[RunManager] [Sequence] [{0}] [Condition] {1}={2} Then {3} => YES", sequenceRun.getId(), condition.getOperator(), condition.getValue(), condition.getThen().getName()));
                                    job = condition.getThen();
                                    break;
                                } else {
                                    LOG.finer(MessageFormat.format("[RunManager] [Sequence] [{0}] [Condition] {1}={2} Then {3} => NO", sequenceRun.getId(), condition.getOperator(), condition.getValue(), condition.getThen().getName()));
                                }
                            }
                        }
                    }
                }
            } catch (YamlContext.YamlContextException | InterruptedException | ExecutionException e) {
                sequenceRun.setState(SequenceRunState.ERROR);
                sequenceRun.updateLocalDateTime();
                setSequenceRun(sequenceRun);
                LOG.log(Level.SEVERE, MessageFormat.format("[RunManager] [Sequence] [{0}] [{1}] > {2}", sequenceRun.getId(), sequenceRun.getState(), sequenceRun.getJobs() == null ? "" : String.join(" > ", sequenceRun.getJobs())));
                return;
            }

            sequenceRun.setState(SequenceRunState.DONE);
            sequenceRun.updateLocalDateTime();
            setSequenceRun(sequenceRun);
            LOG.finer(MessageFormat.format("[RunManager] [Sequence] [{0}] [{1}] > {2}", sequenceRun.getId(), sequenceRun.getState(), sequenceRun.getJobs() == null ? "" : String.join(" > ", sequenceRun.getJobs())));
        };

        executor.submit(runnable);

        return sequenceRun;
    }

    public synchronized SequenceRun getSequenceRun(String id) {
        return sequenceRuns.stream()
                .filter(run -> run.getId().equals(id))
                .findFirst()
                .map(SequenceRun::clone)
                .orElse(null);
    }

    private synchronized void setSequenceRun(SequenceRun sequenceRun) {
        sequenceRuns.removeIf(ci -> ci.getId().equals(sequenceRun.getId()));
        sequenceRuns.add(sequenceRun.clone());
    }

    private synchronized void cleanSequenceRun() {
        LocalDateTime limit = LocalDateTime.now().minusSeconds(config.getRetention());
        int total = sequenceRuns.size();
        sequenceRuns.removeIf(run -> run.getLocalDateTime().isBefore(limit));
        int cleaned = total - sequenceRuns.size();
        if (cleaned != total) {
            LOG.finer(MessageFormat.format("[RunManager] [Sequence] [Clean] {0}/{1} removed", cleaned, total));
        }
    }

    public static class ProcessRunExecution {

        private ProcessRun processRun;
        private Future<?> future;

        public ProcessRunExecution(ProcessRun processRun, Future<?> future) {
            this.processRun = processRun;
            this.future = future;
        }

        public ProcessRun getProcessRun() {
            return processRun;
        }

        public Future<?> getFuture() {
            return future;
        }
    }
}
