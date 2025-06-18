import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public class Main {
    private static final  String SAVE_FILE = "project.ser";
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Inicjalizacja z przykładowymi danymi
            Container container = loadProjects();
            if(container == null){
                container=initSampleData(); // Inicjalizacja z przykładowymi danymi
            }
            ImageIcon icon = new ImageIcon("Icon.png");
            // User testUser = new User("Test", "test@test.com", "123", false);
            // SettingsWindow settings = new SettingsWindow(testUser, container);
            // settings.setVisible(true);

            // Poprawka: deklarujemy zmienną frame
            MainFrame frame = new MainFrame(container);
            frame.setIconImage(icon.getImage());
        });
    }


    private static Container loadProjects() {
        File file = new File(SAVE_FILE);
        if (!file.exists()){
            return null;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))){
            Container container = (Container) ois.readObject();
            System.out.println("WCZYTANO: " + container.projects.size() + " projektów i " + 
                            container.getTeams().size() + " zespołów");
            
            for (Team team : container.getTeams()) {
                System.out.println("  - Zespół: " + team.getName());
            }
            return container;
        } catch(IOException|ClassNotFoundException e){
            System.err.println("Nastąpił problem z otworzeniem pliku: "+ e.getMessage());
            return null;
        }

    }


    private static Container initSampleData() {
        Container container = new Container();
        int startDay = (int) (System.currentTimeMillis() / (1000L * 3600*24));
        
        Project p1 = new Project("Website Redesign", "Modernizacja strony korporacyjnej", startDay, startDay + 30);
        p1.addTask(new Task("Projekt UI", false, (byte) 5));
        p1.addTask(new Task("Implementacja frontendu", false, (byte) 8));
        container.addProject(p1);

        Project p2 = new Project("Aplikacja mobilna", "Nowa aplikacja dla klientów" + p1.predictDate(), startDay, startDay + 45);
        p2.addTask(new Task("Research rynku", true, (byte) 3));
        container.addProject(p2);
        
        return container;
    }
    
    public static void saveProjects(Container container) {
        try {
            File file = new File(SAVE_FILE);
            if (!file.exists()) {
                file.createNewFile();
            }
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(container);
                System.out.println("Zapisano: " + container.projects.size() + " projektów i " + 
                    container.getTeams().size() + " zespołów");
                for (Team team : container.getTeams()) {
                    System.out.println("  - Zespół: " + team.getName());
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, 
                "Błąd zapisu projektu: " + e.getMessage(), 
                "Błąd", JOptionPane.ERROR_MESSAGE);
        }
    }
}