package doduzy;

import java.awt.*;
import javax.swing.*;

public class Stactics {
    JFrame jf;
    JTabbedPane tabpane;

    public Stactics(String msg) {
        jf = new JFrame(msg);
        tabpane = new JTabbedPane();

        JPanel one = new JPanel(new GridLayout(5, 1, 10, 10));
        JPanel two = new JPanel(new BorderLayout());
        JPanel three = new JPanel(new BorderLayout());
        JPanel four = new JPanel(new BorderLayout());

        tabpane.addTab("일", one);
        tabpane.addTab("주", two);
        tabpane.addTab("월", three);
        tabpane.addTab("년", four);

        // 일별 진행상황 게이지
        JLabel label1 = new JLabel("일별 진행상황");
        one.add(label1);

        JProgressBar progressBar0 = new JProgressBar();
        progressBar0.setValue(75);
        progressBar0.setString("일별 진행상황");
        progressBar0.setStringPainted(true);
        one.add(progressBar0);
        
        JProgressBar progressBar1 = new JProgressBar();
        progressBar1.setValue(75);
        progressBar1.setString("과목 1 진행상황");
        progressBar1.setStringPainted(true);
        one.add(progressBar1);

        JProgressBar progressBar2 = new JProgressBar();
        progressBar2.setValue(50);
        progressBar2.setString("과목 2 진행상황");
        progressBar2.setStringPainted(true);
        one.add(progressBar2);

        JProgressBar progressBar3 = new JProgressBar();
        progressBar3.setValue(90);
        progressBar3.setString("과목 3 진행상황");
        progressBar3.setStringPainted(true);
        one.add(progressBar3);

        // 주별 진행상황
        JLabel label21 = new JLabel("주별 진행상황");
        two.add(label21, BorderLayout.NORTH);

        JSplitPane splitPane1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new BarChartPanel(), new PieChartPanel());
        splitPane1.setResizeWeight(0.5); // Adjust the split ratio
        two.add(splitPane1, BorderLayout.CENTER);
        JLabel label22 = new JLabel("이번 주 가장 바쁜 날 = 수요일"); //나중엔 계산 된 것을 넣기
        two.add(label22, BorderLayout.SOUTH);


        // 월별 진행상황
        JLabel label31 = new JLabel("월별 진행상황");
        three.add(label31, BorderLayout.NORTH);

        JSplitPane splitPane2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new BarChartPanel(), new PieChartPanel());
        splitPane2.setResizeWeight(0.5); // Adjust the split ratio
        three.add(splitPane2, BorderLayout.CENTER);
        JLabel label32 = new JLabel("이번 달 가장 바쁜 날 = 18일"); //나중엔 계산 된 것을 넣기
        three.add(label32, BorderLayout.SOUTH);

        // 연별 진행상황
        JLabel label41 = new JLabel("연별 진행상황");
        four.add(label41, BorderLayout.NORTH);

        JSplitPane splitPane3 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new BarChartPanel(), new PieChartPanel());
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

    class BarChartPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int[] values = {1, 4, 3, 5, 5};
            String[] labels = {"월", "화", "수", "목", "금"};
            int width = getWidth();
            int height = getHeight();
            int barWidth = width / values.length;
            int maxBarHeight = height - 30;

            g.setColor(Color.BLUE);
            for (int i = 0; i < values.length; i++) {
                int barHeight = (int) ((values[i] / 5.0) * maxBarHeight);
                int x = i * barWidth;
                int y = height - barHeight - 20;
                g.fillRect(x, y, barWidth - 10, barHeight);
                g.drawString(labels[i], x + (barWidth / 2) - 5, height - 5);
            }
        }
    }

    class PieChartPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int[] values = {43, 28, 29};
            String[] labels = {"과목 1", "과목 2", "과목 3"};
            Color[] colors = {Color.RED, Color.GREEN, Color.BLUE};

            int width = getWidth();
            int height = getHeight();
            int diameter = Math.min(width, height) - 10;
            int total = 0;

            for (int value : values) {
                total += value;
            }

            int startAngle = 0;
            for (int i = 0; i < values.length; i++) {
                int arcAngle = (int) (360.0 * values[i] / total);
                g.setColor(colors[i]);
                g.fillArc(5, 5, diameter, diameter, startAngle, arcAngle);
                startAngle += arcAngle;
            }

            // Draw labels
            int x = diameter + 10;
            int y = 20;
            for (int i = 0; i < labels.length; i++) {
                g.setColor(colors[i]);
                g.fillRect(x, y - 10, 10, 10);
                g.setColor(Color.BLACK);
                g.drawString(labels[i], x + 15, y);
                y += 20;
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Stactics("진행 상황"));
    }
}
