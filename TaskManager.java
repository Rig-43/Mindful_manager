package mindfulmanager.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Manages tasks and notifies listeners when task data changes.
 */
public class TaskManager {
    public interface TaskManagerListener {
        void tasksChanged();
    }

    private final ArrayList<Task> tasks = new ArrayList<>();
    private final ArrayList<TaskManagerListener> listeners = new ArrayList<>();

    private File getDefaultTasksFile() {
        File dataDir = new File(System.getProperty("user.home"), ".mindful-manager");
        return new File(dataDir, "tasks.ser");
    }

    public void addListener(TaskManagerListener listener) {
        if (listener == null) return;
        if (!listeners.contains(listener)) listeners.add(listener);
    }

    public void removeListener(TaskManagerListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        for (TaskManagerListener l : new ArrayList<>(listeners)) {
            l.tasksChanged();
        }
    }

    /**
     * Returns the internal tasks list. UI code should treat it as read-only.
     */
    public List<Task> getTasks() {
        return Collections.unmodifiableList(tasks);
    }

    public void addTask(Task task) {
        if (task == null) return;
        tasks.add(task);
        notifyListeners();
    }

    public boolean updateTask(String id, String title, String description, java.time.LocalDate deadline, Priority priority) {
        Task t = findById(id);
        if (t == null) return false;
        t.setTitle(title);
        t.setDescription(description);
        t.setDeadline(deadline);
        t.setPriority(priority);
        notifyListeners();
        return true;
    }

    public boolean deleteTask(String id) {
        Task t = findById(id);
        if (t == null) return false;
        tasks.remove(t);
        notifyListeners();
        return true;
    }

    public boolean setCompleted(String id, boolean completed) {
        Task t = findById(id);
        if (t == null) return false;
        t.setCompleted(completed);
        notifyListeners();
        return true;
    }

    public Task findById(String id) {
        if (id == null) return null;
        for (Task t : tasks) {
            if (id.equals(t.getId())) return t;
        }
        return null;
    }

    public int getTotalTasks() {
        return tasks.size();
    }

    public int getCompletedTasks() {
        int count = 0;
        for (Task t : tasks) if (t.isCompleted()) count++;
        return count;
    }

    public int getPendingTasks() {
        return getTotalTasks() - getCompletedTasks();
    }

    public double getProgressPercentage() {
        int total = getTotalTasks();
        if (total == 0) return 0.0;
        return (getCompletedTasks() * 100.0) / total;
    }

    /**
     * Loads previously saved tasks from the default tasks file (if it exists).
     */
    public void loadDefaultTasks() {
        File file = getDefaultTasksFile();
        if (!file.exists()) return;

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = in.readObject();
            if (obj instanceof List<?>) {
                tasks.clear();
                for (Object item : (List<?>) obj) {
                    if (item instanceof Task) tasks.add((Task) item);
                }
                notifyListeners();
            }
        } catch (IOException | ClassNotFoundException ignored) {
            // If loading fails, keep the app starting with an empty task list.
        }
    }

    /**
     * Persists current tasks to the default tasks file.
     */
    public void saveDefaultTasks() {
        File file = getDefaultTasksFile();
        File dataDir = file.getParentFile();
        if (dataDir != null && !dataDir.exists()) {
            // Best-effort directory creation.
            dataDir.mkdirs();
        }

        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(new ArrayList<>(tasks));
        } catch (IOException ignored) {
            // Best-effort persistence: ignore failures.
        }
    }
}

