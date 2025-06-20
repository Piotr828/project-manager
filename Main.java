import javax.swing.*;
import java.io.*;
import java.util.*;

public class Main {
    private static final String SAVE_FILE = "project.ser";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Container container = loadProjects();
            if (container == null) {
                container = new Container(); // lub initSampleData()
            }

            // Uruchamiamy ekran logowania (odpowiedzialny za dalszy przebieg)
        MainFrame frame = new MainFrame(container);
        frame.setVisible(true);


        });
    }

    public static Container loadProjects() {
        File file = new File(SAVE_FILE);
        if (!file.exists()) return null;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Container container = (Container) ois.readObject();
            System.out.println("WCZYTANO: " + container.projects.size() + " projektów i " +
                    container.getTeams().size() + " zespołów");
            return container;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Błąd przy wczytywaniu: " + e.getMessage());
            return null;
        }
    }

    public static void saveProjects(Container container) {
        File file = new File(SAVE_FILE);
        try {
            if (!file.exists()) file.createNewFile();
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(container);
                System.out.println("Zapisano: " + container.projects.size() + " projektów i " +
                        container.getTeams().size() + " zespołów");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Błąd zapisu: " + e.getMessage());
        }
    }

    public static Container initSampleData() {
        Container container = new Container();
        int startDay = (int) (System.currentTimeMillis() / (1000L * 3600 * 24));
        Project p1 = new Project("Website Redesign", "Modernizacja strony", startDay, startDay + 30);
        p1.addTask(new Task("Projekt UI", false, (byte) 5));
        p1.addTask(new Task("Frontend", false, (byte) 8));
        container.addProject(p1);
        return container;
    }


}
