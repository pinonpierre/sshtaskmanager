package com.xesnet.sshtaskmanager;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.xesnet.sshtaskmanager.model.Condition;
import com.xesnet.sshtaskmanager.model.Job;
import com.xesnet.sshtaskmanager.model.Process;
import com.xesnet.sshtaskmanager.model.ProcessRun;
import com.xesnet.sshtaskmanager.model.ProcessRunState;
import com.xesnet.sshtaskmanager.model.RunManagerConfig;
import com.xesnet.sshtaskmanager.model.Sequence;
import com.xesnet.sshtaskmanager.model.SequenceRun;
import com.xesnet.sshtaskmanager.model.SequenceRunState;
import com.xesnet.sshtaskmanager.model.Server;
import com.xesnet.sshtaskmanager.model.Variable;
import com.xesnet.sshtaskmanager.util.ConditionChecker;
import com.xesnet.sshtaskmanager.util.VariableResolver;
import com.xesnet.sshtaskmanager.yaml.YamlContext;

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
    private final Backend backend;

    private HashSet<ProcessRun> processRuns;
    private HashSet<SequenceRun> sequenceRuns;

    public RunManager(RunManagerConfig config, Backend backend) {
        this.config = config;
        this.backend = backend;
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

    public ProcessRunExecution execute(Process process, String login, List<Variable> variables) {
        ProcessRun processRun = new ProcessRun();
        processRun.setId(UUID.randomUUID().toString());
        processRun.setName(process.getName());
        processRun.setState(ProcessRunState.INIT);
        processRun.updateLocalDateTime();
        setProcessRun(processRun);

        Server nonFinalServer;
        try {
            nonFinalServer = backend.getServer(process.getServerName());
        } catch (YamlContext.YamlContextException e) {
            processRun.setState(ProcessRunState.FAILED);
            processRun.updateLocalDateTime();
            LOG.log(Level.SEVERE, MessageFormat.format("[RunManager] [Process] [{0}] [{1}]", processRun.getId(), processRun.getState()), e);
            setProcessRun(processRun);
            return new ProcessRunExecution(processRun, null);
        }
        Server server = nonFinalServer;

        if (!process.isMultipleRun() && isAnotherProcessRun(processRun)) {
            processRun.setState(ProcessRunState.NO_MULTIPLE_RUN);
            processRun.updateLocalDateTime();
            setProcessRun(processRun);
            LOG.warning(MessageFormat.format("[RunManager] [Process] [{0}] [{1}] Run \"{2}\" from Server \"{3}\" by User \"{4}\"", processRun.getId(), processRun.getState(), process.getName(), nonFinalServer.getName(), login));
            return new ProcessRunExecution(processRun, null);
        }

        LOG.fine(MessageFormat.format("[RunManager] [Process] [{0}] [{1}] Run \"{2}\" from Server \"{3}\" by User \"{4}\"", processRun.getId(), processRun.getState(), process.getName(), nonFinalServer.getName(), login));

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

                List<String> commands = (variables == null || variables.size() == 0) ? process.getCommands() : new VariableResolver(variables).substituteValues(process.getCommands());
                channel.setCommand(String.join(";", commands));

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

    public synchronized boolean isAnotherProcessRun(ProcessRun processRun) {
        return processRuns.stream()
                .filter(run -> run.getName().equals(processRun.getName()))
                .filter(run -> !run.getId().equals(processRun.getId()))
                .anyMatch(run -> !run.getState().isDone());
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
        if (cleaned != 0) {
            LOG.finer(MessageFormat.format("[RunManager] [Process] [Clean] {0}/{1} removed", cleaned, total));
        }
    }

    public SequenceRun execute(Sequence sequence, String login, List<Variable> variables) {
        SequenceRun sequenceRun = new SequenceRun();
        sequenceRun.setId(UUID.randomUUID().toString());
        sequenceRun.setName(sequence.getName());
        sequenceRun.setState(SequenceRunState.INIT);
        sequenceRun.updateLocalDateTime();
        setSequenceRun(sequenceRun);

        if (!sequence.isMultipleRun() && isAnotherSequenceRun(sequenceRun)) {
            sequenceRun.setState(SequenceRunState.NO_MULTIPLE_RUN);
            sequenceRun.updateLocalDateTime();
            setSequenceRun(sequenceRun);
            LOG.warning(MessageFormat.format("[RunManager] [Sequence] [{0}] [{1}] Sequence \"{2}\" by User \"{3}\"", sequenceRun.getId(), sequenceRun.getState(), sequence.getName(), login));
            return sequenceRun;
        }

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
                    Process process = backend.getProcess(finalJob.getProcessName());
                    List<Variable> jobVariables = variables == null || variables.isEmpty() || finalJob.getVariables() == null || finalJob.getVariables().isEmpty() ? finalJob.getVariables() : new VariableResolver(variables).substituteVariableValues(finalJob.getVariables());
                    ProcessRunExecution processRunExecution = execute(process, login, jobVariables);
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
                        for (Condition condition : conditions) {
                            if (ConditionChecker.check(processRun, condition)) {
                                LOG.finer(MessageFormat.format("[RunManager] [Sequence] [{0}] [Condition] if ({1} {2} {3}) then {4} else {5} => TRUE", sequenceRun.getId(), condition.getType() == null ? null : condition.getType().getValue(), condition.getOperator() == null ? null : condition.getOperator().getValue(), condition.getValue(), condition.getThenJob() == null ? "continue" : ("Job:\"" + condition.getThenJob().getName() + "\""),
                                        condition.getElseJob() == null ? "continue" : ("Job:\"" + condition.getElseJob().getName() + "\"")));
                                if (condition.getThenJob() != null) {
                                    job = condition.getThenJob();
                                    break;
                                }
                            } else {
                                LOG.finer(MessageFormat.format("[RunManager] [Sequence] [{0}] [Condition] if ({1} {2} {3}) then {4} else {5} => FALSE", sequenceRun.getId(), condition.getType() == null ? null : condition.getType().getValue(), condition.getOperator() == null ? null : condition.getOperator().getValue(), condition.getValue(), condition.getThenJob() == null ? "continue" : ("Job:\"" + condition.getThenJob().getName() + "\""),
                                        condition.getElseJob() == null ? "continue" : ("Job:\"" + condition.getElseJob().getName() + "\"")));
                                if (condition.getElseJob() != null) {
                                    job = condition.getElseJob();
                                    break;
                                }
                            }
                        }
                    }
                }
            } catch (YamlContext.YamlContextException | InterruptedException | ExecutionException e) {
                sequenceRun.setState(SequenceRunState.FAILED);
                sequenceRun.updateLocalDateTime();
                setSequenceRun(sequenceRun);
                LOG.log(Level.SEVERE, MessageFormat.format("[RunManager] [Sequence] [{0}] [{1}] > {2}", sequenceRun.getId(), sequenceRun.getState(), sequenceRun.getJobs() == null ? "" : String.join(" > ", sequenceRun.getJobs())));
                return;
            }

            sequenceRun.setState(SequenceRunState.SUCCESS);
            sequenceRun.updateLocalDateTime();
            setSequenceRun(sequenceRun);
            LOG.finer(MessageFormat.format("[RunManager] [Sequence] [{0}] [{1}] > {2}", sequenceRun.getId(), sequenceRun.getState(), sequenceRun.getJobs() == null ? "" : String.join(" > ", sequenceRun.getJobs())));
        };

        executor.submit(runnable);

        return sequenceRun;
    }

    public synchronized boolean isAnotherSequenceRun(SequenceRun sequenceRun) {
        return sequenceRuns.stream()
                .filter(run -> run.getName().equals(sequenceRun.getName()))
                .filter(run -> !run.getId().equals(sequenceRun.getId()))
                .anyMatch(run -> !run.getState().isDone());
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
        if (cleaned != 0) {
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
