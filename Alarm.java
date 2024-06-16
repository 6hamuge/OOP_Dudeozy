
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Alarm extends JFrame {

    private static final String JDBC_URL = "jdbc:oracle:thin:@localhost:1521:xe";
    private static final String JDBC_USER = "SYSTEM";
    private static final String JDBC_PASSWORD = "foroopcurie";

    public Alarm() {

        scheduleAlerts();
    }


    private void showAlert(String message) {
        JOptionPane.showMessageDialog(this, message, "알림", JOptionPane.INFORMATION_MESSAGE);
    }

    private void playSound() {
        Toolkit.getDefaultToolkit().beep();
    }

    private Calendar adjustAlertTime(Calendar calendar, int alertTime) {
        switch (alertTime) {
            case 1: // 5분 전
                calendar.add(Calendar.MINUTE, -5);
                break;
            case 2: // 10분 전
                calendar.add(Calendar.MINUTE, -10);
                break;
            case 3: // 15분 전
                calendar.add(Calendar.MINUTE, -15);
                break;
            case 4: // 30분 전
                calendar.add(Calendar.MINUTE, -30);
                break;
            case 5: // 1시간 전
                calendar.add(Calendar.HOUR_OF_DAY, -1);
                break;
            case 6: // 하루 전
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                break;
            case 7: // 3일 전
                calendar.add(Calendar.DAY_OF_MONTH, -3);
                break;
            case 8: // 일주일 전
                calendar.add(Calendar.DAY_OF_MONTH, -7);
                break;
            default:
                throw new IllegalArgumentException("Invalid alert time value");
        }
        return calendar;
    }

    private void scheduleAlerts() {
        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
            // 과목 알림 설정
            String sqlSchedule = "SELECT s.subject, s.year, s.month, s.day, s.time, s.task, u.alert_time " +
                    "FROM Schedule s JOIN UserSettingtask u ON s.task = u.task_name WHERE s.completed = 0";
            PreparedStatement pstmtSchedule = conn.prepareStatement(sqlSchedule);
            ResultSet rsSchedule = pstmtSchedule.executeQuery();

            while (rsSchedule.next()) {
                String subject = rsSchedule.getString("subject");
                int year = rsSchedule.getInt("year");
                int month = rsSchedule.getInt("month");
                int day = rsSchedule.getInt("day");
                String time = rsSchedule.getString("time"); 
                String task = rsSchedule.getString("task");
                int alertTime = rsSchedule.getInt("alert_time");

                String[] timeParts = time.split(":");
                int hour = Integer.parseInt(timeParts[0]);
                int minute = Integer.parseInt(timeParts[1]);
                int second = Integer.parseInt(timeParts[2]);

                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month - 1, day, hour, minute, second);

                // Adjust alert time
                calendar = adjustAlertTime(calendar, alertTime);
                Date executionTime = calendar.getTime();

                Timer timer = new Timer();
                TimerTask timerTask = new TimerTask() {
                    public void run() {
                        long currentTimeMillis = System.currentTimeMillis();
                        long executionTimeMillis = executionTime.getTime();
                        long remainingTimeMillis = executionTimeMillis - currentTimeMillis;

                        if (remainingTimeMillis > 0) {
                            long remainingSeconds = remainingTimeMillis / 1000;
                            long days = remainingSeconds / (24 * 3600);
                            long hours = (remainingSeconds % (24 * 3600)) / 3600;
                            long minutes = (remainingSeconds % 3600) / 60;

                            String message = String.format("%s의 %s의 마감이 %d일 %d시간 %d분 남았습니다.",
                                    subject, task, days, hours, minutes);
                            showAlert(message);
                            playSound();
                        }
                    }
                };

                timer.schedule(timerTask, executionTime);
            }

            rsSchedule.close();
            pstmtSchedule.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Alarm());
    }
}
