import java.awt.*;
import java.util.Date;
import javax.swing.*;
import java.util.List;
import java.util.ArrayList;

public class ProjectForm extends JDialog {
    private JComboBox<Team> teamComboBox;
    
    public ProjectForm(Container container, Runnable onSuccess) {
        this (null, container, p ->{
            container.addProject(p);
            onSuccess.run();
        });
    }

    public ProjectForm(Project project, Container container, java.util.function.Consumer<Project> onSuccess) {
        setTitle(project == null ? "Dodaj projekt" : "Edytuj projekt");
        setModal(true);
        setSize(400, 650); // Zwiększ wysokość dla nowego pola
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // === NAGŁÓWEK ===
        JPanel header = new JPanel(new BorderLayout());
        JLabel title = new JLabel(project == null ? "Dodaj projekt" : "Edytuj projekt", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        header.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        header.add(title, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        // === PANEL GŁÓWNY ===
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        int row = 0;

        // === Nazwa ===
        JTextField nameField = new JTextField();
        if (project != null) {
            nameField.setText(project.name);
        }
        
        gbc.gridy = row++;
        panel.add(new JLabel("Nazwa projektu:"), gbc);
        gbc.gridy = row++;
        panel.add(nameField, gbc);
        
        // === Opis ===
        JTextArea descField = new JTextArea(3, 20);
        descField.setLineWrap(true);
        descField.setWrapStyleWord(true);
        if (project != null) {
            descField.setText(project.descript);
        }
        
        gbc.gridy = row++;
        panel.add(new JLabel("Opis:"), gbc);
        gbc.gridy = row++;
        panel.add(new JScrollPane(descField), gbc);
        
        // === Data rozpoczęcia ===
        JSpinner startDateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor startEditor = new JSpinner.DateEditor(startDateSpinner, "dd/MM/yyyy");
        startDateSpinner.setEditor(startEditor);
        if (project != null) {
            startDateSpinner.setValue(new Date(project.start * 24 * 60 * 60 * 1000L));
        }
        
        gbc.gridy = row++;
        panel.add(new JLabel("Data rozpoczęcia:"), gbc);
        gbc.gridy = row++;
        panel.add(startDateSpinner, gbc);
        
        // === Termin ===
        JSpinner deadlineSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor deadlineEditor = new JSpinner.DateEditor(deadlineSpinner, "dd/MM/yyyy");
        deadlineSpinner.setEditor(deadlineEditor);
        if (project != null) {
            deadlineSpinner.setValue(new Date(project.deadline * 24 * 60 * 60 * 1000L));
        }
        
        gbc.gridy = row++;
        panel.add(new JLabel("Termin:"), gbc);
        gbc.gridy = row++;
        panel.add(deadlineSpinner, gbc);
        
        // === Zespół (NOWE POLE) ===
        setupTeamComboBox();
        if (project != null) {
            teamComboBox.setSelectedItem(project.getTeam());
        }
        
        gbc.gridy = row++;
        panel.add(new JLabel("Zespół:"), gbc);
        gbc.gridy = row++;
        panel.add(teamComboBox, gbc);
        
        // === Kolor ===
        JPanel colorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JSpinner redSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 255, 1));
        JSpinner greenSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 255, 1));
        JSpinner blueSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 255, 1));
        
        if (project != null) {
            redSpinner.setValue(project.red);
            greenSpinner.setValue(project.green);
            blueSpinner.setValue(project.blue);
        }
        
        colorPanel.add(new JLabel("R:"));
        colorPanel.add(redSpinner);
        colorPanel.add(new JLabel("G:"));
        colorPanel.add(greenSpinner);
        colorPanel.add(new JLabel("B:"));
        colorPanel.add(blueSpinner);
        
        gbc.gridy = row++;
        panel.add(new JLabel("Kolor:"), gbc);
        gbc.gridy = row++;
        panel.add(colorPanel, gbc);
        
        // === Przyciski ===
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton saveButton = new JButton(project == null ? "Dodaj" : "Zapisz");
        saveButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String description = descField.getText().trim();
            
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nazwa projektu jest wymagana!");
                return;
            }
            
            Date startDate = (Date) startDateSpinner.getValue();
            Date deadlineDate = (Date) deadlineSpinner.getValue();
            
            if (deadlineDate.before(startDate)) {
                JOptionPane.showMessageDialog(this, "Termin nie może być wcześniejszy niż data rozpoczęcia!");
                return;
            }
            
            long startEpoch = startDate.getTime() / (1000L * 60 * 60 * 24);
            long deadlineEpoch = deadlineDate.getTime() / (1000L * 60 * 60 * 24);
            
            int red = (Integer) redSpinner.getValue();
            int green = (Integer) greenSpinner.getValue();
            int blue = (Integer) blueSpinner.getValue();
            
            Team selectedTeam = (Team) teamComboBox.getSelectedItem();
            
            if (project == null) {
                // Nowy projekt
                Project newProject = new Project(name, description, startEpoch, deadlineEpoch, red, green, blue);
                newProject.setTeam(selectedTeam);
                onSuccess.accept(newProject);
            } else {
                // Edycja istniejącego projektu
                project.name = name;
                project.descript = description;
                project.start = startEpoch;
                project.deadline = deadlineEpoch;
                project.red = red;
                project.green = green;
                project.blue = blue;
                project.setTeam(selectedTeam);
                project.calculatePredict();
                onSuccess.accept(project);
            }
            
            Main.saveProjects(container);
            dispose();
        });
        
        JButton cancelButton = new JButton("Anuluj");
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        gbc.gridy = row++;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(buttonPanel, gbc);
        
        add(panel, BorderLayout.CENTER);
    }
    
    private void setupTeamComboBox() {
        // Pobierz zespoły użytkownika
        User currentUser = AuthManager.getActiveUser();
        List<Team> availableTeams = new ArrayList<>();
        
        // Dodaj opcję "Brak zespołu" (projekt publiczny)
        availableTeams.add(null);
        
        if (currentUser != null) {
            availableTeams.addAll(currentUser.getTeams());
        }
        
        teamComboBox = new JComboBox<>(availableTeams.toArray(new Team[0]));
        teamComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    setText("Projekt publiczny");
                } else {
                    setText(((Team) value).getName());
                }
                return this;
            }
        });
    }
}