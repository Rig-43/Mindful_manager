package mindfulmanager.ui.panels;

import mindfulmanager.model.TaskManager;
import mindfulmanager.model.TaskManager.TaskManagerListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

/**
 * Dashboard tab showing task counts and progress.
 */
public class DashboardPanel extends JPanel implements TaskManagerListener {
    private final TaskManager taskManager;

    private final JLabel totalLabel = new JLabel();
    private final JLabel completedLabel = new JLabel();
    private final JLabel pendingLabel = new JLabel();
    private final JLabel progressLabel = new JLabel();
    private final JProgressBar progressBar = new JProgressBar(0, 100);

    public DashboardPanel(TaskManager taskManager) {
        super(new BorderLayout(10, 10));
        this.taskManager = taskManager;
        this.taskManager.addListener(this);

        setBorder(new EmptyBorder(12, 12, 12, 12));

        JPanel content = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new java.awt.Insets(8, 8, 8, 8);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;

        content.add(new JLabel("Task Dashboard"), c);

        c.gridy++;
        content.add(totalLabel, c);
        c.gridy++;
        content.add(completedLabel, c);
        c.gridy++;
        content.add(pendingLabel, c);

        c.gridy++;
        content.add(progressLabel, c);
        c.gridy++;
        content.add(progressBar, c);

        add(content, BorderLayout.CENTER);

        refresh();
    }

    private void refresh() {
        int total = taskManager.getTotalTasks();
        int completed = taskManager.getCompletedTasks();
        int pending = taskManager.getPendingTasks();
        double progress = taskManager.getProgressPercentage();

        totalLabel.setText("Total: " + total);
        completedLabel.setText("Completed: " + completed);
        pendingLabel.setText("Pending: " + pending);
        progressLabel.setText("Progress: " + String.format("%.1f", progress) + "%");
        progressBar.setValue((int) Math.round(progress));
    }

    @Override
    public void tasksChanged() {
        refresh();
    }
}

