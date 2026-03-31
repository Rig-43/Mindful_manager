package mindfulmanager.ui.panels;

import mindfulmanager.timer.PomodoroTimer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

/**
 * Focus Mode tab: Pomodoro timer with notifications.
 */
public class FocusPanel extends JPanel implements PomodoroTimer.PomodoroListener {
    private final PomodoroTimer pomodoroTimer;

    private final JLabel phaseLabel = new JLabel("Ready");
    private final JLabel countdownLabel = new JLabel(PomodoroTimer.formatSeconds(25 * 60), SwingConstants.CENTER);

    private final JButton startBtn = new JButton("Start Work");
    private final JButton pauseBtn = new JButton("Pause");
    private final JButton resumeBtn = new JButton("Resume");
    private final JButton resetBtn = new JButton("Reset");
    private final JButton skipBtn = new JButton("Skip Phase");

    public FocusPanel(PomodoroTimer pomodoroTimer) {
        super(new BorderLayout(10, 10));
        this.pomodoroTimer = pomodoroTimer;
        this.pomodoroTimer.setListener(this);

        setBorder(new EmptyBorder(12, 12, 12, 12));

        countdownLabel.setFont(countdownLabel.getFont().deriveFont(28f));

        JPanel center = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new java.awt.Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;

        center.add(phaseLabel, c);
        c.gridy = 1;
        center.add(countdownLabel, c);
        add(center, BorderLayout.CENTER);

        JPanel buttons = new JPanel();
        buttons.add(startBtn);
        buttons.add(pauseBtn);
        buttons.add(resumeBtn);
        buttons.add(resetBtn);
        buttons.add(skipBtn);
        add(buttons, BorderLayout.SOUTH);

        startBtn.addActionListener(e -> pomodoroTimer.startWork());
        pauseBtn.addActionListener(e -> pomodoroTimer.pause());
        resumeBtn.addActionListener(e -> pomodoroTimer.resume());
        resetBtn.addActionListener(e -> pomodoroTimer.stopAndReset());
        skipBtn.addActionListener(e -> pomodoroTimer.skipPhase());

        updateButtonState(false, pomodoroTimer.getPhase());
    }

    @Override
    public void onTick(PomodoroTimer.Phase phase, int remainingSeconds) {
        phaseLabel.setText(phase == PomodoroTimer.Phase.WORK ? "Work Session" : "Break Session");
        countdownLabel.setText(PomodoroTimer.formatSeconds(remainingSeconds));
    }

    @Override
    public void onPhaseEnded(PomodoroTimer.Phase endedPhase, PomodoroTimer.Phase nextPhase) {
        String message;
        if (endedPhase == PomodoroTimer.Phase.WORK) {
            message = "Work session complete. Take a short break!";
        } else {
            message = "Break complete. Back to focus!";
        }
        JOptionPane.showMessageDialog(this, message, "Pomodoro", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void onStateChanged(boolean running, PomodoroTimer.Phase phase) {
        updateButtonState(running, phase);
        phaseLabel.setText(phase == PomodoroTimer.Phase.WORK ? "Work Session" : "Break Session");
        countdownLabel.setText(PomodoroTimer.formatSeconds(pomodoroTimer.getRemainingSeconds()));
    }

    private void updateButtonState(boolean running, PomodoroTimer.Phase phase) {
        // Simple enable/disable logic so the UI stays predictable.
        startBtn.setEnabled(!running);
        pauseBtn.setEnabled(running);
        resumeBtn.setEnabled(!running);
        resetBtn.setEnabled(true);
        skipBtn.setEnabled(running);
    }
}

