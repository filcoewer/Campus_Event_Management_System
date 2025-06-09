import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class Event {
    private String id;
    private String title;
    private String location;
    private String time;
    private int capacity;
    private Organizer organizer;
    private List<Student> participants = new ArrayList<>();

    public Event(String id, String title, String location, String time, int capacity, Organizer organizer) {
        this.id = id;
        this.title = title;
        this.location = location;
        this.time = time;
        this.capacity = capacity;
        this.organizer = organizer;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getLocation() { return location; }
    public String getTime() { return time; }
    public int getCapacity() { return capacity; }
    public Organizer getOrganizer() { return organizer; }
    public List<Student> getParticipants() { return participants; }
    public String getStatus() {
        try {
            LocalDate date = LocalDate.parse(time);
            if (date.isBefore(LocalDate.now())) {
                return "已結束";
            }
        } catch (DateTimeParseException e) {
            // ignore malformed date
        }
        if (participants.size() >= capacity) {
            return "額滿";
        }
        return "開放中";
    }

    public void edit(String title, String location, String time, int capacity) {
        this.title = title;
        this.location = location;
        this.time = time;
        if (capacity >= participants.size()) {
            this.capacity = capacity;
        }
    }

    public boolean register(Student s) {
        if (participants.size() >= capacity) {
            return false;
        }
        if (!participants.contains(s)) {
            participants.add(s);
            return true;
        }
        return false;
    }

    public void cancel(Student s) {
        participants.remove(s);
    }
}
