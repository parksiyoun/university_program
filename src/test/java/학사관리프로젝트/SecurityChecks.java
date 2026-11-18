package 학사관리프로젝트;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Standalone checks requiring no DB or test dependencies. */
public final class SecurityChecks {
    private static void check(boolean value, String description) {
        if (!value) throw new AssertionError(description);
    }
    private static void invalid(String value) {
        try { Passwords.hash(value.toCharArray()); throw new AssertionError("Invalid password accepted"); }
        catch (IllegalArgumentException expected) { }
    }
    public static void main(String[] args) throws Exception {
        if (args.length > 0 && "config".equals(args[0])) {
            try { DatabaseConnection.getConnection(); throw new AssertionError("Unsafe configuration accepted"); }
            catch (SQLException expected) {
                check(expected.getMessage().contains("전용 DB 계정") || expected.getMessage().contains("root 대신"), "Reject before connecting");
                System.out.println("DB configuration guard passed"); return;
            }
        }
        char[] password = "Example1234!".toCharArray();
        String first = Passwords.hash(password);
        check(Passwords.verify(password, first), "Correct password");
        check(!Passwords.verify("incorrect-password".toCharArray(), first), "Incorrect password");
        check(!first.equals(Passwords.hash(password)), "Independent salts");
        for (String malformed : new String[]{"plaintext", "", "pbkdf2-sha256$0$x$x", "pbkdf2-sha256$2147483647$x$x", "pbkdf2-sha256$600000$!$!"})
            check(!Passwords.verify(password, malformed), "Malformed hash rejected");
        invalid(""); invalid("123456"); invalid(" ".repeat(12)); invalid("x".repeat(11)); invalid("x".repeat(13));
        check(Passwords.verify("x".repeat(12).toCharArray(), Passwords.hash("x".repeat(12).toCharArray())), "12 characters");
        String historical = "x".repeat(128);
        check(Passwords.verify(historical.toCharArray(), Passwords.hashExisting(historical.toCharArray())), "Existing longer passwords remain valid");
        javax.swing.JPasswordField field = new javax.swing.JPasswordField();
        Passwords.limitNewPassword(field);
        field.setText("x".repeat(12));
        field.getDocument().insertString(12, "x", null);
        check(field.getPassword().length == 12, "Typing cannot exceed 12");
        field.selectAll(); field.replaceSelection("y".repeat(13));
        check(new String(field.getPassword()).equals("x".repeat(12)), "Overlong paste rejected without truncation");
        field.selectAll(); field.replaceSelection("y".repeat(12));
        check(new String(field.getPassword()).equals("y".repeat(12)), "12-character replacement accepted");
        check(Passwords.verify("760201".toCharArray(), Passwords.hashExisting("760201".toCharArray())), "Historical password retained");
        Set<String> temporary = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            String issued = Passwords.temporary(); Passwords.validate(issued.toCharArray());
            check(temporary.add(issued), "Unique temporary password");
        }
        String sql = Files.readString(Path.of("database/university_full.sql"));
        Matcher seeds = Pattern.compile("\\('([aps]\\d+)','(pbkdf2-sha256[^']+)','(?:admin|student|professor)','[^']+','(\\d{4}-\\d{2}-\\d{2})'\\)").matcher(sql);
        Set<String> salts = new HashSet<>(); int count = 0;
        while (seeds.find()) {
            check(Passwords.verify(seeds.group(3).substring(2).replace("-", "").toCharArray(), seeds.group(2)), "Seed compatibility: " + seeds.group(1));
            check(salts.add(seeds.group(2).split("\\$")[2]), "Distinct seed salts"); count++;
        }
        check(count == 30, "All 30 sample accounts converted");
        check(sql.contains("password_change_required` boolean NOT NULL DEFAULT TRUE"), "First-login reset default");
        for (String entry : new String[]{"DatabaseConnection", "UniversityMenu", "ProfessorManagement", "StudentManagement", "SubjectManagement", "DepartmentManagement", "ProfessorGradeManagement", "StudentGradeInquiry", "EnrollmentManagement"}) {
            String source = Files.readString(Path.of("src/main/java/학사관리프로젝트/" + entry + ".java"));
            check(source.substring(source.indexOf("public static void main")).contains("Login.main(args);"), "Login entry: " + entry);
        }
        System.out.println("Password, migration compatibility, seed and entry-point checks passed");
    }
}
