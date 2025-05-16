import javax.swing.*;
import java.io.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.swing.SwingUtilities;



class Task{
	String name;
	boolean status;
	byte diffic;
	public Task(String name, boolean status, byte diffic) {
	    this.name = name;
	    this.status = status;
	    this.diffic = diffic;
	}
};

class Project {
	byte progress;
	int start;
	int deadline;
	int predict;
	int delay;
	List<Task> tasks;
	String name;
	String descript;
	int color;

	public Project(String name, String descript, int start, int deadline) {
        this.name = name;
        this.descript = descript;
        this.start = start;
        this.deadline = deadline;
        this.tasks = new ArrayList<>();
        this.progress = 0;
        this.predict = 0;
        this.delay = predict - deadline;
        this.color = 16777215; //aqua
    }

	public void addTask(Task task) {
        tasks.add(task);
        System.out.println("Dodano zadanie: " + task.name + ", Liczba zadań: " + tasks.size());
    }
    public void removeTask(Task task) {
    	tasks.remove(task);
    }

private void calculatePredict() {
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
            this.delay = this.predict - deadline;
        } else {
            this.predict = currentDay;
            this.delay = this.predict - deadline;
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


class Container{
	byte sortby;
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
                comparator = Comparator.comparingInt(p -> p.start);
                break;
            case 2:
                comparator = Comparator.comparingInt(p -> p.deadline);
                break;
            case 3:
                comparator = Comparator.comparingInt(p -> 
                    p.tasks.stream().mapToInt(t -> t.diffic).sum());
                break;
            case 4:
                comparator = Comparator.comparingInt(p -> p.progress);
                break;
            case 5:
                comparator = Comparator.comparingInt(p -> p.predict);
                break;
            case 6:
                comparator = Comparator.comparingInt(p -> p.delay);
                break;
            case 7:
                comparator = Comparator.comparingInt(p -> p.color);
                break;
            default:
                return;
        }

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
            throw new FileNotFoundException("1");
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
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }


    private boolean isValidPassword(String password) {
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
            // Tworzymy obiekt MessageDigest dla algorytmu SHA-256
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

// class Calendar{

// }
