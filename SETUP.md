# 실행 및 DB 설정

## 실행 준비

1. JDK 11 이상과 Maven을 준비합니다.
2. IntelliJ에서 pom.xml이 있는 프로젝트 폴더를 Maven 프로젝트로 엽니다. Maven이 MySQL Connector/J 의존성을 자동으로 내려받습니다.
3. MySQL에서 `university` 데이터베이스를 선택하고 `database/university_full.sql`로 새 DB의 테이블과 샘플 데이터를 준비합니다. 이 파일은 기존 테이블을 삭제하므로 이미 사용 중인 DB에는 재실행하지 않습니다.
4. 아래 절차로 전용 DB 계정을 만들고 실행 환경에 접속 정보를 설정합니다.

UNIVERSITY_DB_USER=university_app
UNIVERSITY_DB_PASSWORD=직접 발급받은 DB 계정 비밀번호

기본 접속 주소는 jdbc:mysql://localhost:3306/university입니다. 다른 주소를 쓰면 UNIVERSITY_DB_URL도 설정할 수 있습니다. DB 비밀번호를 Java 코드나 GitHub 저장소에 넣지 마세요.

로그인 화면 시작 클래스는 `학사관리프로젝트.Login`입니다. 각 화면의 예전 실행 진입점도 로그인 화면으로 연결됩니다.

샘플 관리자 ID는 `a101`이며 데모 초기 비밀번호는 `760201`입니다. 샘플 데이터는 기존 데모 비밀번호의 PBKDF2 해시를 저장하고, 첫 로그인에서 반드시 새 비밀번호를 설정하게 합니다. 신규 학생·교수 계정에는 무작위 임시 비밀번호를 발급하여 등록 직후 한 번 표시합니다. 첫 로그인에서 변경을 완료해야 메뉴가 열립니다.

비밀번호는 PBKDF2-HMAC-SHA256(600,000회, 계정별 무작위 16바이트 salt)으로 저장합니다. 새 비밀번호와 신규 임시 비밀번호는 정확히 12자이며 현재 비밀번호 확인 및 재입력 확인을 거쳐 변경합니다. 새 비밀번호 입력칸은 12자를 초과한 입력을 거부합니다. 기존 비밀번호는 길이 변경으로 무효화하지 않습니다. 평문 저장이나 평문 로그인 비교는 허용하지 않습니다.

## 전용 DB 계정

MySQL 8의 관리자 연결에서 아래를 한 번 실행합니다. `IDENTIFIED BY RANDOM PASSWORD`가 반환한 비밀번호를 IntelliJ 실행 설정의 환경 변수에 입력하고 소스에 저장하지 않습니다.

```sql
CREATE USER 'university_app'@'localhost' IDENTIFIED BY RANDOM PASSWORD;
GRANT SELECT, INSERT, UPDATE, DELETE ON university.* TO 'university_app'@'localhost';
SHOW GRANTS FOR 'university_app'@'localhost';
```

새 전용 계정에는 다른 DB 접근, CREATE/ALTER/DROP, 사용자 관리 및 GRANT 권한을 부여하지 않습니다. 이미 같은 이름의 계정이 있다면 관리자에게 기존 권한을 확인한 뒤 사용합니다. 앱은 root 계정과 누락된 접속 정보를 거부합니다. 스키마 생성·변경은 관리자 연결에서만 수행합니다.

## 이미 만들어 둔 DB 전환

앱을 종료한 상태에서 관리자 연결로 다음을 한 번 실행합니다. 기존 테이블과 학사 데이터는 유지합니다.

```sql
USE university;
ALTER TABLE admin_settings
  MODIFY user_pw VARCHAR(255) NOT NULL,
  ADD COLUMN password_change_required BOOLEAN NOT NULL DEFAULT TRUE;
```

위 전용 계정의 환경 변수를 설정한 IntelliJ 실행 설정에서 `학사관리프로젝트.PasswordMigration`을 `--hash-existing` 인자로 한 번 실행합니다. 기존 평문 비밀번호의 값은 유지하고 저장 방식만 해시로 바꿉니다. 비밀번호와 해시는 출력하지 않으며 트랜잭션 실패 시 전체를 롤백합니다. 이미 변환한 계정은 건너뜁니다. 그다음 `Login`을 실행하여 기존 비밀번호로 로그인하고 새 비밀번호를 설정합니다. 업데이트된 전체 SQL로 새 DB를 만들었다면 이 전환 절차는 필요 없습니다.

이 프로그램은 로컬 포트폴리오 데모입니다. DB 접속 권한을 가진 PC 사용자는 DB에 직접 접근할 수 있으며, 공통 DB 계정으로 연결하는 구조에서는 학생·교수별 권한을 DB 수준에서 격리하지 않습니다.

## 보안 검사 실행

프로젝트 루트에서 다음을 실행합니다. `SecurityChecks`는 DB에 접속하지 않고 해시 검증, salt 분리, 입력 길이, 임시 비밀번호, 기존 비밀번호 변환 호환성, 샘플 계정 30개의 해시, 로그인 진입점을 검사하는 독립 실행형 검사입니다. Maven의 기본 테스트 실행과 별도로 실행합니다.

```powershell
mvn test-compile
java -cp "target/classes;target/test-classes" 학사관리프로젝트.SecurityChecks
```

PBKDF2 설정 참고: [OWASP Password Storage Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Password_Storage_Cheat_Sheet.html#pbkdf2).

## DB 연결 관리 검사

화면과 로그아웃은 하나의 공유 연결을 재사용하며, 연결이 닫혔으면 다음 접근에서 다시 연결합니다. 프로그램 종료 시 미완료 트랜잭션을 롤백하고 연결을 닫습니다. 쿼리의 Statement와 ResultSet은 각 사용 지점에서 닫습니다.

`ConnectionChecks`는 위 전용 DB 계정 환경 변수와 MySQL Connector/J가 포함된 클래스패스로 실행합니다. IntelliJ에서 `src/test/java`의 해당 클래스를 실행하고 Login과 같은 환경 변수를 설정하면 됩니다. 동시 초기화, 100회 반복 접근, 닫힌 연결 재접속, 수강 신청 거부 후 자동 커밋 복구, 종료 시 연결 해제를 검사하며 실제 학사 데이터는 변경하지 않습니다.

