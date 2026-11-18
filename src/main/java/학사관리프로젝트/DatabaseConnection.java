package 학사관리프로젝트;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
	private static Connection conn;
	
	/* Configure credentials with UNIVERSITY_DB_USER and UNIVERSITY_DB_PASSWORD. */
	static String driver = "com.mysql.cj.jdbc.Driver";
	static String user = System.getenv("UNIVERSITY_DB_USER");
	static String pass = System.getenv("UNIVERSITY_DB_PASSWORD");
	static String dbURL = System.getenv().getOrDefault("UNIVERSITY_DB_URL",
			"jdbc:mysql://localhost:3306/university?serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8");


	private DatabaseConnection() { }

	/** Screens share one connection; only the application owns its lifetime. */
	static synchronized Connection getConnection() throws SQLException {
		if (conn != null && !conn.isClosed()) return conn;
		if (user == null || user.isBlank() || pass == null || pass.isBlank())
			throw new SQLException("UNIVERSITY_DB_USER와 UNIVERSITY_DB_PASSWORD에 전용 DB 계정을 설정하세요.");
		if ("root".equalsIgnoreCase(user.trim()))
			throw new SQLException("프로그램에는 root 대신 university 전용 DB 계정을 사용하세요.");
		try {
			// Load the MySQL JDBC driver.
			Class.forName(driver);
			System.out.println("MySQL 드라이버 검색 성공...");
			
			conn = DriverManager.getConnection(dbURL, user, pass); // 데이터베이스 연결
			System.out.println("MySQL 연결 성공...");
			return conn;
			
		} catch ( ClassNotFoundException e ) {
			throw new SQLException("MySQL JDBC 드라이버를 찾지 못했습니다. DatabaseConnection/J를 classpath에 추가하세요.", e);
		}
	}

	static synchronized void closeAll() throws SQLException {
		Connection connection = conn;
		conn = null;
		if (connection != null) {
			try {
				if (!connection.isClosed()) {
					if (!connection.getAutoCommit()) connection.rollback();
				}
			} catch (SQLException exception) {
				try { connection.close(); } catch (SQLException closeError) {
					exception.addSuppressed(closeError);
				}
				throw exception;
			}
			connection.close();
		}
	}

	public static void main(String[] args) throws SQLException {
		Login.main(args);
	}
}


