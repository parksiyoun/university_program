package 학사관리프로젝트;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.util.Arrays;

/** Explicit maintenance: retain existing passwords, replace only their storage format. */
public final class PasswordMigration {
    private PasswordMigration() { }
    public static void main(String[] args) throws Exception {
        if (args.length != 1 || !"--hash-existing".equals(args[0])) {
            System.out.println("기존 비밀번호 값을 유지하면서 해시 저장으로 전환합니다. 실행 인자: --hash-existing");
            return;
        }
        Connection connection = DatabaseConnection.getConnection();
        int count = 0;
        try {
            connection.setAutoCommit(false);
            try (PreparedStatement select = connection.prepareStatement("SELECT user_id,user_pw FROM admin_settings FOR UPDATE");
                    ResultSet rows = select.executeQuery();
                    PreparedStatement update = connection.prepareStatement(
                            "UPDATE admin_settings SET user_pw=?,password_change_required=TRUE WHERE user_id=?")) {
                while (rows.next()) {
                    if (rows.getString(2).startsWith("pbkdf2-sha256$")) continue;
                    char[] existing = rows.getString(2).toCharArray();
                    try { update.setString(1, Passwords.hashExisting(existing)); }
                    finally { Arrays.fill(existing, '\0'); }
                    update.setString(2, rows.getString(1));
                    update.executeUpdate();
                    count++;
                }
            }
            connection.commit();
            System.out.println("해시 저장으로 전환한 계정 수: " + count + ". 기존 비밀번호로 로그인 후 새 비밀번호를 설정하세요.");
        } catch (Exception exception) {
            connection.rollback();
            throw exception;
        } finally {
            connection.setAutoCommit(true);
            DatabaseConnection.closeAll();
        }
    }
}
