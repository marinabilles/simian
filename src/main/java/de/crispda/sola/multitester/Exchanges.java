package de.crispda.sola.multitester;

import org.openqa.selenium.Point;

import java.util.concurrent.Exchanger;

public interface Exchanges extends Interaction {
    void setExchanger(Exchanger<Point> exchanger);
}
