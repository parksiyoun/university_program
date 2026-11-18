package 학사관리프로젝트;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FlowLayout;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

/** Statistics for students registered in the logged-in professor's courses. */
public final class ProfessorStatistics extends UniversityFrame {
    private static final String[] GRADES = {"A", "B", "C", "D", "F"};
    private final String professorCode;

    public ProfessorStatistics(String professorCode) throws SQLException {
        this.professorCode = professorCode;
        setTitle("내 강좌 성적 통계");
        setLayout(null);
        setSize(1400, 800);
        setLocationRelativeTo(null);

        JPanel header = new JPanel(null);
        header.setBounds(0, 0, 1400, 50);
        header.setBackground(new Color(123, 139, 155));
        JLabel title = new JLabel("내 강 좌 성 적 통 계", JLabel.CENTER);
        title.setBounds(0, 10, 1400, 30);
        title.setFont(UiFonts.font(Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        header.add(title);
        add(header);

        Map<String, List<StudentResult>> courses = loadResults();
        JTabbedPane tabs = new JTabbedPane();
        tabs.setBounds(20, 65, 1360, 680);
        JPanel studentPanel = new JPanel(new BorderLayout());
        JPanel courseSelection = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        JLabel courseLabel = new JLabel("강좌 선택");
        courseLabel.setFont(UiFonts.font(Font.PLAIN, 15));
        courseSelection.add(courseLabel);
        JComboBox<CourseChoice> courseFilter = new JComboBox<>();
        courseFilter.setFont(UiFonts.font(Font.PLAIN, 15));
        courseFilter.setPreferredSize(new Dimension(480, 32));
        courseFilter.addItem(new CourseChoice(null, "강좌를 선택하세요"));
        loadCourseOptions(courseFilter);
        courseSelection.add(courseFilter);
        studentPanel.add(courseSelection, BorderLayout.NORTH);
        JScrollPane studentScroll = new JScrollPane(studentTable(Collections.emptyMap()));
        studentPanel.add(studentScroll, BorderLayout.CENTER);
        JPanel gradeSummary = new JPanel(new FlowLayout(FlowLayout.CENTER, 65, 12));
        gradeSummary.setBorder(new TitledBorder("등급별 인원"));
        gradeSummary.setPreferredSize(new Dimension(0, 75));
        JLabel[] gradeCounts = new JLabel[GRADES.length];
        for (int index = 0; index < GRADES.length; index++) {
            gradeCounts[index] = new JLabel(GRADES[index] + ": 0명");
            gradeCounts[index].setFont(UiFonts.font(Font.BOLD, 16));
            gradeSummary.add(gradeCounts[index]);
        }
        studentPanel.add(gradeSummary, BorderLayout.SOUTH);
        courseFilter.addActionListener(event -> {
            CourseChoice selected = (CourseChoice) courseFilter.getSelectedItem();
            Map<String, List<StudentResult>> selectedCourse = new LinkedHashMap<>();
            List<StudentResult> students = Collections.emptyList();
            if (selected != null && selected.key != null && courses.containsKey(selected.key)) {
                students = courses.get(selected.key);
                selectedCourse.put(selected.key, students);
            }
            studentScroll.setViewportView(studentTable(selectedCourse));
            updateGradeCounts(students, gradeCounts);
        });
        tabs.addTab("강좌별 학생 평균·등수", studentPanel);
        tabs.addTab("강좌별 평균", new JScrollPane(courseTable(courses)));
        add(tabs);

        setVisible(true);
    }

    private void updateGradeCounts(List<StudentResult> students, JLabel[] gradeCounts) {
        int[] counts = new int[GRADES.length];
        for (StudentResult student : students) {
            if (student.average == null) continue;
            String grade = GradeCalculator.letter(student.average);
            for (int index = 0; index < GRADES.length; index++) {
                if (GRADES[index].equals(grade)) {
                    counts[index]++;
                    break;
                }
            }
        }
        for (int index = 0; index < GRADES.length; index++) {
            gradeCounts[index].setText(GRADES[index] + ": " + counts[index] + "명");
        }
    }

    private void loadCourseOptions(JComboBox<CourseChoice> courseFilter) throws SQLException {
        String sql = "SELECT DISTINCT r.subject_cd,s.subject_name,r.year,r.semester "
                + "FROM registration_list r JOIN subject s ON s.subject_cd=r.subject_cd "
                + "WHERE r.professor_cd=? ORDER BY r.year DESC,r.semester,r.subject_cd";
        try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(sql)) {
            statement.setString(1, professorCode);
            try (ResultSet rows = statement.executeQuery()) {
                while (rows.next()) {
                    String code = rows.getString(1);
                    int year = rows.getInt(3);
                    String semester = rows.getString(4);
                    String key = code + ":" + year + ":" + semester;
                    String label = rows.getString(2) + " (" + code + ", "
                            + String.format("%04d", year) + " " + semesterLabel(semester) + ")";
                    courseFilter.addItem(new CourseChoice(key, label));
                }
            }
        }
    }

    private Map<String, List<StudentResult>> loadResults() throws SQLException {
        Map<String, List<StudentResult>> courses = new LinkedHashMap<>();
        String sql = "SELECT r.subject_cd,s.subject_name,r.year,r.semester,r.student_cd,"
                + "si.user_name,g.attendance_score,g.assignment_score,g.midterm_score,"
                + "g.final_score,g.credit FROM registration_list r "
                + "JOIN subject s ON s.subject_cd=r.subject_cd "
                + "JOIN student_info si ON si.student_cd=r.student_cd "
                + "LEFT JOIN student_grades g ON g.student_cd=r.student_cd "
                + "AND g.subject_cd=r.subject_cd AND g.academic_year=r.year "
                + "AND g.semester=r.semester AND g.professor_cd=r.professor_cd "
                + "WHERE r.professor_cd=? ORDER BY r.subject_cd,r.year,r.semester,r.student_cd";
        try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(sql)) {
            statement.setString(1, professorCode);
            try (ResultSet rows = statement.executeQuery()) {
                while (rows.next()) {
                    String key = rows.getString(1) + ":" + rows.getInt(3) + ":" + rows.getString(4);
                    BigDecimal average = null;
                    if (rows.getBigDecimal(11) != null) {
                        try {
                            average = GradeCalculator.average(rows.getInt(7), rows.getInt(8),
                                    rows.getInt(9), rows.getInt(10));
                        } catch (IllegalArgumentException exception) {
                            throw new SQLException("성적 데이터가 올바르지 않습니다: " + rows.getString(5), exception);
                        }
                    }
                    courses.computeIfAbsent(key, ignored -> new ArrayList<>()).add(
                            new StudentResult(rows.getString(2), rows.getInt(3), rows.getString(4),
                                    rows.getString(5), rows.getString(6), average));
                }
            }
        }
        return courses;
    }

    private JTable studentTable(Map<String, List<StudentResult>> courses) {
        DefaultTableModel model = model(new String[]{"강좌", "개설년도", "학기", "학번", "이름",
                "평균점수", "등급", "평점", "강좌 등수"});
        for (List<StudentResult> students : courses.values()) {
            students.sort(Comparator.comparing((StudentResult result) -> result.average,
                    Comparator.nullsLast(Comparator.reverseOrder())).thenComparing(result -> result.studentCode));
            BigDecimal previous = null;
            int rank = 0;
            for (int index = 0; index < students.size(); index++) {
                StudentResult student = students.get(index);
                if (student.average != null &&
                        (previous == null || student.average.compareTo(previous) != 0)) {
                    rank = index + 1;
                    previous = student.average;
                }
                model.addRow(new Object[]{student.courseName, String.format("%04d", student.year),
                        semesterLabel(student.semester), student.studentCode, student.studentName,
                        student.average == null ? "" : student.average.toPlainString(),
                        student.average == null ? "" : GradeCalculator.letter(student.average),
                        student.average == null ? "" : GradeCalculator.points(student.average).toPlainString(),
                        student.average == null ? "" : rank});
            }
        }
        return table(model);
    }

    private JTable courseTable(Map<String, List<StudentResult>> courses) {
        DefaultTableModel model = model(new String[]{"강좌", "개설년도", "학기",
                "수강 인원", "채점 인원", "평균점수"});
        for (List<StudentResult> students : courses.values()) {
            if (students.isEmpty()) continue;
            StudentResult first = students.get(0);
            BigDecimal total = BigDecimal.ZERO;
            int graded = 0;
            for (StudentResult student : students) {
                if (student.average != null) {
                    total = total.add(student.average);
                    graded++;
                }
            }
            model.addRow(new Object[]{first.courseName, String.format("%04d", first.year),
                    semesterLabel(first.semester), students.size(), graded,
                    graded == 0 ? "" : total.divide(BigDecimal.valueOf(graded),
                            1, RoundingMode.HALF_UP).toPlainString()});
        }
        return table(model);
    }

    private DefaultTableModel model(String[] columns) {
        return new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
    }

    private JTable table(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setBackground(new Color(208, 224, 240));
        table.setFont(UiFonts.font(Font.PLAIN, 13));
        return table;
    }

    private String semesterLabel(String code) {
        if ("1".equals(code)) return "1학기";
        if ("2".equals(code)) return "2학기";
        if ("3".equals(code)) return "여름학기";
        if ("4".equals(code)) return "겨울학기";
        return code;
    }

    private static final class StudentResult {
        final String courseName, semester, studentCode, studentName;
        final int year;
        final BigDecimal average;

        StudentResult(String courseName, int year, String semester, String studentCode,
                String studentName, BigDecimal average) {
            this.courseName = courseName;
            this.year = year;
            this.semester = semester;
            this.studentCode = studentCode;
            this.studentName = studentName;
            this.average = average;
        }
    }

    private static final class CourseChoice {
        final String key;
        final String label;

        CourseChoice(String key, String label) {
            this.key = key;
            this.label = label;
        }

        @Override public String toString() { return label; }
    }
}
