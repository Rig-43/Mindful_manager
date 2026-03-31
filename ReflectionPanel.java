package mindfulmanager.ui.panels;

import mindfulmanager.reflection.ReflectionEntry;
import mindfulmanager.reflection.ReflectionJournal;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.time.LocalDate;

/**
 * Daily reflection tab.
 */
public class ReflectionPanel extends JPanel {
    private final ReflectionJournal journal;

    private final JLabel dateLabel = new JLabel();
    private final JTextArea accomplishedArea = new JTextArea(6, 40);
    private final JTextArea feelingsArea = new JTextArea(6, 40);

    public ReflectionPanel(ReflectionJournal journal) {
        super(new BorderLayout(10, 10));
        this.journal = journal;
        setBorder(new EmptyBorder(12, 12, 12, 12));

        dateLabel.setText("Today's Reflection: " + LocalDate.now());

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        form.add(dateLabel, c);

        c.gridwidth = 1;
        c.gridy = 1;
        c.gridx = 0;
        form.add(new JLabel("What I accomplished:"), c);
        c.gridx = 1;
        JScrollPane accomplishedScroll = new JScrollPane(accomplishedArea);
        form.add(accomplishedScroll, c);

        c.gridx = 0;
        c.gridy = 2;
        form.add(new JLabel("How I feel:"), c);
        c.gridx = 1;
        JScrollPane feelingsScroll = new JScrollPane(feelingsArea);
        form.add(feelingsScroll, c);

        JButton saveBtn = new JButton("Save Reflection");
        saveBtn.addActionListener(e -> onSave());

        add(form, BorderLayout.CENTER);
        add(saveBtn, BorderLayout.SOUTH);

        // Load today's reflection if it exists.
        try {
            ReflectionEntry entry = journal.loadToday();
            if (entry != null) {
                accomplishedArea.setText(entry.getAccomplished());
                feelingsArea.setText(entry.getFeelings());
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Could not load today's reflection.\n" + ex.getMessage(),
                    "Load Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onSave() {
        try {
            journal.saveToday(accomplishedArea.getText(), feelingsArea.getText());
            JOptionPane.showMessageDialog(this,
                    "Reflection saved successfully.",
                    "Saved",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Could not save reflection.\n" + ex.getMessage(),
                    "Save Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}

