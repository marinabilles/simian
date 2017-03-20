package de.crispda.sola.multitester.runner;

public class Failure {
    public final int id;
    public final String neutralEventName;

    public Failure(int id, String neutralEventName) {
        this.id = id;
        this.neutralEventName = neutralEventName;
    }

    @Override
    public String toString() {
        return Integer.toString(id);
    }
}
