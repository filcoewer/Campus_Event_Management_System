import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
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

    private static class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if ("開放中".equals(value)) {
                c.setForeground(Color.GREEN.darker());
            } else if ("額滿".equals(value)) {
                c.setForeground(Color.RED);
            } else {
                c.setForeground(isSelected ? table.getSelectionForeground() : Color.BLACK);
            }
            return c;
        }
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
        idField.setPreferredSize(new Dimension(400, 40));
        idField.setFont(scaled(idField.getFont(), 1.5f));
        gbc.gridx = 1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(idField, gbc);

        JLabel pwLabel = new JLabel("密碼：");
        pwLabel.setFont(scaled(pwLabel.getFont(), 1.5f));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(pwLabel, gbc);

        JPasswordField pwField = new JPasswordField();
        pwField.setPreferredSize(new Dimension(400, 40));
        pwField.setFont(scaled(pwField.getFont(), 1.5f));
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(pwField, gbc);

        JButton loginButton = new JButton("登入");
        loginButton.setPreferredSize(new Dimension(225, 60));
        loginButton.setFont(scaled(loginButton.getFont(), 1.5f));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(loginButton, gbc);
        loginButton.addActionListener(e -> {
            String id = idField.getText().trim();
            String pw = new String(pwField.getPassword());
            currentUser = manager.getStudent(id);
            if (currentUser == null) {
                currentUser = manager.getOrganizer(id);
            }
            if (currentUser == null || !currentUser.checkPassword(pw)) {
                JOptionPane.showMessageDialog(frame, "帳號或密碼錯誤");
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
        JButton searchBtn = createButton("搜尋活動");
        JButton registerBtn = createButton("報名活動");
        JButton cancelBtn = createButton("取消報名");
        JButton logoutBtn = createButton("登出");
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(searchBtn);
        buttonPanel.add(Box.createVerticalStrut(20));
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
        String[] cols = {"ID", "名稱", "地點", "時間", "人數", "狀態"};
        DefaultTableModel regModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable registeredTable = new JTable(regModel);
        registeredTable.setFont(scaled(registeredTable.getFont(), 1.2f));
        registeredTable.getTableHeader().setFont(scaled(registeredTable.getTableHeader().getFont(), 1.2f));
        registeredTable.getColumnModel().getColumn(5).setCellRenderer(new StatusRenderer());
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
        allTable.getColumnModel().getColumn(5).setCellRenderer(new StatusRenderer());
        allPanel.add(new JScrollPane(allTable), BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new GridLayout(2,1));
        rightPanel.add(registeredPanel);
        rightPanel.add(allPanel);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buttonPanel, rightPanel);
        split.setResizeWeight(0);
        split.setEnabled(false);
        searchBtn.addActionListener(e -> {
            String q = JOptionPane.showInputDialog(frame, "輸入關鍵字或日期(YYYY-MM-DD)：");
            if (q != null) {
                java.util.List<Event> result = manager.searchEvents(q);
                String msg = result.stream()
                        .map(ev -> String.format("%s: %s 在 %s %s (%d/%d) [%s]",
                                ev.getId(), ev.getTitle(), ev.getLocation(), ev.getTime(),
                                ev.getParticipants().size(), ev.getCapacity(), ev.getStatus()))
                        .collect(Collectors.joining("\n"));
                if (msg.isEmpty()) msg = "沒有符合的活動。";
                JOptionPane.showMessageDialog(frame, msg);
            }
        });
        registerBtn.addActionListener(e -> {
            String eventId = JOptionPane.showInputDialog(frame, "輸入要報名的活動ID：");
            if (eventId != null) {
                Event ev = manager.getAllEvents().stream()
                        .filter(evnt -> evnt.getId().equals(eventId))
                        .findFirst().orElse(null);
                if (ev != null) {
                    if ("已結束".equals(ev.getStatus())) {
                        JOptionPane.showMessageDialog(frame, "活動已結束，無法報名");
                    } else if ("額滿".equals(ev.getStatus())) {
                        JOptionPane.showMessageDialog(frame, "活動已額滿");
                    } else {
                        student.registerEvent(ev);
                        refreshStudentTables(student, regModel, allModel);
                    }
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
                    if ("已結束".equals(ev.getStatus())) {
                        JOptionPane.showMessageDialog(frame, "活動已結束，無法取消");
                    } else {
                        student.cancelEvent(ev);
                        refreshStudentTables(student, regModel, allModel);
                    }
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
        JButton exportBtn = createButton("匯出名單");
        JButton logoutBtn = createButton("登出");
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(createBtn);
        buttonPanel.add(Box.createVerticalStrut(20));
        buttonPanel.add(exportBtn);
        buttonPanel.add(Box.createVerticalStrut(20));
        buttonPanel.add(logoutBtn);
        buttonPanel.add(Box.createVerticalGlue());

        JPanel rightPanel = new JPanel(new BorderLayout());
        JLabel hostLabel = new JLabel("我的活動", SwingConstants.CENTER);
        hostLabel.setFont(scaled(hostLabel.getFont(), 1.2f));
        rightPanel.add(hostLabel, BorderLayout.NORTH);
        String[] cols = {"ID", "名稱", "地點", "時間", "人數", "狀態"};
        DefaultTableModel hostModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable hostedTable = new JTable(hostModel);
        hostedTable.setFont(scaled(hostedTable.getFont(), 1.2f));
        hostedTable.getTableHeader().setFont(scaled(hostedTable.getTableHeader().getFont(), 1.2f));
        hostedTable.getColumnModel().getColumn(5).setCellRenderer(new StatusRenderer());
        rightPanel.add(new JScrollPane(hostedTable), BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buttonPanel, rightPanel);
        split.setResizeWeight(0);
        split.setEnabled(false);

        createBtn.addActionListener(e -> {
            createEvent(org);
            refreshOrganizerTable(org, hostModel);
        });
        exportBtn.addActionListener(e -> {
            String eventId = JOptionPane.showInputDialog(frame, "輸入活動ID以匯出名單：");
            if (eventId != null) {
                Event ev = org.getHostedEvents().stream()
                        .filter(evnt -> evnt.getId().equals(eventId))
                        .findFirst().orElse(null);
                if (ev != null) {
                    manager.exportParticipants(ev, eventId + "_participants.csv");
                    JOptionPane.showMessageDialog(frame, "已匯出到 " + eventId + "_participants.csv");
                } else {
                    JOptionPane.showMessageDialog(frame, "找不到活動");
                }
            }
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
                String dateStr = timeField.getText().trim();
                try {
                    java.time.LocalDate date = java.time.LocalDate.parse(dateStr);
                    if (date.isBefore(java.time.LocalDate.now())) {
                        JOptionPane.showMessageDialog(frame, "日期無效");
                        return;
                    }
                } catch (java.time.format.DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(frame, "日期無效");
                    return;
                }
                Event e = org.createEvent(idField.getText().trim(), titleField.getText().trim(), locationField.getText().trim(), dateStr, capacity);
                manager.addEvent(e);
                JOptionPane.showMessageDialog(frame, "活動已建立。");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "名額無效");
            }
        }
    }

    private void refreshStudentTables(Student student, DefaultTableModel regModel, DefaultTableModel allModel) {
        regModel.setRowCount(0);
        for (Event ev : student.getRegisteredEvents().stream()
                .sorted(Event.STATUS_DATE_COMPARATOR)
                .collect(java.util.stream.Collectors.toList())) {
            regModel.addRow(new Object[]{ev.getId(), ev.getTitle(), ev.getLocation(), ev.getTime(), ev.getParticipants().size() + "/" + ev.getCapacity(), ev.getStatus()});
        }
        allModel.setRowCount(0);
        for (Event ev : manager.getSortedEvents()) {
            allModel.addRow(new Object[]{ev.getId(), ev.getTitle(), ev.getLocation(), ev.getTime(), ev.getParticipants().size() + "/" + ev.getCapacity(), ev.getStatus()});
        }
    }

    private void refreshOrganizerTable(Organizer org, DefaultTableModel model) {
        model.setRowCount(0);
        for (Event ev : org.getHostedEvents().stream()
                .sorted(Event.STATUS_DATE_COMPARATOR)
                .collect(java.util.stream.Collectors.toList())) {
            model.addRow(new Object[]{ev.getId(), ev.getTitle(), ev.getLocation(), ev.getTime(), ev.getParticipants().size() + "/" + ev.getCapacity(), ev.getStatus()});
        }
    }

    private void showAllEvents() {
        String msg = manager.getSortedEvents().stream()
                .map(ev -> String.format("%s: %s 在 %s %s (%d/%d) [%s]", ev.getId(), ev.getTitle(), ev.getLocation(), ev.getTime(), ev.getParticipants().size(), ev.getCapacity(), ev.getStatus()))
                .collect(Collectors.joining("\n"));
        if (msg.isEmpty()) msg = "沒有活動。";
        JOptionPane.showMessageDialog(frame, msg);
    }
}
