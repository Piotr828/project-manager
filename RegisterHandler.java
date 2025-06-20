import javax.swing.JOptionPane;

public class RegisterHandler {
    private MainFrame mainFrame;

    public RegisterHandler(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    public MainFrame getMainFrame() {
        return mainFrame;
    }

    public void setMainFrame(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    public void handleRegister(String name, String email, String password, String confirmPassword) {
        if (name.trim().isEmpty() || email.trim().isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(mainFrame, "Wszystkie pola są wymagane.", "Błąd rejestracji", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(mainFrame, "Hasła są różne.", "Błąd rejestracji", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!User.isValidEmail(email)) {
            JOptionPane.showMessageDialog(mainFrame, "Błędny adres e-mail.", "Błąd rejestracji", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!User.isValidPassword(password)) {
            JOptionPane.showMessageDialog(mainFrame, "Hasło musi posiadać:\n- Co najmniej 8 znaków\n- Wielką literę\n- Cyfrę\n- Znak specjalny", "Błąd rejestracji", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String errorMessage = AuthManager.register(name, email, password);

        if (errorMessage == null) {
            JOptionPane.showMessageDialog(mainFrame, "Rejestracja udana! Proszę się zalogować..", "Zarejestrowano pomyślnie", JOptionPane.INFORMATION_MESSAGE);
            mainFrame.showLoginView();
        } else {
            JOptionPane.showMessageDialog(mainFrame, "Registration nieudana!: " + errorMessage, "Błąd rejestracji", JOptionPane.ERROR_MESSAGE);
        }
    }
}