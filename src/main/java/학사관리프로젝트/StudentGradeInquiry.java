package 학사관리프로젝트;

import java.awt.Color;
import java.awt.Font;
import java.math.BigDecimal;
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
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

/** Read-only grades for the student who is logged in. */
public class StudentGradeInquiry extends UniversityFrame {
    private final String studentCode;
    private final JComboBox<String> yearFilter = new JComboBox<>();
    private final JComboBox<String> semesterFilter = new JComboBox<>();
    private final JComboBox<CourseChoice> courseFilter = new JComboBox<>();
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"강좌명", "개설년도", "학기", "출석", "과제", "중간", "기말",
                    "총점", "평균점수", "등급", "평점"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };

    public StudentGradeInquiry(String studentCode) throws SQLException {
        this.studentCode = studentCode;
        setTitle("성적 조회");
        setLayout(null);
        setSize(1400, 800);
        setLocationRelativeTo(null);
        DatabaseConnection.getConnection();
        buildHeader();
        buildSearch();
        buildTable();
        loadFilterOptions();
        loadGrades();
        setVisible(true);
    }

    private void buildHeader() {
        JPanel header = new JPanel(null);
        header.setBounds(0, 0, 1400, 50);
        header.setBackground(new Color(123, 139, 155));
        JLabel title = new JLabel("성 적 조 회", JLabel.CENTER);
        title.setBounds(0, 10, 1400, 30);
        title.setFont(UiFonts.font(Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        header.add(title);
        add(header);
    }

    private void buildSearch() {
        JPanel search = new JPanel(null);
        search.setBounds(0, 55, 1400, 65);
        search.setOpaque(false);
        addFilter(search, "개설년도", yearFilter, 35, 110, 160);
        addFilter(search, "학기", semesterFilter, 290, 335, 175);
        addFilter(search, "강좌", courseFilter, 535, 585, 480);
        JButton lookup = new JButton("조회");
        lookup.setBounds(1100, 17, 240, 32);
        styleButton(lookup);
        lookup.addActionListener(event -> loadGrades());
        search.add(lookup);
        add(search);
    }

    private void addFilter(JPanel panel, String labelText, JComboBox<?> filter,
            int labelX, int fieldX, int width) {
        JLabel label = new JLabel(labelText);
        label.setBounds(labelX, 17, fieldX - labelX - 5, 32);
        label.setFont(UiFonts.font(Font.PLAIN, 13));
        filter.setBounds(fieldX, 17, width, 32);
        filter.setFont(UiFonts.font(Font.PLAIN, 13));
        panel.add(label);
        panel.add(filter);
    }

    private void buildTable() {
        JTable table = new JTable(model);
        table.setFont(UiFonts.font(Font.PLAIN, 13));
        table.setRowHeight(25);
        table.setBackground(new Color(208, 224, 240));
        int[] widths = {180, 110, 110, 90, 90, 90, 90, 100, 120, 90, 90};
        for (int index = 0; index < widths.length; index++) {
            table.getColumnModel().getColumn(index).setPreferredWidth(widths[index]);
        }
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new TitledBorder("성적 목록"));
        scroll.setBounds(20, 130, 1360, 615);
        scroll.getViewport().setBackground(Color.WHITE);
        add(scroll);
    }

    private void styleButton(JButton button) {
        button.setFont(UiFonts.font(Font.PLAIN, 15));
        button.setBackground(new Color(234, 206, 202));
        button.setForeground(new Color(105, 100, 107));
    }

    private void loadFilterOptions() throws SQLException {
        yearFilter.addItem("전체 연도");
        semesterFilter.addItem("전체 학기");
        courseFilter.addItem(new CourseChoice(null, "전체 강좌"));
        try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(
                "SELECT DISTINCT year FROM registration_list WHERE student_cd=? ORDER BY year DESC")) {
            statement.setString(1, studentCode);
            try (ResultSet rows = statement.executeQuery()) {
                while (rows.next()) yearFilter.addItem(String.format("%04d", rows.getInt(1)));
            }
        }
        try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(
                "SELECT DISTINCT semester FROM registration_list WHERE student_cd=? ORDER BY semester")) {
            statement.setString(1, studentCode);
            try (ResultSet rows = statement.executeQuery()) {
                while (rows.next()) semesterFilter.addItem(semesterLabel(rows.getString(1)));
            }
        }
        try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(
                "SELECT DISTINCT r.subject_cd,s.subject_name FROM registration_list r "
                + "JOIN subject s ON s.subject_cd=r.subject_cd WHERE r.student_cd=? ORDER BY s.subject_name")) {
            statement.setString(1, studentCode);
            try (ResultSet rows = statement.executeQuery()) {
                while (rows.next()) courseFilter.addItem(new CourseChoice(rows.getString(1), rows.getString(2)));
            }
        }
    }

    private void loadGrades() {
        StringBuilder sql = new StringBuilder(
                "SELECT s.subject_name,r.year,r.semester,g.attendance_score,g.assignment_score,"
                + "g.midterm_score,g.final_score,g.credit FROM registration_list r "
                + "JOIN subject s ON s.subject_cd=r.subject_cd "
                + "LEFT JOIN student_grades g ON g.student_cd=r.student_cd "
                + "AND g.subject_cd=r.subject_cd AND g.academic_year=r.year "
                + "AND g.semester=r.semester AND g.professor_cd=r.professor_cd "
                + "WHERE r.student_cd=?");
        boolean hasYear = yearFilter.getSelectedIndex() > 0;
        boolean hasSemester = semesterFilter.getSelectedIndex() > 0;
        CourseChoice course = (CourseChoice) courseFilter.getSelectedItem();
        if (hasYear) sql.append(" AND r.year=?");
        if (hasSemester) sql.append(" AND r.semester=?");
        if (course != null && course.code != null) sql.append(" AND r.subject_cd=?");
        sql.append(" ORDER BY r.year DESC,r.semester,r.subject_cd");
        model.setRowCount(0);
        try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(sql.toString())) {
            int parameter = 1;
            statement.setString(parameter++, studentCode);
            if (hasYear) statement.setInt(parameter++, Integer.parseInt((String) yearFilter.getSelectedItem()));
            if (hasSemester) statement.setString(parameter++, semesterCode((String) semesterFilter.getSelectedItem()));
            if (course != null && course.code != null) statement.setString(parameter, course.code);
            try (ResultSet rows = statement.executeQuery()) {
                while (rows.next()) addGradeRow(rows);
            }
        } catch (SQLException | IllegalArgumentException exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(),
                    "성적 조회 오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addGradeRow(ResultSet row) throws SQLException {
        boolean graded = row.getBigDecimal(8) != null;
        Object[] values = {row.getString(1), String.format("%04d", row.getInt(2)),
                semesterLabel(row.getString(3)), "", "", "", "", "", "", "", ""};
        if (graded) {
            int attendance = row.getInt(4);
            int assignment = row.getInt(5);
            int midterm = row.getInt(6);
            int finalExam = row.getInt(7);
            BigDecimal average = GradeCalculator.average(attendance, assignment, midterm, finalExam);
            values[3] = attendance;
            values[4] = assignment;
            values[5] = midterm;
            values[6] = finalExam;
            values[7] = GradeCalculator.total(attendance, assignment, midterm, finalExam);
            values[8] = average.toPlainString();
            values[9] = GradeCalculator.letter(average);
            values[10] = GradeCalculator.points(average).toPlainString();
        }
        model.addRow(values);
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

    private static final class CourseChoice {
        final String code;
        final String name;

        CourseChoice(String code, String name) {
            this.code = code;
            this.name = name;
        }

        @Override public String toString() { return name; }
    }

    /** Keeps an older IntelliJ run configuration available for direct testing. */
    public static void main(String[] args) throws SQLException {
		Login.main(args);
    }
}
