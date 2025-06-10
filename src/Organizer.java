import java.util.ArrayList;
import java.util.List;

public class Organizer extends User {
    private List<Event> hostedEvents = new ArrayList<>();

    public Organizer(String id, String name, String password) {
        super(id, name, password);
    }

    public Event createEvent(String id, String title, String location, String startTime, String endTime, int capacity) {
        Event e = new Event(id, title, location, startTime, endTime, capacity, this);
        hostedEvents.add(e);
        return e;
    }

    public void editEvent(Event e, String title, String location, String startTime, String endTime, int capacity) {
        if (e.getOrganizer() != this) {
            System.out.println("You are not the organizer of this event");
            return;
        }
        if (hostedEvents.contains(e)) {
            e.edit(title, location, startTime, endTime, capacity);
            System.out.println("Event updated.");
        } else {
            System.out.println("Event not found");
        }
    }

    public void listParticipants(Event e) {
        if (!hostedEvents.contains(e)) {
            System.out.println("Event not found");
            return;
        }
        if (e.getParticipants().isEmpty()) {
            System.out.println("No participants.");
        } else {
            for (Student s : e.getParticipants()) {
                System.out.println(s.getName() + " (" + s.getId() + ")");
            }
        }
    }

    public List<Event> getHostedEvents() {
        return hostedEvents;
    }

    @Override
    public void displayMenu() {
        System.out.println("1. Create Event\n2. View My Events\n3. Edit Event\n4. View Participants\n5. Export Participants\n0. Logout");
    }
}
