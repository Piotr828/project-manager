import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginView extends JPanel {
    private MainFrame mainFrame;
    private LoginHandler loginHandler;

    private JTextField emailField;
    private JPasswordField passwordField;

    public MainFrame getMainFrame() {
        return mainFrame;
    }

    public void setMainFrame(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    public LoginView(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.loginHandler = new LoginHandler(mainFrame);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Tytuł
        JLabel titleLabel = new JLabel("Zaloguj się");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);

        // Email
        JLabel emailLabel = new JLabel("Email:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        add(emailLabel, gbc);

        emailField = new JTextField(25);
        gbc.gridx = 1;
        gbc.gridy = 1;
        add(emailField, gbc);

        // Hasło
        JLabel passwordLabel = new JLabel("Hasło:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(passwordLabel, gbc);

        passwordField = new JPasswordField(25);
        gbc.gridx = 1;
        gbc.gridy = 2;
        add(passwordField, gbc);

        // Przycisk logowania
        JButton loginButton = new JButton("Zaloguj");
        loginButton.addActionListener((ActionEvent e) -> {
            loginHandler.handleLogin(
                emailField.getText(),
                new String(passwordField.getPassword())
            );
        });
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(loginButton, gbc);

        // Link do rejestracji
        JButton registerLinkButton = new JButton("Nie masz konta? Zarejestruj się");
        registerLinkButton.setFont(new Font("Arial", Font.PLAIN, 12));
        registerLinkButton.setForeground(Color.BLUE);
        registerLinkButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerLinkButton.setBorderPainted(false);
        registerLinkButton.setContentAreaFilled(false);
        registerLinkButton.setFocusPainted(false);
        registerLinkButton.addActionListener((ActionEvent e) -> mainFrame.showRegisterView());

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(5, 10, 10, 10);
        add(registerLinkButton, gbc);
    }

    public void clearFields() {
        emailField.setText("");
        passwordField.setText("");
    }
}