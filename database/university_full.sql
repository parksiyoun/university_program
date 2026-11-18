-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: localhost    Database: university
-- ------------------------------------------------------
-- Server version	8.0.46

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `admin_settings`
--

DROP TABLE IF EXISTS `admin_settings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `admin_settings` (
  `user_id` varchar(20) NOT NULL,
  `user_pw` varchar(255) NOT NULL,
  `password_change_required` boolean NOT NULL DEFAULT TRUE,
  `access_level` varchar(10) NOT NULL,
  `user_name` varchar(30) NOT NULL,
  `birth_date` date NOT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin_settings`
--

LOCK TABLES `admin_settings` WRITE;
/*!40000 ALTER TABLE `admin_settings` DISABLE KEYS */;
INSERT INTO `admin_settings` (user_id,user_pw,access_level,user_name,birth_date) VALUES ('a101','pbkdf2-sha256$600000$JuV5s9vd4yd4C3iZXsIW+A==$k5nBZ1EximEGUPjpE9I71uh06yhuSD2JJ7D6hwrije0=','admin','김민수','1976-02-01'),('a102','pbkdf2-sha256$600000$S17NrqPb+LzOjHvYa4JV9g==$409B4X9KedRp+zUcztUNrglZlueRIoftq4qXnoIZjlQ=','admin','이서연','1973-05-08'),('p101','pbkdf2-sha256$600000$CGsBY/1BZyEeRDNAug+MYw==$XPuK6jnooxv09oUmNxYOFgHXquUgj6lgrOV840aUDmc=','professor','박지훈','1979-03-24'),('p102','pbkdf2-sha256$600000$OY2DE+DnHUDEOH1UxCCMjw==$NvLN+T1QAudcNJDdO9JOZB9muM+3rHMDEhnKS1RFeto=','professor','최유진','1983-11-24'),('p103','pbkdf2-sha256$600000$yNC368PyWx6gJn3gTRyOPw==$CGrpJbyCLJYeOZx6aKUVX0ctmX1dtap7RYle+nLjeL0=','professor','정현우','1969-02-19'),('p104','pbkdf2-sha256$600000$UY3slP7nR1jWN6S0ETrzsQ==$O4MEJtymJ1Co2AI/Zk2YyR40z2z3icHK2yg6l8CVKJc=','professor','강민지','1973-01-01'),('p105','pbkdf2-sha256$600000$sdAZLhVJ3qdBtz8dFLS30g==$fpTyITCYg8lvqZygepB2q37MBQ9VpsESVneQzJTaUAA=','professor','윤서준','1984-04-08'),('s101','pbkdf2-sha256$600000$RYRx1NCwPnqVbVs3ps7yFw==$hNFe7wZncL0gJMX24RaXPpa522YG5p0gKd7rQUpZeBc=','student','한지우','1999-10-01'),('s102','pbkdf2-sha256$600000$sin6cqYhYmkV15Re3A49JA==$S3bYNzXiJa0ac/1n4BfJytwKhfDFAe8uxtoavH/538M=','student','오수빈','1999-04-23'),('s103','pbkdf2-sha256$600000$/0RkBK4XcC/+uX65EPvG8A==$1ijKjkzyY4vjuRBNzvLOnAKkP+paYkJonmJik4K1pjQ=','student','임도윤','1997-12-18'),('s104','pbkdf2-sha256$600000$gD72KPiWKgEux32DVe+3lQ==$Y4QTeQEA65pf5e8+HgoqdZ4lirHHbl1lwBY7Z69JJeM=','student','조하은','2001-04-15'),('s105','pbkdf2-sha256$600000$HkDileJK9fFzlpROyDI8rg==$7hu+MXt9EWUS3dG/QMyCjAqTxa9DPiDvyjKyvpVK82c=','student','장예준','1998-05-26'),('s106','pbkdf2-sha256$600000$DEVPwtEcWSjgfPhXzt30sw==$viGEJoYvg1Vk2gk97i2MzpoXyLMv/xjNsGPcINEEHmI=','student','신유나','2007-03-23'),('s107','pbkdf2-sha256$600000$GnFGVJVn3itS7CPX1fWjvw==$g6z/R4ugcb8ymhDZ5oxej0IyZnZHBkPakVQWUWSA+Us=','student','서준호','2001-06-09'),('s108','pbkdf2-sha256$600000$0blmH45YbymkLeyKD9FeVw==$NrCBCHv1/78mLBnmwPHC9AS5i18ovWUIdmZ5Qs2Z/Pk=','student','권서현','2005-04-25'),('s109','pbkdf2-sha256$600000$cCGsokWfRlL7aWmDpucUeQ==$k05D6pKUT15ARHyc7ye4V9ykhceCrXhTb2LUjhlk+i8=','student','황지호','2002-02-03'),('s110','pbkdf2-sha256$600000$63eTgsADFUd36Tnt9xJCVQ==$mzN/fpddymR8LzMeG5v6AJQQFUHA7N374Jex9SYmX1U=','student','안유진','2001-02-12'),('s111','pbkdf2-sha256$600000$wNU1lL02IynSclfv1+1LSw==$99+dFohNN0vjpGNiNae8KCwB4Wv0Zkyup9vH62sQcK0=','student','송민재','2002-10-09'),('s112','pbkdf2-sha256$600000$LK/Je0+MfGU0zoaqIjYFwQ==$E1GKGW6pE7RGjGTj1DJR9DH2SpJ3KThE9sj9UL9RBxg=','student','문채원','2006-12-15'),('s113','pbkdf2-sha256$600000$B3wzZZgZWBj57lvz2lCR4g==$Il9wJZDQ3vraFQkUKzER769X3BUa6nZUwBNt+5yxZts=','student','배도현','1999-02-13'),('s114','pbkdf2-sha256$600000$g6P+IMvmX7iE7gFwO8S9Eg==$vF/rOl/mQUboudcPFG/CKoAi7Q0+83biMpMx40Zny8M=','student','백수아','2006-09-10'),('s115','pbkdf2-sha256$600000$pmL+Kct5seVXaSMJ3PJ7gw==$oP2HICF4mQusNGQCiG9avUKVhrvcF/V2nBxJPVVQ/Xg=','student','남지훈','1997-10-28'),('s116','pbkdf2-sha256$600000$XrXh2Ep2+RmDwX9VXeZMBg==$tx8XGeU9XbrydNvnxBMC2slNwDACLW4tCAvcBWJ5hv0=','student','유하린','2002-10-07'),('s117','pbkdf2-sha256$600000$Y3sFaM+iQAcDqnGdMBg0LA==$6gYVswflBzR8lD/OZbOjOzkJed1iAt3IPb4290qrYQM=','student','노현준','2006-01-22'),('s118','pbkdf2-sha256$600000$xxKJ+OfgpeoxfufjYypC6A==$wve9kBMWGBKo1SzZvj9YiubrS45v++h3G7GPelS6Y7A=','student','차은우','2004-05-03'),('s119','pbkdf2-sha256$600000$kV9oPdWcfIucEAnbVSRwRg==$6rCXEwbUEC4hx5AozBadWihHyPTBDLPAd06WOK7+xqk=','student','전소민','2004-02-13'),('s120','pbkdf2-sha256$600000$O78R5G9ypf6q31W8jDW3Qg==$55oJ3na37+n7W/qZDFRM7I3StTUxnIyIhIxcSlG13C4=','student','홍서진','2003-08-21'),('s121','pbkdf2-sha256$600000$JjVqE5ENq4bdj6do+pUKlg==$kRpkbMzpNNjJk+9EYHSry5MZAQpr54IwnpO4Z1Yuy5g=','student','고은찬','2002-03-12'),('s122','pbkdf2-sha256$600000$nR10U/uoxCxVA9MJuITcVg==$GrGBz/VVp+wicLz4/FgoNxljwVoi+RgezqI/3n7wxnE=','student','양지민','2002-04-22'),('s123','pbkdf2-sha256$600000$hyuLZ47gmz9t6zhCqRgAWA==$SQaDnScU0QW6887efzFYSbvgMYn17/rmRfxPBLWm5DE=','student','주예린','2003-12-22');
/*!40000 ALTER TABLE `admin_settings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `department`
--

DROP TABLE IF EXISTS `department`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `department` (
  `department_cd` varchar(10) NOT NULL,
  `department_name` varchar(20) NOT NULL,
  `major_name` varchar(20) NOT NULL,
  `professor_cd` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`department_cd`),
  KEY `fk_department_professor` (`professor_cd`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `department`
--

LOCK TABLES `department` WRITE;
/*!40000 ALTER TABLE `department` DISABLE KEYS */;
INSERT INTO `department` (department_cd,department_name,major_name) VALUES ('d001','전산학과','소프트웨어공학'),('d002','전자공학과','반도체공학'),('d003','화학공학과','화학공정공학'),('d004','기계공학과','로봇공학'),('d005','항공우주공학과','항공우주공학');
/*!40000 ALTER TABLE `department` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `professor_info`
--

DROP TABLE IF EXISTS `professor_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `professor_info` (
  `user_id` varchar(20) NOT NULL,
  `professor_cd` varchar(10) NOT NULL,
  `user_name` varchar(30) NOT NULL,
  `user_address` varchar(255) NOT NULL,
  `mobile_no` varchar(20) NOT NULL,
  `phone_no` varchar(20) DEFAULT NULL,
  `hire_year` year NOT NULL,
  `degree` varchar(20) NOT NULL,
  `department_cd` varchar(10) NOT NULL,
  `room_no` varchar(10) NOT NULL,
  `user_email` varchar(255) NOT NULL,
  `birth_date` date NOT NULL,
  `user_gender` char(2) NOT NULL,
  `retirement_date` date DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `uq_professor_cd` (`professor_cd`),
  KEY `fk_professor_department` (`department_cd`),
  CONSTRAINT `fk_professor_department` FOREIGN KEY (`department_cd`) REFERENCES `department` (`department_cd`),
  CONSTRAINT `fk_professor_user` FOREIGN KEY (`user_id`) REFERENCES `admin_settings` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `professor_info`
--

LOCK TABLES `professor_info` WRITE;
/*!40000 ALTER TABLE `professor_info` DISABLE KEYS */;
INSERT INTO `professor_info` (user_id,professor_cd,user_name,user_address,mobile_no,phone_no,hire_year,degree,department_cd,room_no,user_email,birth_date,user_gender) VALUES ('p101','p101','박지훈','서울특별시 강남구 테헤란로 12','010-2345-1001','02-1234-1001',2015,'박사','d001','공학관 301호','parkjh@university.ac.kr','1979-03-24','남성'),('p102','p102','최유진','경기도 수원시 영통구 광교로 25','010-2345-1002','031-234-1002',2018,'박사','d002','공학관 302호','choiyj@university.ac.kr','1983-11-24','여성'),('p103','p103','정현우','서울특별시 송파구 올림픽로 30','010-2345-1003','02-1234-1003',2012,'박사','d003','공학관 303호','junghw@university.ac.kr','1969-02-19','남성'),('p104','p104','강민지','경기도 성남시 분당구 판교로 40','010-2345-1004','031-234-1004',2019,'석사','d004','공학관 304호','kangmj@university.ac.kr','1973-01-01','여성'),('p105','p105','윤서준','인천광역시 연수구 송도과학로 50','010-2345-1005','032-234-1005',2016,'박사','d005','공학관 305호','yoon sj@university.ac.kr','1984-04-08','남성');
/*!40000 ALTER TABLE `professor_info` ENABLE KEYS */;
UNLOCK TABLES;
UPDATE `department` d SET d.professor_cd = (
  SELECT MIN(p.professor_cd) FROM `professor_info` p
  WHERE p.department_cd = d.department_cd AND p.retirement_date IS NULL
);
ALTER TABLE `department`
  ADD CONSTRAINT `fk_department_professor` FOREIGN KEY (`professor_cd`)
  REFERENCES `professor_info` (`professor_cd`);

--
-- Table structure for table `registration_list`
--

DROP TABLE IF EXISTS `registration_list`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `registration_list` (
  `registration_no` int unsigned NOT NULL AUTO_INCREMENT,
  `student_cd` varchar(10) NOT NULL,
  `subject_cd` varchar(10) NOT NULL,
  `professor_cd` varchar(10) NOT NULL,
  `year` year NOT NULL,
  `semester` char(1) NOT NULL,
  PRIMARY KEY (`registration_no`),
  KEY `fk_registration_student` (`student_cd`),
  KEY `fk_registration_subject` (`subject_cd`),
  KEY `fk_registration_professor` (`professor_cd`),
  CONSTRAINT `fk_registration_professor` FOREIGN KEY (`professor_cd`) REFERENCES `professor_info` (`professor_cd`),
  CONSTRAINT `fk_registration_student` FOREIGN KEY (`student_cd`) REFERENCES `student_info` (`student_cd`),
  CONSTRAINT `fk_registration_subject` FOREIGN KEY (`subject_cd`) REFERENCES `subject` (`subject_cd`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `registration_list`
--

LOCK TABLES `registration_list` WRITE;
/*!40000 ALTER TABLE `registration_list` DISABLE KEYS */;
INSERT INTO `registration_list` VALUES (1,'s101','c001','p101',2026,'2'),(2,'s106','c003','p102',2026,'2'),(3,'s111','c005','p103',2026,'2');
/*!40000 ALTER TABLE `registration_list` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student_grades`
--

DROP TABLE IF EXISTS `student_grades`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student_grades` (
  `student_cd` varchar(10) NOT NULL,
  `department_cd` varchar(10) NOT NULL,
  `subject_cd` varchar(10) NOT NULL,
  `professor_cd` varchar(10) NOT NULL,
  `academic_year` year NOT NULL,
  `semester` char(1) NOT NULL,
  `midterm_score` int NOT NULL,
  `final_score` int NOT NULL,
  `assignment_score` int NOT NULL,
  `attendance_score` int NOT NULL,
  `grade` char(2) NOT NULL,
  `credit` decimal(2,1) DEFAULT NULL,
  PRIMARY KEY (`student_cd`,`subject_cd`,`academic_year`,`semester`),
  KEY `fk_grades_department` (`department_cd`),
  KEY `fk_grades_subject` (`subject_cd`),
  KEY `fk_grades_professor` (`professor_cd`),
  CONSTRAINT `fk_grades_department` FOREIGN KEY (`department_cd`) REFERENCES `department` (`department_cd`),
  CONSTRAINT `fk_grades_professor` FOREIGN KEY (`professor_cd`) REFERENCES `professor_info` (`professor_cd`),
  CONSTRAINT `fk_grades_student` FOREIGN KEY (`student_cd`) REFERENCES `student_info` (`student_cd`),
  CONSTRAINT `fk_grades_subject` FOREIGN KEY (`subject_cd`) REFERENCES `subject` (`subject_cd`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student_grades`
--

LOCK TABLES `student_grades` WRITE;
/*!40000 ALTER TABLE `student_grades` DISABLE KEYS */;
INSERT INTO `student_grades` VALUES ('s101','d001','c001','p101',2026,'2',85,90,90,100,'A',4.0),('s106','d002','c003','p102',2026,'2',75,80,85,90,'B',3.0),('s111','d003','c005','p103',2026,'2',65,70,75,90,'C',2.0);
/*!40000 ALTER TABLE `student_grades` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student_info`
--

DROP TABLE IF EXISTS `student_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student_info` (
  `user_id` varchar(20) NOT NULL,
  `student_cd` varchar(10) NOT NULL,
  `user_name` varchar(30) NOT NULL,
  `user_address` varchar(255) NOT NULL,
  `mobile_no` varchar(20) NOT NULL,
  `phone_no` varchar(20) DEFAULT NULL,
  `admission_year` date NOT NULL,
  `graduation_year` date DEFAULT NULL,
  `department_cd` varchar(10) NOT NULL,
  `professor_cd` varchar(10) NOT NULL,
  `user_email` varchar(255) NOT NULL,
  `birth_date` date NOT NULL,
  `user_gender` char(2) NOT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `uq_student_cd` (`student_cd`),
  KEY `fk_student_department` (`department_cd`),
  KEY `fk_student_professor` (`professor_cd`),
  CONSTRAINT `fk_student_department` FOREIGN KEY (`department_cd`) REFERENCES `department` (`department_cd`),
  CONSTRAINT `fk_student_professor` FOREIGN KEY (`professor_cd`) REFERENCES `professor_info` (`professor_cd`),
  CONSTRAINT `fk_student_user` FOREIGN KEY (`user_id`) REFERENCES `admin_settings` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student_info`
--

LOCK TABLES `student_info` WRITE;
/*!40000 ALTER TABLE `student_info` DISABLE KEYS */;
INSERT INTO `student_info` VALUES ('s101','S101','한지우','서울특별시 강남구 역삼동 101','010-3000-1001','02-3000-1001','2018-03-02','2022-02-28','d001','p101','hanjiwoo@university.ac.kr','1999-10-01','남성'),('s102','S102','오수빈','경기도 수원시 영통구 매탄동 102','010-3000-1002','031-3000-1002','2018-03-02','2022-02-28','d001','p101','osubin@university.ac.kr','1999-04-23','여성'),('s103','S103','임도윤','서울특별시 마포구 서교동 103','010-3000-1003','02-3000-1003','2016-03-02','2020-02-28','d001','p101','imdy@university.ac.kr','1997-12-18','남성'),('s104','S104','조하은','인천광역시 남동구 구월동 104','010-3000-1004','032-3000-1004','2020-03-02','2024-02-29','d001','p101','johaeun@university.ac.kr','2001-04-15','여성'),('s105','S105','장예준','경기도 성남시 분당구 정자동 105','010-3000-1005','031-3000-1005','2017-03-02','2021-02-28','d001','p101','jangyj@university.ac.kr','1998-05-26','남성'),('s106','S106','신유나','서울특별시 송파구 잠실동 106','010-3000-1006','02-3000-1006','2025-03-02',NULL,'d002','p102','shinyuna@university.ac.kr','2007-03-23','여성'),('s107','S107','서준호','경기도 용인시 수지구 풍덕천동 107','010-3000-1007','031-3000-1007','2020-03-02','2024-02-29','d002','p102','seojh@university.ac.kr','2001-06-09','남성'),('s108','S108','권서현','서울특별시 노원구 상계동 108','010-3000-1008','02-3000-1008','2024-03-02',NULL,'d002','p102','kwonsh@university.ac.kr','2005-04-25','여성'),('s109','S109','황지호','경기도 안양시 동안구 평촌동 109','010-3000-1009','031-3000-1009','2021-03-02','2025-02-28','d002','p102','hwangjh@university.ac.kr','2002-02-03','남성'),('s110','S110','안유진','서울특별시 서초구 반포동 110','010-3000-1010','02-3000-1010','2020-03-02','2024-02-29','d002','p102','anyujin@university.ac.kr','2001-02-12','여성'),('s111','S111','송민재','경기도 고양시 일산동구 장항동 111','010-3000-1011','031-3000-1011','2021-03-02','2025-02-28','d003','p103','songmj@university.ac.kr','2002-10-09','남성'),('s112','S112','문채원','서울특별시 강서구 화곡동 112','010-3000-1012','02-3000-1012','2025-03-02',NULL,'d003','p103','moonchw@university.ac.kr','2006-12-15','여성'),('s113','S113','배도현','경기도 화성시 동탄동 113','010-3000-1013','031-3000-1013','2018-03-02','2022-02-28','d003','p103','baedh@university.ac.kr','1999-02-13','남성'),('s114','S114','백수아','서울특별시 관악구 신림동 114','010-3000-1014','02-3000-1014','2024-03-02',NULL,'d003','p103','baeksa@university.ac.kr','2006-09-10','여성'),('s115','S115','남지훈','경기도 부천시 중동 115','010-3000-1015','032-3000-1015','2016-03-02','2020-02-28','d003','p103','namjh@university.ac.kr','1997-10-28','남성'),('s116','S116','유하린','서울특별시 성북구 길음동 116','010-3000-1016','02-3000-1016','2021-03-02','2025-02-28','d004','p104','yuharin@university.ac.kr','2002-10-07','여성'),('s117','S117','노현준','경기도 의정부시 민락동 117','010-3000-1017','031-3000-1017','2025-03-02',NULL,'d004','p104','noh hj@university.ac.kr','2006-01-22','남성'),('s118','S118','차은우','서울특별시 용산구 한남동 118','010-3000-1018','02-3000-1018','2023-03-02',NULL,'d004','p104','chaeunwoo@university.ac.kr','2004-05-03','남성'),('s119','S119','전소민','경기도 광명시 철산동 119','010-3000-1019','02-3000-1019','2023-03-02',NULL,'d004','p104','jeonsm@university.ac.kr','2004-02-13','여성'),('s120','S120','홍서진','서울특별시 동작구 사당동 120','010-3000-1020','02-3000-1020','2022-03-02','2026-02-28','d005','p105','hong sj@university.ac.kr','2003-08-21','남성'),('s121','S121','고은찬','경기도 김포시 장기동 121','010-3000-1021','031-3000-1021','2021-03-02','2025-02-28','d005','p105','goec@university.ac.kr','2002-03-12','남성'),('s122','S122','양지민','서울특별시 은평구 불광동 122','010-3000-1022','02-3000-1022','2021-03-02','2025-02-28','d005','p105','yangjm@university.ac.kr','2002-04-22','여성'),('s123','S123','주예린','경기도 남양주시 다산동 123','010-3000-1023','031-3000-1023','2022-03-02','2026-02-28','d005','p105','juyerin@university.ac.kr','2003-12-22','여성');
/*!40000 ALTER TABLE `student_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `subject`
--

DROP TABLE IF EXISTS `subject`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `subject` (
  `subject_cd` varchar(10) NOT NULL,
  `subject_name` varchar(20) NOT NULL,
  `academic_year` year NOT NULL,
  `semester` char(1) NOT NULL,
  `department_cd` varchar(10) NOT NULL,
  `professor_cd` varchar(10) NOT NULL,
  `subject_hour` varchar(20) NOT NULL,
  `subject_credit` char(1) NOT NULL,
  PRIMARY KEY (`subject_cd`),
  KEY `fk_subject_department` (`department_cd`),
  KEY `fk_subject_professor` (`professor_cd`),
  CONSTRAINT `fk_subject_department` FOREIGN KEY (`department_cd`) REFERENCES `department` (`department_cd`),
  CONSTRAINT `fk_subject_professor` FOREIGN KEY (`professor_cd`) REFERENCES `professor_info` (`professor_cd`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `subject`
--

LOCK TABLES `subject` WRITE;
/*!40000 ALTER TABLE `subject` DISABLE KEYS */;
INSERT INTO `subject` VALUES ('c001','데이터베이스',2026,'2','d001','p101','월 09:00-12:00','3'),('c002','대학생활과진로',2026,'2','d001','p101','화 13:00-15:00','2'),('c003','회로이론',2026,'2','d002','p102','월 13:00-16:00','3'),('c004','의사소통과글쓰기',2026,'2','d002','p102','수 10:00-11:00','1'),('c005','유기화학',2026,'2','d003','p103','화 09:00-12:00','3'),('c006','환경과지속가능성',2026,'2','d003','p103','목 13:00-15:00','2'),('c007','기계설계',2026,'2','d004','p104','수 13:00-16:00','3'),('c008','창의적문제해결',2026,'2','d004','p104','금 10:00-11:00','1'),('c009','항공역학',2026,'2','d005','p105','목 09:00-12:00','3'),('c010','과학기술과사회',2026,'2','d005','p105','금 13:00-15:00','2');
/*!40000 ALTER TABLE `subject` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-11-11 17:56:55
