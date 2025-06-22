import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.plaf.basic.BasicProgressBarUI;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ProjectCard extends JPanel {
    private final Project project;

    public ProjectCard(Project project, Runnable onClick) {
        this.project = project;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);
        setAlignmentX(Component.LEFT_ALIGNMENT);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 6, 0, 0, new Color(project.getRed(), project.getGreen(), project.getBlue())),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                        BorderFactory.createEmptyBorder(15, 20, 15, 20)
                )
        ));

        addNameLabel();
        add(Box.createVerticalStrut(5));
        addDescription();
        add(Box.createVerticalStrut(10));
        addHeader();
        add(Box.createVerticalStrut(5));
        addValues();
        add(Box.createVerticalStrut(10));
        addProgressBar();

        addAncestorListener(new AncestorListener() {
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

            @Override
            public void ancestorRemoved(AncestorEvent event) {}

            @Override
            public void ancestorMoved(AncestorEvent event) {}
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
        setupContextMenu();
    }

    private void addNameLabel() {
        String displayName = project.getName().length() > 30
                ? project.getName().substring(0, 27) + "..."
                : project.getName();

        JLabel nameLabel = new JLabel(displayName);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        nameLabel.setToolTipText(project.getName());
        add(nameLabel);
    }

    private void addDescription() {
        JTextArea descArea = new JTextArea(project.getDescript());
        descArea.setFont(new Font("SansSerif", Font.PLAIN, 13));
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setEditable(false);
        descArea.setOpaque(false);
        descArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(descArea);
    }

    private void addHeader() {
        JPanel headerPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        headerPanel.setOpaque(false);
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerPanel.add(createHeaderLabel("Opóźnienie"));
        headerPanel.add(createHeaderLabel("Przewidywanie"));
        headerPanel.add(createHeaderLabel("Termin"));
        add(headerPanel);
    }

    private void addValues() {
        JPanel valuePanel = new JPanel(new GridLayout(1, 3, 20, 0));
        valuePanel.setOpaque(false);
        valuePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel delayLabel = new JLabel(Math.max(project.getDelay(), 0) + " dni");
        delayLabel.setForeground(project.getDelay() > 0 ? Color.RED : new Color(0, 128, 0));
        delayLabel.setHorizontalAlignment(SwingConstants.CENTER);
        valuePanel.add(delayLabel);

        JLabel predictLabel = new JLabel(project.predictDate());
        predictLabel.setHorizontalAlignment(SwingConstants.CENTER);
        valuePanel.add(predictLabel);

        JLabel deadlineLabel = new JLabel(formatDate(project.getDeadline()));
        deadlineLabel.setHorizontalAlignment(SwingConstants.CENTER);
        valuePanel.add(deadlineLabel);

        add(valuePanel);
    }

    private void addProgressBar() {
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue(project.progress());
        progressBar.setStringPainted(true);
        progressBar.setFont(new Font("SansSerif", Font.BOLD, 12));
        progressBar.setForeground(new Color(project.getRed(), project.getGreen(), project.getBlue()));
        progressBar.setAlignmentX(Component.LEFT_ALIGNMENT);
        progressBar.setUI(new BasicProgressBarUI() {
            @Override
            protected Color getSelectionBackground() {
                return Color.BLACK;
            }

            @Override
            protected Color getSelectionForeground() {
                return Color.WHITE;
            }
        });

        add(progressBar);
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

private void updateWidth() {
    java.awt.Container parent = getParent();
    if (parent != null) {
        int width = parent.getWidth();
        setPreferredSize(new Dimension(width, getPreferredSize().height));
        setMaximumSize(new Dimension(width, Integer.MAX_VALUE));
        setAlignmentX(Component.LEFT_ALIGNMENT);
        revalidate();
        repaint();
    }
}

    private void setupContextMenu() {
        JPopupMenu contextMenu = new JPopupMenu();

        JMenuItem deleteItem = new JMenuItem("Usuń projekt");
        deleteItem.addActionListener(e -> {
            int response = JOptionPane.showConfirmDialog(
                    this,
                    "Czy na pewno chcesz usunąć projekt '" + project.getName() + "'?",
                    "Potwierdzenie usunięcia",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (response == JOptionPane.YES_OPTION) {
                Container container = ((DashboardView) SwingUtilities.getAncestorOfClass(
                        DashboardView.class, this)).container;
                container.removeProject(project);
                Main.saveProjects(container);
                SwingUtilities.invokeLater(() -> {
                    JPanel parent = (JPanel) getParent();
                    if (parent != null) {
                        parent.remove(this);
                        parent.revalidate();
                        parent.repaint();
                    }
                });
            }
        });

        contextMenu.add(deleteItem);
        setComponentPopupMenu(contextMenu);
    }

    public Project getProject() {
        return project;
    }
}
