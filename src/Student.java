import java.util.ArrayList;
import java.util.List;

public class Student extends User {
    private List<Event> registeredEvents = new ArrayList<>();

    public Student(String id, String name) {
        super(id, name);
    }

    public void registerEvent(Event e) {
        if (!registeredEvents.contains(e) && e.register(this)) {
            registeredEvents.add(e);
            System.out.println("Registered for event: " + e.getTitle());
        } else {
            System.out.println("Cannot register for event: " + e.getTitle());
        }
    }

    public void cancelEvent(Event e) {
        if (registeredEvents.contains(e)) {
            registeredEvents.remove(e);
            e.cancel(this);
            System.out.println("Cancelled registration for event: " + e.getTitle());
        }
    }

    public List<Event> getRegisteredEvents() {
        return registeredEvents;
    }

    @Override
    public void displayMenu() {
        System.out.println("1. View Events\n2. Register Event\n3. Cancel Event\n4. My Events\n0. Logout");
    }
}
