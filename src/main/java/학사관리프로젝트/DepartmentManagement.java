package 학사관리프로젝트;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

public class DepartmentManagement extends UniversityFrame implements ActionListener {
    private static final String SELECT_DEPARTMENTS =
            "SELECT d.department_cd,d.department_name,d.major_name,d.professor_cd,p.user_name "
            + "FROM department d LEFT JOIN professor_info p ON p.professor_cd=d.professor_cd";
    private static final String[] SEARCH_COLUMNS =
            {"d.department_cd", "d.department_name", "d.major_name", "d.professor_cd", ""};
    private static final ProfessorOption UNASSIGNED = new ProfessorOption(null, "미지정");
    private final Font fieldFont = UiFonts.font(Font.PLAIN, 13);
    private final Font buttonFont = UiFonts.font(Font.PLAIN, 15);

    private JPanel editPanel;
    private JTextField codeField, nameField, majorField, searchField;
    private JComboBox<String> searchCategory;
    private JComboBox<ProfessorOption> searchChoice, professorField;
    private JButton searchButton, allButton, registerButton, editButton, deleteButton, closeButton;
    private JTable table;
    private DefaultTableModel model;

    public DepartmentManagement() throws SQLException {
        setTitle("학과관리");
        setLayout(null);
        setSize(1400, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        buildTitle();
        buildEditPanel();
        buildSearchPanel();
        buildTable();
        buildButtons();
        DatabaseConnection.getConnection();
        loadAll();
        setVisible(true);
    }

    private void buildTitle() {
        JPanel titlePanel = new JPanel(null);
        titlePanel.setBounds(0, 0, 1400, 50);
        titlePanel.setBackground(new Color(123, 139, 155));
        JLabel title = new JLabel("학 과 관 리", JLabel.CENTER);
        title.setBounds(0, 10, 1400, 30);
        title.setForeground(Color.WHITE);
        title.setFont(UiFonts.font(Font.BOLD, 24));
        titlePanel.add(title);
        add(titlePanel);
    }

    private void buildEditPanel() {
        editPanel = new JPanel(null);
        editPanel.setBounds(20, 475, 1360, 180);
        editPanel.setBackground(new Color(245, 245, 245));
        editPanel.setBorder(new TitledBorder("학과 정보 수정"));

        JLabel codeLabel = new JLabel("학과코드");
        codeLabel.setBounds(35, 65, 90, 30);
        codeField = new JTextField();
        codeField.setBounds(125, 65, 190, 30);
        codeField.setEditable(false);
        JLabel nameLabel = new JLabel("학과명");
        nameLabel.setBounds(335, 65, 70, 30);
        nameField = new JTextField();
        nameField.setBounds(405, 65, 220, 30);
        JLabel majorLabel = new JLabel("전공명");
        majorLabel.setBounds(645, 65, 70, 30);
        majorField = new JTextField();
        majorField.setBounds(715, 65, 220, 30);
        JLabel professorLabel = new JLabel("담당교수");
        professorLabel.setBounds(955, 65, 80, 30);
        professorField = new JComboBox<>();
        professorField.setBounds(1035, 65, 285, 30);

        for (java.awt.Component component : new java.awt.Component[]{
                codeLabel, codeField, nameLabel, nameField, majorLabel, majorField,
                professorLabel, professorField}) {
            component.setFont(fieldFont);
            editPanel.add(component);
        }
        add(editPanel);
        editPanel.setVisible(false);
    }

    private void buildSearchPanel() {
        JPanel searchPanel = new JPanel(null);
        searchPanel.setBounds(0, 55, 1400, 65);
        searchPanel.setOpaque(false);
        searchCategory = new JComboBox<>(new String[]{
                "학과코드", "학과명", "전공명", "담당교수", "검색 카테고리 선택"});
        searchCategory.setBounds(40, 17, 300, 32);
        searchCategory.setSelectedIndex(4);
        searchField = new JTextField();
        searchField.setBounds(360, 17, 420, 32);
        searchChoice = new JComboBox<>();
        searchChoice.setBounds(360, 17, 420, 32);
        searchChoice.setVisible(false);
        searchButton = new JButton("검색");
        searchButton.setBounds(900, 17, 170, 32);
        allButton = new JButton("전체조회");
        allButton.setBounds(1100, 17, 240, 32);
        searchCategory.setFont(fieldFont);
        searchField.setFont(fieldFont);
        searchChoice.setFont(fieldFont);
        for (JButton button : new JButton[]{searchButton, allButton}) {
            styleButton(button);
            button.addActionListener(this);
        }
        for (java.awt.Component component : new java.awt.Component[]{
                searchCategory, searchField, searchChoice, searchButton, allButton}) {
            searchPanel.add(component);
        }
        add(searchPanel);
        searchCategory.addActionListener(event -> updateSearchInput());
    }

    private void buildTable() {
        model = new DefaultTableModel(new String[]{"학과코드", "학과명", "전공명", "담당교수"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new TitledBorder("학과 목록"));
        scroll.setBounds(20, 130, 1360, 335);
        table.setBackground(new Color(208, 224, 240));
        scroll.getViewport().setBackground(Color.WHITE);
        table.getColumnModel().getColumn(0).setPreferredWidth(170);
        table.getColumnModel().getColumn(1).setPreferredWidth(350);
        table.getColumnModel().getColumn(2).setPreferredWidth(430);
        table.getColumnModel().getColumn(3).setPreferredWidth(390);
        table.getSelectionModel().addListSelectionListener(event -> {
            if (event.getValueIsAdjusting()) return;
            int viewRow = table.getSelectedRow();
            if (viewRow < 0) return;
            int row = table.convertRowIndexToModel(viewRow);
            codeField.setText(String.valueOf(model.getValueAt(row, 0)));
            nameField.setText(String.valueOf(model.getValueAt(row, 1)));
            majorField.setText(String.valueOf(model.getValueAt(row, 2)));
            try {
                ProfessorOption selected = (ProfessorOption) model.getValueAt(row, 3);
                loadProfessorOptions(selected.code());
            } catch (SQLException exception) {
                showError("담당교수 조회 오류", exception);
                return;
            }
            showEditPanel(true);
        });
        add(scroll);
    }

    private void buildButtons() {
        registerButton = new JButton("새 학과 등록");
        registerButton.setBounds(20, 700, 320, 45);
        editButton = new JButton("수정");
        editButton.setBounds(360, 700, 320, 45);
        deleteButton = new JButton("삭제");
        deleteButton.setBounds(700, 700, 320, 45);
        closeButton = new JButton("종료");
        closeButton.setBounds(1040, 700, 320, 45);
        for (JButton button : new JButton[]{
                registerButton, editButton, deleteButton, closeButton}) {
            styleButton(button);
            button.addActionListener(this);
            add(button);
        }
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
    }

    private void styleButton(JButton button) {
        button.setFont(buttonFont);
        button.setBackground(new Color(234, 206, 202));
        button.setForeground(new Color(105, 100, 107));
    }

    private void showEditPanel(boolean visible) {
        editPanel.setVisible(visible);
        editButton.setEnabled(visible);
        deleteButton.setEnabled(visible);
        revalidate();
        repaint();
    }

    @Override public void actionPerformed(ActionEvent event) {
        Object source = event.getSource();
        if (source == searchButton) search();
        else if (source == allButton) loadAll();
        else if (source == registerButton) {
            try {
                DepartmentRegistrationDialog dialog = new DepartmentRegistrationDialog(this);
                dialog.setVisible(true);
            } catch (SQLException exception) {
                JOptionPane.showMessageDialog(getDialogOwnerFrame(), exception.getMessage(),
                        "학과 등록 오류", JOptionPane.ERROR_MESSAGE);
            }
        } else if (source == editButton) updateSelected();
        else if (source == deleteButton) deleteSelected();
        else if (source == closeButton) confirmProgramExit();
    }

    void loadAll() {
        model.setRowCount(0);
        showEditPanel(false);
        try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(SELECT_DEPARTMENTS);
             ResultSet rows = statement.executeQuery()) {
            while (rows.next()) addRow(rows);
        } catch (SQLException exception) {
            showError("학과 목록 오류", exception);
        }
    }

    private void addRow(ResultSet row) throws SQLException {
        model.addRow(new Object[]{row.getString(1), row.getString(2), row.getString(3),
                professorOption(row.getString(4), row.getString(5))});
    }

    private ProfessorOption professorOption(String code, String name) {
        return code == null ? UNASSIGNED : new ProfessorOption(code, name);
    }

    private String professorCode(ProfessorOption option) {
        return option == null ? null : option.code();
    }

    private void loadProfessorOptions(String currentCode) throws SQLException {
        professorField.removeAllItems();
        professorField.addItem(UNASSIGNED);
        try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(
                "SELECT p.professor_cd,p.user_name,d.department_name "
                + "FROM professor_info p JOIN department d ON d.department_cd=p.department_cd "
                + "WHERE p.retirement_date IS NULL OR p.professor_cd=? "
                + "ORDER BY p.user_name,p.professor_cd")) {
            statement.setString(1, currentCode);
            try (ResultSet rows = statement.executeQuery()) {
                while (rows.next()) professorField.addItem(new ProfessorOption(rows.getString(1),
                        rows.getString(2) + " · " + rows.getString(3)));
            }
        }
        if (currentCode != null) {
            for (int i = 0; i < professorField.getItemCount(); i++) {
                if (currentCode.equals(professorCode(professorField.getItemAt(i)))) {
                    professorField.setSelectedIndex(i);
                    return;
                }
            }
        }
        professorField.setSelectedIndex(0);
    }

    private void updateSearchInput() {
        boolean professorSearch = searchCategory.getSelectedIndex() == 3;
        searchField.setVisible(!professorSearch);
        searchChoice.setVisible(professorSearch);
        searchField.setText("");
        searchChoice.removeAllItems();
        if (!professorSearch) return;
        searchChoice.addItem(UNASSIGNED);
        try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(
                "SELECT DISTINCT d.professor_cd,p.user_name,home.department_name "
                + "FROM department d JOIN professor_info p ON p.professor_cd=d.professor_cd "
                + "JOIN department home ON home.department_cd=p.department_cd "
                + "ORDER BY p.user_name,d.professor_cd");
             ResultSet rows = statement.executeQuery()) {
            while (rows.next()) searchChoice.addItem(new ProfessorOption(rows.getString(1),
                    rows.getString(2) + " · " + rows.getString(3)));
        } catch (SQLException exception) {
            showError("담당교수 검색 항목 오류", exception);
        }
        searchChoice.setSelectedIndex(-1);
    }

    private void search() {
        String column = SEARCH_COLUMNS[searchCategory.getSelectedIndex()];
        if (column.isEmpty()) {
            JOptionPane.showMessageDialog(this, "카테고리를 선택해주세요.");
            return;
        }
        boolean professorSearch = "d.professor_cd".equals(column);
        ProfessorOption selectedProfessor = professorSearch
                ? (ProfessorOption) searchChoice.getSelectedItem() : null;
        String keyword = professorSearch ? null : searchField.getText().trim();
        if (professorSearch ? selectedProfessor == null : keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, professorSearch
                    ? "담당교수를 선택해주세요." : "검색어를 입력해주세요.");
            if (!professorSearch) searchField.requestFocus();
            return;
        }
        String condition = professorSearch
                ? (selectedProfessor.code() == null ? "d.professor_cd IS NULL" : "d.professor_cd=?")
                : column + " LIKE ?";
        model.setRowCount(0);
        showEditPanel(false);
        try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(
                SELECT_DEPARTMENTS + " WHERE " + condition)) {
            if (!professorSearch || selectedProfessor.code() != null) {
                statement.setString(1, professorSearch ? selectedProfessor.code() : "%" + keyword + "%");
            }
            try (ResultSet rows = statement.executeQuery()) {
                while (rows.next()) addRow(rows);
            }
            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "검색 결과가 없습니다.");
            }
        } catch (SQLException exception) {
            showError("학과 검색 오류", exception);
        }
    }

    String nextDepartmentCode() throws SQLException {
        int highest = 0;
        try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(
                "SELECT department_cd FROM department");
             ResultSet rows = statement.executeQuery()) {
            while (rows.next()) {
                String code = rows.getString(1);
                if (code != null && code.matches("(?i)d[0-9]+")) {
                    highest = Math.max(highest, Integer.parseInt(code.substring(1)));
                }
            }
        }
        if (highest >= 999) throw new SQLException("학과코드를 더 생성할 수 없습니다.");
        return String.format("d%03d", highest + 1);
    }

    boolean registerDepartment(String code, String name, String major, ProfessorOption professor) {
        try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(
                "INSERT INTO department(department_cd,department_name,major_name,professor_cd) "
                + "VALUES(?,?,?,?)")) {
            statement.setString(1, code);
            statement.setString(2, name.trim());
            statement.setString(3, major.trim());
            statement.setString(4, professorCode(professor));
            statement.executeUpdate();
            loadAll();
            return true;
        } catch (SQLException exception) {
            String message = exception.getErrorCode() == 1062
                    ? "이미 등록된 학과코드입니다." : exception.getMessage();
            JOptionPane.showMessageDialog(getDialogOwnerFrame(), message,
                    "학과 등록 오류", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void updateSelected() {
        if (table.getSelectedRow() < 0) return;
        try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(
                "UPDATE department SET department_name=?,major_name=?,professor_cd=? "
                + "WHERE department_cd=?")) {
            statement.setString(1, nameField.getText().trim());
            statement.setString(2, majorField.getText().trim());
            statement.setString(3, professorCode((ProfessorOption) professorField.getSelectedItem()));
            statement.setString(4, codeField.getText());
            if (statement.executeUpdate() == 1) {
                loadAll();
                JOptionPane.showMessageDialog(this, "수정이 완료 되었습니다.");
            }
        } catch (SQLException exception) {
            showError("학과 수정 오류", exception);
        }
    }

    private void deleteSelected() {
        if (table.getSelectedRow() < 0) return;
        int answer = JOptionPane.showOptionDialog(this, "선택한 학과를 삭제하시겠습니까?",
                "학과 삭제", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE,
                null, new Object[]{"예", "아니오"}, "아니오");
        if (answer != JOptionPane.YES_OPTION) return;
        try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(
                "DELETE FROM department WHERE department_cd=?")) {
            statement.setString(1, codeField.getText());
            if (statement.executeUpdate() == 1) {
                loadAll();
                JOptionPane.showMessageDialog(this, "삭제되었습니다.");
            }
        } catch (SQLException exception) {
            JOptionPane.showMessageDialog(this,
                    "학생·교수·강좌 등에 연결된 학과는 삭제할 수 없습니다.\n" + exception.getMessage(),
                    "학과 삭제 제한", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void showError(String title, SQLException exception) {
        JOptionPane.showMessageDialog(this, exception.getMessage(), title, JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) throws SQLException {
		Login.main(args);
    }
}
