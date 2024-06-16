package dudeozy;

import java.awt.Color;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ColorHelper {
    private MainPage mainPage;
    private static final Color DEFAULT_COLOR = Color.GRAY; // 기본 색상을 회색으로 설정

    public ColorHelper(MainPage mainPage) {
        this.mainPage = mainPage;
    }

    public Color getSubjectColor(String subject) {
        Connection conn = mainPage.getConnection();
        Color color = DEFAULT_COLOR; // 기본 색상으로 초기화

        try {
            String sql = "SELECT color FROM UserSettingTask WHERE task_name = ? UNION SELECT color FROM UserSettingEvent WHERE event_name = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, subject);
            pstmt.setString(2, subject);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String colorHex = rs.getString("color");
                color = Color.decode(colorHex); // 데이터베이스에서 가져온 색상으로 설정
            }

            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return color;
    }
}


