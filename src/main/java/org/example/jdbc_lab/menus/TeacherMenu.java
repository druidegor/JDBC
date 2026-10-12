package org.example.jdbc_lab.menus;

import org.example.jdbc_lab.prepared_statement.action.TeacherAction;
import org.example.jdbc_lab.utils.InputManager;
import org.example.jdbc_lab.utils.MenuUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class TeacherMenu {
    private static final Logger logger = Logger.getLogger(TeacherMenu.class.getName());
    private final TeacherAction teacherAction = new TeacherAction();

    public void print() {
        List<String> options = new ArrayList<>();
        options.add("Добавить преподавателя");
        options.add("Обновить преподавателя");
        options.add("Удалить преподавателя");
        options.add("Найти по предмету");
        options.add("Найти по стажу (от N лет)");
        options.add("Показать всех преподавателей");
        final String header = MenuUtils.getHeader("Меню преподавателей (PreparedStatement)", options);
        while (true) {
            System.out.print(header);
            try {
                switch (InputManager.getNextInt()) {
                    case 1: { teacherAction.insertTeacher(); break; }
                    case 2: { teacherAction.updateTeacher(); break; }
                    case 3: { teacherAction.deleteTeacher(); break; }
                    case 4: { teacherAction.getTeachersBySubject(); break; }
                    case 5: { teacherAction.getTeachersByExperience(); break; }
                    case 6: { teacherAction.getAllTeachers(); break; }
                    case 0: { return; }
                    default: {
                        System.out.println("Такого варианта нет!");
                        break;
                    }
                }
            } catch (Exception e) {
                logger.severe("Ошибка при обработке меню преподавателей: " + e.getMessage());
            }
        }
    }
}
