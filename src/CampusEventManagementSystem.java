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
            System.out.print("Password: ");
            String pw = scanner.nextLine();
            User user = manager.getStudent(id);
            if (user == null) {
                user = manager.getOrganizer(id);
            }
            if (user == null || !user.checkPassword(pw)) {
                System.out.println("Invalid credentials");
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
                manager.getSortedEvents().stream()
                        .forEach(e -> System.out.printf("%s: %s at %s %s to %s (%d/%d) [%s]\n",
                                e.getId(), e.getTitle(), e.getLocation(), e.getStartTime(), e.getEndTime(),
                                e.getParticipants().size(), e.getCapacity(), e.getStatus()));
                break;
            case "2":
                System.out.print("Keyword or date (YYYY-MM-DD): ");
                String query = scanner.nextLine();
                manager.searchEvents(query).forEach(ev ->
                        System.out.printf("%s: %s at %s %s to %s (%d/%d) [%s]\n",
                                ev.getId(), ev.getTitle(), ev.getLocation(), ev.getStartTime(), ev.getEndTime(),
                                ev.getParticipants().size(), ev.getCapacity(), ev.getStatus()));
                break;
            case "3":
                System.out.print("Enter event ID to register: ");
                String eventId = scanner.nextLine();
                Event e = manager.getAllEvents().stream()
                        .filter(ev -> ev.getId().equals(eventId))
                        .findFirst().orElse(null);
                if (e != null) {
                    if ("已結束".equals(e.getStatus())) {
                        System.out.println("Event already ended");
                    } else if ("額滿".equals(e.getStatus())) {
                        System.out.println("Event is full");
                    } else {
                        student.registerEvent(e);
                    }
                } else {
                    System.out.println("Event not found");
                }
                break;
            case "4":
                System.out.print("Enter event ID to cancel: ");
                String cancelId = scanner.nextLine();
                e = student.getRegisteredEvents().stream().filter(ev -> ev.getId().equals(cancelId)).findFirst().orElse(null);
                if (e != null) {
                    student.cancelEvent(e);
                } else {
                    System.out.println("Event not found in your registrations");
                }
                break;
            case "5":
                for (Event reg : student.getRegisteredEvents()) {
                    System.out.printf("%s: %s at %s %s to %s\n", reg.getId(), reg.getTitle(), reg.getLocation(), reg.getStartTime(), reg.getEndTime());
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
                System.out.print("Start time (YYYY-MM-DD HH:mm): ");
                String start = scanner.nextLine();
                System.out.print("End time (YYYY-MM-DD HH:mm): ");
                String end = scanner.nextLine();
                java.time.LocalDateTime startDt;
                java.time.LocalDateTime endDt;
                try {
                    startDt = java.time.LocalDateTime.parse(start, java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                    endDt = java.time.LocalDateTime.parse(end, java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                    if (startDt.isAfter(endDt) || startDt.isBefore(java.time.LocalDateTime.now())) {
                        System.out.println("Invalid date/time range");
                        return;
                    }
                } catch (java.time.format.DateTimeParseException ex) {
                    System.out.println("Invalid date");
                    return;
                }
                System.out.print("Capacity: ");
                int capacity;
                try {
                    capacity = Integer.parseInt(scanner.nextLine());
                } catch (NumberFormatException ex) {
                    System.out.println("Invalid capacity");
                    return;
                }
                Event e = org.createEvent(id, title, location, start, end, capacity);
                manager.addEvent(e);
                System.out.println("Event created.");
                break;
            case "2":
                for (Event event : org.getHostedEvents().stream()
                        .sorted(Event.STATUS_DATE_COMPARATOR)
                        .collect(java.util.stream.Collectors.toList())) {
                    System.out.printf("%s: %s at %s %s to %s (%d/%d) [%s]\n",
                            event.getId(), event.getTitle(), event.getLocation(), event.getStartTime(), event.getEndTime(),
                            event.getParticipants().size(), event.getCapacity(), event.getStatus());
                }
                break;
            case "3":
                System.out.print("Enter event ID to edit: ");
                String editId = scanner.nextLine();
                Event toEdit = org.getHostedEvents().stream().filter(ev -> ev.getId().equals(editId)).findFirst().orElse(null);
                if (toEdit == null) {
                    System.out.println("Event not found");
                    break;
                }
                System.out.print("New title: ");
                title = scanner.nextLine();
                System.out.print("New location: ");
                location = scanner.nextLine();
                System.out.print("New start time (YYYY-MM-DD HH:mm): ");
                String newStart = scanner.nextLine();
                System.out.print("New end time (YYYY-MM-DD HH:mm): ");
                String newEnd = scanner.nextLine();
                try {
                    java.time.LocalDateTime sdt = java.time.LocalDateTime.parse(newStart, java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                    java.time.LocalDateTime edt = java.time.LocalDateTime.parse(newEnd, java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                    if (sdt.isAfter(edt) || sdt.isBefore(java.time.LocalDateTime.now())) {
                        System.out.println("Invalid date/time range");
                        break;
                    }
                } catch (java.time.format.DateTimeParseException ex) {
                    System.out.println("Invalid date");
                    break;
                }
                System.out.print("New capacity: ");
                try {
                    capacity = Integer.parseInt(scanner.nextLine());
                } catch (NumberFormatException ex) {
                    System.out.println("Invalid capacity");
                    break;
                }
                org.editEvent(toEdit, title, location, newStart, newEnd, capacity);
                break;
            case "4":
                System.out.print("Enter event ID to view participants: ");
                String pid = scanner.nextLine();
                Event pEvent = org.getHostedEvents().stream().filter(ev -> ev.getId().equals(pid)).findFirst().orElse(null);
                if (pEvent != null) {
                    org.listParticipants(pEvent);
                } else {
                    System.out.println("Event not found");
                }
                break;
            case "5":
                System.out.print("Enter event ID to export participants: ");
                String expId = scanner.nextLine();
                Event expEvent = org.getHostedEvents().stream().filter(ev -> ev.getId().equals(expId)).findFirst().orElse(null);
                if (expEvent != null) {
                    manager.exportParticipants(expEvent, expId + "_participants.csv");
                    System.out.println("Exported to " + expId + "_participants.csv");
                } else {
                    System.out.println("Event not found");
                }
                break;
            default:
                System.out.println("Unknown choice");
        }
    }
}
