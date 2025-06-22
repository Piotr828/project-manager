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
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));


        JCheckBox checkBox = new JCheckBox(task.name, task.status);
        checkBox.addActionListener(this::toggleTaskStatus);
        checkBox.setFont(new Font("Arial", Font.PLAIN, 13));


        JLabel difficultyLabel = new JLabel(String.valueOf(task.diffic), SwingConstants.CENTER);
        difficultyLabel.setPreferredSize(new Dimension(50, 20));
        difficultyLabel.setFont(new Font("Arial", Font.PLAIN, 13));

        add(checkBox, BorderLayout.CENTER);
        add(difficultyLabel, BorderLayout.EAST);
    }

    private void toggleTaskStatus(ActionEvent e) {
        JCheckBox checkBox = (JCheckBox) e.getSource();
        task.status = checkBox.isSelected();
    }
}