package de.crispda.sola.multitester.runner;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import de.crispda.sola.multitester.scenario.*;
import org.openqa.selenium.Keys;

import java.util.List;

class ExperimentSpecs {
    static List<ExperimentSpec> specList = Lists.newArrayList(
            /*
            new SetExperimentSpec(
                    Firepad.actionSet,
                    new FirepadCombinator(Firepad.url),
                    Firepad.exclusionRectangles,
                    new FirepadInitEmpty(),
                    "Firepad Set Test"
            ),
            new SetExperimentSpec(
                    Owncloud.actionSet,
                    new OwncloudCombinator(Owncloud.url),
                    new ArrayList<>(),
                    new OwncloudInitEmpty(),
                    "Owncloud Set Test"
            ),
            new SetExperimentSpec(
                    GDocs.actionSet,
                    new GDocsFailsafeCombinator(true),
                    GDocs.exclusionRectangles,
                    new GDocsInitEmpty(),
                    "GDocs Set Test"
            ),
            new SetExperimentSpec(
                    Sets.newHashSet(
                            new GDocsApplyModification(Selection.LineAfter, GDocs.Modification.bold),
                            new GDocsWrite("a"),
                            new GDocsWrite("b"),
                            new GDocsWrite(Keys.HOME),
                            new GDocsDelete(Selection.LineBefore)),
                    new GDocsFailsafeCombinator(true),
                    GDocs.exclusionRectangles,
                    new GDocsInitCompleteReset(),
                    "GDocs Reduced Test"
            ),
            new SetExperimentSpec(
                    Sets.newHashSet(
                            new GDocsApplyModification(Selection.LineAfter, GDocs.Modification.bold),
                            new GDocsApplyModification(Selection.LineBefore, GDocs.Modification.italic),
                            new GDocsWrite("a"),
                            new GDocsWrite("b"),
                            new GDocsWrite("c", Keys.RETURN),
                            new GDocsWrite(Keys.END),
                            new GDocsWrite(Keys.BACK_SPACE),
                            new GDocsDelete(Selection.LineBefore),
                            new GDocsButtonClick(GDocs.Button.undo),
                            new GDocsButtonClick(GDocs.Button.redo)),

                    new GDocsFailsafeCombinator(true),
                    GDocs.exclusionRectangles,
                    new GDocsInitCompleteReset(),
                    "GDocs Reduced Test 2"
            ),
            new SetExperimentSpec(
                    Sets.newHashSet(
                            new GDocsApplyModification(Selection.LineAfter, GDocs.Modification.bold),
                            new GDocsApplyModification(Selection.LineBefore, GDocs.Modification.italic),
                            new GDocsWrite("a"),
                            new GDocsWrite(Keys.TAB),
                            new GDocsWrite(Keys.HOME),
                            new GDocsWrite(Keys.BACK_SPACE),
                            new GDocsDelete(Selection.LineBefore),
                            new GDocsButtonClick(GDocs.Button.undo),
                            new GDocsButtonClick(GDocs.Button.redo)),
                    new GDocsFailsafeCombinator(true),
                    GDocs.exclusionRectangles,
                    new GDocsInitCompleteReset(),
                    new GDocsCursorSender(),
                    new GDocsCursorReceiver(),
                    "GDocs Reduced Test 3"
            ),
            new SetExperimentSpec(
                    Sets.newHashSet(
                            new FirepadApplyModification(Selection.LineAfter, Firepad.Modification.bold),
                            new FirepadApplyModification(Selection.LineBefore, Firepad.Modification.italic),
                            new FirepadWrite("a"),
                            new FirepadWrite(Keys.TAB),
                            new FirepadWrite(Keys.HOME),
                            new FirepadWrite(Keys.BACK_SPACE),
                            new FirepadDelete(Selection.LineBefore),
                            new FirepadClickButton(Firepad.Button.undo),
                            new FirepadClickButton(Firepad.Button.redo),
                            new FirepadClickButton(Firepad.Button.bulletList)),
                    new FirepadCombinator(Firepad.url),
                    Firepad.exclusionRectangles,
                    new FirepadInitEmpty(),
                    new FirepadCursorSender(),
                    new FirepadCursorReceiver(),
                    "Firepad Reduced Test"
            ),
            new SetExperimentSpec(
                    Sets.newHashSet(
                            new FirepadApplyModification(Selection.LineAfter, Firepad.Modification.bold),
                            new FirepadApplyModification(Selection.LineBefore, Firepad.Modification.italic),
                            new FirepadWrite("testing"),
                            new FirepadWrite(Keys.TAB),
                            new FirepadWrite(Keys.HOME),
                            new FirepadWrite(Keys.BACK_SPACE),
                            new FirepadDelete(Selection.LineBefore),
                            new FirepadClickButton(Firepad.Button.undo),
                            new FirepadClickButton(Firepad.Button.redo),
                            new FirepadClickButton(Firepad.Button.bulletList)),
                    new FirepadCombinator(Firepad.url),
                    Firepad.exclusionRectangles,
                    new FirepadInitEmpty(),
                    new FirepadCursorSender(),
                    new FirepadCursorReceiver(),
                    "Firepad Reduced Test 2"
            ),
            new SetExperimentSpec(
                    Sets.newHashSet(
                            new OwncloudApplyModification(Selection.LineAfter, Owncloud.Modification.bold),
                            new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.arial),
                            new OwncloudWrite("a"),
                            new OwncloudWrite(Keys.TAB),
                            new OwncloudWrite(Keys.HOME),
                            new OwncloudWrite(Keys.BACK_SPACE),
                            new OwncloudDelete(Selection.LineBefore),
                            new OwncloudIncreaseFontSize(Selection.LineBefore)
                    ),
                    new OwncloudCombinator(Owncloud.url),
                    Owncloud.exclusionRectangles,
                    new OwncloudInitEmpty(),
                    new OwncloudCursorSender(),
                    new OwncloudCursorReceiver(),
                    "Owncloud Reduced Test"
            ),
            new SetExperimentSpec(
                    Sets.newHashSet(
                            new OwncloudApplyModification(Selection.LineAfter, Owncloud.Modification.bold),
                            new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.timesNewRoman),
                            new OwncloudWrite("a"),
                            new OwncloudWrite(Keys.TAB),
                            new OwncloudWrite(Keys.HOME),
                            new OwncloudClickButton(Owncloud.Button.alignCenter),
                            new OwncloudDelete(Selection.LineBefore),
                            new OwncloudIncreaseFontSize(Selection.LineBefore)
                    ),
                    new OwncloudCombinator(Owncloud.url),
                    Owncloud.exclusionRectangles,
                    new OwncloudInitEmpty(),
                    new OwncloudCursorSender(),
                    new OwncloudCursorReceiver(),
                    "Owncloud Reduced Test 2"
            ),
            */
            new SetExperimentSpec(
                    Sets.newHashSet(
                            new GDocsWrite("a"),
                            new GDocsWrite(Keys.RETURN),
                            new GDocsApplyModification(Selection.LineBefore, GDocs.Modification.bold),
                            new GDocsDelete(Selection.LineBefore),
                            new GDocsMakeFont(Selection.LineBefore, GDocs.Font.verdana)
                    ),
                    new GDocsFailsafeCombinator(),
                    GDocs.exclusionRectangles,
                    new GDocsInitCompleteReset(),
                    new GDocsCursorSender(),
                    new GDocsCursorReceiver(),
                    "GDocs AS5"
            ),
            new SetExperimentSpec(
                    Sets.newHashSet(
                            new GDocsWrite("a"),
                            new GDocsWrite(Keys.TAB),
                            new GDocsWrite(Keys.RETURN),
                            new GDocsWrite(" "),
                            new GDocsApplyModification(Selection.LineBefore, GDocs.Modification.bold),
                            new GDocsApplyModification(Selection.LineAfter, GDocs.Modification.italic),
                            new GDocsMakeFontSize(Selection.LineBefore, "18"),
                            new GDocsDelete(Selection.LineBefore),
                            new GDocsMakeFont(Selection.LineBefore, GDocs.Font.verdana),
                            new GDocsWrite("b")
                    ),
                    new GDocsFailsafeCombinator(),
                    GDocs.exclusionRectangles,
                    new GDocsInitCompleteReset(),
                    new GDocsCursorSender(),
                    new GDocsCursorReceiver(),
                    "GDocs AS10"
            ),
            new SetExperimentSpec(
                    Sets.newHashSet(
                            new GDocsWrite("a"),
                            new GDocsWrite(Keys.TAB),
                            new GDocsWrite(Keys.RETURN),
                            new GDocsWrite(" "),
                            new GDocsApplyModification(Selection.LineBefore, GDocs.Modification.bold),
                            new GDocsApplyModification(Selection.LineAfter, GDocs.Modification.italic),
                            new GDocsMakeFontSize(Selection.LineBefore, "18"),
                            new GDocsDelete(Selection.LineBefore),
                            new GDocsMakeFont(Selection.LineBefore, GDocs.Font.verdana),
                            new GDocsWrite("b"),
                            new GDocsApplyModification(Selection.LineBefore, GDocs.Modification.underlined),
                            new GDocsWrite("c"),
                            new GDocsWrite(Keys.BACK_SPACE),
                            new GDocsMakeFont(Selection.LineBefore, GDocs.Font.arial),
                            new GDocsMakeFont(Selection.LineBefore, GDocs.Font.timesNewRoman)
                    ),
                    new GDocsFailsafeCombinator(),
                    GDocs.exclusionRectangles,
                    new GDocsInitCompleteReset(),
                    new GDocsCursorSender(),
                    new GDocsCursorReceiver(),
                    "GDocs AS15"
            ),
            new SetExperimentSpec(
                    Sets.newHashSet(
                            new FirepadWrite("a"),
                            new FirepadApplyModification(Selection.LineBefore, Firepad.Modification.bold),
                            new FirepadDelete(Selection.LineBefore),
                            new FirepadWrite(Keys.RETURN),
                            new FirepadMakeFont(Selection.LineBefore, Firepad.Font.verdana)
                    ),
                    new FirepadCombinator(Firepad.url),
                    Firepad.exclusionRectangles,
                    new FirepadInitEmpty(),
                    new FirepadCursorSender(),
                    new FirepadCursorReceiver(),
                    "Firepad AS5"
            ),
            new SetExperimentSpec(
                    Sets.newHashSet(
                            new FirepadWrite("a"),
                            new FirepadApplyModification(Selection.LineBefore, Firepad.Modification.bold),
                            new FirepadDelete(Selection.LineBefore),
                            new FirepadWrite(Keys.RETURN),
                            new FirepadMakeFont(Selection.LineBefore, Firepad.Font.verdana),
                            new FirepadWrite(Keys.TAB),
                            new FirepadWrite(" "),
                            new FirepadApplyModification(Selection.LineAfter, Firepad.Modification.italic),
                            new FirepadMakeFontSize(Selection.LineBefore, "18"),
                            new FirepadWrite("b")
                    ),
                    new FirepadCombinator(Firepad.url),
                    Firepad.exclusionRectangles,
                    new FirepadInitEmpty(),
                    new FirepadCursorSender(),
                    new FirepadCursorReceiver(),
                    "Firepad AS10"
            ),
            new SetExperimentSpec(
                    Sets.newHashSet(
                            new FirepadWrite("a"),
                            new FirepadApplyModification(Selection.LineBefore, Firepad.Modification.bold),
                            new FirepadDelete(Selection.LineBefore),
                            new FirepadWrite(Keys.RETURN),
                            new FirepadMakeFont(Selection.LineBefore, Firepad.Font.verdana),
                            new FirepadWrite(Keys.TAB),
                            new FirepadWrite(" "),
                            new FirepadApplyModification(Selection.LineAfter, Firepad.Modification.italic),
                            new FirepadMakeFontSize(Selection.LineBefore, "18"),
                            new FirepadWrite("b"),
                            new FirepadApplyModification(Selection.LineBefore, Firepad.Modification.underlined),
                            new FirepadWrite("c"),
                            new FirepadWrite(Keys.BACK_SPACE),
                            new FirepadMakeFont(Selection.LineBefore, Firepad.Font.arial),
                            new FirepadMakeFont(Selection.LineBefore, Firepad.Font.timesNewRoman)
                    ),
                    new FirepadCombinator(Firepad.url),
                    Firepad.exclusionRectangles,
                    new FirepadInitEmpty(),
                    new FirepadCursorSender(),
                    new FirepadCursorReceiver(),
                    "Firepad AS15"
            ),
            new SetExperimentSpec(
                    Sets.newHashSet(
                            new OwncloudApplyModification(Selection.LineBefore, Owncloud.Modification.bold),
                            new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana),
                            new OwncloudWrite("a"),
                            new OwncloudWrite(Keys.RETURN),
                            new OwncloudDelete(Selection.LineBefore)
                    ),
                    new OwncloudCombinator(Owncloud.url),
                    Owncloud.exclusionRectangles,
                    new OwncloudInitEmpty(),
                    new OwncloudCursorSender(),
                    new OwncloudCursorReceiver(),
                    "Owncloud AS5"
            ),
            new SetExperimentSpec(
                    Sets.newHashSet(
                            new OwncloudApplyModification(Selection.LineBefore, Owncloud.Modification.bold),
                            new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana),
                            new OwncloudWrite("a"),
                            new OwncloudWrite(Keys.RETURN),
                            new OwncloudDelete(Selection.LineBefore),
                            new OwncloudApplyModification(Selection.LineAfter, Owncloud.Modification.italic),
                            new OwncloudWrite(" "),
                            new OwncloudWrite("b"),
                            new OwncloudMakeFontSize18(Selection.LineBefore),
                            new OwncloudWrite(Keys.TAB)
                    ),
                    new OwncloudCombinator(Owncloud.url),
                    Owncloud.exclusionRectangles,
                    new OwncloudInitEmpty(),
                    new OwncloudCursorSender(),
                    new OwncloudCursorReceiver(),
                    "Owncloud AS10"
            ),
            new SetExperimentSpec(
                    Sets.newHashSet(
                            new OwncloudApplyModification(Selection.LineBefore, Owncloud.Modification.bold),
                            new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.verdana),
                            new OwncloudWrite("a"),
                            new OwncloudWrite(Keys.RETURN),
                            new OwncloudDelete(Selection.LineBefore),
                            new OwncloudApplyModification(Selection.LineAfter, Owncloud.Modification.italic),
                            new OwncloudWrite(" "),
                            new OwncloudWrite("b"),
                            new OwncloudMakeFontSize18(Selection.LineBefore),
                            new OwncloudWrite(Keys.TAB),
                            new OwncloudApplyModification(Selection.LineBefore, Owncloud.Modification.underlined),
                            new OwncloudWrite("c"),
                            new OwncloudWrite(Keys.BACK_SPACE),
                            new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.arial),
                            new OwncloudMakeFont(Selection.LineBefore, Owncloud.Font.timesNewRoman)
                    ),
                    new OwncloudCombinator(Owncloud.url),
                    Owncloud.exclusionRectangles,
                    new OwncloudInitEmpty(),
                    new OwncloudCursorSender(),
                    new OwncloudCursorReceiver(),
                    "Owncloud AS15"
            )
    );
}
