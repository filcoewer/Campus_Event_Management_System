import java.io.*;
import java.util.*;

public class EventManager {
    private Map<String, Student> students = new HashMap<>();
    private Map<String, Organizer> organizers = new HashMap<>();
    private Map<String, Event> events = new HashMap<>();

    private static final String USERS_FILE = "users.csv";
    private static final String EVENTS_FILE = "events.csv";

    public EventManager() {
        loadUsers();
        loadEvents();
    }

    public Collection<Event> getAllEvents() {
        return events.values();
    }

    public Student getStudent(String id) {
        return students.get(id);
    }

    public Organizer getOrganizer(String id) {
        return organizers.get(id);
    }

    public void addEvent(Event e) {
        events.put(e.getId(), e);
    }

    private void loadUsers() {
        File f = new File(USERS_FILE);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 3) continue;
                String role = parts[0];
                String id = parts[1];
                String name = parts[2];
                if ("student".equalsIgnoreCase(role)) {
                    students.put(id, new Student(id, name));
                } else if ("organizer".equalsIgnoreCase(role)) {
                    organizers.put(id, new Organizer(id, name));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadEvents() {
        File f = new File(EVENTS_FILE);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 6) continue;
                String id = parts[0];
                String title = parts[1];
                String location = parts[2];
                String time = parts[3];
                int capacity = Integer.parseInt(parts[4]);
                String organizerId = parts[5];
                Organizer organizer = organizers.get(organizerId);
                if (organizer != null) {
                    Event event = new Event(id, title, location, time, capacity, organizer);
                    events.put(id, event);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveEvents() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(EVENTS_FILE))) {
            for (Event e : events.values()) {
                pw.printf("%s,%s,%s,%s,%d,%s%n", e.getId(), e.getTitle(), e.getLocation(), e.getTime(), e.getCapacity(), e.getOrganizer().getId());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
