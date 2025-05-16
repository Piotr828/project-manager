import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ProjectDetailView extends JPanel {
    public ProjectDetailView(Project project, MainFrame frame) {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Przycisk powrotu
        JButton backButton = new JButton("← Wróć");
        backButton.addActionListener(e -> frame.showDashboard());
        add(backButton, BorderLayout.NORTH);

        // Główny kontent
        JPanel content = new JPanel(new BorderLayout());
        
        // Nagłówek projektu
        JPanel header = new JPanel(new BorderLayout());
        JLabel title = new JLabel(project.name);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        header.add(title, BorderLayout.WEST);
        
        JButton editButton = new JButton("Edytuj");
        editButton.addActionListener(e -> showEditForm(project, frame));
        header.add(editButton, BorderLayout.EAST);
        
        content.add(header, BorderLayout.NORTH);

        // Postęp projektu
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue(project.progress());
        progressBar.setStringPainted(true);
        content.add(progressBar, BorderLayout.CENTER);

        // Lista zadań
        JPanel tasksPanel = new JPanel();
        tasksPanel.setLayout(new BoxLayout(tasksPanel, BoxLayout.Y_AXIS));
        
        for (Task task : project.tasks) {
            tasksPanel.add(new TaskItem(task));
            tasksPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        }
        
        JButton addTaskButton = new JButton("+ Dodaj zadanie");
        addTaskButton.addActionListener(e -> showAddTaskForm(project));
        
        content.add(new JScrollPane(tasksPanel), BorderLayout.CENTER);
        content.add(addTaskButton, BorderLayout.SOUTH);
        
        add(content, BorderLayout.CENTER);
    }

    private void showEditForm(Project project, MainFrame frame) {
        ProjectForm form = new ProjectForm(project, p -> {
            frame.showDashboard();
            frame.showProjectDetail(p);
        });
        form.setVisible(true);
    }

    private void showAddTaskForm(Project project) {
        TaskForm form = new TaskForm(project, t -> {
            project.addTask(t);
            // Zastąp obecny widok nową instancją
            MainFrame frame = (MainFrame) SwingUtilities.getWindowAncestor(this);
            frame.showProjectDetail(project);
        });
        form.setVisible(true);
    }
}