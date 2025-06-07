import java.util.Scanner;

public class CampusEventManagementSystem {
    private static EventManager manager = new EventManager();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Welcome to Campus Event Management System");
        while (true) {
            System.out.print("Enter user ID (or 'exit'): ");
            String id = scanner.nextLine();
            if ("exit".equalsIgnoreCase(id)) {
                manager.saveEvents();
                System.out.println("Goodbye!");
                break;
            }
            User user = manager.getStudent(id);
            if (user == null) {
                user = manager.getOrganizer(id);
            }
            if (user == null) {
                System.out.println("User not found");
                continue;
            }
            handleUser(user);
        }
    }

    private static void handleUser(User user) {
        while (true) {
            user.displayMenu();
            System.out.print("Choice: ");
            String choice = scanner.nextLine();
            if (choice.equals("0")) {
                break;
            }
            if (user instanceof Student) {
                handleStudent((Student) user, choice);
            } else if (user instanceof Organizer) {
                handleOrganizer((Organizer) user, choice);
            }
        }
    }

    private static void handleStudent(Student student, String choice) {
        switch (choice) {
            case "1":
                for (Event e : manager.getAllEvents()) {
                    System.out.printf("%s: %s at %s %s (%d/%d)\n", e.getId(), e.getTitle(), e.getLocation(), e.getTime(), e.getParticipants().size(), e.getCapacity());
                }
                break;
            case "2":
                System.out.print("Enter event ID to register: ");
                String eventId = scanner.nextLine();
                Event e = manager.getAllEvents().stream().filter(ev -> ev.getId().equals(eventId)).findFirst().orElse(null);
                if (e != null) {
                    student.registerEvent(e);
                } else {
                    System.out.println("Event not found");
                }
                break;
            case "3":
                System.out.print("Enter event ID to cancel: ");
                String cancelId = scanner.nextLine();
                e = student.getRegisteredEvents().stream().filter(ev -> ev.getId().equals(cancelId)).findFirst().orElse(null);
                if (e != null) {
                    student.cancelEvent(e);
                } else {
                    System.out.println("Event not found in your registrations");
                }
                break;
            case "4":
                for (Event reg : student.getRegisteredEvents()) {
                    System.out.printf("%s: %s at %s %s\n", reg.getId(), reg.getTitle(), reg.getLocation(), reg.getTime());
                }
                break;
            default:
                System.out.println("Unknown choice");
        }
    }

    private static void handleOrganizer(Organizer org, String choice) {
        switch (choice) {
            case "1":
                System.out.print("Event ID: ");
                String id = scanner.nextLine();
                System.out.print("Title: ");
                String title = scanner.nextLine();
                System.out.print("Location: ");
                String location = scanner.nextLine();
                System.out.print("Time: ");
                String time = scanner.nextLine();
                System.out.print("Capacity: ");
                int capacity = Integer.parseInt(scanner.nextLine());
                Event e = org.createEvent(id, title, location, time, capacity);
                manager.addEvent(e);
                System.out.println("Event created.");
                break;
            case "2":
                for (Event event : org.getHostedEvents()) {
                    System.out.printf("%s: %s at %s %s (%d/%d)\n", event.getId(), event.getTitle(), event.getLocation(), event.getTime(), event.getParticipants().size(), event.getCapacity());
                }
                break;
            default:
                System.out.println("Unknown choice");
        }
    }
}
