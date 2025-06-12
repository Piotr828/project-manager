public class AuthManager {
    public static User getActiveUser();
    public static void setActiveUser(User user){};
    public static boolean isLoggedIn();
    public static void showLoginDialog(JFrame parent, Runnable onSuccess){};
    public static void logout(){};
}