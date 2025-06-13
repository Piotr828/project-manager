import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginView extends JPanel {
    private MainFrame mainFrame;
    private LoginHandler loginHandler;

    private JTextField emailField;
    private JPasswordField passwordField;

    public LoginView(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.loginHandler = new LoginHandler(mainFrame);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Login to Project Manager");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);

        JLabel emailLabel = new JLabel("Email:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        add(emailLabel, gbc);

        emailField = new JTextField(25);
        gbc.gridx = 1;
        gbc.gridy = 1;
        add(emailField, gbc);

        JLabel passwordLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(passwordLabel, gbc);

        passwordField = new JPasswordField(25);
        gbc.gridx = 1;
        gbc.gridy = 2;
        add(passwordField, gbc);
        
        // Login Button
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener((ActionEvent e) -> {
            loginHandler.handleLogin(emailField.getText(), new String(passwordField.getPassword()));
        });
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(loginButton, gbc);

        // Register Link/Button
        JButton registerLinkButton = new JButton("Don't have an account? Register here.");
        registerLinkButton.setFont(new Font("Arial", Font.PLAIN, 12));
        registerLinkButton.setForeground(Color.BLUE);
        registerLinkButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerLinkButton.setBorderPainted(false);
        registerLinkButton.setContentAreaFilled(false);
        registerLinkButton.setFocusPainted(false);
        registerLinkButton.addActionListener((ActionEvent e) -> mainFrame.showRegisterView());
        
        gbc.gridx = 0;
        gbc.gridy = 4; // Next row
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(5, 10, 10, 10); // Smaller top inset
        add(registerLinkButton, gbc);
    }
    
    public void clearFields() {
        emailField.setText("");
        passwordField.setText("");
    }
}