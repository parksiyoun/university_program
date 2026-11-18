package 학사관리프로젝트;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

public class ProfessorManagement extends UniversityFrame implements ActionListener {
	JPanel p1, p2, p3;
	TitledBorder in_bor, ta_bor;
	JLabel title, prf_code, name, resnum, addr, phone, number, year, degree, prf_dep, lab, gen, email, x2, x3;
	JTextField sea_field, tprf_code, tname, res1, taddr, ph1, ph2, ph3, tnumber, tyear, tdegree, tlab, tgender, temail;
	JRadioButton ma, pe;
	ButtonGroup g;
	JButton b_sea, b_allsea, b_in, b_re, b_retire, b_end;
	JComboBox com_col;
    JComboBox<String> searchChoice, tdep;
	String[] cate = { "교수코드", "이름", "생년월일", "임용년도", "주소", "학과", "성별", "검색 카테고리 선택" };
	// String[] cate2 = { "개설학과", "컴퓨터공학", "데이터베이스", "사무자동화", "방사선", "간호" };
	String[] tcate = { "professor_cd", "user_name", "user_address", "birth_date", "mobile_no", "phone_no", "hire_year", "department_cd", "degree", "room_no", "user_gender", "user_email", "retirement_date" };
	JTable table;
	JScrollPane jsp;
	DefaultTableModel dtm;
	int srow;
	String depcate, searchcate, depcode, depname, s;

	Font fon1 = UiFonts.font(Font.PLAIN, 13);
	Font fon2 = UiFonts.font(Font.PLAIN, 15);

	// DB연결
	String[] db = { "professor_cd", "user_name", "birth_date", "hire_year", "user_address", "department_cd", "user_gender", "error" };
	private static final String PROFESSOR_SELECT = "SELECT professor_cd, user_name, user_address, birth_date, mobile_no, phone_no, hire_year, department_cd, degree, room_no, user_gender, user_email, retirement_date FROM professor_info";

	public ProfessorManagement() throws SQLException {
		this.setTitle("교수관리");
		this.setLayout(null);
		this.setSize(1400, 800); this.setLocationRelativeTo(null);
		

		title();
		inPanel();
		seaPanel();
		taPanel();
		buttons();

		// DB 연결
		DBConnect();
		loadDepartmentChoices();
		
        getListAll();
		this.setVisible(true);

	}

	// ================== 타이틀 생성 ===========================
	void title() {
        p1 = new JPanel(null);
        p1.setBounds(0, 0, 1400, 50);
        p1.setBackground(new Color(123, 139, 155));
        title = new JLabel("교 수 관 리", JLabel.CENTER);
        title.setBounds(0, 10, 1400, 30);
        title.setForeground(Color.WHITE);
        title.setFont(UiFonts.font(Font.BOLD, 24));
        p1.add(title);
        add(p1);
    }

    void inPanel() {
        p2 = new JPanel(null);
        p2.setBounds(20, 475, 1360, 215);
        p2.setBackground(new Color(245, 245, 245));
        p2.setBorder(new TitledBorder("교수 정보 수정"));

        prf_code = new JLabel("교수코드"); prf_code.setBounds(30, 20, 90, 25);
        tprf_code = new JTextField(); tprf_code.setBounds(125, 20, 210, 25);
        tprf_code.setEditable(false);
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

        year = new JLabel("임용년도"); year.setBounds(30, 125, 90, 25);
        tyear = new JTextField(); tyear.setBounds(125, 125, 170, 25);
        degree = new JLabel("학위"); degree.setBounds(315, 125, 55, 25);
        tdegree = new JTextField(); tdegree.setBounds(370, 125, 150, 25);
        prf_dep = new JLabel("개설학과"); prf_dep.setBounds(540, 125, 70, 25);
        tdep = new JComboBox<>(); tdep.setBounds(615, 125, 220, 25);
        lab = new JLabel("연구실"); lab.setBounds(855, 125, 65, 25);
        tlab = new JTextField(); tlab.setBounds(925, 125, 380, 25);
        email = new JLabel("이메일"); email.setBounds(30, 165, 90, 25);
        temail = new JTextField(); temail.setBounds(125, 165, 970, 25);
        b_retire = new JButton("퇴임처리"); b_retire.setBounds(1115, 160, 190, 35);
        b_retire.setFont(fon2);
        b_retire.setBackground(new Color(234, 206, 202));
        b_retire.setForeground(new Color(105, 100, 107));
        b_retire.addActionListener(this);
        b_retire.setEnabled(false);

        java.awt.Component[] components = {prf_code, tprf_code, name, tname, resnum, res1,
                gen, tgender, addr, taddr, phone, ph1, x2, ph2, x3, ph3,
                number, tnumber, year, tyear, degree, tdegree, prf_dep, tdep,
                lab, tlab, email, temail};
        for (java.awt.Component component : components) {
            component.setFont(fon1);
            p2.add(component);
        }
        p2.add(b_retire);
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
        for (JButton button : new JButton[]{b_sea, b_allsea}) {
            button.setFont(fon2);
            button.setBackground(new Color(234, 206, 202));
            button.setForeground(new Color(105, 100, 107));
            button.addActionListener(this);
        }
        p3.add(com_col); p3.add(sea_field); p3.add(searchChoice);
        p3.add(b_sea); p3.add(b_allsea);
        add(p3);
        com_col.addActionListener(event -> updateSearchInput());
    }

    public void taPanel() {
        dtm = new DefaultTableModel(null, tcate) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(dtm);
        jsp = new JScrollPane(table);
        jsp.setBorder(new TitledBorder("교수 목록"));
        jsp.setBounds(20, 130, 1360, 335);
        table.setBackground(new Color(208, 224, 240));
        jsp.getViewport().setBackground(Color.WHITE);
        add(jsp);
        table.addMouseListener(new MyAdapter());
        configureProfessorColumns();
    }

    private void configureProfessorColumns() {
        javax.swing.table.TableColumnModel columns = table.getColumnModel();
        columns.moveColumn(10, 3);
        columns.moveColumn(2, 4);
        columns.moveColumn(3, 2);
        columns.moveColumn(12, 8);
        String[] labels = {"교수코드", "이름", "생년월일", "성별", "주소", "휴대폰번호",
                "전화번호", "임용년도", "퇴임일", "학과명", "학위", "연구실", "이메일"};
        for (int i = 0; i < labels.length; i++) columns.getColumn(i).setHeaderValue(labels[i]);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        int[] widths = {75, 65, 90, 42, 210, 110, 105, 68, 90, 95, 52, 125, 200};
        for (int i = 0; i < widths.length; i++) columns.getColumn(i).setPreferredWidth(widths[i]);
        table.getTableHeader().repaint();
    }

    public void buttons() {
        b_in = new JButton("새 교수 등록"); b_in.setBounds(20, 700, 420, 45);
        b_re = new JButton("수정"); b_re.setBounds(480, 700, 420, 45);
        b_end = new JButton("종료"); b_end.setBounds(940, 700, 420, 45);
        for (JButton button : new JButton[]{b_in, b_re, b_end}) {
            button.setFont(fon2);
            button.setBackground(new Color(234, 206, 202));
            button.setForeground(new Color(105, 100, 107));
            button.addActionListener(this);
            add(button);
        }
        b_re.setEnabled(false);
    }

    public void actionPerformed(ActionEvent event) {
        Object source = event.getSource();
        if (source == b_sea) search();
        else if (source == b_allsea) getListAll();
        else if (source == b_in) {
            try {
                ProfessorRegistrationDialog dialog = new ProfessorRegistrationDialog(this);
                dialog.setVisible(true);
            } catch (SQLException exception) {
                JOptionPane.showMessageDialog(getDialogOwnerFrame(), exception.getMessage(),
                        "교수 등록 오류", JOptionPane.ERROR_MESSAGE);
            }
        } else if (source == b_re) edit(tprf_code.getText());
        else if (source == b_retire) retireSelectedProfessor();
        else if (source == b_end) confirmProgramExit();
    }
// 테이블
	class MyAdapter extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent event) {
            int viewRow = table.getSelectedRow();
            if (viewRow < 0) return;
            int row = table.convertRowIndexToModel(viewRow);
            tprf_code.setText(String.valueOf(dtm.getValueAt(row, 0)));
            tname.setText(String.valueOf(dtm.getValueAt(row, 1)));
            taddr.setText(String.valueOf(dtm.getValueAt(row, 2)));
            res1.setText(String.valueOf(dtm.getValueAt(row, 3)));
            String[] parts = String.valueOf(dtm.getValueAt(row, 4)).split("-");
            ph1.setText(parts.length > 0 ? parts[0] : "");
            ph2.setText(parts.length > 1 ? parts[1] : "");
            ph3.setText(parts.length > 2 ? parts[2] : "");
            Object phoneValue = dtm.getValueAt(row, 5);
            tnumber.setText(phoneValue == null ? "" : phoneValue.toString());
            tyear.setText(String.valueOf(dtm.getValueAt(row, 6)));
            try {
                loadDepartmentChoices();
                selectProfessorDepartment(tprf_code.getText());
            } catch (SQLException exception) {
                JOptionPane.showMessageDialog(ProfessorManagement.this, exception.getMessage(),
                        "학과 조회 오류", JOptionPane.ERROR_MESSAGE);
                return;
            }
            tdegree.setText(String.valueOf(dtm.getValueAt(row, 8)));
            tlab.setText(String.valueOf(dtm.getValueAt(row, 9)));
            tgender.setText(String.valueOf(dtm.getValueAt(row, 10)));
            temail.setText(String.valueOf(dtm.getValueAt(row, 11)));
            showEditPanel(true);
            b_retire.setText(dtm.getValueAt(row, 12) == null ? "퇴임처리" : "퇴임 취소");
            b_retire.setEnabled(true);
        }
    }

    private void showEditPanel(boolean visible) {
        p2.setVisible(visible);
        b_re.setEnabled(visible);
        b_retire.setEnabled(false);
        if (!visible) b_retire.setText("퇴임처리");
        revalidate();
        repaint();
    }
public void DBConnect() throws SQLException {
        DatabaseConnection.getConnection();
    }

public void getListAll() {
        dtm.setRowCount(0);
        showEditPanel(false);
        try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(PROFESSOR_SELECT);
             ResultSet professors = statement.executeQuery()) {
            while (professors.next()) addProfessorRow(professors);
        } catch (SQLException exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(), "교수 목록 오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addProfessorRow(ResultSet professor) throws SQLException {
        String departmentCode = professor.getString(8);
        String departmentName = departmentCode;
        try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(
                "SELECT department_name FROM department WHERE department_cd=?")) {
            statement.setString(1, departmentCode);
            try (ResultSet department = statement.executeQuery()) {
                if (department.next()) departmentName = department.getString(1);
            }
        }
        dtm.addRow(new Object[]{professor.getString(1), professor.getString(2),
                professor.getString(3), professor.getString(4), professor.getString(5),
                professor.getString(6), professor.getString(7), departmentName,
                professor.getString(9), professor.getString(10), professor.getString(11), professor.getString(12), professor.getString(13)});
    }

    private void updateSearchInput() {
        String column = db[com_col.getSelectedIndex()];
        boolean useChoice = "department_cd".equals(column) || "user_gender".equals(column);
        sea_field.setVisible(!useChoice);
        searchChoice.setVisible(useChoice);
        sea_field.setText("");
        searchChoice.removeAllItems();
        if (!useChoice) return;

        if ("user_gender".equals(column)) {
            searchChoice.addItem("남성");
            searchChoice.addItem("여성");
        } else {
            try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(
                    "SELECT DISTINCT department_name FROM department ORDER BY department_name");
                 ResultSet departments = statement.executeQuery()) {
                while (departments.next()) searchChoice.addItem(departments.getString(1));
            } catch (SQLException exception) {
                JOptionPane.showMessageDialog(this, exception.getMessage(),
                        "검색 항목 오류", JOptionPane.ERROR_MESSAGE);
            }
        }
        searchChoice.setSelectedIndex(-1);
    }

    public void search() {
        String column = db[com_col.getSelectedIndex()];
        if ("error".equals(column)) {
            JOptionPane.showMessageDialog(this, "카테고리를 선택해주세요.");
            com_col.requestFocus();
            return;
        }
        boolean useChoice = "department_cd".equals(column) || "user_gender".equals(column);
        String keyword = useChoice ? (String) searchChoice.getSelectedItem() : sea_field.getText().trim();
        if (keyword == null || keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, useChoice ? "검색할 항목을 선택해주세요." : "검색어를 입력해주세요.");
            if (useChoice) searchChoice.requestFocus(); else sea_field.requestFocus();
            return;
        }

        String condition;
        if ("department_cd".equals(column)) {
            condition = "department_cd IN (SELECT department_cd FROM department WHERE department_name = ?)";
        } else if ("birth_date".equals(column) || "hire_year".equals(column)) {
            condition = "CAST(" + column + " AS CHAR) LIKE ?";
        } else {
            condition = column + (useChoice ? " = ?" : " LIKE ?");
        }

        dtm.setRowCount(0);
        showEditPanel(false);
        try (PreparedStatement search = DatabaseConnection.getConnection().prepareStatement(
                PROFESSOR_SELECT + " WHERE " + condition)) {
            search.setString(1, useChoice ? keyword : "%" + keyword + "%");
            try (ResultSet professors = search.executeQuery()) {
                while (professors.next()) addProfessorRow(professors);
            }
            if (dtm.getRowCount() == 0) JOptionPane.showMessageDialog(this, "검색 결과가 없습니다.");
        } catch (SQLException exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(), "교수 검색 오류", JOptionPane.ERROR_MESSAGE);
        }
    }
String nextProfessorCode() throws SQLException {
        int highest = 0;
        try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(
                "SELECT professor_cd FROM professor_info");
             ResultSet professors = statement.executeQuery()) {
            while (professors.next()) {
                String code = professors.getString(1);
                if (code != null && code.matches("(?i)p[0-9]+")) {
                    highest = Math.max(highest, Integer.parseInt(code.substring(1)));
                }
            }
        }
        if (highest >= 999) throw new SQLException("교수코드 숫자 3자리를 모두 사용했습니다.");
        return "p" + (highest + 1);
    }

    private void loadDepartmentChoices() throws SQLException {
        tdep.removeAllItems();
        tdep.addItem("학과 선택");
        try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(
                "SELECT department_cd,department_name FROM department ORDER BY department_cd");
             ResultSet rows = statement.executeQuery()) {
            while (rows.next()) tdep.addItem(rows.getString(2) + " (" + rows.getString(1) + ")");
        }
    }

    private void selectProfessorDepartment(String professorCode) throws SQLException {
        try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(
                "SELECT department_cd FROM professor_info WHERE professor_cd=?")) {
            statement.setString(1, professorCode);
            try (ResultSet row = statement.executeQuery()) {
                if (!row.next()) throw new SQLException("교수 정보를 찾을 수 없습니다.");
                String code = row.getString(1);
                for (int i = 1; i < tdep.getItemCount(); i++) {
                    if (tdep.getItemAt(i).endsWith(" (" + code + ")")) {
                        tdep.setSelectedIndex(i);
                        return;
                    }
                }
            }
        }
        throw new SQLException("교수의 소속 학과가 등록된 학과 목록에 없습니다.");
    }

    private String departmentCode(java.sql.Connection connection, String departmentSelection) throws SQLException {
        if (departmentSelection == null || !departmentSelection.endsWith(")")) {
            throw new SQLException("등록된 개설학과를 선택하세요.");
        }
        int start = departmentSelection.lastIndexOf(" (");
        if (start < 0) throw new SQLException("등록된 개설학과를 선택하세요.");
        String code = departmentSelection.substring(start + 2, departmentSelection.length() - 1);
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT department_cd FROM department WHERE department_cd=?")) {
            statement.setString(1, code);
            try (ResultSet department = statement.executeQuery()) {
                if (department.next()) return department.getString(1);
            }
        }
        throw new SQLException("등록된 개설학과를 선택하세요.");
    }

    boolean registerProfessor(String code, String professorName, String birthDateInput,
            String address, String mobile, String phoneNumber, String hireYear, String degreeValue,
            String departmentName, String room, String emailValue, String gender) {
        java.sql.Connection connection;
        try { connection = DatabaseConnection.getConnection(); }
        catch (SQLException exception) {
            JOptionPane.showMessageDialog(getDialogOwnerFrame(), exception.getMessage(), "DB 연결 오류", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        boolean autoCommit = true;
        try {
            String codeValue = code.trim();
            if (!codeValue.matches("p[0-9]{1,3}")) throw new SQLException("교수코드 형식이 올바르지 않습니다.");
            String birthDate = DatabaseValues.birthDate(birthDateInput, "");
            if (!hireYear.trim().matches("\\d{4}")) throw new SQLException("임용년도는 숫자 네 자리로 입력하세요.");
            if (room.trim().length() > 10) throw new SQLException("연구실은 10자 이내로 입력하세요.");
            if (!"남성".equals(gender) && !"여성".equals(gender)) throw new SQLException("성별을 선택하세요.");
            String department = departmentCode(connection, departmentName);
            autoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try (PreparedStatement check = connection.prepareStatement(
                    "SELECT 1 FROM professor_info WHERE professor_cd=?")) {
                check.setString(1, codeValue);
                try (ResultSet existing = check.executeQuery()) {
                    if (existing.next()) throw new SQLException("이미 등록된 교수코드입니다.");
                }
            }
            String userId = codeValue.toLowerCase(java.util.Locale.ROOT);
            String temporaryPassword = Passwords.temporary();
            try (PreparedStatement account = connection.prepareStatement(
                    "INSERT INTO admin_settings(user_id,user_pw,access_level,user_name,birth_date) VALUES(?,?,?,?,?)")) {
                account.setString(1, userId);
                account.setString(2, Passwords.hash(temporaryPassword.toCharArray()));
                account.setString(3, "professor");
                account.setString(4, professorName.trim());
                account.setString(5, birthDate);
                account.executeUpdate();
            }
            try (PreparedStatement insert = connection.prepareStatement(
                    "INSERT INTO professor_info(user_id,professor_cd,user_name,user_address,mobile_no,"
                    + "phone_no,hire_year,degree,department_cd,room_no,user_email,birth_date,user_gender)"
                    + " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)")) {
                insert.setString(1, userId);
                insert.setString(2, codeValue);
                insert.setString(3, professorName.trim());
                insert.setString(4, address.trim());
                insert.setString(5, mobile.trim());
                if (phoneNumber.isBlank()) insert.setNull(6, java.sql.Types.VARCHAR);
                else insert.setString(6, phoneNumber.trim());
                insert.setString(7, hireYear.trim());
                insert.setString(8, degreeValue.trim());
                insert.setString(9, department);
                insert.setString(10, room.trim());
                insert.setString(11, emailValue.trim());
                insert.setString(12, birthDate);
                insert.setString(13, gender);
                insert.executeUpdate();
            }
            connection.commit();
            JOptionPane.showMessageDialog(getDialogOwnerFrame(), "계정: " + userId + "\n임시 비밀번호: " + temporaryPassword
                    + "\n처음 로그인하면 비밀번호를 변경해야 합니다. 이 비밀번호는 다시 표시되지 않습니다.", "계정 발급", JOptionPane.INFORMATION_MESSAGE);
            getListAll();
            return true;
        } catch (SQLException exception) {
            try { connection.rollback(); } catch (SQLException rollbackError) {
                exception.addSuppressed(rollbackError);
            }
            String message = exception.getErrorCode() == 1062
                    ? "이미 등록된 교수코드입니다." : exception.getMessage();
            JOptionPane.showMessageDialog(getDialogOwnerFrame(), message, "교수 등록 오류", JOptionPane.ERROR_MESSAGE);
            return false;
        } finally {
            try { connection.setAutoCommit(autoCommit); } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }
    }

    public int edit(String professorCode) {
        java.sql.Connection connection;
        try { connection = DatabaseConnection.getConnection(); }
        catch (SQLException exception) {
            JOptionPane.showMessageDialog(getDialogOwnerFrame(), exception.getMessage(), "DB 연결 오류", JOptionPane.ERROR_MESSAGE);
            return 0;
        }
        boolean autoCommit = true;
        try {
            String birthDate = DatabaseValues.birthDate(res1.getText(), "");
            String gender = tgender.getText().trim();
            if (!"남성".equals(gender) && !"여성".equals(gender)) {
                throw new SQLException("성별은 남성 또는 여성으로 입력하세요.");
            }
            String hireYear = tyear.getText().trim();
            if (!hireYear.matches("\\d{4}")) throw new SQLException("임용년도는 숫자 네 자리로 입력하세요.");
            if (tlab.getText().trim().length() > 10) throw new SQLException("연구실은 10자 이내로 입력하세요.");
            String emailValue = temail.getText().trim();
            if (emailValue.isEmpty()) throw new SQLException("이메일을 입력하세요.");
            String department = departmentCode(connection, (String) tdep.getSelectedItem());
            autoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            String userId;
            try (PreparedStatement find = connection.prepareStatement(
                    "SELECT user_id FROM professor_info WHERE professor_cd=?")) {
                find.setString(1, professorCode);
                try (ResultSet professor = find.executeQuery()) {
                    if (!professor.next()) throw new SQLException("교수 정보를 찾을 수 없습니다.");
                    userId = professor.getString(1);
                }
            }
            int updated;
            try (PreparedStatement update = connection.prepareStatement(
                    "UPDATE professor_info SET user_name=?,user_address=?,birth_date=?,mobile_no=?,"
                    + "phone_no=?,hire_year=?,department_cd=?,degree=?,room_no=?,user_gender=?,user_email=?"
                    + " WHERE professor_cd=?")) {
                update.setString(1, tname.getText().trim());
                update.setString(2, taddr.getText().trim());
                update.setString(3, birthDate);
                update.setString(4, ph1.getText().trim() + "-" + ph2.getText().trim() + "-" + ph3.getText().trim());
                if (tnumber.getText().isBlank()) update.setNull(5, java.sql.Types.VARCHAR);
                else update.setString(5, tnumber.getText().trim());
                update.setString(6, hireYear);
                update.setString(7, department);
                update.setString(8, tdegree.getText().trim());
                update.setString(9, tlab.getText().trim());
                update.setString(10, gender);
                update.setString(11, emailValue);
                update.setString(12, professorCode);
                updated = update.executeUpdate();
            }
            try (PreparedStatement account = connection.prepareStatement(
                    "UPDATE admin_settings SET user_name=?,birth_date=? WHERE user_id=?")) {
                account.setString(1, tname.getText().trim());
                account.setString(2, birthDate);
                account.setString(3, userId);
                account.executeUpdate();
            }
            connection.commit();
            getListAll();
            JOptionPane.showMessageDialog(this, "수정이 완료 되었습니다.", "교수 정보 수정", JOptionPane.INFORMATION_MESSAGE);
            return updated;
        } catch (SQLException exception) {
            try { connection.rollback(); } catch (SQLException rollbackError) {
                exception.addSuppressed(rollbackError);
            }
            JOptionPane.showMessageDialog(this, exception.getMessage(), "교수 수정 오류", JOptionPane.ERROR_MESSAGE);
            return 0;
        } finally {
            try { connection.setAutoCommit(autoCommit); } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }
    }
private void retireSelectedProfessor() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) return;
        int row = table.convertRowIndexToModel(viewRow);
        String professorCode = String.valueOf(dtm.getValueAt(row, 0));
        String professorName = String.valueOf(dtm.getValueAt(row, 1));
        if (dtm.getValueAt(row, 12) != null) {
            cancelRetirement(professorCode, professorName);
            return;
        }

        String input = JOptionPane.showInputDialog(this,
                "퇴임 날짜를 입력하세요 (YYYY-MM-DD).", java.time.LocalDate.now().toString());
        if (input == null) return;
        java.time.LocalDate retirementDate;
        try {
            retirementDate = java.time.LocalDate.parse(input.trim());
        } catch (java.time.format.DateTimeParseException exception) {
            JOptionPane.showMessageDialog(this, "날짜는 YYYY-MM-DD 형식으로 입력하세요.",
                    "퇴임 날짜", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (retirementDate.isAfter(java.time.LocalDate.now())) {
            JOptionPane.showMessageDialog(this, "미래 날짜는 퇴임일로 입력할 수 없습니다.",
                    "퇴임 날짜", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int answer = JOptionPane.showOptionDialog(this,
                professorName + " 교수님을 " + retirementDate + "에 퇴임 처리하시겠습니까?",
                "퇴임처리 확인", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, new Object[]{"예", "아니오"}, "아니오");
        if (answer != JOptionPane.YES_OPTION) return;

        try (PreparedStatement update = DatabaseConnection.getConnection().prepareStatement(
                "UPDATE professor_info SET retirement_date=? "
                + "WHERE professor_cd=? AND retirement_date IS NULL")) {
            update.setDate(1, java.sql.Date.valueOf(retirementDate));
            update.setString(2, professorCode);
            if (update.executeUpdate() != 1) {
                JOptionPane.showMessageDialog(this, "교수 정보를 찾을 수 없거나 이미 퇴임 처리되었습니다.",
                        "퇴임처리", JOptionPane.WARNING_MESSAGE);
                getListAll();
                return;
            }
            getListAll();
            JOptionPane.showMessageDialog(this, "퇴임 처리되었습니다.", "퇴임처리",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(),
                    "퇴임처리 오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelRetirement(String professorCode, String professorName) {
        int answer = JOptionPane.showOptionDialog(this,
                professorName + " 교수님의 퇴임 처리를 취소하시겠습니까?",
                "퇴임 취소 확인", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, new Object[]{"예", "아니오"}, "아니오");
        if (answer != JOptionPane.YES_OPTION) return;

        try (PreparedStatement update = DatabaseConnection.getConnection().prepareStatement(
                "UPDATE professor_info SET retirement_date=NULL "
                + "WHERE professor_cd=? AND retirement_date IS NOT NULL")) {
            update.setString(1, professorCode);
            if (update.executeUpdate() != 1) {
                JOptionPane.showMessageDialog(this, "교수 정보를 찾을 수 없거나 이미 재직 중입니다.",
                        "퇴임 취소", JOptionPane.WARNING_MESSAGE);
                getListAll();
                return;
            }
            getListAll();
            JOptionPane.showMessageDialog(this, "퇴임 처리가 취소되었습니다.", "퇴임 취소",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(),
                    "퇴임 취소 오류", JOptionPane.ERROR_MESSAGE);
        }
    }
public static void main(String[] args) throws SQLException {
		Login.main(args);
	}
}



