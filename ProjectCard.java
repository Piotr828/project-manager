import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.*;

public class ProjectCard extends JPanel {
    public ProjectCard(Project project, Runnable onClick) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // === Kolorowy border z lewej ===
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 6, 0, 0, new Color(project.red, project.green, project.blue)),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
            )
        ));

        // === Środek karty ===
        JPanel infoPanel = new JPanel(new GridLayout(0, 1));
        infoPanel.setOpaque(false);

        JLabel nameLabel = new JLabel(project.name);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 18));
        infoPanel.add(nameLabel);

        JLabel descLabel = new JLabel(project.descript);
        descLabel.setForeground(Color.GRAY);
        infoPanel.add(descLabel);

        add(infoPanel, BorderLayout.CENTER);

        //== Panel z datami ==
        JPanel datesPanel = new JPanel(new GridLayout(2,2,5,5));
        datesPanel.setOpaque(false);
        datesPanel.setBorder(BorderFactory.createEmptyBorder(5,0,0,0));

        //Termin koncowy
        datesPanel.add(new JLabel("Termin: "));
        datesPanel.add(new JLabel(formatDate(project.deadline)));

        //Szacowane zakonczenie
        datesPanel.add(new JLabel("Szacowany czas: "));
        datesPanel.add(new JLabel(project.predictDate()));

        infoPanel.add(datesPanel);

        //==Opoznienie==

        JLabel delayLabel = new JLabel("Opóźnienie: "+ Math.max(project.delay,0) + " dni");
        delayLabel.setFont(new Font("Arial", Font.BOLD,12));
        delayLabel.setForeground(project.delay > 0 ? Color.RED : Color.GREEN);
        infoPanel.add(delayLabel);

        // === Pasek postępu ===
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue(project.progress());
        progressBar.setStringPainted(true);
        progressBar.setPreferredSize(new Dimension(100,20));
        add(progressBar, BorderLayout.EAST);

        // === Obsługa kliknięcia ===
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onClick.run();
            }
        });
    }
    private String formatDate(long epochDay){
        return LocalDate.ofEpochDay(epochDay).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }
}
