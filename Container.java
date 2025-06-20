import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Comparator;

//darkmode
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
};

class Project implements Serializable {
    private static final long serialVersionUID = 1L;
    byte progress;
    long start;
    long deadline;
    long predict;
    int delay;
    List<Task> tasks;
    String name;
    String descript;
    int red;
    int green;
    int blue;
    Team team;

    public Project(String name, String description, long start, long deadline, int red, int green, int blue) {
        this.name = name;
        this.descript = description;
        this.start = start;
        this.deadline = deadline;
        this.tasks = new ArrayList<>();
        this.progress = 0;
        this.predict = 0;
        this.delay = (int) (predict - deadline) / 86400;
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.team = null;
        calculatePredict(); // Obliczamy przewidywaną datę na podstawie zadań

    }

    public Project(String name, String description, long start, long deadline) {
        this(name, description, start, deadline, 0, 0, 0);
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }


    public String predictDate() {
        LocalDate date = LocalDate.ofEpochDay(predict);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return date.format(formatter);
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

    public boolean removeTaskByName(String taskName) {
        for (Task task : tasks) {
            if (task.name.equals(taskName)) {
                return tasks.remove(task);
            }
        }
        return false;
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
            if (task.status == true) {
                totalDifficulty += task.diffic;
                taskTrue++;
            }
        }

        if (taskTrue > 0) {
            this.predict = currentDay + (currentDay - start) * tasks.size() / taskTrue;
            this.delay = (int) (this.predict - deadline);
        } else {
            this.predict = currentDay;
            this.delay = (int) (this.predict - deadline);
        }
    }

    public int progress() {
        int total = 0;
        int done = 0;
        for (Task t : tasks) {
            total += t.diffic;
            if (t.status) {
                done += t.diffic;
            }
        }
        if (total == 0) return 0;
        return (int) (100.0 * done / total);
    }


};


class Container implements Serializable {
    private static final long serialVersionUID = 2L;
    public byte sortby;
    List<Project> projects = new ArrayList<>();
    List<Team> teams = new ArrayList<>();

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
                   .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    public void addProject(Project project) {
        projects.add(project);
    }

    public boolean removeProjectByName(String projectName) {
        for (Project project : projects) {
            if (project.name.equals(projectName)) {
                return projects.remove(project);
            }
        }
        return false;
    }

    public void removeProject(Project project) {
        projects.remove(project);
    }

    public User findUserByEmail(String email) {
    for (Team team : teams) {
        for (User user : team.getMembers()) {
            if (user.getEmail().equals(email)) {
                return user;
            }
        }
    }
    return null;
    }


 public void sortProjects() {
    Comparator<Project> comparator = getProjectComparator();
    if (comparator != null) {
        projects.sort(comparator);
    }
}


public Comparator<Project> getProjectComparator() {
    boolean reverse = sortby < 0;
    int key = Math.abs(sortby);

    Comparator<Project> comparator = switch (key) {
        case 1 -> Comparator.comparing(p -> p.name.toLowerCase());
        case 2 -> Comparator.comparingLong(p -> p.start);
        case 3 -> Comparator.comparingLong(p -> p.deadline);
        case 4 -> Comparator.comparingInt(p -> p.tasks.stream().mapToInt(t -> t.diffic).sum());
        case 5 -> Comparator.comparingInt(Project::progress);
        case 6 -> Comparator.comparingLong(p -> p.predict);
        case 7 -> Comparator.comparingInt(p -> p.delay);
        case 8 -> Comparator
                    .comparingInt((Project p) -> p.red)
                    .thenComparingInt(p -> p.green)
                    .thenComparingInt(p -> p.blue);
        default -> null;
    };

    return (comparator != null && reverse) ? comparator.reversed() : comparator;
}
}

class User implements Serializable {
    private static final long serialVersionUID = 1L;

    String name;
    String email;
    String passhash;
    Map<Team, Role> teamRoles;

    public User(String filename) throws IOException, ClassNotFoundException {
        File file = new File(filename);
        if (!file.exists()) {
            throw new FileNotFoundException("Plik użytkownika nie znaleziony: " + filename);
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            User loaded = (User) ois.readObject();
            this.name = loaded.name;
            this.email = loaded.email;
            this.passhash = loaded.passhash;
            this.teamRoles = loaded.teamRoles != null ? loaded.teamRoles : new HashMap<>();
        }
    }

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.passhash = enhash(password);
        this.teamRoles = new HashMap<>();
    }

    public User(String name, String email, String password, String filename) throws IOException {
        if (!isValidEmail(email)) {
            System.err.println("Niepoprawny email podany dla użytkownika: " + name);
            return;
        }
        if (!isValidPassword(password)) {
            System.err.println("Niepoprawne hasło podane dla użytkownika: " + name);
            return;
        }
        this.name = name;
        this.email = email;
        this.passhash = enhash(password);
        this.teamRoles = new HashMap<>();

    }

    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }


    public static boolean isValidPassword(String password) {
        if (password == null) return false;
        // Hasło musi mieć co najmniej 8 znaków, zawierać jedną dużą literę, jedną cyfrę i jeden znak specjalny
        String passwordRegex = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*(),.?\":{}|<>]).{8,}$";
        Pattern pattern = Pattern.compile(passwordRegex);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    public String getEmail (){return email;}
    public void exportToFile(String filename) {
        File file = new File(filename);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(this);
        } catch (IOException e) {
            System.err.println("Eksport użytkownika " + this.name + " do " + filename + " nie powiódł się: " + e.getMessage());
        }
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
            System.err.println("SHA-256 Algorithm not found: " + e.getMessage());
            return null;
        }
    }

    private Map<Team, Role> getTeamRolesMap() {
        if (this.teamRoles == null) {
            this.teamRoles = new HashMap<>();
        }
        return this.teamRoles;
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
        return name != null ? name : "Użytkownik bez nazwy";
    }

    public static void main(String[] args) {

    }
    @Override
public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    User other = (User) obj;
    return email.equals(other.email); // lub inne unikalne pole
}

@Override
public int hashCode() {
    return email.hashCode();
}

}


class Calendar {
    private StringBuilder sb;
    private SimpleDateFormat sdf;

    public Calendar(Container container) {
        sb = new StringBuilder();
        sb.append("BEGIN:VCALENDAR\n");
        sb.append("VERSION:2.0\n");
        sb.append("PRODID:-//ProjectManager//ICS Generator//EN\n");

        sdf = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        for (Project project : container.projects) {
            addEvent(project);
        }

        sb.append("END:VCALENDAR\n");
    }

    private void addEvent(Project project) {
        sb.append("BEGIN:VEVENT\n");
        sb.append("UID:").append(UUID.randomUUID().toString()).append("\n");
        sb.append("DTSTAMP:").append(sdf.format(new Date())).append("\n");
        sb.append("SUMMARY:").append(escapeText(project.name)).append("\n");
        sb.append("DESCRIPTION:").append(escapeText(buildDescription(project))).append("\n");
        sb.append("DTSTART:").append(formatDate(project.start)).append("\n");
        sb.append("DTEND:").append(formatDate(project.deadline)).append("\n");
        sb.append("END:VEVENT\n");
    }

    private String buildDescription(Project project) {
        StringBuilder desc = new StringBuilder();
        desc.append(project.descript).append("\n");
        desc.append("Zadania:\n");
        for (Task task : project.tasks) {
            desc.append("- ").append(task.name).append("\n");
        }
        return desc.toString();
    }

    private String escapeText(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\")
                .replace("\n", "\\n")
                .replace(",", "\\,")
                .replace(";", "\\;");
    }

    private String formatDate(long timestampDays) {
        long millis = timestampDays * 86400000L; // Konwersja dni Unix do milisekund
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