package 학사관리프로젝트;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

public class SubjectManagement extends UniversityFrame implements ActionListener {
    private static final String SUBJECT_SELECT =
            "SELECT subject_cd,subject_name,department_cd,academic_year,semester,"
            + "subject_hour,professor_cd,subject_credit FROM subject";
    private static final String[] SEARCH_COLUMNS =
            {"subject_cd", "subject_name", "department_cd", "professor_cd",
                    "subject_hour", "subject_credit", ""};
    private final Font fieldFont = UiFonts.font(Font.PLAIN, 13);
    private final Font buttonFont = UiFonts.font(Font.PLAIN, 15);

    private JPanel editPanel;
    private JTextField codeField, nameField, yearField, startTimeField, endTimeField, searchField, professorField;
    private JComboBox<String> departmentField, semesterField, dayField, creditField,
            searchCategory, searchChoice, searchYear, searchSemester;
    private JButton searchButton, allButton, registerButton, editButton, deleteButton, closeButton;
    private JTable table;
    private DefaultTableModel model;

    public SubjectManagement() throws SQLException {
        setTitle("강좌 개설 관리");
        setLayout(null);
        setSize(1400, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        buildTitle();
        buildEditPanel();
        buildSearchPanel();
        buildTable();
        buildButtons();
        DatabaseConnection.getConnection();
        loadDepartments();
        loadAll();
        setVisible(true);
    }

    private void buildTitle() {
        JPanel titlePanel = new JPanel(null);
        titlePanel.setBounds(0, 0, 1400, 50);
        titlePanel.setBackground(new Color(123, 139, 155));
        JLabel title = new JLabel("강 좌 개 설 관 리", JLabel.CENTER);
        title.setBounds(0, 10, 1400, 30);
        title.setForeground(Color.WHITE);
        title.setFont(UiFonts.font(Font.BOLD, 24));
        titlePanel.add(title);
        add(titlePanel);
    }

    private void buildEditPanel() {
        editPanel = new JPanel(null);
        editPanel.setBounds(20, 475, 1360, 205);
        editPanel.setBackground(new Color(245, 245, 245));
        editPanel.setBorder(new TitledBorder("강좌 정보 수정"));

        JLabel codeLabel = new JLabel("강좌코드"); codeLabel.setBounds(30, 25, 90, 25);
        codeField = new JTextField(); codeField.setBounds(125, 25, 210, 25);
        codeField.setEditable(false);
        JLabel nameLabel = new JLabel("강좌명"); nameLabel.setBounds(365, 25, 60, 25);
        nameField = new JTextField(); nameField.setBounds(430, 25, 220, 25);
        JLabel yearLabel = new JLabel("개설년도"); yearLabel.setBounds(680, 25, 100, 25);
        yearField = new JTextField(); yearField.setBounds(785, 25, 180, 25);
        yearField.setToolTipText("연도 네 자리(예: 2026)");
        JLabel semesterLabel = new JLabel("학기"); semesterLabel.setBounds(990, 25, 90, 25);
        semesterField = new JComboBox<>(new String[]{"1학기", "2학기", "여름학기", "겨울학기"});
        semesterField.setBounds(1085, 25, 220, 25);

        JLabel departmentLabel = new JLabel("학과"); departmentLabel.setBounds(30, 75, 90, 25);
        departmentField = new JComboBox<>(); departmentField.setBounds(125, 75, 290, 25);
        JLabel professorLabel = new JLabel("담당교수"); professorLabel.setBounds(450, 75, 90, 25);
        professorField = new JTextField(); professorField.setBounds(545, 75, 250, 25);
        professorField.setEditable(false);
        JLabel dayLabel = new JLabel("요일"); dayLabel.setBounds(30, 125, 90, 25);
        dayField = new JComboBox<>(new String[]{"요일 선택", "월", "화", "수", "목", "금"});
        dayField.setBounds(125, 125, 165, 25);
        JLabel startLabel = new JLabel("시작시간"); startLabel.setBounds(330, 125, 90, 25);
        startTimeField = new JTextField(); startTimeField.setBounds(425, 125, 145, 25);
        startTimeField.setToolTipText("HH:mm, 예: 09:00");
        JLabel endLabel = new JLabel("종료시간"); endLabel.setBounds(610, 125, 90, 25);
        endTimeField = new JTextField(); endTimeField.setBounds(705, 125, 145, 25);
        endTimeField.setToolTipText("HH:mm, 예: 12:00");
        JLabel creditLabel = new JLabel("학점"); creditLabel.setBounds(900, 125, 60, 25);
        creditField = new JComboBox<>(new String[]{"1", "2", "3", "4", "5"});
        creditField.setBounds(965, 125, 165, 25);

        for (java.awt.Component component : new java.awt.Component[]{
                codeLabel, codeField, nameLabel, nameField, yearLabel, yearField,
                semesterLabel, semesterField, departmentLabel, departmentField,
                professorLabel, professorField, dayLabel, dayField, startLabel, startTimeField,
                endLabel, endTimeField, creditLabel, creditField}) {
            component.setFont(fieldFont);
            editPanel.add(component);
        }
        add(editPanel);
        editPanel.setVisible(false);
        departmentField.addActionListener(event -> showAssignedProfessor());
    }

    private void buildSearchPanel() {
        JPanel searchPanel = new JPanel(null);
        searchPanel.setBounds(0, 55, 1400, 65);
        searchPanel.setOpaque(false);
        searchCategory = new JComboBox<>(new String[]{
                "강좌코드", "강좌명", "학과", "담당교수", "강의 요일", "학점", "검색 항목 선택"});
        searchCategory.setBounds(25, 17, 210, 32);
        searchCategory.setSelectedIndex(6);
        searchField = new JTextField(); searchField.setBounds(245, 17, 220, 32);
        searchField.setVisible(false);
        searchChoice = new JComboBox<>(); searchChoice.setBounds(245, 17, 220, 32);
        searchChoice.setVisible(false);
        JLabel yearLabel = new JLabel("개설년도"); yearLabel.setBounds(485, 17, 80, 32);
        searchYear = new JComboBox<>(); searchYear.setBounds(565, 17, 130, 32);
        JLabel semesterLabel = new JLabel("학기"); semesterLabel.setBounds(715, 17, 40, 32);
        searchSemester = new JComboBox<>(); searchSemester.setBounds(755, 17, 145, 32);
        searchButton = new JButton("검색"); searchButton.setBounds(925, 17, 170, 32);
        allButton = new JButton("전체조회"); allButton.setBounds(1110, 17, 240, 32);
        searchCategory.setFont(fieldFont);
        searchField.setFont(fieldFont);
        searchChoice.setFont(fieldFont);
        yearLabel.setFont(fieldFont);
        semesterLabel.setFont(fieldFont);
        searchYear.setFont(fieldFont);
        searchSemester.setFont(fieldFont);
        for (JButton button : new JButton[]{searchButton, allButton}) {
            styleButton(button);
            button.addActionListener(this);
        }
        for (java.awt.Component component : new java.awt.Component[]{
                searchCategory, searchField, searchChoice, yearLabel, searchYear,
                semesterLabel, searchSemester, searchButton, allButton}) {
            searchPanel.add(component);
        }
        add(searchPanel);
        searchCategory.addActionListener(event -> updateSearchInput());
    }

    private void buildTable() {
        model = new DefaultTableModel(new String[]{
                "강좌코드", "강좌명", "학과", "개설년도",
                "학기", "강의시간", "담당교수", "학점"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new TitledBorder("강좌 목록"));
        scroll.setBounds(20, 130, 1360, 335);
        table.setBackground(new Color(208, 224, 240));
        scroll.getViewport().setBackground(Color.WHITE);
        int[] widths = {130, 235, 200, 130, 135, 200, 195, 115};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
        table.getSelectionModel().addListSelectionListener(event -> {
            if (event.getValueIsAdjusting()) return;
            int viewRow = table.getSelectedRow();
            if (viewRow < 0) return;
            int row = table.convertRowIndexToModel(viewRow);
            codeField.setText(String.valueOf(model.getValueAt(row, 0)));
            nameField.setText(String.valueOf(model.getValueAt(row, 1)));
            departmentField.setSelectedItem(model.getValueAt(row, 2));
            showAssignedProfessor();
            yearField.setText(String.valueOf(model.getValueAt(row, 3)));
            semesterField.setSelectedItem(model.getValueAt(row, 4));
            ClassSchedule schedule = ClassSchedule.parseStored(String.valueOf(model.getValueAt(row, 5)));
            dayField.setSelectedItem(schedule == null ? "요일 선택" : schedule.day);
            startTimeField.setText(schedule == null ? "" : schedule.start);
            endTimeField.setText(schedule == null ? "" : schedule.end);
            creditField.setSelectedItem(String.valueOf(model.getValueAt(row, 7)));
            showEditPanel(true);
        });
        add(scroll);
    }

    private void buildButtons() {
        registerButton = new JButton("새 강좌 등록");
        registerButton.setBounds(20, 700, 320, 45);
        editButton = new JButton("수정");
        editButton.setBounds(360, 700, 320, 45);
        deleteButton = new JButton("삭제");
        deleteButton.setBounds(700, 700, 320, 45);
        closeButton = new JButton("종료");
        closeButton.setBounds(1040, 700, 320, 45);
        for (JButton button : new JButton[]{
                registerButton, editButton, deleteButton, closeButton}) {
            styleButton(button);
            button.addActionListener(this);
            add(button);
        }
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
    }

    private void styleButton(JButton button) {
        button.setFont(buttonFont);
        button.setBackground(new Color(234, 206, 202));
        button.setForeground(new Color(105, 100, 107));
    }

    private void showEditPanel(boolean visible) {
        editPanel.setVisible(visible);
        editButton.setEnabled(visible);
        deleteButton.setEnabled(visible);
        revalidate();
        repaint();
    }

    @Override public void actionPerformed(ActionEvent event) {
        Object source = event.getSource();
        if (source == searchButton) search();
        else if (source == allButton) loadAll();
        else if (source == registerButton) {
            try {
                SubjectRegistrationDialog dialog = new SubjectRegistrationDialog(this);
                dialog.setVisible(true);
            } catch (SQLException exception) {
                showError("강좌 등록 오류", exception);
            }
        } else if (source == editButton) updateSelected();
        else if (source == deleteButton) deleteSelected();
        else if (source == closeButton) confirmProgramExit();
    }

    private void loadDepartments() throws SQLException {
        departmentField.removeAllItems();
        try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(
                "SELECT department_name FROM department ORDER BY department_cd");
             ResultSet rows = statement.executeQuery()) {
            while (rows.next()) departmentField.addItem(rows.getString(1));
        }
    }

    ProfessorOption assignedProfessor(String departmentName) throws SQLException {
        try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(
                "SELECT p.professor_cd,p.user_name,p.retirement_date "
                + "FROM department d LEFT JOIN professor_info p ON p.professor_cd=d.professor_cd "
                + "WHERE d.department_name=?")) {
            statement.setString(1, departmentName);
            try (ResultSet row = statement.executeQuery()) {
                if (!row.next()) throw new SQLException("등록된 학과를 찾을 수 없습니다.");
                if (row.getString(1) == null || row.getDate(3) != null) return null;
                return new ProfessorOption(row.getString(1), row.getString(2));
            }
        }
    }

    private void showAssignedProfessor() {
        professorField.setText("");
        String departmentName = (String) departmentField.getSelectedItem();
        if (departmentName == null) return;
        try {
            ProfessorOption assigned = assignedProfessor(departmentName);
            professorField.setText(assigned == null ? "재직 중인 담당교수 없음" : assigned.toString());
        } catch (SQLException exception) {
            showError("담당교수 조회 오류", exception);
        }
    }

    void loadAll() {
        model.setRowCount(0);
        showEditPanel(false);
        try {
            searchCategory.setSelectedIndex(SEARCH_COLUMNS.length - 1);
            searchField.setText("");
            loadYearOptions();
            loadSemesterOptions();
            try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(SUBJECT_SELECT);
                 ResultSet rows = statement.executeQuery()) {
                while (rows.next()) addRow(rows);
            }
        } catch (SQLException exception) {
            showError("강좌 목록 오류", exception);
        }
    }

    private void loadYearOptions() throws SQLException {
        searchYear.removeAllItems();
        searchYear.addItem("전체 연도");
        try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(
                "SELECT DISTINCT academic_year FROM subject ORDER BY academic_year DESC");
             ResultSet rows = statement.executeQuery()) {
            while (rows.next()) searchYear.addItem(String.format("%04d", rows.getInt(1)));
        }
        searchYear.setSelectedIndex(0);
    }

    private void loadSemesterOptions() throws SQLException {
        searchSemester.removeAllItems();
        searchSemester.addItem("전체 학기");
        try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(
                "SELECT DISTINCT semester FROM subject ORDER BY semester");
             ResultSet rows = statement.executeQuery()) {
            while (rows.next()) searchSemester.addItem(semesterLabel(rows.getString(1)));
        }
        searchSemester.setSelectedIndex(0);
    }

    private void addRow(ResultSet row) throws SQLException {
        String department = lookupName("department", "department_name", "department_cd", row.getString(3));
        String professor = lookupName("professor_info", "user_name", "professor_cd", row.getString(7));
        String storedSchedule = row.getString(6);
        ClassSchedule schedule = ClassSchedule.parseStored(storedSchedule);
        model.addRow(new Object[]{row.getString(1), row.getString(2), department,
                String.format("%04d", row.getInt(4)), semesterLabel(row.getString(5)),
                schedule == null ? storedSchedule : schedule.toString(),
                professor, row.getString(8)});
    }

    private void updateSearchInput() {
        String column = SEARCH_COLUMNS[searchCategory.getSelectedIndex()];
        boolean choice = isChoiceColumn(column);
        searchField.setVisible(!choice && !column.isEmpty());
        searchChoice.setVisible(choice);
        searchField.setText("");
        searchChoice.removeAllItems();
        if (!choice) return;
        if ("subject_hour".equals(column)) {
            for (String day : new String[]{"월", "화", "수", "목", "금"}) searchChoice.addItem(day);
            searchChoice.setSelectedIndex(-1);
            return;
        }
        if ("subject_credit".equals(column)) {
            for (int credit = 1; credit <= 5; credit++) searchChoice.addItem(Integer.toString(credit));
            searchChoice.setSelectedIndex(-1);
            return;
        }
        String sql;
        switch (column) {
            case "department_cd":
                sql = "SELECT DISTINCT department_name FROM department ORDER BY department_name";
                break;
            case "professor_cd":
                sql = "SELECT DISTINCT user_name FROM professor_info ORDER BY user_name";
                break;
            default:
                return;
        }
        try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(sql);
             ResultSet rows = statement.executeQuery()) {
            while (rows.next()) {
                String value = rows.getString(1);
                searchChoice.addItem(value);
            }
        } catch (SQLException exception) {
            showError("검색 항목 오류", exception);
        }
        searchChoice.setSelectedIndex(-1);
    }

    private boolean isChoiceColumn(String column) {
        return "department_cd".equals(column) || "professor_cd".equals(column)
                || "subject_hour".equals(column) || "subject_credit".equals(column);
    }

    private void search() {
        String column = SEARCH_COLUMNS[searchCategory.getSelectedIndex()];
        List<String> conditions = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        if (!column.isEmpty()) {
            boolean choice = isChoiceColumn(column);
            String keyword = choice ? (String) searchChoice.getSelectedItem() : searchField.getText().trim();
            if (keyword == null || keyword.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        choice ? "검색할 항목을 선택해주세요." : "검색어를 입력해주세요.");
                return;
            }
            if ("department_cd".equals(column)) {
                conditions.add("department_cd IN (SELECT department_cd FROM department WHERE department_name=?)");
                values.add(keyword);
            } else if ("professor_cd".equals(column)) {
                conditions.add("professor_cd IN (SELECT professor_cd FROM professor_info WHERE user_name=?)");
                values.add(keyword);
            } else if ("subject_hour".equals(column)) {
                conditions.add("TRIM(subject_hour) LIKE ?");
                values.add(keyword + " %");
            } else {
                conditions.add(column + (choice ? "=?" : " LIKE ?"));
                values.add(choice ? keyword : "%" + keyword + "%");
            }
        }
        if (searchYear.getSelectedIndex() > 0) {
            conditions.add("academic_year=?");
            values.add(Integer.parseInt((String) searchYear.getSelectedItem()));
        }
        if (searchSemester.getSelectedIndex() > 0) {
            conditions.add("semester=?");
            values.add(semesterCode((String) searchSemester.getSelectedItem()));
        }
        if (conditions.isEmpty()) {
            JOptionPane.showMessageDialog(this, "검색 조건을 선택해주세요.");
            return;
        }
        model.setRowCount(0);
        showEditPanel(false);
        try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(
                SUBJECT_SELECT + " WHERE " + String.join(" AND ", conditions))) {
            for (int i = 0; i < values.size(); i++) {
                Object value = values.get(i);
                if (value instanceof Integer) statement.setInt(i + 1, (Integer) value);
                else statement.setString(i + 1, (String) value);
            }
            try (ResultSet rows = statement.executeQuery()) {
                while (rows.next()) addRow(rows);
            }
            if (model.getRowCount() == 0) JOptionPane.showMessageDialog(this, "검색 결과가 없습니다.");
        } catch (SQLException exception) {
            showError("강좌 검색 오류", exception);
        }
    }

    String nextSubjectCode() throws SQLException {
        int highest = 0;
        try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(
                "SELECT subject_cd FROM subject");
             ResultSet rows = statement.executeQuery()) {
            while (rows.next()) {
                String code = rows.getString(1);
                if (code != null && code.matches("(?i)c[0-9]+")) {
                    highest = Math.max(highest, Integer.parseInt(code.substring(1)));
                }
            }
        }
        if (highest >= 999) throw new SQLException("강좌코드를 더 생성할 수 없습니다.");
        return String.format("c%03d", highest + 1);
    }

    boolean registerSubject(String code, String name, String departmentName, String year,
            String semester, String hours, String credit) {
        try {
            String departmentCode = lookupCode("department", "department_cd", "department_name", departmentName);
            ProfessorOption assigned = assignedProfessor(departmentName);
            if (assigned == null) throw new SQLException(
                    "학과 관리에서 재직 중인 담당교수를 지정한 뒤 강좌를 등록하세요.");
            String professorCode = assigned.code();
            validateSubject(name, departmentCode, year, semester, hours, professorCode, credit);
            try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(
                    "INSERT INTO subject(subject_cd,subject_name,academic_year,semester,"
                    + "department_cd,professor_cd,subject_hour,subject_credit) VALUES(?,?,?,?,?,?,?,?)")) {
                statement.setString(1, code);
                statement.setString(2, name.trim());
                statement.setString(3, year.trim());
                statement.setString(4, semesterCode(semester));
                statement.setString(5, departmentCode);
                statement.setString(6, professorCode);
                statement.setString(7, ClassSchedule.parseStored(hours).toString());
                statement.setString(8, credit.trim());
                statement.executeUpdate();
            }
            loadAll();
            return true;
        } catch (SQLException exception) {
            String message = exception.getErrorCode() == 1062
                    ? "이미 등록된 강좌코드입니다." : exception.getMessage();
            JOptionPane.showMessageDialog(getDialogOwnerFrame(), message,
                    "강좌 등록 오류", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void updateSelected() {
        if (table.getSelectedRow() < 0) return;
        String code = codeField.getText();
        if (hasEnrollmentHistory(code)) {
            JOptionPane.showMessageDialog(this,
                    "수강 신청 이력이 있는 강좌는 수정할 수 없습니다. 새 강좌 코드로 개설하세요.",
                    "수정 제한", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            String departmentCode = lookupCode("department", "department_cd", "department_name",
                    (String) departmentField.getSelectedItem());
            ProfessorOption assigned = assignedProfessor((String) departmentField.getSelectedItem());
            if (assigned == null) throw new SQLException(
                    "학과 관리에서 재직 중인 담당교수를 지정한 뒤 강좌를 수정하세요.");
            String professorCode = assigned.code();
            String schedule = ClassSchedule.create((String) dayField.getSelectedItem(),
                    startTimeField.getText(), endTimeField.getText()).toString();
            validateSubject(nameField.getText(), departmentCode, yearField.getText(),
                    (String) semesterField.getSelectedItem(), schedule, professorCode,
                    (String) creditField.getSelectedItem());
            try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(
                    "UPDATE subject SET subject_name=?,academic_year=?,semester=?,department_cd=?,"
                    + "professor_cd=?,subject_hour=?,subject_credit=? WHERE subject_cd=?")) {
                statement.setString(1, nameField.getText().trim());
                statement.setString(2, yearField.getText().trim());
                statement.setString(3, semesterCode((String) semesterField.getSelectedItem()));
                statement.setString(4, departmentCode);
                statement.setString(5, professorCode);
                statement.setString(6, schedule);
                statement.setString(7, (String) creditField.getSelectedItem());
                statement.setString(8, code);
                if (statement.executeUpdate() == 1) {
                    loadAll();
                    JOptionPane.showMessageDialog(this, "수정이 완료 되었습니다.");
                }
            }
        } catch (SQLException exception) {
            showError("강좌 수정 오류", exception);
        } catch (IllegalArgumentException exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(),
                    "강의시간 입력 오류", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void deleteSelected() {
        if (table.getSelectedRow() < 0) return;
        String code = codeField.getText();
        if (hasEnrollmentHistory(code)) {
            JOptionPane.showMessageDialog(this,
                    "수강 신청 이력이 있는 강좌는 삭제할 수 없습니다. 성적과 수강 이력을 보존하세요.",
                    "삭제 제한", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int answer = JOptionPane.showOptionDialog(this, "선택한 강좌를 삭제하시겠습니까?",
                "강좌 삭제", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE,
                null, new Object[]{"예", "아니오"}, "아니오");
        if (answer != JOptionPane.YES_OPTION) return;
        try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(
                "DELETE FROM subject WHERE subject_cd=?")) {
            statement.setString(1, code);
            if (statement.executeUpdate() == 1) {
                loadAll();
                JOptionPane.showMessageDialog(this, "삭제되었습니다.");
            }
        } catch (SQLException exception) {
            showError("강좌 삭제 오류", exception);
        }
    }

    private boolean hasEnrollmentHistory(String code) {
        String sql = "SELECT 1 FROM registration_list WHERE subject_cd=? "
                + "UNION ALL SELECT 1 FROM student_grades WHERE subject_cd=? LIMIT 1";
        try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(sql)) {
            statement.setString(1, code);
            statement.setString(2, code);
            try (ResultSet rows = statement.executeQuery()) { return rows.next(); }
        } catch (SQLException exception) {
            showError("강좌 이력 오류", exception);
            return true;
        }
    }

    private void validateSubject(String name, String departmentCode, String year,
            String semester, String hours, String professorCode, String credit) throws SQLException {
        if (name == null || name.isBlank()) throw new SQLException("강좌명을 입력하세요.");
        if (departmentCode == null) throw new SQLException("학과를 선택하세요.");
        if (professorCode == null) throw new SQLException("학과의 담당교수를 지정하세요.");
        if (year == null || !year.trim().matches("\\d{4}")) {
            throw new SQLException("개설년도는 숫자 네 자리(yyyy)로 입력하세요.");
        }
        if (semesterCode(semester) == null) throw new SQLException("학기를 선택하세요.");
        if (ClassSchedule.parseStored(hours) == null) {
            throw new SQLException("요일과 시작·종료시간을 확인하세요. 예: 월 09:00~12:00");
        }
        if (credit == null || !credit.trim().matches("[1-5]")) {
            throw new SQLException("학점은 1~5학점 중에서 선택하세요.");
        }
    }

    private String lookupCode(String tableName, String codeColumn, String nameColumn,
            String value) throws SQLException {
        if (value == null) return null;
        try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(
                "SELECT " + codeColumn + " FROM " + tableName + " WHERE " + nameColumn + "=?")) {
            statement.setString(1, value);
            try (ResultSet row = statement.executeQuery()) {
                return row.next() ? row.getString(1) : null;
            }
        }
    }

    private String lookupName(String tableName, String nameColumn, String codeColumn,
            String value) throws SQLException {
        if (value == null) return null;
        try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(
                "SELECT " + nameColumn + " FROM " + tableName + " WHERE " + codeColumn + "=?")) {
            statement.setString(1, value);
            try (ResultSet row = statement.executeQuery()) {
                return row.next() ? row.getString(1) : value;
            }
        }
    }

    private String semesterCode(String label) {
        if (label == null) return null;
        if (label.startsWith("1")) return "1";
        if (label.startsWith("2")) return "2";
        if ("여름학기".equals(label)) return "3";
        if ("겨울학기".equals(label)) return "4";
        return null;
    }

    private String semesterLabel(String code) {
        if ("1".equals(code)) return "1학기";
        if ("2".equals(code)) return "2학기";
        if ("3".equals(code)) return "여름학기";
        if ("4".equals(code)) return "겨울학기";
        return code;
    }

    private void showError(String title, SQLException exception) {
        JOptionPane.showMessageDialog(this, exception.getMessage(), title, JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) throws SQLException {
		Login.main(args);
    }
}
