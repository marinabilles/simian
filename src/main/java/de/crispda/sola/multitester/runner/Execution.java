package de.crispda.sola.multitester.runner;

import de.crispda.sola.multitester.RunState;
import de.crispda.sola.multitester.util.Paths;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.util.Callback;
import javax.xml.transform.TransformerConfigurationException;
import java.awt.Rectangle;
import java.io.IOException;
import java.time.Duration;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class Execution {
    public String name;
    private final ExperimentSpec experimentSpec;
    public boolean isStarted = false;
    public final BooleanProperty isRunning = new SimpleBooleanProperty(true);
    private FileHandler handler;
    public ExperimentTask task;
    private ExecutionType type;
    private int depth;
    private static final Logger log = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private Duration duration = Duration.ZERO;
    private final RunnerController controller;

    public Execution(String name) {
        this.controller = null;
        this.name = name;
        this.experimentSpec = null;
    }

    public Execution(RunnerController controller, String name, ExperimentSpec experimentSpec, ExecutionType type,
                     int depth) {
        if (type == ExecutionType.RANDOM_STATE)
            throw new IllegalArgumentException("Missing duration parameter for type RANDOM_STATE");
        this.controller = controller;
        this.name = name;
        this.experimentSpec = experimentSpec;
        this.type = type;
        this.depth = depth;
    }

    public Execution(RunnerController controller, String name, ExperimentSpec experimentSpec, ExecutionType type,
                     int depth, Duration duration) {
        this.controller = controller;
        this.name = name;
        this.experimentSpec = experimentSpec;
        this.type = type;
        this.depth = depth;
        this.duration = duration;
    }

    public static final String path = Paths.get("execution");

    public void start() throws IOException {
        setup();
        startTask();
    }

    public void setup() throws IOException {
        isStarted = true;
        assert experimentSpec != null : "experimentSpec is null";

        if (handler == null) {
            handler = new FileHandler(path + name + ".xml", true);
            log.addHandler(handler);
        }

        if (task == null) {
            if (type == ExecutionType.RANDOM && experimentSpec instanceof SetExperimentSpec) {
                task = new RandomExperimentTask(experimentSpec, path + name);
            } else if (type == ExecutionType.GUIDED && experimentSpec instanceof SetExperimentSpec) {
                try {
                    task = new GuidedExperimentTask((SetExperimentSpec) experimentSpec, path + name, depth);
                } catch (TransformerConfigurationException e) {
                    e.printStackTrace();
                }
            } else if (type == ExecutionType.RANDOM_STATE && experimentSpec instanceof SetExperimentSpec) {
                try {
                    task = new RandomStateExperimentTask((SetExperimentSpec) experimentSpec, path + name, depth,
                            duration);
                } catch (TransformerConfigurationException e) {
                    e.printStackTrace();
                }
            } else if (type == ExecutionType.EXHAUSTIVE && experimentSpec instanceof SetExperimentSpec) {
                try {
                    task = new ExhaustiveExperimentTask((SetExperimentSpec) experimentSpec, path + name, depth);
                } catch (TransformerConfigurationException e) {
                    e.printStackTrace();
                }
            } else if (type == ExecutionType.GUIDED_CACHED && experimentSpec instanceof SetExperimentSpec) {
                try {
                    task = new GuidedCachedExperimentTask((SetExperimentSpec) experimentSpec, path + name, depth);
                } catch (TransformerConfigurationException e) {
                    e.printStackTrace();
                }
            } else if (experimentSpec instanceof SetExperimentSpec) {
                try {
                    task = new ProbabilisticExperimentTask(((SetExperimentSpec) experimentSpec), path + name);
                } catch (TransformerConfigurationException e) {
                    e.printStackTrace();
                }
            }
        } else {
            task.setRunState(RunState.RUNNING);
        }
    }

    public void startTask() {
        assert experimentSpec != null;

        if (task != null) {
            for (Rectangle r : experimentSpec.exclusionRectangles) {
                log.info(String.format("Exclusion rectangle: (%d, %d, %d, %d)", r.x, r.y, r.width, r.height));
            }

            task.setOnCancelled(workerStateEvent ->
                    log.severe("ExperimentTask was cancelled!"));
            task.setOnFailed(workerStateEvent ->
                    log.severe("ExperimentTask failed!\n" +
                            ExceptionUtils.getStackTrace(task.getException())));
            task.setOnSucceeded(workerStateEvent -> {
                if (controller != null) {
                    isStarted = false;
                    isRunning.set(false);
                    handler.close();
                    controller.stopExecution();
                }
            });
            new Thread(task).start();
        }
    }

    public void pause() {
        isStarted = false;
        task.setRunState(RunState.PAUSED);
    }

    public void quit() {
        isStarted = false;
        isRunning.set(false);
        if (task != null)
            task.setRunState(RunState.STOPPED);
        handler.close();
    }

    public static Callback<Execution, Observable[]> extractor() {
        return execution -> new Observable[] {execution.isRunning};
    }
}
