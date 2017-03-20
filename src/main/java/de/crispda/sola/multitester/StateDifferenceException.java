package de.crispda.sola.multitester;

public class StateDifferenceException extends Exception {
    public StateDifferenceException(String name) {
        super("The states before and after the neutral event " + name + " are different!");
    }
}
