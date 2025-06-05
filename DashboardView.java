import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

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
        sortPanel.add(sortCombo);

        JCheckBox reverseCheck = new JCheckBox("Odwrotnie");
        reverseCheck.setSelected(container.sortby <0);
        sortPanel.add(reverseCheck);

        JButton sortButton = new JButton("Sortuj");
        sortButton.addActionListener(e -> {
            int selected = sortCombo.getSelectedIndex() + 1;
            container.sortby = (byte) (reverseCheck.isSelected() ? -(selected + 1) : (selected + 1));
            refreshProjects();
        });
        sortPanel.add(sortButton);

        rightPanel.add(sortPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0)); // Panel na przyciski

        JButton addButton = new JButton("+ Nowy projekt");
        addButton.addActionListener(this::showAddProjectForm);
        buttonPanel.add(addButton);

        JButton exportButton = new JButton("Eksportuj do ICS");
        exportButton.addActionListener(this::exportICS);
        buttonPanel.add(exportButton);

        rightPanel.add(buttonPanel, BorderLayout.SOUTH);
        header.add(rightPanel, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Panel projektów
        projectsPanel = new JPanel();
        projectsPanel.setLayout(new BoxLayout(projectsPanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(projectsPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // Listener do zmiany rozmiaru i odświeżania kart
        scrollPane.getViewport().addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                SwingUtilities.invokeLater(() -> resizeProjectCards());
            }
        });

        refreshProjects();
    }
private void exportICS(ActionEvent e) {
    try {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Zapisz plik ICS");
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            if (!filePath.endsWith(".ics")) {
                filePath += ".ics";
            }

            // Generujemy ICS
            Calendar calendar = new Calendar(container);
            calendar.saveToFile(filePath);

            JOptionPane.showMessageDialog(this, "Plik ICS zapisano jako:\n" + filePath, "Eksport zakończony", JOptionPane.INFORMATION_MESSAGE);
        }
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Błąd podczas eksportu: " + ex.getMessage(), "Błąd", JOptionPane.ERROR_MESSAGE);
    }
}

    private void refreshProjects() {
        projectsPanel.removeAll();
        
        // Dodajemy informację o sortowaniu
        JLabel sortInfo = new JLabel(getSortDescription());
        sortInfo.setFont(new Font("Arial", Font.ITALIC, 12));
        sortInfo.setBorder(BorderFactory.createEmptyBorder(0, 5, 10, 0));
        projectsPanel.add(sortInfo);
        
        for (Project project : container.projects) {
            project.calculatePredict();
            ProjectCard card = new ProjectCard(project, () -> frame.showProjectDetail(project));
            card.setAlignmentX(Component.LEFT_ALIGNMENT);
            projectsPanel.add(card);
            projectsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        revalidate();
        repaint();
    }

    private String getSortDescription() {
        String[] sortOptions = {
            "Nazwa (A-Z)", 
            "Data rozpoczęcia", 
            "Termin", 
            "Suma trudności", 
            "Postęp", 
            "Przewidywane zakończenie", 
            "Opóźnienie", 
            "Kolor"
        };
        
        int absSort = Math.abs(container.sortby) - 1; // -1 bo wartości 1-8
        if (absSort < 0 || absSort >= sortOptions.length) {
            return "Sortowanie: domyślne";
        }
        
        String direction = container.sortby < 0 ? "malejąco" : "rosnąco";
        return "Sortowanie: " + sortOptions[absSort] + " (" + direction + ")";
    }

    private void resizeProjectCards() {
        int width = projectsPanel.getWidth() - 30; // Margines na scrollbar
        for (Component comp : projectsPanel.getComponents()) {
            if (comp instanceof ProjectCard) {
                ((ProjectCard) comp).setMaximumSize(new Dimension(width, 220));
            }
        }
    }

    private void showAddProjectForm(ActionEvent e) {
        ProjectForm form = new ProjectForm(container, this::refreshProjects);
        form.setVisible(true);
    }
}