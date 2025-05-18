import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

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

        JButton addButton = new JButton("+ Nowy projekt");
        addButton.addActionListener(this::showAddProjectForm);
        header.add(addButton, BorderLayout.EAST);
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

    private void refreshProjects() {
        projectsPanel.removeAll();
        container.sortProjects();

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

    private void resizeProjectCards() {
        int width = projectsPanel.getWidth();
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