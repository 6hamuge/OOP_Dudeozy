package Doo;

import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

public class subject extends JFrame {
	JTabbedPane t = new JTabbedPane();
	JTable table3 = new JTable();
    JTable table1 = new JTable();
    JTable table2 = new JTable();

    public pra() {
        super.setTitle("할일");

        Object[][] data1 = {
            {false, "1단원 끝", "2024.4.19"},
            {false, "2단원 끝", "2024.4.23"}
        };
        Object[][] data2 = {
            {false, "오디세이아 끝", "2024.4.25"},
            {false, "프시케 끝", "2024.4.30"}
        };
        
        Object[][] data3 = {
                {false, "CPU 끝", "2024.4.25"},
                {false, "회로 끝", "2024.4.27"}
            };
        Object[] columnNames = {"완료", "할일", "날짜"};
        
        DefaultTableModel model1 = new DefaultTableModel(data1, columnNames) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return Boolean.class;
                }
                return String.class;
            }
        };

        DefaultTableModel model2 = new DefaultTableModel(data2, columnNames) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return Boolean.class;
                }
                return String.class;
            }
        };
        
        DefaultTableModel model3 = new DefaultTableModel(data3, columnNames) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return Boolean.class;
                }
                return String.class;
            }
        };

        table1 = new JTable(model1);
        table2 = new JTable(model2);
        table3 = new JTable(model3);
        
        // 체크박스 렌더러와 에디터 설정
        table1.getColumnModel().getColumn(0).setCellRenderer((TableCellRenderer) new CheckBoxRenderer());
        table1.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(new JCheckBox()));
        table2.getColumnModel().getColumn(0).setCellRenderer(new CheckBoxRenderer()); 
        table2.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(new JCheckBox())); 
        table3.getColumnModel().getColumn(0).setCellRenderer(new CheckBoxRenderer()); 
        table3.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(new JCheckBox())); 

        // 텍스트 셀 렌더러 설정
        for (int i = 1; i < table1.getColumnCount(); i++) {
            table1.getColumnModel().getColumn(i).setCellRenderer(new StrikethroughRenderer());
            table2.getColumnModel().getColumn(i).setCellRenderer(new StrikethroughRenderer());
            table3.getColumnModel().getColumn(i).setCellRenderer(new StrikethroughRenderer());
        }

        // 테이블 모델 리스너 추가
        model1.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                int row = e.getFirstRow();
                int column = e.getColumn();
                if (column == 0) { // 체크박스 열이 변경되었을 때
                    model1.fireTableRowsUpdated(row, row);
                }
            }
        });

        model2.addTableModelListener(new TableModelListener() { 
            @Override
            public void tableChanged(TableModelEvent e) {
                int row = e.getFirstRow();
                int column = e.getColumn();
                if (column == 0) { // 체크박스 열이 변경되었을 때
                    model2.fireTableRowsUpdated(row, row);
                }
            }
        });

        model3.addTableModelListener(new TableModelListener() { 
            @Override
            public void tableChanged(TableModelEvent e) {
                int row = e.getFirstRow();
                int column = e.getColumn();
                if (column == 0) { // 체크박스 열이 변경되었을 때
                    model3.fireTableRowsUpdated(row, row);
                }
            }
        });
        
        JScrollPane scrollPane1 = new JScrollPane(table1);
        JScrollPane scrollPane2 = new JScrollPane(table2);
        JScrollPane scrollPane3 = new JScrollPane(table3);
        t.add("공학수학", scrollPane1);
        t.add("컴퓨터아키텍쳐", scrollPane3);
        t.add("신화의 이해", scrollPane2);

        setLayout(new BorderLayout());
        add(t, BorderLayout.CENTER); 

        setSize(1920, 1080);
        setLocationRelativeTo(null);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new pra());
    }
}

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
        setSelected((value != null && (Boolean) value));
        return this;
    }
}

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
