package 학사관리프로젝트;

import java.awt.BorderLayout;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/** Student averages/ranks within departments and department average summary. */
public final class AcademicStatistics extends UniversityFrame {
    public AcademicStatistics() throws SQLException {
        super("학생·학과 성적 통계");
        setSize(1520, 520);
        setLocationRelativeTo(null);
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("학생별 평균·학과 등수", new JScrollPane(studentTable()));
        tabs.addTab("학과별 평균", new JScrollPane(departmentTable()));
        add(tabs, BorderLayout.CENTER);
        setVisible(true);
    }

    private JTable studentTable() throws SQLException {
        DefaultTableModel model = new DefaultTableModel(new Object[]{"학번", "이름", "학과", "평균", "학과 등수"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        String sql = "SELECT si.student_cd, si.user_name, d.department_name, " +
                "AVG((g.attendance_score+g.assignment_score+g.midterm_score+g.final_score)/4.0) AS average_score " +
                "FROM student_grades g JOIN student_info si ON si.student_cd=g.student_cd " +
                "JOIN department d ON d.department_cd=si.department_cd " +
                "WHERE g.credit IS NOT NULL " +
                "GROUP BY si.student_cd, si.user_name, d.department_cd, d.department_name";
        List<StudentAverage> students = new ArrayList<>();
        try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(sql);
             ResultSet rows = statement.executeQuery()) {
            while (rows.next()) students.add(new StudentAverage(rows.getString(1), rows.getString(2), rows.getString(3), rows.getBigDecimal(4)));
        }
        students.sort(Comparator.comparing(StudentAverage::department).thenComparing(StudentAverage::average, Comparator.reverseOrder()));
        String previousDepartment = null;
        int rank = 0;
        BigDecimal previousAverage = null;
        for (StudentAverage student : students) {
            BigDecimal average = student.average.setScale(1, RoundingMode.HALF_UP);
            if (!student.department.equals(previousDepartment)) { rank = 1; previousAverage = average; previousDepartment = student.department; }
            else if (average.compareTo(previousAverage) != 0) { rank++; previousAverage = average; }
            model.addRow(new Object[]{student.code, student.name, student.department, average.toPlainString(), rank});
        }
        return new JTable(model);
    }

    private JTable departmentTable() throws SQLException {
        DefaultTableModel model = new DefaultTableModel(new Object[]{"학과", "학생 평균"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        String sql = "SELECT d.department_name, AVG((g.attendance_score+g.assignment_score+g.midterm_score+g.final_score)/4.0) " +
                "FROM student_grades g JOIN student_info si ON si.student_cd=g.student_cd " +
                "JOIN department d ON d.department_cd=si.department_cd " +
                "WHERE g.credit IS NOT NULL " +
                "GROUP BY d.department_cd, d.department_name ORDER BY d.department_name";
        try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(sql);
             ResultSet rows = statement.executeQuery()) {
            while (rows.next()) model.addRow(new Object[]{rows.getString(1), rows.getBigDecimal(2).setScale(1, RoundingMode.HALF_UP).toPlainString()});
        }
        return new JTable(model);
    }

    private static final class StudentAverage {
        final String code, name, department;
        final BigDecimal average;
        StudentAverage(String code, String name, String department, BigDecimal average) {
            this.code = code; this.name = name; this.department = department; this.average = average;
        }
        String department() { return department; }
        BigDecimal average() { return average; }
    }
}


