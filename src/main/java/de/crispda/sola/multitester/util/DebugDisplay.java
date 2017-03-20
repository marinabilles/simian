package de.crispda.sola.multitester.util;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

public class DebugDisplay extends JFrame {
    private final JLabel label1;
    private final JLabel label2;
    public DebugDisplay() {
        super("Debug Display");
        setAlwaysOnTop(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JPanel panel = new JPanel(new GridLayout(0, 2));
        label1 = new JLabel();
        label1.setFont(new Font(label1.getFont().getName(), Font.PLAIN, 20));
        label1.setMinimumSize(new Dimension(400, 0));
        label2 = new JLabel();
        label2.setFont(new Font(label1.getFont().getName(), Font.PLAIN, 20));
        label2.setMinimumSize(new Dimension(400, 0));
        panel.add(label1);
        panel.add(label2);
        getContentPane().add(panel);
        setSize(new Dimension(800, 100));
        setVisible(true);
    }

    public LabelDebugAdapter first() {
        return new LabelDebugAdapter(label1, getContentPane());
    }

    public LabelDebugAdapter second() {
        return new LabelDebugAdapter(label2, getContentPane());
    }
}
