package 학사관리프로젝트;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

final class DepartmentRegistrationDialog extends JDialog {
    private final DepartmentManagement manager;
    private final JTextField code = new JTextField();
    private final JTextField name = new JTextField();
    private final JTextField major = new JTextField();
    private final JComboBox<ProfessorOption> professor = new JComboBox<>();

    DepartmentRegistrationDialog(DepartmentManagement manager) throws SQLException {
        super((Frame) manager.getDialogOwnerFrame(), "새 학과 등록", Dialog.ModalityType.APPLICATION_MODAL);
        this.manager = manager;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(620, 365);
        setResizable(false);
        setLayout(new BorderLayout());
        code.setText(manager.nextDepartmentCode());
        code.setEditable(false);
        professor.addItem(new ProfessorOption(null, "미지정"));
        try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(
                "SELECT p.professor_cd,p.user_name,d.department_name "
                + "FROM professor_info p JOIN department d ON d.department_cd=p.department_cd "
                + "WHERE p.retirement_date IS NULL ORDER BY p.user_name,p.professor_cd");
             ResultSet rows = statement.executeQuery()) {
            while (rows.next()) professor.addItem(new ProfessorOption(rows.getString(1),
                    rows.getString(2) + " · " + rows.getString(3)));
        }

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 24, 12, 24));
        addRow(form, 0, "학과코드 (자동 부여)", code);
        addRow(form, 1, "학과명", name);
        addRow(form, 2, "전공명", major);
        addRow(form, 3, "담당교수", professor);
        JButton register = new JButton("등록하기");
        JButton cancel = new JButton("취소");
        Font font = UiFonts.font(Font.PLAIN, 14);
        for (JButton button : new JButton[]{register, cancel}) {
            button.setFont(font);
            button.setBackground(new Color(234, 206, 202));
            button.setForeground(new Color(105, 100, 107));
        }
        JPanel buttons = new JPanel();
        buttons.add(register);
        buttons.add(cancel);
        add(form, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
        register.addActionListener(event -> register());
        cancel.addActionListener(event -> dispose());
        setLocationRelativeTo(manager.getDialogOwnerFrame());
    }

    private void addRow(JPanel panel, int row, String text, java.awt.Component field) {
        GridBagConstraints label = new GridBagConstraints();
        label.gridx = 0;
        label.gridy = row;
        label.anchor = GridBagConstraints.LINE_END;
        label.insets = new Insets(6, 5, 6, 14);
        panel.add(new JLabel(text), label);
        field.setPreferredSize(new Dimension(290, 30));
        GridBagConstraints input = new GridBagConstraints();
        input.gridx = 1;
        input.gridy = row;
        input.insets = new Insets(6, 0, 6, 5);
        panel.add(field, input);
    }

    private void register() {
        if (name.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "학과명이 입력되지 않았습니다!");
            return;
        }
        if (major.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "전공명이 입력되지 않았습니다!");
            return;
        }
        if (manager.registerDepartment(code.getText(), name.getText(), major.getText(),
                (ProfessorOption) professor.getSelectedItem())) {
            JOptionPane.showMessageDialog(this, "학과가 등록되었습니다.");
            dispose();
        }
    }
}
