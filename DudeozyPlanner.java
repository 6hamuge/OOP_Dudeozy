package dudeozy;

import javax.swing.*;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import javax.swing.plaf.basic.BasicInternalFrameUI;
import java.awt.*;

public class DudeozyPlanner {
    public static void main(String[] args) {
        // 메인 프레임 생성
        JFrame mainFrame = new JFrame("TabbedPane with Internal Frames");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(1920, 1080);
        
        // 탭 패널 생성
        JTabbedPane tabbedPane = new JTabbedPane();

        // 첫 번째 탭에 내부 프레임 추가
        JDesktopPane desktopPane1 = new JDesktopPane();
        MainPage mainPage = new MainPage();
        JInternalFrame internalFrame1 = mainPage.getInternalFrame();
        BasicInternalFrameUI ui = (BasicInternalFrameUI) internalFrame1.getUI();
        ui.setNorthPane(null); // 제목 바 제거
        internalFrame1.setBorder(null);
        internalFrame1.setSize(1440, 800);
        desktopPane1.add(internalFrame1);
        tabbedPane.addTab("Main Page", desktopPane1);

        // 두 번째 탭에 내부 프레임 추가
        JDesktopPane desktopPane2 = new JDesktopPane();
        pra pra = new pra();
        JInternalFrame internalFrame2 = pra;
        BasicInternalFrameUI ui2 = (BasicInternalFrameUI) internalFrame2.getUI();
        ui2.setNorthPane(null); // 제목 바 제거
        internalFrame2.setBorder(null);
        internalFrame2.setSize(1440, 800);
        desktopPane2.add(internalFrame2);
        tabbedPane.addTab("할 일", desktopPane2);
        
        // 세 번째 탭에 내부 프레임 추가
        JDesktopPane desktopPane3 = new JDesktopPane();
        Stactics stactics = new Stactics();
        JInternalFrame internalFrame3 = stactics.getInternalFrame();
        BasicInternalFrameUI ui3 = (BasicInternalFrameUI) internalFrame3.getUI();
        ui3.setNorthPane(null);
        internalFrame3.setBorder(null);
        internalFrame3.setSize(1440,800);
        desktopPane3.add(internalFrame3);
        tabbedPane.addTab("통계", desktopPane3);

        // 네 번째 탭에 내부 프레임 추가
        JDesktopPane desktopPane4 = new JDesktopPane();
        SettingFrame setting = new SettingFrame();
        JInternalFrame internalFrame4 = setting;
        BasicInternalFrameUI ui4 = (BasicInternalFrameUI) internalFrame4.getUI();
        ui4.setNorthPane(null);
        internalFrame4.setBorder(null);
        internalFrame4.setSize(1440,800);
        desktopPane4.add(internalFrame4);
        tabbedPane.addTab("사용자 설정", desktopPane4);
        
        // 메인 프레임에 탭 패널 추가
        mainFrame.getContentPane().add(tabbedPane, BorderLayout.CENTER);
        
        // 메인 프레임 표시
        mainFrame.setVisible(true);
    }
    /*
    private static JInternalFrame createInternalFrame(JPanel panel) {
        // 내부 프레임 생성
        JInternalFrame internalFrame = new JInternalFrame("", false, false, false, false);
        internalFrame.setBorder(null); // 테두리 없애기
        internalFrame.setSize(1450, 800);
        internalFrame.setVisible(true);

        // 내부 패널 설정
        internalFrame.setContentPane(panel);

        // 제목 바를 제거하는 코드
        BasicInternalFrameTitlePane titlePane = (BasicInternalFrameTitlePane) ((BasicInternalFrameUI) internalFrame.getUI()).getNorthPane();
        internalFrame.remove(titlePane);

        return internalFrame;
    }
    */
}
