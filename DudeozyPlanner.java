package dudeozy;

import javax.swing.*;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import javax.swing.plaf.basic.BasicInternalFrameUI;
import java.awt.*;
import java.sql.SQLException;

public class DudeozyPlanner {
    public static void main(String[] args) throws SQLException {
        // 메인 프레임 생성
        JFrame mainFrame = new JFrame("두더지 플래너");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(1920, 1080);
        
        // 탭 패널 생성
        JTabbedPane tabbedPane = new JTabbedPane();

        // 첫 번째 탭: Main Page
        JDesktopPane desktopPane1 = new JDesktopPane();
        MainPage mainPage = new MainPage();
        JInternalFrame internalFrame1 = mainPage.getInternalFrame();
        BasicInternalFrameUI ui = (BasicInternalFrameUI) internalFrame1.getUI();
        ui.setNorthPane(null); // 제목 바 제거
        internalFrame1.setBorder(null);
        internalFrame1.setSize(1440, 800);
        desktopPane1.add(internalFrame1);
        tabbedPane.addTab("Main Page", desktopPane1);

        // 두 번째 탭: D-Day
        JDesktopPane desktopPane2 = new JDesktopPane();
        DDay dday = new DDay();
        JInternalFrame internalFrame2 = dday.getInternalFrame();
        BasicInternalFrameUI ui2 = (BasicInternalFrameUI) internalFrame2.getUI();
        ui2.setNorthPane(null); // 제목 바 제거
        internalFrame2.setBorder(null);
        internalFrame2.setSize(1440, 800);
        desktopPane2.add(internalFrame2);
        tabbedPane.addTab("D-Day", desktopPane2);
       
        // 세 번째 탭: 할 일
        JDesktopPane desktopPane3 = new JDesktopPane();
        Obje obje = new Obje();
        JInternalFrame internalFrame3 = obje;
        BasicInternalFrameUI ui3 = (BasicInternalFrameUI) internalFrame3.getUI();
        ui3.setNorthPane(null);
        internalFrame3.setBorder(null);
        internalFrame3.setSize(1440,800);
        desktopPane3.add(internalFrame3);
        tabbedPane.addTab("할 일", desktopPane3);
       
        // 네 번째 탭: 통계
        JDesktopPane desktopPane4 = new JDesktopPane();
        Stactics stactics = new Stactics();
        JInternalFrame internalFrame4 = stactics.getInternalFrame();
        BasicInternalFrameUI ui4 = (BasicInternalFrameUI) internalFrame4.getUI();
        ui4.setNorthPane(null);
        internalFrame4.setBorder(null);
        internalFrame4.setSize(1440,800);
        desktopPane4.add(internalFrame4);
        tabbedPane.addTab("통계", desktopPane4);
        
        // 다섯 번째 탭: 추천
        JDesktopPane desktopPane5 = new JDesktopPane();
        Recommend recommend= new Recommend();
        JInternalFrame internalFrame5 = recommend.getInternalFrame();
        BasicInternalFrameUI ui5 = (BasicInternalFrameUI) internalFrame5.getUI();
        ui5.setNorthPane(null);
        internalFrame5.setBorder(null);
        internalFrame5.setSize(1440,800);
        desktopPane5.add(internalFrame5);
        tabbedPane.addTab("추천", desktopPane5);
        
        // 여섯 번째 탭: 사용자 설정
        JDesktopPane desktopPane6 = new JDesktopPane();
        SettingFrame setting = new SettingFrame();
        JInternalFrame internalFrame6 = setting.getInternalFrame();
        BasicInternalFrameUI ui6 = (BasicInternalFrameUI) internalFrame6.getUI();
        ui6.setNorthPane(null);
        internalFrame6.setBorder(null);
        internalFrame6.setSize(1440,800);
        desktopPane6.add(internalFrame6);
        tabbedPane.addTab("사용자 설정", desktopPane6);
       
        // 메인 프레임에 탭 패널 추가
        mainFrame.getContentPane().add(tabbedPane, BorderLayout.CENTER);
        
        // 메인 프레임 표시
        mainFrame.setVisible(true);
    }
}
