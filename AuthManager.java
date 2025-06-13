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
    private static final String USERS_DIR = "userdata/"; 
    private static final String USER_FILE_SUFFIX = ".user";

    static {
        File dir = new File(USERS_DIR);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                LOGGER.severe("Nie można utworzyć katalogu danych użytkowników: " + USERS_DIR);
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
                    LOGGER.log(Level.SEVERE, "Błąd podczas ładowania użytkownika z " + userFile.getAbsolutePath(), e);
                }
            }
        }
        LOGGER.info("Załadowano " + users.size() + " użytkowników.");
    }

    private static void saveUser(User user) {
        if (user == null || user.email == null) {
            LOGGER.warning("Próba zapisania użytkownika null lub użytkownika z emailem null.");
            return;
        }
        String filenameBase = user.email.replaceAll("[^a-zA-Z0-9.-]", "_");
        String filename = USERS_DIR + filenameBase + USER_FILE_SUFFIX;
        
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(user);
            LOGGER.info("Zapisano użytkownika: " + user.name + " do " + filename);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Błąd podczas zapisywania użytkownika " + user.name, e);
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
        LOGGER.info("Użytkownik wylogowany.");
    }

    public static synchronized boolean login(String email, String password) {
        if (email == null || password == null) return false;
        loadUsers();
        Optional<User> userOpt = users.stream()
                                      .filter(u -> u.email != null && u.email.equals(email))
                                      .findFirst();
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            String hashedPassword = User.enhash(password);
            if (user.passhash != null && user.passhash.equals(hashedPassword)) {
                activeUser = user;
                LOGGER.info("Użytkownik zalogowany: " + user.name);
                return true;
            } else {
                LOGGER.warning("Logowanie nie powiodło się dla emaila (niezgodność hasła): " + email);
            }
        } else {
            LOGGER.warning("Logowanie nie powiodło się dla emaila (użytkownik nie znaleziony): " + email);
        }
        return false;
    }

    public static synchronized String register(String name, String email, String password, boolean darkMode) {
        if (name == null || name.trim().isEmpty()) return "Nazwa nie może być pusta.";
        if (email == null || email.trim().isEmpty()) return "Email nie może być pusty.";
        if (password == null || password.isEmpty()) return "Hasło nie może być puste.";

        loadUsers(); 

        if (users.stream().anyMatch(u -> u.email != null && u.email.equals(email))) {
            LOGGER.warning("Rejestracja nie powiodła się (email istnieje): " + email);
            return "Użytkownik o tym adresie email już istnieje.";
        }

        User newUser = new User(name.trim(), email.trim(), password, darkMode); 
        users.add(newUser);
        saveUser(newUser);
        LOGGER.info("Użytkownik zarejestrowany: " + newUser.name);
        return null; 
    }
}