package dudeozy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.sql.*;

public class MainPage {
	JInternalFrame jf;
	JComboBox<String> subjects;
	JComboBox<Integer> yearBox;
    JComboBox<String> monthBox;
    JComboBox<Integer> dayBox;
    JComboBox<Integer> hourBox;
    JComboBox<Integer> minuteBox;
    JComboBox<Integer> importanceBox;
    JTextField task;
    DefaultListModel<String> todayListModel;
	private JDesktopPane desktopPane;
	private Connection conn;
	
	public MainPage() {
		try {
			Class.forName("oracle.jdbc.OracleDriver");
			
			String url = "jdbc:oracle:thin:@localhost:1521:xe";
			String user = "SYSTEM";
			String password = "foroopcurie";
			conn = DriverManager.getConnection(url,user,password);
			
			// 데이터베이스 연결 상태 확인
			boolean isConnected = conn.isValid(5); // 5초 동안 연결 상태 확인
			
			if (isConnected) {
				System.out.println("데이터베이스 연결됨");
			} else {
				System.out.println("데이터베이스 연결되지 않음");
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// frame 생성
		jf = new JInternalFrame("MainPage", false, false, false, false);
        jf.setLayout(new BorderLayout());
        jf.setVisible(true);
        
        // 캘린더 설정 ======================================================================
        // 캘린더 가져오기
		CustomCalendar customCalendar = new CustomCalendar(this);
		
		// JInternalFrame 캘린더 설정
		JInternalFrame calendar = new JInternalFrame("Calendar", true, true, true, true);
		calendar.getContentPane().add(customCalendar); 
		calendar.setSize(960, 750);
		calendar.setResizable(true); 
		calendar.setIconifiable(false); 
		calendar.setClosable(false);
		calendar.setLocation(0,0);
		calendar.setVisible(true);
		
		// desktopPane에 캘린더 추가
		desktopPane = new JDesktopPane();
		desktopPane.add(calendar);
		desktopPane.moveToBack(calendar);
		
		
		
		// 오른쪽에 오늘 할 일을 보여주는 panel 설정 =================================================
		
		// 오늘의 todolist 생성
		todayListModel = new DefaultListModel<>();
		JList<String> todayList = new JList<>(todayListModel);
		JScrollPane todoScrollPane = new JScrollPane(todayList);
		
		// 글자 크기 설정
		Font listFont = new Font("Malgun Gothic", Font.PLAIN, 15);
        todayList.setFont(listFont);
		
        // todolist를 위한 panel 생성
		JPanel todayPanel = new JPanel(new BorderLayout());
		todayPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // 위 아래로 공백 추가
		todayPanel.setPreferredSize(new Dimension(300,750));
		
		// 오늘 날짜 가져오기
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");
        String formattedDate = today.format(formatter);
        
        // JLabel에 오늘 날짜 설정
        JLabel dateLabel = new JLabel(formattedDate);
        dateLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
        
        // panel에 추가
		todayPanel.add(dateLabel, BorderLayout.NORTH);
		todayPanel.add(todoScrollPane, BorderLayout.CENTER);
		
		
		
		
		// 할 일 입력 받는 panel 설정 ===========================================================
		
		// 새로운 입력 받는 데를 위한 inputPanel 생성
		JPanel inputPanel = new JPanel(new BorderLayout());
		inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		// "새로운 할 일을 입력하세요"만 넣을 메시지 panel
		JPanel msgPanel = new JPanel();
		
		//날짜 입력을 위한 panel 생성
	    JPanel datePanel = new JPanel(new FlowLayout());
	    
	    // 중요도, 할 일 내용, 과목명, 버튼을 위한 panel
		JPanel optionPanel = new JPanel();
		
		// label 생성
		JLabel msgLabel = new JLabel("새로운 해야 할 일을 입력하세요", SwingConstants.CENTER);
		msgLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 18));
		JLabel importanceLabel = new JLabel("중요도");
		JLabel taskLabel = new JLabel("내용");
		
		// 연도, 월, 일 JComboBox 생성
		yearBox = new JComboBox<>();
	    monthBox = new JComboBox<>();
	    dayBox = new JComboBox<>();
	    setDefaultDate(today);
	    
	    // 시간 JComboBox 생성
	    Integer hours[] = {0,1,2,3,4,5,6,7,8,9,10,11,12,
	    		13,14,15,16,17,18,19,20,21,22,23};
	    Integer minutes[] = {0,5,10,15,20,25,30,
	    		35,40,45,50,55};
	    
	    hourBox = new JComboBox<>(hours);
	    minuteBox = new JComboBox<>(minutes);
	    
	    // 중요도 JComboBox 생성
	    Integer importances[] = {1,2,3,4,5};
	    importanceBox = new JComboBox<>(importances);
	    
	    // 할 일 입력
		task = new JTextField(30);
		
		// 과목명 items
		String items[] = fetchSubjects();
		subjects = new JComboBox<>(items);
		
		// panel에 집어넣기
		// 모든 panel을 포함하는 제일 큰 panel (inputPanel)
		inputPanel.add(msgPanel.add(msgLabel), BorderLayout.NORTH);
		
		// optionPanel
		optionPanel.add(importanceLabel);
		optionPanel.add(importanceBox);
		optionPanel.add(subjects);
		optionPanel.add(taskLabel);
		optionPanel.add(task);
		// 입력 버튼 ActionListener 등록
		JButton inputButton = new JButton("입력");
		inputButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				handleInputButtonClick();
			}
		});
		optionPanel.add(inputButton);
		inputPanel.add(optionPanel, BorderLayout.SOUTH);
		
		// datePanel
		datePanel.add(yearBox);
		datePanel.add(new JLabel("년"));
	    datePanel.add(monthBox);
	    datePanel.add(new JLabel("월"));
	    datePanel.add(dayBox);
	    datePanel.add(new JLabel("일"));
	    datePanel.add(hourBox);
	    datePanel.add(new JLabel("시"));
	    datePanel.add(minuteBox);
	    datePanel.add(new JLabel("분"));
	    inputPanel.add(datePanel, BorderLayout.CENTER);
	    
		
		// frame에 세 가지 큰 덩어리들 집어넣기
		jf.add(desktopPane, BorderLayout.CENTER);
		jf.add(todayPanel, BorderLayout.EAST);
		jf.add(inputPanel, BorderLayout.SOUTH);
		
		
		// frame을 보이도록 설정
		jf.setVisible(true);
		
		fetchTodayTasks();
	}


	
	// 오늘 todoList를 설정하는 메서드 =============================================================================
	public void fetchTodayTasks() {
	    try {
	        LocalDate today = LocalDate.now();
	        String sql = "SELECT subject, time, task, completed FROM Schedule WHERE year = ? AND month = ? AND day = ?";
	        PreparedStatement pstmt = conn.prepareStatement(sql);
	        pstmt.setInt(1, today.getYear());
	        pstmt.setInt(2, today.getMonthValue());
	        pstmt.setInt(3, today.getDayOfMonth());

	        ResultSet rs = pstmt.executeQuery();

	        while (rs.next()) {
	            String subject = rs.getString("subject");
	            String task = rs.getString("task");
	            String timeHHMM = rs.getString("time");
	            int completedValue = rs.getInt("completed");

	            String hour = timeHHMM.substring(0, 2);
	            String minute = timeHHMM.substring(2);

	           
	            SwingUtilities.invokeLater(() -> {
	                if (completedValue == 1) {
	                    todayListModel.addElement(subject + ") " + hour + ":" + minute + " - " + task + " (완료됨)");
	                } else {
	                    todayListModel.addElement(subject + ") " + hour + ":" + minute + " - " + task);
	                }
	            });
	        }

	        rs.close();
	        pstmt.close();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	private void insertTask(String subject, int year, int month, int day, int hour, int minute, int importance, String task) {
		try {
			String sql = "INSERT INTO SCHEDULE (SUBJECT, YEAR, MONTH, DAY, TIME, TASK, IMPORTANCE, COMPLETED) " +
		"VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			
			String timeHHMM = String.format("%02d%02d", hour, minute);
			Integer completed = 0;
			pstmt.setString(1, subject);
			pstmt.setInt(2, year);
			pstmt.setInt(3, month);
			pstmt.setInt(4, day);
			pstmt.setString(5, timeHHMM);
			pstmt.setString(6, task);
			pstmt.setInt(7, importance);
			pstmt.setInt(8, completed);
			
			pstmt.executeUpdate();
			pstmt.close();
			// 만일 추가해야 하는 값이 오늘 날짜에 해당하는 것일 경우
			LocalDate today = LocalDate.now();
			if (year == today.getYear() && month == today.getMonthValue() && day == today.getDayOfMonth()) {
				todayListModel.addElement(subject + ") " + timeHHMM.substring(0,2) + ":" + timeHHMM.substring(2) + " - " + task);
			}
			
			updateMyCalendar();
		
			// 알림창 띄우기
			JOptionPane.showMessageDialog(jf, "입력이 완료되었습니다.", "두더지플래너 알림", JOptionPane.INFORMATION_MESSAGE);
			
			//입력 필드 초기화
			resetInputFields();
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void updateMyCalendar() {
	    Component[] components = desktopPane.getComponents();
	    for (Component component : components) {
	        if (component instanceof CustomCalendar) {
	            ((CustomCalendar) component).updateCalendar();
	            break;
	        }
	    }
	}
	
	
	private void handleInputButtonClick() {
		String selectedSubject = (String) subjects.getSelectedItem();
		int selectedYear = Integer.parseInt(yearBox.getSelectedItem().toString());
		int selectedMonth = Integer.parseInt(monthBox.getSelectedItem().toString());
		int selectedDay = Integer.parseInt(dayBox.getSelectedItem().toString());
		int selectedHour = Integer.parseInt(hourBox.getSelectedItem().toString());
		int selectedMinute = Integer.parseInt(minuteBox.getSelectedItem().toString());
		int selectedImportance = (int) importanceBox.getSelectedItem();
		String selectedTask = task.getText();
		
		insertTask(selectedSubject, selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute,
				selectedImportance, selectedTask);

	}
	
	private void resetInputFields() {
		subjects.setSelectedIndex(0);
		setDefaultDate(LocalDate.now());
		hourBox.setSelectedIndex(0);
		minuteBox.setSelectedIndex(0);
		importanceBox.setSelectedIndex(0);
		task.setText("");
	}
	
	// 연도, 월, 일 JComboBox 초기값 생성
	private void setDefaultDate(LocalDate date) {
		int currentYear = date.getYear();
		int currentMonth = date.getMonthValue();
		int currentDay = date.getDayOfMonth();
		
		for (int year = currentYear -10; year <= currentYear + 10; year++) {
			yearBox.addItem(year);
		}
		yearBox.setSelectedItem(currentYear);
		
		for (int month =1; month<=12; month++) {
			monthBox.addItem(String.valueOf(month));
		}
		monthBox.setSelectedItem(String.valueOf(currentMonth));
		
		int daysInMonth = date.lengthOfMonth();
		for (int day =1; day<=daysInMonth; day++) {
			dayBox.addItem(day);
		}
		dayBox.setSelectedItem(currentDay);
	}
	
	private String[] fetchSubjects() {
        ArrayList<String> subjectsList = new ArrayList<>();
        try {
            String sql = "SELECT task_name AS name FROM UserSettingTask UNION SELECT event_name AS name FROM UserSettingEvent";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                subjectsList.add(rs.getString("name"));
            }

            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return subjectsList.toArray(new String[0]);
    }
	
	public JInternalFrame getInternalFrame() {
	    return jf;
	}
	
	public JDesktopPane getDesktopPane() {
        return desktopPane;
    }	
	public Connection getConnection() {
		return conn;
	}
}