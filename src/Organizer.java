import java.util.ArrayList;
import java.util.List;

public class Organizer extends User {
    private List<Event> hostedEvents = new ArrayList<>();

    public Organizer(String id, String name) {
        super(id, name);
    }

    public Event createEvent(String id, String title, String location, String time, int capacity) {
        Event e = new Event(id, title, location, time, capacity, this);
        hostedEvents.add(e);
        return e;
    }

    public List<Event> getHostedEvents() {
        return hostedEvents;
    }

    @Override
    public void displayMenu() {
        System.out.println("1. Create Event\n2. View My Events\n0. Logout");
    }
}
