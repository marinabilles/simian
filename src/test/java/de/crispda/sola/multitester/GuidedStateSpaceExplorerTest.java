package de.crispda.sola.multitester;

import com.google.common.collect.Sets;
import de.crispda.sola.multitester.runner.ExperimentSpec;
import de.crispda.sola.multitester.runner.SetExperimentSpec;
import de.crispda.sola.multitester.scenario.*;
import de.crispda.sola.multitester.util.ImageFrame;
import de.crispda.sola.multitester.web.*;
import org.openqa.selenium.Keys;

import javax.xml.transform.TransformerConfigurationException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class GuidedStateSpaceExplorerTest {
    @org.testng.annotations.Test
    public void parallelRunTest() throws Exception {
        Interaction act1 = new GDocsWrite("a");
        Interaction act2 = new GDocsWrite("b");
        SetExperimentSpec spec = new SetExperimentSpec(
                Sets.newHashSet(act1, act2),
                new GDocsFailsafeCombinator(true),
                GDocs.exclusionRectangles,
                new GDocsInitCompleteReset(),
                new GDocsCursorSender(),
                new GDocsCursorReceiver(),
                "Test spec");
        RemoteDriverSupplier supplier = Drivers.remoteDriver(true);
        GuidedStateSpaceExplorer explorer = new GuidedStateSpaceExplorer(
                spec, supplier);
        explorer.setRunState(RunState.RUNNING);

        ExplorationState state = new ExplorationState();
        state.add(new GuidedStateSpaceExplorer.SequentialStep(act1));
        state.add(new GuidedStateSpaceExplorer.ParallelStep(act1, act2));
        try {
            explorer.parallelRun(state);
        } finally {
            TestUtil.waitFor(supplier);
        }
    }

    private void explore(String specName, ExplorationState state) throws TransformerConfigurationException,
            InterruptedException {
        RemoteDriverSupplier supplier = Drivers.remoteDriver(true);
        SetExperimentSpec spec = (SetExperimentSpec) ExperimentSpec.getExperimentSpecs().stream()
                .filter(s -> s.getName().equals(specName)).findFirst()
                .orElseThrow(() -> new RuntimeException("Spec not found."));
        GuidedStateSpaceExplorer explorer = new GuidedStateSpaceExplorer(spec, supplier);
        explorer.setRunState(RunState.RUNNING);
        try {
            explorer.parallelRun(state);
        } finally {
            TestUtil.waitFor(supplier);
        }
    }

    private void gDocs3(ExplorationState state) throws TransformerConfigurationException, InterruptedException {
        explore("GDocs Reduced Test 3", state);
    }

    // false positive (?)
    @org.testng.annotations.Test
    public void ex120_34() throws Exception {
        ExplorationState state = new ExplorationState();
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new GDocsWrite(Keys.HOME)));
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new GDocsWrite("a")));
        state.add(new GuidedStateSpaceExplorer.ParallelStep(new GDocsWrite(Keys.BACK_SPACE),
                        new GDocsButtonClick(GDocs.Button.redo)));
        gDocs3(state);
    }

    // false positive
    @org.testng.annotations.Test
    public void ex120_49() throws InterruptedException, TransformerConfigurationException {
        ExplorationState state = new ExplorationState();
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new GDocsWrite("a")));
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new GDocsApplyModification(Selection.LineBefore,
                GDocs.Modification.italic)));
        state.add(new GuidedStateSpaceExplorer.ParallelStep(new GDocsButtonClick(GDocs.Button.undo),
                new GDocsApplyModification(Selection.LineBefore, GDocs.Modification.italic)));
        gDocs3(state);
    }

    // reproduced
    @org.testng.annotations.Test
    public void ex120_55() throws InterruptedException, TransformerConfigurationException {
        ExplorationState state = new ExplorationState();
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new GDocsWrite("a")));
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new GDocsApplyModification(Selection.LineBefore,
                GDocs.Modification.italic)));
        state.add(new GuidedStateSpaceExplorer.ParallelStep(new GDocsWrite("a"),
                new GDocsWrite(Keys.TAB)));
        gDocs3(state);
    }

    // reproduced
    @org.testng.annotations.Test
    public void ex120_57() throws InterruptedException, TransformerConfigurationException {
        ExplorationState state = new ExplorationState();
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new GDocsWrite("a")));
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new GDocsApplyModification(Selection.LineBefore,
                GDocs.Modification.italic)));
        state.add(new GuidedStateSpaceExplorer.ParallelStep(new GDocsWrite("a"),
                new GDocsWrite(Keys.BACK_SPACE)));
        gDocs3(state);
    }

    // reproduced
    @org.testng.annotations.Test
    public void ex120_59() throws InterruptedException, TransformerConfigurationException {
        ExplorationState state = new ExplorationState();
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new GDocsWrite("a")));
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new GDocsApplyModification(Selection.LineBefore,
                GDocs.Modification.italic)));
        state.add(new GuidedStateSpaceExplorer.ParallelStep(new GDocsWrite("a"),
                new GDocsDelete(Selection.LineBefore)));
        gDocs3(state);
    }

    // reproduced
    @org.testng.annotations.Test
    public void ex120_133() throws InterruptedException, TransformerConfigurationException {
        ExplorationState state = new ExplorationState();
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new GDocsApplyModification(Selection.LineAfter,
                GDocs.Modification.bold)));
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new GDocsWrite("a")));
        state.add(new GuidedStateSpaceExplorer.ParallelStep(new GDocsWrite("a"),
                new GDocsWrite(Keys.TAB)));
        gDocs3(state);
    }

    // reproduced
    @org.testng.annotations.Test
    public void ex120_136() throws InterruptedException, TransformerConfigurationException {
        ExplorationState state = new ExplorationState();
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new GDocsApplyModification(Selection.LineAfter,
                GDocs.Modification.bold)));
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new GDocsWrite("a")));
        state.add(new GuidedStateSpaceExplorer.ParallelStep(new GDocsWrite("a"),
                new GDocsWrite(Keys.BACK_SPACE)));
        gDocs3(state);
    }

    // reproduced
    @org.testng.annotations.Test
    public void ex120_139() throws InterruptedException, TransformerConfigurationException {
        ExplorationState state = new ExplorationState();
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new GDocsApplyModification(Selection.LineAfter,
                GDocs.Modification.bold)));
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new GDocsWrite("a")));
        state.add(new GuidedStateSpaceExplorer.ParallelStep(new GDocsWrite("a"),
                new GDocsDelete(Selection.LineBefore)));
        gDocs3(state);
    }

    private void firepad(ExplorationState state) throws TransformerConfigurationException, InterruptedException {
        explore("Firepad Reduced Test", state);
    }

    // reproduced
    @org.testng.annotations.Test
    public void ex122_4() throws Exception {
        ExplorationState state = new ExplorationState();
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new FirepadWrite("a")));
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new FirepadWrite("a")));
        state.add(new GuidedStateSpaceExplorer.ParallelStep(new FirepadApplyModification(Selection.LineBefore,
                Firepad.Modification.italic),
                new FirepadWrite(Keys.BACK_SPACE)));
        firepad(state);
    }

    // reproduced
    @org.testng.annotations.Test
    public void ex122_46() throws Exception {
        ExplorationState state = new ExplorationState();
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new FirepadClickButton(Firepad.Button.undo)));
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new FirepadClickButton(Firepad.Button.bulletList)));
        state.add(new GuidedStateSpaceExplorer.ParallelStep(new FirepadApplyModification(Selection.LineAfter,
                Firepad.Modification.bold),
                new FirepadWrite(Keys.TAB)));
        firepad(state);
    }

    // reproduced
    @org.testng.annotations.Test
    public void ex122_55() throws Exception {
        ExplorationState state = new ExplorationState();
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new FirepadClickButton(Firepad.Button.undo)));
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new FirepadClickButton(Firepad.Button.bulletList)));
        state.add(new GuidedStateSpaceExplorer.ParallelStep(new FirepadApplyModification(Selection.LineAfter,
                Firepad.Modification.bold),
                new FirepadWrite(Keys.BACK_SPACE)));
        firepad(state);
    }

    // reproduced
    @org.testng.annotations.Test
    public void ex122_63() throws Exception {
        ExplorationState state = new ExplorationState();
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new FirepadClickButton(Firepad.Button.undo)));
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new FirepadClickButton(Firepad.Button.undo)));
        state.add(new GuidedStateSpaceExplorer.ParallelStep(new FirepadApplyModification(Selection.LineAfter,
                Firepad.Modification.bold),
                new FirepadWrite(Keys.TAB)));
        firepad(state);
    }

    // reproduced
    @org.testng.annotations.Test
    public void ex122_110() throws Exception {
        ExplorationState state = new ExplorationState();
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new FirepadApplyModification(Selection.LineAfter,
                Firepad.Modification.bold)));
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new FirepadWrite("a")));
        state.add(new GuidedStateSpaceExplorer.ParallelStep(new FirepadWrite("a"),
                new FirepadApplyModification(Selection.LineBefore, Firepad.Modification.italic)));
        firepad(state);
    }

    // reproduced
    @org.testng.annotations.Test
    public void ex122_128() throws Exception {
        ExplorationState state = new ExplorationState();
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new FirepadApplyModification(Selection.LineAfter,
                Firepad.Modification.bold)));
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new FirepadClickButton(Firepad.Button.undo)));
        state.add(new GuidedStateSpaceExplorer.ParallelStep(new FirepadApplyModification(Selection.LineAfter,
                Firepad.Modification.bold),
                new FirepadWrite(Keys.TAB)));
        firepad(state);
    }

    // false positive
    @org.testng.annotations.Test
    public void ex122_130() throws Exception {
        ExplorationState state = new ExplorationState();
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new FirepadClickButton(Firepad.Button.undo)));
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new FirepadApplyModification(Selection.LineAfter,
                Firepad.Modification.bold)));
        state.add(new GuidedStateSpaceExplorer.ParallelStep(new FirepadClickButton(Firepad.Button.undo),
                new FirepadWrite(Keys.TAB)));
        firepad(state);
    }

    // false positive
    @org.testng.annotations.Test
    public void ex122_130_lin1() throws Exception {
        ExplorationState state = new ExplorationState();
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new FirepadClickButton(Firepad.Button.undo)));
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new FirepadApplyModification(Selection.LineAfter,
                Firepad.Modification.bold)));
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new FirepadClickButton(Firepad.Button.undo)));
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new FirepadWrite(Keys.TAB)));
        firepad(state);
    }

    // false positive
    @org.testng.annotations.Test
    public void ex122_130_lin2() throws Exception {
        ExplorationState state = new ExplorationState();
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new FirepadClickButton(Firepad.Button.undo)));
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new FirepadApplyModification(Selection.LineAfter,
                Firepad.Modification.bold)));
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new FirepadWrite(Keys.TAB)));
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new FirepadClickButton(Firepad.Button.undo)));
        firepad(state);
    }

    // false positive
    @org.testng.annotations.Test
    public void ex122_131() throws Exception {
        ExplorationState state = new ExplorationState();
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new FirepadClickButton(Firepad.Button.undo)));
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new FirepadApplyModification(Selection.LineAfter,
                Firepad.Modification.bold)));
        state.add(new GuidedStateSpaceExplorer.ParallelStep(new FirepadClickButton(Firepad.Button.undo),
                new FirepadApplyModification(Selection.LineBefore, Firepad.Modification.italic)));
        firepad(state);
    }

    // reproduced
    @org.testng.annotations.Test
    public void ex122_149() throws Exception {
        ExplorationState state = new ExplorationState();
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new FirepadWrite(Keys.HOME)));
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new FirepadClickButton(Firepad.Button.undo)));
        state.add(new GuidedStateSpaceExplorer.ParallelStep(new FirepadWrite("a"),
                new FirepadApplyModification(Selection.LineAfter, Firepad.Modification.bold)));
        firepad(state);
    }

    // reproduced
    @org.testng.annotations.Test
    public void ex122_162() throws Exception {
        ExplorationState state = new ExplorationState();
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new FirepadApplyModification(Selection.LineBefore,
                Firepad.Modification.italic)));
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new FirepadWrite("a")));
        state.add(new GuidedStateSpaceExplorer.ParallelStep(new FirepadApplyModification(Selection.LineBefore,
                Firepad.Modification.italic),
                new FirepadWrite(Keys.TAB)));
        firepad(state);
    }

    // reproduced
    @org.testng.annotations.Test
    public void ex122_178() throws Exception {
        ExplorationState state = new ExplorationState();
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new FirepadClickButton(Firepad.Button.undo)));
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new FirepadDelete(Selection.LineBefore)));
        state.add(new GuidedStateSpaceExplorer.ParallelStep(new FirepadWrite(("a")),
                new FirepadApplyModification(Selection.LineAfter, Firepad.Modification.bold)));
        firepad(state);
    }

    private void owncloud127(ExplorationState state) throws TransformerConfigurationException, InterruptedException {
        owncloud(state, "Owncloud Reduced Test");
    }

    private void owncloud(ExplorationState state, String specName) throws TransformerConfigurationException,
            InterruptedException {
        RemoteDriverSupplier supplier = Drivers.remoteDriver(true);
        SetExperimentSpec spec = (SetExperimentSpec) ExperimentSpec.getExperimentSpecs().stream()
                .filter(s -> s.getName().equals(specName)).findFirst()
                .orElseThrow(() -> new RuntimeException("Spec not found."));
        ((OwncloudCombinator) spec.combinator).setLogOut(false);
        GuidedStateSpaceExplorer explorer = new GuidedStateSpaceExplorer(spec, supplier);
        explorer.setRunState(RunState.RUNNING);
        List<Optional<byte[]>> result = null;
        try {
            result = explorer.parallelRun(state);
        } finally {
            List<WrappedDriver> drivers = supplier.getDrivers();
            WrappedDriver d1 = drivers.get(0);
            WrappedDriver d2 = drivers.get(1);
            TestUtil.waitFor(supplier, () -> {
                new Owncloud(d1).logout();
                new Owncloud(d2).logout();
                return null;
            });

            if (result != null && result.size() == 2) {
                Optional<byte[]> optFirst = result.get(0);
                Optional<byte[]> optSecond = result.get(1);
                if (optFirst.isPresent() && optSecond.isPresent()) {
                    try {
                        ImageFrame.showImage(Images.getDiff(optFirst.get(), optSecond.get(),
                                spec.exclusionRectangles).getImage());
                    } catch (IOException | ImageDimensionException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    // reproduced (+manually)
    @org.testng.annotations.Test
    public void ex127_29() throws Exception {
        ExplorationState state = new ExplorationState();
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")));
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new OwncloudIncreaseFontSize(Selection.LineBefore)));
        state.add(new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite(("a")),
                new OwncloudIncreaseFontSize(Selection.LineBefore)));
        owncloud127(state);
    }

    // reproduced (+manually)
    @org.testng.annotations.Test
    public void ex127_71() throws Exception {
        ExplorationState state = new ExplorationState();
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new OwncloudApplyModification(Selection.LineAfter,
                Owncloud.Modification.bold)));
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")));
        state.add(new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite(("a")),
                new OwncloudDelete(Selection.LineBefore)));
        owncloud127(state);
    }

    // reproduced (+manually)
    @org.testng.annotations.Test
    public void ex127_76() throws Exception {
        ExplorationState state = new ExplorationState();
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new OwncloudApplyModification(Selection.LineAfter,
                Owncloud.Modification.bold)));
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")));
        state.add(new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite(("a")),
                new OwncloudWrite(Keys.BACK_SPACE)));
        owncloud127(state);
    }

    // reproduced
    @org.testng.annotations.Test
    public void ex128_2() throws Exception {
        ExplorationState state = new ExplorationState();
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")));
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new OwncloudIncreaseFontSize(Selection.LineBefore)));
        state.add(new GuidedStateSpaceExplorer.ParallelStep(new OwncloudMakeFont(Selection.LineBefore,
                        Owncloud.Font.arial),
                new OwncloudIncreaseFontSize(Selection.LineBefore)));
        owncloud127(state);
    }

    // not reproduced
    @org.testng.annotations.Test
    public void ex128_3() throws Exception {
        ExplorationState state = new ExplorationState();
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")));
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new OwncloudIncreaseFontSize(Selection.LineBefore)));
        state.add(new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.arial)));
        owncloud127(state);
    }

    // false positive
    @org.testng.annotations.Test
    public void ex128_7() throws Exception {
        ExplorationState state = new ExplorationState();
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")));
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")));
        state.add(new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite(Keys.BACK_SPACE),
                new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.arial)));
        owncloud127(state);
    }

    // false positive
    @org.testng.annotations.Test
    public void ex128_11() throws Exception {
        ExplorationState state = new ExplorationState();
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")));
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")));
        state.add(new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite(Keys.BACK_SPACE),
                new OwncloudIncreaseFontSize(Selection.LineBefore)));
        owncloud127(state);
    }

    // reproduced (+manually)
    @org.testng.annotations.Test
    public void ex128_13() throws Exception {
        ExplorationState state = new ExplorationState();
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")));
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")));
        state.add(new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                new OwncloudIncreaseFontSize(Selection.LineBefore)));
        owncloud127(state);
    }

    // false positive
    @org.testng.annotations.Test
    public void ex128_17() throws Exception {
        ExplorationState state = new ExplorationState();
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new OwncloudApplyModification(Selection.LineAfter,
                Owncloud.Modification.bold)));
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")));
        state.add(new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.arial)));
        owncloud127(state);
    }

    // reproduced (+manually)
    @org.testng.annotations.Test
    public void ex128_20() throws Exception {
        ExplorationState state = new ExplorationState();
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new OwncloudApplyModification(Selection.LineAfter,
                Owncloud.Modification.bold)));
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")));
        state.add(new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                new OwncloudWrite(Keys.BACK_SPACE)));
        owncloud127(state);
    }

    // reproduced (+manually)
    @org.testng.annotations.Test
    public void ex128_22() throws Exception {
        ExplorationState state = new ExplorationState();
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new OwncloudApplyModification(Selection.LineAfter,
                Owncloud.Modification.bold)));
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")));
        state.add(new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                new OwncloudDelete(Selection.LineBefore)));
        owncloud127(state);
    }

    // reproduced (+manually)
    @org.testng.annotations.Test
    public void ex128_23() throws Exception {
        ExplorationState state = new ExplorationState();
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new OwncloudApplyModification(Selection.LineAfter,
                Owncloud.Modification.bold)));
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")));
        state.add(new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                new OwncloudIncreaseFontSize(Selection.LineBefore)));
        owncloud127(state);
    }

    // false positive
    @org.testng.annotations.Test
    public void ex128_26() throws Exception {
        ExplorationState state = new ExplorationState();
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")));
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new OwncloudMakeFont(Selection.LineBefore,
                Owncloud.Font.arial)));
        state.add(new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                new OwncloudWrite(Keys.BACK_SPACE)));
        owncloud127(state);
    }

    // false positive
    @org.testng.annotations.Test
    public void ex128_28() throws Exception {
        ExplorationState state = new ExplorationState();
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")));
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new OwncloudMakeFont(Selection.LineBefore,
                Owncloud.Font.arial)));
        state.add(new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                new OwncloudDelete(Selection.LineBefore)));
        owncloud127(state);
    }

    // reproduced
    @org.testng.annotations.Test
    public void ex128_29() throws Exception {
        ExplorationState state = new ExplorationState();
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")));
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new OwncloudMakeFont(Selection.LineBefore,
                Owncloud.Font.arial)));
        state.add(new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                new OwncloudIncreaseFontSize(Selection.LineBefore)));
        owncloud127(state);
    }

    // reproduced
    @org.testng.annotations.Test
    public void ex128_38() throws Exception {
        ExplorationState state = new ExplorationState();
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite(Keys.BACK_SPACE)));
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")));
        state.add(new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                new OwncloudIncreaseFontSize(Selection.LineBefore)));
        owncloud127(state);
    }

    // false positive
    @org.testng.annotations.Test
    public void ex128_39() throws Exception {
        ExplorationState state = new ExplorationState();
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite(Keys.TAB)));
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")));
        state.add(new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite(Keys.BACK_SPACE),
                new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.arial)));
        owncloud127(state);
    }

    // false positive
    @org.testng.annotations.Test
    public void ex128_43() throws Exception {
        ExplorationState state = new ExplorationState();
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite(Keys.TAB)));
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")));
        state.add(new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite(Keys.BACK_SPACE),
                new OwncloudIncreaseFontSize(Selection.LineBefore)));
        owncloud127(state);
    }

    private void owncloud130(ExplorationState state) throws Exception {
        owncloud(state, "Owncloud Reduced Test 2");
    }

    // reproduced
    @org.testng.annotations.Test
    public void ex130_6() throws Exception {
        ExplorationState state = new ExplorationState();
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")));
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")));
        state.add(new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                new OwncloudIncreaseFontSize(Selection.LineBefore)));
        owncloud130(state);
    }

    // reproduced
    @org.testng.annotations.Test
    public void ex130_9() throws Exception {
        ExplorationState state = new ExplorationState();
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")));
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")));
        state.add(new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.timesNewRoman)));
        owncloud130(state);
    }

    // reproduced
    @org.testng.annotations.Test
    public void ex130_15() throws Exception {
        ExplorationState state = new ExplorationState();
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new OwncloudIncreaseFontSize(
                Selection.LineBefore)));
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")));
        state.add(new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                new OwncloudDelete(Selection.LineBefore)));
        owncloud130(state);
    }

    // reproduced
    @org.testng.annotations.Test
    public void ex130_16() throws Exception {
        ExplorationState state = new ExplorationState();
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new OwncloudIncreaseFontSize(
                Selection.LineBefore)));
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")));
        state.add(new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                new OwncloudIncreaseFontSize(Selection.LineBefore)));
        owncloud130(state);
    }

    // reproduced
    @org.testng.annotations.Test
    public void ex130_19() throws Exception {
        ExplorationState state = new ExplorationState();
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new OwncloudIncreaseFontSize(
                Selection.LineBefore)));
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")));
        state.add(new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.timesNewRoman)));
        owncloud130(state);
    }

    // reproduced
    @org.testng.annotations.Test
    public void ex130_37() throws Exception {
        ExplorationState state = new ExplorationState();
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")));
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new OwncloudApplyModification(
                Selection.LineAfter, Owncloud.Modification.bold)));
        state.add(new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                new OwncloudIncreaseFontSize(Selection.LineBefore)));
        owncloud130(state);
    }

    // reproduced
    @org.testng.annotations.Test
    public void ex130_40() throws Exception {
        ExplorationState state = new ExplorationState();
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")));
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new OwncloudApplyModification(
                Selection.LineAfter, Owncloud.Modification.bold)));
        state.add(new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.timesNewRoman)));
        owncloud130(state);
    }

    // reproduced
    @org.testng.annotations.Test
    public void ex130_50() throws Exception {
        ExplorationState state = new ExplorationState();
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new OwncloudMakeFont(
                Selection.LineBefore, Owncloud.Font.timesNewRoman)));
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")));
        state.add(new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                new OwncloudDelete(Selection.LineBefore)));
        owncloud130(state);
    }

    // reproduced
    @org.testng.annotations.Test
    public void ex130_51() throws Exception {
        ExplorationState state = new ExplorationState();
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new OwncloudMakeFont(
                Selection.LineBefore, Owncloud.Font.timesNewRoman)));
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")));
        state.add(new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                new OwncloudIncreaseFontSize(Selection.LineBefore)));
        owncloud130(state);
    }

    // reproduced
    @org.testng.annotations.Test
    public void ex130_67() throws Exception {
        ExplorationState state = new ExplorationState();
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new OwncloudApplyModification(
                Selection.LineAfter, Owncloud.Modification.bold)));
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")));
        state.add(new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                new OwncloudDelete(Selection.LineBefore)));
        owncloud130(state);
    }

    // reproduced
    @org.testng.annotations.Test
    public void ex130_68() throws Exception {
        ExplorationState state = new ExplorationState();
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new OwncloudApplyModification(
                Selection.LineAfter, Owncloud.Modification.bold)));
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")));
        state.add(new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                new OwncloudIncreaseFontSize(Selection.LineBefore)));
        owncloud130(state);
    }

    // reproduced
    @org.testng.annotations.Test
    public void ex130_71() throws Exception {
        ExplorationState state = new ExplorationState();
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new OwncloudApplyModification(
                Selection.LineAfter, Owncloud.Modification.bold)));
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new OwncloudWrite("a")));
        state.add(new GuidedStateSpaceExplorer.ParallelStep(new OwncloudWrite("a"),
                new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.timesNewRoman)));
        owncloud130(state);
    }


    // Ex 132
    private void gDocsAS5(ExplorationState state) throws TransformerConfigurationException, InterruptedException {
        explore("GDocs AS5", state);
    }

    // manually reproduced
    @org.testng.annotations.Test
    public void ex132_21() throws Exception {
        ExplorationState state = new ExplorationState();
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new GDocsWrite("a")));
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new GDocsMakeFont(Selection.LineBefore,
                GDocs.Font.verdana)));
        state.add(new GuidedStateSpaceExplorer.ParallelStep(new GDocsWrite("a"),
                new GDocsDelete(Selection.LineBefore)));
        gDocsAS5(state);
    }

    // automatically reproduced but failed to reproduce manually
    @org.testng.annotations.Test
    public void ex132_23() throws Exception {
        ExplorationState state = new ExplorationState();
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new GDocsWrite("a")));
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new GDocsMakeFont(Selection.LineBefore,
                GDocs.Font.verdana)));
        state.add(new GuidedStateSpaceExplorer.ParallelStep(new GDocsWrite("a"),
                new GDocsWrite(Keys.RETURN)));
        gDocsAS5(state);
    }

    // manually reproduced
    @org.testng.annotations.Test
    public void ex132_27() throws Exception {
        ExplorationState state = new ExplorationState();
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new GDocsWrite("a")));
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new GDocsApplyModification(Selection.LineBefore,
                GDocs.Modification.bold)));
        state.add(new GuidedStateSpaceExplorer.ParallelStep(new GDocsWrite("a"),
                new GDocsDelete(Selection.LineBefore)));
        gDocsAS5(state);
    }

    // automatically reproduced but failed to reproduce manually
    @org.testng.annotations.Test
    public void ex132_29() throws Exception {
        ExplorationState state = new ExplorationState();
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new GDocsWrite("a")));
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new GDocsApplyModification(Selection.LineBefore,
                GDocs.Modification.bold)));
        state.add(new GuidedStateSpaceExplorer.ParallelStep(new GDocsWrite("a"),
                new GDocsWrite(Keys.RETURN)));
        gDocsAS5(state);
    }

    // Ex 133
    private void firepadAS5(ExplorationState state) throws TransformerConfigurationException, InterruptedException {
        explore("Firepad AS5", state);
    }

    @org.testng.annotations.Test
    public void ex133_3() throws Exception {
        ExplorationState state = new ExplorationState();
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new FirepadApplyModification(Selection.LineBefore,
                Firepad.Modification.bold)));
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new FirepadWrite("a")));
        state.add(new GuidedStateSpaceExplorer.ParallelStep(new FirepadWrite("a"),
                new FirepadApplyModification(Selection.LineBefore, Firepad.Modification.bold)));
        firepadAS5(state);
    }

    @org.testng.annotations.Test
    public void fontTest2() throws Exception {
        ExplorationState state = new ExplorationState();
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new GDocsWrite("a")));
        state.add(new GuidedStateSpaceExplorer.ParallelStep(new GDocsWrite("a"),
                new GDocsMakeFont(Selection.LineBefore, GDocs.Font.verdana)));
        gDocsAS5(state);
    }

    @org.testng.annotations.Test
    public void fontTest3() throws Exception {
        ExplorationState state = new ExplorationState();
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new GDocsWrite("a")));
        state.add(new GuidedStateSpaceExplorer.SequentialStep(new GDocsMakeFont(Selection.LineBefore, GDocs.Font.verdana)));
        gDocsAS5(state);
    }
}
