import javax.swing.*;
import java.awt.*;
import java.util.Date;

public class ProjectForm extends JDialog {
    public ProjectForm(Container container, Runnable onSuccess) {
        setTitle("Nowy projekt");
        setModal(true);
        setSize(400, 600);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        JPanel header = new JPanel(new BorderLayout());

        JLabel title = new JLabel("Dodaj projekt", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        header.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0)); // 20px góra i dół margines
        title.setFont(new Font("Arial", Font.BOLD, 24));
        header.add(title, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridx = 0;

        // Nazwa
        JTextField nameField = new JTextField();
        nameField.setFont(new Font("Arial", Font.PLAIN ,18));
        nameField.setPreferredSize(new Dimension(200, 40));

        gbc.gridy = 0;
        panel.add(new JLabel("Nazwa projektu:"), gbc);
        gbc.gridy = 1;
        panel.add(nameField, gbc);

        // Opis
        JTextArea descArea = new JTextArea();
        descArea.setFont(new Font("Arial", Font.PLAIN, 14));
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(descArea);
        scrollPane.setPreferredSize(new Dimension(200, 120));

        gbc.gridy = 2;
        panel.add(new JLabel("Opis:"), gbc);
        gbc.gridy = 3;
        panel.add(scrollPane, gbc);


        // Data rozpoczęcia
        JSpinner startSpinner = new JSpinner(new SpinnerDateModel(new Date(), null, null, java.util.Calendar.DAY_OF_MONTH));
        startSpinner.setEditor(new JSpinner.DateEditor(startSpinner, "dd.MM.yyyy"));
        startSpinner.setPreferredSize(new Dimension(200, 40));

        gbc.gridy = 4;
        panel.add(new JLabel("Data rozpoczęcia:"), gbc);
        gbc.gridy = 5;
        panel.add(startSpinner, gbc);

        // Termin końcowy
        JSpinner deadlineSpinner = new JSpinner(new SpinnerDateModel(new Date(), null, null, java.util.Calendar.DAY_OF_MONTH));
        deadlineSpinner.setEditor(new JSpinner.DateEditor(deadlineSpinner, "dd.MM.yyyy"));
        deadlineSpinner.setPreferredSize(new Dimension(200, 40));

        gbc.gridy = 6;
        panel.add(new JLabel("Termin końcowy:"), gbc);
        gbc.gridy = 7;
        panel.add(deadlineSpinner, gbc);

        // Przycisk Zapisz
        JButton saveButton = new JButton("Zapisz");
        saveButton.setPreferredSize(new Dimension(200, 40));
        gbc.gridy = 8;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(saveButton, gbc);

        // Akcja zapisu
    saveButton.addActionListener(e -> {
        String name = nameField.getText().trim();

        if (name.isEmpty()) {
            nameField.setBorder(BorderFactory.createLineBorder(Color.RED));
            return;
        } else {
            nameField.setBorder(UIManager.getLookAndFeel().getDefaults().getBorder("TextField.border"));
        }

        Date startDate = (Date) startSpinner.getValue();
        Date deadlineDate = (Date) deadlineSpinner.getValue();

        Project project = new Project(
            name,
            descArea.getText(),
            startDate.toInstant().getEpochSecond() / 86400,
            deadlineDate.toInstant().getEpochSecond() / 86400
        );

    container.addProject(project);
    onSuccess.run();
    dispose();
});


    add(panel, BorderLayout.CENTER); 

    }

    public ProjectForm(Project project, java.util.function.Consumer<Project> onSuccess) {
        // TODO: wersja edycji
    }
}
