package de.crispda.sola.multitester;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.display.screenimage.awt.ARGBScreenImage;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.IntegerType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import org.openqa.selenium.Dimension;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class ImageDiff implements Serializable {
    private static final long serialVersionUID = 1L;
    private final int width;
    private final int height;
    private int secondWidth = 0;
    private int secondHeight = 0;
    private transient Img<ARGBType> beforeImg;
    private transient Img<ARGBType> afterImg;
    private transient Img<UnsignedByteType> diff;
    private final List<Rectangle> excludingRectangles;

    private static final int LabelNotPresent = 0;
    private static final int LabelPresent = 0x01;
    private static final int LabelStep = 0x01;
    private static final int PixelThreshold = 10;
    private static final int HighlightColor = Color.ORANGE.getRGB();
    private static final int DoubleHighlightColor = Color.CYAN.getRGB();
    private static final int BorderColor = new Color(0x87, 0xCE, 0xEB, 0xFF).getRGB();
    private static final int ExcludedColor = Color.LIGHT_GRAY.getRGB();
    public static boolean saveImage = false;

    private static class LongPoint {

        final long x;
        final long y;
        LongPoint(long x, long y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            LongPoint longPoint = (LongPoint) o;
            return x == longPoint.x &&
                    y == longPoint.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }

    }
    public ImageDiff(final byte[] beforeScr, final byte[] afterScr) throws IOException, ImageDimensionException {
        this(beforeScr, afterScr, new ArrayList<>());
    }

    public ImageDiff(final byte[] beforeScr, final byte[] afterScr, final List<Rectangle> excludingRectangles)
            throws IOException, ImageDimensionException {
        this.excludingRectangles = excludingRectangles;

        // Load Imgs
        InputStream beforeIS = new ByteArrayInputStream(beforeScr);
        BufferedImage beforeBI = ImageIO.read(beforeIS);
        beforeIS.close();
        if (beforeBI == null)
            throw new IOException("Unsupported image file type for 'before'");
        width = beforeBI.getWidth();
        height = beforeBI.getHeight();

        beforeImg = new ARGBScreenImage(width, height,
                beforeBI.getRGB(0, 0, width, height, null, 0, width));

        InputStream afterIS = new ByteArrayInputStream(afterScr);
        BufferedImage afterBI = ImageIO.read(afterIS);
        afterIS.close();
        if (afterBI == null)
            throw new IOException("Unsupported image file type for 'after'");
        if (afterBI.getWidth() != width || afterBI.getHeight() != height)
            throw new ImageDimensionException(new Dimension(width, height),
                    new Dimension(afterBI.getWidth(), afterBI.getHeight()));

        Img<ARGBType> afterImg = new ARGBScreenImage(width, height,
                afterBI.getRGB(0, 0, width, height, null, 0 , width));

        diff = compare(beforeImg, afterImg);
    }

    public ImageDiff(Img<ARGBType> beforeImg, Img<ARGBType> afterImg) {
        this(beforeImg, afterImg, new ArrayList<>());
    }

    public ImageDiff(Img<ARGBType> beforeImg, Img<ARGBType> afterImg, List<Rectangle> excludingRectangles) {
        this.excludingRectangles = excludingRectangles;

        if (beforeImg.numDimensions() != 2 || afterImg.numDimensions() != 2) {
            throw new UnsupportedOperationException("Images not two-dimensional!");
        }
        width = Math.toIntExact(beforeImg.dimension(0));
        height = Math.toIntExact(beforeImg.dimension(1));
        secondWidth = Math.toIntExact(afterImg.dimension(0));
        secondHeight = Math.toIntExact(afterImg.dimension(1));
        if (saveImage) {
            this.beforeImg = beforeImg;
            this.afterImg = afterImg;
        }

        if (width != secondWidth || height != secondHeight) {
            diff = null;
        } else {
            diff = compare(beforeImg, afterImg);
        }
    }

    private Img<UnsignedByteType> compare(Img<ARGBType> beforeImg, Img<ARGBType> afterImg) {
        ImgFactory<UnsignedByteType> factory = new ArrayImgFactory<>();
        Img<UnsignedByteType> diff = factory.create(new long[] {width, height}, new UnsignedByteType());
        RandomAccess<ARGBType> raBefore = beforeImg.randomAccess();
        RandomAccess<ARGBType> raAfter = afterImg.randomAccess();
        RandomAccess<UnsignedByteType> raDiff = diff.randomAccess();
        long[] pos = new long[2];
        Cursor<UnsignedByteType> cursor = diff.localizingCursor();
        while (cursor.hasNext()) {
            cursor.fwd();
            cursor.localize(pos);
            raBefore.setPosition(pos);
            raAfter.setPosition(pos);
            raDiff.setPosition(pos);

            if (excludingRectangles.stream().anyMatch(rect -> isInRectangle(pos, rect))) {
                raDiff.get().set(LabelNotPresent);
            } else if (raBefore.get().get() != raAfter.get().get()) {
                raDiff.get().set(LabelPresent);
            } else {
                raDiff.get().set(LabelNotPresent);
            }
        }

        // Find and exclude small areas (< PixelThreshold) from the Diff
        Map<Integer, Integer> labelAreas = getLabelAreas(diff, LabelPresent, LabelStep);
        Set<Integer> labelsToEliminate = labelAreas.entrySet().stream()
                .filter(labelArea -> labelArea.getValue() < PixelThreshold)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        cursor.reset();
        while (cursor.hasNext()) {
            cursor.fwd();
            cursor.localize(pos);
            raDiff.setPosition(pos);
            int value = raDiff.get().get();
            if (labelsToEliminate.contains(value)) {
                raDiff.get().set(LabelNotPresent);
            } else if (value != LabelNotPresent && value != LabelPresent) {
                raDiff.get().set(LabelPresent);
            }
        }

        return diff;
    }

    private static boolean isInRectangle(long[] position, Rectangle rect) {
        long x = position[0];
        long y = position[1];
        return x >= rect.x && x < rect.x + rect.width &&
                y >= rect.y && y < rect.y + rect.height;
    }

    /*
        8-neighbor flood fill algorithm to find connected areas (breadth-first)
     */
    private static <T extends IntegerType<T> & NativeType<T>> Map<Integer, Integer> getLabelAreas(
            Img<T> img, long present, long step) {
        RandomAccess<T> ra = img.randomAccess();
        Cursor<T> cursor = img.localizingCursor();
        long[] pos = new long[2];
        long width = img.dimension(0);
        long height = img.dimension(1);
        int label = LabelPresent;
        Map<Integer, Integer> labelAreas = new HashMap<>();

        while (cursor.hasNext()) {
            cursor.fwd();
            cursor.localize(pos);
            ra.setPosition(pos);

            if (ra.get().getIntegerLong() == present) {
                label += step;

                int labelArea = 0;

                Queue<LongPoint> pointQueue = new LinkedList<>();
                pointQueue.add(new LongPoint(pos[0], pos[1]));
                while (!pointQueue.isEmpty()) {
                    LongPoint p = pointQueue.remove();
                    if (p.x >= 0 && p.x < width && p.y >= 0 && p.y < height) {
                        ra.setPosition(new long[] {p.x, p.y});
                        if (ra.get().getIntegerLong() == present) {
                            ra.get().setInteger(label);
                            labelArea++;
                            addNeighbors(pointQueue, p);
                        }
                    }
                }

                labelAreas.put(label, labelArea);
            }
        }

        return labelAreas;
    }

    private static void addNeighbors(Queue<LongPoint> pointQueue, LongPoint p) {
        pointQueue.add(new LongPoint(p.x + 1, p.y));
        pointQueue.add(new LongPoint(p.x, p.y + 1));
        pointQueue.add(new LongPoint(p.x, p.y - 1));
        pointQueue.add(new LongPoint(p.x - 1, p.y));
        pointQueue.add(new LongPoint(p.x + 1, p.y + 1));
        pointQueue.add(new LongPoint(p.x - 1, p.y + 1));
        pointQueue.add(new LongPoint(p.x + 1, p.y - 1));
        pointQueue.add(new LongPoint(p.x - 1, p.y - 1));
    }

    public BufferedImage getImage() {
        return getImage(true);
    }

    public BufferedImage getImage(boolean asOverlay) {
        if (diff == null)
            throw new UnsupportedOperationException(
                    new ImageDimensionException(new Dimension(width, height),
                            new Dimension(secondWidth, secondHeight)));
        ARGBScreenImage outputImg = new ARGBScreenImage(width, height);
        long[] pos = new long[2];
        RandomAccess<UnsignedByteType> raDiff = diff.randomAccess();
        RandomAccess<ARGBType> raBefore = null;
        if (asOverlay) {
            if (beforeImg == null)
                throw new UnsupportedOperationException("No image saved in this diff");
            raBefore = beforeImg.randomAccess();
        }
        RandomAccess<ARGBType> raOutput = outputImg.randomAccess();
        Cursor<ARGBType> cursor = outputImg.localizingCursor();

        while (cursor.hasNext()) {
            cursor.fwd();
            cursor.localize(pos);
            raDiff.setPosition(pos);
            raOutput.setPosition(pos);
            if (excludingRectangles.stream().anyMatch(rect -> isInRectangle(pos, rect))) {
                raOutput.get().set(ExcludedColor);
            } else if (raDiff.get().get() == LabelPresent) {
                raOutput.get().set(HighlightColor);
            } else if (asOverlay) {
                raBefore.setPosition(pos);
                raOutput.get().set(multiply(0.8f, raBefore.get().get()));
            } else {
                raOutput.get().set(Color.BLACK.getRGB());
            }
        }

        return outputImg.image();
    }

    public Optional<BufferedImage> getOverlapImage(final ImageDiff otherDiff, boolean asOverlay)
            throws ImageDimensionException {
        if (width != otherDiff.width || height != otherDiff.height)
            throw new ImageDimensionException(new Dimension(width, height),
                    new Dimension(otherDiff.width, otherDiff.height));
        if (diff == null)
            throw new UnsupportedOperationException(new ImageDimensionException(
                    new Dimension(width, height), new Dimension(secondWidth, secondHeight)));
        if (otherDiff.diff == null)
            throw new UnsupportedOperationException(new ImageDimensionException(
                    new Dimension(otherDiff.width, otherDiff.height),
                    new Dimension(otherDiff.secondWidth, otherDiff.secondHeight)));

        ARGBScreenImage outputImg = new ARGBScreenImage(width, height);
        RandomAccess<UnsignedByteType> raThisDiff = diff.randomAccess();
        RandomAccess<UnsignedByteType> raOtherDiff = otherDiff.diff.randomAccess();
        RandomAccess<ARGBType> raBefore = null;
        if (asOverlay) {
            if (beforeImg == null)
                throw new UnsupportedOperationException("No image saved in this diff");
            raBefore = beforeImg.randomAccess();
        }
        RandomAccess<ARGBType> raOutput = outputImg.randomAccess();
        long[] pos = new long[2];
        Cursor<ARGBType> cursor = outputImg.localizingCursor();

        boolean overlap = false;
        while (cursor.hasNext()) {
            cursor.fwd();
            cursor.localize(pos);
            raThisDiff.setPosition(pos);
            raOtherDiff.setPosition(pos);
            raOutput.setPosition(pos);
            if (excludingRectangles.stream().anyMatch(rect -> isInRectangle(pos, rect))) {
                raOutput.get().set(ExcludedColor);
            } else if (raThisDiff.get().get() == LabelPresent &&
                    raOtherDiff.get().get() == LabelPresent) {
                overlap = true;
                raOutput.get().set(DoubleHighlightColor);
            } else if (raThisDiff.get().get() == LabelPresent ||
                    raOtherDiff.get().get() == LabelPresent) {
                raOutput.get().set(HighlightColor);
            } else if (asOverlay) {
                raBefore.setPosition(pos);
                raOutput.get().set(multiply(.8f, raBefore.get().get()));
            } else {
                raOutput.get().set(Color.BLACK.getRGB());
            }
        }

        cursor.reset();
        while (cursor.hasNext()) {
            cursor.fwd();
            cursor.localize(pos);
            raOutput.setPosition(pos);

            if (raOutput.get().get() == DoubleHighlightColor) {
                Queue<LongPoint> queue = new LinkedList<>();
                Set<LongPoint> done = new HashSet<>();
                queue.add(new LongPoint(pos[0], pos[1]));
                while (!queue.isEmpty()) {
                    LongPoint p = queue.remove();
                    if (p.x >= 0 && p.x < width && p.y >= 0 && p.y < height) {
                        raOutput.setPosition(new long[] {p.x, p.y});
                        if (raOutput.get().get() == DoubleHighlightColor) {
                            if (!done.contains(p)) {
                                done.add(p);
                                addNeighbors(queue, p);
                            }
                        } else {
                            raOutput.get().set(BorderColor);
                        }
                    }
                }
            }
        }

        if (!overlap)
            return Optional.empty();
        else
            return Optional.of(outputImg.image());
    }

    public boolean overlapsWith(ImageDiff otherDiff) {
        if (diff == null || otherDiff.diff == null || width != otherDiff.width || height != otherDiff.height)
            return true;

        RandomAccess<UnsignedByteType> raThisDiff = diff.randomAccess();
        RandomAccess<UnsignedByteType> raOtherDiff = otherDiff.diff.randomAccess();
        long[] pos = new long[2];
        Cursor<UnsignedByteType> cursor = diff.localizingCursor();
        while (cursor.hasNext()) {
            cursor.fwd();
            cursor.localize(pos);
            raThisDiff.setPosition(pos);
            raOtherDiff.setPosition(pos);
            if (raThisDiff.get().get() == LabelPresent &&
                    raOtherDiff.get().get() == LabelPresent)
                return true;
        }

        return false;
    }

    private static int multiply(float factor, int argb) {
        Color c = new Color(argb);
        int newRed = Math.round(c.getRed() * factor);
        int newGreen = Math.round(c.getGreen() * factor);
        int newBlue = Math.round(c.getBlue() * factor);
        return new Color(newRed, newGreen, newBlue, c.getAlpha()).getRGB();
    }

    public boolean hasDifference() {
        if (diff == null)
            throw new UnsupportedOperationException(new ImageDimensionException(
                    new Dimension(width, height), new Dimension(secondWidth, secondHeight)));
        long[] pos = new long[2];
        RandomAccess<UnsignedByteType> raDiff = diff.randomAccess();
        Cursor<UnsignedByteType> cursor = diff.localizingCursor();
        while (cursor.hasNext()) {
            cursor.fwd();
            cursor.localize(pos);
            raDiff.setPosition(pos);

            if (raDiff.get().get() == LabelPresent)
                return true;
        }

        return false;
    }

    public List<Optional<Integer>> getOverlappingPixels(ImageDiff second) {
        Optional<Integer> myPixelOp = Optional.empty();
        Optional<Integer> secondPixelOp = Optional.empty();
        int overlappingPixels = 0;
        if (diff != null) {
            int myPixels = 0;
            if (second != null && second.diff != null) {
                int secondPixels = 0;
                Cursor<UnsignedByteType> cursor = diff.localizingCursor();
                RandomAccess<UnsignedByteType> myRA = diff.randomAccess();
                RandomAccess<UnsignedByteType> otherRA = second.diff.randomAccess();
                long[] pos = new long[2];
                while (cursor.hasNext()) {
                    cursor.fwd();
                    cursor.localize(pos);
                    myRA.setPosition(pos);
                    otherRA.setPosition(pos);
                    if (myRA.get().get() == LabelPresent) {
                        if (otherRA.get().get() == LabelPresent) {
                            overlappingPixels++;
                        } else {
                            myPixels++;
                        }
                    } else if (otherRA.get().get() == LabelPresent) {
                        secondPixels++;
                    }
                }

                secondPixelOp = Optional.of(secondPixels);
            } else {
                Cursor<UnsignedByteType> cursor = diff.cursor();
                while (cursor.hasNext()) {
                    cursor.fwd();
                    if (cursor.get().get() == LabelPresent)
                        myPixels++;
                }
            }

            myPixelOp = Optional.of(myPixels);
        } else if (second != null && second.diff != null) {
            int secondPixels = 0;
            Cursor<UnsignedByteType> cursor = second.diff.cursor();
            while (cursor.hasNext()) {
                cursor.fwd();
                if (cursor.get().get() == LabelPresent)
                    secondPixels++;
            }

            secondPixelOp = Optional.of(secondPixels);
        }

        List<Optional<Integer>> returnValue = new ArrayList<>();
        returnValue.add(myPixelOp);
        returnValue.add(secondPixelOp);
        returnValue.add(Optional.of(overlappingPixels));

        return returnValue;
    }

    public Img<ARGBType> getBeforeImg() {
        return beforeImg;
    }

    public Img<ARGBType> getAfterImg() {
        return afterImg;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        if (diff == null) {
            out.writeBoolean(false);
        } else {
            out.writeBoolean(true);
            out.writeInt(diff.numDimensions());
            long[] dimensions = new long[diff.numDimensions()];
            diff.dimensions(dimensions);
            for (long dim : dimensions) {
                out.writeLong(dim);
            }

            Cursor<UnsignedByteType> cursor = diff.cursor();
            while (cursor.hasNext()) {
                cursor.fwd();
                out.writeByte(cursor.get().get());
            }
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        boolean hasDiff = in.readBoolean();
        if (!hasDiff) {
            diff = null;
        } else {
            long[] dimensions = new long[in.readInt()];
            for (int i = 0; i < dimensions.length; i++) {
                dimensions[i] = in.readLong();
            }

            ImgFactory<UnsignedByteType> factory = new ArrayImgFactory<>();
            diff = factory.create(dimensions, new UnsignedByteType());
            int totalSize = Math.toIntExact(diff.size());

            Cursor<UnsignedByteType> cursor = diff.cursor();
            byte[] buffer = new byte[Math.min(totalSize, 8192)];
            int cursorIndex = 0;
            while (cursorIndex < totalSize) {
                int readBytes = in.read(buffer, 0, Math.min(buffer.length, totalSize - cursorIndex));
                for (int i = 0; cursor.hasNext() && i < readBytes; i++) {
                    cursor.fwd();
                    cursor.get().set(buffer[i]);
                }

                if (readBytes < 0)
                    break;
                cursorIndex += readBytes;
            }
        }
    }
}
