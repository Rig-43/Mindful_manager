package mindfulmanager.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a single task in Mindful Manager.
 */
public class Task implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String title;
    private String description;
    private LocalDate deadline;
    private Priority priority;
    private boolean completed;

    public Task(String id, String title, String description, LocalDate deadline, Priority priority, boolean completed) {
        this.id = Objects.requireNonNull(id, "id");
        this.title = title == null ? "" : title;
        this.description = description == null ? "" : description;
        this.deadline = deadline;
        this.priority = priority == null ? Priority.MEDIUM : priority;
        this.completed = completed;
    }

    public static Task newTask(String title, String description, LocalDate deadline, Priority priority) {
        return new Task(java.util.UUID.randomUUID().toString(), title, description, deadline, priority, false);
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? "" : title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? "" : description;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority == null ? Priority.MEDIUM : priority;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}

