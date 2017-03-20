package de.crispda.sola.multitester.util;

import javax.swing.JLabel;
import java.awt.Container;

public class LabelDebugAdapter implements DebugAdapter {
    private final JLabel label;
    private final Container contentPane;
    private final boolean hasThreadId;
    private int threadId;

    public LabelDebugAdapter(JLabel label, Container contentPane) {
        this.label = label;
        this.contentPane = contentPane;
        hasThreadId = false;
    }

    public LabelDebugAdapter(JLabel label, Container contentPane, int threadId) {
        this.label = label;
        this.contentPane = contentPane;
        hasThreadId = true;
        this.threadId = threadId;
    }

    @Override
    public void write(String text) {
        if (hasThreadId)
            text = String.format("[%d] %s", threadId, text);
        label.setText(text);
        contentPane.validate();
        contentPane.repaint();
    }
}
