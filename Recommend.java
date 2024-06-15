package doduzy;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Recommend {

    private Connection connection;

    public Recommend(String jdbcUrl, String username, String password) throws SQLException {
        connection = DriverManager.getConnection(jdbcUrl, username, password);
    }

    // 날짜를 기준으로 해당 날짜에 해당하는 Schedule 테이블의 이벤트 이름과 할 일 가져오기
    public List<EventDetails> getEventsOnDate(LocalDate date) throws SQLException {
        List<EventDetails> events = new ArrayList<>();
        String query = "SELECT S.subject, S.task " +
                       "FROM Schedule S " +
                       "JOIN UserSettingevent U ON S.subject = U.event_name " +
                       "WHERE S.year = ? AND S.month = ? AND S.day = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, date.getYear());
            statement.setInt(2, date.getMonthValue());
            statement.setInt(3, date.getDayOfMonth());

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String eventName = resultSet.getString("subject");
                String task = resultSet.getString("task");
                events.add(new EventDetails(eventName, task));
            }
        }

        return events;
    }

    // 이번 해에는 더 이상 필요하지 않은 이벤트가 있는지 체크
    public List<EventDetails> checkUnnecessaryEvents() {
        List<EventDetails> unnecessaryEvents = new ArrayList<>();
        LocalDate today = LocalDate.now();
        LocalDate lastYearSameDate = today.minusYears(1);

        try {
            // 작년 이 날짜에 발생한 이벤트
            List<EventDetails> lastYearEvents = getEventsOnDate(lastYearSameDate);

            // 이번 해에는 더 이상 필요하지 않은 이벤트 체크
            String query = "SELECT S.subject, S.task " +
                           "FROM Schedule S " +
                           "JOIN UserSettingevent U ON S.subject = U.event_name " +
                           "WHERE S.year = ? AND S.month = ? AND S.day = ?";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, today.getYear());
                statement.setInt(2, today.getMonthValue());
                statement.setInt(3, today.getDayOfMonth());

                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    String eventName = resultSet.getString("subject");
                    String task = resultSet.getString("task");
                    boolean foundLastYear = false;
                    for (EventDetails event : lastYearEvents) {
                        if (event.getEventName().equals(eventName) && event.getTask().equals(task)) {
                            unnecessaryEvents.add(new EventDetails(eventName, task));
                            foundLastYear = true;
                            break;
                        }
                    }
                    if (!foundLastYear) {
                        unnecessaryEvents.add(new EventDetails(eventName, task));
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return unnecessaryEvents;
    }

    // 추천 패널 표시
 // 추천 패널 표시
    public void displayRecommendationPanel() {
        List<EventDetails> unnecessaryEvents = checkUnnecessaryEvents();

        // 작년 이벤트가 없으면 패널을 표시하지 않음
        if (unnecessaryEvents.isEmpty()) {
            JOptionPane.showMessageDialog(null, "작년에는 이벤트가 없습니다.", "No Events Last Year", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JFrame frame = new JFrame("Recommendation Panel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(new JLabel("작년에 있었던 이벤트:"));

        // 작년에 있었던 이벤트 이름과 할 일(task)을 메시지에 추가
        for (EventDetails event : unnecessaryEvents) {
            panel.add(new JLabel("- " + event.getTask()));
        }

        frame.getContentPane().add(panel, BorderLayout.CENTER);
        panel.add(new JLabel("이번 년도는 필요없으신가요?\n"));
        frame.setVisible(true);
    }

    
    public static void main(String[] args) {
        String jdbcUrl = "jdbc:oracle:thin:@localhost:1521:xe"; // Update with your Oracle JDBC URL
        String username = "system"; // Update with your database username
        String password = "oopdata"; // Update with your database password

        try {
            Recommend recommendationSystem = new Recommend(jdbcUrl, username, password);
            recommendationSystem.displayRecommendationPanel();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void close() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    // 이벤트 상세 정보 클래스
    private static class EventDetails {
        private String eventName;
        private String task;

        public EventDetails(String eventName, String task) {
            this.eventName = eventName;
            this.task = task;
        }

        public String getEventName() {
            return eventName;
        }

        public String getTask() {
            return task;
        }
    }
}
