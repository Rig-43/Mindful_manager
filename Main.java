package mindfulmanager;

import mindfulmanager.mind.MindfulnessEngine;
import mindfulmanager.model.TaskManager;
import mindfulmanager.reflection.ReflectionJournal;
import mindfulmanager.timer.PomodoroTimer;
import mindfulmanager.ui.MainFrame;

import javax.swing.SwingUtilities;

/**
 * Application entry point for Mindful Manager.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TaskManager taskManager = new TaskManager();
            taskManager.loadDefaultTasks();
            MindfulnessEngine mindfulnessEngine = new MindfulnessEngine();
            ReflectionJournal reflectionJournal = new ReflectionJournal();
            PomodoroTimer pomodoroTimer = new PomodoroTimer();

            MainFrame frame = new MainFrame(taskManager, mindfulnessEngine, reflectionJournal, pomodoroTimer);
            frame.setVisible(true);
        });
    }
}

