package dm.decryptmanager;

import candidate.Candidate;
import dm.agent.AgentConclusion;
import dm.candidatecollector.CandidatesCollectorTask;
import dm.dictionary.Dictionary;
import dm.difficultylevel.DifficultyLevel;
import dm.taskproducer.TaskProducer;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import machine.Machine;
import ui.adapter.UIAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

import static utill.Utility.factorial;
import static utill.Utility.nCk;

public class DecryptManager {

    private BlockingQueue<AgentConclusion> candidatesQueue;
    private ThreadPoolExecutor threadExecutor;
    private Thread collector;
    private Thread taskProducer;
    private final Machine enigmaMachine;
    private final Dictionary dictionary;
    private final int numOfAvailableAgents;
    private final List<Candidate> allCandidates = new ArrayList<>();
    private long totalPossibleConfigurations;
    private final long totalPossibleWindowsPositions;
    private final BlockingQueue<Runnable> threadPoolBlockingQueue;

    public int getNumOfAvailableAgents() {
        return numOfAvailableAgents;
    }

    private final BooleanProperty isBruteForceActionCancelled;

    private final BooleanProperty isBruteForceActionPaused;

    private final Object dummy = new Object();


    public DecryptManager(Dictionary dictionary, int numOfAvailableAgents, Machine enigmaMachine) {
        int LIMIT_NUMBER_OF_TASK = 1000;
        this.threadPoolBlockingQueue = new LinkedBlockingQueue<>(LIMIT_NUMBER_OF_TASK);
        this.dictionary = dictionary;
        this.numOfAvailableAgents = numOfAvailableAgents;
        this.enigmaMachine = enigmaMachine;
        this.totalPossibleWindowsPositions = (long) Math.pow(enigmaMachine.getAlphabet().length(), enigmaMachine.getRotorsCount());
        this.isBruteForceActionCancelled = new SimpleBooleanProperty(false);
        this.isBruteForceActionPaused = new SimpleBooleanProperty(false);
    }

    /**
     * pauses execution
     */
    public void pauseDecrypt() {
        System.out.println("about to set pause to " + true);
        System.out.println("now paused is " + isIsBruteForceActionPaused());
        synchronized (isBruteForceActionPaused) {
            System.out.println("inside sync on dm");
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
            System.out.println("dm had notified all with key :" + dummy);
        }
    }

    /**
     * cancel the bruteForce execution
     */
    public void stopDecrypt() {

        // stopping the thread pool
        isBruteForceActionCancelled.set(true);
        threadExecutor.shutdownNow();

        //  stopping the collector Task / Thread
        collector.interrupt();

        // stopping the producer Thread;
        taskProducer.interrupt();
    }

    /**
     * initiates the thread needed to start the brute force process
     *
     * @param taskSize            size of task per thread to execute
     * @param numOfSelectedAgents num of threads in pool
     * @param textToDecipher      the text that agents trying to cipher
     * @param difficultyLevel     difficulty label
     * @param uiAdapter           ui adapter to update the ui
     */
    public void startDecrypt(int taskSize, int numOfSelectedAgents, String textToDecipher,
                             DifficultyLevel difficultyLevel, UIAdapter uiAdapter) {

        this.candidatesQueue = new LinkedBlockingQueue<>();
        isBruteForceActionCancelled.set(false);

        // updates the total configs property
        setTotalConfigs(difficultyLevel);

        // setting up the collector of the candidates
        collector = new Thread(new CandidatesCollectorTask(candidatesQueue, totalPossibleConfigurations, uiAdapter, this,
                isBruteForceActionCancelledProperty(), isBruteForceActionPaused));
        collector.setName("THE_COLLECTOR");

        // starting the thread pool
        threadExecutor = new ThreadPoolExecutor(numOfSelectedAgents, numOfSelectedAgents,
                0L, TimeUnit.MILLISECONDS, threadPoolBlockingQueue);

        // setting up thr thread factory for the thread pool
        threadExecutor.setThreadFactory(new ThreadFactory() {

            private int nameCounter = 0;

            @Override
            public Thread newThread(Runnable r) {
                nameCounter++;
                return new Thread(r, String.valueOf(nameCounter));
            }
        });

        // setting a thread that produces tasks
        taskProducer = new Thread(new TaskProducer(this, taskSize, difficultyLevel, textToDecipher));
        taskProducer.setName("TASK_PRODUCER");

        // trigger the threads
        threadExecutor.prestartAllCoreThreads();

        taskProducer.start(); // thread is in the air starting the missions spread.
        collector.start();
        // main thread ends here
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

    public List<Candidate> getDecipherCandidates() {
        return allCandidates;
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
        return candidatesQueue;
    }

    public BlockingQueue<Runnable> getThreadPoolBlockingQueue() {
        return threadPoolBlockingQueue;
    }

    public boolean isIsBruteForceActionCancelled() {
        return isBruteForceActionCancelled.get();
    }

    public BooleanProperty isBruteForceActionCancelledProperty() {
        return isBruteForceActionCancelled;
    }

    public boolean isIsBruteForceActionPaused() {
        return isBruteForceActionPaused.get();
    }

    public BooleanProperty isBruteForceActionPausedProperty() {
        return isBruteForceActionPaused;
    }

    public Object getDummmy() {
        return dummy;
    }
}
