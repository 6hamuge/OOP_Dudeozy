package dudeozy;

import javax.swing.*;
import java.awt.*;
import java.time.*;
import java.time.format.DateTimeFormatter;

public class MainPage {
	JFrame jf;
	JComboBox<String> subjects;
	private JDesktopPane desktopPane;
	
	public MainPage() {
		// frame 생성
		jf = new JFrame("MainPage");
		jf.setSize(1980, 1080);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setLayout(new BorderLayout());
		
		// calendar 생성
		SimpleCalendar simpleCalendar = new SimpleCalendar(this);
		
		JInternalFrame calendar = new JInternalFrame("Calendar", true, true, true, true);
		calendar.getContentPane().add(simpleCalendar); 
		calendar.setSize(960, 750);
		calendar.setResizable(true); 
		calendar.setIconifiable(false); 
		calendar.setClosable(false);
		calendar.setLocation(0,0);
		calendar.setVisible(true);
		
		desktopPane = new JDesktopPane();
		desktopPane.add(calendar);
		
		// todolist 생성
		DefaultListModel<String> todayListModel = new DefaultListModel<>();
		JList<String> todayList = new JList<>(todayListModel);
		JScrollPane todoScrollPane = new JScrollPane(todayList);
		
		// 크기 설정
		Font listFont = new Font("Malgun Gothic", Font.PLAIN, 15);
        todayList.setFont(listFont);
        
		// 예시로 sample todo 추가
		todayListModel.addElement("객프 과제하기");
		todayListModel.addElement("공수 온라인퀴즈");
		
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
        
		todayPanel.add(dateLabel, BorderLayout.NORTH);
		todayPanel.add(todoScrollPane, BorderLayout.CENTER);
		
		// 새로운 입력 받는 데를 위한 inputPanel 생성
		JPanel inputPanel = new JPanel(new BorderLayout());
		inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		JPanel msgPanel = new JPanel();
		JPanel optionPanel = new JPanel();
		
		// label 생성
		JLabel msgLabel = new JLabel("새로운 해야 할 일을 입력하세요", SwingConstants.CENTER);
		msgLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 18));
		JLabel importanceLabel = new JLabel("중요도");
		JLabel taskLabel = new JLabel("내용");
		
		// textfield 생성
		JTextField importance = new JTextField(5);
		JTextField task = new JTextField(30);
		
		// 과목명 items 임의로 집어넣음
		String items[] = {"test1", "test2", "test3", "etc"};
		subjects = new JComboBox<>(items);
		
		// panel에 집어넣기
		inputPanel.add(msgPanel.add(msgLabel), BorderLayout.NORTH);
		optionPanel.add(importanceLabel);
		optionPanel.add(importance);
		optionPanel.add(taskLabel);
		optionPanel.add(task);
		optionPanel.add(subjects);
		optionPanel.add(new JButton("입력"));
		inputPanel.add(optionPanel, BorderLayout.SOUTH);
		
		// frame에 panel들 집어넣기
		jf.add(desktopPane, BorderLayout.CENTER);
		jf.add(todayPanel, BorderLayout.EAST);
		jf.add(inputPanel, BorderLayout.SOUTH);
		
		
		// frame을 보이도록 설정
		jf.setVisible(true);
	}
	
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(MainPage::new);
	}
	
	public JDesktopPane getDesktopPane() {
        return desktopPane;
    }	
}