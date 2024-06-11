package First;

import javax.swing.*;
import java.awt.*;
import javax.swing.plaf.FontUIResource;

public class Sec {
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

    public static void main(String[] args) {
        setUIFont(new FontUIResource(new Font("Malgun Gothic", Font.PLAIN, 18)));
        Dimension dim = new Dimension(1920, 1080);
        JFrame frame = new JFrame("캘린더");
        frame.setLocation(100, 100);
        frame.setPreferredSize(dim);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        int year1 = 2024, month1 = 6, day1 = 17;
        int year2 = 2024, month2 = 6, day2 = 19;
        int year3 = 2024, month3 = 6, day3 = 22;

        try {
            JPanel dDayPanel = new JPanel();
            dDayPanel.setLayout(new BoxLayout(dDayPanel, BoxLayout.Y_AXIS));
            JCheckBox fir = new JCheckBox("수학 과제 준비");
            fir.setFont(new Font("Malgun Gothic", Font.PLAIN, 18));
            fir.setMaximumSize(new Dimension(Integer.MAX_VALUE, fir.getPreferredSize().height));
            fir.addActionListener(e -> toggleStrikeThrough(fir));
            dDayPanel.add(fir);
            long dDay1 = DDayCalculator.calculateDaysUntil(year1, month1, day1);
            dDayPanel.setBorder(BorderFactory.createTitledBorder("D-" + dDay1 + " / " + month1 + "." + day1));
            panel.add(dDayPanel);

            JPanel dDayPane2 = new JPanel();
            dDayPane2.setLayout(new BoxLayout(dDayPane2, BoxLayout.Y_AXIS));
            JCheckBox fear = new JCheckBox("데이터구조 과제");
            fear.setFont(new Font("Malgun Gothic", Font.PLAIN, 18));
            fear.setMaximumSize(new Dimension(Integer.MAX_VALUE, fear.getPreferredSize().height));
            fear.addActionListener(e -> toggleStrikeThrough(fear));
            JCheckBox fear1 = new JCheckBox("컴아텍 과제");
            fear1.setFont(new Font("Malgun Gothic", Font.PLAIN, 18));
            fear1.setMaximumSize(new Dimension(Integer.MAX_VALUE, fear1.getPreferredSize().height));
            fear1.addActionListener(e -> toggleStrikeThrough(fear1));
            dDayPane2.add(fear);
            dDayPane2.add(fear1);
            long dDay2 = DDayCalculator.calculateDaysUntil(year2, month2, day2);
            dDayPane2.setBorder(BorderFactory.createTitledBorder("D-" + dDay2 + " / " + month2 + "." + day2));
            panel.add(dDayPane2);

            JPanel dDayPane3 = new JPanel();
            dDayPane3.setLayout(new BoxLayout(dDayPane3, BoxLayout.Y_AXIS));
            JCheckBox fear3 = new JCheckBox("C언어 공부");
            fear3.setFont(new Font("Malgun Gothic", Font.PLAIN, 18));
            fear3.setMaximumSize(new Dimension(Integer.MAX_VALUE, fear3.getPreferredSize().height));
            fear3.addActionListener(e -> toggleStrikeThrough(fear3));
            JLabel fear4 = new JLabel("개교기념일");
            fear4.setFont(new Font("Malgun Gothic", Font.PLAIN, 18));
            dDayPane3.add(fear3);
            dDayPane3.add(fear4);
            long dDay3 = DDayCalculator.calculateDaysUntil(year3, month3, day3);
            dDayPane3.setBorder(BorderFactory.createTitledBorder("D-" + dDay3 + " / " + month3 + "." + day3));
            panel.add(dDayPane3);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JScrollPane scrollPane = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        frame.add(scrollPane);

        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private static void toggleStrikeThrough(JCheckBox checkBox) {
        if (checkBox.isSelected()) {
            checkBox.setText("<html><strike>" + checkBox.getText() + "</strike></html>");
        } else {
            checkBox.setText(checkBox.getText().replaceAll("<html><strike>", "").replaceAll("</strike></html>", ""));
        }
    }
}


