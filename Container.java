import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class Task implements Serializable {
    private static final long serialVersionUID = 1L;
    String name;
    boolean status;
    byte diffic;

    public Task(String name, boolean status, byte diffic) {
        this.name = name;
        this.status = status;
        this.diffic = diffic;
    }
}

class Project implements Serializable {
    private static final long serialVersionUID = 1L;

    private byte progress;
    private long start;
    private long deadline;
    private long predict;
    private int delay;
    private List<Task> tasks;
    private String name;
    private String descript;
    private int red;
    private int green;
    private int blue;
    private Team team;

    public Project(String name, String description, long start, long deadline, int red, int green, int blue) {
        this.setName(name);
        this.setDescript(description);
        this.setStart(start);
        this.setDeadline(deadline);
        this.setTasks(new ArrayList<>());
        this.setProgress((byte) 0);
        this.setPredict(0);
        this.setDelay((int) (getPredict() - deadline) / 86400);
        this.setRed(red);
        this.setGreen(green);
        this.setBlue(blue);
        this.setTeam(null);
        calculatePredict();
    }

    public Project(String name, String description, long start, long deadline) {
        this(name, description, start, deadline, 0, 0, 0);
    }

    public long getDeadline() {
        return deadline;
    }

    public void setDeadline(long deadline) {
        this.deadline = deadline;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getPredict() {
        return predict;
    }

    public void setPredict(long predict) {
        this.predict = predict;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescript() {
        return descript;
    }

    public void setDescript(String descript) {
        this.descript = descript;
    }

    public int getRed() {
        return red;
    }

    public void setRed(int red) {
        this.red = red;
    }

    public int getGreen() {
        return green;
    }

    public void setGreen(int green) {
        this.green = green;
    }

    public int getBlue() {
        return blue;
    }

    public void setBlue(int blue) {
        this.blue = blue;
    }

    public byte getProgress() {
        return progress;
    }

    public void setProgress(byte progress) {
        this.progress = progress;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public String predictDate() {
        LocalDate date = LocalDate.ofEpochDay(getPredict());
        return date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

    public boolean removeTaskByName(String taskName) {
        return tasks.removeIf(task -> task.name.equals(taskName));
    }

    public boolean removeTask(Task task) {
        return tasks.remove(task);
    }

    public void calculatePredict() {
        long currentTimeMillis = System.currentTimeMillis();
        int currentDay = (int) (currentTimeMillis / (1000 * 60 * 60 * 24));
        int totalDifficulty = 0;
        int taskTrue = 0;

        for (Task task : tasks) {
            if (task.status) {
                totalDifficulty += task.diffic;
                taskTrue++;
            }
        }

        if (taskTrue > 0) {
            this.predict = currentDay + (currentDay - start) * tasks.size() / taskTrue;
        } else {
            this.predict = currentDay;
        }
        this.delay = (int) (this.predict - deadline);
    }

    public int progress() {
        int total = tasks.stream().mapToInt(t -> t.diffic).sum();
        int done = tasks.stream().filter(t -> t.status).mapToInt(t -> t.diffic).sum();
        return total == 0 ? 0 : (int) (100.0 * done / total);
    }
}

class Container implements Serializable {
    private static final long serialVersionUID = 2L;
    private static byte sortby = 0;
    List<Team> teams = new ArrayList<>();
    private List<Project> projects = new ArrayList<>();

    public static byte getSortby() {
        return sortby;
    }

    public static void setSortby(byte sortby) {
        Container.sortby = sortby;
    }

    public void addTeam(Team team) {
        teams.add(team);
    }

    public void removeTeam(Team team) {
        teams.remove(team);
    }

    public List<Team> getTeams() {
        return new ArrayList<>(teams);
    }

    public List<Team> getUserTeams(User user) {
        return teams.stream()
                .filter(team -> team.isMember(user))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public void addProject(Project project) {
        getProjects().add(project);
    }

    public boolean removeProjectByName(String projectName) {
        return getProjects().removeIf(project -> project.getName().equals(projectName));
    }

    public void removeProject(Project project) {
        getProjects().remove(project);
    }

    public User findUserByEmail(String email) {
        return teams.stream()
                .flatMap(team -> team.getMembers().stream())
                .filter(user -> user.getEmail().equals(email))
                .findFirst()
                .orElse(null);
    }

    public void sortProjects() {
        Comparator<Project> comparator = getProjectComparator();
        if (comparator != null) {
            getProjects().sort(comparator);
        }
    }

    public Comparator<Project> getProjectComparator() {
        boolean reverse = getSortby() < 0;
        int key = Math.abs(getSortby());

        Comparator<Project> comparator = switch (key) {
            case 1 -> Comparator.comparing(p -> p.getName().toLowerCase());
            case 2 -> Comparator.comparingLong(Project::getStart);
            case 3 -> Comparator.comparingLong(Project::getDeadline);
            case 4 -> Comparator.comparingInt(p -> p.getTasks().stream().mapToInt(t -> t.diffic).sum());
            case 5 -> Comparator.comparingInt(Project::progress);
            case 6 -> Comparator.comparingLong(Project::getPredict);
            case 7 -> Comparator.comparingInt(Project::getDelay);
            case 8 -> Comparator
                    .comparingInt(Project::getRed)
                    .thenComparingInt(Project::getGreen)
                    .thenComparingInt(Project::getBlue);
            default -> null;
        };

        return (comparator != null && reverse) ? comparator.reversed() : comparator;
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }
}

class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final Logger logger = Logger.getLogger(User.class.getName());

    private String name;
    private String email;
    private String passhash;
    private Map<Team, Role> teamRoles;

    public User(String filename) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            User loaded = (User) ois.readObject();
            this.setName(loaded.getName());
            this.setEmail(loaded.getEmail());
            this.setPasshash(loaded.getPasshash());
            this.setTeamRoles(Objects.requireNonNullElseGet(loaded.getTeamRoles(), HashMap::new));
        }
    }

    public User(String name, String email, String password) {
        this.setName(name);
        this.setEmail(email);
        this.setPasshash(enhash(password));
        this.setTeamRoles(new HashMap<>());
    }

    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        return Pattern.matches("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$", email);
    }

    public static boolean isValidPassword(String password) {
        if (password == null) return false;
        return Pattern.matches("^(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*(),.?\":{}|<>]).{8,}$", password);
    }

    public static String enhash(String input) {
        if (input == null) return null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder(hashBytes.length * 2);
            for (byte b : hashBytes) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            logger.warning("SHA-256 nie dostępne: " + e.getMessage());
            return null;
        }
    }

    public void exportToFile(String filename) {
        File file = new File(filename);
        Optional.ofNullable(file.getParentFile()).ifPresent(File::mkdirs);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(this);
        } catch (IOException e) {
            logger.warning("Eksport użytkownika " + this.getName() + " nie powiódł się: " + e.getMessage());
        }
    }

    private Map<Team, Role> getTeamRolesMap() {
        if (this.getTeamRoles() == null) {
            this.setTeamRoles(new HashMap<>());
        }
        return this.getTeamRoles();
    }

    public void addTeam(Team team, Role role) {
        getTeamRolesMap().put(team, role);
    }

    public void removeTeam(Team team) {
        getTeamRolesMap().remove(team);
    }

    public Role getRoleInTeam(Team team) {
        return getTeamRolesMap().get(team);
    }

    public Set<Team> getTeams() {
        return getTeamRolesMap().keySet();
    }

    public boolean isMemberOfTeam(Team team) {
        return getTeamRolesMap().containsKey(team);
    }

    @Override
    public String toString() {
        return getName() != null ? getName() : "Użytkownik bez nazwy";
    }

    @Override
    public boolean equals(Object obj) {
        return (this == obj) || (obj instanceof User other && getEmail().equals(other.getEmail()));
    }

    @Override
    public int hashCode() {
        return getEmail().hashCode();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasshash() {
        return passhash;
    }

    public void setPasshash(String passhash) {
        this.passhash = passhash;
    }

    public Map<Team, Role> getTeamRoles() {
        return teamRoles;
    }

    public void setTeamRoles(Map<Team, Role> teamRoles) {
        this.teamRoles = teamRoles;
    }
}

class Calendar {
    private final StringBuilder sb;
    private final SimpleDateFormat sdf;

    public Calendar(Container container) {
        sb = new StringBuilder();
        sb.append("BEGIN:VCALENDAR\nVERSION:2.0\nPRODID:-");
        sdf = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        container.getProjects().forEach(this::addEvent);
        sb.append("END:VCALENDAR\n");
    }

    private void addEvent(Project project) {
        sb.append("BEGIN:VEVENT\n");
        sb.append("UID:").append(UUID.randomUUID()).append("\n");
        sb.append("DTSTAMP:").append(sdf.format(new Date())).append("\n");
        sb.append("SUMMARY:").append(escapeText(project.getName())).append("\n");
        sb.append("DESCRIPTION:").append(escapeText(buildDescription(project))).append("\n");
        sb.append("DTSTART:").append(formatDate(project.getStart())).append("\n");
        sb.append("DTEND:").append(formatDate(project.getDeadline())).append("\n");
        sb.append("END:VEVENT\n");
    }

    private String buildDescription(Project project) {
        return project.getDescript() + "\nZadania:\n" +
                project.getTasks().stream()
                        .map(t -> "- " + t.name)
                        .collect(Collectors.joining("\n")) + "\n";
    }

    private String escapeText(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\")
                .replace("\n", "\\n")
                .replace(",", "\\,")
                .replace(";", "\\;");
    }

    private String formatDate(long timestampDays) {
        long millis = timestampDays * 86_400_000L;
        return sdf.format(new Date(millis));
    }

    public void saveToFile(String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(sb.toString());
        }
    }

    public String getICS() {
        return sb.toString();
    }
}
