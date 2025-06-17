import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class RegisterView extends JPanel {
    private MainFrame mainFrame;
    private RegisterHandler registerHandler;

    private JTextField nameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JCheckBox darkModeCheckBox;

    public MainFrame getMainFrame() {
        return mainFrame;
    }

    public void setMainFrame(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    public RegisterView(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.registerHandler = new RegisterHandler(mainFrame);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Register New Account");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);

        JLabel nameLabel = new JLabel("Full Name:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        add(nameLabel, gbc);

        nameField = new JTextField(25);
        gbc.gridx = 1;
        gbc.gridy = 1;
        add(nameField, gbc);

        JLabel emailLabel = new JLabel("Email:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(emailLabel, gbc);

        emailField = new JTextField(25);
        gbc.gridx = 1;
        gbc.gridy = 2;
        add(emailField, gbc);

        JLabel passwordLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(passwordLabel, gbc);

        passwordField = new JPasswordField(25);
        gbc.gridx = 1;
        gbc.gridy = 3;
        add(passwordField, gbc);

        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        gbc.gridx = 0;
        gbc.gridy = 4;
        add(confirmPasswordLabel, gbc);

        confirmPasswordField = new JPasswordField(25);
        gbc.gridx = 1;
        gbc.gridy = 4;
        add(confirmPasswordField, gbc);

        darkModeCheckBox = new JCheckBox("Enable Dark Mode");
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        add(darkModeCheckBox, gbc);

        JButton registerButton = new JButton("Register");
        registerButton.addActionListener((ActionEvent e) -> {
            registerHandler.handleRegister(
                    nameField.getText(),
                    emailField.getText(),
                    new String(passwordField.getPassword()),
                    new String(confirmPasswordField.getPassword()),
                    darkModeCheckBox.isSelected()
            );
        });
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(registerButton, gbc);

        JButton loginLinkButton = new JButton("Already have an account? Login here.");
        loginLinkButton.setFont(new Font("Arial", Font.PLAIN, 12));
        loginLinkButton.setForeground(Color.BLUE);
        loginLinkButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginLinkButton.setBorderPainted(false);
        loginLinkButton.setContentAreaFilled(false);
        loginLinkButton.setFocusPainted(false);
        loginLinkButton.addActionListener((ActionEvent e) -> mainFrame.showLoginView());

        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(5, 10, 10, 10);
        add(loginLinkButton, gbc);
    }

    public void clearFields() {
        nameField.setText("");
        emailField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
        darkModeCheckBox.setSelected(false);
    }
}