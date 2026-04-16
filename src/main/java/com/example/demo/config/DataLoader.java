package com.example.demo.config;

import com.example.demo.Model.Attendance;
import com.example.demo.Model.Student;
import com.example.demo.Model.TeacherClassMapping;
import com.example.demo.Model.User;
import com.example.demo.repository.AttendanceRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.repository.TeacherClassMappingRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Configuration
public class DataLoader {

    private static final String DEFAULT_PASSWORD = "pass123";
    private static final int[] ATTENDANCE_DAY_OFFSETS = {
            28, 26, 24, 22, 20, 18, 16,
            14, 12, 10, 8, 6, 4, 2
    };

    @Value("${app.demo-data.reset:false}")
    private boolean resetDemoData;

    @Bean
    CommandLineRunner loadData(StudentRepository studentRepo,
                               AttendanceRepository attendanceRepo,
                               UserRepository userRepo,
                               TeacherClassMappingRepository mappingRepo) {
        return args -> {
            long userCount = userRepo.count();
            long mappingCount = mappingRepo.count();
            long studentCount = studentRepo.count();
            long attendanceCount = attendanceRepo.count();

            if (resetDemoData) {
                attendanceRepo.deleteAll();
                studentRepo.deleteAll();
                mappingRepo.deleteAll();
                userRepo.deleteAll();
                userCount = 0;
                mappingCount = 0;
                studentCount = 0;
                attendanceCount = 0;
                System.out.println("DataLoader: cleared existing data because app.demo-data.reset=true.");
            }

            if (userCount > 0 || mappingCount > 0 || studentCount > 0 || attendanceCount > 0) {
                System.out.printf(
                        "DataLoader: skipping demo seed because data already exists "
                                + "(users=%d, mappings=%d, students=%d, attendance=%d)%n",
                        userCount, mappingCount, studentCount, attendanceCount
                );
                return;
            }

            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            LocalDate today = LocalDate.now();

            User khan = createTeacher(userRepo, encoder, "khan", "Prof. Khan");
            User mir = createTeacher(userRepo, encoder, "mir", "Prof. Mir");
            User fatima = createTeacher(userRepo, encoder, "fatima", "Prof. Fatima");

            createMapping(mappingRepo, khan, "BCA", 1);
            createMapping(mappingRepo, khan, "MCA", 1);
            createMapping(mappingRepo, mir, "BCA", 3);
            createMapping(mappingRepo, fatima, "BCA", 1);

            Student ali = createStudent(studentRepo, "Ali Khan", "101", "BCA", 1);
            Student ahmed = createStudent(studentRepo, "Ahmed Raza", "102", "BCA", 1);
            Student sara = createStudent(studentRepo, "Sara Malik", "103", "BCA", 1);
            Student zara = createStudent(studentRepo, "Zara Sheikh", "104", "BCA", 1);
            Student noor = createStudent(studentRepo, "Noor Fatima", "109", "BCA", 1);

            Student usman = createStudent(studentRepo, "Usman Tariq", "105", "BCA", 3);
            Student hina = createStudent(studentRepo, "Hina Butt", "106", "BCA", 3);
            Student bilal = createStudent(studentRepo, "Bilal Chaudhry", "107", "BCA", 3);
            Student meera = createStudent(studentRepo, "Meera Joshi", "108", "BCA", 3);

            Student ayesha = createStudent(studentRepo, "Ayesha Noor", "301", "MCA", 1);
            Student raza = createStudent(studentRepo, "Raza Ahmed", "302", "MCA", 1);
            Student sana = createStudent(studentRepo, "Sana Iqbal", "303", "MCA", 1);

            createStudentLogin(userRepo, encoder, ali, "ali_student");
            createStudentLogin(userRepo, encoder, sara, "sara_student");
            createStudentLogin(userRepo, encoder, hina, "hina_student");
            createStudentLogin(userRepo, encoder, ayesha, "ayesha_student");

            seedAttendanceSeries(attendanceRepo, ali, khan.getName(), today,
                    "A", "A", "A", "P", "P", "P", "P",
                    "P", "P", "P", "P", "P", "P", "P");

            seedAttendanceSeries(attendanceRepo, ahmed, khan.getName(), today,
                    "P", "P", "P", "P", "P", "P", "A",
                    "P", "A", "A", "P", "A", "A", "A");

            seedAttendanceSeries(attendanceRepo, sara, khan.getName(), today,
                    "P", "P", "P", "P", "P", "A", "A",
                    "P", "P", "P", "P", "P", "A", "A");

            seedAttendanceSeries(attendanceRepo, zara, khan.getName(), today,
                    "P", "P", "P", "P", "P", "P", "A",
                    "P", "A", "A", "A", "A", "A", "A");

            seedAttendanceSeries(attendanceRepo, noor, khan.getName(), today,
                    "P", "P", "P", "P", "P", "A", "A",
                    "P", "P", "P", "P", "P", "P", "A");

            seedAttendanceSeries(attendanceRepo, usman, mir.getName(), today,
                    "P", "P", "P", "P", "A", "A", "A",
                    "P", "P", "P", "P", "P", "P", "A");

            seedAttendanceSeries(attendanceRepo, hina, mir.getName(), today,
                    "P", "P", "P", "P", "P", "P", "A",
                    "P", "P", "P", "P", "P", "P", "A");

            seedAttendanceSeries(attendanceRepo, bilal, mir.getName(), today,
                    "P", "P", "P", "P", "P", "A", "A",
                    "P", "P", "A", "A", "A", "A", "A");

            seedAttendanceSeries(attendanceRepo, meera, mir.getName(), today,
                    "P", "P", "P", "P", "P", "A", "A",
                    "P", "P", "P", "P", "P", "A", "A");

            seedAttendanceSeries(attendanceRepo, ayesha, khan.getName(), today,
                    "P", "P", "P", "P", "P", "P", "A",
                    "P", "P", "P", "P", "P", "P", "P");

            seedAttendanceSeries(attendanceRepo, raza, khan.getName(), today,
                    "P", "P", "P", "P", "P", "A", "A",
                    "P", "P", "P", "P", "P", "A", "A");

            seedAttendanceSeries(attendanceRepo, sana, khan.getName(), today,
                    "P", "P", "P", "P", "A", "A", "A",
                    "P", "P", "A", "A", "A", "A", "A");

            printSummary(khan, mir, fatima);
        };
    }

    private User createTeacher(UserRepository userRepo,
                               BCryptPasswordEncoder encoder,
                               String username,
                               String name) {
        return userRepo.save(new User(
                null,
                username,
                encoder.encode(DEFAULT_PASSWORD),
                "TEACHER",
                name,
                null
        ));
    }

    private void createMapping(TeacherClassMappingRepository mappingRepo,
                               User teacher,
                               String course,
                               int semester) {
        mappingRepo.save(new TeacherClassMapping(
                null,
                teacher.getId(),
                teacher.getName(),
                course,
                semester
        ));
    }

    private Student createStudent(StudentRepository studentRepo,
                                  String name,
                                  String rollNumber,
                                  String course,
                                  int semester) {
        return studentRepo.save(new Student(null, name, rollNumber, course, semester));
    }

    private void createStudentLogin(UserRepository userRepo,
                                    BCryptPasswordEncoder encoder,
                                    Student student,
                                    String username) {
        userRepo.save(new User(
                null,
                username,
                encoder.encode(DEFAULT_PASSWORD),
                "STUDENT",
                student.getName(),
                student.getRollNumber()
        ));
    }

    private void seedAttendanceSeries(AttendanceRepository attendanceRepo,
                                      Student student,
                                      String teacherName,
                                      LocalDate today,
                                      String... statusCodes) {
        if (statusCodes.length != ATTENDANCE_DAY_OFFSETS.length) {
            throw new IllegalArgumentException(
                    "Expected " + ATTENDANCE_DAY_OFFSETS.length
                            + " attendance codes per student but got " + statusCodes.length
            );
        }

        for (int i = 0; i < ATTENDANCE_DAY_OFFSETS.length; i++) {
            attendanceRepo.save(new Attendance(
                    null,
                    student.getRollNumber(),
                    student.getName(),
                    today.minusDays(ATTENDANCE_DAY_OFFSETS[i]).format(DateTimeFormatter.ISO_LOCAL_DATE),
                    toStatus(statusCodes[i]),
                    teacherName,
                    student.getCourse(),
                    student.getSemester()
            ));
        }
    }

    private String toStatus(String statusCode) {
        if ("P".equalsIgnoreCase(statusCode)) {
            return "Present";
        }
        if ("A".equalsIgnoreCase(statusCode)) {
            return "Absent";
        }
        throw new IllegalArgumentException("Unsupported attendance code: " + statusCode);
    }

    private void printSummary(User khan, User mir, User fatima) {
        System.out.println("DataLoader: seeded demo data for teacher mapping, student lookup, dashboards, reports, and trend analysis.");
        System.out.println("Teacher logins (password: pass123)");
        System.out.printf("  khan -> teacherId=%s, classes=[BCA-1, MCA-1]%n", khan.getId());
        System.out.printf("  mir -> teacherId=%s, classes=[BCA-3]%n", mir.getId());
        System.out.printf("  fatima -> teacherId=%s, classes=[BCA-1]%n", fatima.getId());
        System.out.println("Student logins (password: pass123)");
        System.out.println("  ali_student -> rollNumber=101");
        System.out.println("  sara_student -> rollNumber=103");
        System.out.println("  hina_student -> rollNumber=106");
        System.out.println("  ayesha_student -> rollNumber=301");
    }
}
