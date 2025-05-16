import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;


// class Main {
//     public static void main(String[] args) {
//         SwingUtilities.invokeLater(() -> {
//             // Utworzenie kontenera i przykładowych projektów
//             Container container = new Container();
//             int startDay = (int) (System.currentTimeMillis() / (1000L * 60 * 60 * 24));
//             for (int i = 1; i <= 5; i++) {
//                 int deadlineDay = startDay + 7;
//                 Project p = new Project(
//                         "Projekt " + i,
//                         "Opis projektu nr " + i,
//                         startDay,
//                         deadlineDay
//                 );
//                 container.addProject(p);
//             }

//             // Utworzenie okna i dodanie DashboardView
//             JFrame frame = new JFrame("Project Manager");
//             frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//             frame.setSize(800, 600);
//             frame.setLocationRelativeTo(null);
//             frame.setVisible(true);
//         });
//     }
// }

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Inicjalizacja z przykładowymi danymi
            Container container = initSampleData();
            
            // Uruchomienie głównego okna
            new MainFrame();
        });
    }
    
    private static Container initSampleData() {
        Container container = new Container();
        int startDay = (int) (System.currentTimeMillis() / (1000L * 60 * 60 * 24));
        
        Project p1 = new Project("Website Redesign", "Modernizacja strony korporacyjnej", startDay, startDay + 30);
        p1.addTask(new Task("Projekt UI", false, (byte) 5));
        p1.addTask(new Task("Implementacja frontendu", false, (byte) 8));
        container.addProject(p1);
        
        Project p2 = new Project("Aplikacja mobilna", "Nowa aplikacja dla klientów", startDay, startDay + 45);
        p2.addTask(new Task("Research rynku", true, (byte) 3));
        container.addProject(p2);
        
        return container;
    }
}