package 학사관리프로젝트;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

public final class ProfessorRegistrationDialog extends JDialog {
    private final ProfessorManagement manager;
    private final JTextField professorCode = new JTextField(24);
    private final JTextField professorName = new JTextField(24);
    private final JTextField birthDate = new JTextField(24);
    private final JTextField address = new JTextField(24);
    private final JTextField mobileFirst = new JTextField(6);
    private final JTextField mobileSecond = new JTextField(8);
    private final JTextField mobileThird = new JTextField(8);
    private final JTextField phoneFirst = new JTextField(6);
    private final JTextField phoneSecond = new JTextField(8);
    private final JTextField phoneThird = new JTextField(8);
    private final JTextField hireYear = new JTextField(24);
    private final JTextField degree = new JTextField(24);
    private final JComboBox<String> department = new JComboBox<>();
    private final JTextField room = new JTextField(24);
    private final JTextField email = new JTextField(24);
    private final JRadioButton male = new JRadioButton("남성");
    private final JRadioButton female = new JRadioButton("여성");

    ProfessorRegistrationDialog(ProfessorManagement manager) throws SQLException {
        super((Frame) manager.getDialogOwnerFrame(), "새 교수 등록", Dialog.ModalityType.APPLICATION_MODAL);
        this.manager = manager;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(780, 680);
        setResizable(false);
        setLayout(new BorderLayout(12, 12));
        professorCode.setText(manager.nextProfessorCode());
        professorCode.setEditable(false);
        department.addItem("학과 선택");
        try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(
                "SELECT department_cd,department_name FROM department ORDER BY department_cd");
             ResultSet rows = statement.executeQuery()) {
            while (rows.next()) {
                department.addItem(rows.getString(2) + " (" + rows.getString(1) + ")");
            }
        }

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(javax.swing.BorderFactory.createEmptyBorder(18, 24, 12, 24));
        addRow(form, 0, "교수코드 (자동 부여)", professorCode);
        addRow(form, 1, "이름", professorName);
        addRow(form, 2, "생년월일 (YYYY-MM-DD)", birthDate);
        addRow(form, 3, "주소", address);
        addRow(form, 4, "휴대폰번호", phoneParts(mobileFirst, mobileSecond, mobileThird));
        addRow(form, 5, "전화번호 (선택)", phoneParts(phoneFirst, phoneSecond, phoneThird));
        addRow(form, 6, "임용년도 (YYYY)", hireYear);
        addRow(form, 7, "학위", degree);
        addRow(form, 8, "개설학과", department);
        addRow(form, 9, "연구실", room);
        addRow(form, 10, "이메일", email);
        JPanel genders = new JPanel();
        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(male);
        genderGroup.add(female);
        genders.add(male);
        genders.add(female);
        addRow(form, 11, "성별", genders);

        Font font = UiFonts.font(Font.PLAIN, 14);
        for (java.awt.Component component : form.getComponents()) component.setFont(font);
        JButton register = new JButton("등록하기");
        JButton cancel = new JButton("취소");
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
        register.addActionListener(event -> registerProfessor());
        cancel.addActionListener(event -> dispose());
        setLocationRelativeTo(manager.getDialogOwnerFrame());
    }

    private JPanel phoneParts(JTextField first, JTextField second, JTextField third) {
        JPanel panel = new JPanel();
        panel.add(first);
        panel.add(new JLabel("-"));
        panel.add(second);
        panel.add(new JLabel("-"));
        panel.add(third);
        return panel;
    }

    private void addRow(JPanel form, int row, String labelText, java.awt.Component field) {
        GridBagConstraints label = new GridBagConstraints();
        label.gridx = 0;
        label.gridy = row;
        label.anchor = GridBagConstraints.LINE_END;
        label.insets = new Insets(5, 5, 5, 12);
        form.add(new JLabel(labelText), label);
        GridBagConstraints input = new GridBagConstraints();
        input.gridx = 1;
        input.gridy = row;
        input.weightx = 1;
        input.fill = GridBagConstraints.HORIZONTAL;
        input.insets = new Insets(5, 5, 5, 5);
        form.add(field, input);
    }

    private void registerProfessor() {
        String[][] required = {
                {professorName.getText(), "이름이 입력되지 않았습니다!"},
                {birthDate.getText(), "생년월일이 입력되지 않았습니다!"},
                {address.getText(), "주소가 입력되지 않았습니다!"},
                {mobileFirst.getText(), "휴대폰번호가 입력되지 않았습니다!"},
                {mobileSecond.getText(), "휴대폰번호가 입력되지 않았습니다!"},
                {mobileThird.getText(), "휴대폰번호가 입력되지 않았습니다!"},
                {hireYear.getText(), "임용년도가 입력되지 않았습니다!"},
                {degree.getText(), "학위가 입력되지 않았습니다!"},
                {room.getText(), "연구실이 입력되지 않았습니다!"},
                {email.getText(), "이메일이 입력되지 않았습니다!"},
                {male.isSelected() || female.isSelected() ? "selected" : "", "성별이 입력되지 않았습니다!"}
        };
        for (String[] field : required) {
            if (field[0].trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, field[1], "필수 입력", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        if (department.getSelectedIndex() <= 0) {
            JOptionPane.showMessageDialog(this, "개설학과를 선택하세요.",
                    "필수 입력", JOptionPane.WARNING_MESSAGE);
            return;
        }
        boolean anyPhone = !phoneFirst.getText().isBlank()
                || !phoneSecond.getText().isBlank() || !phoneThird.getText().isBlank();
        boolean allPhone = !phoneFirst.getText().isBlank()
                && !phoneSecond.getText().isBlank() && !phoneThird.getText().isBlank();
        if (anyPhone && !allPhone) {
            JOptionPane.showMessageDialog(this, "전화번호를 모두 입력하거나 비워주세요.",
                    "전화번호", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String mobile = mobileFirst.getText().trim() + "-" + mobileSecond.getText().trim()
                + "-" + mobileThird.getText().trim();
        String phone = allPhone ? phoneFirst.getText().trim() + "-" + phoneSecond.getText().trim()
                + "-" + phoneThird.getText().trim() : "";
        boolean registered = manager.registerProfessor(professorCode.getText(), professorName.getText(),
                birthDate.getText(), address.getText(), mobile, phone, hireYear.getText(),
                degree.getText(), (String) department.getSelectedItem(), room.getText(), email.getText(),
                male.isSelected() ? "남성" : "여성");
        if (registered) {
            JOptionPane.showMessageDialog(this, "교수 정보가 등록되었습니다.", "등록",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
        }
    }
}
