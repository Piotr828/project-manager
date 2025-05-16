import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ProjectForm extends JDialog {
    public ProjectForm(Container container, Runnable onSuccess) {
        setTitle("Nowy projekt");
        setModal(true);
        setSize(400, 400);
        setLocationRelativeTo(null);
        
        JPanel panel = new JPanel(new GridLayout(0, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Pola formularza
        JTextField nameField = new JTextField();
        JTextArea descArea = new JTextArea(3, 20);
        JTextField startField = new JTextField();
        JTextField deadlineField = new JTextField();
        
        panel.add(new JLabel("Nazwa projektu:"));
        panel.add(nameField);
        panel.add(new JLabel("Opis:"));
        panel.add(new JScrollPane(descArea));
        panel.add(new JLabel("Data rozpoczęcia:"));
        panel.add(startField);
        panel.add(new JLabel("Termin końcowy:"));
        panel.add(deadlineField);
        
        // Przyciski
        JButton saveButton = new JButton("Zapisz");
        saveButton.addActionListener(e -> {
            Project project = new Project(
                nameField.getText(),
                descArea.getText(),
                Integer.parseInt(startField.getText()),
                Integer.parseInt(deadlineField.getText())
            );
            container.addProject(project);
            onSuccess.run();
            dispose();
        });
        
        panel.add(saveButton);
        add(panel);
    }
    
    // Wersja do edycji istniejącego projektu
    public ProjectForm(Project project, java.util.function.Consumer<Project> onSuccess) {
        // Analogiczna implementacja dla edycji
    }
}