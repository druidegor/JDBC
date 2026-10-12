package org.example.jdbc_lab.prepared_statement.action;

import org.example.jdbc_lab.prepared_statement.dao.StudentDao;
import org.example.jdbc_lab.prepared_statement.model.Student;
import org.example.jdbc_lab.utils.InputManager;

import java.util.List;

public class PreparedStatementAction {
    StudentDao studentDao = new StudentDao();

    public void insertStudent() {
        Student student = Student.getStudentFromInput();
        studentDao.insert(student);
        System.out.println("Студент добавлен успешно!");
    }

    public void updateStudent() {
        System.out.print("Введи id студента: ");
        Student student = studentDao.getById(InputManager.getNextLong());
        if (student == null) {
            System.out.println("Студент с введённым id не найден!");
        } else {
            Student newStudent = Student.getStudentFromInput();
            newStudent.setId(student.getId());
            studentDao.update(newStudent);
            System.out.println("Студент обновлён успешно!");
        }
    }

    public void deleteStudent() {
        System.out.print("Введи id студента: ");
        Student student = studentDao.getById(InputManager.getNextLong());
        if (student == null) {
            System.out.println("Студент с введённым id не найден!");
        } else {
            studentDao.delete(student.getId());
            System.out.println("Студент удалён успешно!");
        }
    }

    public void getStudentById() {
        System.out.print("Введи id студента: ");
        Student student = studentDao.getById(InputManager.getNextLong());
        if (student == null) {
            System.out.println("Студент с введённым id не найден!");
        } else {
            System.out.println("[Студент по id]\n" + student);
        }
    }

    public void getStudentsByFirstName() {
        System.out.print("Введи имя (или его часть): ");
        printStudents("Студенты по имени", studentDao.getByFirstName(InputManager.getNextLine()));
    }

    public void getStudentsByLastName() {
        System.out.print("Введи фамилию (или её часть): ");
        printStudents("Студенты по фамилии", studentDao.getByLastName(InputManager.getNextLine()));
    }

    public void getStudentsByCity() {
        System.out.print("Введи название города (или его часть): ");
        printStudents("Студенты по городу", studentDao.getByCity(InputManager.getNextLine()));
    }

    public void getStudentsByEmail() {
        System.out.print("Введи электронную почту (или её часть): ");
        printStudents("Студенты по электронной почте", studentDao.getByEmail(InputManager.getNextLine()));
    }

    public void getStudentsByAge() {
        int start;
        int end;
        while (true) {
            System.out.print("Введи начальный возраст для поиска: ");
            start = InputManager.getNextIntInRange(17, 71);
            System.out.print("Введи конечный возраст для поиска: ");
            end = InputManager.getNextIntInRange(17, 71);
            if (start > end) {
                System.out.println("Начальный возраст не может быть > конечного!");
            } else {
                break;
            }
        }
        printStudents("Студенты по возрасту", studentDao.getByAge(start, end));
    }

    public void getAllStudents() {
        printStudents("Все студенты", studentDao.getAll());
    }

    private void printStudents(String title, List<Student> students) {
        if (students.isEmpty()) {
            System.out.println("Ничего не найдено!");
            return;
        }
        System.out.println("[" + title + "]");
        for (Student student : students) {
            System.out.println(student);
        }
    }
}
