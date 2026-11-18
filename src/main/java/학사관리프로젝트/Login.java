package 학사관리프로젝트;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;


public class Login extends UniversityFrame implements ActionListener {
	JPanel p1, p2;
	JLabel title, id, pass;
	JTextField tid;
	JPasswordField tpass;
	JButton btn;
	Font fon, fon1;
	LineBorder line;

	Dimension dimen, dimen1;

	// DB연결
	String sql;
	String userid, userpass, username;
	String accessLevel;

	public Login() throws SQLException {
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new java.awt.event.WindowAdapter() {
			@Override public void windowClosing(java.awt.event.WindowEvent event) {
				confirmProgramExit();
			}
		});
		this.setTitle("로그인");
		this.setLayout(null);
		this.getContentPane().setBackground(new Color(245, 245, 245));
		setSize(600, 300);
		Toolkit tk = Toolkit.getDefaultToolkit();
		dimen = tk.getScreenSize(); // 모니터 해상도 크기 받아옴
		dimen1 = this.getSize(); // 프레임의 크기 반환

		int xpos = (int) (dimen.getWidth() / 2 - dimen1.getWidth() / 2);
		int ypos = (int) (dimen.getHeight() / 2 - dimen1.getHeight() / 2);
		setLocation(xpos, ypos);

		// 폼 생성 메서드
		title();
		login();
		right();

		// DB
		DBConnect();

		// 연결과 화면 구성이 끝난 뒤 창을 보여준다.
		setVisible(true);
	}

	// ================== 타이틀 생성 ===========================
	void title() {
		p1 = new JPanel();
		title = new JLabel("학사 관리 프로그램");
		fon = UiFonts.font(Font.BOLD, 24);
		// 패널 설정
		p1.setLayout(null);
		this.p1.setBounds(0, 0, 600, 50);
		p1.setBackground(new Color(133, 149, 165));
		// 타이틀 설정
		this.title.setBounds(0, 10, 600, 30);
		title.setHorizontalAlignment(SwingConstants.CENTER);
		title.setForeground(Color.WHITE);
		title.setFont(fon);

		p1.add(title);
		add(p1);
	}

	// ================== 로그인 입력 생성 =======================
	void login() {
		id = new JLabel("아이디");
		pass = new JLabel("패스워드");
		tid = new JTextField();
		tpass = new JPasswordField();
		((AbstractDocument) tid.getDocument()).setDocumentFilter(new LengthLimitFilter(5));
		fon1 = UiFonts.font(Font.PLAIN, 13);
		// 위치설정
		this.id.setBounds(170, 80, 60, 25);
		this.tid.setBounds(250, 80, 180, 25);
		this.pass.setBounds(170, 125, 60, 25);
		this.tpass.setBounds(250, 125, 180, 25);
		// 폰트설정
		id.setFont(fon1);
		pass.setFont(fon1);
		// 올리기
		add(id);
		add(tid);
		add(pass);
		add(tpass);
	}

	// ================== 확인 버튼 =======================
	void right() {
		line = new LineBorder(new Color(105, 100, 107), 2);
		p2 = new JPanel();
		btn = new JButton("로그인");
		fon1 = UiFonts.font(Font.BOLD, 16);
		// 패널설정
		p2.setLayout(null);
		this.p2.setBounds(0, 211, 600, 50);
		p2.setBackground(new Color(123, 139, 155));
		// 버튼설정
		btn.setBackground(new Color(234, 206, 202));
		btn.setForeground(new Color(105, 100, 107));
		btn.setFont(fon1);
		btn.setBorder(line);
		this.btn.setBounds(250, 10, 100, 30);
		// 올리기
		p2.add(btn);
		add(p2);

		// 버튼 이벤트 등록
		btn.addActionListener(this);
		tid.addActionListener(event -> tpass.requestFocusInWindow());
		tpass.addActionListener(event -> btn.doClick());
	}

	// ================== 이벤트 생성 ===========================
	public void actionPerformed(ActionEvent e) {
		Object b = e.getSource();
		if (b == btn) {
			DBSelect();
		}
	}

	// ========================
	// MySQL 데이터베이스 연결작업
	// ========================
	public void DBConnect() throws SQLException {
		DatabaseConnection.getConnection();
	}

	public void DBSelect() {
		String enteredId = tid.getText();
		if (enteredId.isEmpty() || enteredId.codePoints().anyMatch(Character::isWhitespace)) {
			JOptionPane.showMessageDialog(this, "아이디 또는 비밀번호가 올바르지 않습니다.",
					"경고", JOptionPane.WARNING_MESSAGE);
			return;
		}
		userid = null;
		username = null;
		accessLevel = null;
		sql = "SELECT user_id,user_name,access_level,user_pw,password_change_required FROM admin_settings WHERE user_id=?";
		char[] enteredPassword = tpass.getPassword();

		try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(sql)) {
			statement.setString(1, enteredId);
			try (ResultSet rows = statement.executeQuery()) {
				if (rows.next() && Passwords.verify(enteredPassword, rows.getString(4))) {
					String role = rows.getString(3);
					if (!"admin".equals(role) && !"professor".equals(role) && !"student".equals(role)) return;
					if (rows.getBoolean(5) && !PasswordChange.require(this, rows.getString(1), rows.getString(4))) return;
					userid = rows.getString(1);
					username = rows.getString(2);
					accessLevel = role;
				}
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "로그인 처리에 실패했습니다. DB 설정과 스키마를 확인하세요.", "로그인 오류", JOptionPane.ERROR_MESSAGE);
			return;
		} finally {
			java.util.Arrays.fill(enteredPassword, '\0');
			tpass.setText("");
		}

		// 등록된 사용자 판독하기
		if (userid != null && enteredId.equals(userid)) {
			JOptionPane.showMessageDialog(this, username + "님 안녕하세요!", "Welcome", JOptionPane.PLAIN_MESSAGE);
			try {
				new UniversityMenu(userid, username, accessLevel);
				this.dispose();
			} catch (SQLException exception) {
				JOptionPane.showMessageDialog(this, "메뉴를 여는 중 오류가 발생했습니다.\n" + exception.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
			}
		} else
			JOptionPane.showMessageDialog(this, "아이디 또는 비밀번호가 올바르지 않습니다.", "경고", JOptionPane.WARNING_MESSAGE);
	}

	public static void main(String[] args) {
		try {
			new Login();
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null,
					"데이터베이스 연결에 실패했습니다. IntelliJ의 Login 실행 설정과 DB 접속 정보를 확인해 주세요.\n"
							+ e.getMessage(),
					"DB 연결 오류", JOptionPane.ERROR_MESSAGE);
		}
	}

	private static final class LengthLimitFilter extends DocumentFilter {
		private final int maxLength;

		private LengthLimitFilter(int maxLength) {
			this.maxLength = maxLength;
		}

		@Override
		public void insertString(FilterBypass filterBypass, int offset, String text, AttributeSet attributes)
				throws BadLocationException {
			if (text != null && filterBypass.getDocument().getLength() + text.length() <= maxLength) {
				filterBypass.insertString(offset, text, attributes);
			}
		}

		@Override
		public void replace(FilterBypass filterBypass, int offset, int length, String text, AttributeSet attributes)
				throws BadLocationException {
			String replacement = text == null ? "" : text;
			int resultingLength = filterBypass.getDocument().getLength() - length + replacement.length();
			if (resultingLength <= maxLength) {
				filterBypass.replace(offset, length, replacement, attributes);
			}
		}
	}
}


