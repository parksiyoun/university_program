package 학사관리프로젝트;

import java.time.LocalTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class ClassSchedule {
    private static final Pattern STORED = Pattern.compile(
            "^([월화수목금])\\s+([0-2]?[0-9]:[0-5][0-9])\\s*[-~]\\s*([0-2]?[0-9]:[0-5][0-9])$");
    private static final Pattern TIME = Pattern.compile("(?:[01][0-9]|2[0-3]):[0-5][0-9]");

    final String day;
    final String start;
    final String end;

    private ClassSchedule(String day, String start, String end) {
        this.day = day;
        this.start = start;
        this.end = end;
    }

    static ClassSchedule create(String day, String start, String end) {
        if (day == null || !day.matches("[월화수목금]")) {
            throw new IllegalArgumentException("요일을 선택하세요.");
        }
        if (start == null || !TIME.matcher(start.trim()).matches()) {
            throw new IllegalArgumentException("시작시간을 HH:mm 형식으로 입력하세요. 예: 09:00");
        }
        if (end == null || !TIME.matcher(end.trim()).matches()) {
            throw new IllegalArgumentException("종료시간을 HH:mm 형식으로 입력하세요. 예: 12:00");
        }
        String startValue = start.trim();
        String endValue = end.trim();
        if (!LocalTime.parse(startValue).isBefore(LocalTime.parse(endValue))) {
            throw new IllegalArgumentException("종료시간은 시작시간보다 늦어야 합니다.");
        }
        return new ClassSchedule(day, startValue, endValue);
    }

    static ClassSchedule parseStored(String stored) {
        if (stored == null) return null;
        Matcher match = STORED.matcher(stored.trim());
        if (!match.matches()) return null;
        try {
            String start = match.group(2);
            String end = match.group(3);
            return create(match.group(1), start.length() == 4 ? "0" + start : start,
                    end.length() == 4 ? "0" + end : end);
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    @Override
    public String toString() {
        return day + " " + start + "~" + end;
    }
}
