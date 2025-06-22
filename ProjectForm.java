import javax.swing.*;
import java.awt.*;
import java.util.Date;

public class ProjectForm extends JDialog {
    private final JTextField nameField = new JTextField();
    private final JTextArea descArea = new JTextArea(5, 20);
    private final JSpinner startSpinner = new JSpinner(new SpinnerDateModel(new Date(), null, null, java.util.Calendar.DAY_OF_MONTH));
    private final JSpinner deadlineSpinner = new JSpinner(new SpinnerDateModel(new Date(), null, null, java.util.Calendar.DAY_OF_MONTH));
    private final JComboBox<Team> teamCombo = new JComboBox<>();
    private final Color[] selectedColor = new Color[]{Color.WHITE};

    public ProjectForm(Container container, Runnable onSuccess) {
        this(null, container, p -> {
            container.addProject(p);
            onSuccess.run();
        });
    }

    public ProjectForm(Project project, Container container, java.util.function.Consumer<Project> onSuccess) {
        setTitle(project == null ? "Dodaj projekt" : "Edytuj projekt");
        setModal(true);
        setSize(400, 600);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        if (project != null) {
            selectedColor[0] = new Color(project.getRed(), project.getGreen(), project.getBlue());
        }

        add(initHeaderPanel(project), BorderLayout.NORTH);
        add(initFormPanel(project, container, onSuccess), BorderLayout.CENTER);
    }

    private JPanel initHeaderPanel(Project project) {
        JPanel header = new JPanel(new BorderLayout());
        JLabel title = new JLabel(project == null ? "Dodaj projekt" : "Edytuj projekt", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        header.add(title, BorderLayout.CENTER);
        return header;
    }

    private JPanel initFormPanel(Project project, Container container, java.util.function.Consumer<Project> onSuccess) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        
        if (project != null) nameField.setText(project.getName());
        nameField.setFont(new Font("Arial", Font.PLAIN, 18));
        nameField.setPreferredSize(new Dimension(200, 40));

        panel.add(new JLabel("Nazwa projektu:"), gbc);

        panel.add(nameField, gbc);

        
        if (project != null) descArea.setText(project.getDescript());
        descArea.setFont(new Font("Arial", Font.PLAIN, 14));
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(descArea);

        panel.add(new JLabel("Opis:"), gbc);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        panel.add(scrollPane, gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;

        
        panel.add(new JLabel("Data rozpoczęcia:"), gbc);
        startSpinner.setEditor(new JSpinner.DateEditor(startSpinner, "dd.MM.yyyy"));
        startSpinner.setPreferredSize(new Dimension(200, 40));
        panel.add(startSpinner, gbc);

        panel.add(new JLabel("Termin końcowy:"), gbc);
        deadlineSpinner.setEditor(new JSpinner.DateEditor(deadlineSpinner, "dd.MM.yyyy"));
        deadlineSpinner.setPreferredSize(new Dimension(200, 40));
        panel.add(deadlineSpinner, gbc);

        
        panel.add(new JLabel("Zespół (opcjonalnie):"), gbc);
        teamCombo.addItem(null);
        User currentUser = AuthManager.getActiveUser();
        if (currentUser != null) {
            for (Team team : container.getUserTeams(currentUser)) {
                teamCombo.addItem(team);
            }
        }
        if (project != null && project.getTeam() != null) {
            teamCombo.setSelectedItem(project.getTeam());
        }

        panel.add(teamCombo, gbc);

        
        panel.add(new JLabel("Kolor:"), gbc);
        JButton colorButton = new JButton("Wybierz kolor");
        colorButton.setBackground(selectedColor[0]);
        colorButton.setPreferredSize(new Dimension(10, 40));
        panel.add(colorButton, gbc);

        colorButton.addActionListener(e -> {
            Color color = JColorChooser.showDialog(this, "Wybierz kolor", selectedColor[0]);
            if (color != null) {
                selectedColor[0] = color;
                colorButton.setBackground(color);
            }
        });

        
        gbc.anchor = GridBagConstraints.CENTER;
        JButton saveButton = createSaveButton(project, container, onSuccess);
        panel.add(saveButton, gbc);

        return panel;
    }

    private JButton createSaveButton(Project project, Container container, java.util.function.Consumer<Project> onSuccess) {
        JButton saveButton = new JButton("Zapisz");
        saveButton.setPreferredSize(new Dimension(200, 40));
        saveButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                nameField.setBorder(BorderFactory.createLineBorder(Color.RED));
                nameField.requestFocus();
                return;
            } else {
                nameField.setBorder(UIManager.getLookAndFeel().getDefaults().getBorder("TextField.border"));
            }

            Team selectedTeam = (Team) teamCombo.getSelectedItem();
            if (project == null) {
                Project newProject = new Project(
                        name,
                        descArea.getText(),
                        ((Date) startSpinner.getValue()).toInstant().getEpochSecond() / 86400,
                        ((Date) deadlineSpinner.getValue()).toInstant().getEpochSecond() / 86400,
                        selectedColor[0].getRed(),
                        selectedColor[0].getGreen(),
                        selectedColor[0].getBlue()
                );
                newProject.setTeam(selectedTeam);
                onSuccess.accept(newProject);
            } else {
                project.setName(name);
                project.setDescript(descArea.getText());
                project.setStart(((Date) startSpinner.getValue()).toInstant().getEpochSecond() / 86400);
                project.setDeadline(((Date) deadlineSpinner.getValue()).toInstant().getEpochSecond() / 86400);
                project.setRed(selectedColor[0].getRed());
                project.setGreen(selectedColor[0].getGreen());
                project.setBlue(selectedColor[0].getBlue());
                project.setTeam(selectedTeam);
                project.calculatePredict();
                onSuccess.accept(project);
            }

            if (container != null) {
                Main.saveProjects(container);
            }
            dispose();
        });

        return saveButton;
    }
}
