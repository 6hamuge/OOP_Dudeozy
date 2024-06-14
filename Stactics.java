package doduzy;

import java.awt.*;
import javax.swing.*;
import java.sql.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class Stactics {
    JInternalFrame jf;
    JTabbedPane tabpane;
    Connection conn;

    public Stactics(String msg) {
        // 데이터베이스 연결 설정
        try {
            String url = "jdbc:oracle:thin:@localhost:1521:xe"; // 데이터베이스 URL을 설정합니다.
            String username = "system"; // 데이터베이스 사용자 이름을 설정합니다.
            String password = "oopdata"; // 데이터베이스 비밀번호를 설정합니다.
            conn = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        jf = new JInternalFrame(msg);
        tabpane = new JTabbedPane();

        JPanel one = new JPanel(new GridLayout(5, 1, 10, 10));
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

        JProgressBar progressBar0 = new JProgressBar();
        progressBar0.setValue(calculateOverallProgress(progressDataList));
        progressBar0.setString("일별 진행상황");
        progressBar0.setStringPainted(true);
        one.add(progressBar0);

        for (ProgressData data : progressDataList) {
            JProgressBar progressBar = new JProgressBar();
            progressBar.setValue(data.progress);
            progressBar.setString(data.subject + " 진행상황");
            progressBar.setStringPainted(true);
            one.add(progressBar);
        }

        // 주별 진행상황
        JLabel label21 = new JLabel("주별 진행상황");
        two.add(label21, BorderLayout.NORTH);

        JSplitPane splitPane1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new BarChartPanel(progressDataList), new PieChartPanel(progressDataList));
        splitPane1.setResizeWeight(0.5); // Adjust the split ratio
        two.add(splitPane1, BorderLayout.CENTER);
        JLabel label22 = new JLabel("이번 주 가장 바쁜 날 = 수요일"); //나중엔 계산 된 것을 넣기
        two.add(label22, BorderLayout.SOUTH);

        // 월별 진행상황
        JLabel label31 = new JLabel("월별 진행상황");
        three.add(label31, BorderLayout.NORTH);

        JSplitPane splitPane2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new BarChartPanel(progressDataList), new PieChartPanel(progressDataList));
        splitPane2.setResizeWeight(0.5); // Adjust the split ratio
        three.add(splitPane2, BorderLayout.CENTER);
        JLabel label32 = new JLabel("이번 달 가장 바쁜 날 = 18일"); //나중엔 계산 된 것을 넣기
        three.add(label32, BorderLayout.SOUTH);

        // 연별 진행상황
        JLabel label41 = new JLabel("연별 진행상황");
        four.add(label41, BorderLayout.NORTH);

        JSplitPane splitPane3 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new BarChartPanel(progressDataList), new PieChartPanel(progressDataList));
        splitPane3.setResizeWeight(0.5); // Adjust the split ratio
        four.add(splitPane3, BorderLayout.CENTER);
        JLabel label42 = new JLabel("이번 년도 가장 바쁜 달 = 5월"); //나중엔 계산 된 것을 넣기
        four.add(label42, BorderLayout.SOUTH);

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
            String query = "SELECT subject, task, completed, " +
                           "TO_CHAR(TO_DATE(year || '-' || month || '-' || day, 'YYYY-MM-DD'), 'DY') AS weekday " +
                           "FROM Schedule";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                String subject = rs.getString("subject");
                String task = rs.getString("task");
                int completed = rs.getInt("completed");
                String weekday = rs.getString("weekday");
                int progress = completed == 1 ? 100 : 0;
                dataList.add(new ProgressData(subject, task, progress, weekday));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dataList;
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
        String weekday;

        ProgressData(String subject, String task, int progress, String weekday) {
            this.subject = subject;
            this.task = task;
            this.progress = progress;
            this.weekday = weekday;
        }
    }

    class BarChartPanel extends JPanel {
        Map<String, Integer> weekdayData;

        BarChartPanel(List<ProgressData> data) {
            this.weekdayData = data.stream()
                                   .filter(d -> d.progress == 100)
                                   .collect(Collectors.groupingBy(d -> d.weekday, Collectors.summingInt(d -> 1)));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            String[] labels = {"SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};
            int[] values = new int[labels.length];

            for (int i = 0; i < labels.length; i++) {
                values[i] = weekdayData.getOrDefault(labels[i], 0);
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

        PieChartPanel(List<ProgressData> data) {
            this.subjectData = data.stream()
                                   .filter(d -> d.progress == 100)
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
            List<Color> colors = Arrays.asList(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.ORANGE, Color.MAGENTA, Color.CYAN);

            int colorIndex = 0;
            for (Map.Entry<String, Long> entry : subjectData.entrySet()) {
                int arcAngle = (int) (360.0 * entry.getValue() / total);
                g.setColor(colors.get(colorIndex++ % colors.size()));
                g.fillArc(5, 5, diameter, diameter, startAngle, arcAngle);
                startAngle += arcAngle;
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Stactics("진행 상황"));
    }
    public JInternalFrame getInternalFrame() {
    	return jf;
    }
    
}
