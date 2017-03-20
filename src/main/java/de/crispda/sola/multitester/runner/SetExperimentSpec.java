package de.crispda.sola.multitester.runner;

import de.crispda.sola.multitester.Combinator;
import de.crispda.sola.multitester.Exchanges;
import de.crispda.sola.multitester.Interaction;
import de.crispda.sola.multitester.TestInit;
import org.openqa.selenium.Point;

import java.awt.Rectangle;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Exchanger;

public class SetExperimentSpec extends ExperimentSpec {
    public final Set<Interaction> interactionSet;
    public final Combinator combinator;
    public final List<Interaction> neutralEvents;
    public final TestInit init;
    public Exchanges sender;
    public Exchanges receiver;

    public SetExperimentSpec(Set<Interaction> interactionSet, Combinator combinator,
                             List<Rectangle> exclusionRectangles,
                             TestInit init, String name) {
        super(name, exclusionRectangles);
        this.interactionSet = interactionSet;
        this.combinator = combinator;
        this.neutralEvents = new ArrayList<>();
        this.init = init;
    }

    public SetExperimentSpec(Set<Interaction> interactionSet, Combinator combinator,
                             List<Rectangle> exclusionRectangles,
                             TestInit init, Exchanges sender,
                             Exchanges receiver, String name) {
        this(interactionSet, combinator, exclusionRectangles, init, name);
        this.sender = sender;
        this.receiver = receiver;
        Exchanger<Point> exchanger = new Exchanger<>();
        sender.setExchanger(exchanger);
        receiver.setExchanger(exchanger);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        if (sender != null) {
            Exchanger<Point> exchanger = new Exchanger<>();
            sender.setExchanger(exchanger);
            receiver.setExchanger(exchanger);
        }
    }
}
