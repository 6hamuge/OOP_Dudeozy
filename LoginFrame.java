package TeamProject;

import javax.swing.*;
import java.awt.*;

public class LoginFrame {

	public static void main(String[] args) {
		JFrame f = new JFrame("로그인");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(500, 400);
        f.setLocationRelativeTo(null); 

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); 
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel l1 = new JLabel("ID");
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(l1, gbc);

        JTextField text = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(text, gbc);

        JLabel l2 = new JLabel("PASSWORD");
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(l2, gbc);

        JPasswordField value = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(value, gbc);

        JButton b = new JButton("Login");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(b, gbc);

        f.add(panel);
        f.setVisible(true);

	}

}
