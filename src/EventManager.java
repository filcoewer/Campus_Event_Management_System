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

    public java.util.List<Event> getSortedEvents() {
        return events.values().stream()
                .sorted(Event.STATUS_DATE_COMPARATOR)
                .collect(java.util.stream.Collectors.toList());
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

    public List<Event> searchEvents(String query) {
        java.time.LocalDate parsedDate;
        try {
            parsedDate = java.time.LocalDate.parse(query);
        } catch (java.time.format.DateTimeParseException ex) {
            parsedDate = null;
        }
        final java.time.LocalDate date = parsedDate;
        return events.values().stream()
                .filter(ev -> {
                    if (date != null) {
                        try {
                            java.time.LocalDate d = java.time.LocalDate.parse(ev.getStartTime().substring(0, 10));
                            return d.equals(date);
                        } catch (java.time.format.DateTimeParseException e) {
                            return false;
                        }
                    }
                    return ev.getTitle().toLowerCase().contains(query.toLowerCase());
                })
                .sorted(Event.STATUS_DATE_COMPARATOR)
                .collect(java.util.stream.Collectors.toList());
    }

    private void loadUsers() {
        File f = new File(USERS_FILE);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 4) continue;
                String role = parts[0];
                String id = parts[1];
                String name = parts[2];
                String password = parts[3];
                if ("student".equalsIgnoreCase(role)) {
                    students.put(id, new Student(id, name, password));
                } else if ("organizer".equalsIgnoreCase(role)) {
                    organizers.put(id, new Organizer(id, name, password));
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
                if (parts.length < 7) continue;
                String id = parts[0];
                String title = parts[1];
                String location = parts[2];
                String start = parts[3];
                String end = parts[4];
                int capacity;
                try {
                    capacity = Integer.parseInt(parts[5]);
                } catch (NumberFormatException ex) {
                    continue; // ignore malformed line
                }
                String organizerId = parts[6];
                Organizer organizer = organizers.get(organizerId);
                if (organizer != null) {
                    Event event = new Event(id, title, location, start, end, capacity, organizer);
                    events.put(id, event);
                    organizer.getHostedEvents().add(event);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveEvents() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(EVENTS_FILE))) {
            for (Event e : events.values()) {
                pw.printf("%s,%s,%s,%s,%s,%d,%s%n",
                        e.getId(), e.getTitle(), e.getLocation(),
                        e.getStartTime(), e.getEndTime(),
                        e.getCapacity(), e.getOrganizer().getId());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void exportParticipants(Event event, String file) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            for (Student s : event.getParticipants()) {
                pw.printf("%s,%s%n", s.getId(), s.getName());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
