package de.crispda.sola.multitester;

public class MaybeWait {
    private int waitCount;

    public MaybeWait(int waitCount) {
        setWaitCount(waitCount);
    }

    public int getWaitCount() {
        return waitCount;
    }

    public void setWaitCount(int waitCount) {
        if (waitCount < 0)
            throw new IllegalArgumentException("waitCount < 0");
        this.waitCount = waitCount;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("");
        if (waitCount > 0)
            sb.append("wait(").append(waitCount).append(")");
        else
            sb.append("skip");
        return sb.toString();
    }
}
