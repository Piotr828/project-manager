import java.awt.*;
import java.util.Date;
import javax.swing.*;

public class ProjectForm extends JDialog {
    public ProjectForm(Container container, Runnable onSuccess) {
        this (null, container, p ->{
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
        if (project !=null){
            nameField.setText(project.name);
        }
        nameField.setFont(new Font("Arial", Font.PLAIN, 18));
        nameField.setPreferredSize(new Dimension(200, 40));

        gbc.gridy = row++;
        panel.add(new JLabel("Nazwa projektu:"), gbc);
        gbc.gridy = row++;
        panel.add(nameField, gbc);

        // === Opis ===
        JTextArea descArea = new JTextArea(5, 20);
        if (project != null) {
            descArea.setText(project.descript);
        }
        descArea.setFont(new Font("Arial", Font.PLAIN, 14));
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(descArea);

        gbc.gridy = row++;
        panel.add(new JLabel("Opis:"), gbc);
        gbc.gridy = row++;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        panel.add(scrollPane, gbc);

        // Reset fill i weighty dla kolejnych komponentów
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;

        // === Data rozpoczęcia ===
        Date startDate = project == null ? new Date():
            new Date(project.start * 86400L * 1000);
        JSpinner startSpinner = new JSpinner(new SpinnerDateModel(new Date(), null, null, java.util.Calendar.DAY_OF_MONTH));
        startSpinner.setEditor(new JSpinner.DateEditor(startSpinner, "dd.MM.yyyy"));
        startSpinner.setPreferredSize(new Dimension(200, 40));

        gbc.gridy = row++;
        panel.add(new JLabel("Data rozpoczęcia:"), gbc);
        gbc.gridy = row++;
        panel.add(startSpinner, gbc);

        // === Termin końcowy ===
        Date deadlineDate = project == null ? new Date():
            new Date(project.deadline * 86400L * 1000);
        JSpinner deadlineSpinner = new JSpinner(new SpinnerDateModel(new Date(), null, null, java.util.Calendar.DAY_OF_MONTH));
        deadlineSpinner.setEditor(new JSpinner.DateEditor(deadlineSpinner, "dd.MM.yyyy"));
        deadlineSpinner.setPreferredSize(new Dimension(200, 40));

        gbc.gridy = row++;
        panel.add(new JLabel("Termin końcowy:"), gbc);
        gbc.gridy = row++;
        panel.add(deadlineSpinner, gbc);

        // === Wybór koloru ===
        
        Color initialColor = project == null ? Color.WHITE : 
            new Color(project.red, project.green, project.blue);
        Color[] selectedColor = {initialColor};


        gbc.gridy = row++;
        panel.add(new JLabel("Kolor:"), gbc);

        JButton colorButton = new JButton("Wybierz kolor");
        colorButton.setBackground((initialColor));
        colorButton.setPreferredSize(new Dimension(10, 40));
        gbc.gridy = row++;
        panel.add(colorButton, gbc);

        colorButton.addActionListener(e -> {
            Color color = JColorChooser.showDialog(this, "Wybierz kolor", selectedColor[0]);
            if (color != null) {
                selectedColor[0] = color;
                colorButton.setBackground(color);
            }
        });

        // === Przycisk Zapisz ===
        JButton saveButton = new JButton("Zapisz");
        saveButton.setPreferredSize(new Dimension(200, 40));
        gbc.gridy = row++;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(saveButton, gbc);

        saveButton.addActionListener(e -> {
            String name = nameField.getText().trim();

            if (name.isEmpty()) {
                nameField.setBorder(BorderFactory.createLineBorder(Color.RED));
                nameField.requestFocus();
                return;
            } else {
                nameField.setBorder(UIManager.getLookAndFeel().getDefaults().getBorder("TextField.border"));
            }

            if (project == null) {
                // Tworzenie nowego projektu
                Project newProject = new Project(
                    name,
                    descArea.getText(),
                    ((Date)startSpinner.getValue()).toInstant().getEpochSecond() / 86400,
                    ((Date)deadlineSpinner.getValue()).toInstant().getEpochSecond() / 86400,
                    selectedColor[0].getRed(),
                    selectedColor[0].getGreen(),
                    selectedColor[0].getBlue()
                );
                onSuccess.accept(newProject);
            } else {
                // Edycja istniejącego projektu
                project.name = name;
                project.descript = descArea.getText();
                project.start = ((Date)startSpinner.getValue()).toInstant().getEpochSecond() / 86400;
                project.deadline = ((Date)deadlineSpinner.getValue()).toInstant().getEpochSecond() / 86400;
                project.red = selectedColor[0].getRed();
                project.green = selectedColor[0].getGreen();
                project.blue = selectedColor[0].getBlue();
                project.calculatePredict();
                onSuccess.accept(project);
            }

            if (container != null) {
                Main.saveProjects(container);
            }
            dispose();
        });

        add(panel, BorderLayout.CENTER);
    }
}
