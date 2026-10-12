package org.example.jdbc_lab.prepared_statement.dao;

import org.example.jdbc_lab.connection.ConnectionManager;
import org.example.jdbc_lab.prepared_statement.model.Student;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class StudentDao implements PreparedStatementDao<Student> {
    private static final Logger logger = Logger.getLogger(StudentDao.class.getName());

    @Override
    public Student getById(Long id) {
        final String query = "SELECT * FROM students WHERE id = ? LIMIT 1";
        try (PreparedStatement pStmt = ConnectionManager.getConnection().prepareStatement(query)) {
            pStmt.setLong(1, id);
            List<Student> list = getStudentListByResultSet(pStmt.executeQuery());
            return list.isEmpty() ? null : list.get(0);
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void insert(Student student) {
        final String query = "INSERT INTO students (first_name, last_name, age, city, email) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pStmt = ConnectionManager.getConnection().prepareStatement(query)) {
            pStmt.setString(1, student.getFirstName());
            pStmt.setString(2, student.getLastName());
            pStmt.setInt(3, student.getAge());
            pStmt.setString(4, student.getCity());
            pStmt.setString(5, student.getEmail());
            pStmt.executeUpdate();
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Student student) {
        final String query = "UPDATE students SET first_name = ?, last_name = ?, age = ?, city = ?, email = ? WHERE id = ?";
        try (PreparedStatement pStmt = ConnectionManager.getConnection().prepareStatement(query)) {
            pStmt.setString(1, student.getFirstName());
            pStmt.setString(2, student.getLastName());
            pStmt.setInt(3, student.getAge());
            pStmt.setString(4, student.getCity());
            pStmt.setString(5, student.getEmail());
            pStmt.setLong(6, student.getId());
            pStmt.executeUpdate();
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Long id) {
        final String query = "DELETE FROM students WHERE id = ?";
        try (PreparedStatement pStmt = ConnectionManager.getConnection().prepareStatement(query)) {
            pStmt.setLong(1, id);
            pStmt.executeUpdate();
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Student> getAll() {
        return findByQuery("SELECT * FROM students", null);
    }

    public List<Student> getByFirstName(String firstName) {
        return findByQuery("SELECT * FROM students WHERE first_name LIKE ?", firstName + "%");
    }

    public List<Student> getByLastName(String lastName) {
        return findByQuery("SELECT * FROM students WHERE last_name LIKE ?", lastName + "%");
    }

    public List<Student> getByCity(String city) {
        return findByQuery("SELECT * FROM students WHERE city LIKE ?", city + "%");
    }

    public List<Student> getByEmail(String email) {
        return findByQuery("SELECT * FROM students WHERE email LIKE ?", "%" + email + "%");
    }

    public List<Student> getByAge(final int start, final int end) {
        final String query = "SELECT * FROM students WHERE age >= ? AND age <= ?";
        try (PreparedStatement pStmt = ConnectionManager.getConnection().prepareStatement(query)) {
            pStmt.setInt(1, start);
            pStmt.setInt(2, end);
            return getStudentListByResultSet(pStmt.executeQuery());
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    // Общий метод для запросов с одним строковым параметром (или без параметров)
    private List<Student> findByQuery(String query, String param) {
        try (PreparedStatement pStmt = ConnectionManager.getConnection().prepareStatement(query)) {
            if (param != null) {
                pStmt.setString(1, param);
            }
            return getStudentListByResultSet(pStmt.executeQuery());
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private List<Student> getStudentListByResultSet(ResultSet resultSet) throws SQLException {
        List<Student> students = new ArrayList<>();
        while (resultSet.next()) {
            Student student = new Student();
            student.setId(resultSet.getLong("id"));
            student.setFirstName(resultSet.getString("first_name"));
            student.setLastName(resultSet.getString("last_name"));
            student.setAge(resultSet.getInt("age"));
            student.setCity(resultSet.getString("city"));
            student.setEmail(resultSet.getString("email"));
            students.add(student);
        }
        return students;
    }
}