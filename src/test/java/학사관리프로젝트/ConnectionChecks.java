package 학사관리프로젝트;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.ArrayList;
import java.util.List;

/** Integration checks against a configured MySQL DB; no persistent data is changed. */
public final class ConnectionChecks {
    private static void check(boolean condition, String description) {
        if (!condition) throw new AssertionError(description);
    }

    private static long id(Connection connection) throws Exception {
        try (Statement statement = connection.createStatement();
                ResultSet rows = statement.executeQuery("SELECT CONNECTION_ID()")) {
            rows.next(); return rows.getLong(1);
        }
    }

    public static void main(String[] args) throws Exception {
        try {
            ExecutorService executor = Executors.newFixedThreadPool(8);
            Connection first;
            try {
                List<Future<Connection>> calls = new ArrayList<>();
                for (int i = 0; i < 32; i++) calls.add(executor.submit(DatabaseConnection::getConnection));
                first = calls.get(0).get();
                for (Future<Connection> call : calls) check(call.get() == first, "Concurrent initialization shares one connection");
            } finally { executor.shutdownNow(); }
            long originalId = id(first);
            for (int i = 0; i < 100; i++) {
                Connection next = DatabaseConnection.getConnection();
                check(next == first && id(next) == originalId, "Repeated screen access keeps the same DB session");
            }
            check(first.getAutoCommit(), "Initial auto-commit");
            check(CourseRegistrationService.enroll(first, "__missing_student__", "__missing_course__")
                    == CourseRegistrationService.ResultCode.COURSE_NOT_FOUND, "Transaction rejection path");
            check(first.getAutoCommit(), "Transaction restores auto-commit");
            check(id(DatabaseConnection.getConnection()) == originalId, "Transaction does not open another connection");
            first.close();
            Connection reopened = DatabaseConnection.getConnection();
            check(reopened != first && id(reopened) != originalId, "Closed connection is replaced");
            reopened.setAutoCommit(false);
            try (Statement statement = reopened.createStatement(); ResultSet rows = statement.executeQuery("SELECT 1")) {
                check(rows.next(), "Connection usable in transaction");
            }
            DatabaseConnection.closeAll();
            check(reopened.isClosed(), "Shutdown rolls back and closes the shared connection");
            DatabaseConnection.closeAll();
            Connection fresh = DatabaseConnection.getConnection();
            check(fresh != reopened && fresh.getAutoCommit(), "Reopening after shutdown is clean");
            System.out.println("Shared connection, 100 repeated calls, concurrent initialization, transaction and shutdown checks passed");
        } finally { DatabaseConnection.closeAll(); }
    }
}
