import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.plaf.basic.BasicProgressBarUI;

public class ProjectCard extends JPanel {

    public ProjectCard(Project project, Runnable onClick) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);
        setAlignmentX(Component.LEFT_ALIGNMENT);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 6, 0, 0, new Color(project.red, project.green, project.blue)),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
            )
        ));

        String displayName = project.name.length() > 30 ? 
            project.name.substring(0, 27) + "..." : project.name;
        JLabel nameLabel = new JLabel(displayName);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        nameLabel.setToolTipText(project.name);

        JTextArea descArea = new JTextArea(project.descript);
        descArea.setFont(new Font("SansSerif", Font.PLAIN, 13));
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setEditable(false);
        descArea.setOpaque(false);
        descArea.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel headerPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        headerPanel.setOpaque(false);
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        headerPanel.add(createHeaderLabel("Opóźnienie"));
        headerPanel.add(createHeaderLabel("Przewidywanie"));
        headerPanel.add(createHeaderLabel("Termin"));

        JPanel valuePanel = new JPanel(new GridLayout(1, 3, 20, 0));
        valuePanel.setOpaque(false);
        valuePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel delayLabel = new JLabel(Math.max(project.delay, 0) + " dni");
        delayLabel.setForeground(project.delay > 0 ? Color.RED : new Color(0, 128, 0));
        delayLabel.setHorizontalAlignment(SwingConstants.CENTER);
        valuePanel.add(delayLabel);

        JLabel predictLabel = new JLabel(project.predictDate());
        predictLabel.setHorizontalAlignment(SwingConstants.CENTER);
        valuePanel.add(predictLabel);

        JLabel deadlineLabel = new JLabel(formatDate(project.deadline));
        deadlineLabel.setHorizontalAlignment(SwingConstants.CENTER);
        valuePanel.add(deadlineLabel);

        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue(project.progress());
        progressBar.setStringPainted(true);
        progressBar.setFont(new Font("SansSerif", Font.BOLD, 12));
        progressBar.setForeground(new Color(project.red, project.green, project.blue));
        progressBar.setAlignmentX(Component.LEFT_ALIGNMENT);
        progressBar.setUI(new BasicProgressBarUI() {
            protected Color getSelectionBackground() { return Color.BLACK; }
            protected Color getSelectionForeground() { return Color.WHITE; }
        });

        add(nameLabel);
        add(Box.createVerticalStrut(5));
        add(descArea);
        add(Box.createVerticalStrut(10));
        add(headerPanel);
        add(Box.createVerticalStrut(5));
        add(valuePanel);
        add(Box.createVerticalStrut(10));
        add(progressBar);

        this.addAncestorListener(new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                updateWidth();
                Component parent = getParent();
                if (parent != null) {
                    parent.addComponentListener(new ComponentAdapter() {
                        @Override
                        public void componentResized(ComponentEvent e) {
                            updateWidth();
                        }
                    });
                }
            }

            @Override public void ancestorRemoved(AncestorEvent event) {}
            @Override public void ancestorMoved(AncestorEvent event) {}
        });

        MouseAdapter clickHandler = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onClick.run();
            }
        };
        for (Component c : getComponents()) {
            c.addMouseListener(clickHandler);
        }
        this.addMouseListener(clickHandler);
    }

    private void updateWidth() {
        if (getParent() != null) {
            int width = getParent().getWidth();
            setPreferredSize(new Dimension(width, getPreferredSize().height));
            setMaximumSize(new Dimension(width, Integer.MAX_VALUE));
            revalidate();
            repaint();
        }
    }

    private JLabel createHeaderLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 12));
        label.setForeground(Color.GRAY);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }

    private String formatDate(long epochDay) {
        return LocalDate.ofEpochDay(epochDay).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }
}
