package mindfulmanager.ui.panels;

import mindfulmanager.model.Priority;
import mindfulmanager.model.Task;
import mindfulmanager.model.TaskManager;
import mindfulmanager.model.TaskManager.TaskManagerListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Task management panel (add/edit/delete/complete).
 */
public class TasksPanel extends JPanel implements TaskManagerListener {
    private final TaskManager taskManager;

    private final JTextField titleField = new JTextField();
    private final JTextArea descriptionArea = new JTextArea(4, 20);
    private final JTextField deadlineField = new JTextField();
    private final JComboBox<Priority> priorityCombo = new JComboBox<>(Priority.values());

    private final JTable taskTable;
    private final TaskTableModel tableModel = new TaskTableModel();

    private String selectedTaskId = null;

    public TasksPanel(TaskManager taskManager) {
        super(new BorderLayout(10, 10));
        this.taskManager = taskManager;
        this.taskManager.addListener(this);

        setBorder(new EmptyBorder(12, 12, 12, 12));

        taskTable = new JTable(tableModel);
        taskTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        taskTable.setRowHeight(24);
        JScrollPane scrollPane = new JScrollPane(taskTable);

        // Form layout (top)
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new java.awt.Insets(4, 4, 4, 4);
        c.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        c.gridx = 0;
        c.gridy = row;
        formPanel.add(new JLabel("Title:"), c);
        c.gridx = 1;
        formPanel.add(titleField, c);
        row++;

        c.gridx = 0;
        c.gridy = row;
        formPanel.add(new JLabel("Deadline (yyyy-MM-dd):"), c);
        c.gridx = 1;
        formPanel.add(deadlineField, c);
        row++;

        c.gridx = 0;
        c.gridy = row;
        formPanel.add(new JLabel("Priority:"), c);
        c.gridx = 1;
        formPanel.add(priorityCombo, c);
        row++;

        c.gridx = 0;
        c.gridy = row;
        c.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(new JLabel("Description:"), c);
        c.gridx = 1;
        c.anchor = GridBagConstraints.CENTER;
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        formPanel.add(descScroll, c);

        // Buttons (bottom)
        JButton addBtn = new JButton("Add Task");
        JButton updateBtn = new JButton("Update Task");
        JButton deleteBtn = new JButton("Delete Task");
        JButton completeBtn = new JButton("Mark Completed");

        addBtn.addActionListener(e -> onAdd());
        updateBtn.addActionListener(e -> onUpdate());
        deleteBtn.addActionListener(e -> onDelete());
        completeBtn.addActionListener(e -> onToggleCompleted());

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.add(addBtn);
        buttonsPanel.add(updateBtn);
        buttonsPanel.add(deleteBtn);
        buttonsPanel.add(completeBtn);

        add(formPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonsPanel, BorderLayout.SOUTH);

        taskTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = taskTable.getSelectedRow();
                if (selectedRow >= 0) {
                    List<Task> tasks = taskManager.getTasks();
                    if (selectedRow < tasks.size()) {
                        Task t = tasks.get(selectedRow);
                        selectedTaskId = t.getId();
                        populateFieldsFromTask(t);
                    }
                }
            }
        });
    }

    private void populateFieldsFromTask(Task t) {
        if (t == null) return;
        titleField.setText(t.getTitle());
        descriptionArea.setText(t.getDescription());
        deadlineField.setText(t.getDeadline() == null ? "" : t.getDeadline().toString());
        priorityCombo.setSelectedItem(t.getPriority());
    }

    private void clearFields() {
        titleField.setText("");
        descriptionArea.setText("");
        deadlineField.setText("");
        priorityCombo.setSelectedItem(Priority.MEDIUM);
        selectedTaskId = null;
        taskTable.clearSelection();
    }

    private LocalDate parseDeadlineOrNull(String text) {
        String s = text == null ? "" : text.trim();
        if (s.isEmpty()) return null;
        try {
            return LocalDate.parse(s);
        } catch (DateTimeParseException ex) {
            return null;
        }
    }

    private void onAdd() {
        String title = titleField.getText().trim();
        if (title.isEmpty()) {
            showError("Title is required.");
            return;
        }

        String deadlineText = deadlineField.getText().trim();
        LocalDate deadline = null;
        if (!deadlineText.isEmpty()) {
            try {
                deadline = LocalDate.parse(deadlineText);
            } catch (DateTimeParseException ex) {
                showError("Deadline must be in yyyy-MM-dd format.");
                return;
            }
        }

        String desc = descriptionArea.getText();
        Priority priority = (Priority) priorityCombo.getSelectedItem();

        Task task = Task.newTask(title, desc, deadline, priority);
        taskManager.addTask(task);
        clearFields();
    }

    private void onUpdate() {
        if (selectedTaskId == null) {
            showError("Select a task to update.");
            return;
        }

        String title = titleField.getText().trim();
        if (title.isEmpty()) {
            showError("Title is required.");
            return;
        }

        String deadlineText = deadlineField.getText().trim();
        LocalDate deadline = null;
        if (!deadlineText.isEmpty()) {
            try {
                deadline = LocalDate.parse(deadlineText);
            } catch (DateTimeParseException ex) {
                showError("Deadline must be in yyyy-MM-dd format.");
                return;
            }
        }

        String desc = descriptionArea.getText();
        Priority priority = (Priority) priorityCombo.getSelectedItem();

        boolean ok = taskManager.updateTask(selectedTaskId, title, desc, deadline, priority);
        if (!ok) showError("Task not found (it may have been deleted).");
        clearFields();
    }

    private void onDelete() {
        if (selectedTaskId == null) {
            showError("Select a task to delete.");
            return;
        }
        boolean ok = taskManager.deleteTask(selectedTaskId);
        if (!ok) showError("Task not found (it may have been deleted).");
        clearFields();
    }

    private void onToggleCompleted() {
        if (selectedTaskId == null) {
            showError("Select a task first.");
            return;
        }

        Task t = taskManager.findById(selectedTaskId);
        if (t == null) {
            showError("Task not found.");
            clearFields();
            return;
        }
        taskManager.setCompleted(selectedTaskId, !t.isCompleted());
        // Keep selection so user can keep editing.
        populateFieldsFromTask(t);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Validation Error", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void tasksChanged() {
        // Refresh the table.
        tableModel.fireTableDataChanged();

        // If the selected task was deleted, clear the fields.
        if (selectedTaskId != null && taskManager.findById(selectedTaskId) == null) {
            clearFields();
        }
    }

    private class TaskTableModel extends AbstractTableModel {
        private final String[] columns = new String[] { "Title", "Deadline", "Priority", "Status" };

        @Override
        public int getRowCount() {
            return taskManager.getTasks().size();
        }

        @Override
        public int getColumnCount() {
            return columns.length;
        }

        @Override
        public String getColumnName(int column) {
            return columns[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            List<Task> tasks = taskManager.getTasks();
            if (rowIndex < 0 || rowIndex >= tasks.size()) return "";
            Task t = tasks.get(rowIndex);

            switch (columnIndex) {
                case 0:
                    return t.getTitle();
                case 1:
                    return t.getDeadline() == null ? "" : t.getDeadline().toString();
                case 2:
                    return t.getPriority();
                case 3:
                    return t.isCompleted() ? "Completed" : "Pending";
                default:
                    return "";
            }
        }
    }
}

