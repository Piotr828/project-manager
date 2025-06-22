import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DashboardView extends JPanel {
    public final Container container;
    private final MainFrame frame;
    private final JPanel projectsPanel;

    public DashboardView(Container container, MainFrame frame) {
        this.container = container;
        this.frame = frame;
        this.projectsPanel = createProjectsPanel();

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createScrollPane(projectsPanel), BorderLayout.CENTER);
        addProjectsResizeListener();
        refreshProjects();
    }

    private JPanel createProjectsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        return panel;
    }

    private JScrollPane createScrollPane(JPanel content) {
        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        return scrollPane;
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());

        JLabel title = new JLabel("Twoje projekty");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        header.add(title, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(new SortPanel(), BorderLayout.NORTH);
        rightPanel.add(new ButtonPanel(), BorderLayout.SOUTH);

        header.add(rightPanel, BorderLayout.EAST);
        return header;
    }

    private void exportICS(ActionEvent e) {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Zapisz plik ICS");
            if (fileChooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            if (!filePath.endsWith(".ics")) filePath += ".ics";

            new Calendar(container).saveToFile(filePath);

            JOptionPane.showMessageDialog(this, "Plik ICS zapisano jako:\n" + filePath,
                    "Eksport zakończony", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Błąd podczas eksportu: " + ex.getMessage(),
                    "Błąd", JOptionPane.ERROR_MESSAGE);
        }
    }

    void refreshProjects() {
        projectsPanel.removeAll();
        container.sortProjects();
        User currentUser = AuthManager.getActiveUser();
        List<Project> ownTeamProjects = new ArrayList<>();
        List<Project> publicProjects = new ArrayList<>();

        for (Project project : container.getProjects()) {
            Team team = project.getTeam();
            if (team != null && team.isMember(currentUser)) {
                ownTeamProjects.add(project);
            } else if (team == null) {
                publicProjects.add(project);
            }
        }

        Comparator<Project> comparator = container.getProjectComparator();
        if (comparator != null) {
            ownTeamProjects.sort(comparator);
            publicProjects.sort(comparator);
        }


        projectsPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        if (!ownTeamProjects.isEmpty()) {
            for (Project project : ownTeamProjects) {
                project.calculatePredict();
                ProjectCard card = new ProjectCard(project, () -> frame.showProjectDetail(project));
                card.setAlignmentX(Component.LEFT_ALIGNMENT);
                projectsPanel.add(card);
                projectsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }

        if (!ownTeamProjects.isEmpty() && !publicProjects.isEmpty()) {

            JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
            separator.setForeground(new Color(180, 180, 180));
            separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
            projectsPanel.add(separator);
            projectsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        if (!publicProjects.isEmpty()) {
            for (Project project : publicProjects) {
                project.calculatePredict();
                ProjectCard card = new ProjectCard(project, () -> frame.showProjectDetail(project));
                card.setAlignmentX(Component.LEFT_ALIGNMENT);
                projectsPanel.add(card);
                projectsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }

        revalidate();
        repaint();
    }

    private void resizeProjectCards() {
        int width = projectsPanel.getWidth() - 30;
        for (Component comp : projectsPanel.getComponents()) {
            if (comp instanceof ProjectCard card) {
                card.setMaximumSize(new Dimension(width, 220));
            }
        }
    }

    private void addProjectsResizeListener() {
        ComponentAdapter listener = new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                SwingUtilities.invokeLater(DashboardView.this::resizeProjectCards);
            }
        };
        for (Component c : getComponents()) {
            if (c instanceof JScrollPane scroll) {
                scroll.getViewport().addComponentListener(listener);
            }
        }
    }

    private void showAddProjectForm(ActionEvent e) {
        ProjectForm form = new ProjectForm(container, this::refreshProjects);
        form.setVisible(true);
    }

    private class SortPanel extends JPanel {
        public SortPanel() {
            setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 0));
            setOpaque(false);

            JLabel sortLabel = new JLabel("Sortuj wg: ");
            add(sortLabel);

            String[] options = {
                    "Nazwy (A-Z)", "Data rozpoczęcia", "Terminu",
                    "Trudności", "Postępu", "Przewidywania",
                    "Opóźnienia", "Koloru"
            };
            JComboBox<String> sortCombo = new JComboBox<>(options);
            sortCombo.setSelectedIndex(Math.abs(Container.getSortby()) - 1);
            add(sortCombo);

            JCheckBox reverseCheck = new JCheckBox("Odwrotnie");
            reverseCheck.setSelected(Container.getSortby() < 0);
            add(reverseCheck);

            JButton sortButton = new JButton("Sortuj");
            sortButton.addActionListener(e -> {
                int selected = sortCombo.getSelectedIndex() + 1;
                Container.setSortby((byte) (reverseCheck.isSelected() ? -selected : selected));
                refreshProjects();
            });

            add(sortButton);
        }
    }

    private class ButtonPanel extends JPanel {
        public ButtonPanel() {
            setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 0));

            JButton addButton = new JButton("+ Nowy projekt");
            addButton.addActionListener(DashboardView.this::showAddProjectForm);
            add(addButton);

            JButton exportButton = new JButton("Eksportuj do ICS");
            exportButton.addActionListener(DashboardView.this::exportICS);
            add(exportButton);

            JButton reportButton = new JButton("Generuj raport");
            reportButton.addActionListener(e -> new ReportGenerator().generate());
            add(reportButton);
        }
    }

    private class ReportGenerator {
        public void generate() {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Zapisz raport projektów");

            if (fileChooser.showSaveDialog(DashboardView.this) != JFileChooser.APPROVE_OPTION) return;

            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            if (!filePath.endsWith(".txt")) filePath += ".txt";

            List<Project> projects = container.getProjects();
            int totalProjects = projects.size();
            int totalTasks = 0;
            int completedTasks = 0;
            int delayCount = 0;
            int delaySum = 0;
            int maxDelay = 0;
            int minDelay = Integer.MAX_VALUE;

            for (Project p : projects) {
                for (Task t : p.getTasks()) {
                    totalTasks++;
                    if (t.status) completedTasks++;
                }

                if (p.getDelay() >= 0) {
                    delayCount++;
                    delaySum += p.getDelay();
                    maxDelay = Math.max(maxDelay, p.getDelay());
                    minDelay = Math.min(minDelay, p.getDelay());
                }
            }

            double avgProgress = projects.stream().mapToInt(Project::progress).average().orElse(0);
            double avgDelay = delayCount > 0 ? (double) delaySum / delayCount : 0;
            if (delayCount == 0) minDelay = 0;

            try (FileWriter writer = new FileWriter(filePath)) {
                writer.write("RAPORT PROJEKTÓW\n=========================\n");
                writer.write("Liczba projektów: " + totalProjects + "\n");
                writer.write("Łączna liczba zadań: " + totalTasks + "\n");
                writer.write("Ukończone zadania: " + completedTasks + " (" +
                        (totalTasks > 0 ? (completedTasks * 100 / totalTasks) : 0) + "%)\n");
                writer.write("Średni postęp projektów: " + String.format("%.2f", avgProgress) + "%\n");
                writer.write("Średnie opóźnienie: " + String.format("%.2f", avgDelay) + " dni\n");
                writer.write("Największe opóźnienie: " + maxDelay + " dni\n");
                writer.write("Najmniejsze opóźnienie: " + minDelay + " dni\n");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(DashboardView.this,
                        "Błąd podczas zapisu raportu: " + ex.getMessage(),
                        "Błąd", JOptionPane.ERROR_MESSAGE);
            }

            JOptionPane.showMessageDialog(DashboardView.this,
                    "Raport zapisano jako:\n" + filePath,
                    "Raport wygenerowany", JOptionPane.INFORMATION_MESSAGE);
        }
    }

}
