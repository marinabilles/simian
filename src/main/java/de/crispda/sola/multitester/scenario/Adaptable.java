package de.crispda.sola.multitester.scenario;

import de.crispda.sola.multitester.util.DebugAdapter;

import java.io.Serializable;
import java.util.Optional;
import java.util.function.Consumer;

public abstract class Adaptable implements Serializable {
    private transient DebugAdapter debugAdapter;

    public void setDebugAdapter(DebugAdapter debugAdapter) {
        this.debugAdapter = debugAdapter;
    }

    protected Optional<DebugAdapter> debug(Consumer<? super DebugAdapter> consumer) {
        Optional.ofNullable(debugAdapter).ifPresent(consumer);
        return Optional.ofNullable(debugAdapter);
    }
}
