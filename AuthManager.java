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
                LOGGER.severe("Nie można było zapisać danych: " + USERS_DIR);
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
                    LOGGER.log(Level.SEVERE, "Błąd pobierania z " + userFile.getAbsolutePath(), e);
                }
            }
        }
        LOGGER.info("Loaded " + users.size() + " users.");
    }

    private static void saveUser(User user) {
        if (user == null || user.email == null) {
            LOGGER.warning("Nie można zapisac pustych danych.");
            return;
        }
        // Sanitize email to use as filename, or use a dedicated user ID
        String filenameBase = user.email.replaceAll("[^a-zA-Z0-9.-]", "_");
        String filename = USERS_DIR + filenameBase + USER_FILE_SUFFIX;
        
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(user);
            LOGGER.info("Zapisano użytkownika: " + user.name + " to " + filename);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Błąd zapisu użytkownika " + user.name, e);
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
        LOGGER.info("Wylogowano użytkownika.");
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
                LOGGER.info("Zalogowany użytkownik: " + user.name);
                return true;
            }
        } 
        return false;
    }

    // Returns error message String or null if successful
    public static synchronized String register(String name, String email, String password) {
        if (name == null || name.trim().isEmpty()) return "Imię nie może być puste.";
        if (email == null || email.trim().isEmpty()) return "Email nie może być pusty.";
        if (password == null || password.isEmpty()) return "Hasło nie może być puste.";

        loadUsers(); // Refresh users list

        if (users.stream().anyMatch(u -> u.email != null && u.email.equals(email))) {
            LOGGER.warning("Rejestracja nieudana (email zajęty): " + email);
            return "Użytkownik z tym e-mailem już istnieje.";
        }

        User newUser = new User(name.trim(), email.trim(), password); // Constructor hashes password
        users.add(newUser);
        saveUser(newUser);
        LOGGER.info("Zarejestrowano: " + newUser.name);
        return null; // Success
    }
}