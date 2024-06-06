package First;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.plaf.FontUIResource;
import java.awt.*;

public class Sec {
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
	public static void main(String[] args)
	{
		setUIFont(new FontUIResource(new Font("Malgun Gothic", Font.PLAIN, 16)));
		// 글씨 크기, 체 변경
		Dimension dim = new Dimension(1920,1080);
		JFrame frame = new JFrame("캘린더");
		frame.setLocation(100,100);
		frame.setPreferredSize(dim);
		
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		JPanel dDayPanel = new JPanel();
		 dDayPanel.setLayout(new BoxLayout(dDayPanel, BoxLayout.Y_AXIS));
        JCheckBox fir = new JCheckBox("수학 과제 준비");
        dDayPanel.add(fir);
        dDayPanel.setBorder(BorderFactory.createTitledBorder("D-day" + " / " + "5.18"));
        panel.add(dDayPanel);
        
        JPanel dDayPane2 = new JPanel();
        dDayPane2.setLayout(new BoxLayout(dDayPane2, BoxLayout.Y_AXIS));
        JCheckBox fear= new JCheckBox("데이터구조 과제");
        JCheckBox fear1= new JCheckBox("컴아텍 과제");
        dDayPane2.add(fear);
        dDayPane2.add(fear1);
        dDayPane2.setBorder(BorderFactory.createTitledBorder("D-1" + " / " + "5.19"));
        panel.add(dDayPane2);
        

        JPanel dDayPane3 = new JPanel();
        dDayPane3.setLayout(new BoxLayout(dDayPane3, BoxLayout.Y_AXIS));
        JCheckBox fear3= new JCheckBox("C언어 공부");
        JLabel fear4= new JLabel("개교기념일");
        dDayPane3.add(fear3);
        dDayPane3.add(fear4);
        dDayPane3.setBorder(BorderFactory.createTitledBorder("D-4" + " / " + "5.22"));
        panel.add(dDayPane3);
        
        JScrollPane scrollPane = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        frame.add(scrollPane);
		//허허허 왜지 그거야 모르지
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);	
		
	}
//맑은 고딕 사용하기
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
    private JLabel label; 

    public StrikethroughRenderer() {
        label = new JLabel(); 
        label.setOpaque(true); 
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        boolean isCompleted = (Boolean) table.getValueAt(row, 0);

        if (isSelected) { 
            label.setBackground(table.getSelectionBackground()); 
            label.setForeground(table.getSelectionForeground());
        } else { 
            label.setBackground(table.getBackground());
            label.setForeground(table.getForeground()); 
        }

        if (isCompleted) { 
            label.setText("<html><strike>" + value + "</strike></html>");
        } else {
            label.setText("<html>" + value + "</html>");
        }

        return label;
    }
}
