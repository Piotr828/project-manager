import javax.swing.JOptionPane;

public class LoginHandler {

    private MainFrame mainFrame;

    public LoginHandler(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    public void handleLogin(String email, String password) {
        if (email.trim().isEmpty() || password.trim().isEmpty()) {
            JOptionPane.showMessageDialog(mainFrame, "Login i hasło nie mogą być puste.", "Login Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean success = AuthManager.login(email, password);

        if (success) {
            mainFrame.showDashboard();
            mainFrame.updateLoginStatus(); 
        } else {
            JOptionPane.showMessageDialog(mainFrame, "Niepoprawny email lub hasło.", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}