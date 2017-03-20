package de.crispda.sola.multitester.util;

import com.google.common.collect.Lists;
import de.crispda.sola.multitester.ImageDiff;
import de.crispda.sola.multitester.ImageDimensionException;
import de.crispda.sola.multitester.Images;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class ImageFrame extends JFrame {
    private ImageFrame(final List<ImageIcon> icons, CyclicBarrier barrier, String title) {
        super(title);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        final JPanel panel = new JPanel(new GridLayout(0, icons.size()));
        for (ImageIcon icon : icons) {
            final JLabel label = new JLabel();
            label.setIcon(icon);
            label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            panel.add(label);
        }
        getContentPane().add(panel);
        pack();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                setVisible(false);
                try {
                    barrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static void showIcons(List<ImageIcon> icons) throws InterruptedException {
        showIcons(icons, "Image Viewer");
    }

    private static void showIcons(List<ImageIcon> icons, String title) throws InterruptedException {
        final CyclicBarrier barrier = new CyclicBarrier(2);
        final ImageFrame frame = new ImageFrame(icons, barrier, title);
        frame.setVisible(true);
        Thread waiterThread = new FrameWaiter(barrier);
        waiterThread.start();
        waiterThread.join();
    }

    private static void showImage(final ImageIcon icon) throws InterruptedException {
        showIcons(Lists.newArrayList(icon));
    }

    private static void showImage(final ImageIcon icon, String title) throws InterruptedException {
        showIcons(Lists.newArrayList(icon), title);
    }

    public static void showImage(final Image image) throws InterruptedException {
        showImage(new ImageIcon(image));
    }

    public static void showImage(final Image image, String title) throws InterruptedException {
        showImage(new ImageIcon(image), title);
    }

    public static void showImage(final byte[] image) throws InterruptedException {
        showImage(new ImageIcon(image));
    }

    private static void showImages(final ImageIcon icon1, final ImageIcon icon2)
            throws InterruptedException {
        showIcons(Lists.newArrayList(icon1, icon2));
    }

    public static void showImages(final Image image1, final Image image2)
            throws InterruptedException {
        showImages(new ImageIcon(image1), new ImageIcon(image2));
    }

    public static void showImages(final byte[] image1, final byte[] image2)
            throws InterruptedException {
        showImages(new ImageIcon(image1), new ImageIcon(image2));
    }

    public static void showImages(final ImageDiff diff1, final ImageDiff diff2)
            throws InterruptedException, ImageDimensionException {
        final CyclicBarrier barrier = new CyclicBarrier(2);
        final ImageFrame frame = new ImageFrame(
                Lists.newArrayList(
                        new ImageIcon(diff1.getImage()),
                        new ImageIcon(diff2.getImage())), barrier, "Image Viewer");
        StringBuilder builder = new StringBuilder();
        builder.append(frame.getTitle());
        builder.append(" - Overlap: ");
        if (Images.diffsOverlap(diff1, diff2))
            builder.append("true");
        else
            builder.append("false");
        frame.setTitle(builder.toString());

        frame.setVisible(true);
        Thread waiterThread = new FrameWaiter(barrier);
        waiterThread.start();
        waiterThread.join();
    }
}
