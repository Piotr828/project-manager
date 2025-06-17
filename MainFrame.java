import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;

public class MainFrame extends JFrame {
    private final Container container;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private DashboardView dashboardView;
    private LoginView loginView;
    private RegisterView registerView;
    
    private JMenuItem teamsItem;
    private JMenuItem logoutItem;
    private JMenu fileMenu; 
    
    public MainFrame(Container container) {
        this.container=container;
        setTitle("Project Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        setupMenuBar(); 
        
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        loginView = new LoginView(this);
        registerView = new RegisterView(this);


        mainPanel.add(loginView, "login");
        mainPanel.add(registerView, "register");

        add(mainPanel);
    
        DashboardView dashboard = new DashboardView(container, this);
        mainPanel.add(dashboard, "dashboard");
        if (AuthManager.getActiveUser() != null) {
            showDashboard();
        } else {
            showLoginView();
        }
        updateLoginStatus();
        add(mainPanel);
        setVisible(true);
    }

    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        fileMenu = new JMenu("File");
        logoutItem = new JMenuItem("Logout");
        logoutItem.addActionListener((ActionEvent e) -> {
            AuthManager.logout();
            showLoginView();
            updateLoginStatus();
        });
        fileMenu.add(logoutItem);
        menuBar.add(fileMenu);

        JMenu settingsMenu = new JMenu("Ustawienia");
        JMenuItem teamsItem = new JMenuItem("Zarządzanie zespołami");
        teamsItem.addActionListener(e -> showTeamSettings());
        settingsMenu.add(teamsItem);
        
        menuBar.add(settingsMenu);
        setJMenuBar(menuBar);
    }

    public void updateLoginStatus() {
        boolean isLoggedIn = AuthManager.getActiveUser() != null;

        if (teamsItem != null) {
            teamsItem.setEnabled(isLoggedIn);
        }
        if (logoutItem != null) {
            logoutItem.setEnabled(isLoggedIn);
        }
        if (fileMenu != null) {
            fileMenu.setEnabled(isLoggedIn); 
        }


        if (isLoggedIn && dashboardView != null) {
            dashboardView.refreshProjects();
        }
    }
    
    private void showTeamSettings() {
        User currentUser = AuthManager.getActiveUser();
        if (currentUser != null) {
            SettingsWindow settings = new SettingsWindow(currentUser, container);
            settings.setVisible(true);
        }
    }

    public void showLoginView() {
        if (loginView == null) { 
            loginView = new LoginView(this);
            mainPanel.add(loginView, "login");
        }
        loginView.clearFields();
        cardLayout.show(mainPanel, "login");
    }

    public void showRegisterView() {
        if (registerView == null) { 
            registerView = new RegisterView(this);
            mainPanel.add(registerView, "register");
        }
        registerView.clearFields();
        cardLayout.show(mainPanel, "register");
    }
    
    public void showDashboard() {
        if (AuthManager.getActiveUser() == null) {
            showLoginView(); 
            return;
        }
        
        if (dashboardView == null) { 
            dashboardView = new DashboardView(container, this);
            mainPanel.add(dashboardView, "dashboard");
        } else {
            dashboardView.refreshProjects(); 
        }
        cardLayout.show(mainPanel, "dashboard");
         updateLoginStatus(); 
    }
    
    public void showProjectDetail(Project project) {
        if (AuthManager.getActiveUser() == null) {
           showLoginView(); 
           return;
       }
       ProjectDetailView detailView = new ProjectDetailView(project, this, container);
       String cardName = "projectDetail_" + project.name.replaceAll("\\s+", ""); 
       mainPanel.add(detailView, cardName);
       cardLayout.show(mainPanel, cardName);
   }
}