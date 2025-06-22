import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AuthManager {
    private static final Logger LOGGER = Logger.getLogger(AuthManager.class.getName());
    private static final List<User> users = new ArrayList<>();
    private static final String USERS_DIR = "userdata/";
    private static final String USER_FILE_SUFFIX = ".user";

    private static User activeUser;

    
    static {
        initializeStorage();
        loadUsers();
    }

    private AuthManager() {
        throw new UnsupportedOperationException("Utility class");
    }

    private static void initializeStorage() {
        File dir = new File(USERS_DIR);
        if (!dir.exists() && !dir.mkdirs()) {
            LOGGER.severe(() -> "Nie można było utworzyć katalogu: " + USERS_DIR);
        }
    }

    private static void loadUsers() {
        users.clear();
        File dir = new File(USERS_DIR);
        File[] userFiles = dir.listFiles((d, name) -> name.endsWith(USER_FILE_SUFFIX));

        if (userFiles != null) {
            for (File userFile : userFiles) {
                loadUserFromFile(userFile).ifPresent(users::add);
            }
        }

        LOGGER.info(() -> String.format("Załadowano %d użytkowników.", users.size()));
    }

    private static Optional<User> loadUserFromFile(File file) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            if (obj instanceof User user && user.getEmail() != null) {
                return Optional.of(user);
            }
        } catch (IOException | ClassNotFoundException e) {
            LOGGER.severe(() -> "Błąd przy ładowaniu pliku: " + file.getAbsolutePath()); 
        }
        return Optional.empty();
    }

    private static void saveUser(User user) {
        if (user == null || user.getEmail() == null) {
            LOGGER.warning("Nie można zapisać pustego użytkownika lub adresu e-mail.");
            return;
        }

        String filename = USERS_DIR + sanitizeFilename(user.getEmail()) + USER_FILE_SUFFIX;

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(user);
            LOGGER.info(() -> String.format("Zapisano użytkownika: %s do %s", user.getName(), filename));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Błąd zapisu użytkownika: " + user.getName(), e);
        }
    }

    private static String sanitizeFilename(String input) {
        return input.replaceAll("[^a-zA-Z0-9.-]", "_");
    }

    public static User getActiveUser() {
        return activeUser;
    }

    public static void logout() {
        activeUser = null;
        LOGGER.info("Użytkownik został wylogowany.");
    }

    public static synchronized boolean login(String email, String password) {
        if (email == null || password == null) return false;

        loadUsers(); 

        return users.stream()
                .filter(user -> email.equals(user.getEmail()))
                .filter(user -> Objects.equals(user.getPasshash(), User.enhash(password)))
                .findFirst()
                .map(user -> {
                    activeUser = user;
                    LOGGER.info(() -> "Zalogowano użytkownika: " + user.getName());
                    return true;
                })
                .orElse(false);
    }

    public static synchronized String register(String name, String email, String password) {
        if (isBlank(name)) return "Imię nie może być puste.";
        if (isBlank(email)) return "Email nie może być pusty.";
        if (isBlank(password)) return "Hasło nie może być puste.";

        loadUsers();

        boolean exists = users.stream().anyMatch(u -> email.equals(u.getEmail()));
        if (exists) {
            LOGGER.warning(() -> "Rejestracja nieudana — email zajęty: " + email);
            return "Użytkownik z tym e-mailem już istnieje.";
        }

        User newUser = new User(name.trim(), email.trim(), password);
        users.add(newUser);
        saveUser(newUser);
        LOGGER.info(() -> "Zarejestrowano nowego użytkownika: " + newUser.getName());
        return null;
    }

    private static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

}
