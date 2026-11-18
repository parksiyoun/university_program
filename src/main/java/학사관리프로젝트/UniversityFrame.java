package 학사관리프로젝트;

import java.awt.Container;
import java.awt.Window;
import java.sql.SQLException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/** Base frame that applies bundled UI settings and can display feature screens in the main window. */
public class UniversityFrame extends JFrame {
    private static UniversityMenu embeddingHost;
    private UniversityMenu embeddedOwner;

    static {
        UiFonts.applyDefaults();
    }

    protected UniversityFrame() {
        super();
        setResizable(false);
    }

    protected UniversityFrame(String title) {
        super(title);
        setResizable(false);
    }

    static void setEmbeddingHost(UniversityMenu host) {
        embeddingHost = host;
    }

    @Override
    public void setVisible(boolean visible) {
        UniversityMenu host = embeddingHost;
        if (visible && host != null && this != host) {
            host.showEmbeddedScreen(this);
            return;
        }
        super.setVisible(visible);
    }

    @Override
    public void dispose() {
        if (embeddedOwner != null) {
            embeddedOwner.returnToMenu(this);
            return;
        }
        super.dispose();
    }

    final void attachToHost(UniversityMenu host) {
        embeddedOwner = host;
    }

    final void releaseFromHost() {
        embeddedOwner = null;
        super.dispose();
    }

    final JFrame getDialogOwnerFrame() {
        return embeddedOwner == null ? this : embeddedOwner;
    }

    protected final void confirmProgramExit() {
        JFrame owner = getDialogOwnerFrame();
        Object[] options = {"예", "아니오"};
        int choice = JOptionPane.showOptionDialog(owner,
                "프로그램을 종료하시겠습니까?", "종료 확인",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options, options[1]);
        if (choice != JOptionPane.YES_OPTION) return;

        try {
            DatabaseConnection.closeAll();
        } catch (SQLException exception) {
            JOptionPane.showMessageDialog(owner,
                    "데이터베이스 연결을 정리하는 중 오류가 발생했습니다. 프로그램을 종료합니다.\n"
                            + exception.getMessage(),
                    "종료 알림", JOptionPane.WARNING_MESSAGE);
        }
        setEmbeddingHost(null);
        for (Window window : Window.getWindows()) {
            if (window instanceof UniversityMenu) ((UniversityMenu) window).releaseActiveScreenForExit();
        }
        for (Window window : Window.getWindows()) window.dispose();
        System.exit(0);
    }

    final Container takeContentForEmbedding() {
        Container content = getContentPane();
        setContentPane(new JPanel());
        return content;
    }
}

