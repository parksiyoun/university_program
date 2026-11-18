package 학사관리프로젝트;

import java.awt.Font;
import java.awt.Frame;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;

/** Role-specific navigation after successful login. */
public class UniversityMenu extends UniversityFrame {
    private final String userId;
    private final String userName;
    private final String accessLevel;
    private String relatedCode;
    private final Font menuFont = UiFonts.font(Font.PLAIN, 13);
    private final JPanel homePanel = new JPanel();
    private final Dimension homeSize = new Dimension(1400, 800);
    private UniversityFrame activeScreen;
    private boolean normalSizeCorrectionPending;

    public UniversityMenu(String userId, String userName, String accessLevel) throws SQLException {
        this.userId = userId;
        this.userName = userName;
        this.accessLevel = accessLevel;
        setContentPane(homePanel);
        UniversityFrame.setEmbeddingHost(this);
        setTitle("학사관리시스템 : " + userName + " (" + accessLevel + ")");
        setResizable(true);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent event) {
                confirmProgramExit();
            }
        });
        if ("student".equals(accessLevel)) relatedCode = lookupCode("SELECT student_cd FROM student_info WHERE user_id=?");
        else if ("professor".equals(accessLevel)) relatedCode = lookupCode("SELECT professor_cd FROM professor_info WHERE user_id=?");
        buildMenu();
        homeSize.setSize(1400, 800 + getJMenuBar().getPreferredSize().height);
        setSize(homeSize);
        setLocationRelativeTo(null);
        setVisible(true);
        addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent event) {
                enforceNormalSize();
            }
        });
        addWindowStateListener(event -> enforceNormalSize());
    }

    private void enforceNormalSize() {
        if (normalSizeCorrectionPending) return;
        normalSizeCorrectionPending = true;
        SwingUtilities.invokeLater(() -> {
            normalSizeCorrectionPending = false;
            if (!isDisplayable() || (getExtendedState() & Frame.MAXIMIZED_BOTH) != 0) return;
            if (!getSize().equals(homeSize)) setSize(homeSize);
        });
    }

    void showEmbeddedScreen(UniversityFrame screen) {
        if (screen == this) return;

        if (activeScreen != null && activeScreen != screen) {
            activeScreen.releaseFromHost();
        }

        Dimension designSize = screen.getSize();
        Container content = screen.takeContentForEmbedding();
        screen.attachToHost(this);
        activeScreen = screen;
        setContentPane(content.getLayout() == null
                ? new ResponsiveContent(content, designSize.width, designSize.height)
                : content);

        revalidate();
        repaint();
    }

    void returnToMenu(UniversityFrame screen) {
        if (activeScreen == screen) {
            activeScreen = null;
            setContentPane(homePanel);
            revalidate();
            repaint();
        }
        screen.releaseFromHost();
    }

    void releaseActiveScreenForExit() {
        if (activeScreen != null) {
            activeScreen.releaseFromHost();
            activeScreen = null;
        }
    }
    private String lookupCode(String sql) throws SQLException {
        try (PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(sql)) {
            statement.setString(1, userId);
            try (ResultSet rows = statement.executeQuery()) {
                if (rows.next()) return rows.getString(1);
            }
        }
        throw new SQLException("로그인 계정에 연결된 사용자 정보가 없습니다.");
    }

    private void buildMenu() {
        JMenuBar bar = new JMenuBar();
        if ("admin".equals(accessLevel)) {
            JMenu userInfoMenu = new JMenu("사용자 정보 관리");
            userInfoMenu.setFont(menuFont);
            addMenuItem(userInfoMenu, "교수 정보", () -> new ProfessorManagement());
            addMenuItem(userInfoMenu, "학생 정보", () -> new StudentManagement());
            addMenuItem(userInfoMenu, "관리자 정보", () -> new AdminInformation());
            addMenuItem(userInfoMenu, "내 정보", () -> new AccountSettings(userId, accessLevel));
            bar.add(userInfoMenu);
            JMenu classMenu = new JMenu("수업 및 수강 관리");
            classMenu.setFont(menuFont);
            addMenuItem(classMenu, "학과 관리", () -> new DepartmentManagement());
            addMenuItem(classMenu, "강좌 개설 관리", () -> new SubjectManagement());
            bar.add(classMenu);
            addMenu(bar, "통계", "학생·학과 평균 및 등수", () -> new AcademicStatistics());
        } else if ("professor".equals(accessLevel)) {
            addMenu(bar, "성적관리", "성적 입력·조회", () -> new ProfessorGradeManagement(relatedCode));
            addMenu(bar, "통계", "내 강좌 성적 통계", () -> new ProfessorStatistics(relatedCode));
            addMenu(bar, "계정", "내 정보", () -> new AccountSettings(userId, accessLevel));
        } else if ("student".equals(accessLevel)) {
            addMenu(bar, "수강", "수강 신청", () -> new EnrollmentManagement(relatedCode));
            addMenu(bar, "성적", "성적 조회", () -> new StudentGradeInquiry(relatedCode));
            addMenu(bar, "계정", "내 정보", () -> new AccountSettings(userId, accessLevel));
        }
        addLogoutItem(bar);
        setJMenuBar(bar);
    }

    private void addLogoutItem(JMenuBar bar) {
        JMenu logoutMenu = new JMenu("로그아웃") {
            private boolean handlingSelection;

            @Override
            public void setPopupMenuVisible(boolean visible) {
                if (!visible) {
                    super.setPopupMenuVisible(false);
                    return;
                }


                if (handlingSelection) return;
                handlingSelection = true;
                SwingUtilities.invokeLater(() -> {
                    try {
                        logout();
                    } catch (Exception exception) {
                        JOptionPane.showMessageDialog(UniversityMenu.this,
                                "로그아웃 중 오류가 발생했습니다.\n" + exception.getMessage(),
                                "오류", JOptionPane.ERROR_MESSAGE);
                    } finally {
                        handlingSelection = false;
                    }
                });
            }
        };
        logoutMenu.setFont(menuFont);
        bar.add(logoutMenu);
    }
    private void logout() throws SQLException {
        Object[] options = {"예", "아니오"};
        int choice = JOptionPane.showOptionDialog(this,
                "로그아웃 하시겠습니까?",
                "로그아웃 확인",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1]);

        if (choice != 0) {
            MenuSelectionManager.defaultManager().clearSelectedPath();
            return;
        }

        MenuSelectionManager.defaultManager().clearSelectedPath();
        JOptionPane.showMessageDialog(this, "로그아웃 되었습니다.", "로그아웃", JOptionPane.INFORMATION_MESSAGE);
        UniversityFrame.setEmbeddingHost(null);
        if (activeScreen != null) {
            activeScreen.releaseFromHost();
            activeScreen = null;
        }
        Login login = new Login();
        for (Window window : Window.getWindows()) {
            if (window != login) window.dispose();
        }
    }
    private void addMenu(JMenuBar bar, String title, String itemTitle, WindowAction action) {
        JMenu menu = new JMenu(title);
        menu.setFont(menuFont);
        addMenuItem(menu, itemTitle, action);
        bar.add(menu);
    }

    private void addMenuItem(JMenu menu, String itemTitle, WindowAction action) {
        JMenuItem item = new JMenuItem(itemTitle);
        item.setFont(menuFont);
        item.addActionListener(event -> {
            try { action.open(); }
            catch (Exception exception) {
                JOptionPane.showMessageDialog(this, "화면을 여는 중 오류가 발생했습니다.\n" + exception.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
            }
        });
        menu.add(item);
    }
    @FunctionalInterface private interface WindowAction { void open() throws Exception; }

    /** Keeps older IntelliJ run configurations working while still requiring login. */
    public static void main(String[] args) {
        Login.main(args);
    }
}


















