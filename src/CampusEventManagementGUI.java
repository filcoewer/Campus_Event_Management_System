import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.LineBorder;
import java.util.stream.Collectors;

public class CampusEventManagementGUI {
    private EventManager manager = new EventManager();
    private JFrame frame;
    private User currentUser;

    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setBorder(new LineBorder(Color.GRAY, 1, true));
        btn.setMaximumSize(new Dimension(200, 40));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        return btn;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CampusEventManagementGUI().showLogin());
    }

    private void showLogin() {
        frame = new JFrame("校園活動管理系統 - 登入");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        panel.add(new JLabel("使用者ID："));
        JTextField idField = new JTextField(10);
        panel.add(idField);
        JButton loginButton = createButton("登入");
        panel.add(loginButton);
        loginButton.addActionListener(e -> {
            String id = idField.getText().trim();
            currentUser = manager.getStudent(id);
            if (currentUser == null) {
                currentUser = manager.getOrganizer(id);
            }
            if (currentUser == null) {
                JOptionPane.showMessageDialog(frame, "找不到使用者");
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
        frame.setSize(900, 1024);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void showStudentMenu(Student student) {
        frame = new JFrame("學生選單 - " + student.getName());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JButton viewBtn = createButton("查看活動");
        JButton registerBtn = createButton("報名活動");
        JButton cancelBtn = createButton("取消報名");
        JButton myBtn = createButton("我的活動");
        JButton logoutBtn = createButton("登出");
        panel.add(viewBtn);
        panel.add(Box.createVerticalStrut(10));
        panel.add(registerBtn);
        panel.add(Box.createVerticalStrut(10));
        panel.add(cancelBtn);
        panel.add(Box.createVerticalStrut(10));
        panel.add(myBtn);
        panel.add(Box.createVerticalStrut(10));
        panel.add(logoutBtn);

        viewBtn.addActionListener(e -> showAllEvents());
        registerBtn.addActionListener(e -> {
            String eventId = JOptionPane.showInputDialog(frame, "輸入要報名的活動ID：");
            if (eventId != null) {
                Event ev = manager.getAllEvents().stream()
                        .filter(evnt -> evnt.getId().equals(eventId))
                        .findFirst().orElse(null);
                if (ev != null) {
                    student.registerEvent(ev);
                } else {
                    JOptionPane.showMessageDialog(frame, "找不到活動");
                }
            }
        });
        cancelBtn.addActionListener(e -> {
            String eventId = JOptionPane.showInputDialog(frame, "輸入要取消的活動ID：");
            if (eventId != null) {
                Event ev = student.getRegisteredEvents().stream()
                        .filter(evnt -> evnt.getId().equals(eventId))
                        .findFirst().orElse(null);
                if (ev != null) {
                    student.cancelEvent(ev);
                } else {
                    JOptionPane.showMessageDialog(frame, "在你的報名中找不到此活動");
                }
            }
        });
        myBtn.addActionListener(e -> {
            String msg = student.getRegisteredEvents().stream()
                    .map(ev -> String.format("%s: %s 在 %s %s", ev.getId(), ev.getTitle(), ev.getLocation(), ev.getTime()))
                    .collect(Collectors.joining("\n"));
            if (msg.isEmpty()) msg = "沒有任何報名。";
            JOptionPane.showMessageDialog(frame, msg);
        });
        logoutBtn.addActionListener(e -> {
            manager.saveEvents();
            frame.dispose();
            showLogin();
        });

        frame.getContentPane().add(panel);
        frame.pack();
        frame.setSize(900, 1024);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void showOrganizerMenu(Organizer org) {
        frame = new JFrame("主辦者選單 - " + org.getName());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JButton createBtn = createButton("建立活動");
        JButton viewBtn = createButton("查看我的活動");
        JButton logoutBtn = createButton("登出");
        panel.add(createBtn);
        panel.add(Box.createVerticalStrut(10));
        panel.add(viewBtn);
        panel.add(Box.createVerticalStrut(10));
        panel.add(logoutBtn);

        createBtn.addActionListener(e -> createEvent(org));
        viewBtn.addActionListener(e -> {
            String msg = org.getHostedEvents().stream()
                    .map(ev -> String.format("%s: %s 在 %s %s (%d/%d)", ev.getId(), ev.getTitle(), ev.getLocation(), ev.getTime(), ev.getParticipants().size(), ev.getCapacity()))
                    .collect(Collectors.joining("\n"));
            if (msg.isEmpty()) msg = "沒有活動。";
            JOptionPane.showMessageDialog(frame, msg);
        });
        logoutBtn.addActionListener(e -> {
            manager.saveEvents();
            frame.dispose();
            showLogin();
        });

        frame.getContentPane().add(panel);
        frame.pack();
        frame.setSize(900, 1024);
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
                "ID：", idField,
                "標題：", titleField,
                "地點：", locationField,
                "時間：", timeField,
                "名額：", capacityField
        };
        int option = JOptionPane.showConfirmDialog(frame, message, "建立活動", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                int capacity = Integer.parseInt(capacityField.getText().trim());
                Event e = org.createEvent(idField.getText().trim(), titleField.getText().trim(), locationField.getText().trim(), timeField.getText().trim(), capacity);
                manager.addEvent(e);
                JOptionPane.showMessageDialog(frame, "活動已建立。");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "名額無效");
            }
        }
    }

    private void showAllEvents() {
        String msg = manager.getAllEvents().stream()
                .map(ev -> String.format("%s: %s 在 %s %s (%d/%d)", ev.getId(), ev.getTitle(), ev.getLocation(), ev.getTime(), ev.getParticipants().size(), ev.getCapacity()))
                .collect(Collectors.joining("\n"));
        if (msg.isEmpty()) msg = "沒有活動。";
        JOptionPane.showMessageDialog(frame, msg);
    }
}
