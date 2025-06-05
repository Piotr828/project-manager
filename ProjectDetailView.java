import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class ProjectDetailView extends JPanel {
    private final Container container; 

    public ProjectDetailView(Project project, MainFrame frame, Container container) {
        this.container=container;
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Przycisk powrotu
        JButton backButton = new JButton("← Wróć");
        backButton.addActionListener(e -> frame.showDashboard());
        add(backButton, BorderLayout.NORTH);

        // Główny kontentF
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        
        // Nagłówek projektu
        JPanel header = new JPanel(new BorderLayout());
        JLabel title = new JLabel(project.name);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(title);
        
        JButton editButton = new JButton("Edytuj");
        editButton.addActionListener(e -> showEditForm(project, frame));
        header.add(editButton, BorderLayout.EAST);
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, editButton.getPreferredSize().height));
        content.add(header);

        // Opis projektu
        JTextArea description = new JTextArea(project.descript);
        description.setEditable(false);
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        description.setFont(new Font("Arial", Font.PLAIN, 14));
        description.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        content.add(description);
        

        // Postęp projektu
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue(project.progress());
        progressBar.setStringPainted(true);
        progressBar.setFont(new Font("Arial", Font.BOLD, 14));
        progressBar.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        content.add(progressBar);

        // Lista zadań
        JLabel tasksLabel = new JLabel("Zadania:");
        tasksLabel.setFont(new Font("Arial", Font.BOLD, 18));
        tasksLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(tasksLabel);

        JPanel tasksPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JPanel tasksTable = new JPanel();
        tasksTable.setLayout(new BoxLayout(tasksTable, BoxLayout.Y_AXIS));
        tasksTable.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Nagłówki kolumn
        JPanel tasksHeader = new JPanel(new GridLayout(1, 2));
        tasksHeader.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        tasksHeader.setMaximumSize(new Dimension(400, 30));

        JLabel taskNameHeader = new JLabel("Nazwa zadania", SwingConstants.CENTER);
        taskNameHeader.setFont(new Font("Arial", Font.BOLD, 14));
        tasksHeader.add(taskNameHeader);

        JLabel difficultyHeader = new JLabel("Trudność", SwingConstants.CENTER);
        difficultyHeader.setFont(new Font("Arial", Font.BOLD, 14));
        tasksHeader.add(difficultyHeader);

        tasksTable.add(tasksHeader);

        // Lista zadań
        for (Task task : project.tasks) {
            TaskItem item = new TaskItem(task);
            item.setMaximumSize(new Dimension(400, 30)); // stała szerokość
            tasksTable.add(item);
            tasksTable.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        tasksPanel.add(tasksTable); // osadzamy tabelę w centrowanym panelu

        JScrollPane scrollPane = new JScrollPane(tasksPanel);
        scrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        content.add(scrollPane);
        
        JButton addTaskButton = new JButton("+ Dodaj zadanie");
        addTaskButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        addTaskButton.addActionListener(e -> showAddTaskForm(project));
        content.add(Box.createRigidArea(new Dimension(0, 10)));
        content.add(addTaskButton);
        
        add(content, BorderLayout.CENTER);
    }

    private void showEditForm(Project project, MainFrame frame) {
        ProjectForm form = new ProjectForm(project,container, updatedProject -> {
            Main.saveProjects(container);
            frame.showDashboard();
            frame.showProjectDetail(updatedProject);
        });
        form.setVisible(true);
    }

    private void showAddTaskForm(Project project) {
        TaskForm form = new TaskForm(project,container, t -> {
            project.addTask(t);
            // Zastąp obecny widok nową instancją
            MainFrame frame = (MainFrame) SwingUtilities.getWindowAncestor(this);
            frame.showProjectDetail(project);
        });
        form.setVisible(true);
    }
}