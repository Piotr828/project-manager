import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

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
        this.container = container;
        setTitle("Project Manager");
        setIconImage(new ImageIcon("Icon.png").getImage());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setMinimumSize(new Dimension(800, 600));
        setLocationRelativeTo(null);

        setupMenuBar();

        setCardLayout(new CardLayout());
        setMainPanel(new JPanel(getCardLayout()));

        setLoginView(new LoginView(this));
        setRegisterView(new RegisterView(this));


        getMainPanel().add(getLoginView(), "login");
        getMainPanel().add(getRegisterView(), "register");

        add(getMainPanel());

        DashboardView dashboard = new DashboardView(container, this);
        getMainPanel().add(dashboard, "dashboard");
        if (AuthManager.getActiveUser() != null) {
            showDashboard();
        } else {
            showLoginView();
        }
        updateLoginStatus();
        add(getMainPanel());
        setVisible(true);
    }

    public Container getContainer() {
        return container;
    }

    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        setFileMenu(new JMenu("File"));
        setLogoutItem(new JMenuItem("Logout"));
        getLogoutItem().addActionListener((ActionEvent e) -> {
            AuthManager.logout();
            showLoginView();
            updateLoginStatus();
        });
        getFileMenu().add(getLogoutItem());
        menuBar.add(getFileMenu());

        JMenu settingsMenu = new JMenu("Ustawienia");
        JMenuItem jMenuItem = new JMenuItem("Zarządzanie zespołami");
        jMenuItem.addActionListener(e -> showTeamSettings());
        settingsMenu.add(jMenuItem);

        menuBar.add(settingsMenu);
        setJMenuBar(menuBar);
    }

    public void updateLoginStatus() {
        boolean isLoggedIn = AuthManager.getActiveUser() != null;

        if (getTeamsItem() != null) {
            getTeamsItem().setEnabled(isLoggedIn);
        }
        if (getLogoutItem() != null) {
            getLogoutItem().setEnabled(isLoggedIn);
        }
        if (getFileMenu() != null) {
            getFileMenu().setEnabled(isLoggedIn);
        }


        if (isLoggedIn && getDashboardView() != null) {
            getDashboardView().refreshProjects();
        }
    }

    private void showTeamSettings() {
        User currentUser = AuthManager.getActiveUser();
        if (currentUser != null) {
            SettingsWindow settings = new SettingsWindow(currentUser, getContainer());
            settings.setVisible(true);
        }
    }

    public void showLoginView() {
        if (getLoginView() == null) {
            setLoginView(new LoginView(this));
            getMainPanel().add(getLoginView(), "login");
        }
        getLoginView().clearFields();
        getCardLayout().show(getMainPanel(), "login");
    }

    public void showRegisterView() {
        if (getRegisterView() == null) {
            setRegisterView(new RegisterView(this));
            getMainPanel().add(getRegisterView(), "register");
        }
        getRegisterView().clearFields();
        getCardLayout().show(getMainPanel(), "register");
    }

    public void showDashboard() {
        if (AuthManager.getActiveUser() == null) {
            showLoginView();
            return;
        }

        if (getDashboardView() == null) {
            setDashboardView(new DashboardView(getContainer(), this));
            getMainPanel().add(getDashboardView(), "dashboard");
        } else {
            getDashboardView().refreshProjects();
        }
        getCardLayout().show(getMainPanel(), "dashboard");
        updateLoginStatus();
    }

    public void showProjectDetail(Project project) {
        if (AuthManager.getActiveUser() == null) {
            showLoginView();
            return;
        }
        ProjectDetailView detailView = new ProjectDetailView(project, this, getContainer());
        String cardName = "projectDetail_" + project.getName().replaceAll("\\s+", "");
        getMainPanel().add(detailView, cardName);
        getCardLayout().show(getMainPanel(), cardName);
    }

    public CardLayout getCardLayout() {
        return cardLayout;
    }

    public void setCardLayout(CardLayout cardLayout) {
        this.cardLayout = cardLayout;
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public void setMainPanel(JPanel mainPanel) {
        this.mainPanel = mainPanel;
    }

    public DashboardView getDashboardView() {
        return dashboardView;
    }

    public void setDashboardView(DashboardView dashboardView) {
        this.dashboardView = dashboardView;
    }

    public LoginView getLoginView() {
        return loginView;
    }

    public void setLoginView(LoginView loginView) {
        this.loginView = loginView;
    }

    public RegisterView getRegisterView() {
        return registerView;
    }

    public void setRegisterView(RegisterView registerView) {
        this.registerView = registerView;
    }

    public JMenuItem getTeamsItem() {
        return teamsItem;
    }

    public void setTeamsItem(JMenuItem teamsItem) {
        this.teamsItem = teamsItem;
    }

    public JMenuItem getLogoutItem() {
        return logoutItem;
    }

    public void setLogoutItem(JMenuItem logoutItem) {
        this.logoutItem = logoutItem;
    }

    public JMenu getFileMenu() {
        return fileMenu;
    }

    public void setFileMenu(JMenu fileMenu) {
        this.fileMenu = fileMenu;
    }
}