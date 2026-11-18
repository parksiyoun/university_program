package 학사관리프로젝트;

import java.awt.Component;
import java.awt.GridLayout;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

final class PasswordChange {
    private PasswordChange() { }

    /** The caller must have just verified the account's current password. */
    static boolean require(Component owner, String userId, String oldHash) throws SQLException {
        while (true) {
            JPasswordField first = new JPasswordField(24);
            JPasswordField second = new JPasswordField(24);
            Passwords.limitNewPassword(first);
            Passwords.limitNewPassword(second);
            JPanel panel = new JPanel(new GridLayout(0, 1));
            panel.add(new JLabel("최초 로그인: 새 비밀번호를 설정하세요 (12자)."));
            panel.add(first);
            panel.add(new JLabel("새 비밀번호 확인"));
            panel.add(second);
            if (JOptionPane.showConfirmDialog(owner, panel, "비밀번호 변경", JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return false;
            char[] password = first.getPassword();
            char[] confirmation = second.getPassword();
            first.setText(""); second.setText("");
            try {
                Passwords.validate(password);
                if (!Arrays.equals(password, confirmation)) throw new IllegalArgumentException("두 비밀번호가 일치하지 않습니다.");
                if (Passwords.verify(password, oldHash)) throw new IllegalArgumentException("기존 비밀번호와 다르게 설정하세요.");
                try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(
                        "UPDATE admin_settings SET user_pw=?,password_change_required=FALSE WHERE user_id=? AND user_pw=?")) {
                    statement.setString(1, Passwords.hash(password));
                    statement.setString(2, userId);
                    statement.setString(3, oldHash);
                    if (statement.executeUpdate() != 1) throw new SQLException("계정이 변경되었습니다. 다시 로그인하세요.");
                }
                return true;
            } catch (IllegalArgumentException exception) {
                JOptionPane.showMessageDialog(owner, exception.getMessage());
            } finally { Arrays.fill(password, '\0'); Arrays.fill(confirmation, '\0'); }
        }
    }
}
