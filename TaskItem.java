import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class TaskItem extends JPanel {
    private final Task task;
    
    public TaskItem(Task task) {
        this.task = task;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        // Checkbox i nazwa zadania
        JCheckBox checkBox = new JCheckBox(task.name, task.status);
        checkBox.addActionListener(this::toggleTaskStatus);
        checkBox.setFont(new Font("Arial", Font.PLAIN, 16));
        add(checkBox, BorderLayout.CENTER);

        // Trudność zadania
        JLabel difficultyLabel = new JLabel("Trudność: " + task.diffic);
        add(difficultyLabel, BorderLayout.EAST);
    }

    private void toggleTaskStatus(ActionEvent e) {
        JCheckBox checkBox = (JCheckBox) e.getSource();
        task.status = checkBox.isSelected();
    }
}