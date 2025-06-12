import javax.swing.JFrame;

public class AuthManager {
    private static User activeUser;
    
    public static User getActiveUser() {
        return activeUser;
    }
    
    public static void setActiveUser(User user) {
        activeUser = user;
    }
    
    public static boolean isLoggedIn() {
        return activeUser != null;
    }
    
    public static void showLoginDialog(JFrame parent, Runnable onSuccess) {
        // Implementacja logowania
    }
    
    public static void logout() {
        activeUser = null;
    }
}