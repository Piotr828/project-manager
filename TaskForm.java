import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class TaskForm extends JDialog {

    public TaskForm(Project project, Container container, Consumer<Task> onSuccess, Consumer<Task> onDelete) {

        setTitle("Nowe zadanie");
        setModal(true);
        setSize(350, 250);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(0, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));


        JTextField nameField = new JTextField();
        JSpinner difficultySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));

        panel.add(new JLabel("Nazwa zadania:"));
        panel.add(nameField);
        panel.add(new JLabel("Trudność (1-10):"));
        panel.add(difficultySpinner);

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