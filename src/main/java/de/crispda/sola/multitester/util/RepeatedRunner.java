package de.crispda.sola.multitester.util;

import de.crispda.sola.multitester.*;
import de.crispda.sola.multitester.runner.ExperimentSpec;
import de.crispda.sola.multitester.runner.SetExperimentSpec;
import de.crispda.sola.multitester.scenario.*;
import de.crispda.sola.multitester.web.DriverSupplier;
import de.crispda.sola.multitester.web.Drivers;
import org.openqa.selenium.Keys;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static de.crispda.sola.multitester.util.XMLFile.createElement;

public class RepeatedRunner {

    public static void main(String[] args) throws IOException, TransformerException, InterruptedException,
            ParserConfigurationException, SAXException {
        SetExperimentSpec GDocsAS5 = (SetExperimentSpec) ExperimentSpec.forName("GDocs AS5");
        SetExperimentSpec GDocsAS10 = (SetExperimentSpec) ExperimentSpec.forName("GDocs AS10");

        SetExperimentSpec FirepadAS5 = (SetExperimentSpec) ExperimentSpec.forName("Firepad AS5");
        SetExperimentSpec FirepadAS10 = (SetExperimentSpec) ExperimentSpec.forName("Firepad AS10");

        SetExperimentSpec OwncloudAS5 = (SetExperimentSpec) ExperimentSpec.forName("Owncloud AS5");
        SetExperimentSpec OwncloudAS10 = (SetExperimentSpec) ExperimentSpec.forName("Owncloud AS10");

        List<ExplorationState> gDocsAS5list = new ArrayList<>();
        List<ExplorationState> gDocsAS10list = new ArrayList<>();
        List<ExplorationState> firepadAS5list = new ArrayList<>();
        List<ExplorationState> firepadAS10list = new ArrayList<>();
        List<ExplorationState> owncloudAS5list = new ArrayList<>();
        List<ExplorationState> owncloudAS10list = new ArrayList<>();

        gDocsAS5list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new GDocsMakeFont(Selection.LineBefore, GDocs.Font.verdana)),
                new GuidedStateSpaceExplorer.SequentialStep(new GDocsWrite("a")),
                new GuidedStateSpaceExplorer.ParallelStep(new GDocsWrite("a"),
                        new GDocsDelete(Selection.LineBefore)))
        );
        gDocsAS5list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new GDocsMakeFont(Selection.LineBefore, GDocs.Font.verdana)),
                new GuidedStateSpaceExplorer.SequentialStep(new GDocsWrite("a")),
                new GuidedStateSpaceExplorer.ParallelStep(new GDocsWrite("a"),
                        new GDocsWrite(Keys.RETURN)))
        );
        gDocsAS5list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new GDocsWrite("a")),
                new GuidedStateSpaceExplorer.SequentialStep(new GDocsApplyModification(Selection.LineBefore, GDocs.Modification.bold)),
                new GuidedStateSpaceExplorer.ParallelStep(new GDocsWrite("a"),
                        new GDocsDelete(Selection.LineBefore)))
        );
        gDocsAS5list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new GDocsWrite("a")),
                new GuidedStateSpaceExplorer.SequentialStep(new GDocsApplyModification(Selection.LineBefore, GDocs.Modification.bold)),
                new GuidedStateSpaceExplorer.ParallelStep(new GDocsWrite("a"),
                        new GDocsWrite(Keys.RETURN)))
        );
        firepadAS5list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new FirepadWrite("a")),
                new GuidedStateSpaceExplorer.SequentialStep(new FirepadApplyModification(Selection.LineBefore, Firepad.Modification.bold)),
                new GuidedStateSpaceExplorer.ParallelStep(new FirepadWrite("a"),
                        new FirepadApplyModification(Selection.LineBefore, Firepad.Modification.bold)))
        );
        firepadAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new FirepadWrite("a")),
                new GuidedStateSpaceExplorer.ParallelStep(new FirepadMakeFontSize(Selection.LineBefore, "18"),
                        new FirepadApplyModification(Selection.LineBefore, Firepad.Modification.bold)))
        );
        firepadAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new FirepadWrite("b")),
                new GuidedStateSpaceExplorer.ParallelStep(new FirepadWrite("a"),
                        new FirepadWrite("b")))
        );
        firepadAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new FirepadWrite("b")),
                new GuidedStateSpaceExplorer.ParallelStep(new FirepadWrite("a"),
                        new FirepadWrite("b")))
        );
        firepadAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new FirepadWrite("b")),
                new GuidedStateSpaceExplorer.ParallelStep(new FirepadMakeFontSize(Selection.LineBefore, "18"),
                        new FirepadApplyModification(Selection.LineBefore, Firepad.Modification.bold)))
        );
        firepadAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new FirepadWrite("b")),
                new GuidedStateSpaceExplorer.ParallelStep(new FirepadWrite("b"),
                        new FirepadApplyModification(Selection.LineBefore, Firepad.Modification.bold)))
        );
        gDocsAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new GDocsWrite("b")),
                new GuidedStateSpaceExplorer.SequentialStep(new GDocsMakeFont(Selection.LineBefore, GDocs.Font.verdana)),
                new GuidedStateSpaceExplorer.ParallelStep(new GDocsWrite(Keys.RETURN),
                        new GDocsWrite(" ")))
        );
        gDocsAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new GDocsWrite("b")),
                new GuidedStateSpaceExplorer.SequentialStep(new GDocsMakeFont(Selection.LineBefore, GDocs.Font.verdana)),
                new GuidedStateSpaceExplorer.ParallelStep(new GDocsWrite("a"),
                        new GDocsWrite(Keys.TAB)))
        );
        gDocsAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new GDocsWrite("a")),
                new GuidedStateSpaceExplorer.SequentialStep(new GDocsApplyModification(Selection.LineBefore, GDocs.Modification.bold)),
                new GuidedStateSpaceExplorer.ParallelStep(new GDocsWrite("a"),
                        new GDocsWrite(" ")))
        );
        gDocsAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new GDocsWrite("a")),
                new GuidedStateSpaceExplorer.SequentialStep(new GDocsApplyModification(Selection.LineBefore, GDocs.Modification.bold)),
                new GuidedStateSpaceExplorer.ParallelStep(new GDocsWrite("a"),
                        new GDocsDelete(Selection.LineBefore)))
        );
        gDocsAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new GDocsWrite("a")),
                new GuidedStateSpaceExplorer.SequentialStep(new GDocsApplyModification(Selection.LineBefore, GDocs.Modification.bold)),
                new GuidedStateSpaceExplorer.ParallelStep(new GDocsWrite("b"),
                        new GDocsDelete(Selection.LineBefore)))
        );
        gDocsAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new GDocsApplyModification(Selection.LineAfter, GDocs.Modification.italic)),
                new GuidedStateSpaceExplorer.SequentialStep(new GDocsWrite("a")),
                new GuidedStateSpaceExplorer.ParallelStep(new GDocsWrite("b"),
                        new GDocsWrite(" ")))
        );
        gDocsAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new GDocsApplyModification(Selection.LineAfter, GDocs.Modification.italic)),
                new GuidedStateSpaceExplorer.SequentialStep(new GDocsWrite("a")),
                new GuidedStateSpaceExplorer.ParallelStep(new GDocsWrite("a"),
                        new GDocsWrite(Keys.RETURN)))
        );
        gDocsAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new GDocsApplyModification(Selection.LineAfter, GDocs.Modification.italic)),
                new GuidedStateSpaceExplorer.SequentialStep(new GDocsWrite("a")),
                new GuidedStateSpaceExplorer.ParallelStep(new GDocsWrite("a"),
                        new GDocsDelete(Selection.LineBefore)))
        );
        gDocsAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new GDocsApplyModification(Selection.LineAfter, GDocs.Modification.italic)),
                new GuidedStateSpaceExplorer.SequentialStep(new GDocsWrite("a")),
                new GuidedStateSpaceExplorer.ParallelStep(new GDocsWrite("a"),
                        new GDocsWrite(Keys.TAB)))
        );
        gDocsAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new GDocsApplyModification(Selection.LineAfter, GDocs.Modification.italic)),
                new GuidedStateSpaceExplorer.SequentialStep(new GDocsWrite("a")),
                new GuidedStateSpaceExplorer.ParallelStep(new GDocsWrite("b"),
                        new GDocsDelete(Selection.LineBefore)))
        );
        gDocsAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new GDocsApplyModification(Selection.LineAfter, GDocs.Modification.italic)),
                new GuidedStateSpaceExplorer.SequentialStep(new GDocsWrite("a")),
                new GuidedStateSpaceExplorer.ParallelStep(new GDocsWrite("b"),
                        new GDocsWrite(Keys.TAB)))
        );
        gDocsAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new GDocsMakeFontSize(Selection.LineBefore, "18")),
                new GuidedStateSpaceExplorer.SequentialStep(new GDocsWrite("b")),
                new GuidedStateSpaceExplorer.ParallelStep(new GDocsWrite("b"),
                        new GDocsWrite(" ")))
        );
        gDocsAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new GDocsMakeFontSize(Selection.LineBefore, "18")),
                new GuidedStateSpaceExplorer.SequentialStep(new GDocsWrite("b")),
                new GuidedStateSpaceExplorer.ParallelStep(new GDocsWrite(Keys.RETURN),
                        new GDocsWrite(" ")))
        );
        gDocsAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new GDocsMakeFontSize(Selection.LineBefore, "18")),
                new GuidedStateSpaceExplorer.SequentialStep(new GDocsWrite("b")),
                new GuidedStateSpaceExplorer.ParallelStep(new GDocsWrite("a"),
                        new GDocsWrite(Keys.RETURN)))
        );
        gDocsAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new GDocsMakeFontSize(Selection.LineBefore, "18")),
                new GuidedStateSpaceExplorer.SequentialStep(new GDocsWrite("b")),
                new GuidedStateSpaceExplorer.ParallelStep(new GDocsWrite("a"),
                        new GDocsDelete(Selection.LineBefore)))
        );
        gDocsAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new GDocsMakeFontSize(Selection.LineBefore, "18")),
                new GuidedStateSpaceExplorer.SequentialStep(new GDocsWrite("b")),
                new GuidedStateSpaceExplorer.ParallelStep(new GDocsWrite(Keys.TAB),
                        new GDocsWrite(" ")))
        );
        gDocsAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new GDocsMakeFontSize(Selection.LineBefore, "18")),
                new GuidedStateSpaceExplorer.SequentialStep(new GDocsWrite("b")),
                new GuidedStateSpaceExplorer.ParallelStep(new GDocsWrite("b"),
                        new GDocsWrite(Keys.TAB)))
        );
        gDocsAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new GDocsApplyModification(Selection.LineAfter, GDocs.Modification.italic)),
                new GuidedStateSpaceExplorer.SequentialStep(new GDocsWrite("b")),
                new GuidedStateSpaceExplorer.ParallelStep(new GDocsWrite("a"),
                        new GDocsDelete(Selection.LineBefore)))
        );
        gDocsAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new GDocsApplyModification(Selection.LineAfter, GDocs.Modification.italic)),
                new GuidedStateSpaceExplorer.SequentialStep(new GDocsWrite("b")),
                new GuidedStateSpaceExplorer.ParallelStep(new GDocsWrite("b"),
                        new GDocsDelete(Selection.LineBefore)))
        );
        gDocsAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new GDocsApplyModification(Selection.LineAfter, GDocs.Modification.italic)),
                new GuidedStateSpaceExplorer.SequentialStep(new GDocsWrite("b")),
                new GuidedStateSpaceExplorer.ParallelStep(new GDocsWrite("b"),
                        new GDocsWrite(Keys.TAB)))
        );
        gDocsAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new GDocsMakeFontSize(Selection.LineBefore, "18")),
                new GuidedStateSpaceExplorer.SequentialStep(new GDocsWrite("a")),
                new GuidedStateSpaceExplorer.ParallelStep(new GDocsWrite("b"),
                        new GDocsWrite(Keys.RETURN)))
        );
        gDocsAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new GDocsMakeFontSize(Selection.LineBefore, "18")),
                new GuidedStateSpaceExplorer.SequentialStep(new GDocsWrite("a")),
                new GuidedStateSpaceExplorer.ParallelStep(new GDocsWrite("a"),
                        new GDocsWrite(" ")))
        );
        gDocsAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new GDocsMakeFontSize(Selection.LineBefore, "18")),
                new GuidedStateSpaceExplorer.SequentialStep(new GDocsWrite("a")),
                new GuidedStateSpaceExplorer.ParallelStep(new GDocsWrite(Keys.RETURN),
                        new GDocsWrite(" ")))
        );
        gDocsAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new GDocsMakeFontSize(Selection.LineBefore, "18")),
                new GuidedStateSpaceExplorer.SequentialStep(new GDocsWrite("a")),
                new GuidedStateSpaceExplorer.ParallelStep(new GDocsWrite("a"),
                        new GDocsWrite(Keys.RETURN)))
        );
        gDocsAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new GDocsMakeFontSize(Selection.LineBefore, "18")),
                new GuidedStateSpaceExplorer.SequentialStep(new GDocsWrite("a")),
                new GuidedStateSpaceExplorer.ParallelStep(new GDocsWrite("a"),
                        new GDocsDelete(Selection.LineBefore)))
        );
        gDocsAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new GDocsMakeFontSize(Selection.LineBefore, "18")),
                new GuidedStateSpaceExplorer.SequentialStep(new GDocsWrite("a")),
                new GuidedStateSpaceExplorer.ParallelStep(new GDocsWrite("b"),
                        new GDocsDelete(Selection.LineBefore)))
        );
        gDocsAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new GDocsMakeFontSize(Selection.LineBefore, "18")),
                new GuidedStateSpaceExplorer.SequentialStep(new GDocsWrite("a")),
                new GuidedStateSpaceExplorer.ParallelStep(new GDocsWrite("b"),
                        new GDocsWrite(Keys.TAB)))
        );
        gDocsAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new GDocsWrite("a")),
                new GuidedStateSpaceExplorer.SequentialStep(new GDocsMakeFont(Selection.LineBefore, GDocs.Font.verdana)),
                new GuidedStateSpaceExplorer.ParallelStep(new GDocsWrite("a"),
                        new GDocsWrite(Keys.RETURN)))
        );
        gDocsAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new GDocsWrite("a")),
                new GuidedStateSpaceExplorer.SequentialStep(new GDocsMakeFont(Selection.LineBefore, GDocs.Font.verdana)),
                new GuidedStateSpaceExplorer.ParallelStep(new GDocsWrite("a"),
                        new GDocsDelete(Selection.LineBefore)))
        );
        gDocsAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new GDocsWrite("a")),
                new GuidedStateSpaceExplorer.SequentialStep(new GDocsMakeFont(Selection.LineBefore, GDocs.Font.verdana)),
                new GuidedStateSpaceExplorer.ParallelStep(new GDocsWrite("b"),
                        new GDocsWrite(Keys.TAB)))
        );
        gDocsAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new GDocsWrite(Keys.TAB)),
                new GuidedStateSpaceExplorer.SequentialStep(new GDocsWrite("b")),
                new GuidedStateSpaceExplorer.ParallelStep(new GDocsMakeFontSize(Selection.LineBefore, "18"),
                        new GDocsWrite(Keys.RETURN)))
        );
        gDocsAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new GDocsWrite("b")),
                new GuidedStateSpaceExplorer.SequentialStep(new GDocsApplyModification(Selection.LineBefore, GDocs.Modification.bold)),
                new GuidedStateSpaceExplorer.ParallelStep(new GDocsWrite("b"),
                        new GDocsWrite(" ")))
        );
        gDocsAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new GDocsWrite("b")),
                new GuidedStateSpaceExplorer.SequentialStep(new GDocsApplyModification(Selection.LineBefore, GDocs.Modification.bold)),
                new GuidedStateSpaceExplorer.ParallelStep(new GDocsWrite("a"),
                        new GDocsWrite(Keys.RETURN)))
        );
        gDocsAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new GDocsWrite("b")),
                new GuidedStateSpaceExplorer.SequentialStep(new GDocsApplyModification(Selection.LineBefore, GDocs.Modification.bold)),
                new GuidedStateSpaceExplorer.ParallelStep(new GDocsWrite("a"),
                        new GDocsDelete(Selection.LineBefore)))
        );
        gDocsAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new GDocsWrite("b")),
                new GuidedStateSpaceExplorer.SequentialStep(new GDocsApplyModification(Selection.LineBefore, GDocs.Modification.bold)),
                new GuidedStateSpaceExplorer.ParallelStep(new GDocsWrite("a"),
                        new GDocsWrite(Keys.TAB)))
        );
        gDocsAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new GDocsWrite("b")),
                new GuidedStateSpaceExplorer.SequentialStep(new GDocsApplyModification(Selection.LineBefore, GDocs.Modification.bold)),
                new GuidedStateSpaceExplorer.ParallelStep(new GDocsWrite("b"),
                        new GDocsDelete(Selection.LineBefore)))
        );
        firepadAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new FirepadWrite("b")),
                new GuidedStateSpaceExplorer.SequentialStep(new FirepadWrite("a")),
                new GuidedStateSpaceExplorer.ParallelStep(new FirepadMakeFontSize(Selection.LineBefore, "18"),
                        new FirepadApplyModification(Selection.LineBefore, Firepad.Modification.bold)))
        );
        firepadAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new FirepadWrite("b")),
                new GuidedStateSpaceExplorer.SequentialStep(new FirepadWrite("a")),
                new GuidedStateSpaceExplorer.ParallelStep(new FirepadWrite(Keys.TAB),
                        new FirepadApplyModification(Selection.LineBefore, Firepad.Modification.bold)))
        );
        firepadAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new FirepadWrite("b")),
                new GuidedStateSpaceExplorer.SequentialStep(new FirepadWrite("a")),
                new GuidedStateSpaceExplorer.ParallelStep(new FirepadWrite("b"),
                        new FirepadApplyModification(Selection.LineBefore, Firepad.Modification.bold)))
        );
        firepadAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new FirepadApplyModification(Selection.LineAfter, Firepad.Modification.italic)),
                new GuidedStateSpaceExplorer.SequentialStep(new FirepadWrite("a")),
                new GuidedStateSpaceExplorer.ParallelStep(new FirepadMakeFontSize(Selection.LineBefore, "18"),
                        new FirepadApplyModification(Selection.LineBefore, Firepad.Modification.bold)))
        );
        firepadAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new FirepadWrite("a")),
                new GuidedStateSpaceExplorer.SequentialStep(new FirepadWrite("a")),
                new GuidedStateSpaceExplorer.ParallelStep(new FirepadMakeFontSize(Selection.LineBefore, "18"),
                        new FirepadApplyModification(Selection.LineBefore, Firepad.Modification.bold)))
        );
        firepadAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new FirepadWrite("a")),
                new GuidedStateSpaceExplorer.SequentialStep(new FirepadApplyModification(Selection.LineBefore, Firepad.Modification.bold)),
                new GuidedStateSpaceExplorer.ParallelStep(new FirepadMakeFontSize(Selection.LineBefore, "18"),
                        new FirepadApplyModification(Selection.LineBefore, Firepad.Modification.bold)))
        );
        firepadAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new FirepadWrite("b")),
                new GuidedStateSpaceExplorer.SequentialStep(new FirepadWrite("b")),
                new GuidedStateSpaceExplorer.ParallelStep(new FirepadMakeFontSize(Selection.LineBefore, "18"),
                        new FirepadApplyModification(Selection.LineBefore, Firepad.Modification.bold)))
        );
        firepadAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new FirepadWrite("b")),
                new GuidedStateSpaceExplorer.SequentialStep(new FirepadWrite("b")),
                new GuidedStateSpaceExplorer.ParallelStep(new FirepadWrite("b"),
                        new FirepadApplyModification(Selection.LineBefore, Firepad.Modification.bold)))
        );
        firepadAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new FirepadWrite("a")),
                new GuidedStateSpaceExplorer.SequentialStep(new FirepadWrite("b")),
                new GuidedStateSpaceExplorer.ParallelStep(new FirepadWrite("b"),
                        new FirepadApplyModification(Selection.LineBefore, Firepad.Modification.bold)))
        );
        firepadAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new FirepadWrite(" ")),
                new GuidedStateSpaceExplorer.SequentialStep(new FirepadWrite("a")),
                new GuidedStateSpaceExplorer.ParallelStep(new FirepadMakeFontSize(Selection.LineBefore, "18"),
                        new FirepadApplyModification(Selection.LineBefore, Firepad.Modification.bold)))
        );
        firepadAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new FirepadWrite(" ")),
                new GuidedStateSpaceExplorer.SequentialStep(new FirepadWrite("a")),
                new GuidedStateSpaceExplorer.ParallelStep(new FirepadWrite(Keys.TAB),
                        new FirepadApplyModification(Selection.LineBefore, Firepad.Modification.bold)))
        );
        firepadAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new FirepadWrite(" ")),
                new GuidedStateSpaceExplorer.SequentialStep(new FirepadWrite("b")),
                new GuidedStateSpaceExplorer.ParallelStep(new FirepadMakeFontSize(Selection.LineBefore, "18"),
                        new FirepadApplyModification(Selection.LineBefore, Firepad.Modification.bold)))
        );
        firepadAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new FirepadWrite(" ")),
                new GuidedStateSpaceExplorer.SequentialStep(new FirepadWrite("b")),
                new GuidedStateSpaceExplorer.ParallelStep(new FirepadWrite("a"),
                        new FirepadApplyModification(Selection.LineBefore, Firepad.Modification.bold)))
        );
        firepadAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new FirepadMakeFontSize(Selection.LineBefore, "18")),
                new GuidedStateSpaceExplorer.SequentialStep(new FirepadWrite("b")),
                new GuidedStateSpaceExplorer.ParallelStep(new FirepadWrite("b"),
                        new FirepadApplyModification(Selection.LineBefore, Firepad.Modification.bold)))
        );
        firepadAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new FirepadMakeFontSize(Selection.LineBefore, "18")),
                new GuidedStateSpaceExplorer.SequentialStep(new FirepadWrite("b")),
                new GuidedStateSpaceExplorer.ParallelStep(new FirepadWrite("a"),
                        new FirepadApplyModification(Selection.LineBefore, Firepad.Modification.bold)))
        );
        firepadAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new FirepadWrite(Keys.RETURN)),
                new GuidedStateSpaceExplorer.SequentialStep(new FirepadWrite("a")),
                new GuidedStateSpaceExplorer.ParallelStep(new FirepadMakeFontSize(Selection.LineBefore, "18"),
                        new FirepadApplyModification(Selection.LineBefore, Firepad.Modification.bold)))
        );
        firepadAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new FirepadWrite(Keys.TAB)),
                new GuidedStateSpaceExplorer.SequentialStep(new FirepadWrite("a")),
                new GuidedStateSpaceExplorer.ParallelStep(new FirepadWrite(Keys.TAB),
                        new FirepadApplyModification(Selection.LineBefore, Firepad.Modification.bold)))
        );
        firepadAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new FirepadMakeFont(Selection.LineBefore, Firepad.Font.verdana)),
                new GuidedStateSpaceExplorer.SequentialStep(new FirepadWrite("a")),
                new GuidedStateSpaceExplorer.ParallelStep(new FirepadMakeFontSize(Selection.LineBefore, "18"),
                        new FirepadApplyModification(Selection.LineBefore, Firepad.Modification.bold)))
        );
        firepadAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new FirepadMakeFont(Selection.LineBefore, Firepad.Font.verdana)),
                new GuidedStateSpaceExplorer.SequentialStep(new FirepadWrite("a")),
                new GuidedStateSpaceExplorer.ParallelStep(new FirepadWrite(Keys.TAB),
                        new FirepadApplyModification(Selection.LineBefore, Firepad.Modification.bold)))
        );
        firepadAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new FirepadMakeFontSize(Selection.LineBefore, "18")),
                new GuidedStateSpaceExplorer.SequentialStep(new FirepadWrite("a")),
                new GuidedStateSpaceExplorer.ParallelStep(new FirepadWrite("a"),
                        new FirepadApplyModification(Selection.LineBefore, Firepad.Modification.bold)))
        );
        firepadAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new FirepadWrite(Keys.RETURN)),
                new GuidedStateSpaceExplorer.SequentialStep(new FirepadWrite("b")),
                new GuidedStateSpaceExplorer.ParallelStep(new FirepadMakeFontSize(Selection.LineBefore, "18"),
                        new FirepadApplyModification(Selection.LineBefore, Firepad.Modification.bold)))
        );
        firepadAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new FirepadWrite(Keys.RETURN)),
                new GuidedStateSpaceExplorer.SequentialStep(new FirepadWrite("b")),
                new GuidedStateSpaceExplorer.ParallelStep(new FirepadWrite("a"),
                        new FirepadApplyModification(Selection.LineBefore, Firepad.Modification.bold)))
        );
        firepadAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new FirepadWrite("b")),
                new GuidedStateSpaceExplorer.SequentialStep(new FirepadWrite(Keys.TAB)),
                new GuidedStateSpaceExplorer.ParallelStep(new FirepadMakeFontSize(Selection.LineBefore, "18"),
                        new FirepadApplyModification(Selection.LineBefore, Firepad.Modification.bold)))
        );
        firepadAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new FirepadWrite("b")),
                new GuidedStateSpaceExplorer.SequentialStep(new FirepadWrite(Keys.TAB)),
                new GuidedStateSpaceExplorer.ParallelStep(new FirepadWrite(Keys.TAB),
                        new FirepadApplyModification(Selection.LineBefore, Firepad.Modification.bold)))
        );
        firepadAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new FirepadWrite("b")),
                new GuidedStateSpaceExplorer.SequentialStep(new FirepadWrite(Keys.TAB)),
                new GuidedStateSpaceExplorer.ParallelStep(new FirepadWrite("a"),
                        new FirepadApplyModification(Selection.LineBefore, Firepad.Modification.bold)))
        );
        firepadAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new FirepadWrite("b")),
                new GuidedStateSpaceExplorer.SequentialStep(new FirepadApplyModification(Selection.LineAfter, Firepad.Modification.italic)),
                new GuidedStateSpaceExplorer.ParallelStep(new FirepadMakeFontSize(Selection.LineBefore, "18"),
                        new FirepadApplyModification(Selection.LineBefore, Firepad.Modification.bold)))
        );
        firepadAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new FirepadWrite("b")),
                new GuidedStateSpaceExplorer.SequentialStep(new FirepadApplyModification(Selection.LineAfter, Firepad.Modification.italic)),
                new GuidedStateSpaceExplorer.ParallelStep(new FirepadWrite("a"),
                        new FirepadApplyModification(Selection.LineBefore, Firepad.Modification.bold)))
        );
        firepadAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new FirepadWrite("a")),
                new GuidedStateSpaceExplorer.SequentialStep(new FirepadApplyModification(Selection.LineAfter, Firepad.Modification.italic)),
                new GuidedStateSpaceExplorer.ParallelStep(new FirepadWrite("b"),
                        new FirepadApplyModification(Selection.LineBefore, Firepad.Modification.bold)))
        );
        firepadAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new FirepadWrite("a")),
                new GuidedStateSpaceExplorer.SequentialStep(new FirepadApplyModification(Selection.LineAfter, Firepad.Modification.italic)),
                new GuidedStateSpaceExplorer.ParallelStep(new FirepadWrite("a"),
                        new FirepadApplyModification(Selection.LineBefore, Firepad.Modification.bold)))
        );
        firepadAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new FirepadWrite("b")),
                new GuidedStateSpaceExplorer.SequentialStep(new FirepadApplyModification(Selection.LineBefore, Firepad.Modification.bold)),
                new GuidedStateSpaceExplorer.ParallelStep(new FirepadMakeFontSize(Selection.LineBefore, "18"),
                        new FirepadApplyModification(Selection.LineBefore, Firepad.Modification.bold)))
        );
        firepadAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new FirepadWrite("b")),
                new GuidedStateSpaceExplorer.SequentialStep(new FirepadApplyModification(Selection.LineBefore, Firepad.Modification.bold)),
                new GuidedStateSpaceExplorer.ParallelStep(new FirepadWrite(Keys.TAB),
                        new FirepadApplyModification(Selection.LineBefore, Firepad.Modification.bold)))
        );
        firepadAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new FirepadApplyModification(Selection.LineAfter, Firepad.Modification.italic)),
                new GuidedStateSpaceExplorer.SequentialStep(new FirepadWrite("b")),
                new GuidedStateSpaceExplorer.ParallelStep(new FirepadWrite("b"),
                        new FirepadApplyModification(Selection.LineBefore, Firepad.Modification.bold)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"), new OwncloudWrite("b")))
        );
        owncloudAS5list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                        new OwncloudApplyModification(Selection.LineBefore, Owncloud.Modification.bold)))
        );
        owncloudAS5list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                        new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana)))
        );
        owncloudAS5list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudApplyModification(Selection.LineBefore, Owncloud.Modification.bold),
                        new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana)))
        );
        owncloudAS5list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite(Keys.RETURN)),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite(Keys.RETURN)),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite(Keys.RETURN),
                        new OwncloudDelete(Selection.LineBefore)))
        );
        owncloudAS5list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite(Keys.RETURN)),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana)),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite(Keys.RETURN),
                        new OwncloudDelete(Selection.LineBefore)))
        );
        owncloudAS5list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite(Keys.RETURN)),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana)),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                        new OwncloudWrite(Keys.RETURN)))
        );
        owncloudAS5list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                        new OwncloudApplyModification(Selection.LineBefore, Owncloud.Modification.bold)))
        );
        owncloudAS5list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                        new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana)))
        );
        owncloudAS5list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudApplyModification(Selection.LineBefore, Owncloud.Modification.bold),
                        new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana)))
        );
        owncloudAS5list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudDelete(Selection.LineBefore)),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                        new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana)))
        );
        owncloudAS5list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite(Keys.RETURN)),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                        new OwncloudApplyModification(Selection.LineBefore, Owncloud.Modification.bold)))
        );
        owncloudAS5list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite(Keys.RETURN)),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite(Keys.RETURN),
                        new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana)))
        );
        owncloudAS5list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite(Keys.RETURN)),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                        new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana)))
        );
        owncloudAS5list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite(Keys.RETURN)),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudApplyModification(Selection.LineBefore, Owncloud.Modification.bold),
                        new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana)))
        );
        owncloudAS5list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudApplyModification(Selection.LineBefore, Owncloud.Modification.bold)),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                        new OwncloudApplyModification(Selection.LineBefore, Owncloud.Modification.bold)))
        );
        owncloudAS5list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudApplyModification(Selection.LineBefore, Owncloud.Modification.bold)),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                        new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana)))
        );
        owncloudAS5list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudApplyModification(Selection.LineBefore, Owncloud.Modification.bold)),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                        new OwncloudDelete(Selection.LineBefore)))
        );
        owncloudAS5list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana)),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                        new OwncloudApplyModification(Selection.LineBefore, Owncloud.Modification.bold)))
        );
        owncloudAS5list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudApplyModification(Selection.LineBefore, Owncloud.Modification.bold)),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                        new OwncloudApplyModification(Selection.LineBefore, Owncloud.Modification.bold)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana)),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                        new OwncloudWrite("b")))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana)),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                        new OwncloudWrite(Keys.RETURN)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana)),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite(Keys.RETURN),
                        new OwncloudWrite(" ")))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana)),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                        new OwncloudWrite(" ")))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana)),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                        new OwncloudWrite(Keys.TAB)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudApplyModification(Selection.LineBefore, Owncloud.Modification.bold),
                        new OwncloudWrite("b")))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                        new OwncloudApplyModification(Selection.LineBefore, Owncloud.Modification.bold)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("b"),
                        new OwncloudMakeFontSize18(Selection.LineBefore)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                        new OwncloudMakeFontSize18(Selection.LineBefore)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudApplyModification(Selection.LineBefore, Owncloud.Modification.bold),
                        new OwncloudWrite("b")))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                        new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("b"),
                        new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                        new OwncloudApplyModification(Selection.LineBefore, Owncloud.Modification.bold)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("b"),
                        new OwncloudMakeFontSize18(Selection.LineBefore)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                        new OwncloudMakeFontSize18(Selection.LineBefore)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudApplyModification(Selection.LineBefore, Owncloud.Modification.bold),
                        new OwncloudWrite("b")))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                        new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("b"),
                        new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                        new OwncloudApplyModification(Selection.LineBefore, Owncloud.Modification.bold)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudMakeFontSize18(Selection.LineBefore),
                        new OwncloudWrite(Keys.RETURN)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana),
                        new OwncloudWrite(Keys.RETURN)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudApplyModification(Selection.LineBefore, Owncloud.Modification.bold),
                        new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("b"),
                        new OwncloudMakeFontSize18(Selection.LineBefore)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudDelete(Selection.LineBefore),
                        new OwncloudMakeFontSize18(Selection.LineBefore)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                        new OwncloudMakeFontSize18(Selection.LineBefore)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudApplyModification(Selection.LineBefore, Owncloud.Modification.bold)),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudApplyModification(Selection.LineBefore, Owncloud.Modification.bold),
                        new OwncloudWrite("b")))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudApplyModification(Selection.LineBefore, Owncloud.Modification.bold)),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                        new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudApplyModification(Selection.LineBefore, Owncloud.Modification.bold)),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("b"),
                        new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudApplyModification(Selection.LineBefore, Owncloud.Modification.bold)),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                        new OwncloudApplyModification(Selection.LineBefore, Owncloud.Modification.bold)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudApplyModification(Selection.LineBefore, Owncloud.Modification.bold)),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("b"),
                        new OwncloudDelete(Selection.LineBefore)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudApplyModification(Selection.LineBefore, Owncloud.Modification.bold)),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("b"),
                        new OwncloudMakeFontSize18(Selection.LineBefore)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudApplyModification(Selection.LineBefore, Owncloud.Modification.bold)),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                        new OwncloudDelete(Selection.LineBefore)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudApplyModification(Selection.LineBefore, Owncloud.Modification.bold)),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                        new OwncloudMakeFontSize18(Selection.LineBefore)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                        new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("b"),
                        new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                        new OwncloudApplyModification(Selection.LineBefore, Owncloud.Modification.bold)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudMakeFontSize18(Selection.LineBefore),
                        new OwncloudWrite(Keys.RETURN)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana),
                        new OwncloudWrite(Keys.RETURN)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudApplyModification(Selection.LineBefore, Owncloud.Modification.bold),
                        new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("b"),
                        new OwncloudMakeFontSize18(Selection.LineBefore)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudDelete(Selection.LineBefore),
                        new OwncloudMakeFontSize18(Selection.LineBefore)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                        new OwncloudMakeFontSize18(Selection.LineBefore)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudApplyModification(Selection.LineBefore, Owncloud.Modification.bold),
                        new OwncloudWrite("b")))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                        new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("b"),
                        new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                        new OwncloudApplyModification(Selection.LineBefore, Owncloud.Modification.bold)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("b"),
                        new OwncloudMakeFontSize18(Selection.LineBefore)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                        new OwncloudMakeFontSize18(Selection.LineBefore)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite(Keys.RETURN)),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudApplyModification(Selection.LineBefore, Owncloud.Modification.bold),
                        new OwncloudWrite("b")))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite(Keys.RETURN)),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                        new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite(Keys.RETURN)),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("b"),
                        new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite(Keys.RETURN)),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                        new OwncloudApplyModification(Selection.LineBefore, Owncloud.Modification.bold)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite(Keys.RETURN)),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudMakeFontSize18(Selection.LineBefore),
                        new OwncloudWrite(Keys.RETURN)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite(Keys.RETURN)),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana),
                        new OwncloudWrite(Keys.RETURN)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite(Keys.RETURN)),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudApplyModification(Selection.LineBefore, Owncloud.Modification.bold),
                        new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite(Keys.RETURN)),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("b"),
                        new OwncloudMakeFontSize18(Selection.LineBefore)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite(Keys.RETURN)),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudDelete(Selection.LineBefore),
                        new OwncloudMakeFontSize18(Selection.LineBefore)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite(Keys.RETURN)),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                        new OwncloudMakeFontSize18(Selection.LineBefore)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana)),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudApplyModification(Selection.LineBefore, Owncloud.Modification.bold),
                        new OwncloudDelete(Selection.LineBefore)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana)),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudApplyModification(Selection.LineBefore, Owncloud.Modification.bold),
                        new OwncloudMakeFontSize18(Selection.LineBefore)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana)),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                        new OwncloudApplyModification(Selection.LineBefore, Owncloud.Modification.bold)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana)),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("b"),
                        new OwncloudMakeFontSize18(Selection.LineBefore)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana)),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                        new OwncloudMakeFontSize18(Selection.LineBefore)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite(Keys.RETURN)),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudApplyModification(Selection.LineBefore, Owncloud.Modification.bold),
                        new OwncloudWrite("b")))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite(Keys.RETURN)),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                        new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite(Keys.RETURN)),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("b"),
                        new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite(Keys.RETURN)),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                        new OwncloudApplyModification(Selection.LineBefore, Owncloud.Modification.bold)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite(Keys.RETURN)),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudMakeFontSize18(Selection.LineBefore),
                        new OwncloudWrite(Keys.RETURN)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite(Keys.RETURN)),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana),
                        new OwncloudWrite(Keys.RETURN)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite(Keys.RETURN)),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudApplyModification(Selection.LineBefore, Owncloud.Modification.bold),
                        new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite(Keys.RETURN)),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("b"),
                        new OwncloudMakeFontSize18(Selection.LineBefore)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite(Keys.RETURN)),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudDelete(Selection.LineBefore),
                        new OwncloudMakeFontSize18(Selection.LineBefore)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite(Keys.RETURN)),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                        new OwncloudMakeFontSize18(Selection.LineBefore)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudDelete(Selection.LineBefore)),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana)),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                        new OwncloudWrite("b")))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudDelete(Selection.LineBefore)),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana)),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite(Keys.RETURN),
                        new OwncloudWrite(" ")))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudApplyModification(Selection.LineAfter, Owncloud.Modification.italic)),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudApplyModification(Selection.LineBefore, Owncloud.Modification.bold),
                        new OwncloudWrite("b")))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudApplyModification(Selection.LineAfter, Owncloud.Modification.italic)),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                        new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudApplyModification(Selection.LineAfter, Owncloud.Modification.italic)),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("b"),
                        new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudApplyModification(Selection.LineAfter, Owncloud.Modification.italic)),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                        new OwncloudApplyModification(Selection.LineBefore, Owncloud.Modification.bold)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudApplyModification(Selection.LineAfter, Owncloud.Modification.italic)),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("b"),
                        new OwncloudMakeFontSize18(Selection.LineBefore)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudApplyModification(Selection.LineAfter, Owncloud.Modification.italic)),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                        new OwncloudMakeFontSize18(Selection.LineBefore)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudApplyModification(Selection.LineBefore, Owncloud.Modification.bold)),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                        new OwncloudApplyModification(Selection.LineBefore, Owncloud.Modification.bold)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudMakeFontSize18(Selection.LineBefore)),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudApplyModification(Selection.LineBefore, Owncloud.Modification.bold),
                        new OwncloudWrite("b")))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudMakeFontSize18(Selection.LineBefore)),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                        new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudMakeFontSize18(Selection.LineBefore)),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana),
                        new OwncloudWrite(Keys.RETURN)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudMakeFontSize18(Selection.LineBefore)),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("b"),
                        new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudMakeFontSize18(Selection.LineBefore)),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                        new OwncloudApplyModification(Selection.LineBefore, Owncloud.Modification.bold)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudMakeFontSize18(Selection.LineBefore)),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudApplyModification(Selection.LineBefore, Owncloud.Modification.bold),
                        new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudMakeFontSize18(Selection.LineBefore)),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                        new OwncloudDelete(Selection.LineBefore)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudMakeFontSize18(Selection.LineBefore)),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudDelete(Selection.LineBefore),
                        new OwncloudWrite(Keys.RETURN)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudApplyModification(Selection.LineBefore, Owncloud.Modification.bold),
                        new OwncloudWrite("b")))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                        new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("b"),
                        new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                        new OwncloudApplyModification(Selection.LineBefore, Owncloud.Modification.bold)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana),
                        new OwncloudMakeFontSize18(Selection.LineBefore)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("b"),
                        new OwncloudDelete(Selection.LineBefore)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("b"),
                        new OwncloudMakeFontSize18(Selection.LineBefore)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                        new OwncloudDelete(Selection.LineBefore)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                        new OwncloudMakeFontSize18(Selection.LineBefore)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudApplyModification(Selection.LineBefore, Owncloud.Modification.bold)),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                        new OwncloudApplyModification(Selection.LineBefore, Owncloud.Modification.bold)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudApplyModification(Selection.LineBefore, Owncloud.Modification.bold),
                        new OwncloudWrite("b")))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                        new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("b"),
                        new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                        new OwncloudApplyModification(Selection.LineBefore, Owncloud.Modification.bold)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("b"),
                        new OwncloudMakeFontSize18(Selection.LineBefore)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                        new OwncloudMakeFontSize18(Selection.LineBefore)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudDelete(Selection.LineBefore)),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                        new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudDelete(Selection.LineBefore)),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("b"),
                        new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudDelete(Selection.LineBefore)),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                        new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudDelete(Selection.LineBefore)),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("b"),
                        new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudDelete(Selection.LineBefore)),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana),
                        new OwncloudMakeFontSize18(Selection.LineBefore)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana)),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudApplyModification(Selection.LineBefore, Owncloud.Modification.bold),
                        new OwncloudWrite("b")))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana)),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                        new OwncloudApplyModification(Selection.LineBefore, Owncloud.Modification.bold)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana)),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("b"),
                        new OwncloudMakeFontSize18(Selection.LineBefore)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana)),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudDelete(Selection.LineBefore),
                        new OwncloudMakeFontSize18(Selection.LineBefore)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana)),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                        new OwncloudMakeFontSize18(Selection.LineBefore)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana)),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudMakeFontSize18(Selection.LineBefore),
                        new OwncloudWrite(Keys.RETURN)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite(Keys.RETURN)),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudMakeFontSize18(Selection.LineBefore)),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                        new OwncloudWrite(Keys.RETURN)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite(Keys.RETURN)),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudMakeFontSize18(Selection.LineBefore)),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("b"),
                        new OwncloudWrite(Keys.RETURN)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite(Keys.TAB)),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana),
                        new OwncloudWrite(Keys.RETURN)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite(Keys.TAB)),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudApplyModification(Selection.LineBefore, Owncloud.Modification.bold),
                        new OwncloudWrite(Keys.RETURN)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite(Keys.TAB)),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana),
                        new OwncloudDelete(Selection.LineBefore)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite(Keys.TAB)),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudMakeFontSize18(Selection.LineBefore),
                        new OwncloudWrite(Keys.RETURN)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudApplyModification(Selection.LineAfter, Owncloud.Modification.italic)),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudApplyModification(Selection.LineBefore, Owncloud.Modification.bold),
                        new OwncloudWrite("b")))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudApplyModification(Selection.LineAfter, Owncloud.Modification.italic)),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                        new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudApplyModification(Selection.LineAfter, Owncloud.Modification.italic)),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("b"),
                        new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudApplyModification(Selection.LineAfter, Owncloud.Modification.italic)),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                        new OwncloudApplyModification(Selection.LineBefore, Owncloud.Modification.bold)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudApplyModification(Selection.LineAfter, Owncloud.Modification.italic)),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("b"),
                        new OwncloudMakeFontSize18(Selection.LineBefore)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudApplyModification(Selection.LineAfter, Owncloud.Modification.italic)),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                        new OwncloudMakeFontSize18(Selection.LineBefore)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudDelete(Selection.LineBefore)),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudMakeFontSize18(Selection.LineBefore)),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                        new OwncloudWrite(Keys.RETURN)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudMakeFontSize18(Selection.LineBefore)),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudApplyModification(Selection.LineBefore, Owncloud.Modification.bold),
                        new OwncloudWrite("b")))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudMakeFontSize18(Selection.LineBefore)),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                        new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudMakeFontSize18(Selection.LineBefore)),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("b"),
                        new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudMakeFontSize18(Selection.LineBefore)),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                        new OwncloudApplyModification(Selection.LineBefore, Owncloud.Modification.bold)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudMakeFontSize18(Selection.LineBefore)),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudApplyModification(Selection.LineBefore, Owncloud.Modification.bold),
                        new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudMakeFontSize18(Selection.LineBefore)),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("b"),
                        new OwncloudDelete(Selection.LineBefore)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudMakeFontSize18(Selection.LineBefore)),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                        new OwncloudDelete(Selection.LineBefore)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite(" ")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana),
                        new OwncloudWrite(Keys.RETURN)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite(" ")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudApplyModification(Selection.LineBefore, Owncloud.Modification.bold),
                        new OwncloudWrite(Keys.RETURN)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite(" ")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana),
                        new OwncloudDelete(Selection.LineBefore)))
        );
        owncloudAS10list.add(ExplorationState.create(
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("b")),
                new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite(" ")),
                new GuidedStateSpaceExplorer.ParallelStep(new OwncloudMakeFontSize18(Selection.LineBefore),
                        new OwncloudWrite(Keys.RETURN)))
        );


        if (!Paths.exists("repeated"))
            throw new FileNotFoundException("Path not found: " + Paths.get("repeated"));

        run(gDocsAS5list, GDocsAS5);
        run(gDocsAS10list, GDocsAS10);
        run(firepadAS5list, FirepadAS5);
        run(firepadAS10list, FirepadAS10);
        run(owncloudAS5list, OwncloudAS5);
        run(owncloudAS10list, OwncloudAS10);
    }

    private static void run(List<ExplorationState> stateList, SetExperimentSpec spec)
            throws TransformerException, InterruptedException, IOException, SAXException,
            ParserConfigurationException {
        DriverSupplier supplier = Drivers.remoteDriver();
        String path = Paths.get("repeated") + "/" + spec.getName();
        Paths.exists(path);
        GuidedStateSpaceExplorer explorer = new GuidedStateSpaceExplorer(spec, supplier);
        explorer.setRunState(RunState.RUNNING);
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        for (ExplorationState state : stateList) {
            int failedCount = 0;
            for (int i = 0; i < 10; i++) {
                explorer.parallelRun(state);
                if (explorer.getLastFailed())
                    failedCount++;
                explorer.increaseRunId();
            }

            XMLFile xmlFile = new XMLFile(path + "/states.xml", "state");
            Document doc = xmlFile.document;
            Element stateEl = doc.createElement("state");
            xmlFile.root.appendChild(stateEl);
            stateEl.appendChild(createElement(doc, "state", state.toString()));
            stateEl.appendChild(createElement(doc, "failedCount", Integer.toString(failedCount)));
            transformer.transform(new DOMSource(doc), new StreamResult(xmlFile.log));
        }
    }
}
