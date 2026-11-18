package 학사관리프로젝트;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Calculates the course total and grade from four equally weighted score components. */
public final class GradeCalculator {
    private static final BigDecimal NINETY = BigDecimal.valueOf(90);
    private static final BigDecimal EIGHTY = BigDecimal.valueOf(80);
    private static final BigDecimal SEVENTY = BigDecimal.valueOf(70);
    private static final BigDecimal SIXTY = BigDecimal.valueOf(60);

    private GradeCalculator() {
    }

    public static BigDecimal average(int attendance, int assignment, int midterm, int finalExam) {
        return BigDecimal.valueOf(total(attendance, assignment, midterm, finalExam))
                .divide(BigDecimal.valueOf(4), 1, RoundingMode.HALF_UP);
    }

    public static int total(int attendance, int assignment, int midterm, int finalExam) {
        int[] scores = {attendance, assignment, midterm, finalExam};
        int total = 0;
        for (int score : scores) {
            if (score < 0 || score > 100) {
                throw new IllegalArgumentException("각 항목 점수는 0점부터 100점까지 입력하세요.");
            }
            total += score;
        }
        return total;
    }

    public static String letter(BigDecimal average) {
        if (average.compareTo(NINETY) >= 0) return "A";
        if (average.compareTo(EIGHTY) >= 0) return "B";
        if (average.compareTo(SEVENTY) >= 0) return "C";
        if (average.compareTo(SIXTY) >= 0) return "D";
        return "F";
    }

    public static BigDecimal points(BigDecimal average) {
        switch (letter(average)) {
            case "A": return BigDecimal.valueOf(4.0).setScale(1);
            case "B": return BigDecimal.valueOf(3.0).setScale(1);
            case "C": return BigDecimal.valueOf(2.0).setScale(1);
            case "D": return BigDecimal.valueOf(1.0).setScale(1);
            default: return BigDecimal.ZERO.setScale(1);
        }
    }
}


