import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Event {
    private String id;
    private String title;
    private String location;
    private String startTime;
    private String endTime;
    private int capacity;
    private Organizer organizer;
    private List<Student> participants = new ArrayList<>();

    public Event(String id, String title, String location, String startTime, String endTime, int capacity, Organizer organizer) {
        this.id = id;
        this.title = title;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
        this.capacity = capacity;
        this.organizer = organizer;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getLocation() { return location; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    public String getTime() { return startTime + " - " + endTime; }
    public int getCapacity() { return capacity; }
    public Organizer getOrganizer() { return organizer; }
    public List<Student> getParticipants() { return participants; }

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public String getStatus() {
        try {
            LocalDateTime end = LocalDateTime.parse(endTime, FMT);
            if (end.isBefore(LocalDateTime.now())) {
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

    public void edit(String title, String location, String startTime, String endTime, int capacity) {
        this.title = title;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
        if (capacity >= participants.size()) {
            this.capacity = capacity;
        }
    }

    private LocalDateTime parseDate() {
        try {
            return LocalDateTime.parse(startTime, FMT);
        } catch (DateTimeParseException e) {
            return LocalDateTime.MAX;
        }
    }

    public int getStatusRank() {
        switch (getStatus()) {
            case "開放中":
                return 0;
            case "額滿":
                return 1;
            case "已結束":
                return 2;
            default:
                return 3;
        }
    }

    public static final java.util.Comparator<Event> STATUS_DATE_COMPARATOR =
            java.util.Comparator.comparingInt(Event::getStatusRank)
                    .thenComparing(Event::parseDate);

    public boolean register(Student s) {
        if (getStatus().equals("已結束") || participants.size() >= capacity) {
            return false;
        }
        if (!participants.contains(s)) {
            participants.add(s);
            return true;
        }
        return false;
    }

    public void cancel(Student s) {
        if (getStatus().equals("已結束")) {
            return;
        }
        participants.remove(s);
    }
}
