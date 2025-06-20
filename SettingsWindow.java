import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class SettingsWindow extends JDialog {
    private final User currentUser;
    private final Container container;
    private List<Team> teams;
    private List<User> allUsers;
    private JList<Team> teamList;
    private DefaultListModel<Team> teamListModel;
    private JPanel teamDetailsPanel;
    private Team selectedTeam;

    public User getCurrentUser() {
        return currentUser;
    }

    public SettingsWindow(User currentUser, Container container) {
        this.currentUser = currentUser;
        this.container = container;
        this.teams = container.getTeams();
        System.out.println("DEBUG: SettingsWindow - liczba zespołów w kontenerze: " + container.getTeams().size());
        this.allUsers = new ArrayList<>();

        // Dodaj przykładowych użytkowników (w prawdziwej aplikacji pobierz z bazy danych)
        allUsers.add(currentUser);
        allUsers.add(new User("Jan Kowalski", "jan@example.com", "password123"));
        allUsers.add(new User("Anna Nowak", "anna@example.com", "password123"));

        initializeComponents();
        setupLayout();
        refreshTeamList();
    }

    private void initializeComponents() {
        setTitle("Ustawienia - Zarządzanie Zespołami");
        setModal(true);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        teamListModel = new DefaultListModel<>();
        teamList = new JList<>(teamListModel);
        teamList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        teamList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selectedTeam = teamList.getSelectedValue();
                updateTeamDetailsPanel();
            }
        });

        teamDetailsPanel = new JPanel(new BorderLayout());
        teamDetailsPanel.setBorder(BorderFactory.createTitledBorder("Szczegóły zespołu"));
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Lewy panel - lista zespołów
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(new EmptyBorder(10, 10, 10, 5));
        leftPanel.setPreferredSize(new Dimension(250, 0));

        JLabel teamsLabel = new JLabel("Twoje zespoły:");
        teamsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        leftPanel.add(teamsLabel, BorderLayout.NORTH);

        JScrollPane teamScrollPane = new JScrollPane(teamList);
        leftPanel.add(teamScrollPane, BorderLayout.CENTER);

        JButton newTeamButton = new JButton("+ Nowy zespół");
        newTeamButton.addActionListener(this::createNewTeam);
        leftPanel.add(newTeamButton, BorderLayout.SOUTH);

        // Prawy panel - szczegóły zespołu
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(new EmptyBorder(10, 5, 10, 10));
        rightPanel.add(teamDetailsPanel, BorderLayout.CENTER);

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);
    }

    private void createNewTeam(ActionEvent e) {
        String teamName = JOptionPane.showInputDialog(this, "Nazwa zespołu:", "Nowy zespół", JOptionPane.PLAIN_MESSAGE);
        if (teamName != null && !teamName.trim().isEmpty()) {
            String teamDesc = JOptionPane.showInputDialog(this, "Opis zespołu:", "Nowy zespół", JOptionPane.PLAIN_MESSAGE);
            if (teamDesc == null) teamDesc = "";

            Team newTeam = new Team(teamName.trim(), teamDesc.trim(), currentUser);
            teams.add(newTeam);
            container.addTeam(newTeam);
            Main.saveProjects(container); 
            System.out.println("DEBUG: Wywołano saveProjects z createNewTeam");
            refreshTeamList();
        }
    }

    private void refreshTeamList() {
        teamListModel.clear();
        for (Team team : teams) {
            if (team.isMember(currentUser)) {
                teamListModel.addElement(team);
            }
        }
    }

    private void updateTeamDetailsPanel() {
        teamDetailsPanel.removeAll();

        if (selectedTeam == null) {
            teamDetailsPanel.add(new JLabel("Wybierz zespół z listy", SwingConstants.CENTER));
        } else {
            JPanel detailsContent = createTeamDetailsContent();
            teamDetailsPanel.add(detailsContent, BorderLayout.CENTER);
        }

        teamDetailsPanel.revalidate();
        teamDetailsPanel.repaint();
    }

    private JPanel createTeamDetailsContent() {
        JPanel panel = new JPanel(new BorderLayout());

        // Informacje o zespole
        JPanel infoPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        infoPanel.add(new JLabel("Nazwa:"), gbc);
        gbc.gridx = 1;
        JLabel nameLabel = new JLabel(selectedTeam.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        infoPanel.add(nameLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        infoPanel.add(new JLabel("Opis:"), gbc);
        gbc.gridx = 1;
        infoPanel.add(new JLabel(selectedTeam.getDescription()), gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        infoPanel.add(new JLabel("Twórca:"), gbc);
        gbc.gridx = 1;
        infoPanel.add(new JLabel(selectedTeam.getCreator().name), gbc);

        panel.add(infoPanel, BorderLayout.NORTH);

        // Lista członków
        JPanel membersPanel = new JPanel(new BorderLayout());
        membersPanel.setBorder(BorderFactory.createTitledBorder("Członkowie zespołu"));

        DefaultListModel<String> membersModel = new DefaultListModel<>();
        for (Map.Entry<User, Role> entry : selectedTeam.getMembersWithRoles().entrySet()) {
            String memberInfo = entry.getKey().name + " (" + entry.getValue().getName() + ")";
            membersModel.addElement(memberInfo);
        }

        JList<String> membersList = new JList<>(membersModel);
        JScrollPane membersScrollPane = new JScrollPane(membersList);
        membersScrollPane.setPreferredSize(new Dimension(0, 150));
        membersPanel.add(membersScrollPane, BorderLayout.CENTER);

        // Przyciski zarządzania członkami
        if (selectedTeam.isCreator(currentUser) ||
                selectedTeam.getUserRole(currentUser).getPerm() >= 64) {

            JPanel buttonPanel = new JPanel(new FlowLayout());

            JButton addMemberButton = new JButton("Dodaj członka");
            addMemberButton.addActionListener(e -> addMemberToTeam());
            buttonPanel.add(addMemberButton);

            JButton removeMemberButton = new JButton("Usuń członka");
            removeMemberButton.addActionListener(e -> removeMemberFromTeam(membersList));
            buttonPanel.add(removeMemberButton);

            JButton changeRoleButton = new JButton("Zmień rolę");
            changeRoleButton.addActionListener(e -> changeMemberRole(membersList));
            buttonPanel.add(changeRoleButton);

            if (selectedTeam.isCreator(currentUser)) {
                JButton deleteTeamButton = new JButton("Usuń zespół");
                deleteTeamButton.setForeground(Color.RED);
                deleteTeamButton.addActionListener(e -> {
                    int confirm = JOptionPane.showConfirmDialog(
                        this,
                        "Czy na pewno chcesz usunąć zespół " + selectedTeam.getName() + "?\nSpowoduje to usunięcie go ze wszystkich projektów.",
                        "Potwierdź usunięcie",
                        JOptionPane.YES_NO_OPTION
                    );
                    
                    if (confirm == JOptionPane.YES_OPTION) {
                        for(Project p : container.projects) {
                            if (selectedTeam.equals(p.getTeam())) {
                                p.setTeam(null);
                            }
                        }
                        selectedTeam.disbandTeam();
                        container.removeTeam(selectedTeam);         
                        Main.saveProjects(container);               
                        refreshTeamList();
                        selectedTeam = null;
                        updateTeamDetailsPanel();
                    }

                });
                buttonPanel.add(deleteTeamButton);
            }

            membersPanel.add(buttonPanel, BorderLayout.SOUTH);
        }

        panel.add(membersPanel, BorderLayout.CENTER);

        return panel;
    }

    private void addMemberToTeam() {
        List<User> availableUsers = new ArrayList<>();
        for (User user : allUsers) {
            if (!selectedTeam.isMember(user)) {
                availableUsers.add(user);
            }
        }

        if (availableUsers.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Wszyscy użytkownicy są już członkami tego zespołu.");
            return;
        }

        User selectedUser = (User) JOptionPane.showInputDialog(
                this,
                "Wybierz użytkownika do dodania:",
                "Dodaj członka",
                JOptionPane.PLAIN_MESSAGE,
                null,
                availableUsers.toArray(),
                availableUsers.get(0)
        );

        if (selectedUser != null) {
            Role[] roles = {Role.createViewer(), Role.createMember(), Role.createAdministrator()};
            Role selectedRole = (Role) JOptionPane.showInputDialog(
                    this,
                    "Wybierz rolę dla użytkownika:",
                    "Wybierz rolę",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    roles,
                    roles[1]
            );

            if (selectedRole != null) {
                selectedTeam.addMember(selectedUser, selectedRole);
                selectedUser.addTeam(selectedTeam, selectedRole);
                Main.saveProjects(container);
                updateTeamDetailsPanel();
            }
        }
    }

    private User findUserByNameInSelectedTeam(String name) {
        for (User user : selectedTeam.getMembers()) {
            if (user.name.equals(name)) {
                return user;
            }
        }
        return null;
    }

    private void removeMemberFromTeam(JList<String> membersList) {
    String selectedValue = membersList.getSelectedValue();
    if (selectedValue != null) {
        String username = selectedValue.substring(0, selectedValue.lastIndexOf(" ("));
        User userToRemove = findUserByNameInSelectedTeam(username);

        if (userToRemove == null) {
            JOptionPane.showMessageDialog(this, "Nie znaleziono użytkownika.");
            return;
        }

        if (selectedTeam.isCreator(userToRemove)) {
            JOptionPane.showMessageDialog(this, "Nie można usunąć twórcy zespołu.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Czy na pewno chcesz usunąć użytkownika " + userToRemove.name + " z zespołu?",
                "Potwierdź usunięcie",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            selectedTeam.removeMember(userToRemove);
            userToRemove.removeTeam(selectedTeam);
            updateTeamDetailsPanel();
        }
    } else {
        JOptionPane.showMessageDialog(this, "Wybierz członka do usunięcia.");
    }
}


    private void changeMemberRole(JList<String> membersList) {
    String selectedValue = membersList.getSelectedValue();
    if (selectedValue != null) {
        String username = selectedValue.substring(0, selectedValue.lastIndexOf(" ("));
        User userToUpdate = findUserByNameInSelectedTeam(username);

        if (userToUpdate == null) {
            JOptionPane.showMessageDialog(this, "Nie znaleziono użytkownika.");
            return;
        }

        if (selectedTeam.isCreator(userToUpdate)) {
            JOptionPane.showMessageDialog(this, "Nie można zmieniać roli twórcy zespołu.");
            return;
        }

        Role[] roles = {Role.createViewer(), Role.createMember(), Role.createAdministrator()};
        Role currentRole = selectedTeam.getUserRole(userToUpdate);
        Role newRole = (Role) JOptionPane.showInputDialog(
                this,
                "Wybierz nową rolę dla " + userToUpdate.name + ":",
                "Zmień rolę",
                JOptionPane.PLAIN_MESSAGE,
                null,
                roles,
                currentRole
        );

        if (newRole != null && !newRole.equals(currentRole)) {
            selectedTeam.updateMemberRole(userToUpdate, newRole);
            userToUpdate.addTeam(selectedTeam, newRole);
            updateTeamDetailsPanel();
        }
    } else {
        JOptionPane.showMessageDialog(this, "Wybierz członka do zmiany roli.");
    }
}


    public List<Team> getTeams() {
        return teams;
    }
}