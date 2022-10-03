package dm.decryptmanager;

import agent.AgentInfo;
import battlefield.Battlefield;
import candidate.AgentConclusion;
import dm.candidatecollector.CandidatesCollector;
import dm.dictionary.Dictionary;
import difficultylevel.DifficultyLevel;
import dm.taskproducer.TaskProducer;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import jobprogress.JobProgressInfo;
import machine.Machine;
import ui.adapter.UIAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

import static utill.Utility.factorial;
import static utill.Utility.nCk;

public class DecryptManager {

    private BlockingQueue<AgentConclusion> agentReportsOfCandidatesQueue;
    private Thread collector;
    private final List<AgentConclusion> allConclusions = new ArrayList<>();
    private Thread taskProducer;
    private final Machine enigmaMachine;
    private final Dictionary dictionary;
    private DifficultyLevel difficultyLevel;
    private long totalPossibleConfigurations;
    private final long totalPossibleWindowsPositions;
    private LongProperty totalTimeDecryptProperty;
    private final BlockingQueue<Runnable> waitingTasksBlockingQueue;
    private int taskSize;
    private String allieName;
    private boolean isDMReady;
    private int numOfAgents;
    private JobProgressInfo jobProgressInfo;
    private BlockingQueue<AgentConclusion> uboatCandidateQueue;
    private final BooleanProperty isBruteForceActionCancelled;
    private final BooleanProperty isBruteForceActionPaused;
    private final int UNDEFINED = 0;
    private Map<String, AgentInfo> agentName2agentInfo;
    private String textToDecipher;

    public DecryptManager(String allieName, Battlefield battlefield, Map<String, AgentInfo> agentName2agentInfo) {
        this.agentName2agentInfo = agentName2agentInfo;
        final int LIMIT_NUMBER_OF_TASK = 1000;
        this.waitingTasksBlockingQueue = new LinkedBlockingQueue<>(LIMIT_NUMBER_OF_TASK);
        this.dictionary = battlefield.getDictionary();
        this.enigmaMachine = battlefield.getMachine();
        this.difficultyLevel = battlefield.getDifficultyLevel();
        this.allieName = allieName;
        this.numOfAgents = 0;
        this.uboatCandidateQueue = battlefield.getUboatCandidatesQueue();
        this.totalPossibleWindowsPositions = (long) Math.pow(enigmaMachine.getAlphabet().length(), enigmaMachine.getRotorsCount());
        this.isDMReady = false;
        this.taskSize = UNDEFINED;
        this.jobProgressInfo = new JobProgressInfo();
        this.textToDecipher = battlefield.getTextToDecipher();


        // maybe delete those later
        this.totalTimeDecryptProperty = new SimpleLongProperty();
        this.isBruteForceActionCancelled = new SimpleBooleanProperty(false);
        this.isBruteForceActionPaused = new SimpleBooleanProperty(false);
    }

    /**
     * pauses execution
     */
    public void pauseDecrypt() {
        synchronized (isBruteForceActionPaused) {
            isBruteForceActionPaused.setValue(true);
        }
    }

    /**
     * resume the execution after being paused
     */
    public void resumeDecrypt() {
        synchronized (isBruteForceActionPaused) {
            isBruteForceActionPaused.setValue(false);
            isBruteForceActionPaused.notifyAll();
        }
    }

    /**
     * cancel the bruteForce execution
     */
    public void stopDecrypt() {
        isBruteForceActionPaused.setValue(false);

        // stopping the thread pool
        isBruteForceActionCancelled.set(true);
        // threadExecutor.shutdownNow();

        //  stopping the collector Task / Thread
        collector.interrupt();

        // stopping the producer Thread;
        taskProducer.interrupt();
    }

    /**
     * initiates the thread needed to start the brute force process
     *
     * @param uiAdapter ui adapter to update the ui
     */
    public void startDecrypt(UIAdapter uiAdapter) {

        this.agentReportsOfCandidatesQueue = new LinkedBlockingQueue<>();
        isBruteForceActionCancelled.set(false);

        totalTimeDecryptProperty.setValue(System.nanoTime());

        // updates the total configs property
        setTotalConfigs(difficultyLevel);

        // setting up the collector of the candidates
        collector = new Thread(new CandidatesCollector(agentReportsOfCandidatesQueue, totalPossibleConfigurations,
                totalTimeDecryptProperty, isBruteForceActionCancelledProperty(), isBruteForceActionPaused, uboatCandidateQueue,
                allConclusions, jobProgressInfo, agentName2agentInfo));
        collector.setName("THE_COLLECTOR");

        // starting the thread pool
        // threadExecutor = new ThreadPoolExecutor(numOfSelectedAgents, numOfSelectedAgents,
        //     0L, TimeUnit.MILLISECONDS, threadPoolBlockingQueue);

        // setting up thr thread factory for the thread pool
        /* threadExecutor.setThreadFactory(new ThreadFactory() {

            private int nameCounter = 0;

            @Override
            public Thread newThread(Runnable r) {
                nameCounter++;
                return new Thread(r, String.valueOf(nameCounter));
            }
        });*/

        // setting a thread that produces tasks
        taskProducer = new Thread(new TaskProducer(this));
        taskProducer.setName("TASK_PRODUCER");

        // trigger the threads
        // threadExecutor.prestartAllCoreThreads();

        taskProducer.start();
        collector.start();
    }

    private void setTotalConfigs(DifficultyLevel difficultyLevel) {

        switch (difficultyLevel) {
            case EASY:
                totalPossibleConfigurations = (totalPossibleWindowsPositions);
                break;
            case MEDIUM:
                totalPossibleConfigurations = (totalPossibleWindowsPositions * enigmaMachine.getAvailableReflectorsLen());
                break;
            case HARD:
                totalPossibleConfigurations = (totalPossibleWindowsPositions *
                        enigmaMachine.getAvailableReflectorsLen() *
                        factorial(enigmaMachine.getRotorsCount()));
                break;
            case IMPOSSIBLE:
                totalPossibleConfigurations = (totalPossibleWindowsPositions *
                        enigmaMachine.getAvailableReflectorsLen() *
                        factorial(enigmaMachine.getRotorsCount()) *
                        nCk(enigmaMachine.getAvailableRotorsLen(), enigmaMachine.getRotorsCount())
                );
                break;
        }
    }

    public List<AgentConclusion> getDecipherCandidates() {
        return allConclusions;
    }

    public Set<String> getDictionaryWords() {
        return dictionary.getWords();
    }

    public Machine getEnigmaMachine() {
        return enigmaMachine;
    }

    public Dictionary getDictionary() {
        return dictionary;
    }

    public BlockingQueue<AgentConclusion> getCandidatesQueue() {
        return agentReportsOfCandidatesQueue;
    }

    public BlockingQueue<Runnable> getWaitingTasksBlockingQueue() {
        return waitingTasksBlockingQueue;
    }

    public boolean isIsBruteForceActionCancelled() {
        return isBruteForceActionCancelled.get();
    }

    public BooleanProperty isBruteForceActionCancelledProperty() {
        return isBruteForceActionCancelled;
    }

    public boolean getIsBruteForceActionPaused() {
        return isBruteForceActionPaused.get();
    }

    public BooleanProperty isBruteForceActionPausedProperty() {
        return isBruteForceActionPaused;
    }

    public String getAllieName() {
        return allieName;
    }

    public int getNumOfAgents() {
        return numOfAgents;
    }

    public int getTaskSize() {
        return taskSize;
    }

    public String getDictionaryExcludeCharacters() {
        return dictionary.getExcludeChars();
    }

    public boolean isAllWordsInDictionary(String text) {
        return dictionary.isAllWordsInDictionary(text);
    }

    public void setTaskSize(int taskSize) {
        this.taskSize = taskSize;
        jobProgressInfo.setTotalAmountOfTasks(totalPossibleConfigurations / taskSize);
    }

    public void setDMReady(boolean isReady) {
        this.isDMReady = isReady;
    }

    public boolean getDMReady() {
        return isDMReady;
    }

    public JobProgressInfo getJobProgressInfo() {
        return jobProgressInfo;
    }

    public List<AgentConclusion> getAllConclusions() {
        return allConclusions;
    }

    public DifficultyLevel getDifficultyLevel() {
        return difficultyLevel;
    }

    public String getTextToDecipher() {
        return textToDecipher;
    }
}
