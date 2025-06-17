import javax.swing.JOptionPane;

public class RegisterHandler {
    private MainFrame mainFrame;

    public RegisterHandler(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    public void handleRegister(String name, String email, String password, String confirmPassword, boolean darkMode) {
        if (name.trim().isEmpty() || email.trim().isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(mainFrame, "All fields are required.", "Registration Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(mainFrame, "Passwords do not match.", "Registration Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!User.isValidEmail(email)) {
            JOptionPane.showMessageDialog(mainFrame, "Invalid email format.", "Registration Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!User.isValidPassword(password)) {
            JOptionPane.showMessageDialog(mainFrame, "Password does not meet criteria:\n- At least 8 characters\n- One uppercase letter\n- One digit\n- One special character", "Registration Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String errorMessage = AuthManager.register(name, email, password, darkMode);

        if (errorMessage == null) {
            JOptionPane.showMessageDialog(mainFrame, "Registration successful! Please login.", "Registration Success", JOptionPane.INFORMATION_MESSAGE);
            mainFrame.showLoginView();
        } else {
            JOptionPane.showMessageDialog(mainFrame, "Registration failed: " + errorMessage, "Registration Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}