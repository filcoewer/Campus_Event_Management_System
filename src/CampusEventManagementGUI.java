import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.stream.Collectors;

public class CampusEventManagementGUI {
    private EventManager manager = new EventManager();
    private JFrame frame;
    private User currentUser;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CampusEventManagementGUI().showLogin());
    }

    private void showLogin() {
        frame = new JFrame("Campus Event Management System - Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        panel.add(new JLabel("User ID:"));
        JTextField idField = new JTextField(10);
        panel.add(idField);
        JButton loginButton = new JButton("Login");
        panel.add(loginButton);
        loginButton.addActionListener(e -> {
            String id = idField.getText().trim();
            currentUser = manager.getStudent(id);
            if (currentUser == null) {
                currentUser = manager.getOrganizer(id);
            }
            if (currentUser == null) {
                JOptionPane.showMessageDialog(frame, "User not found");
            } else {
                frame.dispose();
                if (currentUser instanceof Student) {
                    showStudentMenu((Student) currentUser);
                } else {
                    showOrganizerMenu((Organizer) currentUser);
                }
            }
        });
        frame.getContentPane().add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void showStudentMenu(Student student) {
        frame = new JFrame("Student Menu - " + student.getName());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0,1));
        JButton viewBtn = new JButton("View Events");
        JButton registerBtn = new JButton("Register Event");
        JButton cancelBtn = new JButton("Cancel Event");
        JButton myBtn = new JButton("My Events");
        JButton logoutBtn = new JButton("Logout");
        panel.add(viewBtn);
        panel.add(registerBtn);
        panel.add(cancelBtn);
        panel.add(myBtn);
        panel.add(logoutBtn);

        viewBtn.addActionListener(e -> showAllEvents());
        registerBtn.addActionListener(e -> {
            String eventId = JOptionPane.showInputDialog(frame, "Enter event ID to register:");
            if (eventId != null) {
                Event ev = manager.getAllEvents().stream()
                        .filter(evnt -> evnt.getId().equals(eventId))
                        .findFirst().orElse(null);
                if (ev != null) {
                    student.registerEvent(ev);
                } else {
                    JOptionPane.showMessageDialog(frame, "Event not found");
                }
            }
        });
        cancelBtn.addActionListener(e -> {
            String eventId = JOptionPane.showInputDialog(frame, "Enter event ID to cancel:");
            if (eventId != null) {
                Event ev = student.getRegisteredEvents().stream()
                        .filter(evnt -> evnt.getId().equals(eventId))
                        .findFirst().orElse(null);
                if (ev != null) {
                    student.cancelEvent(ev);
                } else {
                    JOptionPane.showMessageDialog(frame, "Event not found in your registrations");
                }
            }
        });
        myBtn.addActionListener(e -> {
            String msg = student.getRegisteredEvents().stream()
                    .map(ev -> String.format("%s: %s at %s %s", ev.getId(), ev.getTitle(), ev.getLocation(), ev.getTime()))
                    .collect(Collectors.joining("\n"));
            if (msg.isEmpty()) msg = "No registrations.";
            JOptionPane.showMessageDialog(frame, msg);
        });
        logoutBtn.addActionListener(e -> {
            manager.saveEvents();
            frame.dispose();
            showLogin();
        });

        frame.getContentPane().add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void showOrganizerMenu(Organizer org) {
        frame = new JFrame("Organizer Menu - " + org.getName());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0,1));
        JButton createBtn = new JButton("Create Event");
        JButton viewBtn = new JButton("View My Events");
        JButton logoutBtn = new JButton("Logout");
        panel.add(createBtn);
        panel.add(viewBtn);
        panel.add(logoutBtn);

        createBtn.addActionListener(e -> createEvent(org));
        viewBtn.addActionListener(e -> {
            String msg = org.getHostedEvents().stream()
                    .map(ev -> String.format("%s: %s at %s %s (%d/%d)", ev.getId(), ev.getTitle(), ev.getLocation(), ev.getTime(), ev.getParticipants().size(), ev.getCapacity()))
                    .collect(Collectors.joining("\n"));
            if (msg.isEmpty()) msg = "No events.";
            JOptionPane.showMessageDialog(frame, msg);
        });
        logoutBtn.addActionListener(e -> {
            manager.saveEvents();
            frame.dispose();
            showLogin();
        });

        frame.getContentPane().add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void createEvent(Organizer org) {
        JTextField idField = new JTextField();
        JTextField titleField = new JTextField();
        JTextField locationField = new JTextField();
        JTextField timeField = new JTextField();
        JTextField capacityField = new JTextField();
        Object[] message = {
                "ID:", idField,
                "Title:", titleField,
                "Location:", locationField,
                "Time:", timeField,
                "Capacity:", capacityField
        };
        int option = JOptionPane.showConfirmDialog(frame, message, "Create Event", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                int capacity = Integer.parseInt(capacityField.getText().trim());
                Event e = org.createEvent(idField.getText().trim(), titleField.getText().trim(), locationField.getText().trim(), timeField.getText().trim(), capacity);
                manager.addEvent(e);
                JOptionPane.showMessageDialog(frame, "Event created.");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid capacity");
            }
        }
    }

    private void showAllEvents() {
        String msg = manager.getAllEvents().stream()
                .map(ev -> String.format("%s: %s at %s %s (%d/%d)", ev.getId(), ev.getTitle(), ev.getLocation(), ev.getTime(), ev.getParticipants().size(), ev.getCapacity()))
                .collect(Collectors.joining("\n"));
        if (msg.isEmpty()) msg = "No events.";
        JOptionPane.showMessageDialog(frame, msg);
    }
}
