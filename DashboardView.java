import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.util.ArrayList;
import java.util.List;

public class DashboardView extends JPanel {
    private final MainFrame frame;
    private final Container container;
    private final JPanel projectsPanel;

    public DashboardView(Container container, MainFrame frame) {
        this.container = container;
        this.frame = frame;
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Nagłówek
        JPanel header = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Twoje projekty");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        header.add(title, BorderLayout.WEST);
        
        JPanel rightPanel = new JPanel(new BorderLayout());

        //Panel sortowania
        JPanel sortPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        sortPanel.setOpaque(false);

        JLabel sortLabel = new JLabel("Sortuj wg: ");
        sortPanel.add(sortLabel);

        JComboBox<String> sortCombo = new JComboBox<>(new String[]{
            "Nazwy (A-Z)", "Data rozpoczęcia", "Terminu", 
            "Trudności", "Postępu", "Przewidywania", 
            "Opóźnienia", "Koloru"
        });
        sortCombo.setSelectedIndex(Math.abs(container.sortby) - 1);
        
        // Panel przycisków
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton addButton = new JButton("+ Dodaj projekt");
        addButton.addActionListener(this::showAddProjectForm);
        buttonPanel.add(addButton);
        
        JButton exportButton = new JButton("Eksportuj do ICS");
        exportButton.addActionListener(this::exportICS);
        buttonPanel.add(exportButton);
        
        // Nowy przycisk ustawień
        JButton settingsButton = new JButton("Ustawienia");
        settingsButton.addActionListener(this::showSettings);
        buttonPanel.add(settingsButton);
        
        rightPanel.add(sortPanel, BorderLayout.NORTH);
        rightPanel.add(buttonPanel, BorderLayout.SOUTH);
        header.add(rightPanel, BorderLayout.EAST);
        
        add(header, BorderLayout.NORTH);

        // Panel projektów
        projectsPanel = new JPanel();
        projectsPanel.setLayout(new BoxLayout(projectsPanel, BoxLayout.Y_AXIS));
        
        JScrollPane scrollPane = new JScrollPane(projectsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);

        // Nasłuchiwanie zmian rozmiaru
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizeProjectCards();
            }
        });

        refreshProjects();
    }

    private void showSettings(ActionEvent e) {
        User currentUser = AuthManager.getActiveUser();
        if (currentUser != null) {
            SettingsWindow settings = new SettingsWindow(currentUser, container);
            settings.setVisible(true);
            // Odśwież projekty po zamknięciu ustawień
            refreshProjects();
        }
    }

    private void exportICS(ActionEvent e) {
        // Implementacja eksportu
    }

    private void refreshProjects() {
        projectsPanel.removeAll();
        
        // Pobierz aktualnego użytkownika
        User currentUser = AuthManager.getActiveUser();
        
        if (currentUser != null) {
            // Podziel projekty na zespołowe i publiczne
            List<Project> teamProjects = new ArrayList<>();
            List<Project> publicProjects = new ArrayList<>();
            
            for (Project project : container.projects) {
                if (project.getTeam() != null && project.getTeam().isMember(currentUser)) {
                    teamProjects.add(project);
                } else if (project.getTeam() == null) {
                    publicProjects.add(project);
                }
            }
            
            // Dodaj sekcję projektów zespołowych
            if (!teamProjects.isEmpty()) {
                JLabel teamLabel = new JLabel("Projekty zespołowe");
                teamLabel.setFont(new Font("Arial", Font.BOLD, 16));
                teamLabel.setForeground(new Color(0, 120, 215));
                teamLabel.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5));
                teamLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                projectsPanel.add(teamLabel);
                
                for (Project project : teamProjects) {
                    ProjectCard card = new ProjectCard(project, () -> frame.showProjectDetail(project));
                    card.setAlignmentX(Component.LEFT_ALIGNMENT);
                    projectsPanel.add(card);
                    projectsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                }
                
                // Separator
                JSeparator separator = new JSeparator();
                separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
                projectsPanel.add(separator);
                projectsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
            
            // Dodaj sekcję projektów publicznych
            if (!publicProjects.isEmpty()) {
                JLabel publicLabel = new JLabel("Projekty publiczne");
                publicLabel.setFont(new Font("Arial", Font.BOLD, 16));
                publicLabel.setForeground(Color.GRAY);
                publicLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                publicLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                projectsPanel.add(publicLabel);
                
                for (Project project : publicProjects) {
                    ProjectCard card = new ProjectCard(project, () -> frame.showProjectDetail(project));
                    card.setAlignmentX(Component.LEFT_ALIGNMENT);
                    projectsPanel.add(card);
                    projectsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                }
            }
        } else {
            // Jeśli nie ma zalogowanego użytkownika, pokaż wszystkie publiczne projekty
            for (Project project : container.projects) {
                if (project.getTeam() == null) {
                    ProjectCard card = new ProjectCard(project, () -> frame.showProjectDetail(project));
                    card.setAlignmentX(Component.LEFT_ALIGNMENT);
                    projectsPanel.add(card);
                    projectsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                }
            }
        }

        projectsPanel.add(Box.createVerticalGlue());
        projectsPanel.revalidate();
        projectsPanel.repaint();
    }

    private String getSortDescription() {
        return "Sortowanie aktywne";
    }

    private void resizeProjectCards() {
        // Implementacja zmiany rozmiaru
    }

    private void showAddProjectForm(ActionEvent e) {
        ProjectForm form = new ProjectForm(container, this::refreshProjects);
        form.setVisible(true);
    }
}