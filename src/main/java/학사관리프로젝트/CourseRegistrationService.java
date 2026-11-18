package 학사관리프로젝트;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;

/** Applies enrollment limits atomically so concurrent requests cannot overfill a course. */
public final class CourseRegistrationService {
    public enum ResultCode { SUCCESS, COURSE_FULL, CREDIT_LIMIT, GRADUATED, ALREADY_ENROLLED, COURSE_NOT_FOUND, STUDENT_NOT_FOUND }

    private CourseRegistrationService() { }

    public static ResultCode enroll(Connection connection, String studentCode, String subjectCode) throws SQLException {
        boolean previousAutoCommit = connection.getAutoCommit();
        connection.setAutoCommit(false);
        try {
            int year;
            String semester;
            String departmentCode;
            String professorCode;
            int courseCredits;
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT academic_year, semester, department_cd, professor_cd, subject_credit FROM subject WHERE subject_cd=? FOR UPDATE")) {
                statement.setString(1, subjectCode);
                try (ResultSet rows = statement.executeQuery()) {
                    if (!rows.next()) return rollback(connection, ResultCode.COURSE_NOT_FOUND);
                    year = rows.getInt(1);
                    semester = rows.getString(2);
                    departmentCode = rows.getString(3);
                    professorCode = rows.getString(4);
                    courseCredits = Integer.parseInt(rows.getString(5));
                }
            }

            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT graduation_year FROM student_info WHERE student_cd=? FOR UPDATE")) {
                statement.setString(1, studentCode);
                try (ResultSet rows = statement.executeQuery()) {
                    if (!rows.next()) return rollback(connection, ResultCode.STUDENT_NOT_FOUND);
                    java.sql.Date graduationDate = rows.getDate(1);
                    if (graduationDate != null && !graduationDate.toLocalDate().isAfter(semesterStart(year, semester)))
                        return rollback(connection, ResultCode.GRADUATED);
                }
            }

            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT 1 FROM registration_list WHERE student_cd=? AND subject_cd=? AND year=? AND semester=?")) {
                statement.setString(1, studentCode);
                statement.setString(2, subjectCode);
                statement.setInt(3, year);
                statement.setString(4, semester);
                try (ResultSet rows = statement.executeQuery()) {
                    if (rows.next()) return rollback(connection, ResultCode.ALREADY_ENROLLED);
                }
            }

            int enrolled;
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT COUNT(*) FROM registration_list WHERE subject_cd=? AND year=? AND semester=?")) {
                statement.setString(1, subjectCode);
                statement.setInt(2, year);
                statement.setString(3, semester);
                try (ResultSet rows = statement.executeQuery()) { rows.next(); enrolled = rows.getInt(1); }
            }
            if (enrolled >= 15) return rollback(connection, ResultCode.COURSE_FULL);

            int currentCredits;
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT COALESCE(SUM(CAST(s.subject_credit AS UNSIGNED)),0) " +
                    "FROM registration_list r JOIN subject s ON s.subject_cd=r.subject_cd " +
                    "WHERE r.student_cd=? AND r.year=? AND r.semester=?")) {
                statement.setString(1, studentCode);
                statement.setInt(2, year);
                statement.setString(3, semester);
                try (ResultSet rows = statement.executeQuery()) { rows.next(); currentCredits = rows.getInt(1); }
            }
            if (currentCredits + courseCredits > 18) return rollback(connection, ResultCode.CREDIT_LIMIT);

            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO registration_list(student_cd, subject_cd, professor_cd, year, semester) VALUES(?,?,?,?,?)")) {
                statement.setString(1, studentCode);
                statement.setString(2, subjectCode);
                statement.setString(3, professorCode);
                statement.setInt(4, year);
                statement.setString(5, semester);
                statement.executeUpdate();
            }
            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO student_grades(student_cd, department_cd, subject_cd, professor_cd, academic_year, semester, midterm_score, final_score, assignment_score, attendance_score, grade, credit) VALUES(?,?,?,?,?,?,0,0,0,0,'F',?)")) {
                statement.setString(1, studentCode);
                statement.setString(2, departmentCode);
                statement.setString(3, subjectCode);
                statement.setString(4, professorCode);
                statement.setInt(5, year);
                statement.setString(6, semester);
                statement.setNull(7, Types.DECIMAL);
                statement.executeUpdate();
            }
            connection.commit();
            return ResultCode.SUCCESS;
        } catch (SQLException | RuntimeException failure) {
            connection.rollback();
            throw failure;
        } finally {
            connection.setAutoCommit(previousAutoCommit);
        }
    }

    private static ResultCode rollback(Connection connection, ResultCode result) throws SQLException {
        connection.rollback();
        return result;
    }

    private static LocalDate semesterStart(int year, String semester) {
        switch (semester) {
            case "1": return LocalDate.of(year, 3, 1);
            case "2": return LocalDate.of(year, 9, 1);
            case "3": return LocalDate.of(year, 7, 1);
            case "4": return LocalDate.of(year, 12, 1);
            default: throw new IllegalArgumentException("학기 값이 올바르지 않습니다: " + semester);
        }
    }
}


