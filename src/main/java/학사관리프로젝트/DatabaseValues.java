package 학사관리프로젝트;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DateTimeException;
import java.time.LocalDate;

final class DatabaseValues {
	private DatabaseValues() {}

	static String lookupCode(Connection connection, String table, String codeColumn, String nameColumn, String value)
			throws SQLException {
		if (value == null || value.isBlank()) return null;
		if (value.matches("[A-Za-z0-9_-]+")) return value;
		String sql = "SELECT " + codeColumn + " FROM " + table + " WHERE " + nameColumn + " = ?";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, value.trim());
			try (ResultSet result = statement.executeQuery()) {
				if (result.next()) return result.getString(1);
			}
		}
		return null;
	}

	static String birthDate(String firstPart, String secondPart) throws SQLException {
		if (firstPart != null && firstPart.trim().matches("\\d{4}-\\d{2}-\\d{2}")) {
			try { return LocalDate.parse(firstPart.trim()).toString(); }
			catch (DateTimeException e) { throw new SQLException("올바른 생년월일을 입력하세요.", e); }
		}
		String first = firstPart == null ? "" : firstPart.replaceAll("\\D", "");
		String second = secondPart == null ? "" : secondPart.replaceAll("\\D", "");
		if (first.length() < 6) throw new SQLException("생년월일을 YYMMDD 형식으로 입력하세요.");
		String yyMMdd = first.substring(0, 6);
		int marker = second.isEmpty() ? 0 : Character.digit(second.charAt(0), 10);
		if (marker < 1 || marker > 8) throw new SQLException("주민번호 뒷자리 첫 숫자를 입력하세요.");
		int year = Integer.parseInt(yyMMdd.substring(0, 2));
		int century = (marker == 1 || marker == 2 || marker == 5 || marker == 6) ? 1900 : 2000;
		try {
			return LocalDate.of(century + year, Integer.parseInt(yyMMdd.substring(2, 4)),
					Integer.parseInt(yyMMdd.substring(4, 6))).toString();
		} catch (DateTimeException e) {
			throw new SQLException("올바른 생년월일을 입력하세요.", e);
		}
	}

	static String academicDate(String value, String defaultMonthDay) throws SQLException {
		if (value == null || value.isBlank()) return null;
		String trimmed = value.trim();
		if (trimmed.matches("\\d{4}")) return trimmed + "-" + defaultMonthDay;
		try {
			return LocalDate.parse(trimmed).toString();
		} catch (DateTimeException e) {
			throw new SQLException("날짜는 YYYY 또는 YYYY-MM-DD 형식으로 입력하세요.", e);
		}
	}
}


