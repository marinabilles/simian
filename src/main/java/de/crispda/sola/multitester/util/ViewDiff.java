package de.crispda.sola.multitester.util;

import de.crispda.sola.multitester.ImageDiff;
import de.crispda.sola.multitester.Images;
import de.crispda.sola.multitester.runner.ExperimentSpec;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SerializationUtils;

import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ViewDiff {
    public static void main(String[] args) throws Exception {
        byte[] scr1 = FileUtils.readFileToByteArray(new File(args[0]));
        byte[] scr2 = FileUtils.readFileToByteArray(new File(args[1]));
        boolean overlay = true;
        if (args.length > 2 && Objects.equals(args[2], "-nooverlay")) {
            overlay = false;
        }

        List<Rectangle> exclusionRectangles = new ArrayList<>();
        if (args.length > 2 && args[2].endsWith(".spec")) {
            ExperimentSpec spec = SerializationUtils.deserialize(
                    FileUtils.readFileToByteArray(new File(args[2])));
            exclusionRectangles.addAll(spec.exclusionRectangles);
        }

        ImageDiff diff = Images.getDiff(scr1, scr2, exclusionRectangles);
        ImageFrame.showImage(diff.getImage(overlay));
    }
}
