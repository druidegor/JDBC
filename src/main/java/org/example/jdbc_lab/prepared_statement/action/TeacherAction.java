package org.example.jdbc_lab.prepared_statement.action;

import org.example.jdbc_lab.prepared_statement.dao.TeacherDao;
import org.example.jdbc_lab.prepared_statement.model.Teacher;
import org.example.jdbc_lab.utils.InputManager;

import java.util.List;

public class TeacherAction {
    TeacherDao teacherDao = new TeacherDao();

    public void insertTeacher() {
        Teacher teacher = Teacher.getTeacherFromInput();
        teacherDao.insert(teacher);
        System.out.println("Преподаватель добавлен успешно!");
    }

    public void updateTeacher() {
        System.out.print("Введи id преподавателя: ");
        Teacher teacher = teacherDao.getById(InputManager.getNextLong());
        if (teacher == null) {
            System.out.println("Преподаватель с таким id не найден!");
            return;
        }
        System.out.println("Текущие данные: " + teacher);
        Teacher newTeacher = Teacher.getTeacherFromInput();
        newTeacher.setId(teacher.getId());
        teacherDao.update(newTeacher);
        System.out.println("Преподаватель обновлён успешно!");
    }

    public void deleteTeacher() {
        System.out.print("Введи id преподавателя: ");
        Teacher teacher = teacherDao.getById(InputManager.getNextLong());
        if (teacher == null) {
            System.out.println("Преподаватель с таким id не найден!");
            return;
        }
        teacherDao.delete(teacher.getId());
        System.out.println("Преподаватель удалён успешно!");
    }

    public void getTeachersBySubject() {
        System.out.print("Введи предмет (или его часть): ");
        printTeachers("Преподаватели по предмету", teacherDao.getBySubject(InputManager.getNextLine()));
    }

    public void getTeachersByExperience() {
        System.out.print("Введи минимальный стаж (0 - 60): ");
        int minYears = InputManager.getNextIntInRange(0, 60);
        printTeachers("Преподаватели со стажем от " + minYears + " лет", teacherDao.getByMinExperience(minYears));
    }

    public void getAllTeachers() {
        printTeachers("Все преподаватели", teacherDao.getAll());
    }

    private void printTeachers(String title, List<Teacher> teachers) {
        if (teachers.isEmpty()) {
            System.out.println("Ничего не найдено!");
            return;
        }
        System.out.println("[" + title + "]");
        for (Teacher teacher : teachers) {
            System.out.println(teacher);
        }
    }
}