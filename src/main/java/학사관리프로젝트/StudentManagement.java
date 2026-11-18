package 학사관리프로젝트;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

public class StudentManagement extends UniversityFrame implements ActionListener {
	private static final int[] STUDENT_COLUMN_WEIGHTS = {88, 55, 103, 48, 248, 125, 123, 98, 105, 85, 68, 202};
	JPanel p1, p2, p3;
	TitledBorder in_bor, ta_bor;
	JLabel title, std_code, name, resnum, addr, phone, number, year, hyear, std_dep, std_prf, gen, email, x2, x3;
	JTextField sea_field, tstd_code, tname, res1, taddr, ph1, ph2, ph3, tnumber, tyear, thyear, tgender, temail;
	JButton b_sea, b_allsea, b_in, b_re, b_del, b_end;
	JComboBox com_dep, com_prf, com_col;
    JComboBox<String> searchChoice;
	String[] cate = { "학번", "이름", "생년월일", "입학년도", "주소", "학과", "교수", "성별", "검색 카테고리 선택" };
	String[] tcate = { "student_cd", "user_name", "user_address", "birth_date", "mobile_no", "phone_no", "admission_year", "graduation_year", "department_cd", "professor_cd", "user_gender", "user_email" };
	String[] searchColumns = { "student_cd", "user_name", "birth_date", "admission_year", "user_address", "department_cd", "professor_cd", "user_gender", "error" };
	private static final String STUDENT_SELECT = "SELECT student_cd, user_name, user_address, birth_date, mobile_no, phone_no, admission_year, graduation_year, department_cd, professor_cd, user_gender, user_email FROM student_info";
	JTable table;
	JScrollPane jsp;
	DefaultTableModel dtm;
	int srow;
	String depcate, depcode, depname;

	Font fon1 = UiFonts.font(Font.PLAIN, 13);
	Font fon2 = UiFonts.font(Font.PLAIN, 15);
	public StudentManagement() throws SQLException {
		this.setTitle("학생관리");
		this.setLayout(null);
		this.setSize(1400, 800); this.setLocationRelativeTo(null);
		

		title();
		inPanel();
		seaPanel();
		taPanel();
		buttons();

		// DB 연결
		DBConnect();
		combo();
		getListAll();
		this.setVisible(true);

	}

	// ================== 타이틀 생성 ===========================
	void title() {
		p1 = new JPanel();
		p1.setLayout(null);
		p1.setBounds(0, 0, 1400, 50);
		p1.setBackground(new Color(123, 139, 155));
		title = new JLabel("학 생 관 리", JLabel.CENTER);
		title.setBounds(0, 10, 1400, 30);
		title.setForeground(Color.WHITE);
		title.setFont(UiFonts.font(Font.BOLD, 24));
		p1.add(title);
		add(p1);
	}
	// ================== 입력보더 생성 ===========================
	void inPanel() {
		p2 = new JPanel(null);
		p2.setBounds(20, 475, 1360, 215);
		p2.setBackground(new Color(245, 245, 245));
		in_bor = new TitledBorder("학생 정보 수정");
		p2.setBorder(in_bor);

		std_code = new JLabel("학번"); std_code.setBounds(30, 20, 90, 25);
		tstd_code = new JTextField(); tstd_code.setBounds(125, 20, 210, 25); tstd_code.setEditable(false);
		name = new JLabel("이름"); name.setBounds(365, 20, 60, 25);
		tname = new JTextField(); tname.setBounds(430, 20, 220, 25);
		resnum = new JLabel("생년월일"); resnum.setBounds(680, 20, 100, 25);
		res1 = new JTextField(); res1.setBounds(785, 20, 225, 25);
        gen = new JLabel("성별"); gen.setBounds(1030, 20, 65, 25);
        tgender = new JTextField(); tgender.setBounds(1100, 20, 205, 25);

		addr = new JLabel("주소"); addr.setBounds(30, 55, 90, 25);
		taddr = new JTextField(); taddr.setBounds(125, 55, 1180, 25);
		phone = new JLabel("휴대폰"); phone.setBounds(30, 90, 90, 25);
		ph1 = new JTextField(); ph1.setBounds(125, 90, 150, 25);
		x2 = new JLabel("-"); x2.setBounds(282, 90, 15, 25);
		ph2 = new JTextField(); ph2.setBounds(300, 90, 180, 25);
		x3 = new JLabel("-"); x3.setBounds(487, 90, 15, 25);
		ph3 = new JTextField(); ph3.setBounds(505, 90, 180, 25);
		number = new JLabel("전화번호"); number.setBounds(720, 90, 90, 25);
		tnumber = new JTextField(); tnumber.setBounds(815, 90, 490, 25);

		year = new JLabel("입학년도"); year.setBounds(30, 125, 90, 25);
		tyear = new JTextField(); tyear.setBounds(125, 125, 210, 25);
		hyear = new JLabel("대학졸업일"); hyear.setBounds(365, 125, 100, 25);
		thyear = new JTextField(); thyear.setBounds(470, 125, 215, 25);
		std_dep = new JLabel("학과"); std_dep.setBounds(720, 125, 90, 25);
		com_dep = new JComboBox(); com_dep.setBounds(815, 125, 190, 25);
		std_prf = new JLabel("지도교수"); std_prf.setBounds(1020, 125, 90, 25);
		com_prf = new JComboBox(); com_prf.setBounds(1115, 125, 190, 25);
		email = new JLabel("이메일"); email.setBounds(30, 165, 90, 25);
        temail = new JTextField(); temail.setBounds(125, 165, 1180, 25);

		java.awt.Component[] components = {std_code,tstd_code,name,tname,resnum,res1,addr,taddr,phone,ph1,x2,ph2,x3,ph3,
				number,tnumber,year,tyear,hyear,thyear,std_dep,com_dep,std_prf,com_prf,gen,tgender,email,temail};
		for (java.awt.Component component : components) {
			component.setFont(fon1);
			p2.add(component);
		}
		add(p2);
        p2.setVisible(false);
	}
	// ================== 검색패널 생성 ===========================
	public void seaPanel() {
		p3 = new JPanel(null);
		p3.setBounds(0, 55, 1400, 65);
		p3.setOpaque(false);
		com_col = new JComboBox(cate); com_col.setBounds(40, 17, 300, 32);
		com_col.setSelectedIndex(cate.length - 1);
		sea_field = new JTextField(); sea_field.setBounds(360, 17, 420, 32);
        searchChoice = new JComboBox<>(); searchChoice.setBounds(360, 17, 420, 32);
        searchChoice.setFont(fon1); searchChoice.setVisible(false);
		b_sea = new JButton("검색"); b_sea.setBounds(900, 17, 170, 32);
		b_allsea = new JButton("전체조회"); b_allsea.setBounds(1100, 17, 240, 32);
		com_col.setFont(fon1); sea_field.setFont(fon1);
		b_sea.setFont(fon2); b_allsea.setFont(fon2);
		b_sea.setBackground(new Color(234, 206, 202));
		b_sea.setForeground(new Color(105, 100, 107));
		b_allsea.setBackground(new Color(234, 206, 202));
		b_allsea.setForeground(new Color(105, 100, 107));
		p3.add(com_col); p3.add(sea_field); p3.add(searchChoice); p3.add(b_sea); p3.add(b_allsea);
		add(p3);
		b_sea.addActionListener(this); b_allsea.addActionListener(this);
        com_col.addActionListener(event -> updateSearchInput());
	}
	// ================== 테이블 생성 ===========================
	public void taPanel() {
		dtm = new DefaultTableModel(null, tcate) {
			public boolean isCellEditable(int row, int column) { return false; }
		};
		table = new JTable(dtm);
		jsp = new JScrollPane(table);
		jsp.setBorder(new TitledBorder("학생 목록"));
		jsp.setBounds(20, 130, 1360, 335);
		table.setBackground(new Color(208, 224, 240));
		jsp.getViewport().setBackground(Color.WHITE);
		add(jsp);
		table.addMouseListener(new MyAdapter());
        configureStudentColumns();
		jsp.getViewport().addComponentListener(new ComponentAdapter() {
			@Override public void componentResized(ComponentEvent event) {
				sizeStudentColumnsToViewport();
			}
		});
		SwingUtilities.invokeLater(this::sizeStudentColumnsToViewport);
	}
	// ================== 버튼 생성 ===========================
	public void buttons() {
		b_in = new JButton("새 학생 등록"); b_in.setBounds(20, 700, 320, 45);
		b_re = new JButton("수정"); b_re.setBounds(360, 700, 320, 45);
		b_del = new JButton("삭제"); b_del.setBounds(700, 700, 320, 45);
		b_end = new JButton("종료"); b_end.setBounds(1040, 700, 320, 45);
		for (JButton button : new JButton[]{b_in,b_re,b_del,b_end}) {
			button.setFont(fon2);
			button.setBackground(new Color(234, 206, 202));
			button.setForeground(new Color(105, 100, 107));
			button.addActionListener(this);
			add(button);
		}
        b_re.setEnabled(false);
        b_del.setEnabled(false);
	}
	// ================== 이벤트 생성 ===========================
	// 테이블
	class MyAdapter extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent event) {
			            int viewRow = table.getSelectedRow();
            if (viewRow < 0) return;
            int row = table.convertRowIndexToModel(viewRow);
            tstd_code.setText(String.valueOf(dtm.getValueAt(row, 0)));
            tname.setText(String.valueOf(dtm.getValueAt(row, 1)));
            taddr.setText(String.valueOf(dtm.getValueAt(row, 2)));
            res1.setText(String.valueOf(dtm.getValueAt(row, 3)));
            String[] mobileParts = String.valueOf(dtm.getValueAt(row, 4)).split("-");
            ph1.setText(mobileParts.length > 0 ? mobileParts[0] : "");
            ph2.setText(mobileParts.length > 1 ? mobileParts[1] : "");
            ph3.setText(mobileParts.length > 2 ? mobileParts[2] : "");
            Object phoneValue = dtm.getValueAt(row, 5);
            tnumber.setText(phoneValue == null ? "" : phoneValue.toString());
            tyear.setText(String.valueOf(dtm.getValueAt(row, 6)));
            Object graduation = dtm.getValueAt(row, 7);
            thyear.setText(graduation == null ? "" : graduation.toString());
            com_dep.setSelectedItem(dtm.getValueAt(row, 8));
            com_prf.setSelectedItem(dtm.getValueAt(row, 9));
            tgender.setText(String.valueOf(dtm.getValueAt(row, 10)));
            temail.setText(String.valueOf(dtm.getValueAt(row, 11)));
            showEditPanel(true);
		}
	}

	// ========================
	    private void configureStudentColumns() {
        javax.swing.table.TableColumnModel columns = table.getColumnModel();
        columns.moveColumn(10, 3); // 성별
        columns.moveColumn(2, 4);  // 주소
        columns.moveColumn(3, 2);  // 생년월일
        String[] labels = {"학번", "이름", "생년월일", "성별", "주소", "휴대폰번호",
                "전화번호", "입학일", "졸업일", "학과명", "지도교수", "이메일"};
        for (int i = 0; i < labels.length; i++) columns.getColumn(i).setHeaderValue(labels[i]);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for (int i = 0; i < STUDENT_COLUMN_WEIGHTS.length; i++) {
            columns.getColumn(i).setPreferredWidth(STUDENT_COLUMN_WEIGHTS[i]);
        }
        table.getTableHeader().repaint();
    }

    private void sizeStudentColumnsToViewport() {
        int availableWidth = jsp.getViewport().getWidth();
        if (availableWidth <= 0) return;
        javax.swing.table.TableColumnModel columns = table.getColumnModel();
        int totalWeight = 0;
        for (int weight : STUDENT_COLUMN_WEIGHTS) totalWeight += weight;
        int usedWidth = 0;
        for (int i = 0; i < STUDENT_COLUMN_WEIGHTS.length; i++) {
            int width = i == STUDENT_COLUMN_WEIGHTS.length - 1
                    ? availableWidth - usedWidth
                    : (int) Math.round(availableWidth * STUDENT_COLUMN_WEIGHTS[i] / (double) totalWeight);
            columns.getColumn(i).setPreferredWidth(width);
            columns.getColumn(i).setWidth(width);
            usedWidth += width;
        }
    }

    private void showEditPanel(boolean visible) {
        p2.setVisible(visible);
        b_re.setEnabled(visible);
        b_del.setEnabled(visible);
        revalidate();
        repaint();
    }
// MySQL 데이터베이스 연결작업
	// ========================
	public void DBConnect() throws SQLException {
		DatabaseConnection.getConnection();
	}

	public void combo() {
		try {
			com_dep.removeAllItems();
			com_prf.removeAllItems();
			String sql = "SELECT department_name FROM department";
			try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(sql);
					ResultSet rows = statement.executeQuery()) {
				while (rows.next()) com_dep.addItem(rows.getString(1));
			}
			sql = "SELECT user_name FROM professor_info ORDER BY professor_cd";
			try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(sql);
					ResultSet rows = statement.executeQuery()) {
				while (rows.next()) com_prf.addItem(rows.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void getListAll() {
		try {
			System.out.println("표 보여주기 메서드");
			dtm.setRowCount(0); // DB에 있는거 JTable 0번부터 표시하겠다.
            if (p2 != null && b_re != null) showEditPanel(false);

			String sql = STUDENT_SELECT;
			try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(sql);
					ResultSet rows = statement.executeQuery()) {
				while (rows.next()) addStudentRow(rows);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void addStudentRow(ResultSet row) throws SQLException {
		String departmentCode = row.getString(9);
		String professorCode = row.getString(10);
		String departmentName = lookupCode("department", "department_name", "department_cd", departmentCode);
		String professorName = lookupCode("professor_info", "user_name", "professor_cd", professorCode);
		dtm.addRow(new Object[] {row.getString(1), row.getString(2), row.getString(3), row.getString(4),
				row.getString(5), row.getString(6), row.getString(7), row.getString(8), departmentName,
				professorName, row.getString(11), row.getString(12)});
	}
	private String lookupCode(String tableName, String resultColumn, String matchColumn, String value) throws SQLException {
		if (value == null) return null;
		String sql = "SELECT " + resultColumn + " FROM " + tableName + " WHERE " + matchColumn + " = ?";
		try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(sql)) {
			statement.setString(1, value);
			try (ResultSet result = statement.executeQuery()) {
				return result.next() ? result.getString(1) : null;
			}
		}
	}

	// ================== 이벤트 생성 ===========================
	// 버튼
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == b_sea) search();
		else if (source == b_allsea) getListAll();
		else if (source == b_re) edit();
        else if (source == b_end) confirmProgramExit();
        else if (source == b_del) deleteSelectedStudent();
		else if (source == b_in) {
			try {
				StudentRegistrationDialog dialog = new StudentRegistrationDialog(this);
				dialog.setVisible(true);
			} catch (SQLException exception) {
				JOptionPane.showMessageDialog(getDialogOwnerFrame(), exception.getMessage(), "학생 등록 오류", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	// 검색
	private void updateSearchInput() {
        String column = searchColumns[com_col.getSelectedIndex()];
        boolean useChoice = "department_cd".equals(column)
                || "professor_cd".equals(column) || "user_gender".equals(column);
        sea_field.setVisible(!useChoice);
        searchChoice.setVisible(useChoice);
        sea_field.setText("");
        searchChoice.removeAllItems();
        if (!useChoice) return;

        if ("user_gender".equals(column)) {
            searchChoice.addItem("남성");
            searchChoice.addItem("여성");
        } else {
            String sql = "department_cd".equals(column)
                    ? "SELECT DISTINCT department_name FROM department ORDER BY department_name"
                    : "SELECT DISTINCT user_name FROM professor_info ORDER BY user_name";
            try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(sql);
                 ResultSet options = statement.executeQuery()) {
                while (options.next()) searchChoice.addItem(options.getString(1));
            } catch (SQLException exception) {
                JOptionPane.showMessageDialog(this, exception.getMessage(),
                        "검색 항목 오류", JOptionPane.ERROR_MESSAGE);
            }
        }
        searchChoice.setSelectedIndex(-1);
    }

    public void search() {
        String column = searchColumns[com_col.getSelectedIndex()];
        if ("error".equals(column)) {
            JOptionPane.showMessageDialog(this, "카테고리를 선택해주세요.");
            com_col.requestFocus();
            return;
        }

        boolean useChoice = "department_cd".equals(column)
                || "professor_cd".equals(column) || "user_gender".equals(column);
        String keyword = useChoice ? (String) searchChoice.getSelectedItem() : sea_field.getText().trim();
        if (keyword == null || keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, useChoice ? "검색할 항목을 선택해주세요." : "검색어를 입력해주세요.");
            if (useChoice) searchChoice.requestFocus(); else sea_field.requestFocus();
            return;
        }

        String condition;
        if ("department_cd".equals(column)) {
            condition = "department_cd IN (SELECT department_cd FROM department WHERE department_name = ?)";
        } else if ("professor_cd".equals(column)) {
            condition = "professor_cd IN (SELECT professor_cd FROM professor_info WHERE user_name = ?)";
        } else if ("birth_date".equals(column) || "admission_year".equals(column)) {
            condition = "CAST(" + column + " AS CHAR) LIKE ?";
        } else {
            condition = column + (useChoice ? " = ?" : " LIKE ?");
        }

        dtm.setRowCount(0);
        showEditPanel(false);
        try (PreparedStatement search = DatabaseConnection.getConnection().prepareStatement(
                STUDENT_SELECT + " WHERE " + condition)) {
            search.setString(1, useChoice ? keyword : "%" + keyword + "%");
            try (ResultSet results = search.executeQuery()) {
                while (results.next()) addStudentRow(results);
            }
            if (table.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "검색 결과가 없습니다.");
            }
        } catch (SQLException exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(), "학생 검색 오류", JOptionPane.ERROR_MESSAGE);
        }
    }
// 수정
	public void edit() {
		java.sql.Connection connection;
		try { connection = DatabaseConnection.getConnection(); }
		catch (SQLException exception) {
			JOptionPane.showMessageDialog(getDialogOwnerFrame(), exception.getMessage(), "DB 연결 오류", JOptionPane.ERROR_MESSAGE);
			return;
		}
		boolean autoCommit = true;
		try {
			String birthDate = DatabaseValues.birthDate(res1.getText(), "");
			String admissionDate = DatabaseValues.academicDate(tyear.getText(), "03-01");
			String graduationDate = DatabaseValues.academicDate(thyear.getText(), "02-28");
			String departmentCode = DatabaseValues.lookupCode(connection, "department", "department_cd", "department_name", (String) com_dep.getSelectedItem());
			String professorCode = DatabaseValues.lookupCode(connection, "professor_info", "professor_cd", "user_name", (String) com_prf.getSelectedItem());
			String gender = tgender.getText().trim();
            String emailValue = temail.getText().trim();
            if (emailValue.isEmpty()) throw new SQLException("이메일을 입력하세요.");
            if (!hasEditChanges(birthDate, admissionDate, graduationDate, gender, emailValue)) return;
			autoCommit = connection.getAutoCommit();
			connection.setAutoCommit(false);
			String userId;
			try (PreparedStatement find = connection.prepareStatement("SELECT user_id FROM student_info WHERE student_cd=?")) {
				find.setString(1, tstd_code.getText());
				try (ResultSet row = find.executeQuery()) {
					if (!row.next()) throw new SQLException("학생 정보를 찾을 수 없습니다.");
					userId = row.getString(1);
				}
			}
			String query = "UPDATE student_info SET user_name=?, user_address=?, birth_date=?, mobile_no=?, phone_no=?, admission_year=?, graduation_year=?, department_cd=?, professor_cd=?, user_gender=?, user_email=? WHERE student_cd=?";
			try (PreparedStatement update = connection.prepareStatement(query)) {
				update.setString(1, tname.getText());
				update.setString(2, taddr.getText());
				update.setString(3, birthDate);
				update.setString(4, ph1.getText() + "-" + ph2.getText() + "-" + ph3.getText());
				if (tnumber.getText().isBlank()) update.setNull(5, java.sql.Types.VARCHAR); else update.setString(5, tnumber.getText());
				update.setString(6, admissionDate);
				if (graduationDate == null) update.setNull(7, java.sql.Types.DATE); else update.setString(7, graduationDate);
				update.setString(8, departmentCode);
				update.setString(9, professorCode);
				update.setString(10, gender);
				update.setString(11, emailValue);
                update.setString(12, tstd_code.getText());
				update.executeUpdate();
			}
			try (PreparedStatement updateAccount = connection.prepareStatement("UPDATE admin_settings SET user_name=?, birth_date=? WHERE user_id=?")) {
				updateAccount.setString(1, tname.getText());
				updateAccount.setString(2, birthDate);
				updateAccount.setString(3, userId);
				updateAccount.executeUpdate();
			}
			connection.commit();
            JOptionPane.showMessageDialog(this, "수정이 완료 되었습니다.", "학생 정보 수정", JOptionPane.INFORMATION_MESSAGE);
			getListAll();
		} catch (SQLException e) {
			try { connection.rollback(); } catch (SQLException rollbackError) { e.addSuppressed(rollbackError); }
			JOptionPane.showMessageDialog(this, e.getMessage(), "학생 수정 오류", JOptionPane.ERROR_MESSAGE);
		} finally {
			try { connection.setAutoCommit(autoCommit); } catch (SQLException e) { e.printStackTrace(); }
		}
	}

	    private boolean hasEditChanges(String birthDate, String admissionDate, String graduationDate,
                                   String gender, String emailValue) {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) return false;
        int row = table.convertRowIndexToModel(viewRow);
        String[] current = {
                tname.getText().trim(), taddr.getText().trim(), birthDate,
                (ph1.getText() + "-" + ph2.getText() + "-" + ph3.getText()).trim(),
                tnumber.getText().trim(), admissionDate, graduationDate,
                (String) com_dep.getSelectedItem(), (String) com_prf.getSelectedItem(),
                gender, emailValue
        };
        int[] modelColumns = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
        for (int i = 0; i < current.length; i++) {
            Object saved = dtm.getValueAt(row, modelColumns[i]);
            String oldValue = saved == null ? "" : saved.toString().trim();
            String newValue = current[i] == null ? "" : current[i].trim();
            if (!oldValue.equals(newValue)) return true;
        }
        return false;
    }
    private int confirmStudentAction(String message) {
        return JOptionPane.showOptionDialog(this, message, "학생 삭제",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null,
                new Object[]{"예", "아니오"}, "아니오");
    }

    private void deleteSelectedStudent() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) return;
        String studentCode = String.valueOf(dtm.getValueAt(table.convertRowIndexToModel(viewRow), 0));
        if (confirmStudentAction("삭제하시겠습니까") != JOptionPane.YES_OPTION) return;
        if (confirmStudentAction("해당 학생의 정보가 영구히 삭제됩니다. 정말로 삭제하시겠습니까?")
                != JOptionPane.YES_OPTION) return;

        java.sql.Connection connection;
        try { connection = DatabaseConnection.getConnection(); }
        catch (SQLException exception) {
            JOptionPane.showMessageDialog(getDialogOwnerFrame(), exception.getMessage(), "DB 연결 오류", JOptionPane.ERROR_MESSAGE);
            return;
        }
        boolean autoCommit = true;
        try {
            autoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            String userId;
            try (PreparedStatement find = connection.prepareStatement(
                    "SELECT user_id FROM student_info WHERE student_cd=? FOR UPDATE")) {
                find.setString(1, studentCode);
                try (ResultSet result = find.executeQuery()) {
                    if (!result.next()) throw new SQLException("학생 정보를 찾을 수 없습니다.");
                    userId = result.getString(1);
                }
            }
            for (String sql : new String[]{
                    "DELETE FROM registration_list WHERE student_cd=?",
                    "DELETE FROM student_grades WHERE student_cd=?",
                    "DELETE FROM student_info WHERE student_cd=?"}) {
                try (PreparedStatement delete = connection.prepareStatement(sql)) {
                    delete.setString(1, studentCode);
                    delete.executeUpdate();
                }
            }
            try (PreparedStatement deleteAccount = connection.prepareStatement(
                    "DELETE FROM admin_settings WHERE user_id=?")) {
                deleteAccount.setString(1, userId);
                deleteAccount.executeUpdate();
            }
            connection.commit();
            getListAll();
            JOptionPane.showMessageDialog(this, "삭제되었습니다.", "학생 삭제", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException exception) {
            try { connection.rollback(); } catch (SQLException rollbackError) {
                exception.addSuppressed(rollbackError);
            }
            JOptionPane.showMessageDialog(this, exception.getMessage(), "학생 삭제 오류", JOptionPane.ERROR_MESSAGE);
        } finally {
            try { connection.setAutoCommit(autoCommit); } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }
    }

    String nextStudentCode() throws SQLException {
        int highest = 0;
        try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(
                "SELECT student_cd FROM student_info");
             ResultSet students = statement.executeQuery()) {
            while (students.next()) {
                String code = students.getString(1);
                if (code != null && code.matches("(?i)s[0-9]+")) {
                    highest = Math.max(highest, Integer.parseInt(code.substring(1)));
                }
            }
        }
        if (highest >= 999) throw new SQLException("학번 숫자 3자리를 모두 사용했습니다.");
        return "s" + (highest + 1);
    }
boolean registerStudent(String code, String studentName, String birthDateInput, String address,
			String mobile, String phoneNumber, String admissionYear,
			String departmentName, String gender) {
		java.sql.Connection connection;
		try { connection = DatabaseConnection.getConnection(); }
		catch (SQLException exception) {
			JOptionPane.showMessageDialog(getDialogOwnerFrame(), exception.getMessage(), "DB 연결 오류", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		boolean autoCommit = true;
		try {
			String codeValue = code.trim();
            if (!codeValue.matches("s[0-9]{1,3}")) throw new SQLException("학번 형식이 올바르지 않습니다.");
			String birthDate = DatabaseValues.birthDate(birthDateInput, "");
			String admissionDate = DatabaseValues.academicDate(admissionYear, "03-01");
			String graduationDate = null;
			String departmentCode = DatabaseValues.lookupCode(connection, "department", "department_cd", "department_name", departmentName);
			if (departmentCode == null) throw new SQLException("학과를 선택하세요.");
            String professorCode;
            try (PreparedStatement advisor = connection.prepareStatement("SELECT professor_cd FROM professor_info WHERE department_cd=? AND retirement_date IS NULL ORDER BY professor_cd LIMIT 1")) {
                advisor.setString(1, departmentCode);
                try (ResultSet row = advisor.executeQuery()) {
                    if (!row.next()) throw new SQLException("해당 학과에 재직 중인 교수가 없습니다.");
                    professorCode = row.getString(1);
                }
            }
			autoCommit = connection.getAutoCommit();
			connection.setAutoCommit(false);
			try (PreparedStatement check = connection.prepareStatement("SELECT 1 FROM student_info WHERE student_cd=?")) {
				check.setString(1, codeValue);
				try (ResultSet found = check.executeQuery()) {
					if (found.next()) {
						connection.rollback();
						JOptionPane.showMessageDialog(getDialogOwnerFrame(), "이미 등록된 학번입니다.", "학생 등록", JOptionPane.WARNING_MESSAGE);
						return false;
					}
				}
			}
			String userId = codeValue.toLowerCase(java.util.Locale.ROOT);
			String temporaryPassword = Passwords.temporary();
			try (PreparedStatement account = connection.prepareStatement(
					"INSERT INTO admin_settings(user_id,user_pw,access_level,user_name,birth_date) VALUES(?,?,?,?,?)")) {
				account.setString(1, userId);
				account.setString(2, Passwords.hash(temporaryPassword.toCharArray()));
				account.setString(3, "student");
				account.setString(4, studentName.trim());
				account.setString(5, birthDate);
				account.executeUpdate();
			}
			String sql = "INSERT INTO student_info(user_id,student_cd,user_name,user_address,mobile_no,phone_no,admission_year,graduation_year,department_cd,professor_cd,user_email,birth_date,user_gender) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";
			try (PreparedStatement insert = connection.prepareStatement(sql)) {
				insert.setString(1, userId);
				insert.setString(2, codeValue);
				insert.setString(3, studentName.trim());
				insert.setString(4, address.trim());
				insert.setString(5, mobile.trim());
				if (phoneNumber.isBlank()) insert.setNull(6, java.sql.Types.VARCHAR); else insert.setString(6, phoneNumber.trim());
				insert.setString(7, admissionDate);
				if (graduationDate == null) insert.setNull(8, java.sql.Types.DATE); else insert.setString(8, graduationDate);
				insert.setString(9, departmentCode);
				insert.setString(10, professorCode);
				insert.setString(11, userId + "@university.ac.kr");
				insert.setString(12, birthDate);
				insert.setString(13, gender.trim());
				insert.executeUpdate();
			}
			connection.commit();
			JOptionPane.showMessageDialog(getDialogOwnerFrame(), "계정: " + userId + "\n임시 비밀번호: " + temporaryPassword
					+ "\n처음 로그인하면 비밀번호를 변경해야 합니다. 이 비밀번호는 다시 표시되지 않습니다.", "계정 발급", JOptionPane.INFORMATION_MESSAGE);
			getListAll();
			return true;
		} catch (SQLException exception) {
			try { connection.rollback(); } catch (SQLException rollbackError) { exception.addSuppressed(rollbackError); }
			if (exception.getErrorCode() == 1062) {
				JOptionPane.showMessageDialog(getDialogOwnerFrame(), "이미 등록된 학번입니다.", "학생 등록", JOptionPane.WARNING_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(getDialogOwnerFrame(), exception.getMessage(), "학생 등록 오류", JOptionPane.ERROR_MESSAGE);
			}
			return false;
		} finally {
			try { connection.setAutoCommit(autoCommit); } catch (SQLException exception) { exception.printStackTrace(); }
		}
	}
	public static void main(String[] args) throws SQLException {
		Login.main(args);
	}

}












