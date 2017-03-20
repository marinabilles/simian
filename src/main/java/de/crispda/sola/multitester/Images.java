package de.crispda.sola.multitester;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

public class Images {
    public static Image getDiffImage(final byte[] beforeScr, final byte[] afterScr)
            throws IOException, ImageDimensionException {
        final ImageDiff diff = new ImageDiff(beforeScr, afterScr);
        return diff.getImage();
    }

    public static ImageDiff getDiff(final byte[] beforeScr, final byte[] afterScr)
            throws IOException, ImageDimensionException {
        return new ImageDiff(beforeScr, afterScr);
    }

    public static ImageDiff getDiff(final byte[] beforeScr, final byte[] afterScr, final List<Rectangle>
            exclusionRectangles) throws IOException, ImageDimensionException {
        return new ImageDiff(beforeScr, afterScr, exclusionRectangles);
    }

    public static boolean diffsOverlap(final ImageDiff first, final ImageDiff second) throws ImageDimensionException {
        return first.getOverlapImage(second, false).isPresent();
    }

    public static Optional<BufferedImage> diffsOverlapImage(final ImageDiff first, final ImageDiff second,
            boolean asOverlay) throws ImageDimensionException {
        return first.getOverlapImage(second, asOverlay);
    }

    public static BufferedImage toImage(byte[] beforeScr) {
        try {
            final InputStream stream = new ByteArrayInputStream(beforeScr);
            final BufferedImage image = ImageIO.read(stream);
            stream.close();
            return image;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
