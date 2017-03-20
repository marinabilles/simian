package de.crispda.sola.multitester.web;

import java.util.List;

public interface Quittable {
    void manualQuit();
    List<WrappedDriver> getDrivers();
}
