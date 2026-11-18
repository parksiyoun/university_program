package 학사관리프로젝트;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/** Versioned PBKDF2 hashes with a fresh salt for each password. */
final class Passwords {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int ITERATIONS = 600_000;
    private Passwords() { }

    static void validate(char[] password) {
        if (password.length != 12)
            throw new IllegalArgumentException("비밀번호는 정확히 12자로 입력하세요.");
        boolean nonWhitespace = false;
        for (char c : password) if (!Character.isWhitespace(c)) nonWhitespace = true;
        if (!nonWhitespace) throw new IllegalArgumentException("공백만으로 비밀번호를 만들 수 없습니다.");
    }

    static String temporary() {
        byte[] bytes = new byte[9];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    static String hash(char[] password) {
        validate(password);
        return hashExisting(password);
    }

    static void limitNewPassword(javax.swing.JPasswordField field) {
        ((javax.swing.text.AbstractDocument) field.getDocument()).setDocumentFilter(new javax.swing.text.DocumentFilter() {
            @Override public void insertString(FilterBypass bypass, int offset, String text,
                    javax.swing.text.AttributeSet attributes) throws javax.swing.text.BadLocationException {
                replace(bypass, offset, 0, text, attributes);
            }
            @Override public void replace(FilterBypass bypass, int offset, int length, String text,
                    javax.swing.text.AttributeSet attributes) throws javax.swing.text.BadLocationException {
                String replacement = text == null ? "" : text;
                if (bypass.getDocument().getLength() - length + replacement.length() <= 12)
                    bypass.replace(offset, length, replacement, attributes);
            }
        });
    }

    /** Preserves historical short passwords only during explicit offline migration. */
    static String hashExisting(char[] password) {
        if (password.length == 0 || password.length > 128) throw new IllegalArgumentException("기존 비밀번호 길이가 올바르지 않습니다.");
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        return "pbkdf2-sha256$" + ITERATIONS + "$" + Base64.getEncoder().encodeToString(salt)
                + "$" + Base64.getEncoder().encodeToString(derive(password, salt, ITERATIONS));
    }

    static boolean verify(char[] password, String stored) {
        if (stored == null || password.length > 128) return false;
        try {
            String[] parts = stored.split("\\$", -1);
            if (parts.length != 4 || !"pbkdf2-sha256".equals(parts[0])) return false;
            int rounds = Integer.parseInt(parts[1]);
            if (rounds < ITERATIONS || rounds > 2_000_000) return false;
            byte[] salt = Base64.getDecoder().decode(parts[2]);
            byte[] expected = Base64.getDecoder().decode(parts[3]);
            if (salt.length != 16 || expected.length != 32) return false;
            byte[] actual = derive(password, salt, rounds);
            try { return MessageDigest.isEqual(expected, actual); }
            finally { Arrays.fill(actual, (byte) 0); }
        } catch (IllegalArgumentException exception) { return false; }
    }

    private static byte[] derive(char[] password, byte[] salt, int rounds) {
        PBEKeySpec spec = new PBEKeySpec(password, salt, rounds, 256);
        try { return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded(); }
        catch (GeneralSecurityException exception) { throw new IllegalStateException("비밀번호 해시를 생성할 수 없습니다.", exception); }
        finally { spec.clearPassword(); }
    }
}
