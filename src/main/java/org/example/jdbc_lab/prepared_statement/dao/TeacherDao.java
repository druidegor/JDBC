package org.example.jdbc_lab.prepared_statement.dao;

import org.example.jdbc_lab.connection.ConnectionManager;
import org.example.jdbc_lab.prepared_statement.model.Teacher;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class TeacherDao implements PreparedStatementDao<Teacher> {
    private static final Logger logger = Logger.getLogger(TeacherDao.class.getName());

    // Операция: найти по id
    @Override
    public Teacher getById(Long id) {
        final String query = "SELECT * FROM teachers WHERE id = ? LIMIT 1";
        try (PreparedStatement pStmt = ConnectionManager.getConnection().prepareStatement(query)) {
            pStmt.setLong(1, id);
            List<Teacher> list = getTeacherListByResultSet(pStmt.executeQuery());
            return list.isEmpty() ? null : list.get(0);
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    // Операция 1: добавить
    @Override
    public void insert(Teacher teacher) {
        final String query = "INSERT INTO teachers (first_name, last_name, subject, experience_years, email) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pStmt = ConnectionManager.getConnection().prepareStatement(query)) {
            pStmt.setString(1, teacher.getFirstName());
            pStmt.setString(2, teacher.getLastName());
            pStmt.setString(3, teacher.getSubject());
            pStmt.setInt(4, teacher.getExperienceYears());
            pStmt.setString(5, teacher.getEmail());
            pStmt.executeUpdate();
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    // Операция 2: обновить
    @Override
    public void update(Teacher teacher) {
        final String query = "UPDATE teachers SET first_name = ?, last_name = ?, subject = ?, " +
                "experience_years = ?, email = ? WHERE id = ?";
        try (PreparedStatement pStmt = ConnectionManager.getConnection().prepareStatement(query)) {
            pStmt.setString(1, teacher.getFirstName());
            pStmt.setString(2, teacher.getLastName());
            pStmt.setString(3, teacher.getSubject());
            pStmt.setInt(4, teacher.getExperienceYears());
            pStmt.setString(5, teacher.getEmail());
            pStmt.setLong(6, teacher.getId());
            pStmt.executeUpdate();
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    // Операция 3: удалить
    @Override
    public void delete(Long id) {
        final String query = "DELETE FROM teachers WHERE id = ?";
        try (PreparedStatement pStmt = ConnectionManager.getConnection().prepareStatement(query)) {
            pStmt.setLong(1, id);
            pStmt.executeUpdate();
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    // Операция 6: показать всех
    @Override
    public List<Teacher> getAll() {
        final String query = "SELECT * FROM teachers ORDER BY last_name, first_name";
        try (PreparedStatement pStmt = ConnectionManager.getConnection().prepareStatement(query)) {
            return getTeacherListByResultSet(pStmt.executeQuery());
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    // Операция 4: найти по предмету
    public List<Teacher> getBySubject(String subject) {
        final String query = "SELECT * FROM teachers WHERE subject LIKE ? ORDER BY last_name";
        try (PreparedStatement pStmt = ConnectionManager.getConnection().prepareStatement(query)) {
            pStmt.setString(1, "%" + subject + "%");
            return getTeacherListByResultSet(pStmt.executeQuery());
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    // Операция 5: найти со стажем от N лет
    public List<Teacher> getByMinExperience(int minYears) {
        final String query = "SELECT * FROM teachers WHERE experience_years >= ? ORDER BY experience_years DESC";
        try (PreparedStatement pStmt = ConnectionManager.getConnection().prepareStatement(query)) {
            pStmt.setInt(1, minYears);
            return getTeacherListByResultSet(pStmt.executeQuery());
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private List<Teacher> getTeacherListByResultSet(ResultSet resultSet) throws SQLException {
        List<Teacher> teachers = new ArrayList<>();
        while (resultSet.next()) {
            Teacher teacher = new Teacher();
            teacher.setId(resultSet.getLong("id"));
            teacher.setFirstName(resultSet.getString("first_name"));
            teacher.setLastName(resultSet.getString("last_name"));
            teacher.setSubject(resultSet.getString("subject"));
            teacher.setExperienceYears(resultSet.getInt("experience_years"));
            teacher.setEmail(resultSet.getString("email"));
            teachers.add(teacher);
        }
        return teachers;
    }
}