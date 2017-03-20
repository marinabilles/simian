package de.crispda.sola.multitester.runner;

import de.crispda.sola.multitester.RunState;

import javafx.concurrent.Task;

public abstract class ExperimentTask extends Task<Void> {
    public abstract void setRunState(RunState runState);
}
