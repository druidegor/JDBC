package org.example.jdbc_lab.statement.dao;

import org.example.jdbc_lab.connection.ConnectionManager;
import org.example.jdbc_lab.statement.model.Author;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class AuthorDao implements StatementDao<Author> {
    private static final Logger logger = Logger.getLogger(AuthorDao.class.getName());

    @Override
    public void clearTable() {
        try (Statement stmt = ConnectionManager.getConnection().createStatement()) {
            stmt.executeUpdate("DELETE FROM authors");
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void insert(Author author) {
        try (Statement stmt = ConnectionManager.getConnection().createStatement()) {
            stmt.executeUpdate("INSERT INTO authors (first_name, last_name) " +
                    "VALUES ('" + author.getFirstName() + "', '" + author.getLastName() + "')");
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getCount() {
        int count = 0;
        try (Statement stmt = ConnectionManager.getConnection().createStatement()) {
            ResultSet resultSet = stmt.executeQuery("SELECT COUNT(*) FROM authors");
            if (resultSet.next()) {
                count = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return count;
    }

    @Override
    public List<Author> getAll() {
        List<Author> authors = new ArrayList<>();
        try (Statement stmt = ConnectionManager.getConnection().createStatement()) {
            ResultSet resultSet = stmt.executeQuery("SELECT * FROM authors");
            while (resultSet.next()) {
                Author author = new Author();
                author.setId(resultSet.getLong("id"));
                author.setFirstName(resultSet.getString("first_name"));
                author.setLastName(resultSet.getString("last_name"));
                authors.add(author);
            }
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return authors;
    }

    @Override
    public Author getById(Long id) {
        Author author = null;
        try (Statement stmt = ConnectionManager.getConnection().createStatement()) {
            ResultSet resultSet = stmt.executeQuery("SELECT * FROM authors WHERE id = " + id);
            if (resultSet.next()) {
                author = new Author();
                author.setId(resultSet.getLong("id"));
                author.setFirstName(resultSet.getString("first_name"));
                author.setLastName(resultSet.getString("last_name"));
            }
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return author;
    }

    // Задание 2: авторы, отсортированные по имени и фамилии
    public List<Author> getAllSortedByName() {
        List<Author> authors = new ArrayList<>();
        try (Statement stmt = ConnectionManager.getConnection().createStatement()) {
            ResultSet resultSet = stmt.executeQuery(
                    "SELECT * FROM authors ORDER BY first_name, last_name");
            while (resultSet.next()) {
                Author author = new Author();
                author.setId(resultSet.getLong("id"));
                author.setFirstName(resultSet.getString("first_name"));
                author.setLastName(resultSet.getString("last_name"));
                authors.add(author);
            }
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return authors;
    }

    public int updateFirstNameByShop(String shopName, String newFirstName) {
        final String query = "UPDATE authors SET first_name = '" + newFirstName + "' " +
                "WHERE id IN (" +
                "SELECT author_id FROM books WHERE id IN (" +
                "SELECT book_id FROM shops_books WHERE shop_id = (" +
                "SELECT id FROM shops WHERE name = '" + shopName + "' LIMIT 1)))";
        try (Statement stmt = ConnectionManager.getConnection().createStatement()) {
            return stmt.executeUpdate(query); // вернёт количество обновлённых строк
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public Map<String, Integer> getBookCountByAuthor() {
        Map<String, Integer> result = new LinkedHashMap<>();
        final String query = "SELECT a.first_name, a.last_name, t.book_count " +
                "FROM authors a " +
                "JOIN (SELECT author_id, COUNT(*) AS book_count " +
                "      FROM books GROUP BY author_id) t " +
                "ON t.author_id = a.id " +
                "ORDER BY t.book_count DESC, a.last_name";
        try (Statement stmt = ConnectionManager.getConnection().createStatement()) {
            ResultSet resultSet = stmt.executeQuery(query);
            while (resultSet.next()) {
                result.put(resultSet.getString("first_name") + " " + resultSet.getString("last_name"),
                        resultSet.getInt("book_count"));
            }
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return result;
    }
}
