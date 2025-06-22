import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SettingsWindow extends JDialog {
    private static final Font HEADER_FONT = new Font("Arial", Font.BOLD, 14);
    private static final String TITLE = "Ustawienia - Zarządzanie Zespołami";
    private static final String TEAM_LIST_LABEL = "Twoje zespoły:";
    private static final String NEW_TEAM_BUTTON_TEXT = "+ Nowy zespół";

    private final User currentUser;
    private final Container container;
    private final List<Team> teams;
    private final List<User> allUsers;

    private final DefaultListModel<Team> teamListModel = new DefaultListModel<>();
    private final JList<Team> teamList = new JList<>(teamListModel);
    private final JPanel teamDetailsPanel = new JPanel(new BorderLayout());

    private Team selectedTeam;

    public SettingsWindow(User currentUser, Container container) {
        this.currentUser = currentUser;
        this.container = container;
        this.teams = container.getTeams();
        this.allUsers = AuthManager.getAllUsers(); 

        configureDialog();
        setupLayout();
        refreshTeamList();
    }

    private void configureDialog() {
        setTitle(TITLE);
        setModal(true);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        add(createLeftPanel(), BorderLayout.WEST);
        add(createRightPanel(), BorderLayout.CENTER);
    }

    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(new EmptyBorder(10, 10, 10, 5));
        leftPanel.setPreferredSize(new Dimension(250, 0));

        JLabel teamsLabel = new JLabel(TEAM_LIST_LABEL);
        teamsLabel.setFont(HEADER_FONT);
        leftPanel.add(teamsLabel, BorderLayout.NORTH);

        teamList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        teamList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selectedTeam = teamList.getSelectedValue();
                updateTeamDetails();
            }
        });
        leftPanel.add(new JScrollPane(teamList), BorderLayout.CENTER);

        JButton newTeamButton = new JButton(NEW_TEAM_BUTTON_TEXT);
        newTeamButton.addActionListener(this::createNewTeam);
        leftPanel.add(newTeamButton, BorderLayout.SOUTH);

        return leftPanel;
    }

    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(new EmptyBorder(10, 5, 10, 10));
        teamDetailsPanel.setBorder(BorderFactory.createTitledBorder("Szczegóły zespołu"));
        rightPanel.add(teamDetailsPanel, BorderLayout.CENTER);
        return rightPanel;
    }

    private void createNewTeam(ActionEvent e) {
        String teamName = prompt("Nazwa zespołu:", "Nowy zespół");
        if (teamName == null || teamName.trim().isEmpty()) return;

        String teamDesc = prompt("Opis zespołu:", "Nowy zespół");
        if (teamDesc == null) teamDesc = "";

        Team newTeam = new Team(teamName.trim(), teamDesc.trim(), currentUser);
        teams.add(newTeam);
        container.addTeam(newTeam);
        Main.saveProjects(container);
        refreshTeamList();
    }

    private String prompt(String message, String title) {
        return JOptionPane.showInputDialog(this, message, title, JOptionPane.PLAIN_MESSAGE);
    }

    private void refreshTeamList() {
        teamListModel.clear();
        for (Team team : teams) {
            if (team.isMember(currentUser)) {
                teamListModel.addElement(team);
            }
        }
    }

    private void updateTeamDetails() {
        teamDetailsPanel.removeAll();
        if (selectedTeam == null) {
            teamDetailsPanel.add(new JLabel("Wybierz zespół z listy", SwingConstants.CENTER));
        } else {
            JPanel panel = new TeamDetailsPanel(selectedTeam);
            teamDetailsPanel.add(panel, BorderLayout.CENTER);
        }
        teamDetailsPanel.revalidate();
        teamDetailsPanel.repaint();
    }

    private void addManagementButtons(JPanel panel, JList<String> membersList) {
        if (!canManageTeam()) return;

        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton addMemberButton = new JButton("Dodaj członka");
        addMemberButton.addActionListener(e -> addMember());
        buttonPanel.add(addMemberButton);

        JButton removeMemberButton = new JButton("Usuń członka");
        removeMemberButton.addActionListener(e -> removeMember(membersList));
        buttonPanel.add(removeMemberButton);

        JButton changeRoleButton = new JButton("Zmień rolę");
        changeRoleButton.addActionListener(e -> changeRole(membersList));
        buttonPanel.add(changeRoleButton);

        if (selectedTeam.isCreator(currentUser)) {
            JButton deleteButton = new JButton("Usuń zespół");
            deleteButton.setForeground(Color.RED);
            deleteButton.addActionListener(e -> deleteTeam());
            buttonPanel.add(deleteButton);
        }

        panel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private boolean canManageTeam() {
        return selectedTeam != null &&
                (selectedTeam.isCreator(currentUser) ||
                        selectedTeam.getUserRole(currentUser).getPerm() >= 64);
    }

    private void addMember() {
        List<User> candidates = new ArrayList<>();
        for (User u : allUsers) {
            if (!selectedTeam.isMember(u)) candidates.add(u);
        }

        if (candidates.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Wszyscy użytkownicy należą już do zespołu.");
            return;
        }

        User selected = (User) JOptionPane.showInputDialog(
                this, "Wybierz użytkownika:", "Dodaj członka",
                JOptionPane.PLAIN_MESSAGE, null, candidates.toArray(), candidates.get(0)
        );

        if (selected != null) {
            Role[] roles = {Role.createViewer(), Role.createMember(), Role.createAdministrator()};
            Role role = (Role) JOptionPane.showInputDialog(
                    this, "Rola nowego członka:", "Rola",
                    JOptionPane.PLAIN_MESSAGE, null, roles, roles[1]
            );

            if (role != null) {
                selectedTeam.addMember(selected, role);
                selected.addTeam(selectedTeam, role);
                Main.saveProjects(container);
                updateTeamDetails();
            }
        }
    }

    private void removeMember(JList<String> membersList) {
        String entry = membersList.getSelectedValue();
        if (entry == null) {
            JOptionPane.showMessageDialog(this, "Wybierz członka do usunięcia.");
            return;
        }

        String name = entry.split(" \\(")[0];
        User user = findUserByName(name);
        if (user == null) return;

        if (selectedTeam.isCreator(user)) {
            JOptionPane.showMessageDialog(this, "Nie można usunąć twórcy zespołu.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this, "Usunąć użytkownika " + name + " z zespołu?",
                "Potwierdzenie", JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            selectedTeam.removeMember(user);
            user.removeTeam(selectedTeam);
            Main.saveProjects(container);
            updateTeamDetails();
        }
    }

    private void changeRole(JList<String> membersList) {
        String entry = membersList.getSelectedValue();
        if (entry == null) {
            JOptionPane.showMessageDialog(this, "Wybierz członka do zmiany roli.");
            return;
        }

        String name = entry.split(" \\(")[0];
        User user = findUserByName(name);
        if (user == null) return;

        if (selectedTeam.isCreator(user)) {
            JOptionPane.showMessageDialog(this, "Nie można zmienić roli twórcy zespołu.");
            return;
        }

        Role current = selectedTeam.getUserRole(user);
        Role[] roles = {Role.createViewer(), Role.createMember(), Role.createAdministrator()};
        Role newRole = (Role) JOptionPane.showInputDialog(
                this, "Nowa rola dla " + name + ":", "Zmień rolę",
                JOptionPane.PLAIN_MESSAGE, null, roles, current
        );

        if (newRole != null && !newRole.equals(current)) {
            selectedTeam.updateMemberRole(user, newRole);
            user.addTeam(selectedTeam, newRole);
            Main.saveProjects(container);
            updateTeamDetails();
        }
    }

    private void deleteTeam() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Czy na pewno chcesz usunąć zespół " + selectedTeam.getName() + "?",
                "Potwierdź usunięcie",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            for (Project p : container.getProjects()) {
                if (selectedTeam.equals(p.getTeam())) {
                    p.setTeam(null);
                }
            }
            selectedTeam.disbandTeam();
            container.removeTeam(selectedTeam);
            Main.saveProjects(container);
            selectedTeam = null;
            refreshTeamList();
            updateTeamDetails();
        }
    }

    private User findUserByName(String name) {
        return allUsers.stream().filter(u -> u.getName().equals(name)).findFirst().orElse(null);
    }

    private class TeamDetailsPanel extends JPanel {
        public TeamDetailsPanel(Team team) {
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JPanel top = new JPanel(new GridLayout(3, 1));
            top.add(new JLabel("Nazwa: " + team.getName()));
            top.add(new JLabel("Opis: " + team.getDescription()));
            top.add(new JLabel("Twórca: " + team.getCreator().getName()));
            add(top, BorderLayout.NORTH);

            DefaultListModel<String> model = new DefaultListModel<>();
            for (Map.Entry<User, Role> entry : team.getMembersWithRoles().entrySet()) {
                model.addElement(entry.getKey().getName() + " (" + entry.getValue().getName() + ")");
            }

            JList<String> list = new JList<>(model);
            JScrollPane scroll = new JScrollPane(list);
            scroll.setPreferredSize(new Dimension(0, 150));

            JPanel members = new JPanel(new BorderLayout());
            members.setBorder(BorderFactory.createTitledBorder("Członkowie"));
            members.add(scroll, BorderLayout.CENTER);

            addManagementButtons(members, list);
            add(members, BorderLayout.CENTER);
        }
    }
}
