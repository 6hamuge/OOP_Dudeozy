package dudeozy;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Obje extends JInternalFrame {

    // 데이터베이스 연결 정보 (실제 데이터베이스 접속 정보로 수정해야 함)
    private static final String JDBC_URL = "jdbc:oracle:thin:@localhost:1521:xe";
    private static final String USERNAME = "SYSTEM";
    private static final String PASSWORD = "foroopcurie";

    // Swing 컴포넌트
    private JTabbedPane tabbedPane;
    private List<DefaultTableModel> models;

    public Obje() {
        super("할일");
        setUIFont(new FontUIResource(new Font("Malgun Gothic", Font.PLAIN, 16)));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);

        // 탭패널 초기화
        tabbedPane = new JTabbedPane();

        // 데이터베이스에서 과목 정보 가져오기
        List<String> subjects = fetchSubjectsFromDatabase();

        // 각 과목별로 데이터베이스에서 할 일 데이터 가져오기
        fetchTasksFromDatabase(subjects);

        // 각 탭에 테이블 추가
        for (int i = 0; i < subjects.size(); i++) {
            JTable table = new JTable(models.get(i));
            customizeTable(table); // 테이블 커스터마이징 (체크박스 및 취소선 렌더링)
            JScrollPane scrollPane = new JScrollPane(table);
            tabbedPane.addTab(subjects.get(i), scrollPane); // 각 과목명으로 탭 추가
        }

        getContentPane().add(tabbedPane, BorderLayout.CENTER);
        setVisible(true);

        addTableListeners(); // 테이블 리스너 추가
    }

    // 데이터베이스에서 과목 정보 가져오기
    private List<String> fetchSubjectsFromDatabase() {
        List<String> subjects = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            String query = "SELECT DISTINCT subject FROM Schedule";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            // 결과에서 과목명 추출하여 리스트에 추가
            while (rs.next()) {
                subjects.add(rs.getString("subject"));
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return subjects;
    }

    // 각 과목별로 데이터베이스에서 할 일 데이터 가져오기
    private void fetchTasksFromDatabase(List<String> subjects) {
        models = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            for (String subject : subjects) {
                // 각 과목별로 할 일 데이터를 가져오는 쿼리
                String query = "SELECT completed, task, TO_CHAR(year) || '.' || TO_CHAR(month) || '.' || TO_CHAR(day) AS task_date FROM Schedule WHERE subject = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, subject);
                ResultSet rs = stmt.executeQuery();

                // 테이블에 들어갈 데이터 벡터 준비
                DefaultTableModel model = new DefaultTableModel() {
                    @Override
                    public Class<?> getColumnClass(int columnIndex) {
                        if (columnIndex == 0) {
                            return Boolean.class; // 첫 번째 열은 체크박스로 사용할 것임
                        }
                        return String.class; // 나머지 열은 문자열로 처리
                    }
                };

                model.addColumn("완료");
                model.addColumn("할일");
                model.addColumn("날짜");

                while (rs.next()) {
                    model.addRow(new Object[]{rs.getBoolean("completed"), rs.getString("task"), rs.getString("task_date")});
                }

                models.add(model);

                rs.close();
                stmt.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 테이블 커스터마이징 메서드
    private void customizeTable(JTable table) {
        // 첫 번째 열(완료 체크박스)에 대한 렌더러 및 에디터 설정
        table.getColumnModel().getColumn(0).setCellRenderer(new CheckBoxRenderer());
        table.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(new JCheckBox()));

        // 두 번째 열(할 일)에 대한 취소선 렌더러 설정
        table.getColumnModel().getColumn(1).setCellRenderer(new StrikethroughRenderer());
    }

    // 테이블 모델 리스너 추가
    private void addTableListeners() {
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            JTable table = (JTable) ((JScrollPane) tabbedPane.getComponentAt(i)).getViewport().getView();
            DefaultTableModel model = (DefaultTableModel) table.getModel();

            // 테이블 모델 리스너 추가
            model.addTableModelListener(e -> {
                int row = e.getFirstRow();
                int column = e.getColumn();
                if (column == 0) { // 첫 번째 열(완료 체크박스)이 변경되었을 때
                    boolean completed = (boolean) model.getValueAt(row, column);
                    String task = (String) model.getValueAt(row, 1); // 할 일 열의 데이터 예시
                    updateDatabase(task, completed); // 데이터베이스 업데이트 메서드 호출
                    table.repaint(); // 테이블 다시 그리기
                }
            });
        }
    }

    // 데이터베이스 업데이트 메서드
    private void updateDatabase(String task, boolean completed) {
        try (Connection conn = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            String updateQuery = "UPDATE Schedule SET completed = ? WHERE task = ?";
            PreparedStatement stmt = conn.prepareStatement(updateQuery);
            stmt.setInt(1, completed ? 1 : 0);
            stmt.setString(2, task);
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // CheckBoxRenderer 클래스
    class CheckBoxRenderer extends JCheckBox implements TableCellRenderer {
        public CheckBoxRenderer() {
            setHorizontalAlignment(JLabel.CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            } else {
                setBackground(table.getBackground());
                setForeground(table.getForeground());
            }

            // 첫 번째 열(완료 체크박스)에서만 동작하도록 설정
            if (column == 0) {
                setSelected((value != null && (Boolean) value));
            }

            return this;
        }
    }

    // StrikethroughRenderer 클래스
    class StrikethroughRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            boolean isCompleted = (Boolean) table.getValueAt(row, 0);
            if (isCompleted) {
                value = "<html><strike>" + value + "</strike></html>";
            } else {
                value = "<html>" + value + "</html>";
            }
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }

    // 전체 UI 폰트 설정 메서드
    public static void setUIFont(FontUIResource f) {
        java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource) {
                UIManager.put(key, f);
            }
        }
    }

    // 메인 메서드
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Obje::new);
    }
}