package 학사관리프로젝트;

import java.awt.Color;
import java.awt.Font;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.TitledBorder;

public class ProfessorGradeManagement extends UniversityFrame {
    private final String professorCode;
    private final Font fieldFont = UiFonts.font(Font.PLAIN, 13);
    private final Font buttonFont = UiFonts.font(Font.PLAIN, 15);
    private final JTextField studentCodeField = new JTextField();
    private final JTextField studentNameField = new JTextField();
    private final JTextField attendanceField = new JTextField();
    private final JTextField assignmentField = new JTextField();
    private final JTextField midtermField = new JTextField();
    private final JTextField finalField = new JTextField();
    private final JTextField totalField = new JTextField();
    private final JTextField averageField = new JTextField();
    private final JTextField gradeField = new JTextField();
    private final JTextField pointsField = new JTextField();
    private final JComboBox<String> yearFilter = new JComboBox<>();
    private final JComboBox<String> semesterFilter = new JComboBox<>();
    private final JComboBox<CourseChoice> courseFilter = new JComboBox<>();
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"학번", "이름", "출석", "과제", "중간", "기말",
                    "총점", "평균점수", "등급", "평점"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private JTable table;
    private JPanel inputPanel;
    private JButton saveButton;
    private boolean refreshingFilters;
    private boolean fillingFields;

    public ProfessorGradeManagement(String professorCode) throws SQLException {
        this.professorCode = professorCode;
        setTitle("성적관리");
        setLayout(null);
        setSize(1400, 800);
        setLocationRelativeTo(null);
        DatabaseConnection.getConnection();
        buildTitle();
        buildSearch();
        buildTable();
        buildInput();
        buildButtons();
        refreshFilters();
        setVisible(true);
    }

    private void buildTitle() {
        JPanel header = new JPanel(null);
        header.setBounds(0, 0, 1400, 50);
        header.setBackground(new Color(123, 139, 155));
        JLabel title = new JLabel("성 적 관 리", JLabel.CENTER);
        title.setBounds(0, 10, 1400, 30);
        title.setForeground(Color.WHITE);
        title.setFont(UiFonts.font(Font.BOLD, 24));
        header.add(title);
        add(header);
    }

    private void buildSearch() {
        JPanel search = new JPanel(null);
        search.setBounds(0, 55, 1400, 65);
        search.setOpaque(false);
        addField(search, "개설년도", yearFilter, 35, 110, 17, 160);
        addField(search, "학기", semesterFilter, 290, 335, 17, 175);
        addField(search, "강좌", courseFilter, 535, 585, 17, 480);
        JButton lookup = new JButton("조회");
        lookup.setBounds(1100, 17, 240, 32);
        styleButton(lookup);
        lookup.addActionListener(event -> loadRoster(true));
        search.add(lookup);
        add(search);

        yearFilter.addActionListener(event -> {
            if (refreshingFilters) return;
            try {
                refreshingFilters = true;
                loadSemesterOptions();
                loadCourseOptions();
                clearRoster();
            } catch (SQLException exception) {
                showError("강좌 조회 오류", exception);
            } finally {
                refreshingFilters = false;
            }
        });
        semesterFilter.addActionListener(event -> {
            if (refreshingFilters) return;
            try {
                refreshingFilters = true;
                loadCourseOptions();
                clearRoster();
            } catch (SQLException exception) {
                showError("강좌 조회 오류", exception);
            } finally {
                refreshingFilters = false;
            }
        });
        courseFilter.addActionListener(event -> {
            if (!refreshingFilters) clearRoster();
        });
    }

    private void buildTable() {
        table = new JTable(model);
        table.setBackground(new Color(208, 224, 240));
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new TitledBorder("수강 학생 목록"));
        scroll.setBounds(20, 130, 1360, 335);
        scroll.getViewport().setBackground(Color.WHITE);
        int[] widths = {110, 130, 95, 95, 95, 95, 110, 125, 85, 85};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
        table.getSelectionModel().addListSelectionListener(event -> {
            if (event.getValueIsAdjusting()) return;
            int viewRow = table.getSelectedRow();
            if (viewRow < 0) {
                showInput(false);
                return;
            }
            int row = table.convertRowIndexToModel(viewRow);
            fillingFields = true;
            studentCodeField.setText(valueAt(row, 0));
            studentNameField.setText(valueAt(row, 1));
            attendanceField.setText(valueAt(row, 2));
            assignmentField.setText(valueAt(row, 3));
            midtermField.setText(valueAt(row, 4));
            finalField.setText(valueAt(row, 5));
            fillingFields = false;
            updatePreview();
            showInput(true);
        });
        add(scroll);
    }

    private String valueAt(int row, int column) {
        Object value = model.getValueAt(row, column);
        return value == null ? "" : value.toString();
    }

    private void buildInput() {
        inputPanel = new JPanel(null);
        inputPanel.setBounds(20, 475, 1360, 215);
        inputPanel.setBackground(new Color(245, 245, 245));
        inputPanel.setBorder(new TitledBorder("성적 입력"));

        addField(inputPanel, "학번", studentCodeField, 30, 125, 25, 180);
        addField(inputPanel, "학생이름", studentNameField, 340, 435, 25, 250);

        addField(inputPanel, "출석", attendanceField, 30, 125, 75, 150);
        addField(inputPanel, "과제", assignmentField, 315, 410, 75, 150);
        addField(inputPanel, "중간", midtermField, 600, 695, 75, 150);
        addField(inputPanel, "기말", finalField, 885, 980, 75, 150);

        addField(inputPanel, "총점", totalField, 30, 125, 135, 150);
        addField(inputPanel, "평균점수", averageField, 315, 410, 135, 150);
        addField(inputPanel, "등급", gradeField, 600, 695, 135, 150);
        addField(inputPanel, "평점", pointsField, 885, 980, 135, 150);

        for (JTextField field : new JTextField[]{studentCodeField, studentNameField,
                totalField, averageField, gradeField, pointsField}) {
            field.setEditable(false);
        }
        DocumentListener previewListener = new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent event) { updatePreview(); }
            @Override public void removeUpdate(DocumentEvent event) { updatePreview(); }
            @Override public void changedUpdate(DocumentEvent event) { updatePreview(); }
        };
        for (JTextField field : scoreFields()) field.getDocument().addDocumentListener(previewListener);
        add(inputPanel);
        inputPanel.setVisible(false);
    }

    private void addField(JPanel panel, String labelText, java.awt.Component field,
            int labelX, int fieldX, int y, int width) {
        JLabel label = new JLabel(labelText);
        label.setBounds(labelX, y, fieldX - labelX - 5, 27);
        label.setFont(fieldFont);
        field.setBounds(fieldX, y, width, 27);
        field.setFont(fieldFont);
        panel.add(label);
        panel.add(field);
    }

    private JTextField[] scoreFields() {
        return new JTextField[]{attendanceField, assignmentField, midtermField, finalField};
    }

    private void buildButtons() {
        saveButton = new JButton("저장");
        saveButton.setBounds(20, 700, 660, 45);
        JButton exitButton = new JButton("종료");
        exitButton.setBounds(700, 700, 660, 45);
        for (JButton button : new JButton[]{saveButton, exitButton}) {
            styleButton(button);
            add(button);
        }
        saveButton.setEnabled(false);
        saveButton.addActionListener(event -> saveSelected());
        exitButton.addActionListener(event -> confirmProgramExit());
    }

    private void styleButton(JButton button) {
        button.setFont(buttonFont);
        button.setBackground(new Color(234, 206, 202));
        button.setForeground(new Color(105, 100, 107));
    }

    private void showInput(boolean visible) {
        inputPanel.setVisible(visible);
        saveButton.setEnabled(visible);
        revalidate();
        repaint();
    }

    private void refreshFilters() throws SQLException {
        refreshingFilters = true;
        try {
            yearFilter.removeAllItems();
            yearFilter.addItem("전체 연도");
            try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(
                    "SELECT DISTINCT academic_year FROM subject WHERE professor_cd=? ORDER BY academic_year DESC")) {
                statement.setString(1, professorCode);
                try (ResultSet rows = statement.executeQuery()) {
                    while (rows.next()) yearFilter.addItem(String.format("%04d", rows.getInt(1)));
                }
            }
            loadSemesterOptions();
            loadCourseOptions();
        } finally {
            refreshingFilters = false;
        }
    }

    private void loadSemesterOptions() throws SQLException {
        semesterFilter.removeAllItems();
        semesterFilter.addItem("전체 학기");
        boolean hasYear = yearFilter.getSelectedIndex() > 0;
        String sql = "SELECT DISTINCT semester FROM subject WHERE professor_cd=?"
                + (hasYear ? " AND academic_year=?" : "") + " ORDER BY semester";
        try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(sql)) {
            statement.setString(1, professorCode);
            if (hasYear) statement.setInt(2, Integer.parseInt((String) yearFilter.getSelectedItem()));
            try (ResultSet rows = statement.executeQuery()) {
                while (rows.next()) semesterFilter.addItem(semesterLabel(rows.getString(1)));
            }
        }
    }

    private void loadCourseOptions() throws SQLException {
        courseFilter.removeAllItems();
        courseFilter.addItem(new CourseChoice(null, "강좌 선택", 0, null));
        boolean hasYear = yearFilter.getSelectedIndex() > 0;
        boolean hasSemester = semesterFilter.getSelectedIndex() > 0;
        String sql = "SELECT subject_cd,subject_name,academic_year,semester FROM subject WHERE professor_cd=?"
                + (hasYear ? " AND academic_year=?" : "")
                + (hasSemester ? " AND semester=?" : "")
                + " ORDER BY academic_year DESC,semester,subject_cd";
        try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(sql)) {
            int parameter = 1;
            statement.setString(parameter++, professorCode);
            if (hasYear) statement.setInt(parameter++, Integer.parseInt((String) yearFilter.getSelectedItem()));
            if (hasSemester) statement.setString(parameter, semesterCode((String) semesterFilter.getSelectedItem()));
            try (ResultSet rows = statement.executeQuery()) {
                while (rows.next()) courseFilter.addItem(new CourseChoice(
                        rows.getString(1), rows.getString(2), rows.getInt(3), rows.getString(4)));
            }
        }
    }

    private static String semesterLabel(String code) {
        if ("1".equals(code)) return "1학기";
        if ("2".equals(code)) return "2학기";
        if ("3".equals(code)) return "여름학기";
        if ("4".equals(code)) return "겨울학기";
        return code;
    }

    private static String semesterCode(String label) {
        if ("1학기".equals(label)) return "1";
        if ("2학기".equals(label)) return "2";
        if ("여름학기".equals(label)) return "3";
        if ("겨울학기".equals(label)) return "4";
        return null;
    }

    private void clearRoster() {
        model.setRowCount(0);
        showInput(false);
    }

    private void loadRoster(boolean showMissingSelection) {
        CourseChoice course = (CourseChoice) courseFilter.getSelectedItem();
        clearRoster();
        if (course == null || course.code == null) {
            if (showMissingSelection) JOptionPane.showMessageDialog(this, "강좌를 선택하세요.");
            return;
        }
        String sql = "SELECT r.student_cd,si.user_name,g.attendance_score,g.assignment_score,"
                + "g.midterm_score,g.final_score,g.credit "
                + "FROM registration_list r JOIN student_info si ON si.student_cd=r.student_cd "
                + "LEFT JOIN student_grades g ON g.student_cd=r.student_cd "
                + "AND g.subject_cd=r.subject_cd AND g.academic_year=r.year "
                + "AND g.semester=r.semester AND g.professor_cd=r.professor_cd "
                + "WHERE r.subject_cd=? AND r.professor_cd=? AND r.year=? AND r.semester=? "
                + "ORDER BY r.student_cd";
        try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(sql)) {
            statement.setString(1, course.code);
            statement.setString(2, professorCode);
            statement.setInt(3, course.year);
            statement.setString(4, course.semester);
            try (ResultSet rows = statement.executeQuery()) {
                while (rows.next()) {
                    boolean graded = rows.getBigDecimal(7) != null;
                    Object[] data = new Object[]{rows.getString(1), rows.getString(2),
                            "", "", "", "", "", "", "", ""};
                    if (graded) {
                        int attendance = rows.getInt(3);
                        int assignment = rows.getInt(4);
                        int midterm = rows.getInt(5);
                        int finalExam = rows.getInt(6);
                        BigDecimal average = GradeCalculator.average(attendance, assignment, midterm, finalExam);
                        data[2] = attendance;
                        data[3] = assignment;
                        data[4] = midterm;
                        data[5] = finalExam;
                        data[6] = GradeCalculator.total(attendance, assignment, midterm, finalExam);
                        data[7] = average.toPlainString();
                        data[8] = GradeCalculator.letter(average);
                        data[9] = GradeCalculator.points(average).toPlainString();
                    }
                    model.addRow(data);
                }
            }
        } catch (SQLException | IllegalArgumentException exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(),
                    "성적 조회 오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int parseScore(JTextField field, String label) {
        String text = field.getText().trim();
        if (!text.matches("\\d{1,3}")) {
            throw new IllegalArgumentException(label + " 점수는 0~100의 숫자로 입력하세요.");
        }
        int value = Integer.parseInt(text);
        if (value > 100) throw new IllegalArgumentException(label + " 점수는 0~100으로 입력하세요.");
        return value;
    }

    private void updatePreview() {
        if (fillingFields) return;
        try {
            int attendance = parseScore(attendanceField, "출석");
            int assignment = parseScore(assignmentField, "과제");
            int midterm = parseScore(midtermField, "중간");
            int finalExam = parseScore(finalField, "기말");
            BigDecimal average = GradeCalculator.average(attendance, assignment, midterm, finalExam);
            totalField.setText(Integer.toString(GradeCalculator.total(attendance, assignment, midterm, finalExam)));
            averageField.setText(average.toPlainString());
            gradeField.setText(GradeCalculator.letter(average));
            pointsField.setText(GradeCalculator.points(average).toPlainString());
        } catch (IllegalArgumentException exception) {
            totalField.setText("");
            averageField.setText("");
            gradeField.setText("");
            pointsField.setText("");
        }
    }

    private void saveSelected() {
        CourseChoice course = (CourseChoice) courseFilter.getSelectedItem();
        if (course == null || course.code == null || studentCodeField.getText().isEmpty()) return;
        Connection connection;
        try { connection = DatabaseConnection.getConnection(); }
        catch (SQLException exception) { showError("DB 연결 오류", exception); return; }
        boolean oldAutoCommit = true;
        boolean transactionStarted = false;
        try {
            int attendance = parseScore(attendanceField, "출석");
            int assignment = parseScore(assignmentField, "과제");
            int midterm = parseScore(midtermField, "중간");
            int finalExam = parseScore(finalField, "기말");
            BigDecimal average = GradeCalculator.average(attendance, assignment, midterm, finalExam);
            oldAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            transactionStarted = true;
            String departmentCode;
            try (PreparedStatement check = connection.prepareStatement(
                    "SELECT s.department_cd FROM registration_list r "
                    + "JOIN subject s ON s.subject_cd=r.subject_cd "
                    + "WHERE r.student_cd=? AND r.subject_cd=? AND r.professor_cd=? "
                    + "AND r.year=? AND r.semester=? FOR UPDATE")) {
                check.setString(1, studentCodeField.getText());
                check.setString(2, course.code);
                check.setString(3, professorCode);
                check.setInt(4, course.year);
                check.setString(5, course.semester);
                try (ResultSet row = check.executeQuery()) {
                    if (!row.next()) throw new SQLException("이 강좌의 수강신청 학생이 아닙니다.");
                    departmentCode = row.getString(1);
                }
            }
            boolean gradeExists;
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT 1 FROM student_grades WHERE student_cd=? AND subject_cd=? "
                    + "AND academic_year=? AND semester=? FOR UPDATE")) {
                statement.setString(1, studentCodeField.getText());
                statement.setString(2, course.code);
                statement.setInt(3, course.year);
                statement.setString(4, course.semester);
                try (ResultSet row = statement.executeQuery()) {
                    gradeExists = row.next();
                }
            }
            if (gradeExists) {
                try (PreparedStatement statement = connection.prepareStatement(
                        "UPDATE student_grades SET attendance_score=?,assignment_score=?,"
                        + "midterm_score=?,final_score=?,grade=?,credit=?,"
                        + "professor_cd=?,department_cd=? "
                        + "WHERE student_cd=? AND subject_cd=? "
                        + "AND academic_year=? AND semester=?")) {
                    statement.setInt(1, attendance);
                    statement.setInt(2, assignment);
                    statement.setInt(3, midterm);
                    statement.setInt(4, finalExam);
                    statement.setString(5, GradeCalculator.letter(average));
                    statement.setBigDecimal(6, GradeCalculator.points(average));
                    statement.setString(7, professorCode);
                    statement.setString(8, departmentCode);
                    statement.setString(9, studentCodeField.getText());
                    statement.setString(10, course.code);
                    statement.setInt(11, course.year);
                    statement.setString(12, course.semester);
                    statement.executeUpdate();
                }
            } else {
                try (PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO student_grades(student_cd,department_cd,subject_cd,professor_cd,"
                        + "academic_year,semester,midterm_score,final_score,assignment_score,"
                        + "attendance_score,grade,credit) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)")) {
                    statement.setString(1, studentCodeField.getText());
                    statement.setString(2, departmentCode);
                    statement.setString(3, course.code);
                    statement.setString(4, professorCode);
                    statement.setInt(5, course.year);
                    statement.setString(6, course.semester);
                    statement.setInt(7, midterm);
                    statement.setInt(8, finalExam);
                    statement.setInt(9, assignment);
                    statement.setInt(10, attendance);
                    statement.setString(11, GradeCalculator.letter(average));
                    statement.setBigDecimal(12, GradeCalculator.points(average));
                    statement.executeUpdate();
                }
            }
            connection.commit();
            connection.setAutoCommit(oldAutoCommit);
            transactionStarted = false;
            String selectedStudent = studentCodeField.getText();
            loadRoster(false);
            for (int row = 0; row < model.getRowCount(); row++) {
                if (selectedStudent.equals(model.getValueAt(row, 0))) {
                    table.setRowSelectionInterval(row, row);
                    break;
                }
            }
            JOptionPane.showMessageDialog(this, "성적이 저장되었습니다.");
        } catch (SQLException | IllegalArgumentException exception) {
            if (connection != null && transactionStarted) {
                try { connection.rollback(); } catch (SQLException rollbackError) {
                    exception.addSuppressed(rollbackError);
                }
            }
            JOptionPane.showMessageDialog(this, exception.getMessage(),
                    "성적 저장 오류", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (connection != null && transactionStarted) {
                try { connection.setAutoCommit(oldAutoCommit); } catch (SQLException exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    private void showError(String title, SQLException exception) {
        JOptionPane.showMessageDialog(this, exception.getMessage(), title, JOptionPane.ERROR_MESSAGE);
    }

    private static final class CourseChoice {
        final String code;
        final String name;
        final int year;
        final String semester;

        CourseChoice(String code, String name, int year, String semester) {
            this.code = code;
            this.name = name;
            this.year = year;
            this.semester = semester;
        }

        @Override public String toString() {
            return code == null ? name : name + " · " + year + " " + semesterLabel(semester);
        }
    }

    public static void main(String[] args) throws SQLException {
		Login.main(args);
    }
}
