package mindfulmanager.ui.panels;

import mindfulmanager.mind.MindfulnessEngine;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

/**
 * Mindfulness tab: breathing exercises + quotes.
 */
public class MindfulnessPanel extends JPanel {
    private final MindfulnessEngine engine;

    private final JTextArea breathingArea = new JTextArea();
    private final JLabel quoteLabel = new JLabel("", SwingConstants.CENTER);

    public MindfulnessPanel(MindfulnessEngine engine) {
        super(new BorderLayout(10, 10));
        this.engine = engine;
        setBorder(new EmptyBorder(12, 12, 12, 12));

        breathingArea.setEditable(false);
        breathingArea.setLineWrap(true);
        breathingArea.setWrapStyleWord(true);

        JPanel top = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new java.awt.Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0;
        c.gridy = 0;
        top.add(new JLabel("Breathing Exercise:"), c);
        c.gridy = 1;
        breathingArea.setText(engine.getRandomBreathingExercise());
        JScrollPane breathingScroll = new JScrollPane(breathingArea);
        top.add(breathingScroll, c);

        c.gridy = 2;
        top.add(new JLabel("Quote:"), c);
        c.gridy = 3;
        quoteLabel.setText("<html><body style='text-align:center; width: 600px;'>" + engine.getRandomQuote() + "</body></html>");
        quoteLabel.setFont(quoteLabel.getFont().deriveFont(14f));
        top.add(quoteLabel, c);

        JPanel buttons = new JPanel();
        JButton newBreathingBtn = new JButton("New Breathing");
        JButton newQuoteBtn = new JButton("New Quote");
        JButton shuffleBtn = new JButton("Shuffle Both");

        newBreathingBtn.addActionListener(e -> breathingArea.setText(engine.getRandomBreathingExercise()));
        newQuoteBtn.addActionListener(e -> quoteLabel.setText("<html><body style='text-align:center; width: 600px;'>" + engine.getRandomQuote() + "</body></html>"));
        shuffleBtn.addActionListener(e -> {
            breathingArea.setText(engine.getRandomBreathingExercise());
            quoteLabel.setText("<html><body style='text-align:center; width: 600px;'>" + engine.getRandomQuote() + "</body></html>");
        });

        buttons.add(newBreathingBtn);
        buttons.add(newQuoteBtn);
        buttons.add(shuffleBtn);

        add(top, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
    }
}

