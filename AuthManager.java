import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AuthManager {
    private static final Logger LOGGER = Logger.getLogger(AuthManager.class.getName());
    private static User activeUser;
    private static List<User> users = new ArrayList<>();
    private static final String USERS_DIR = "userdata/"; // Directory to store user files
    private static final String USER_FILE_SUFFIX = ".user";

    static {
        File dir = new File(USERS_DIR);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                LOGGER.severe("Could not create user data directory: " + USERS_DIR);
            }
        }
        loadUsers();
    }

    private static void loadUsers() {
        users.clear(); // Clear existing list before loading
        File dir = new File(USERS_DIR);
        File[] userFiles = dir.listFiles((d, name) -> name.endsWith(USER_FILE_SUFFIX));

        if (userFiles != null) {
            for (File userFile : userFiles) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(userFile))) {
                    User user = (User) ois.readObject();
                    if (user != null) {
                        users.add(user);
                    }
                } catch (IOException | ClassNotFoundException e) {
                    LOGGER.log(Level.SEVERE, "Error loading user from " + userFile.getAbsolutePath(), e);
                }
            }
        }
        LOGGER.info("Loaded " + users.size() + " users.");
    }

    private static void saveUser(User user) {
        if (user == null || user.email == null) {
            LOGGER.warning("Attempted to save null user or user with null email.");
            return;
        }
        // Sanitize email to use as filename, or use a dedicated user ID
        String filenameBase = user.email.replaceAll("[^a-zA-Z0-9.-]", "_");
        String filename = USERS_DIR + filenameBase + USER_FILE_SUFFIX;
        
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(user);
            LOGGER.info("Saved user: " + user.name + " to " + filename);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error saving user " + user.name, e);
        }
    }

    public static User getActiveUser() {
        return activeUser;
    }

    public static void setActiveUser(User user) {
        activeUser = user;
    }

    public static void logout() {
        activeUser = null;
        LOGGER.info("User logged out.");
    }

    public static synchronized boolean login(String email, String password) {
        if (email == null || password == null) return false;
        loadUsers(); // Refresh users list from files in case of external changes / multi-instance
        Optional<User> userOpt = users.stream()
                                      .filter(u -> u.email != null && u.email.equals(email))
                                      .findFirst();
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            String hashedPassword = User.enhash(password);
            if (user.passhash != null && user.passhash.equals(hashedPassword)) {
                activeUser = user;
                LOGGER.info("User logged in: " + user.name);
                return true;
            } else {
                LOGGER.warning("Login failed for email (password mismatch): " + email);
            }
        } else {
            LOGGER.warning("Login failed for email (user not found): " + email);
        }
        return false;
    }

    // Returns error message String or null if successful
    public static synchronized String register(String name, String email, String password, boolean darkMode) {
        if (name == null || name.trim().isEmpty()) return "Name cannot be empty.";
        if (email == null || email.trim().isEmpty()) return "Email cannot be empty.";
        if (password == null || password.isEmpty()) return "Password cannot be empty.";

        loadUsers(); // Refresh users list

        if (users.stream().anyMatch(u -> u.email != null && u.email.equals(email))) {
            LOGGER.warning("Registration failed (email exists): " + email);
            return "User with this email already exists.";
        }

        User newUser = new User(name.trim(), email.trim(), password, darkMode); // Constructor hashes password
        users.add(newUser);
        saveUser(newUser);
        LOGGER.info("User registered: " + newUser.name);
        return null; // Success
    }
}