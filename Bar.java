package dudeozy;

import javax.swing.*;
import java.awt.*;

public class Bar extends JPanel {
	private String subject;
	private Color color;
	
    public Bar(String subject, Color color) {
    	this.subject = subject;
        this.color = color;
        setPreferredSize(new Dimension(200, 10));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Dimension size = getSize();
        int width = size.width;
        int height = size.height;
        g.setColor(color);
        g.fillRect(0, 0, width, height);
    }
}