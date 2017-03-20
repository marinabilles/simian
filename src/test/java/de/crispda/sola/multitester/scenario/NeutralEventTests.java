package de.crispda.sola.multitester.scenario;

import com.google.common.collect.Lists;
import de.crispda.sola.multitester.DiffExecutor;
import de.crispda.sola.multitester.ImageDiff;
import de.crispda.sola.multitester.Test;
import de.crispda.sola.multitester.scenario.neutral.*;
import de.crispda.sola.multitester.util.ImageFrame;
import de.crispda.sola.multitester.web.Drivers;
import de.crispda.sola.multitester.web.Firefox;
import org.testng.Assert;

import java.awt.image.BufferedImage;
import java.util.List;

public class NeutralEventTests {
    @org.testng.annotations.Test
    public void boldButtonUndoTest() throws Exception {
        Test bbu = GDocsInteractions.create(Lists.newArrayList(new GDocsInteractionBoldButtonUndo()));
        DiffExecutor ex = new DiffExecutor(Drivers.firefoxDriver(Firefox.ESR));
        ex.setExclusionRectangles(GDocs.exclusionRectangles);
        ex.scheduleAll(Lists.newArrayList(bbu));
        ex.execute();

        List<ImageDiff> diffs = ex.getImageDiffs();
        Assert.assertEquals(diffs.size(), 1);

        List<BufferedImage> images = ex.getImages();
        ImageFrame.showImages(images.get(0), images.get(1));

        ImageDiff diff = diffs.get(0);
        ImageFrame.showImage(diff.getImage());
        // Assert.assertFalse(diff.hasDifference());
    }

    @org.testng.annotations.Test
    public void undoRedoTest() throws Exception {
        Test ur = GDocsInteractions.create(Lists.newArrayList(new GDocsInteractionUndoRedo()));
        runNeutralityTest(ur);
    }

    @org.testng.annotations.Test
    public void openCloseTest() throws Exception {
        Test test = GDocsInteractions.create(Lists.newArrayList(new GDocsInteractionOpenCloseMenu()));
        runNeutralityTest(test);
    }

    @org.testng.annotations.Test
    public void increaseDecreaseIndentTest() throws Exception {
        Test test = GDocsInteractions.create(Lists.newArrayList(new GDocsInteractionIncreaseDecreaseIndent()));
        runNeutralityTest(test);
    }

    @org.testng.annotations.Test
    public void addCommentCancelTest() throws Exception {
        Test test = GDocsInteractions.create(Lists.newArrayList(new GDocsInteractionAddCommentCancel()));
        runNeutralityTest(test);
    }

    @org.testng.annotations.Test
    public void hideShowMenusTest() throws Exception {
        Test test = GDocsInteractions.create(Lists.newArrayList(new GDocsInteractionHideShowMenus()));
        runNeutralityTest(test);
    }

    private void runNeutralityTest(Test test) throws InterruptedException {
        DiffExecutor ex = new DiffExecutor(Drivers.firefoxDriver(Firefox.ESR));
        ex.setExclusionRectangles(GDocs.exclusionRectangles);
        ex.setInit(new GDocsInitLines());
        ex.scheduleAll(Lists.newArrayList(test));
        ex.execute();

        List<ImageDiff> diffs = ex.getImageDiffs();
        Assert.assertEquals(diffs.size(), 1);

        List<BufferedImage> images = ex.getImages();
        ImageFrame.showImages(images.get(0), images.get(1));

        ImageDiff diff = diffs.get(0);
        System.out.println("hasDifference: " + diff.hasDifference());
        ImageFrame.showImage(diff.getImage());
    }
}
