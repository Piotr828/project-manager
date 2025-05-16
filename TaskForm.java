import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;

public class TaskForm extends JDialog {
    public TaskForm(Project project, Consumer<Task> onSuccess) {
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
        
        // Przyciski
        JButton saveButton = new JButton("Zapisz");
        saveButton.addActionListener(e -> {
            Task task = new Task(
                nameField.getText(),
                false,
                ((Number)difficultySpinner.getValue()).byteValue()
            );
            onSuccess.accept(task);
            dispose();
        });
        
        panel.add(saveButton);
        add(panel);
    }
}