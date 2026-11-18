package 학사관리프로젝트;

import java.awt.Color;
import java.awt.Font;
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
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

/** Course enrollment for the student who is logged in. */
public class EnrollmentManagement extends UniversityFrame {
    private static final String[] COLUMNS = {"강좌코드", "강좌명", "학과", "개설년도",
            "학기", "강의시간", "담당교수", "학점"};
    private final String studentCode;
    private final JComboBox<DepartmentChoice> departmentFilter = new JComboBox<>();
    private final DefaultTableModel availableModel = readOnlyModel();
    private final DefaultTableModel enrolledModel = readOnlyModel();
    private JTable availableTable;
    private JTable enrolledTable;
    private JButton enrollButton;
    private JButton cancelButton;

    EnrollmentManagement(String studentCode) throws SQLException {
        this.studentCode = studentCode;
        setTitle("수강 신청");
        setLayout(null);
        setSize(1400, 800);
        setLocationRelativeTo(null);
        DatabaseConnection.getConnection();
        buildHeader();
        buildSearch();
        buildTables();
        buildButtons();
        loadDepartments();
        refreshLists();
        setVisible(true);
    }

    private static DefaultTableModel readOnlyModel() {
        return new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
    }

    private void buildHeader() {
        JPanel header = new JPanel(null);
        header.setBounds(0, 0, 1400, 50);
        header.setBackground(new Color(123, 139, 155));
        JLabel title = new JLabel("수 강 신 청", JLabel.CENTER);
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
        JLabel label = new JLabel("학과");
        label.setBounds(40, 17, 65, 32);
        label.setFont(UiFonts.font(Font.PLAIN, 13));
        search.add(label);
        departmentFilter.setBounds(110, 17, 650, 32);
        departmentFilter.setFont(UiFonts.font(Font.PLAIN, 13));
        search.add(departmentFilter);
        JButton searchButton = new JButton("검색");
        searchButton.setBounds(900, 17, 170, 32);
        styleButton(searchButton);
        searchButton.addActionListener(event -> reloadAvailable());
        search.add(searchButton);
        JButton allButton = new JButton("전체조회");
        allButton.setBounds(1100, 17, 240, 32);
        styleButton(allButton);
        allButton.addActionListener(event -> {
            departmentFilter.setSelectedIndex(0);
            reloadAvailable();
        });
        search.add(allButton);
        add(search);
    }

    private void buildTables() {
        availableTable = new JTable(availableModel);
        availableTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        availableTable.setRowHeight(25);
        availableTable.setFont(UiFonts.font(Font.PLAIN, 13));
        availableTable.setBackground(new Color(208, 224, 240));
        setCourseColumnWidths(availableTable);
        availableTable.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting() && enrollButton != null) {
                enrollButton.setEnabled(availableTable.getSelectedRow() >= 0);
            }
        });
        JScrollPane availableScroll = new JScrollPane(availableTable);
        availableScroll.setBorder(new TitledBorder("신청 가능한 강좌"));
        availableScroll.setBounds(20, 130, 1360, 260);
        availableScroll.getViewport().setBackground(Color.WHITE);
        add(availableScroll);

        enrolledTable = new JTable(enrolledModel);
        enrolledTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        enrolledTable.setRowHeight(25);
        enrolledTable.setFont(UiFonts.font(Font.PLAIN, 13));
        enrolledTable.setBackground(new Color(208, 224, 240));
        setCourseColumnWidths(enrolledTable);
        enrolledTable.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting() && cancelButton != null) {
                cancelButton.setEnabled(enrolledTable.getSelectedRow() >= 0);
            }
        });
        JScrollPane enrolledScroll = new JScrollPane(enrolledTable);
        enrolledScroll.setBorder(new TitledBorder("신청한 강좌"));
        enrolledScroll.setBounds(20, 405, 1360, 260);
        enrolledScroll.getViewport().setBackground(Color.WHITE);
        add(enrolledScroll);
    }

    private void setCourseColumnWidths(JTable table) {
        int[] widths = {110, 190, 160, 110, 100, 260, 180, 90};
        for (int index = 0; index < widths.length; index++) {
            table.getColumnModel().getColumn(index).setPreferredWidth(widths[index]);
        }
    }

    private void buildButtons() {
        enrollButton = new JButton("수강 신청");
        enrollButton.setBounds(20, 700, 420, 45);
        enrollButton.setEnabled(false);
        styleButton(enrollButton);
        enrollButton.addActionListener(event -> enrollSelected());
        add(enrollButton);

        cancelButton = new JButton("수강 취소");
        cancelButton.setBounds(480, 700, 420, 45);
        cancelButton.setEnabled(false);
        styleButton(cancelButton);
        cancelButton.addActionListener(event -> cancelSelected());
        add(cancelButton);

        JButton exitButton = new JButton("종료");
        exitButton.setBounds(940, 700, 420, 45);
        styleButton(exitButton);
        exitButton.addActionListener(event -> confirmProgramExit());
        add(exitButton);
    }

    private void styleButton(JButton button) {
        button.setFont(UiFonts.font(Font.PLAIN, 15));
        button.setBackground(new Color(234, 206, 202));
        button.setForeground(new Color(105, 100, 107));
    }

    private void loadDepartments() throws SQLException {
        departmentFilter.addItem(new DepartmentChoice(null, "전체 학과"));
        try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(
                "SELECT department_cd,department_name FROM department ORDER BY department_name");
                ResultSet rows = statement.executeQuery()) {
            while (rows.next()) departmentFilter.addItem(
                    new DepartmentChoice(rows.getString(1), rows.getString(2)));
        }
    }

    private void refreshLists() throws SQLException {
        loadAvailable();
        loadEnrolled();
    }

    private void reloadAvailable() {
        try {
            loadAvailable();
        } catch (SQLException exception) {
            showError("강좌 조회 오류", exception);
        }
    }

    private void loadAvailable() throws SQLException {
        DepartmentChoice department = (DepartmentChoice) departmentFilter.getSelectedItem();
        boolean filtered = department != null && department.code != null;
        String sql = "SELECT s.subject_cd,s.subject_name,d.department_name,s.academic_year,"
                + "s.semester,s.subject_hour,p.user_name,s.subject_credit FROM subject s "
                + "JOIN department d ON d.department_cd=s.department_cd "
                + "JOIN professor_info p ON p.professor_cd=s.professor_cd "
                + "WHERE NOT EXISTS (SELECT 1 FROM registration_list r WHERE r.student_cd=? "
                + "AND r.subject_cd=s.subject_cd AND r.year=s.academic_year AND r.semester=s.semester)"
                + (filtered ? " AND s.department_cd=?" : "")
                + " ORDER BY s.academic_year DESC,s.semester,s.subject_cd";
        availableModel.setRowCount(0);
        try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(sql)) {
            statement.setString(1, studentCode);
            if (filtered) statement.setString(2, department.code);
            try (ResultSet rows = statement.executeQuery()) {
                while (rows.next()) availableModel.addRow(courseRow(rows));
            }
        }
        enrollButton.setEnabled(false);
    }

    private void loadEnrolled() throws SQLException {
        String sql = "SELECT r.subject_cd,s.subject_name,d.department_name,r.year,r.semester,"
                + "s.subject_hour,p.user_name,s.subject_credit FROM registration_list r "
                + "JOIN subject s ON s.subject_cd=r.subject_cd "
                + "JOIN department d ON d.department_cd=s.department_cd "
                + "JOIN professor_info p ON p.professor_cd=r.professor_cd "
                + "WHERE r.student_cd=? ORDER BY r.year DESC,r.semester,r.subject_cd";
        enrolledModel.setRowCount(0);
        try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(sql)) {
            statement.setString(1, studentCode);
            try (ResultSet rows = statement.executeQuery()) {
                while (rows.next()) enrolledModel.addRow(courseRow(rows));
            }
        }
        cancelButton.setEnabled(false);
    }

    private Object[] courseRow(ResultSet row) throws SQLException {
        return new Object[]{row.getString(1), row.getString(2), row.getString(3),
                String.format("%04d", row.getInt(4)), semesterLabel(row.getString(5)),
                row.getString(6), row.getString(7), row.getString(8)};
    }

    private void enrollSelected() {
        int row = availableTable.getSelectedRow();
        if (row < 0) return;
        String code = String.valueOf(availableTable.getValueAt(row, 0));
        try {
            CourseRegistrationService.ResultCode result = CourseRegistrationService.enroll(
                    DatabaseConnection.getConnection(), studentCode, code);
            if (result == CourseRegistrationService.ResultCode.SUCCESS) {
                refreshLists();
                JOptionPane.showMessageDialog(getDialogOwnerFrame(), "수강 신청이 완료되었습니다.");
            } else {
                JOptionPane.showMessageDialog(getDialogOwnerFrame(), enrollmentMessage(result));
            }
        } catch (SQLException | RuntimeException exception) {
            showError("수강 신청 오류", exception);
        }
    }

    private String enrollmentMessage(CourseRegistrationService.ResultCode result) {
        switch (result) {
            case COURSE_FULL: return "수강 신청 가능 인원이 초과 되었습니다.";
            case CREDIT_LIMIT: return "한 학기 최대 신청 학점은 18학점입니다.";
            case GRADUATED: return "졸업한 학생은 수강 신청할 수 없습니다.";
            case ALREADY_ENROLLED: return "이미 수강 신청한 강좌입니다.";
            default: return "학생 또는 강좌 정보를 찾을 수 없습니다.";
        }
    }

    private void cancelSelected() {
        int row = enrolledTable.getSelectedRow();
        if (row < 0) return;
        String code = String.valueOf(enrolledTable.getValueAt(row, 0));
        String name = String.valueOf(enrolledTable.getValueAt(row, 1));
        int year = Integer.parseInt(String.valueOf(enrolledTable.getValueAt(row, 3)));
        String semester = semesterCode(String.valueOf(enrolledTable.getValueAt(row, 4)));
        int choice = JOptionPane.showConfirmDialog(getDialogOwnerFrame(),
                name + " 수강 신청을 취소하시겠습니까?", "수강 취소",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (choice != JOptionPane.YES_OPTION) return;
        try {
            if (cancelEnrollment(code, year, semester)) {
                refreshLists();
                JOptionPane.showMessageDialog(getDialogOwnerFrame(), "수강 신청이 취소되었습니다.");
            }
        } catch (SQLException exception) {
            showError("수강 취소 오류", exception);
        }
    }

    private boolean cancelEnrollment(String code, int year, String semester) throws SQLException {
        Connection connection = DatabaseConnection.getConnection();
        boolean oldAutoCommit = connection.getAutoCommit();
        connection.setAutoCommit(false);
        try {
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT credit FROM student_grades WHERE student_cd=? AND subject_cd=? "
                    + "AND academic_year=? AND semester=? FOR UPDATE")) {
                statement.setString(1, studentCode);
                statement.setString(2, code);
                statement.setInt(3, year);
                statement.setString(4, semester);
                try (ResultSet rows = statement.executeQuery()) {
                    if (rows.next() && rows.getBigDecimal(1) != null) {
                        connection.rollback();
                        JOptionPane.showMessageDialog(getDialogOwnerFrame(),
                                "성적이 확정된 강좌는 수강 취소할 수 없습니다.");
                        return false;
                    }
                }
            }
            try (PreparedStatement statement = connection.prepareStatement(
                    "DELETE FROM student_grades WHERE student_cd=? AND subject_cd=? "
                    + "AND academic_year=? AND semester=?")) {
                statement.setString(1, studentCode);
                statement.setString(2, code);
                statement.setInt(3, year);
                statement.setString(4, semester);
                statement.executeUpdate();
            }
            int deleted;
            try (PreparedStatement statement = connection.prepareStatement(
                    "DELETE FROM registration_list WHERE student_cd=? AND subject_cd=? "
                    + "AND year=? AND semester=?")) {
                statement.setString(1, studentCode);
                statement.setString(2, code);
                statement.setInt(3, year);
                statement.setString(4, semester);
                deleted = statement.executeUpdate();
            }
            if (deleted == 0) {
                connection.rollback();
                return false;
            }
            connection.commit();
            return true;
        } catch (SQLException exception) {
            connection.rollback();
            throw exception;
        } finally {
            connection.setAutoCommit(oldAutoCommit);
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

    private void showError(String title, Exception exception) {
        JOptionPane.showMessageDialog(getDialogOwnerFrame(), exception.getMessage(),
                title, JOptionPane.ERROR_MESSAGE);
    }

    private static final class DepartmentChoice {
        final String code;
        final String name;

        DepartmentChoice(String code, String name) {
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
