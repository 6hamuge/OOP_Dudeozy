package dudeozy;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;


public class CustomCalendar extends JPanel {
    private JComboBox<String> monthBox;
    private JComboBox<Integer> yearBox;
    private JPanel calendarPanel;
    private MainPage mainPage;
    private List<JInternalFrame> internalFrames; 
    private static final int FRAME_OFFSET = 30; // 프레임 간의 오프셋
    private ColorHelper colorHelper;

    public CustomCalendar(MainPage mainPage) {
        this.mainPage = mainPage;
        this.internalFrames = new ArrayList<>(); // 생성자에서 리스트 초기화
        this.colorHelper = new ColorHelper(mainPage); // ColorHelper 가져오기

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

    public void updateCalendar() {
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
                    String buttonText = sourceButton.getText(); // button에 있는 값 들고 오기
                    
                    int selectedYear = (int) yearBox.getSelectedItem();
                    int selectedMonth = monthBox.getSelectedIndex() + 1;
                    int selectedDay = Integer.parseInt(buttonText);

                    String title = selectedYear + "년 " + selectedMonth + "월 " + selectedDay + "일";
               
                    JInternalFrame internalFrame = new JInternalFrame(title, true, true, true, true);
                    internalFrame.setSize(350, 200);

                    // 위치 설정: 이전 프레임들과 겹치지 않도록
                    Point location = getNextFramePosition();
                    internalFrame.setLocation(location);
                    
                    // 데이터베이스에서 할 일 내용 가져오기
                    updateInternalFrameContent(internalFrame, selectedYear, selectedMonth, selectedDay);

                    internalFrame.setVisible(true);

                    // mainPage의 Desktop Pane에 추가
                    mainPage.getDesktopPane().add(internalFrame);
                    mainPage.getDesktopPane().moveToFront(internalFrame);

                    // 위치를 리스트에 추가
                    internalFrames.add(internalFrame);
                }
            });

            dayPanel.add(button);
            addBarsPerDay(dayPanel, year, monthIndex + 1, day);
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
    
    // dayPanel에 bar 추가하기
    private void addBarsPerDay(JPanel dayPanel, int year, int month, int day) {
        Connection conn = mainPage.getConnection();
        try {
            String sql = "SELECT subject FROM Schedule WHERE year = ? AND month = ? AND day = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, year);
            pstmt.setInt(2, month);
            pstmt.setInt(3, day);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String subject = rs.getString("subject");
                Color color = colorHelper.getSubjectColor(subject);
                Bar colorBar = new Bar(subject, color);
                dayPanel.add(colorBar);
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    // 데이터베이스에 있는 할 일 가져오기
    private void updateInternalFrameContent(JInternalFrame internalFrame, int year, int month, int day) {
        Connection conn = mainPage.getConnection();
        try {
            String sql = "SELECT subject, time, task FROM Schedule WHERE year = ? AND month = ? AND day = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, year);
            pstmt.setInt(2, month);
            pstmt.setInt(3, day);

            ResultSet rs = pstmt.executeQuery();
            DefaultListModel<String> taskListModel = new DefaultListModel<>();

            while (rs.next()) {
                String subject = rs.getString("subject");
                String task = rs.getString("task");
                String timeHHMM = rs.getString("time");
                
                String hour = timeHHMM.substring(0,2);
                String minute = timeHHMM.substring(2);

                // 데이터가 완료됐는지 여부에 따라 표시
                if (isTaskCompleted(subject, year, month, day)) {
                    taskListModel.addElement(subject + ") " + hour + ":" + minute + " - " + task + " (완료됨)");
                } else {
                    taskListModel.addElement(subject + ") " + hour + ":" + minute + " - " + task);
                }
            }

            rs.close();
            pstmt.close();

            // JList를 생성하여 internalFrame에 추가
            JList<String> taskList = new JList<>(taskListModel);
            JScrollPane scrollPane = new JScrollPane(taskList);
            internalFrame.getContentPane().add(scrollPane, BorderLayout.CENTER);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 데이터베이스에서 특정 할 일이 완료되었는지 여부를 체크하는 메소드
    private boolean isTaskCompleted(String subject, int year, int month, int day) {
        boolean completed = false;
        Connection conn = mainPage.getConnection();
        try {
            String sql = "SELECT completed FROM Schedule WHERE subject = ? AND year = ? AND month = ? AND day = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, subject);
            pstmt.setInt(2, year);
            pstmt.setInt(3, month);
            pstmt.setInt(4, day);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int completedValue = rs.getInt("completed");
                completed = (completedValue == 1); // 1이면 완료된 상태
            }

            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return completed;
    }

    // 새로운 프레임의 위치를 계산하는 메소드
    private Point getNextFramePosition() {
        int x = 400, y = 0;
        if (!internalFrames.isEmpty()) {
            JInternalFrame lastFrame = internalFrames.get(internalFrames.size() - 1);
            Point lastPosition = lastFrame.getLocation();
            x = lastPosition.x + FRAME_OFFSET;
            y = lastPosition.y + FRAME_OFFSET;
        }
        return new Point(x, y);
    }
}