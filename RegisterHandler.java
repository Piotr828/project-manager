import javax.swing.*;

public class RegisterHandler {
    private static final String TITLE_ERROR = "Błąd rejestracji";
    private static final String TITLE_SUCCESS = "Zarejestrowano pomyślnie";
    private static final String MESSAGE_FIELDS_REQUIRED = "Wszystkie pola są wymagane.";
    private static final String MESSAGE_PASSWORDS_DIFFER = "Hasła są różne.";
    private static final String MESSAGE_INVALID_EMAIL = "Błędny adres e-mail.";
    private static final String MESSAGE_INVALID_PASSWORD = "Hasło musi posiadać:\n- Co najmniej 8 znaków\n- Wielką literę\n- Cyfrę\n- Znak specjalny";
    private static final String MESSAGE_SUCCESS = "Rejestracja udana! Proszę się zalogować.";
    private static final String MESSAGE_FAILED_PREFIX = "Rejestracja nieudana!: ";

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
            showError(MESSAGE_FIELDS_REQUIRED);
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError(MESSAGE_PASSWORDS_DIFFER);
            return;
        }

        if (!User.isValidEmail(email)) {
            showError(MESSAGE_INVALID_EMAIL);
            return;
        }

        if (!User.isValidPassword(password)) {
            showError(MESSAGE_INVALID_PASSWORD);
            return;
        }

        String errorMessage = AuthManager.register(name, email, password);

        if (errorMessage == null) {
            JOptionPane.showMessageDialog(mainFrame, MESSAGE_SUCCESS, TITLE_SUCCESS, JOptionPane.INFORMATION_MESSAGE);
            mainFrame.showLoginView();
        } else {
            showError(MESSAGE_FAILED_PREFIX + errorMessage);
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(mainFrame, message, TITLE_ERROR, JOptionPane.ERROR_MESSAGE);
    }
}
