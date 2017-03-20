package de.crispda.sola.multitester.util;

import de.crispda.sola.multitester.ImageDiff;
import de.crispda.sola.multitester.ImageDimensionException;
import de.crispda.sola.multitester.Images;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.stream.Collectors;

public class SequenceImageFrame extends JFrame {
    private JPanel transitionInnerPanel;
    private JPanel overlapInnerPanel;
    private List<List<ImageDiff>> imageDiffs;
    private List<byte[]> byteImages;
    private List<Image> images;
    private boolean overlapAsOverlay = false;
    private boolean transAsOverlay = true;
    private List<Rectangle> exclusionRectangles;

    public SequenceImageFrame(final List<List<ImageDiff>> imageDiffs, List<String> descriptions, CyclicBarrier barrier) {
        super("Image Sequence Viewer");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        this.imageDiffs = imageDiffs;

        JTabbedPane tabbedPane = setup(descriptions, barrier, 2);


        getContentPane().add(tabbedPane);
        pack();

    }

    public SequenceImageFrame(List<String> descriptions, CyclicBarrier barrier, final List<byte[]> images,
                              List<Rectangle> exclusionRectangles) {
        super("Image Sequence Viewer");
        this.byteImages = images;
        this.images = images.stream().map(i -> {
            ByteArrayInputStream stream = new ByteArrayInputStream(i);
            Image theImage = null;
            try {
                theImage = ImageIO.read(stream);
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return theImage;
        }).collect(Collectors.toList());
        this.exclusionRectangles = exclusionRectangles;
        JTabbedPane tabbedPane = setup(descriptions, barrier, 1);
        getContentPane().add(tabbedPane);
        pack();
    }

    private JTabbedPane setup(List<String> descriptions, CyclicBarrier barrier, int noCols) {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();

        // "Transitions" tab

        JPanel panelTransitions = new JPanel(new BorderLayout());
        String[] sequence = descriptions.toArray(new String[0]);
        JPanel transTopPanel = new JPanel(new BorderLayout());
        JList<String> sequenceList = new JList<>(sequence);
        sequenceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        transTopPanel.add(new JScrollPane(sequenceList));
        JPanel transButtonPanel = new JPanel();
        transButtonPanel.setLayout(new BoxLayout(transButtonPanel, BoxLayout.LINE_AXIS));
        JButton transOverlayBtn = new JButton("As overlay");
        transOverlayBtn.addActionListener(actionEvent -> {
            transAsOverlay = !transAsOverlay;
            updateInnerPanel(sequenceList.getSelectedIndex());
        });
        transButtonPanel.add(Box.createHorizontalGlue());
        transButtonPanel.add(transOverlayBtn);
        transButtonPanel.add(Box.createHorizontalGlue());
        transTopPanel.add(transButtonPanel, BorderLayout.SOUTH);
        panelTransitions.add(transTopPanel, BorderLayout.NORTH);

        transitionInnerPanel = new JPanel(new GridLayout(0, noCols));
        sequenceList.addListSelectionListener(listSelectionEvent ->
                updateInnerPanel(sequenceList.getSelectedIndex()));

        panelTransitions.add(transitionInnerPanel);
        updateInnerPanel(0);
        tabbedPane.add("Transitions", panelTransitions);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                try {
                    barrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }
        });

        sequenceList.setSelectedIndex(0);

        // "Overlap" tab

        JPanel panelOverlap = new JPanel(new BorderLayout());
        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel listsPanel = new JPanel(new GridLayout(0, 2));
        JList<String> listA = new JList<>(sequence);
        listsPanel.add(new JScrollPane(listA));
        JList<String> listB = new JList<>(sequence);
        listsPanel.add(new JScrollPane(listB));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
        buttonPanel.add(Box.createHorizontalGlue());
        JButton compareBtn = new JButton("Compare");
        compareBtn.addActionListener(actionEvent ->
                updateOverlap(listA.getSelectedIndex(), listB.getSelectedIndex()));
        buttonPanel.add(compareBtn);
        JButton overlayBtn = new JButton("As overlay");
        overlayBtn.addActionListener(actionEvent -> {
            overlapAsOverlay = !overlapAsOverlay;
            updateOverlap(listA.getSelectedIndex(), listB.getSelectedIndex());
        });
        buttonPanel.add(overlayBtn);
        buttonPanel.add(Box.createHorizontalGlue());

        topPanel.add(listsPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        panelOverlap.add(topPanel, BorderLayout.NORTH);

        overlapInnerPanel = new JPanel(new GridLayout(0, noCols));
        panelOverlap.add(overlapInnerPanel);
        tabbedPane.add("Overlap", panelOverlap);

        listA.setSelectedIndex(0);
        listB.setSelectedIndex(0);

        return tabbedPane;
    }

    private void updateInnerPanel(int index) {
        transitionInnerPanel.removeAll();
        if (imageDiffs != null) {
            List<ImageDiff> sequenceDiffs = imageDiffs.get(index);
            List<Image> sequenceIcons = sequenceDiffs.stream()
                    .map(diff -> {
                        Image returnValue;
                        try {
                            returnValue = diff.getImage(transAsOverlay);
                        } catch (UnsupportedOperationException e) {
                            returnValue = diff.getImage(false);
                        }
                        return returnValue;
                    }).collect(Collectors.toList());

            for (Image icon : sequenceIcons) {
                final JLabel label = new JLabel();
                label.setIcon(new ImageIcon(icon));
                label.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                transitionInnerPanel.add(label);
            }
        } else {
            Image theImage = images.get(index);
            JLabel label = new JLabel();
            label.setIcon(new ImageIcon(theImage));
            label.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            transitionInnerPanel.add(label);
        }
        transitionInnerPanel.revalidate();
        transitionInnerPanel.repaint();
    }

    private void updateOverlap(int indexA, int indexB) {
        overlapInnerPanel.removeAll();
        if (indexA == indexB) {
            overlapInnerPanel.add(new JLabel("Same transitions selected."));
        } else {
            if (imageDiffs != null) {
                List<ImageDiff> diffsA = imageDiffs.get(indexA);
                List<ImageDiff> diffsB = imageDiffs.get(indexB);
                try {
                    addOverlapImage(diffsA.get(0), diffsB.get(0));
                    if (diffsA.size() > 1 && diffsB.size() > 1)
                        addOverlapImage(diffsA.get(1), diffsB.get(1));
                } catch (ImageDimensionException e) {
                    overlapInnerPanel.add(new JLabel("Incompatible image dimensions!"));
                }
            } else {
                try {
                    ImageDiff diff = new ImageDiff(byteImages.get(indexA), byteImages.get(indexB), exclusionRectangles);
                    Image theImage = diff.getImage(overlapAsOverlay);
                    JLabel label = new JLabel();
                    label.setIcon(new ImageIcon(theImage));
                    label.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                    overlapInnerPanel.add(label);
                } catch (IOException | ImageDimensionException e) {
                    e.printStackTrace();
                    overlapInnerPanel.add(new JLabel("Exception!"));
                }
            }
        }
        overlapInnerPanel.revalidate();
        overlapInnerPanel.repaint();
    }

    private void addOverlapImage(final ImageDiff firstDiff, final ImageDiff secondDiff) throws
            ImageDimensionException {
        Optional<BufferedImage> overlapImage = Images.diffsOverlapImage(firstDiff, secondDiff, overlapAsOverlay);
        if (overlapImage.isPresent()) {
            final JLabel overlapImageLabel = new JLabel();
            overlapImageLabel.setIcon(new ImageIcon(overlapImage.get()));
            overlapImageLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            overlapInnerPanel.add(overlapImageLabel);
        } else {
            overlapInnerPanel.add(new JLabel("No overlap"));
        }
    }

    public static void view(List<List<ImageDiff>> imageDiffs, List<String> descriptions) throws InterruptedException {
        if (imageDiffs.isEmpty())
            return;
        CyclicBarrier barrier = new CyclicBarrier(2);
        SequenceImageFrame frame = new SequenceImageFrame(imageDiffs, descriptions, barrier);
        frame.setVisible(true);
        Thread waiterThread = new FrameWaiter(barrier);
        waiterThread.start();
        waiterThread.join();
    }

    public static void view(List<byte[]> images, List<String> descriptions, List<Rectangle> exclusionRectangles)
            throws InterruptedException {
        if (images.isEmpty())
            return;
        CyclicBarrier barrier = new CyclicBarrier(2);
        SequenceImageFrame frame = new SequenceImageFrame(descriptions, barrier, images, exclusionRectangles);
        frame.setVisible(true);
        Thread waiterThread = new FrameWaiter(barrier);
        waiterThread.start();
        waiterThread.join();
    }
}
