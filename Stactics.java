package dudeozy;

import java.awt.*;
import javax.swing.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import java.awt.*;
import javax.swing.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class Stactics {
    //JFrame jf;
	JInternalFrame jf;
    JTabbedPane tabpane;
    Connection conn;

    public Stactics() {
        // 데이터베이스 연결 설정
        try {
            String url = "jdbc:oracle:thin:@localhost:1521:xe"; // 데이터베이스 URL을 설정합니다.
            String username = "SYSTEM"; // 데이터베이스 사용자 이름을 설정합니다.
            String password = "foroopcurie"; // 데이터베이스 비밀번호를 설정합니다.
            conn = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        //jf = new JFrame();
        jf = new JInternalFrame();
        tabpane = new JTabbedPane();

        JPanel one = new JPanel(new GridLayout(0, 1, 10, 10));
        JPanel two = new JPanel(new BorderLayout());
        JPanel three = new JPanel(new BorderLayout());
        JPanel four = new JPanel(new BorderLayout());

        tabpane.addTab("일", one);
        tabpane.addTab("주", two);
        tabpane.addTab("월", three);
        tabpane.addTab("년", four);

        // 데이터베이스에서 데이터를 가져와 진행상황을 설정
        List<ProgressData> progressDataList = fetchProgressData();

        // 일별 진행상황 게이지
        JLabel label1 = new JLabel("일별 진행상황");
        one.add(label1);

        // 오늘 날짜 계산
        LocalDate today = LocalDate.now();

        // 오늘 날짜의 데이터 필터링
        List<ProgressData> todayData = progressDataList.stream()
                .filter(d -> d.year == today.getYear() && d.month == today.getMonthValue() && d.day == today.getDayOfMonth())
                .collect(Collectors.toList());

        // 전체 진행 상황 게이지 추가
        JProgressBar overallProgressBar = new JProgressBar();
        overallProgressBar.setValue(calculateOverallProgress(progressDataList));
        overallProgressBar.setString("전체 진행상황: " + overallProgressBar.getValue() + "%");
        overallProgressBar.setStringPainted(true);
        one.add(overallProgressBar);

        // 과목별 진행 상황 합산 및 게이지 추가
        Map<String, List<ProgressData>> todaySubjectDataMap = todayData.stream()
                .collect(Collectors.groupingBy(d -> d.subject));

        for (Map.Entry<String, List<ProgressData>> entry : todaySubjectDataMap.entrySet()) {
            String subject = entry.getKey();
            List<ProgressData> subjectData = entry.getValue();
            int subjectProgress = (int) (subjectData.stream().mapToInt(d -> d.progress).average().orElse(0));

            JProgressBar progressBar = new JProgressBar();
            progressBar.setValue(subjectProgress);
            progressBar.setString(subject + " 진행상황: " + subjectProgress + "%");
            progressBar.setStringPainted(true);
            one.add(progressBar);
        }

        // 주별 진행상황
        JLabel label21 = new JLabel("주별 진행상황");
        two.add(label21, BorderLayout.NORTH);

        JSplitPane splitPane1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new WeekBarChartPanel(progressDataList), new PieChartPanel(progressDataList, "week"));
        splitPane1.setResizeWeight(0.5); // Adjust the split ratio
        two.add(splitPane1, BorderLayout.CENTER);

        // 월별 진행상황
        JLabel label31 = new JLabel("월별 진행상황");
        three.add(label31, BorderLayout.NORTH);

        JSplitPane splitPane2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new MonthBarChartPanel(progressDataList), new PieChartPanel(progressDataList, "month"));
        splitPane2.setResizeWeight(0.5); // Adjust the split ratio
        three.add(splitPane2, BorderLayout.CENTER);

        // 연별 진행상황
        JLabel label41 = new JLabel("연별 진행상황");
        four.add(label41, BorderLayout.NORTH);

        JSplitPane splitPane3 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new YearBarChartPanel(progressDataList), new PieChartPanel(progressDataList, "year"));
        splitPane3.setResizeWeight(0.5); // Adjust the split ratio
        four.add(splitPane3, BorderLayout.CENTER);

        jf.getContentPane().add(tabpane, BorderLayout.CENTER);
        jf.setSize(1000, 800);
        jf.setMaximumSize(new Dimension(1000, 800));
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setVisible(true);
    }

    // 진행 상황 데이터를 데이터베이스에서 가져오는 메소드
    private List<ProgressData> fetchProgressData() {
        List<ProgressData> dataList = new ArrayList<>();
        try {
            String query = "SELECT subject, task, completed, year, month, day " +
                           "FROM Schedule";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                String subject = rs.getString("subject");
                String task = rs.getString("task");
                int completed = rs.getInt("completed");
                int year = rs.getInt("year");
                int month = rs.getInt("month");
                int day = rs.getInt("day");
                String weekday = getWeekday(year, month, day);
                int progress = completed == 1 ? 100 : 0;
                dataList.add(new ProgressData(subject, task, progress, year, month, day, weekday));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dataList;
    }

    // 요일 계산 메소드
    private String getWeekday(int year, int month, int day) {
        LocalDate date = LocalDate.of(year, month, day);
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek.toString().substring(0, 3).toUpperCase();
    }
    
    // 전체 진행 상황을 계산하는 메소드
    private int calculateOverallProgress(List<ProgressData> data) {
        int totalProgress = 0;
        for (ProgressData d : data) {
            totalProgress += d.progress;
        }
        return totalProgress / data.size();
    }

    class ProgressData {
        String subject;
        String task;
        int progress;
        int year;
        int month;
        int day;
        String weekday;

        ProgressData(String subject, String task, int progress, int year, int month, int day, String weekday) {
            this.subject = subject;
            this.task = task;
            this.progress = progress;
            this.year = year;
            this.month = month;
            this.day = day;
            this.weekday = weekday;
        }
    }

    class WeekBarChartPanel extends JPanel {
        Map<String, Integer> weekData;

        WeekBarChartPanel(List<ProgressData> data) {
            LocalDate now = LocalDate.now();
            WeekFields weekFields = WeekFields.of(Locale.getDefault());
            int currentWeek = now.get(weekFields.weekOfYear());
            int currentYear = now.getYear();

            this.weekData = data.stream()
                                .filter(d -> d.progress == 100)
                                .filter(d -> {
                                    LocalDate date = LocalDate.of(d.year, d.month, d.day);
                                    return date.get(weekFields.weekOfYear()) == currentWeek && d.year == currentYear;
                                })
                                .collect(Collectors.groupingBy(d -> d.weekday, Collectors.summingInt(d -> 1)));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            String[] labels = {"MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"};
            int[] values = new int[labels.length];

            for (int i = 0; i < labels.length; i++) {
                values[i] = weekData.getOrDefault(labels[i], 0);
            }

            int width = getWidth();
            int height = getHeight();
            int barWidth = width / values.length;
            int maxBarHeight = height - 30;
            int maxValue = Arrays.stream(values).max().orElse(1);

            g.setColor(Color.BLUE);
            for (int i = 0; i < values.length; i++) {
                int barHeight = (int) ((values[i] / (double) maxValue) * maxBarHeight);
                int x = i * barWidth;
                int y = height - barHeight - 20;
                g.fillRect(x, y, barWidth - 10, barHeight);
                g.drawString(labels[i], x + (barWidth / 2) - 5, height - 5);
            }
        }
    }

    class MonthBarChartPanel extends JPanel {
        Map<String, Integer> monthData;

        MonthBarChartPanel(List<ProgressData> data) {
            LocalDate now = LocalDate.now();
            int currentMonth = now.getMonthValue();
            int currentYear = now.getYear();
            WeekFields weekFields = WeekFields.of(Locale.getDefault());

            this.monthData = data.stream()
                                 .filter(d -> d.progress == 100)
                                 .filter(d -> d.year == currentYear && d.month == currentMonth)
                                 .collect(Collectors.groupingBy(d -> {
                                     LocalDate date = LocalDate.of(d.year, d.month, d.day);
                                     int weekOfMonth = date.get(weekFields.weekOfMonth());
                                     return "Week " + weekOfMonth;
                                 }, Collectors.summingInt(d -> 1)));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            String[] labels = {"Week 1", "Week 2", "Week 3", "Week 4", "Week 5"};
            int[] values = new int[labels.length];

            for (int i = 0; i < labels.length; i++) {
                values[i] = monthData.getOrDefault(labels[i], 0);
            }

            int width = getWidth();
            int height = getHeight();
            int barWidth = width / values.length;
            int maxBarHeight = height - 30;
            int maxValue = Arrays.stream(values).max().orElse(1);

            g.setColor(Color.BLUE);
            for (int i = 0; i < values.length; i++) {
                int barHeight = (int) ((values[i] / (double) maxValue) * maxBarHeight);
                int x = i * barWidth;
                int y = height - barHeight - 20;
                g.fillRect(x, y, barWidth - 10, barHeight);
                g.drawString(labels[i], x + (barWidth / 2) - 5, height - 5);
            }
        }
    }

    class YearBarChartPanel extends JPanel {
        Map<String, Integer> yearData;

        YearBarChartPanel(List<ProgressData> data) {
            LocalDate now = LocalDate.now();
            int currentYear = now.getYear();

            this.yearData = data.stream()
                                .filter(d -> d.progress == 100)
                                .filter(d -> d.year == currentYear)
                                .collect(Collectors.groupingBy(d -> String.valueOf(d.month), Collectors.summingInt(d -> 1)));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            String[] labels = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};
            int[] values = new int[labels.length];

            for (int i = 0; i < labels.length; i++) {
                values[i] = yearData.getOrDefault(labels[i], 0);
            }

            int width = getWidth();
            int height = getHeight();
            int barWidth = width / values.length;
            int maxBarHeight = height - 30;
            int maxValue = Arrays.stream(values).max().orElse(1);

            g.setColor(Color.BLUE);
            for (int i = 0; i < values.length; i++) {
                int barHeight = (int) ((values[i] / (double) maxValue) * maxBarHeight);
                int x = i * barWidth;
                int y = height - barHeight - 20;
                g.fillRect(x, y, barWidth - 10, barHeight);
                g.drawString(labels[i], x + (barWidth / 2) - 5, height - 5);
            }
        }
    }

    class PieChartPanel extends JPanel {
        Map<String, Long> subjectData;

        PieChartPanel(List<ProgressData> data, String period) {
            if (period.equals("week")) {
                this.subjectData = calculateWeeklySubjectData(data);
            } else if (period.equals("month")) {
                this.subjectData = calculateMonthlySubjectData(data);
            } else if (period.equals("year")) {
                this.subjectData = calculateYearlySubjectData(data);
            } else {
                this.subjectData = new HashMap<>();
            }
        }

        private Map<String, Long> calculateWeeklySubjectData(List<ProgressData> data) {
            LocalDate now = LocalDate.now();
            WeekFields weekFields = WeekFields.of(Locale.getDefault());
            int currentWeek = now.get(weekFields.weekOfYear());
            int currentYear = now.getYear();

            return data.stream()
                       .filter(d -> d.progress == 100)
                       .filter(d -> {
                           LocalDate date = LocalDate.of(d.year, d.month, d.day);
                           return date.get(weekFields.weekOfYear()) == currentWeek && d.year == currentYear;
                       })
                       .collect(Collectors.groupingBy(d -> d.subject, Collectors.counting()));
        }

        private Map<String, Long> calculateMonthlySubjectData(List<ProgressData> data) {
            LocalDate now = LocalDate.now();
            int currentMonth = now.getMonthValue();
            int currentYear = now.getYear();

            return data.stream()
                       .filter(d -> d.progress == 100)
                       .filter(d -> d.year == currentYear && d.month == currentMonth)
                       .collect(Collectors.groupingBy(d -> d.subject, Collectors.counting()));
        }

        private Map<String, Long> calculateYearlySubjectData(List<ProgressData> data) {
            LocalDate now = LocalDate.now();
            int currentYear = now.getYear();

            return data.stream()
                       .filter(d -> d.progress == 100)
                       .filter(d -> d.year == currentYear)
                       .collect(Collectors.groupingBy(d -> d.subject, Collectors.counting()));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int width = getWidth();
            int height = getHeight();
            int diameter = Math.min(width, height) - 10;

            int total = subjectData.values().stream().mapToInt(Long::intValue).sum();
            int startAngle = 0;
            List<Color> colors = Arrays.asList(new Color(255, 179, 186), new Color(255, 223, 186), new Color(255, 255, 186),
                                               new Color(186, 255, 201), new Color(186, 225, 255), new Color(255, 186, 255));

            int colorIndex = 0;
            for (Map.Entry<String, Long> entry : subjectData.entrySet()) {
                int arcAngle = (int) (360.0 * entry.getValue() / total);
                g.setColor(colors.get(colorIndex % colors.size()));
                g.fillArc(5, 5, diameter, diameter, startAngle, arcAngle);

                // 과목 이름을 원 안에 표시
                int angle = startAngle + arcAngle / 2;
                double radian = Math.toRadians(angle);
                int x = (int) (width / 2 + (diameter / 3) * Math.cos(radian));
                int y = (int) (height / 2 + (diameter / 3) * Math.sin(radian));
                g.setColor(Color.BLACK);
                g.drawString(entry.getKey(), x, y);

                startAngle += arcAngle;
                colorIndex++;
            }

            // Draw labels
            int x = diameter + 10;
            int y = 20;
            colorIndex = 0;
            for (String subject : subjectData.keySet()) {
                g.setColor(colors.get(colorIndex++ % colors.size()));
                g.fillRect(x, y - 10, 10, 10);
                g.setColor(Color.BLACK);
                g.drawString(subject, x + 15, y);
                y += 20;
            }
        }
    }
    /*
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Stactics());
    }
    */
       
    public JInternalFrame getInternalFrame() {
    	return jf;
    }
}