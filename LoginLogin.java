package doduzy;

import java.sql.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class LoginLogin {

	JFrame frame;
	JPasswordField USER_PW;
	JTextField USER_ID;
	JButton btn_SIGNUP;
	
	JTextField textField_1;
	JPasswordField passwordField;
	JTextField textField_3;
	JTextField textField_4;
	
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;
	
	String db_name="데이터베이스 스키마 이름";
    String db_route="jdbc:oracle:thin:@localhost:1521:xe"; // Oracle에 맞는 URL 형식
	String db_id = "system"; // Oracle 데이터베이스 사용자 ID
	String db_password = "oopdata"; // Oracle 데이터베이스 비밀번호
	
	// 생성자 Constructor ---------------------------->
	public LoginLogin() {
		// ...
	}	
	
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LoginLogin window = new LoginLogin();
					window.LOGIN();
					window.frame.setVisible(true);
					

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	
	// Oracle 접속하기 ---------------------------->
	protected void SQL_CONNECT() {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			con = DriverManager.getConnection(db_route, db_id, db_password);
			stmt = con.createStatement();
			System.out.println("데이터베이스 연결성공");
		
		} catch (Exception e) { System.out.println("데이터베이스 연결실패"); }
	}
	
	
	// Oracle 종료하기 ---------------------------->	
	protected void SQL_DISCONNECT() {
		try {
			if (con != null && !con.isClosed()) con.close();
			if (stmt != null && !stmt.isClosed()) stmt.close();
			System.out.println("데이터베이스 종료성공");
		} catch (Exception e) { System.out.println("데이터베이스 종료실패"); }		
	}
	
	
	// Oracle 연동 > 로그인 시도 ---------------------------->
	protected void SQL_LOGIN() {
		System.out.println("로그인 시도");	
		SQL_CONNECT();
		
		try {
			System.out.println("데이터베이스 추출시작");
			rs = stmt.executeQuery("SELECT * FROM account");
			for (int n=1; rs.next(); n++) {
				System.out.println("계정목록 불러오기 ===>" +
						"\n" + n + " > ID [" + rs.getString("ID") +
						"]\n" + n + " > PW [" + rs.getString("PW") +
						"]\n" + n + " > NAME [" + rs.getString("NAME") +
						"]\n" + n + " > TEL [" + rs.getString("TEL") + "]"
						);
				// 하나씩 대조하다보니, 딱 맞는게 나오면 return 종료
				if (USER_ID.getText().equals(rs.getString("ID")) && USER_PW.getText().equals(rs.getString("PW"))) {
					JOptionPane.showMessageDialog(null, rs.getString("NAME") + "님 환영합니다 :)");
					USER_ID.requestFocus();
					return;
				}			
			} // 다했지만, 안나온다면 return을 만나지못했음으로, 아래 내용이 실행
			JOptionPane.showMessageDialog(null, "ID 혹은 PW를 확인해주세요");
			USER_ID.requestFocus();
			
			
		} catch (Exception e) { System.out.println("오류오류"); }
		
		SQL_DISCONNECT();	
	}
	

	// Oracle 연동 > 회원가입 시도 [매개변수 > ①아디 ②비번 ③이름 ④연락처]
	protected void SQL_SIGNUP(String getID, String getPW, String getNAME, int getTEL) {
	    System.out.println("회원가입 시도");
	    
	    SQL_CONNECT();
	    try {
	        System.out.println("데이터베이스 추출 시작");
	        rs = stmt.executeQuery("SELECT * FROM account");
	        boolean isDuplicate = false;
	        while (rs.next()) {
	            System.out.println("계정 중복확인 > ID [" + rs.getString("ID") + "]");
	            if (getID.equals(rs.getString("ID"))) {
	                JOptionPane.showMessageDialog(null, "[등록불가] 누군가 사용중인 계정입니다");
	                isDuplicate = true;
	                break;
	            }
	        }
	        if (!isDuplicate) {
	            stmt.executeUpdate("INSERT INTO account VALUES('" + 
	                getID + "', '" +
	                getPW + "', '" +
	                getNAME + "', " +
	                getTEL + ")"       
	            );
	            JOptionPane.showMessageDialog(null, "회원가입 성공!");
	        }
	    
	    } catch (Exception e) {
	        System.out.println("회원가입 오류");
	        e.printStackTrace();
	    } finally {
	        SQL_DISCONNECT();
	    }
	}
	
	// 유저인터페이스 [ 1 ] _ 로그인화면 ---------------------------->
	protected void LOGIN() {
		frame = new JFrame();
		frame.getContentPane().setBackground(new Color(255, 255, 255));
		frame.setResizable(false);
		frame.getContentPane().setLayout(null);

		JPanel p1 = new JPanel();
		p1.setBounds(0, 0, 284, 361);
		p1.setBackground(Color.WHITE);

		USER_ID = new JTextField("아이디 입력");
		USER_ID.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
		USER_ID.setHorizontalAlignment(SwingConstants.CENTER);
		USER_ID.setColumns(10);
		USER_ID.setBounds(12, 68, 260, 30);
		USER_ID.setBorder(javax.swing.BorderFactory.createEmptyBorder());
		USER_ID.setForeground(Color.GRAY);
		USER_ID.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				if (USER_ID.getText().equals("아이디 입력")) {
					USER_ID.setText("");
					USER_ID.setForeground(Color.BLACK);
					
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (USER_ID.getText().isEmpty()) {
					USER_ID.setText("아이디 입력");
					USER_ID.setForeground(Color.GRAY);
				}
				
			}
		});
		
		USER_PW = new JPasswordField("비밀번호 입력");
		USER_PW.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
		USER_PW.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					SQL_LOGIN();
					
				}
			}
		});
		USER_PW.setHorizontalAlignment(SwingConstants.CENTER);
		USER_PW.setBounds(12, 160, 260, 30);
		USER_PW.setBorder(javax.swing.BorderFactory.createEmptyBorder());
		USER_PW.setForeground(Color.GRAY);
		USER_PW.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				if (USER_PW.getText().equals("비밀번호 입력")) {
					USER_PW.setText("");
					USER_PW.setForeground(Color.BLACK);
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (USER_PW.getText().isEmpty()) {
					USER_PW.setText("비밀번호 입력");
					USER_PW.setForeground(Color.GRAY);
				}
				
			}
		});
		p1.setLayout(null);


		p1.add(USER_ID);
		p1.add(USER_PW);
		
		btn_SIGNUP = new JButton("회원가입");
		btn_SIGNUP.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SIGNUP();
				btn_SIGNUP.setEnabled(false);
			}
		});
		btn_SIGNUP.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
		btn_SIGNUP.setBackground(Color.WHITE);
		btn_SIGNUP.setBounds(12, 293, 260, 40);
		btn_SIGNUP.setBorderPainted(false);
		btn_SIGNUP.setFocusPainted(false);
		p1.add(btn_SIGNUP);
		
		JLabel lblNewLabel = new JLabel("아이디");
		lblNewLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(12, 43, 260, 15);
		p1.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("비밀번호");
		lblNewLabel_1.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1.setBounds(12, 135, 260, 15);
		p1.add(lblNewLabel_1);
		
		JButton btn_LOGIN = new JButton("로그인");
		btn_LOGIN.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SQL_LOGIN();
			}
		});
		btn_LOGIN.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
		btn_LOGIN.setBackground(Color.WHITE);
		btn_LOGIN.setBounds(12, 243, 260, 40);
		btn_LOGIN.setBorderPainted(false);
		btn_LOGIN.setFocusPainted(false);
		p1.add(btn_LOGIN);
		frame.getContentPane().add(p1);

		frame.setBounds(100, 100, 300, 400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	    protected void SIGNUP() {
	        JFrame signupFrame = new JFrame();
	        signupFrame.addWindowListener(new WindowAdapter() {
	            @Override
	            public void windowClosed(WindowEvent e) {
	                btn_SIGNUP.setEnabled(true);
	            }
	        });
	        signupFrame.setBounds(100, 100, 450, 300);
	        signupFrame.setResizable(false);

	        JPanel p1 = new JPanel();
	        p1.setBounds(0, 0, 284, 361);
	        p1.setBackground(Color.WHITE);
	        p1.setLayout(null);

	        JLabel lblNewLabel_1 = new JLabel("아이디");
	        lblNewLabel_1.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
	        lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
	        lblNewLabel_1.setBounds(15, 23, 100, 15);
	        p1.add(lblNewLabel_1);

	        JLabel lblNewLabel_2 = new JLabel("비밀번호");
	        lblNewLabel_2.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
	        lblNewLabel_2.setHorizontalAlignment(SwingConstants.CENTER);
	        lblNewLabel_2.setBounds(157, 23, 100, 15);
	        p1.add(lblNewLabel_2);

	        JLabel lblNewLabel_3 = new JLabel("이름");
	        lblNewLabel_3.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
	        lblNewLabel_3.setHorizontalAlignment(SwingConstants.CENTER);
	        lblNewLabel_3.setBounds(15, 106, 100, 15);
	        p1.add(lblNewLabel_3);

	        JLabel lblNewLabel_3_1 = new JLabel("전화번호");
	        lblNewLabel_3_1.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
	        lblNewLabel_3_1.setHorizontalAlignment(SwingConstants.CENTER);
	        lblNewLabel_3_1.setBounds(157, 106, 100, 15);
	        p1.add(lblNewLabel_3_1);

	        JTextField textField_1 = new JTextField();
	        textField_1.setBorder(javax.swing.BorderFactory.createEmptyBorder());
	        textField_1.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
	        textField_1.setForeground(Color.GRAY);
	        textField_1.setText("아이디 입력");
	        textField_1.setHorizontalAlignment(SwingConstants.CENTER);
	        textField_1.setBounds(17, 48, 100, 30);
	        textField_1.setColumns(10);
	        textField_1.addFocusListener(new FocusListener() {
	            @Override
	            public void focusGained(java.awt.event.FocusEvent e) {
	                if (textField_1.getText().equals("아이디 입력")) {
	                    textField_1.setText("");
	                    textField_1.setForeground(Color.BLACK);
	                }

	            }
	            @Override
	            public void focusLost(java.awt.event.FocusEvent e) {
	                if (textField_1.getText().isEmpty()) {
	                    textField_1.setText("아이디 입력");
	                    textField_1.setForeground(Color.GRAY);
	                }

	            }

	        });
	        p1.add(textField_1);

	        JPasswordField passwordField = new JPasswordField();
	        passwordField.setForeground(Color.GRAY);
	        passwordField.setText("비밀번호 입력");
	        passwordField.setHorizontalAlignment(SwingConstants.CENTER);
	        passwordField.setBounds(160, 48, 100, 30);
	        passwordField.setBorder(javax.swing.BorderFactory.createEmptyBorder());
	        passwordField.addFocusListener(new FocusListener() {
	            @Override
	            public void focusGained(java.awt.event.FocusEvent e) {
	                if (passwordField.getText().equals("비밀번호 입력")) {
	                    passwordField.setText("");
	                    passwordField.setForeground(Color.BLACK);
	                }

	            }
	            @Override
	            public void focusLost(java.awt.event.FocusEvent e) {
	                if (passwordField.getText().isEmpty()) {
	                    passwordField.setText("비밀번호 입력");
	                    passwordField.setForeground(Color.GRAY);
	                }

	            }
	        });
	        p1.add(passwordField);

	        JTextField textField_3 = new JTextField();
	        textField_3.setBorder(javax.swing.BorderFactory.createEmptyBorder());
	        textField_3.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
	        textField_3.setForeground(Color.GRAY);
	        textField_3.setText("이름 입력");
	        textField_3.setHorizontalAlignment(SwingConstants.CENTER);
	        textField_3.setColumns(10);
	        textField_3.setBounds(17, 138, 100, 30);
	        textField_3.addFocusListener(new FocusListener() {
	            @Override
	            public void focusGained(java.awt.event.FocusEvent e) {
	                if (textField_3.getText().equals("이름 입력")) {
	                    textField_3.setText("");
	                    textField_3.setForeground(Color.BLACK);
	                }

	            }
	            @Override
	            public void focusLost(java.awt.event.FocusEvent e) {
	                if (textField_3.getText().isEmpty()) {
	                    textField_3.setText("이름 입력");
	                    textField_3.setForeground(Color.GRAY);
	                }

	            }
	        });
	        p1.add(textField_3);

	        JTextField textField_4 = new JTextField();
	        textField_4.setBorder(javax.swing.BorderFactory.createEmptyBorder());
	        textField_4.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
	        textField_4.setForeground(Color.GRAY);
	        textField_4.setText("전화번호 입력");
	        textField_4.setHorizontalAlignment(SwingConstants.CENTER);
	        textField_4.setColumns(10);
	        textField_4.setBounds(157, 138, 100, 30);
	        textField_4.addFocusListener(new FocusListener() {
	            @Override
	            public void focusGained(java.awt.event.FocusEvent e) {
	                if (textField_4.getText().equals("전화번호 입력")) {
	                    textField_4.setText("");
	                    textField_4.setForeground(Color.BLACK);
	                }

	            }
	            @Override
	            public void focusLost(java.awt.event.FocusEvent e) {
	                if (textField_4.getText().isEmpty()) {
	                    textField_4.setText("전화번호 입력");
	                    textField_4.setForeground(Color.GRAY);
	                }

	            }
	        });
	        p1.add(textField_4);


	        JButton btn_OK = new JButton("확인");
	        btn_OK.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	                // 회원가입 버튼 눌렀을때 "SQL_SIGNUP()" 메소드 실행 (위에 있음)
	                try {
	                    SQL_SIGNUP( textField_1.getText(), passwordField.getText(), textField_3.getText(), Integer.parseInt(textField_4.getText()) );
	                } catch (Exception NumberFormatException) {
	                    JOptionPane.showMessageDialog(null, "전화번호는 숫자로만 입력가능합니다.");
	                }
	            }
	        });
	        btn_OK.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
	        btn_OK.setBackground(Color.WHITE);
	        btn_OK.setBounds(15, 260, 250, 40);
	        btn_OK.setBorderPainted(false);
	        btn_OK.setFocusPainted(false);
	        p1.add(btn_OK);

	        JButton btn_CANCLE = new JButton("취소");
	        btn_CANCLE.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
	        btn_CANCLE.setBackground(Color.WHITE);
	        btn_CANCLE.setBounds(15, 310, 250, 40);
	        btn_CANCLE.setBorderPainted(false);
	        btn_CANCLE.setFocusPainted(false);
	        // 취소 버튼 클릭 시 처리
	        btn_CANCLE.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	                signupFrame.dispose(); // 회원가입 창 닫기
	            }
	        });
		
	        p1.add(btn_CANCLE);
		    signupFrame.getContentPane().add(p1);
		    signupFrame.setBounds(100, 100, 300, 400);
		    signupFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		    signupFrame.setVisible(true);
	}
}
