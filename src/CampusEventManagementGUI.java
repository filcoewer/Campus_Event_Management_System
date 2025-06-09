import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.util.stream.Collectors;

public class CampusEventManagementGUI {
    private EventManager manager = new EventManager();
    private JFrame frame;
    private User currentUser;

    private Font scaled(Font f, float factor) {
        return f.deriveFont(f.getSize2D() * factor);
    }

    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setBorder(new LineBorder(Color.GRAY, 1, true));
        btn.setPreferredSize(new Dimension(150, 100));
        btn.setMaximumSize(new Dimension(150, 100));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFont(scaled(btn.getFont(), 1.5f));
        return btn;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CampusEventManagementGUI().showLogin());
    }

    private void showLogin() {
        frame = new JFrame("校園活動管理系統 - 登入");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel idLabel = new JLabel("使用者ID：");
        idLabel.setFont(scaled(idLabel.getFont(), 1.5f));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(idLabel, gbc);

        JTextField idField = new JTextField();
        idField.setPreferredSize(new Dimension(50, 40));
        idField.setFont(scaled(idField.getFont(), 1.5f));
        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(idField, gbc);

        JButton loginButton = new JButton("登入");
        loginButton.setPreferredSize(new Dimension(225, 60));
        loginButton.setFont(scaled(loginButton.getFont(), 1.5f));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(loginButton, gbc);
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
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        JButton registerBtn = createButton("報名活動");
        JButton cancelBtn = createButton("取消報名");
        JButton logoutBtn = createButton("登出");
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(registerBtn);
        buttonPanel.add(Box.createVerticalStrut(20));
        buttonPanel.add(cancelBtn);
        buttonPanel.add(Box.createVerticalStrut(20));
        buttonPanel.add(logoutBtn);
        buttonPanel.add(Box.createVerticalGlue());

        JPanel registeredPanel = new JPanel(new BorderLayout());
        JLabel regLabel = new JLabel("已報名活動", SwingConstants.CENTER);
        regLabel.setFont(scaled(regLabel.getFont(), 1.2f));
        registeredPanel.add(regLabel, BorderLayout.NORTH);
        String[] cols = {"ID", "名稱", "地點", "時間", "人數"};
        DefaultTableModel regModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable registeredTable = new JTable(regModel);
        registeredTable.setFont(scaled(registeredTable.getFont(), 1.2f));
        registeredTable.getTableHeader().setFont(scaled(registeredTable.getTableHeader().getFont(), 1.2f));
        registeredPanel.add(new JScrollPane(registeredTable), BorderLayout.CENTER);

        JPanel allPanel = new JPanel(new BorderLayout());
        JLabel allLabel = new JLabel("所有活動", SwingConstants.CENTER);
        allLabel.setFont(scaled(allLabel.getFont(), 1.2f));
        allPanel.add(allLabel, BorderLayout.NORTH);
        DefaultTableModel allModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable allTable = new JTable(allModel);
        allTable.setFont(scaled(allTable.getFont(), 1.2f));
        allTable.getTableHeader().setFont(scaled(allTable.getTableHeader().getFont(), 1.2f));
        allPanel.add(new JScrollPane(allTable), BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new GridLayout(2,1));
        rightPanel.add(registeredPanel);
        rightPanel.add(allPanel);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buttonPanel, rightPanel);
        split.setResizeWeight(0);
        split.setEnabled(false);
        registerBtn.addActionListener(e -> {
            String eventId = JOptionPane.showInputDialog(frame, "輸入要報名的活動ID：");
            if (eventId != null) {
                Event ev = manager.getAllEvents().stream()
                        .filter(evnt -> evnt.getId().equals(eventId))
                        .findFirst().orElse(null);
                if (ev != null) {
                    student.registerEvent(ev);
                    refreshStudentTables(student, regModel, allModel);
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
                    refreshStudentTables(student, regModel, allModel);
                } else {
                    JOptionPane.showMessageDialog(frame, "在你的報名中找不到此活動");
                }
            }
        });
        logoutBtn.addActionListener(e -> {
            manager.saveEvents();
            frame.dispose();
            showLogin();
        });

        refreshStudentTables(student, regModel, allModel);
        frame.getContentPane().add(split);
        frame.pack();
        frame.setSize(900, 1024);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void showOrganizerMenu(Organizer org) {
        frame = new JFrame("主辦者選單 - " + org.getName());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        JButton createBtn = createButton("建立活動");
        JButton logoutBtn = createButton("登出");
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(createBtn);
        buttonPanel.add(Box.createVerticalStrut(20));
        buttonPanel.add(logoutBtn);
        buttonPanel.add(Box.createVerticalGlue());

        JPanel rightPanel = new JPanel(new BorderLayout());
        JLabel hostLabel = new JLabel("我的活動", SwingConstants.CENTER);
        hostLabel.setFont(scaled(hostLabel.getFont(), 1.2f));
        rightPanel.add(hostLabel, BorderLayout.NORTH);
        String[] cols = {"ID", "名稱", "地點", "時間", "人數"};
        DefaultTableModel hostModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable hostedTable = new JTable(hostModel);
        hostedTable.setFont(scaled(hostedTable.getFont(), 1.2f));
        hostedTable.getTableHeader().setFont(scaled(hostedTable.getTableHeader().getFont(), 1.2f));
        rightPanel.add(new JScrollPane(hostedTable), BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buttonPanel, rightPanel);
        split.setResizeWeight(0);
        split.setEnabled(false);

        createBtn.addActionListener(e -> {
            createEvent(org);
            refreshOrganizerTable(org, hostModel);
        });
        logoutBtn.addActionListener(e -> {
            manager.saveEvents();
            frame.dispose();
            showLogin();
        });

        refreshOrganizerTable(org, hostModel);
        frame.getContentPane().add(split);
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
        JLabel idLabel = new JLabel("ID：");
        JLabel titleLabel = new JLabel("標題：");
        JLabel locLabel = new JLabel("地點：");
        JLabel timeLabel = new JLabel("時間：");
        JLabel capLabel = new JLabel("名額：");
        idLabel.setFont(scaled(idLabel.getFont(), 1.2f));
        titleLabel.setFont(scaled(titleLabel.getFont(), 1.2f));
        locLabel.setFont(scaled(locLabel.getFont(), 1.2f));
        timeLabel.setFont(scaled(timeLabel.getFont(), 1.2f));
        capLabel.setFont(scaled(capLabel.getFont(), 1.2f));
        idField.setFont(scaled(idField.getFont(), 1.2f));
        titleField.setFont(scaled(titleField.getFont(), 1.2f));
        locationField.setFont(scaled(locationField.getFont(), 1.2f));
        timeField.setFont(scaled(timeField.getFont(), 1.2f));
        capacityField.setFont(scaled(capacityField.getFont(), 1.2f));
        Object[] message = {
                idLabel, idField,
                titleLabel, titleField,
                locLabel, locationField,
                timeLabel, timeField,
                capLabel, capacityField
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

    private void refreshStudentTables(Student student, DefaultTableModel regModel, DefaultTableModel allModel) {
        regModel.setRowCount(0);
        for (Event ev : student.getRegisteredEvents()) {
            regModel.addRow(new Object[]{ev.getId(), ev.getTitle(), ev.getLocation(), ev.getTime(), ev.getParticipants().size() + "/" + ev.getCapacity()});
        }
        allModel.setRowCount(0);
        for (Event ev : manager.getAllEvents()) {
            allModel.addRow(new Object[]{ev.getId(), ev.getTitle(), ev.getLocation(), ev.getTime(), ev.getParticipants().size() + "/" + ev.getCapacity()});
        }
    }

    private void refreshOrganizerTable(Organizer org, DefaultTableModel model) {
        model.setRowCount(0);
        for (Event ev : org.getHostedEvents()) {
            model.addRow(new Object[]{ev.getId(), ev.getTitle(), ev.getLocation(), ev.getTime(), ev.getParticipants().size() + "/" + ev.getCapacity()});
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
