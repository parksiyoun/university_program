package 학사관리프로젝트;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/** Allows a user to update only their own account and profile. */
public final class AccountSettings extends UniversityFrame {
    private final String loginId;
    private final String accessLevel;
    private final JTextField name = new JTextField();
    private final javax.swing.JPasswordField currentPassword = new javax.swing.JPasswordField();
    private final javax.swing.JPasswordField password = new javax.swing.JPasswordField();
    private final javax.swing.JPasswordField confirmation = new javax.swing.JPasswordField();
    private final JTextField address = new JTextField();
    private final JTextField mobile = new JTextField();
    private final JTextField phone = new JTextField();
    private final JTextField email = new JTextField();

    public AccountSettings(String loginId, String accessLevel) throws SQLException {
        super("내 정보");
        this.loginId = loginId;
        this.accessLevel = accessLevel;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1120, 510);
        setLocationRelativeTo(null);
        name.setEditable(false);
        Passwords.limitNewPassword(password);
        Passwords.limitNewPassword(confirmation);

        JPanel form = new JPanel(new GridBagLayout());
        addRow(form, "이름", name);
        addRow(form, "현재 비밀번호 (비밀번호 변경 시 입력)", currentPassword);
        addRow(form, "새 비밀번호 (12자)", password);
        addRow(form, "새 비밀번호 확인", confirmation);
        if (!"admin".equals(accessLevel)) {
            addRow(form, "주소", address);
            addRow(form, "휴대전화", mobile);
            addRow(form, "전화번호", phone);
            addRow(form, "이메일", email);
        }

        JButton save = new JButton("저장");
        JButton close = new JButton("닫기");
        JPanel buttons = new JPanel();
        buttons.add(save);
        buttons.add(close);
        add(form, java.awt.BorderLayout.CENTER);
        add(buttons, java.awt.BorderLayout.SOUTH);
        save.addActionListener(event -> save());
        close.addActionListener(event -> dispose());
        loadOwnAccount();
        setVisible(true);
    }

    private static void addRow(JPanel panel, String label, JTextField field) {
        int row = panel.getComponentCount() / 2;
        GridBagConstraints caption = new GridBagConstraints();
        caption.gridx = 0;
        caption.gridy = row;
        caption.anchor = GridBagConstraints.LINE_END;
        caption.insets = new Insets(6, 8, 6, 16);
        panel.add(new JLabel(label), caption);

        field.setPreferredSize(new Dimension(320, 32));
        GridBagConstraints input = new GridBagConstraints();
        input.gridx = 1;
        input.gridy = row;
        input.anchor = GridBagConstraints.LINE_START;
        input.insets = new Insets(6, 0, 6, 8);
        panel.add(field, input);
    }
private void loadOwnAccount() throws SQLException {
        try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(
                "SELECT user_name FROM admin_settings WHERE user_id=?")) {
            statement.setString(1, loginId);
            try (ResultSet rows = statement.executeQuery()) {
                if (rows.next()) name.setText(rows.getString(1));
            }
        }

        String table = profileTable();
        if (table == null) return;
        String sql = "SELECT user_address,mobile_no,phone_no,user_email FROM " + table + " WHERE user_id=?";
        try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(sql)) {
            statement.setString(1, loginId);
            try (ResultSet rows = statement.executeQuery()) {
                if (rows.next()) {
                    address.setText(rows.getString(1));
                    mobile.setText(rows.getString(2));
                    phone.setText(rows.getString(3));
                    email.setText(rows.getString(4));
                }
            }
        }
    }

    private void save() {
        java.sql.Connection connection;
        try { connection = DatabaseConnection.getConnection(); }
        catch (SQLException exception) { showError(exception); return; }
        boolean autoCommit = true;
        char[] newPassword = password.getPassword();
        char[] current = currentPassword.getPassword();
        char[] repeated = confirmation.getPassword();
        try {
            autoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            if (newPassword.length > 0 || current.length > 0 || repeated.length > 0) {
                Passwords.validate(newPassword);
                if (!java.util.Arrays.equals(newPassword, repeated))
                    throw new IllegalArgumentException("두 비밀번호가 일치하지 않습니다.");
                try (PreparedStatement check = connection.prepareStatement(
                        "SELECT user_pw FROM admin_settings WHERE user_id=? FOR UPDATE")) {
                    check.setString(1, loginId);
                    try (ResultSet rows = check.executeQuery()) {
                        if (!rows.next() || !Passwords.verify(current, rows.getString(1)))
                            throw new IllegalArgumentException("현재 비밀번호가 올바르지 않습니다.");
                        if (Passwords.verify(newPassword, rows.getString(1)))
                            throw new IllegalArgumentException("기존 비밀번호와 다르게 설정하세요.");
                    }
                }
                try (PreparedStatement statement = connection.prepareStatement(
                        "UPDATE admin_settings SET user_pw=?,password_change_required=FALSE WHERE user_id=?")) {
                    statement.setString(1, Passwords.hash(newPassword));
                    statement.setString(2, loginId);
                    statement.executeUpdate();
                }
            }
            String table = profileTable();
            if (table != null) updateOwnProfile(connection, table);
            connection.commit();
            JOptionPane.showMessageDialog(this, "내 정보를 저장했습니다.", "저장", JOptionPane.INFORMATION_MESSAGE);
            password.setText("");
        } catch (SQLException | RuntimeException exception) {
            try { connection.rollback(); } catch (SQLException rollbackError) { exception.addSuppressed(rollbackError); }
            showError(exception);
        } finally {
            java.util.Arrays.fill(newPassword, '\0');
            java.util.Arrays.fill(current, '\0');
            java.util.Arrays.fill(repeated, '\0');
            password.setText(""); currentPassword.setText(""); confirmation.setText("");
            try { connection.setAutoCommit(autoCommit); } catch (SQLException exception) { exception.printStackTrace(); }
        }
    }

    private String profileTable() {
        if ("student".equals(accessLevel)) return "student_info";
        if ("professor".equals(accessLevel)) return "professor_info";
        return null;
    }

    private void updateOwnProfile(java.sql.Connection connection, String table) throws SQLException {
        String sql = "UPDATE " + table + " SET user_address=?, mobile_no=?, phone_no=?, user_email=? WHERE user_id=?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, address.getText().trim());
            statement.setString(2, mobile.getText().trim());
            if (phone.getText().isBlank()) statement.setNull(3, java.sql.Types.VARCHAR);
            else statement.setString(3, phone.getText().trim());
            statement.setString(4, email.getText().trim());
            statement.setString(5, loginId);
            statement.executeUpdate();
        }
    }

    private void showError(Exception exception) {
        JOptionPane.showMessageDialog(this, "내 정보를 저장하지 못했습니다.\n" + exception.getMessage(),
                "오류", JOptionPane.ERROR_MESSAGE);
    }
}
