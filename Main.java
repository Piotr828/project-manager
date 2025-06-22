

import javax.swing.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    private static final String SAVE_FILE = "project.ser";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Container container = loadProjects();
            if (container == null) {
                container = new Container();
            }
            MainFrame frame = new MainFrame(container);
            frame.setVisible(true);
        });
    }

    public static Container loadProjects() {
        File file = new File(SAVE_FILE);
        if (!file.exists()) return null;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Container container = (Container) ois.readObject();
            LOGGER.info("WCZYTANO: " + container.getProjects().size() + " projektów i " +
                    container.getTeams().size() + " zespołów");
            return container;
        } catch (IOException | ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Błąd przy wczytywaniu", e);
            return null;
        }
    }

    public static void saveProjects(Container container) {
        File file = new File(SAVE_FILE);
        try {
            if (!file.exists() && !file.createNewFile()) {
                LOGGER.warning("Nie udało się utworzyć pliku: " + file.getName());
            }
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(container);
                LOGGER.info("Zapisano: " + container.getProjects().size() + " projektów i " +
                        container.getTeams().size() + " zespołów");
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Błąd zapisu", e);
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
