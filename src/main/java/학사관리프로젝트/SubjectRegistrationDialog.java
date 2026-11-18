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

final class SubjectRegistrationDialog extends JDialog {
    private final SubjectManagement manager;
    private final JTextField code = new JTextField();
    private final JTextField name = new JTextField();
    private final JTextField year = new JTextField();
    private final JTextField startTime = new JTextField();
    private final JTextField endTime = new JTextField();
    private final JComboBox<String> department = new JComboBox<>();
    private final JTextField professor = new JTextField();
    private final JComboBox<String> day = new JComboBox<>(new String[]{
            "요일 선택", "월", "화", "수", "목", "금"});
    private final JComboBox<String> semester = new JComboBox<>(new String[]{
            "학기 선택", "1학기", "2학기", "여름학기", "겨울학기"});
    private final JComboBox<String> credit = new JComboBox<>();

    SubjectRegistrationDialog(SubjectManagement manager) throws SQLException {
        super((Frame) manager.getDialogOwnerFrame(), "새 강좌 등록", Dialog.ModalityType.APPLICATION_MODAL);
        this.manager = manager;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(680, 600);
        setResizable(false);
        setLayout(new BorderLayout());
        code.setText(manager.nextSubjectCode());
        code.setEditable(false);
        department.addItem("학과 선택");
        try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(
                "SELECT department_name FROM department ORDER BY department_cd");
             ResultSet rows = statement.executeQuery()) {
            while (rows.next()) department.addItem(rows.getString(1));
        }
        professor.setEditable(false);
        professor.setText("학과를 선택하세요");
        department.addActionListener(event -> showAssignedProfessor());
        credit.addItem("학점 선택");
        for (int value = 1; value <= 5; value++) credit.addItem(Integer.toString(value));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(javax.swing.BorderFactory.createEmptyBorder(18, 24, 12, 24));
        addRow(form, 0, "강좌코드 (자동 부여)", code);
        addRow(form, 1, "강좌명", name);
        addRow(form, 2, "학과", department);
        addRow(form, 3, "담당교수", professor);
        addRow(form, 4, "개설년도 (yyyy)", year);
        addRow(form, 5, "학기", semester);
        addRow(form, 6, "요일", day);
        addRow(form, 7, "시작시간 (HH:mm)", startTime);
        addRow(form, 8, "종료시간 (HH:mm)", endTime);
        addRow(form, 9, "학점", credit);
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
        register.addActionListener(event -> register());
        cancel.addActionListener(event -> dispose());
        setLocationRelativeTo(manager.getDialogOwnerFrame());
    }

    private void addRow(JPanel panel, int row, String text, java.awt.Component field) {
        GridBagConstraints label = new GridBagConstraints();
        label.gridx = 0;
        label.gridy = row;
        label.anchor = GridBagConstraints.LINE_END;
        label.insets = new Insets(5, 5, 5, 14);
        panel.add(new JLabel(text), label);
        field.setPreferredSize(new Dimension(320, 30));
        GridBagConstraints input = new GridBagConstraints();
        input.gridx = 1;
        input.gridy = row;
        input.insets = new Insets(5, 0, 5, 5);
        panel.add(field, input);
    }

    private void showAssignedProfessor() {
        professor.setText("학과를 선택하세요");
        if (department.getSelectedIndex() <= 0) return;
        try {
            ProfessorOption assigned = manager.assignedProfessor((String) department.getSelectedItem());
            professor.setText(assigned == null ? "재직 중인 담당교수 없음" : assigned.toString());
        } catch (SQLException exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(),
                    "담당교수 조회 오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void register() {
        if (name.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "강좌명이 입력되지 않았습니다!");
            return;
        }
        if (department.getSelectedIndex() <= 0) {
            JOptionPane.showMessageDialog(this, "학과를 선택하세요.");
            return;
        }
        if (year.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "개설년도(yyyy)를 입력하세요.");
            return;
        }
        if (semester.getSelectedIndex() <= 0 || credit.getSelectedIndex() <= 0) {
            JOptionPane.showMessageDialog(this, "학기와 학점을 선택하세요.");
            return;
        }
        String schedule;
        try {
            schedule = ClassSchedule.create((String) day.getSelectedItem(),
                    startTime.getText(), endTime.getText()).toString();
        } catch (IllegalArgumentException exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(),
                    "강의시간 입력 오류", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (manager.registerSubject(code.getText(), name.getText(),
                (String) department.getSelectedItem(), year.getText(),
                (String) semester.getSelectedItem(), schedule,
                (String) credit.getSelectedItem())) {
            JOptionPane.showMessageDialog(this, "강좌가 등록되었습니다.");
            dispose();
        }
    }
}
