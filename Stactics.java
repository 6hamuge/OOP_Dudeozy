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


public class Stactics {
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

        jf.getContentPane().add(tabpane, BorderLayout.CENTER);
        jf.setSize(1000, 800);
        jf.setMaximumSize(new Dimension(1000, 800));
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setVisible(true);

        updateTabs();
    }

    public void updateTabs() {
        List<ProgressData> progressDataList = fetchProgressData();

        updateDayTab(progressDataList);
        updateWeekTab(progressDataList);
        updateMonthTab(progressDataList);
        updateYearTab(progressDataList);
    }

    private void updateDayTab(List<ProgressData> progressDataList) {
        JPanel one = new JPanel(new GridLayout(0, 1, 10, 10));
        JLabel label1 = new JLabel("일별 진행상황");
        one.add(label1);

        LocalDate today = LocalDate.now();
        List<ProgressData> todayData = progressDataList.stream()
                .filter(d -> d.year == today.getYear() && d.month == today.getMonthValue() && d.day == today.getDayOfMonth())
                .collect(Collectors.toList());

        JProgressBar overallProgressBar = new JProgressBar();
        overallProgressBar.setValue(calculateOverallProgress(todayData));
        overallProgressBar.setString("전체 진행상황: " + overallProgressBar.getValue() + "%");
        overallProgressBar.setStringPainted(true);
        one.add(overallProgressBar);

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

        if (tabpane.getTabCount() > 0) {
            tabpane.setComponentAt(0, one);
        } else {
            tabpane.addTab("일", one);
        }
    }

    private void updateWeekTab(List<ProgressData> progressDataList) {
        JPanel two = new JPanel(new BorderLayout());
        JLabel label21 = new JLabel("주별 진행상황");
        two.add(label21, BorderLayout.NORTH);

        JSplitPane splitPane1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new WeekBarChartPanel(progressDataList), new PieChartPanel(progressDataList, "week"));
        splitPane1.setResizeWeight(0.5);
        two.add(splitPane1, BorderLayout.CENTER);

        if (tabpane.getTabCount() > 1) {
            tabpane.setComponentAt(1, two);
        } else {
            tabpane.addTab("주", two);
        }
    }

    private void updateMonthTab(List<ProgressData> progressDataList) {
        JPanel three = new JPanel(new BorderLayout());
        JLabel label31 = new JLabel("월별 진행상황");
        three.add(label31, BorderLayout.NORTH);

        JSplitPane splitPane2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new MonthBarChartPanel(progressDataList), new PieChartPanel(progressDataList, "month"));
        splitPane2.setResizeWeight(0.5);
        three.add(splitPane2, BorderLayout.CENTER);

        if (tabpane.getTabCount() > 2) {
            tabpane.setComponentAt(2, three);
        } else {
            tabpane.addTab("월", three);
        }
    }

    private void updateYearTab(List<ProgressData> progressDataList) {
        JPanel four = new JPanel(new BorderLayout());
        JLabel label41 = new JLabel("연별 진행상황");
        four.add(label41, BorderLayout.NORTH);

        JSplitPane splitPane3 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new YearBarChartPanel(progressDataList), new PieChartPanel(progressDataList, "year"));
        splitPane3.setResizeWeight(0.5);
        four.add(splitPane3, BorderLayout.CENTER);

        if (tabpane.getTabCount() > 3) {
            tabpane.setComponentAt(3, four);
        } else {
            tabpane.addTab("년", four);
        }
    }

    private List<ProgressData> fetchProgressData() {
        List<ProgressData> dataList = new ArrayList<>();
        try {
            String query = "SELECT subject, task, completed, year, month, day FROM Schedule";
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

    private String getWeekday(int year, int month, int day) {
        LocalDate date = LocalDate.of(year, month, day);
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek.toString().substring(0, 3).toUpperCase();
    }

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

            drawBarChart(g, labels, values);
        }

        private void drawBarChart(Graphics g, String[] labels, int[] values) {
            int width = getWidth();
            int height = getHeight();
            int barWidth = width / values.length;

            int maxVal = Arrays.stream(values).max().orElse(1);

            for (int i = 0; i < values.length; i++) {
                int barHeight = (int) ((double) values[i] / maxVal * height);
                g.setColor(Color.BLUE);
                g.fillRect(i * barWidth, height - barHeight, barWidth - 2, barHeight);
                g.setColor(Color.BLACK);
                g.drawString(labels[i], i * barWidth + (barWidth / 2) - 10, height - 5);
                g.drawString(String.valueOf(values[i]), i * barWidth + (barWidth / 2) - 10, height - barHeight - 5);
            }
        }
    }

    class MonthBarChartPanel extends JPanel {
        Map<Integer, Long> monthData;

        MonthBarChartPanel(List<ProgressData> data) {
            LocalDate now = LocalDate.now();
            int currentMonth = now.getMonthValue();
            int currentYear = now.getYear();

            this.monthData = data.stream()
                                .filter(d -> d.progress == 100)
                                .filter(d -> d.month == currentMonth && d.year == currentYear)
                                .collect(Collectors.groupingBy(d -> d.day, Collectors.counting()));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int daysInMonth = LocalDate.now().lengthOfMonth();
            String[] labels = new String[daysInMonth];
            int[] values = new int[daysInMonth];

            for (int i = 0; i < daysInMonth; i++) {
                labels[i] = String.valueOf(i + 1);
                values[i] = monthData.getOrDefault(i + 1, 0L).intValue();
            }

            drawBarChart(g, labels, values);
        }

        private void drawBarChart(Graphics g, String[] labels, int[] values) {
            int width = getWidth();
            int height = getHeight();
            int barWidth = width / values.length;

            int maxVal = Arrays.stream(values).max().orElse(1);

            for (int i = 0; i < values.length; i++) {
                int barHeight = (int) ((double) values[i] / maxVal * height);
                g.setColor(Color.GREEN);
                g.fillRect(i * barWidth, height - barHeight, barWidth - 2, barHeight);
                g.setColor(Color.BLACK);
                g.drawString(labels[i], i * barWidth + (barWidth / 2) - 10, height - 5);
                g.drawString(String.valueOf(values[i]), i * barWidth + (barWidth / 2) - 10, height - barHeight - 5);
            }
        }
    }

    class YearBarChartPanel extends JPanel {
        Map<Integer, Long> yearData;

        YearBarChartPanel(List<ProgressData> data) {
            LocalDate now = LocalDate.now();
            int currentYear = now.getYear();

            this.yearData = data.stream()
                                .filter(d -> d.progress == 100)
                                .filter(d -> d.year == currentYear)
                                .collect(Collectors.groupingBy(d -> d.month, Collectors.counting()));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            String[] labels = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
            int[] values = new int[labels.length];

            for (int i = 0; i < labels.length; i++) {
                values[i] = yearData.getOrDefault(i + 1, 0L).intValue();
            }

            drawBarChart(g, labels, values);
        }

        private void drawBarChart(Graphics g, String[] labels, int[] values) {
            int width = getWidth();
            int height = getHeight();
            int barWidth = width / values.length;

            int maxVal = Arrays.stream(values).max().orElse(1);

            for (int i = 0; i < values.length; i++) {
                int barHeight = (int) ((double) values[i] / maxVal * height);
                g.setColor(Color.ORANGE);
                g.fillRect(i * barWidth, height - barHeight, barWidth - 2, barHeight);
                g.setColor(Color.BLACK);
                g.drawString(labels[i], i * barWidth + (barWidth / 2) - 10, height - 5);
                g.drawString(String.valueOf(values[i]), i * barWidth + (barWidth / 2) - 10, height - barHeight - 5);
            }
        }
    }

    class PieChartPanel extends JPanel {
        Map<String, Long> pieData;

        PieChartPanel(List<ProgressData> data, String type) {
            LocalDate now = LocalDate.now();

            if (type.equals("week")) {
                WeekFields weekFields = WeekFields.of(Locale.getDefault());
                int currentWeek = now.get(weekFields.weekOfYear());
                int currentYear = now.getYear();

                this.pieData = data.stream()
                        .filter(d -> d.progress == 100)
                        .filter(d -> {
                            LocalDate date = LocalDate.of(d.year, d.month, d.day);
                            return date.get(weekFields.weekOfYear()) == currentWeek && d.year == currentYear;
                        })
                        .collect(Collectors.groupingBy(d -> d.subject, Collectors.counting()));
            } else if (type.equals("month")) {
                int currentMonth = now.getMonthValue();
                int currentYear = now.getYear();

                this.pieData = data.stream()
                        .filter(d -> d.progress == 100)
                        .filter(d -> d.month == currentMonth && d.year == currentYear)
                        .collect(Collectors.groupingBy(d -> d.subject, Collectors.counting()));
            } else if (type.equals("year")) {
                int currentYear = now.getYear();

                this.pieData = data.stream()
                        .filter(d -> d.progress == 100)
                        .filter(d -> d.year == currentYear)
                        .collect(Collectors.groupingBy(d -> d.subject, Collectors.counting()));
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            drawPieChart(g);
        }

        private void drawPieChart(Graphics g) {
            int total = pieData.values().stream().mapToInt(Long::intValue).sum();
            int startAngle = 0;

            int i = 0;
            for (Map.Entry<String, Long> entry : pieData.entrySet()) {
                int arcAngle = (int) ((entry.getValue() / (double) total) * 360);
                g.setColor(getColor(i));
                g.fillArc(10, 10, getWidth() - 20, getHeight() - 20, startAngle, arcAngle);
                startAngle += arcAngle;
                i++;
            }
        }
        
        private Color getColor(int index) {
            Color[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.ORANGE, Color.CYAN, Color.MAGENTA};
            return colors[index % colors.length];
        }
    }
    public JInternalFrame getInternalFrame() {
	    return jf;
	}

}
