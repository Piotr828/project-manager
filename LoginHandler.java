import javax.swing.JOptionPane;

public class LoginHandler {

    private MainFrame mainFrame;

    public LoginHandler(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    public MainFrame getMainFrame() {
        return mainFrame;
    }

    public void setMainFrame(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    public void handleLogin(String email, String password) {
        if (email.trim().isEmpty() || password.trim().isEmpty()) {
            JOptionPane.showMessageDialog(mainFrame, "Email and password cannot be empty.", "Login Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean success = AuthManager.login(email, password);

        if (success) {
            mainFrame.showDashboard();
            mainFrame.updateLoginStatus(); // To update menu etc.
        } else {
            JOptionPane.showMessageDialog(mainFrame, "Invalid email or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}