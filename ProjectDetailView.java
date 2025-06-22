import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ProjectDetailView extends JPanel {
    
    private static final String BUTTON_BACK_TEXT = "← Wróć";
    private static final String BUTTON_EDIT_TEXT = "Edytuj";
    private static final String BUTTON_ADD_TASK_TEXT = "+ Dodaj zadanie";
    private static final String DELETE_PROJECT_TEXT = "Usuń projekt";
    private static final String DELETE_PROJECT_CONFIRM_TITLE = "Potwierdzenie usunięcia";
    private static final String DELETE_PROJECT_CONFIRM_MESSAGE = "Czy na pewno chcesz usunąć projekt \"%s\"?";
    private static final String LABEL_TASKS = "Zadania:";
    private static final String LABEL_TASK_NAME = "Nazwa zadania";
    private static final String LABEL_DIFFICULTY = "Trudność";
    private static final String DELETE_BUTTON_TEXT = "Usuń";
    private static final String DELETE_CONFIRM_TITLE = "Potwierdzenie usunięcia";
    private static final String DELETE_CONFIRM_TEMPLATE = "Czy na pewno chcesz usunąć zadanie '%s'?";

    
    private static final String FONT_NAME = "Arial";
    private static final Font FONT_TITLE = new Font(FONT_NAME, Font.BOLD, 24);
    private static final Font FONT_HEADER = new Font(FONT_NAME, Font.BOLD, 18);
    private static final Font FONT_SUBHEADER = new Font(FONT_NAME, Font.BOLD, 14);
    private static final Font FONT_BODY = new Font(FONT_NAME, Font.PLAIN, 14);

    private final Container container;

    public ProjectDetailView(Project project, MainFrame frame, Container container) {
        this.container = container;
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JButton backButton = new JButton(BUTTON_BACK_TEXT);
        backButton.addActionListener(e -> frame.showDashboard());
        add(backButton, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JPanel header = new JPanel(new BorderLayout());
        JLabel title = new JLabel(project.getName());
        title.setFont(FONT_TITLE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(title);

        JButton editButton = new JButton(BUTTON_EDIT_TEXT);
        editButton.addActionListener(e -> showEditForm(project, frame));
        header.add(editButton, BorderLayout.EAST);
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, editButton.getPreferredSize().height));
        content.add(header);

        JTextArea description = new JTextArea(project.getDescript());
        description.setEditable(false);
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        description.setFont(FONT_BODY);
        description.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        content.add(description);

        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue(project.progress());
        progressBar.setStringPainted(true);
        progressBar.setFont(FONT_SUBHEADER);
        progressBar.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        content.add(progressBar);

        JLabel tasksLabel = new JLabel(LABEL_TASKS);
        tasksLabel.setFont(FONT_HEADER);
        tasksLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(tasksLabel);

        JPanel tasksPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JPanel tasksTable = new JPanel();
        tasksTable.setLayout(new BoxLayout(tasksTable, BoxLayout.Y_AXIS));
        tasksTable.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel tasksHeader = new JPanel(new GridLayout(1, 2));
        tasksHeader.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        tasksHeader.setMaximumSize(new Dimension(400, 30));

        JLabel taskNameHeader = new JLabel(LABEL_TASK_NAME, SwingConstants.CENTER);
        taskNameHeader.setFont(FONT_SUBHEADER);
        tasksHeader.add(taskNameHeader);

        JLabel difficultyHeader = new JLabel(LABEL_DIFFICULTY, SwingConstants.CENTER);
        difficultyHeader.setFont(FONT_SUBHEADER);
        tasksHeader.add(difficultyHeader);

        tasksHeader.add(new JLabel("")); 
        tasksTable.add(tasksHeader);

        for (Task task : project.getTasks()) {
            JPanel taskRowPanel = new JPanel(new BorderLayout(10, 0));
            taskRowPanel.setMaximumSize(new Dimension(500, 35));

            TaskItem item = new TaskItem(task);
            taskRowPanel.add(item, BorderLayout.CENTER);

            JButton deleteButton = new JButton(DELETE_BUTTON_TEXT);
            deleteButton.addActionListener(e -> {
                int response = JOptionPane.showConfirmDialog(ProjectDetailView.this, String.format(DELETE_CONFIRM_TEMPLATE, task.name), DELETE_CONFIRM_TITLE, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (response == JOptionPane.YES_OPTION) {
                    project.removeTask(task);
                    Main.saveProjects(container);
                    frame.showProjectDetail(project);
                }
            });

            taskRowPanel.add(deleteButton, BorderLayout.EAST);
            tasksTable.add(taskRowPanel);
            tasksTable.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        tasksPanel.add(tasksTable);

        JScrollPane scrollPane = new JScrollPane(tasksPanel);
        scrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        content.add(scrollPane);

        JButton addTaskButton = new JButton(BUTTON_ADD_TASK_TEXT);
        addTaskButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        addTaskButton.addActionListener(e -> showAddTaskForm(project));
        content.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonsPanel.add(addTaskButton);

        JButton deleteProjectButton = new JButton(DELETE_PROJECT_TEXT);
        deleteProjectButton.setForeground(Color.RED);
        deleteProjectButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(ProjectDetailView.this, String.format(DELETE_PROJECT_CONFIRM_MESSAGE, project.getName()), DELETE_PROJECT_CONFIRM_TITLE, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                container.removeProject(project);
                Main.saveProjects(container);
                frame.showDashboard();
            }
        });

        buttonsPanel.add(deleteProjectButton);
        content.add(buttonsPanel);

        add(content, BorderLayout.CENTER);
    }

    private void showEditForm(Project project, MainFrame frame) {
        ProjectForm form = new ProjectForm(project, container, updatedProject -> {
            Main.saveProjects(container);
            frame.showDashboard();
            frame.showProjectDetail(updatedProject);
        });
        form.setVisible(true);
    }

    private void showAddTaskForm(Project project) {
        TaskForm form = new TaskForm(project, container, task -> {
            project.addTask(task);
            Main.saveProjects(container);
            MainFrame frame = (MainFrame) SwingUtilities.getWindowAncestor(this);
            frame.showProjectDetail(project);
        }, null);
        form.setVisible(true);
    }
}
