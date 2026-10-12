package org.example.jdbc_lab.prepared_statement.model;


import lombok.Data;
import org.example.jdbc_lab.utils.InputManager;

@Data
public class Teacher {
    private Long id;
    private String firstName;
    private String lastName;
    private String subject;
    private Integer experienceYears;
    private String email;

    public Teacher() {}

    public Teacher(String firstName, String lastName, String subject, Integer experienceYears, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.subject = subject;
        this.experienceYears = experienceYears;
        this.email = email;
    }

    // Ввод данных преподавателя с клавиатуры
    public static Teacher getTeacherFromInput() {
        Teacher teacher = new Teacher();
        System.out.print("Введи имя: ");
        teacher.setFirstName(InputManager.getNextLine());
        System.out.print("Введи фамилию: ");
        teacher.setLastName(InputManager.getNextLine());
        System.out.print("Введи предмет: ");
        teacher.setSubject(InputManager.getNextLine());
        System.out.print("Введи стаж в годах (0 - 60): ");
        teacher.setExperienceYears(InputManager.getNextIntInRange(0, 60));
        System.out.print("Введи email (-, чтобы пропустить): ");
        teacher.setEmail(InputManager.getNextLineWithSkip());
        return teacher;
    }
}
