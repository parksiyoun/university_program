package 학사관리프로젝트;

import java.awt.BorderLayout;
import java.awt.Font;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/** Read-only view of administrator account details, without passwords. */
public final class AdminInformation extends UniversityFrame {
    public AdminInformation() throws SQLException {
        super("관리자 정보");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(1120, 500);

        DefaultTableModel model = new DefaultTableModel(
                new Object[] {"아이디", "이름", "권한", "생년월일"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        String sql = "SELECT user_id,user_name,access_level,birth_date " +
                "FROM admin_settings WHERE access_level='admin' ORDER BY user_id";
        try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(sql);
             ResultSet rows = statement.executeQuery()) {
            while (rows.next()) {
                model.addRow(new Object[] {rows.getString(1), rows.getString(2), rows.getString(3), rows.getString(4)});
            }
        }

        JTable table = new JTable(model);
        table.setFont(UiFonts.font(Font.PLAIN, 14));
        table.setRowHeight(28);
        add(new JScrollPane(table), BorderLayout.CENTER);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
