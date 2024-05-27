package dudeozy;

import javax.swing.*;
import javax.swing.border.LineBorder;

import java.awt.*;
import java.awt.event.*;
import java.time.YearMonth;

public class SimpleCalendar extends JPanel {
    private JComboBox<String> monthBox;
    private JComboBox<Integer> yearBox;
    private JPanel calendarPanel;
    private MainPage mainPage;

    public SimpleCalendar(MainPage mainPage) {
    	this.mainPage = mainPage;
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        JLabel yearLabel = new JLabel("연도:");
        yearBox = new JComboBox<>();
        for (int year = 2000; year <= 2050; year++) {
            yearBox.addItem(year);
        }
        yearBox.setSelectedItem(YearMonth.now().getYear());
        yearBox.addActionListener(e -> updateCalendar());

        JLabel monthLabel = new JLabel("월:");
        String[] months = {"1월", "2월", "3월", "4월", "5월", "6월",
                "7월", "8월", "9월", "10월", "11월", "12월"};
        monthBox = new JComboBox<>(months);
        monthBox.setSelectedIndex(YearMonth.now().getMonthValue() - 1);
        monthBox.addActionListener(e -> updateCalendar());

        topPanel.add(yearLabel);
        topPanel.add(yearBox);
        topPanel.add(monthLabel);
        topPanel.add(monthBox);

        calendarPanel = new JPanel(new GridLayout(0, 7));

        add(topPanel, BorderLayout.NORTH);
        add(calendarPanel, BorderLayout.CENTER);

        updateCalendar();
    }

    private void updateCalendar() {
        calendarPanel.removeAll();

        int year = (int) yearBox.getSelectedItem();
        int monthIndex = monthBox.getSelectedIndex();
        YearMonth yearMonth = YearMonth.of(year, monthIndex + 1);

        int daysInMonth = yearMonth.lengthOfMonth();
        int firstDayOfWeek = yearMonth.atDay(1).getDayOfWeek().getValue(); // 1: 월, ..., 7: 일

        // 맨 위에 요일 추가
        String[] dayNames = {"월", "화", "수", "목", "금", "토", "일"};
        for (String day : dayNames) {
            JLabel label = new JLabel(day, SwingConstants.CENTER);
            calendarPanel.add(label);
        }

        // 빈 panel 추가하기
        for (int i = 1; i < firstDayOfWeek; i++) {
        	JPanel blank = new JPanel();
        	blank.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        	calendarPanel.add(blank);
        }

        // 각 날짜마다 panel 추가하기
        for (int day = 1; day <= daysInMonth; day++) {
        	JPanel dayPanel = new JPanel();
        	dayPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        	
        	// panel마다 날짜 버튼 추가
        	JButton button = new JButton(String.valueOf(day));
            button.setFont(new Font("Arial", Font.PLAIN, 10));
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // 새로운 JInternalFrame 생성
                	JButton sourceButton = (JButton) e.getSource();
                    String buttonText = sourceButton.getText(); //button에 있는 값 들고 오기
                    
                    JInternalFrame internalFrame = new JInternalFrame(buttonText+"일", true, true, true, true);
                    internalFrame.setSize(200, 200);
                    internalFrame.setLocation(800, 100);
                    internalFrame.setVisible(true);

                    // mainPage의 Desktop Pane에 추가
                    mainPage.getDesktopPane().add(internalFrame);
                    mainPage.getDesktopPane().moveToFront(internalFrame);
                }
            });

            dayPanel.add(button);
            calendarPanel.add(dayPanel);
        }
        
        // 남은 마지막 줄에 빈 panel 추가
        int remainingCells = (7 - (firstDayOfWeek + daysInMonth - 1) % 7) % 7;
        for (int i = 0; i < remainingCells; i++) {
        	JPanel blank = new JPanel();
        	blank.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        	calendarPanel.add(blank);
        }

        revalidate();
        repaint();
    }
}
//JLabel label = new JLabel(String.valueOf(day), SwingConstants.CENTER);
//button.setPreferredSize(new Dimension(40,20)); // 크기 설정
//button.setBackground(Color.WHITE); // 배경색 설정
//button.setForeground(Color.BLACK); // 글자 색 설정
//button.setBorder(new LineBorder(Color.WHITE, 2, true)); // 둥근 테두리 설정
//button.setContentAreaFilled(false); // 내용 영역 채우지 않음
/*
try {
    internalFrame.setSelected(true);
} catch (java.beans.PropertyVetoException ex) {
    ex.printStackTrace();
}
*/