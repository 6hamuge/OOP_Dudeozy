package alarm;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Date;
import java.util.Calendar;

public class Alarm extends JFrame {

    public Alarm() {
        // 프레임 초기화
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //this.setSize(300, 200);
        //this.setVisible(true);

      }

    public void showAlert() {
        int daysLeft = 3;
        String object = "컴아텍";  // char 대신 String 사용
        
        String message = String.format("%s 종료까지 %d일 남았습니다.", object, daysLeft);
        JOptionPane.showMessageDialog(this, message, "알림", JOptionPane.INFORMATION_MESSAGE);
        
       }

    public static void main(String[] args) {
        // 알람 인스턴스 생성
        Alarm alarm = new Alarm();
        
        // Timer와 TimerTask를 이용하여 특정 시간에 팝업 표시
        Timer timer = new Timer();

        TimerTask task = new TimerTask() {
            public void run() {
                
                alarm.showAlert();
            }
        };


        Calendar calendar = Calendar.getInstance();
        calendar.set(2024, Calendar.JUNE, 6, 13, 16, 0);
        Date executionTime = calendar.getTime();
        
  
        timer.schedule(task, executionTime);
    }
}
