import java.awt.*;
import java.util.function.Consumer;
import javax.swing.*;

public class TaskForm extends JDialog {
    private final Container container;
    private final Project project;

    public TaskForm(Project project,Container container,Consumer<Task> onSuccess, Consumer<Task> onDelete) {
        this.project=project;
        this.container=container;

        setTitle("Nowe zadanie");
        setModal(true);
        setSize(350, 250);
        setLocationRelativeTo(null);
        
        JPanel panel = new JPanel(new GridLayout(0, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Pola formularza
        JTextField nameField = new JTextField();
        JSpinner difficultySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        
        panel.add(new JLabel("Nazwa zadania:"));
        panel.add(nameField);
        panel.add(new JLabel("Trudność (1-10):"));
        panel.add(difficultySpinner);

        // Panel przycisków
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        // Przyciski
        JButton saveButton = new JButton("Zapisz");
        saveButton.addActionListener(e -> {
            String name = nameField.getText().trim();

            if (name.isEmpty()) {
                nameField.setBorder(BorderFactory.createLineBorder(Color.RED));
                return;
            } else {
                nameField.setBorder(UIManager.getLookAndFeel().getDefaults().getBorder("TextField.border"));
            }

            Task task = new Task(
                name,
                false,
                ((Number) difficultySpinner.getValue()).byteValue()
            );

            onSuccess.accept(task);
            Main.saveProjects(container);
            dispose();
        });

        
        panel.add(saveButton);
        add(panel);
    }
}