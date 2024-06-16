import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;

public class DDay {
////전체 틀 만들어!!////
	private JInternalFrame frame;
    private JDesktopPane desktopPane;

    public DDay() {
        setUIFont(new FontUIResource(new Font("Malgun Gothic", Font.PLAIN, 18)));

        frame = new JInternalFrame("D-day ");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        desktopPane = new JDesktopPane();
        frame.add(desktopPane, BorderLayout.CENTER);

        loadDataFromDatabase();

        frame.setVisible(true);
    }
/////////
    private void loadDataFromDatabase() {
        Map<String, List<ScheduleItem>> scheduleMap = new HashMap<>();

        try (Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "SYSTEM", "foroopcurie");
             PreparedStatement ps = conn.prepareStatement("SELECT subject, year, month, day, time, task, importance, completed FROM Schedule")) {

            ResultSet rs = ps.executeQuery();

            LocalDate currentDate = LocalDate.now();

            while (rs.next()) {
                String subject = rs.getString("subject");
                int year = rs.getInt("year");
                int month = rs.getInt("month");
                int day = rs.getInt("day");
                String time = rs.getString("time");
                String task = rs.getString("task");
                int importance = rs.getInt("importance");
                boolean completed = rs.getInt("completed") == 1;

                LocalDate targetDate = LocalDate.of(year, month, day);
                long dDay = calculateDaysUntil(targetDate);

                // 날짜가 이미 지난 일정_건너 뛰어!
                if (targetDate.isBefore(currentDate)) {
                    continue;
                }
                
                String formattedTime = " (" + time.substring(0, 2) + ":" + time.substring(2) + ")";//시간!

                ScheduleItem item = new ScheduleItem(subject, targetDate, time, task, importance, completed);
                String dDayText = (dDay == 0) ? "D-day" : "D-" + dDay;

                if (!completed) {
                    scheduleMap.computeIfAbsent(dDayText, k -> new ArrayList<>()).add(item);
                }
            }

            List<Map.Entry<String, List<ScheduleItem>>> entryList = new ArrayList<>(scheduleMap.entrySet());
            // D-day가 빠른 순서대로 정렬 (D-day부터 시작하도록)
            Collections.sort(entryList, Comparator.comparingLong(e -> (e.getKey().equals("D-day") ? 0 : Long.parseLong(e.getKey().substring(2)))));

            int yOffset = 10;
            for (Map.Entry<String, List<ScheduleItem>> entry : entryList) {
                List<ScheduleItem> itemList = entry.getValue();
                // 중요도에 따라 내림차순으로 정렬
                itemList.sort(Comparator.comparingInt(ScheduleItem::getImportance).reversed());

                JInternalFrame internalFrame = new JInternalFrame(entry.getKey(), true, true, true, true);
                internalFrame.setSize(400, 300);
                internalFrame.setLocation(20, yOffset);
                internalFrame.setLayout(new BorderLayout());

                JPanel dDayPanel = new JPanel();
                dDayPanel.setLayout(new BoxLayout(dDayPanel, BoxLayout.Y_AXIS));

                for (ScheduleItem item : itemList) {
                    JPanel itemPanel = new JPanel(new BorderLayout());
                    JCheckBox taskCheckBox = new JCheckBox(item.getSubject() + ") " + item.getTask() + " " + item.getTime());
                    taskCheckBox.setFont(new Font("Malgun Gothic", Font.PLAIN, 18));
                    taskCheckBox.setSelected(item.isCompleted());
                    toggleStrikeThrough(taskCheckBox, item.isCompleted());
                    taskCheckBox.addActionListener(e -> toggleStrikeThrough(taskCheckBox));

                    JLabel dateLabel = new JLabel();
                    if (entry.getKey().equals("D-day")) {
                        dateLabel.setText("오늘");
                    } else {
                        dateLabel.setText("D-" + entry.getKey().substring(2) + " / " + item.getDate().getMonthValue() + "." + item.getDate().getDayOfMonth());
                    }
                    dateLabel.setFont(new Font("Malgun Gothic", Font.PLAIN, 16));

                    itemPanel.add(taskCheckBox, BorderLayout.CENTER);
                    itemPanel.add(dateLabel, BorderLayout.LINE_END);
                    itemPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

                    dDayPanel.add(itemPanel);
                }

                internalFrame.add(new JScrollPane(dDayPanel));
                desktopPane.add(internalFrame);
                internalFrame.setVisible(true);

                yOffset += 40;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setUIFont(FontUIResource f) {
        java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource) {
                UIManager.put(key, f);
            }
        }
    }

    private static long calculateDaysUntil(LocalDate targetDate) {
        LocalDate currentDate = LocalDate.now();
        return ChronoUnit.DAYS.between(currentDate, targetDate);
    }
/////취소선!////
    private static void toggleStrikeThrough(JCheckBox checkBox) {
        boolean completed = checkBox.isSelected();
        String text = checkBox.getText();
        String task = text.substring(text.indexOf(")") + 2, text.lastIndexOf(" "));

        updateDatabase(task, completed);

        if (completed) {
            checkBox.setText("<html><strike>" + text + "</strike></html>");
        } else {
            checkBox.setText(text.replaceAll("<html><strike>", "").replaceAll("</strike></html>", ""));
        }
    }

    private static void toggleStrikeThrough(JCheckBox checkBox, boolean isCompleted) {
        String text = checkBox.getText();
        if (isCompleted) {
            checkBox.setText("<html><strike>" + text + "</strike></html>");
        } else {
            checkBox.setText(text.replaceAll("<html><strike>", "").replaceAll("</strike></html>", ""));
        }
    }
////반영하기!////
    private static void updateDatabase(String task, boolean completed) {
        try (Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "SYSTEM", "foroopcurie");
             PreparedStatement ps = conn.prepareStatement("UPDATE Schedule SET completed = ? WHERE task = ?")) {

            ps.setInt(1, completed ? 1 : 0);
            ps.setString(2, task);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
////////
    private static class ScheduleItem {
        private final String subject;
        private final LocalDate date;
        private final String time;
        private final String task;
        private final int importance;
        private final boolean completed;

        public ScheduleItem(String subject, LocalDate date, String time, String task, int importance, boolean completed) {
            this.subject = subject;
            this.date = date;
            this.time = time;
            this.task = task;
            this.importance = importance;
            this.completed = completed;
        }

        public String getSubject() {
            return subject;
        }

        public LocalDate getDate() {
            return date;
        }

        public String getTime() {
            return time;
        }

        public String getTask() {
            return task;
        }

        public int getImportance() {
            return importance;
        }

        public boolean isCompleted() {
            return completed;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DDay::new);
    }
}
