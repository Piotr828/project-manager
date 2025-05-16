import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ProjectCard extends JPanel {
    public ProjectCard(Project project, Runnable onClick) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Lewa strona - informacje
        JPanel infoPanel = new JPanel(new GridLayout(0, 1));
        JLabel nameLabel = new JLabel(project.name);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 18));
        infoPanel.add(nameLabel);
        
        JLabel descLabel = new JLabel(project.descript);
        descLabel.setForeground(Color.GRAY);
        infoPanel.add(descLabel);
        
        add(infoPanel, BorderLayout.CENTER);

        // Prawa strona - postęp
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue(project.progress());
        progressBar.setStringPainted(true);
        add(progressBar, BorderLayout.EAST);

        // Obsługa kliknięcia
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onClick.run();
            }
        });
    }
}