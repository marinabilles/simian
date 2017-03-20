package de.crispda.sola.multitester;

public class InferrenceException extends Exception {
    public InferrenceException() {
    }

    public InferrenceException(String s) {
        super(s);
    }

    public InferrenceException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public InferrenceException(Throwable throwable) {
        super(throwable);
    }
}
