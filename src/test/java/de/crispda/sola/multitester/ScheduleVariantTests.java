package de.crispda.sola.multitester;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import de.crispda.sola.multitester.scenario.*;
import de.crispda.sola.multitester.util.DebugDisplay;
import de.crispda.sola.multitester.util.FrameWaiter;
import de.crispda.sola.multitester.util.WaiterFrame;
import de.crispda.sola.multitester.web.Drivers;
import org.openqa.selenium.Keys;
import org.testng.Assert;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CyclicBarrier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ScheduleVariantTests {

    @org.testng.annotations.Test
    public void sequence() {
        final List<Test> tests = Lists.newArrayList(
                new Test() {
                    @Override
                    public void test() throws Exception {
                        maybeWait();
                        maybeWait();
                    }

                    @Override
                    public int getMaybeWaitCount() {
                        return 2;
                    }

                    @Override
                    public String getInitialURL() {
                        return null;
                    }
                },
                new Test() {
                    @Override
                    public void test() throws Exception {
                        maybeWait();
                        maybeWait();
                        maybeWait();
                    }

                    @Override
                    public int getMaybeWaitCount() {
                        return 3;
                    }

                    @Override
                    public String getInitialURL() {
                        return null;
                    }
                }
        );

        final MultiTestGenerator generator = new SimpleMultiTestGenerator();
        generator.putAll(tests);
        final Set<MultiTest<Test>> multiTests = generator.generateTests();
        Assert.assertEquals(multiTests.size(), 1);

        final VariantCreator variantCreator = new SimpleVariantCreator();
        variantCreator.putAll(multiTests);
        final List<ScheduleVariant<?>> variants = variantCreator.createVariants();
        Assert.assertEquals(variants.size(), 1);

        final List<String> sequence = variants.get(0).toSequence();

        Assert.assertEquals(sequence.size(), 4);
        Assert.assertEquals(sequence.get(0), "First.1");
        Assert.assertEquals(sequence.get(1), "Second.1");
        Assert.assertEquals(sequence.get(2), "Second.2");
        Assert.assertEquals(sequence.get(3), "First.2 and Second.3");
    }

    @org.testng.annotations.Test
    public void schedule73_0() throws Exception {
        DebugDisplay debugDisplay = new DebugDisplay();

        FirepadCombinator combinatorFirst = new FirepadCombinator(Firepad.url);
        combinatorFirst.setDebugAdapter(debugDisplay.first());

        FirepadCombinator combinatorSecond = new FirepadCombinator(Firepad.url);
        combinatorSecond.setDebugAdapter(debugDisplay.second());

        Test first = combinatorFirst.combine(Lists.newArrayList(
                new FirepadMakeFontColor(Selection.LineBefore, "blue"),
                new FirepadApplyModification(Selection.LineBefore, Firepad.Modification.underlined),
                new FirepadClickButton(Firepad.Button.paragraphRight),
                new FirepadMakeFont(Selection.WordAfter, Firepad.Font.comicSans),
                new FirepadWrite("c"),
                new FirepadMakeFont(Selection.LineAfter, Firepad.Font.timesNewRoman),
                new FirepadApplyModification(Selection.WordAfter, Firepad.Modification.underlined),
                new FirepadClickButton(Firepad.Button.undo),
                new FirepadMakeFontSize(Selection.WordAfter, "24"),
                new FirepadClickButton(Firepad.Button.undo)
        ));
        Test second = combinatorSecond.combine(Lists.newArrayList(
                new FirepadDelete(Selection.LineAfter),
                new FirepadMakeFont(Selection.WordAfter, Firepad.Font.courierNew),
                new FirepadWrite("b"),
                new FirepadMakeFont(Selection.LineAfter, Firepad.Font.arial),
                new FirepadWrite("text"),
                new FirepadMakeFontSize(Selection.LineAfter, "14"),
                new FirepadWrite(" This"),
                new FirepadWrite(Keys.RETURN),
                new FirepadWrite("."),
                new FirepadApplyModification(Selection.LineBefore, Firepad.Modification.underlined)
        ));

        int[] mw1 = {1, 1, 0, 1, 2, 1, 1, 1, 0, 1, 0};
        int[] mw2 = {2, 1, 0, 1, 3, 0, 0, 0, 1, 0, 1};
        runTest(mw1, mw2, first, second, new FirepadInitEmpty());
    }

    @org.testng.annotations.Test
    public void ex80_60() throws Exception {
        DebugDisplay debugDisplay = new DebugDisplay();

        GDocsFailsafeCombinator combinatorFirst = new GDocsFailsafeCombinator();
        combinatorFirst.setDebugAdapter(debugDisplay.first());

        GDocsFailsafeCombinator combinatorSecond = new GDocsFailsafeCombinator();
        combinatorSecond.setDebugAdapter(debugDisplay.second());

        Test first = combinatorFirst.combine(Lists.newArrayList(
                new GDocsApplyModification(Selection.WordAfter, GDocs.Modification.bold),
                new GDocsDelete(Selection.WordBefore),
                new GDocsWrite("a"),
                new GDocsApplyModification(Selection.WordBefore, GDocs.Modification.bold),
                new GDocsApplyModification(Selection.LineAfter, GDocs.Modification.italic),
                new GDocsInteractionInsertTable(),
                new GDocsWrite(" This"),
                new GDocsWrite(Keys.BACK_SPACE, "a"),
                new GDocsWrite(Keys.BACK_SPACE, Keys.BACK_SPACE, Keys.BACK_SPACE),
                new GDocsWrite(Keys.BACK_SPACE, "a")
        ));
        Test second = combinatorSecond.combine(Lists.newArrayList(
                new GDocsWrite("c"),
                new GDocsApplyModification(Selection.LineBefore, GDocs.Modification.bold),
                new GDocsWrite("test"),
                new GDocsWrite(Keys.TAB),
                new GDocsDelete(Selection.WordAfter),
                new GDocsWrite("a"),
                new GDocsWrite(Keys.TAB),
                new GDocsWrite("t"),
                new GDocsWrite(Keys.BACK_SPACE, Keys.BACK_SPACE, Keys.BACK_SPACE),
                new GDocsWrite(Keys.RIGHT)
        ));

        int[] mw1 = {1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0};
        int[] mw2 = {0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1};
        runTest(mw1, mw2, first, second, new GDocsInitEmpty());
    }

    @org.testng.annotations.Test
    public void ex80_78() throws Exception {
        DebugDisplay debugDisplay = new DebugDisplay();

        GDocsFailsafeCombinator combinatorFirst = new GDocsFailsafeCombinator();
        combinatorFirst.setDebugAdapter(debugDisplay.first());

        GDocsFailsafeCombinator combinatorSecond = new GDocsFailsafeCombinator();
        combinatorSecond.setDebugAdapter(debugDisplay.second());

        Test first = combinatorFirst.combine(Lists.newArrayList(
                new GDocsApplyModification(Selection.WordAfter, GDocs.Modification.italic),
                new GDocsApplyModification(Selection.WordAfter, GDocs.Modification.italic),
                new GDocsDelete(Selection.WordAfter),
                new GDocsWrite(Keys.DELETE),
                new GDocsWrite(Keys.HOME),
                new GDocsDelete(Selection.LineAfter),
                new GDocsApplyModification(Selection.WordAfter, GDocs.Modification.underlined),
                new GDocsWrite("-------"),
                new GDocsWrite("text"),
                new GDocsWrite(" ")
        ));
        Test second = combinatorSecond.combine(Lists.newArrayList(
                new GDocsWrite(" "),
                new GDocsWrite("more text"),
                new GDocsApplyModification(Selection.LineAfter, GDocs.Modification.italic),
                new GDocsApplyModification(Selection.LineBefore, GDocs.Modification.bold),
                new GDocsWrite("-------"),
                new GDocsWrite(Keys.HOME),
                new GDocsWrite(Keys.RETURN),
                new GDocsApplyModification(Selection.LineAfter, GDocs.Modification.underlined),
                new GDocsWrite("text"),
                new GDocsWrite("a")
        ));

        int[] mw1 = {0, 0, 3, 0, 1, 1, 1, 0, 0, 0, 1};
        int[] mw2 = {1, 0, 0, 0, 0, 0, 1, 1, 0, 2, 2};
        runTest(mw1, mw2, first, second, new GDocsInitEmpty());
    }

    @org.testng.annotations.Test
    public void ex80_141() throws Exception {
        DebugDisplay debugDisplay = new DebugDisplay();

        GDocsFailsafeCombinator combinatorFirst = new GDocsFailsafeCombinator();
        combinatorFirst.setDebugAdapter(debugDisplay.first());

        GDocsFailsafeCombinator combinatorSecond = new GDocsFailsafeCombinator();
        combinatorSecond.setDebugAdapter(debugDisplay.second());

        Test first = combinatorFirst.combine(Lists.newArrayList(
                new GDocsWrite("-------"),
                new GDocsApplyModification(Selection.LineBefore, GDocs.Modification.underlined),
                new GDocsApplyModification(Selection.WordAfter, GDocs.Modification.underlined),
                new GDocsButtonClick(GDocs.Button.undo),
                new GDocsWrite(" This"),
                new GDocsWrite("text", Keys.RETURN),
                new GDocsWrite(Keys.DOWN),
                new GDocsApplyModification(Selection.WordBefore, GDocs.Modification.underlined),
                new GDocsWrite(Keys.DOWN),
                new GDocsApplyModification(Selection.LineBefore, GDocs.Modification.bold)
        ));
        Test second = combinatorSecond.combine(Lists.newArrayList(
                new GDocsWrite("text"),
                new GDocsApplyModification(Selection.WordAfter, GDocs.Modification.italic),
                new GDocsWrite("a"),
                new GDocsWrite("test"),
                new GDocsButtonClick(GDocs.Button.redo),
                new GDocsApplyModification(Selection.LineBefore, GDocs.Modification.italic),
                new GDocsInteractionTableAddColumn(),
                new GDocsWrite(Keys.UP),
                new GDocsWrite(" This"),
                new GDocsWrite("more text")
        ));

        int[] mw1 = {0, 0, 1, 1, 0, 2, 3, 0, 1, 0, 0};
        int[] mw2 = {0, 0, 0, 2, 0, 2, 0, 1, 0, 1, 2};
        runTest(mw1, mw2, first, second, new GDocsInitEmpty());
    }

    @org.testng.annotations.Test
    public void ex80_223() throws Exception {
        DebugDisplay debugDisplay = new DebugDisplay();

        GDocsFailsafeCombinator combinatorFirst = new GDocsFailsafeCombinator();
        combinatorFirst.setDebugAdapter(debugDisplay.first());

        GDocsFailsafeCombinator combinatorSecond = new GDocsFailsafeCombinator();
        combinatorSecond.setDebugAdapter(debugDisplay.second());

        Test first = combinatorFirst.combine(Lists.newArrayList(
                new GDocsWrite(Keys.RETURN),
                new GDocsApplyModification(Selection.WordAfter, GDocs.Modification.bold),
                new GDocsWrite("test"),
                new GDocsWrite("."),
                new GDocsApplyModification(Selection.WordAfter, GDocs.Modification.bold),
                new GDocsWrite("t"),
                new GDocsApplyModification(Selection.LineAfter, GDocs.Modification.underlined),
                new GDocsWrite(Keys.RETURN),
                new GDocsWrite("."),
                new GDocsDelete(Selection.LineAfter)
        ));
        Test second = combinatorSecond.combine(Lists.newArrayList(
                new GDocsApplyModification(Selection.LineBefore, GDocs.Modification.italic),
                new GDocsWrite("c"),
                new GDocsApplyModification(Selection.WordAfter, GDocs.Modification.italic),
                new GDocsWrite(" This"),
                new GDocsWrite(Keys.TAB),
                new GDocsWrite(" This"),
                new GDocsWrite(Keys.LEFT),
                new GDocsDelete(Selection.LineAfter),
                new GDocsApplyModification(Selection.WordAfter, GDocs.Modification.underlined),
                new GDocsWrite(Keys.UP)
        ));

        int[] mw1 = {0, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0};
        int[] mw2 = {1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1};
        runTest(mw1, mw2, first, second, new GDocsInitEmpty());
    }

    @org.testng.annotations.Test
    public void ex80_326() throws Exception {
        DebugDisplay debugDisplay = new DebugDisplay();

        GDocsFailsafeCombinator combinatorFirst = new GDocsFailsafeCombinator();
        combinatorFirst.setDebugAdapter(debugDisplay.first());

        GDocsFailsafeCombinator combinatorSecond = new GDocsFailsafeCombinator();
        combinatorSecond.setDebugAdapter(debugDisplay.second());

        Test first = combinatorFirst.combine(Lists.newArrayList(
                new GDocsWrite("c"),
                new GDocsWrite(Keys.BACK_SPACE, Keys.BACK_SPACE, Keys.BACK_SPACE),
                new GDocsWrite(Keys.HOME),
                new GDocsWrite(Keys.LEFT),
                new GDocsWrite(Keys.HOME),
                new GDocsApplyModification(Selection.LineBefore, GDocs.Modification.bold),
                new GDocsWrite(Keys.BACK_SPACE),
                new GDocsButtonClick(GDocs.Button.redo),
                new GDocsWrite("c"),
                new GDocsWrite("b")
        ));
        Test second = combinatorSecond.combine(Lists.newArrayList(
                new GDocsWrite("a"),
                new GDocsButtonClick(GDocs.Button.redo),
                new GDocsApplyModification(Selection.WordBefore, GDocs.Modification.bold),
                new GDocsApplyModification(Selection.LineBefore, GDocs.Modification.italic),
                new GDocsWrite(Keys.LEFT),
                new GDocsWrite(Keys.RIGHT),
                new GDocsWrite("text"),
                new GDocsButtonClick(GDocs.Button.redo),
                new GDocsApplyModification(Selection.WordBefore, GDocs.Modification.bold),
                new GDocsApplyModification(Selection.LineBefore, GDocs.Modification.underlined)
        ));

        int[] mw1 = {0, 0, 1, 1, 0, 0, 1, 0, 0, 0, 0};
        int[] mw2 = {0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0};
        runTest(mw1, mw2, first, second, new GDocsInitEmpty());
    }

    @org.testng.annotations.Test
    public void ex80_326_reduced() throws Exception {
        GDocsFailsafeCombinator combinator = new GDocsFailsafeCombinator();

        Test first = combinator.combine(Lists.newArrayList(
                new GDocsWrite("c"),
                new GDocsWrite(Keys.BACK_SPACE, Keys.BACK_SPACE, Keys.BACK_SPACE)
        ));
        Test second = combinator.combine(Lists.newArrayList(
                new GDocsWrite("a"),
                new GDocsButtonClick(GDocs.Button.redo)
        ));

        int[] mw1 = {1, 0, 1};
        int[] mw2 = {1, 0, 1};
        runTest(mw1, mw2, first, second, new GDocsInitEmpty());
    }

    @org.testng.annotations.Test
    public void ex80_383() throws Exception {
        DebugDisplay debugDisplay = new DebugDisplay();

        GDocsFailsafeCombinator combinatorFirst = new GDocsFailsafeCombinator();
        combinatorFirst.setDebugAdapter(debugDisplay.first());

        GDocsFailsafeCombinator combinatorSecond = new GDocsFailsafeCombinator();
        combinatorSecond.setDebugAdapter(debugDisplay.second());

        Test first = combinatorFirst.combine(Lists.newArrayList(
                new GDocsWrite(Keys.BACK_SPACE, Keys.BACK_SPACE, Keys.BACK_SPACE),
                new GDocsWrite("a"),
                new GDocsWrite("text", Keys.RETURN),
                new GDocsWrite(Keys.END),
                new GDocsApplyModification(Selection.LineAfter, GDocs.Modification.italic),
                new GDocsApplyModification(Selection.WordBefore, GDocs.Modification.bold),
                new GDocsButtonClick(GDocs.Button.redo),
                new GDocsWrite(Keys.HOME),
                new GDocsWrite("text", Keys.RETURN),
                new GDocsWrite(Keys.LEFT)
        ));
        Test second = combinatorSecond.combine(Lists.newArrayList(
                new GDocsWrite(Keys.RIGHT),
                new GDocsWrite("text", Keys.RETURN),
                new GDocsApplyModification(Selection.WordAfter, GDocs.Modification.bold),
                new GDocsWrite(Keys.UP),
                new GDocsWrite(Keys.RIGHT),
                new GDocsWrite(Keys.BACK_SPACE, Keys.BACK_SPACE, Keys.BACK_SPACE),
                new GDocsApplyModification(Selection.LineBefore, GDocs.Modification.underlined),
                new GDocsApplyModification(Selection.LineAfter, GDocs.Modification.bold),
                new GDocsWrite("a"),
                new GDocsWrite("c")
        ));

        int[] mw1 = {0, 0, 2, 0, 1, 0, 1, 0, 1, 2, 1};
        int[] mw2 = {1, 1, 1, 2, 1, 0, 0, 0, 1, 1, 0};
        runTest(mw1, mw2, first, second, new GDocsInitEmpty());
    }

    @org.testng.annotations.Test
    public void ex80_383_reduced() throws Exception {
        GDocsFailsafeCombinator combinator = new GDocsFailsafeCombinator();
        Test first = combinator.combine(Lists.newArrayList(
                new GDocsWrite("a"),
                new GDocsApplyModification(Selection.LineAfter, GDocs.Modification.italic),
                new GDocsWrite("text", Keys.RETURN)
        ));
        Test second = combinator.combine(Lists.newArrayList(
                new GDocsWrite(Keys.RIGHT),
                new GDocsWrite("text", Keys.RETURN)
        ));

        int[] mw1 = {1, 0, 2, 1};
        int[] mw2 = {2, 1, 1};
        runTest(mw1, mw2, first, second, new GDocsInitEmpty());
    }

    @org.testng.annotations.Test
    public void ex80_449() throws Exception {
        DebugDisplay debugDisplay = new DebugDisplay();

        GDocsFailsafeCombinator combinatorFirst = new GDocsFailsafeCombinator();
        combinatorFirst.setDebugAdapter(debugDisplay.first());

        GDocsFailsafeCombinator combinatorSecond = new GDocsFailsafeCombinator();
        combinatorSecond.setDebugAdapter(debugDisplay.second());

        Test first = combinatorFirst.combine(Lists.newArrayList(
                new GDocsWrite("t"),
                new GDocsDelete(Selection.WordBefore),
                new GDocsInteractionTableAddColumn(),
                new GDocsWrite(Keys.DOWN),
                new GDocsWrite(Keys.UP),
                new GDocsButtonClick(GDocs.Button.redo),
                new GDocsDelete(Selection.WordAfter),
                new GDocsWrite(Keys.HOME),
                new GDocsDelete(Selection.WordBefore),
                new GDocsWrite("b")
        ));
        Test second = combinatorSecond.combine(Lists.newArrayList(
                new GDocsWrite(Keys.HOME),
                new GDocsWrite("b"),
                new GDocsWrite(" "),
                new GDocsWrite(Keys.UP),
                new GDocsApplyModification(Selection.LineAfter, GDocs.Modification.italic),
                new GDocsApplyModification(Selection.WordAfter, GDocs.Modification.italic),
                new GDocsApplyModification(Selection.WordAfter, GDocs.Modification.bold),
                new GDocsWrite(" "),
                new GDocsWrite("t"),
                new GDocsWrite(Keys.END)
        ));

        int[] mw1 = {2, 1, 2, 0, 0, 1, 0, 0, 1, 0, 0};
        int[] mw2 = {0, 1, 0, 0, 1, 1, 3, 0, 0, 0, 1};
        runTest(mw1, mw2, first, second, new GDocsInitEmpty());
    }

    @org.testng.annotations.Test
    public void ex80_472() throws Exception {
        GDocsFailsafeCombinator combinator = new GDocsFailsafeCombinator();
        Test first = combinator.combine(Lists.newArrayList(
                new GDocsWrite("b"),
                new GDocsWrite(Keys.DOWN),
                new GDocsDelete(Selection.LineBefore),
                new GDocsApplyModification(Selection.WordBefore, GDocs.Modification.italic),
                new GDocsApplyModification(Selection.LineAfter, GDocs.Modification.italic),
                new GDocsWrite(Keys.RIGHT),
                new GDocsInteractionInsertTable(),
                new GDocsWrite("."),
                new GDocsWrite("c"),
                new GDocsApplyModification(Selection.LineBefore, GDocs.Modification.underlined)
        ));
        Test second = combinator.combine(Lists.newArrayList(
                new GDocsApplyModification(Selection.WordAfter, GDocs.Modification.italic),
                new GDocsDelete(Selection.LineBefore),
                new GDocsWrite("text", Keys.RETURN),
                new GDocsWrite(Keys.LEFT),
                new GDocsWrite("t"),
                new GDocsWrite(Keys.BACK_SPACE),
                new GDocsWrite(Keys.UP),
                new GDocsWrite(Keys.HOME),
                new GDocsWrite("test"),
                new GDocsApplyModification(Selection.WordBefore, GDocs.Modification.underlined)
        ));

        int[] mw1 = {0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1};
        int[] mw2 = {0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0};
        runTest(mw1, mw2, first, second, new GDocsInitEmpty());
    }

    @org.testng.annotations.Test
    public void ex80_512() throws Exception {
        GDocsFailsafeCombinator combinator = new GDocsFailsafeCombinator();
        Test first = combinator.combine(Lists.newArrayList(
                new GDocsApplyModification(Selection.WordAfter, GDocs.Modification.italic),
                new GDocsApplyModification(Selection.WordBefore, GDocs.Modification.underlined),
                new GDocsApplyModification(Selection.LineBefore, GDocs.Modification.italic),
                new GDocsWrite("text", Keys.RETURN),
                new GDocsWrite(Keys.DELETE),
                new GDocsApplyModification(Selection.LineBefore, GDocs.Modification.italic),
                new GDocsWrite(Keys.UP),
                new GDocsWrite("test"),
                new GDocsWrite("more text"),
                new GDocsApplyModification(Selection.WordAfter, GDocs.Modification.bold)
        ));

        Test second = combinator.combine(Lists.newArrayList(
                new GDocsWrite("c"),
                new GDocsWrite(Keys.RIGHT),
                new GDocsDelete(Selection.LineBefore),
                new GDocsWrite("."),
                new GDocsWrite("a"),
                new GDocsDelete(Selection.WordBefore),
                new GDocsDelete(Selection.WordBefore),
                new GDocsWrite("t"),
                new GDocsApplyModification(Selection.LineAfter, GDocs.Modification.italic),
                new GDocsWrite("z")
        ));

        int[] mw1 = {0, 0, 1, 1, 0, 1, 1, 1, 1, 2, 0};
        int[] mw2 = {0, 1, 1, 2, 1, 1, 0, 0, 0, 2, 0};
        runTest(mw1, mw2, first, second, new GDocsInitEmpty());
    }

    @org.testng.annotations.Test
    public void ex80_553() throws Exception {
        DebugDisplay debugDisplay = new DebugDisplay();

        GDocsFailsafeCombinator combinatorFirst = new GDocsFailsafeCombinator();
        combinatorFirst.setDebugAdapter(debugDisplay.first());

        GDocsFailsafeCombinator combinatorSecond = new GDocsFailsafeCombinator();
        combinatorSecond.setDebugAdapter(debugDisplay.second());

        Test first = combinatorFirst.combine(Lists.newArrayList(
                new GDocsWrite("test"),
                new GDocsApplyModification(Selection.LineAfter, GDocs.Modification.bold),
                new GDocsApplyModification(Selection.WordBefore, GDocs.Modification.underlined),
                new GDocsWrite("a"),
                new GDocsApplyModification(Selection.LineAfter, GDocs.Modification.italic),
                new GDocsWrite(Keys.UP),
                new GDocsApplyModification(Selection.LineAfter, GDocs.Modification.bold),
                new GDocsWrite(" This"),
                new GDocsWrite(Keys.RETURN),
                new GDocsWrite(Keys.DELETE)
        ));

        Test second = combinatorSecond.combine(Lists.newArrayList(
                new GDocsWrite("more text"),
                new GDocsApplyModification(Selection.WordAfter, GDocs.Modification.bold),
                new GDocsWrite("test"),
                new GDocsApplyModification(Selection.LineBefore, GDocs.Modification.underlined),
                new GDocsApplyModification(Selection.LineBefore, GDocs.Modification.italic),
                new GDocsButtonClick(GDocs.Button.undo),
                new GDocsWrite("t"),
                new GDocsApplyModification(Selection.WordBefore, GDocs.Modification.italic),
                new GDocsApplyModification(Selection.WordBefore, GDocs.Modification.italic),
                new GDocsWrite(Keys.TAB)
        ));

        int[] mw1 = {1, 0, 1, 0, 3, 2, 0, 1, 0, 1, 0};
        int[] mw2 = {0, 1, 0, 1, 0, 0, 2, 0, 3, 1, 1};
        runTest(mw1, mw2, first, second, new GDocsInitEmpty(), false);
    }


    @org.testng.annotations.Test
    public void ex80_553_paused() throws Exception {
        DebugDisplay debugDisplay = new DebugDisplay();

        GDocsFailsafeCombinator combinatorFirst = new GDocsFailsafeCombinator();
        combinatorFirst.setDebugAdapter(debugDisplay.first());

        GDocsFailsafeCombinator combinatorSecond = new GDocsFailsafeCombinator();
        combinatorSecond.setDebugAdapter(debugDisplay.second());

        Test first = combinatorFirst.combine(Lists.newArrayList(
                new GDocsWrite("test"),
                new GDocsApplyModification(Selection.LineAfter, GDocs.Modification.bold),
                new GDocsApplyModification(Selection.WordBefore, GDocs.Modification.underlined),
                new GDocsWrite("a"),
                new GDocsApplyModification(Selection.LineAfter, GDocs.Modification.italic),
                new GDocsWrite(Keys.UP),
                new GDocsApplyModification(Selection.LineAfter, GDocs.Modification.bold),
                new GDocsInteraction() {
                    @Override
                    public void perform() throws IOException, InterruptedException {
                        Thread.sleep(5000);
                    }
                },
                new GDocsWrite(" This"),
                new GDocsWrite(Keys.RETURN),
                new GDocsWrite(Keys.DELETE)
        ));

        Test second = combinatorSecond.combine(Lists.newArrayList(
                new GDocsWrite("more text"),
                new GDocsApplyModification(Selection.WordAfter, GDocs.Modification.bold),
                new GDocsWrite("test"),
                new GDocsApplyModification(Selection.LineBefore, GDocs.Modification.underlined),
                new GDocsApplyModification(Selection.LineBefore, GDocs.Modification.italic),
                new GDocsButtonClick(GDocs.Button.undo),
                new GDocsWrite("t"),
                new GDocsApplyModification(Selection.WordBefore, GDocs.Modification.italic),
                new GDocsApplyModification(Selection.WordBefore, GDocs.Modification.italic),
                new GDocsInteraction() {
                    @Override
                    public void perform() throws IOException, InterruptedException {
                        Thread.sleep(5000);
                    }
                },
                new GDocsWrite(Keys.TAB)
        ));

        int[] mw1 = {1, 0, 1, 0, 3, 2, 0, 1, 1, 0, 1, 0};
        int[] mw2 = {0, 1, 0, 1, 0, 0, 2, 0, 3, 1, 1, 1};
        runTest(mw1, mw2, first, second, new GDocsInitEmpty());
    }

    @org.testng.annotations.Test
    public void ex80_553_reduced() throws Exception {
        GDocsFailsafeCombinator combinator = new GDocsFailsafeCombinator();
        Test first = combinator.combine(Lists.newArrayList(
                new GDocsApplyModification(Selection.LineAfter, GDocs.Modification.bold),
                new GDocsWrite("T"),
                new GDocsWrite("text")
        ));
        Test second = combinator.combine(Lists.newArrayList(
                new GDocsWrite(Keys.END),
                new GDocsWrite(Keys.TAB)
        ));
        int[] mw1 = {1, 0, 1, 1};
        int[] mw2 = {1, 1, 1};
        runTest(mw1, mw2, first, second, new GDocsInitEmpty(), false);
    }

    @org.testng.annotations.Test
    public void ex80_578() throws Exception {
        DebugDisplay debugDisplay = new DebugDisplay();

        GDocsFailsafeCombinator combinatorFirst = new GDocsFailsafeCombinator();
        combinatorFirst.setDebugAdapter(debugDisplay.first());

        GDocsFailsafeCombinator combinatorSecond = new GDocsFailsafeCombinator();
        combinatorSecond.setDebugAdapter(debugDisplay.second());

        Test first = combinatorFirst.combine(Lists.newArrayList(
                new GDocsWrite(Keys.END),
                new GDocsApplyModification(Selection.LineBefore, GDocs.Modification.underlined),
                new GDocsApplyModification(Selection.LineAfter, GDocs.Modification.italic),
                new GDocsApplyModification(Selection.LineBefore, GDocs.Modification.bold),
                new GDocsWrite(" This"),
                new GDocsWrite("a"),
                new GDocsApplyModification(Selection.LineAfter, GDocs.Modification.italic),
                new GDocsApplyModification(Selection.LineAfter, GDocs.Modification.underlined),
                new GDocsApplyModification(Selection.WordAfter, GDocs.Modification.bold),
                new GDocsDelete(Selection.WordAfter)
        ));

        Test second = combinatorSecond.combine(Lists.newArrayList(
                new GDocsApplyModification(Selection.LineBefore, GDocs.Modification.underlined),
                new GDocsWrite(" "),
                new GDocsApplyModification(Selection.LineAfter, GDocs.Modification.bold),
                new GDocsWrite("."),
                new GDocsDelete(Selection.LineBefore),
                new GDocsWrite(Keys.LEFT),
                new GDocsApplyModification(Selection.LineBefore, GDocs.Modification.bold),
                new GDocsApplyModification(Selection.LineBefore, GDocs.Modification.italic),
                new GDocsApplyModification(Selection.LineBefore, GDocs.Modification.bold),
                new GDocsWrite(Keys.END)
        ));

        int[] mw1 = {1, 1, 1, 0, 1, 0, 1, 1, 1, 1, 1};
        int[] mw2 = {0, 1, 0, 1, 2, 2, 1, 1, 0, 0, 1};
        runTest(mw1, mw2, first, second, new GDocsInitEmpty());
    }

    @org.testng.annotations.Test
    public void ex80_578_reduced() throws Exception {
        GDocsFailsafeCombinator combinator = new GDocsFailsafeCombinator();
        Test first = combinator.combine(Lists.newArrayList(
                new GDocsWrite("Test"),
                new GDocsApplyModification(Selection.WordBefore, GDocs.Modification.bold),
                new GDocsWrite("text")
        ));
        Test second = combinator.combine(Lists.newArrayList(
                new GDocsWrite(Keys.END),
                new GDocsDelete(Selection.LineBefore)
        ));
        int[] mw1 = {1, 0, 2, 1};
        int[] mw2 = {2, 1, 1};
        runTest(mw1, mw2, first, second, new GDocsInitEmpty());
    }

    @org.testng.annotations.Test
    public void ex82_131() throws Exception {
        FirepadCombinator combinator = new FirepadCombinator(Firepad.url);
        Test first = combinator.combine(Lists.newArrayList(
                new FirepadApplyModification(Selection.WordBefore,
                        Firepad.Modification.strikethrough),
                new FirepadDelete(Selection.LineBefore),
                new FirepadMakeFontSize(Selection.LineBefore, "14"),
                new FirepadClickButton(Firepad.Button.checkList),
                new FirepadMakeFont(Selection.LineAfter, Firepad.Font.comicSans),
                new FirepadWrite("-------"),
                new FirepadWrite("."),
                new FirepadApplyModification(Selection.LineAfter, Firepad.Modification.italic),
                new FirepadMakeFontColor(Selection.WordBefore, "black"),
                new FirepadWrite("b")
        ));
        Test second = combinator.combine(Lists.newArrayList(
                new FirepadMakeFontSize(Selection.WordBefore, "12"),
                new FirepadMakeFont(Selection.LineAfter, Firepad.Font.timesNewRoman),
                new FirepadApplyModification(Selection.LineAfter, Firepad.Modification.underlined),
                new FirepadApplyModification(Selection.WordBefore, Firepad.Modification.underlined),
                new FirepadWrite("text"),
                new FirepadWrite(Keys.RIGHT),
                new FirepadClickButton(Firepad.Button.redo),
                new FirepadMakeFontColor(Selection.LineAfter, "blue"),
                new FirepadMakeFontColor(Selection.LineAfter, "red"),
                new FirepadMakeFontSize(Selection.LineBefore, "14")
        ));
        int[] mw1 = {0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1};
        int[] mw2 = {0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0};
        runTest(mw1, mw2, first, second, new FirepadInitEmpty());
    }

    @org.testng.annotations.Test
    public void ex82_131_reduced() throws Exception {
        FirepadCombinator combinator = new FirepadCombinator(Firepad.url);
        Test first = combinator.combine(Lists.newArrayList(
                new FirepadClickButton(Firepad.Button.checkList)
        ));
        Test second = combinator.combine(Lists.newArrayList(
                new FirepadWrite("text")
        ));
        int[] mw1 = {1, 1};
        int[] mw2 = {1, 1};
        runTest(mw1, mw2, first, second, new FirepadInitEmpty(), false);
    }

    @org.testng.annotations.Test
    public void ex82_131_reduced_changed() throws Exception {
        FirepadCombinator combinator = new FirepadCombinator(Firepad.url);
        Test first = combinator.combine(Lists.newArrayList(
                new FirepadClickButton(Firepad.Button.bulletList)
        ));
        Test second = combinator.combine(Lists.newArrayList(
                new FirepadWrite("a")
        ));
        int[] mw1 = {1, 1};
        int[] mw2 = {1, 1};
        runTest(mw1, mw2, first, second, new FirepadInitEmpty(), false);
    }

    @org.testng.annotations.Test
    public void ex82_223() throws Exception {
        FirepadCombinator combinator = new FirepadCombinator(Firepad.url);
        Test first = combinator.combine(Lists.newArrayList(
                new FirepadMakeFontSize(Selection.WordAfter, "12"),
                new FirepadApplyModification(Selection.WordAfter, Firepad.Modification.strikethrough),
                new FirepadWrite(Keys.TAB),
                new FirepadGotoHome(),
                new FirepadMakeFontColor(Selection.LineAfter, "red"),
                new FirepadWrite(Keys.DELETE),
                new FirepadApplyModification(Selection.LineAfter, Firepad.Modification.italic),
                new FirepadClickButton(Firepad.Button.redo),
                new FirepadWrite(Keys.END),
                new FirepadWrite(" ")
        ));
        Test second = combinator.combine(Lists.newArrayList(
                new FirepadWrite("b"),
                new FirepadMakeFontColor(Selection.LineAfter, "red"),
                new FirepadMakeFont(Selection.LineBefore, Firepad.Font.courierNew),
                new FirepadMakeFontSize(Selection.LineAfter, "14"),
                new FirepadMakeFontSize(Selection.WordAfter, "14"),
                new FirepadApplyModification(Selection.LineBefore, Firepad.Modification.italic),
                new FirepadMakeFont(Selection.LineAfter, Firepad.Font.comicSans),
                new FirepadWrite(Keys.UP),
                new FirepadInsertImage(),
                new FirepadDelete(Selection.WordBefore)
        ));
        int[] mw1 = {0, 0, 1, 0, 0, 2, 2, 1, 0, 2, 0};
        int[] mw2 = {1, 1, 3, 1, 0, 0, 0, 1, 1, 0, 0};
        runTest(mw1, mw2, first, second, new FirepadInitEmpty(), false);
    }

    /*
     * reduction doesn't reliably work. See full test case
     */
    @org.testng.annotations.Test
    public void ex82_223_reduced() throws Exception {
        FirepadCombinator combinator = new FirepadCombinator(Firepad.url);
        Test first = combinator.combine(Lists.newArrayList(
                new FirepadWrite(Keys.TAB)
        ));
        Test second = combinator.combine(Lists.newArrayList(
                new FirepadWrite("b")
        ));
        int[] mw1 = {1, 1};
        int[] mw2 = {1, 1};
        runTest(mw1, mw2, first, second, new FirepadInitEmpty(), false);
    }

    /*
     * false positive
     */
    @org.testng.annotations.Test
    public void ex82_257() throws Exception {
        FirepadCombinator combinator = new FirepadCombinator(Firepad.url);
        Test first = combinator.combine(Lists.newArrayList(
                new FirepadMakeFont(Selection.LineAfter, Firepad.Font.timesNewRoman),
                new FirepadMakeFontColor(Selection.WordBefore, "blue"),
                new FirepadClickButton(Firepad.Button.undo),
                new FirepadMakeFontSize(Selection.WordAfter, "14"),
                new FirepadMakeFontColor(Selection.LineAfter, "blue"),
                new FirepadWrite(Keys.END),
                new FirepadDelete(Selection.WordBefore),
                new FirepadWrite(Keys.BACK_SPACE, Keys.BACK_SPACE, Keys.BACK_SPACE),
                new FirepadMakeFont(Selection.LineBefore, Firepad.Font.arial),
                new FirepadApplyModification(Selection.LineBefore, Firepad.Modification.underlined)
        ));
        Test second = combinator.combine(Lists.newArrayList(
                new FirepadMakeFontColor(Selection.LineBefore, "black"),
                new FirepadWrite(" "),
                new FirepadWrite("-------"),
                new FirepadApplyModification(Selection.LineAfter, Firepad.Modification.strikethrough),
                new FirepadApplyModification(Selection.LineAfter, Firepad.Modification.bold),
                new FirepadInsertImage(),
                new FirepadMakeFontSize(Selection.WordBefore, "24"),
                new FirepadClickButton(Firepad.Button.redo),
                new FirepadMakeFont(Selection.WordAfter, Firepad.Font.courierNew),
                new FirepadMakeFontSize(Selection.LineAfter, "24")
        ));
        int[] mw1 = {0, 0, 3, 1, 2, 1, 0, 0, 1, 1, 0};
        int[] mw2 = {2, 1, 0, 0, 0, 4, 1, 0, 0, 1, 0};
        runTest(mw1, mw2, first, second, new FirepadInitEmpty(), false);
    }

    @org.testng.annotations.Test
    public void ex82_301() throws Exception {
        FirepadCombinator combinator = new FirepadCombinator(Firepad.url);
        Test first = combinator.combine(Lists.newArrayList(
                new FirepadApplyModification(Selection.WordAfter, Firepad.Modification.underlined),
                new FirepadWrite("t"),
                new FirepadMakeFont(Selection.WordBefore, Firepad.Font.comicSans),
                new FirepadMakeFontColor(Selection.WordAfter, "blue"),
                new FirepadMakeFontSize(Selection.WordBefore, "24"),
                new FirepadMakeFontColor(Selection.WordBefore, "black"),
                new FirepadMakeFontColor(Selection.LineBefore, "black"),
                new FirepadApplyModification(Selection.WordBefore, Firepad.Modification.underlined),
                new FirepadDelete(Selection.WordBefore),
                new FirepadMakeFontSize(Selection.WordBefore, "24")
        ));
        Test second = combinator.combine(Lists.newArrayList(
                new FirepadMakeFontSize(Selection.LineBefore, "14"),
                new FirepadWrite(Keys.DELETE),
                new FirepadClickButton(Firepad.Button.paragraphLeft),
                new FirepadMakeFontColor(Selection.WordBefore, "blue"),
                new FirepadMakeFont(Selection.LineAfter, Firepad.Font.arial),
                new FirepadClickButton(Firepad.Button.undo),
                new FirepadWrite("text", Keys.RETURN),
                new FirepadWrite(Keys.END),
                new FirepadMakeFontColor(Selection.LineAfter, "red"),
                new FirepadWrite(Keys.DOWN)
        ));
        int[] mw1 = {0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0};
        int[] mw2 = {0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1};
        runTest(mw1, mw2, first, second, new FirepadInitEmpty(), false);
    }

    @org.testng.annotations.Test
    public void ex82_301_reduced() throws Exception {
        FirepadCombinator combinator = new FirepadCombinator(Firepad.url);
        Test first = combinator.combine(Lists.newArrayList(
                new FirepadWrite("t")
        ));
        Test second = combinator.combine(Lists.newArrayList(
                new FirepadClickButton(Firepad.Button.paragraphLeft)
        ));
        int[] mw1 = {1, 1};
        int[] mw2 = {1, 1};
        runTest(mw1, mw2, first, second, new FirepadInitEmpty(), false);
    }

    @org.testng.annotations.Test
    public void ex82_355() throws Exception {
        FirepadCombinator combinator = new FirepadCombinator(Firepad.url);
        Test first = combinator.combine(Lists.newArrayList(
                new FirepadClickButton(Firepad.Button.decreaseIndent),
                new FirepadMakeFontColor(Selection.LineBefore, "red"),
                new FirepadWrite(" This"),
                new FirepadApplyModification(Selection.LineAfter, Firepad.Modification.underlined),
                new FirepadWrite("test"),
                new FirepadWrite(" "),
                new FirepadMakeFontColor(Selection.LineBefore, "red"),
                new FirepadWrite(Keys.DOWN),
                new FirepadMakeFont(Selection.LineBefore, Firepad.Font.comicSans),
                new FirepadWrite("c")
        ));
        Test second = combinator.combine(Lists.newArrayList(
                new FirepadWrite("z"),
                new FirepadMakeFontColor(Selection.WordAfter, "blue"),
                new FirepadMakeFontColor(Selection.LineBefore, "red"),
                new FirepadMakeFontColor(Selection.LineBefore, "black"),
                new FirepadApplyModification(Selection.LineBefore, Firepad.Modification.italic),
                new FirepadApplyModification(Selection.WordBefore, Firepad.Modification.bold),
                new FirepadMakeFontColor(Selection.WordBefore, "red"),
                new FirepadWrite("text", Keys.RETURN),
                new FirepadMakeFont(Selection.LineAfter, Firepad.Font.arial),
                new FirepadClickButton(Firepad.Button.numberedList)
        ));
        int[] mw1 = {0, 1, 0, 0, 0, 0, 1, 0, 0, 1, 0};
        int[] mw2 = {0, 0, 0, 1, 0, 0, 1, 1, 0, 0, 0};
        runTest(mw1, mw2, first, second, new FirepadInitEmpty(), false);
    }

    @org.testng.annotations.Test
    public void ex82_355_reduced() throws Exception {
        FirepadCombinator combinator = new FirepadCombinator(Firepad.url);
        Test first = combinator.combine(Lists.newArrayList(
                new FirepadClickButton(Firepad.Button.decreaseIndent)
        ));
        Test second = combinator.combine(Lists.newArrayList(
                new FirepadWrite("z")
        ));
        int[] mw1 = {1, 1};
        int[] mw2 = {1, 1};
        runTest(mw1, mw2, first, second, new FirepadInitEmpty(), false);
    }


    private void runTest(int[] mw1, int[] mw2, Test first, Test second, TestInit init)
            throws InterruptedException {
        runTest(mw1, mw2, first, second, init, true);
    }

    private void runTest(int[] mw1, int[] mw2, Test first, Test second, TestInit init,
                         boolean cleanup) throws InterruptedException {
        Assert.assertEquals(IntStream.of(mw1).sum(), IntStream.of(mw2).sum());

        ArrayDeque<MaybeWait> firstSchedule = Queues.newArrayDeque(
                Arrays.stream(mw1).boxed().map(MaybeWait::new).collect(Collectors.toList()));
        ArrayDeque<MaybeWait> secondSchedule = Queues.newArrayDeque(
                Arrays.stream(mw2).boxed().map(MaybeWait::new).collect(Collectors.toList()));

        MultiTest<Test> test = new MultiTest<>(first, second);
        ScheduleVariant<Test> variant = new ScheduleVariant<>(test, firstSchedule, secondSchedule);

        ScheduleVariantExecutor executor = new ScheduleVariantExecutor(Drivers.remoteDriver());
        if (!cleanup)
            executor.setCleanUp(false);
        if (init != null)
            executor.setInit(init);
        executor.scheduleAll(Lists.newArrayList(variant));
        executor.execute();

        if (!cleanup) {
            CyclicBarrier barrier = new CyclicBarrier(2);
            WaiterFrame frame = new WaiterFrame(barrier);
            frame.setVisible(true);
            Thread waiterThread = new FrameWaiter(barrier);
            waiterThread.start();
            waiterThread.join();
            first.cleanUp();
            second.cleanUp();
        }
    }
}
