package dudeozy;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;

public class DDay {

    private JInternalFrame frame;
    private JDesktopPane desktopPane;
    private JScrollPane scrollPane; // 스크롤 기능 추가

    public DDay() {
        setUIFont(new FontUIResource(new Font("Malgun Gothic", Font.PLAIN, 18)));

        frame = new JInternalFrame("D-day ");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        desktopPane = new JDesktopPane();
        desktopPane.setLayout(new GridLayout(0, 4, 10, 10)); // 4개의 열로 배치하고 간격 추가
        scrollPane = new JScrollPane(desktopPane); // 스크롤 가능한 패널로 변환
        frame.add(scrollPane, BorderLayout.CENTER);

        loadDataFromDatabase();

        frame.setVisible(true);
    }

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
                String timeStr = rs.getString("time");

                // timeStr이 null이면 continue하여 다음 레코드로 넘어감
                if (timeStr == null) {
                    continue;
                }

                LocalTime time = LocalTime.of(Integer.parseInt(timeStr.substring(0, 2)), Integer.parseInt(timeStr.substring(2))); // LocalTime으로 시간 파싱
                String task = rs.getString("task");
                int importance = rs.getInt("importance");
                boolean completed = rs.getInt("completed") == 1;

                LocalDate targetDate = LocalDate.of(year, month, day);
                long dDay = calculateDaysUntil(targetDate);

                // 날짜가 이미 지난 일정_건너 뛰어!
                if (targetDate.isBefore(currentDate)) {
                    continue;
                }

                String formattedTime = " (" + time.format(DateTimeFormatter.ofPattern("HH:mm")) + ")"; // 시간 포맷팅 추가

                ScheduleItem item = new ScheduleItem(subject, targetDate, time, task, importance, completed);
                String dDayText = (dDay == 0) ? "D-day" : "D-" + dDay;

                if (!completed) {
                    scheduleMap.computeIfAbsent(dDayText, k -> new ArrayList<>()).add(item);
                }
            }

            List<Map.Entry<String, List<ScheduleItem>>> entryList = new ArrayList<>(scheduleMap.entrySet());
            // D-day가 빠른 순서대로 정렬 (D-day부터 시작하도록)
            Collections.sort(entryList, Comparator.comparingLong(e -> (e.getKey().equals("D-day") ? 0 : Long.parseLong(e.getKey().substring(2)))));

            for (Map.Entry<String, List<ScheduleItem>> entry : entryList) {
                List<ScheduleItem> itemList = entry.getValue();
                // 중요도에 따라 내림차순으로 정렬
                itemList.sort(Comparator.comparingInt(ScheduleItem::getImportance).reversed());

                JPanel dDayPanel = new JPanel();
                dDayPanel.setLayout(new BoxLayout(dDayPanel, BoxLayout.Y_AXIS));

                for (ScheduleItem item : itemList) {
                    JPanel itemPanel = new JPanel(new BorderLayout());
                    String formattedTime = " (" + item.getTime().format(DateTimeFormatter.ofPattern("HH:mm")) + ")";
                    JCheckBox taskCheckBox = new JCheckBox(item.getSubject() + ") " + item.getTask() + formattedTime);
                    taskCheckBox.setFont(new Font("Malgun Gothic", Font.PLAIN, 18));
                    taskCheckBox.setSelected(item.isCompleted());
                    toggleStrikeThrough(taskCheckBox, item.isCompleted());
                    taskCheckBox.addActionListener(e -> toggleStrikeThrough(taskCheckBox));

                    JLabel dateLabel = new JLabel();
                    if (entry.getKey().equals("D-day")) {
                        dateLabel.setText("");
                    } else {
                        dateLabel.setText(item.getDate().getMonthValue() + "." + item.getDate().getDayOfMonth());
                    }
                    dateLabel.setFont(new Font("Malgun Gothic", Font.PLAIN, 16));

                    itemPanel.add(taskCheckBox, BorderLayout.CENTER);
                    itemPanel.add(dateLabel, BorderLayout.LINE_END);
                    itemPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

                    dDayPanel.add(itemPanel);
                }

                JInternalFrame internalFrame = new JInternalFrame(entry.getKey(), true, true, true, true);
                internalFrame.setSize(400, 300);
                internalFrame.setLayout(new BorderLayout());
                internalFrame.add(new JScrollPane(dDayPanel));
                internalFrame.setVisible(true);

                desktopPane.add(internalFrame); // desktopPane에 추가
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

    private static class ScheduleItem {
        private final String subject;
        private final LocalDate date;
        private final LocalTime time;
        private final String task;
        private final int importance;
        private final boolean completed;

        public ScheduleItem(String subject, LocalDate date, LocalTime time, String task, int importance, boolean completed) {
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

        public LocalTime getTime() {
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
    
    JInternalFrame getInternalFrame() {
    	return frame;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DDay::new);
    }
}
