package 학사관리프로젝트;

final class ProfessorOption {
    private final String code;
    private final String name;

    ProfessorOption(String code, String name) {
        this.code = code;
        this.name = name;
    }

    String code() {
        return code;
    }

    @Override
    public String toString() {
        return name;
    }
}
