import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



class Task implements Serializable{
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

	public Project(String name, String description, long start, long deadline, int red, int green, int blue)
 {
        this.name = name;
        this.descript = description;
        this.start = start;
        this.deadline = deadline;
        this.tasks = new ArrayList<>();
        this.progress = 0;
        this.predict = 0;
        this.delay = (int) (predict-deadline)/86400;
        this.red = red; 
        this.green = green;
        this.blue = blue;


    }

    public Project(String name, String description, long start, long deadline) {
        this(name, description, start, deadline, 0, 0, 0); 
    }


    public String predictDate() {
        LocalDate date = LocalDate.ofEpochDay(predict); 
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return date.format(formatter);
}

    public void addTask(Task task) {
        tasks.add(task);
    }
    public void removeTask(Task task) {
    	tasks.remove(task);
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


class Container implements Serializable{
    private static final long serialVersionUID = 1L;
	public byte sortby;
    	List<Project> projects = new ArrayList<>();
	public void addProject(Project project) {
        projects.add(project);
    }

    public void removeProject(Project project) {
    	projects.remove(project);
    }

public void sortProjects() {
    Comparator<Project> comparator = null;
    boolean reverse = sortby < 0;
    int key = Math.abs(sortby);

    switch (key) {
        case 0:
            comparator = Comparator.comparing(p -> p.name.toLowerCase());
            break;
        case 1:
            comparator = Comparator.comparingLong(p -> p.start);
            break;
        case 2:
            comparator = Comparator.comparingLong(p -> p.deadline);
            break;
        case 3:
            comparator = Comparator.comparingInt(p -> 
                p.tasks.stream().mapToInt(t -> t.diffic).sum());
            break;
        case 4:
            comparator = Comparator.comparingInt(p -> p.progress);
            break;
        case 5:
            comparator = Comparator.comparingLong(p -> p.predict);
            break;
        case 6:
            comparator = Comparator.comparingInt(p -> p.delay);
            break;
        case 7:
            comparator = Comparator
                .comparingInt((Project p) -> p.red)
                .thenComparingInt(p -> p.green)
                .thenComparingInt(p -> p.blue);
            break;
        default:
            return;
    }

    if (reverse && comparator != null) {
        comparator = comparator.reversed();
    }

    projects.sort(comparator);


	if (comparator != null) {
		if (reverse) {
		                projects.sort(comparator.reversed());
		            } else {
		                projects.sort(comparator);
		            }
		        }

			}
		}


class User implements Serializable {
private static final long serialVersionUID = 1L;

String name;
String email;
String passhash;
boolean darkmode;
public User(String filename) throws IOException, ClassNotFoundException {
        File file = new File(filename);
        if (!file.exists()) {
            throw new FileNotFoundException("Error");
        }

try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            User loaded = (User) ois.readObject();
            this.name = loaded.name;
            this.email = loaded.email;
            this.passhash = loaded.passhash;
            this.darkmode = loaded.darkmode;
        }
    }
    public User(String name, String email, String password, boolean darkmode) {
        this.name = name;
        this.email = email;
        this.passhash = enhash(password);
        this.darkmode = darkmode;
    }

   public User(String name, String email, String password, boolean darkmode, String filename) throws IOException {
        // Sprawdzamy poprawność e-maila
        if (!isValidEmail(email)) {
            System.out.println("Niepoprawny adres e-mail.");
            return;  // Jeśli e-mail jest niepoprawny, nie tworzymy obiektu
        }

        // Sprawdzamy poprawność hasła
        if (!isValidPassword(password)) {
            System.out.println("Hasło musi zawierać co najmniej 8 znaków, jedną wielką literę, cyfrę i znak specjalny.");
            return;  // Jeśli hasło jest niepoprawne, nie tworzymy obiektu
        }
}
    public static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }


    public static void main(String[] args) {
        
    } boolean isValidPassword(String password) {
        // Hasło musi mieć co najmniej 8 znaków, zawierać jedną dużą literę, jedną cyfrę i jeden znak specjalny
        String passwordRegex = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*(),.?\":{}|<>]).{8,}$";
        Pattern pattern = Pattern.compile(passwordRegex);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }



    public void exportToFile(String filename) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(this);
        } catch (IOException e) {
            System.err.println("Export failed: " + e.getMessage());
        }
    }


    public static String enhash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder(hashBytes.length * 2); // Wstępna alokacja pamięci
            for (byte b : hashBytes) {
                hexString.append(String.format("%02x", b));
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            // W przypadku błędu z algorytmem (co nie powinno się zdarzyć)
            return null;
        }
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