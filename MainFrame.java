package mindfulmanager.ui;

import mindfulmanager.mind.MindfulnessEngine;
import mindfulmanager.model.TaskManager;
import mindfulmanager.reflection.ReflectionJournal;
import mindfulmanager.timer.PomodoroTimer;
import mindfulmanager.ui.panels.DashboardPanel;
import mindfulmanager.ui.panels.FocusPanel;
import mindfulmanager.ui.panels.MindfulnessPanel;
import mindfulmanager.ui.panels.ReflectionPanel;
import mindfulmanager.ui.panels.TasksPanel;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Main application window.
 */
public class MainFrame extends JFrame {
    public MainFrame(
            TaskManager taskManager,
            MindfulnessEngine mindfulnessEngine,
            ReflectionJournal reflectionJournal,
            PomodoroTimer pomodoroTimer
    ) {
        super("Mindful Manager");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(920, 640);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Tasks", new TasksPanel(taskManager));
        tabs.addTab("Focus Mode", new FocusPanel(pomodoroTimer));
        tabs.addTab("Mindfulness", new MindfulnessPanel(mindfulnessEngine));
        tabs.addTab("Reflection", new ReflectionPanel(reflectionJournal));
        tabs.addTab("Dashboard", new DashboardPanel(taskManager));

        setContentPane(tabs);

        // Best-effort persistence + cleanup on exit.
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                pomodoroTimer.stopAndReset();
                taskManager.saveDefaultTasks();
            }
        });
    }
}

