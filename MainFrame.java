import java.awt.*;
import javax.swing.*;

public class MainFrame extends JFrame {
    private final Container container;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    
    public MainFrame(Container container) {
        this.container=container;
        setTitle("Project Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        setupMenuBar();
        
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        DashboardView dashboard = new DashboardView(container, this);
        mainPanel.add(dashboard, "dashboard");
        
        add(mainPanel);
        setVisible(true);
    }

    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        JMenu settingsMenu = new JMenu("Ustawienia");
        JMenuItem teamsItem = new JMenuItem("Zarządzanie zespołami");
        teamsItem.addActionListener(e -> showTeamSettings());
        settingsMenu.add(teamsItem);
        
        menuBar.add(settingsMenu);
        setJMenuBar(menuBar);
    }
    
    private void showTeamSettings() {
        User currentUser = AuthManager.getActiveUser();
        if (currentUser != null) {
            SettingsWindow settings = new SettingsWindow(currentUser, container);
            settings.setVisible(true);
        }
    }
    
    public void showDashboard() {
        cardLayout.show(mainPanel, "dashboard");
    }
    
    public void showProjectDetail(Project project) {
        ProjectDetailView detailView = new ProjectDetailView(project, this, container);
        mainPanel.add(detailView, "projectDetail");
        cardLayout.show(mainPanel, "projectDetail");
    }
}