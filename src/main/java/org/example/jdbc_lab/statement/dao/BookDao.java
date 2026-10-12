package org.example.jdbc_lab.statement.dao;

import org.example.jdbc_lab.connection.ConnectionManager;
import org.example.jdbc_lab.statement.model.Book;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class BookDao implements StatementDao<Book> {
    private static final Logger logger = Logger.getLogger(BookDao.class.getName());

    @Override
    public void clearTable() {
        try (Statement stmt = ConnectionManager.getConnection().createStatement()) {
            stmt.executeUpdate("DELETE FROM books");
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void insert(Book book) {
        try (Statement stmt = ConnectionManager.getConnection().createStatement()) {
            stmt.executeUpdate("INSERT INTO books (title, author_id, year_of_publication) " +
                    "VALUES ('" + book.getTitle() + "', " + book.getAuthorId() + ", " +
                    book.getYearOfPublication() + ")");
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getCount() {
        int count = 0;
        try (Statement stmt = ConnectionManager.getConnection().createStatement()) {
            ResultSet resultSet = stmt.executeQuery("SELECT COUNT(*) FROM books");
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
    public List<Book> getAll() {
        List<Book> books = new ArrayList<>();
        try (Statement stmt = ConnectionManager.getConnection().createStatement()) {
            ResultSet resultSet = stmt.executeQuery("SELECT * FROM books");
            while (resultSet.next()) {
                books.add(mapBook(resultSet));
            }
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return books;
    }

    @Override
    public Book getById(Long id) {
        Book book = null;
        try (Statement stmt = ConnectionManager.getConnection().createStatement()) {
            ResultSet resultSet = stmt.executeQuery("SELECT * FROM books WHERE id = " + id);
            if (resultSet.next()) {
                book = mapBook(resultSet);
            }
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return book;
    }

    private Book mapBook(ResultSet resultSet) throws SQLException {
        Book book = new Book();
        book.setId(resultSet.getLong("id"));
        book.setTitle(resultSet.getString("title"));
        book.setAuthorId(resultSet.getLong("author_id"));
        book.setYearOfPublication(resultSet.getInt("year_of_publication"));
        return book;
    }

    private List<Book> getListByQuery(String query) {
        List<Book> books = new ArrayList<>();
        try (Statement stmt = ConnectionManager.getConnection().createStatement()) {
            ResultSet resultSet = stmt.executeQuery(query);
            while (resultSet.next()) {
                books.add(mapBook(resultSet));
            }
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return books;
    }

    // Задание 3: книги, изданные после определённого года, сортировка по году
    public List<Book> getPublishedAfter(int year) {
        return getListByQuery("SELECT * FROM books WHERE year_of_publication > " + year +
                " ORDER BY year_of_publication");
    }

    public Book updateRandomBookTitleInShop(String shopName, String newTitle) {
        try (Statement stmt = ConnectionManager.getConnection().createStatement()) {
            ResultSet resultSet = stmt.executeQuery(
                    "SELECT b.* FROM books b " +
                            "JOIN shops_books sb ON sb.book_id = b.id " +
                            "JOIN shops s ON s.id = sb.shop_id " +
                            "WHERE s.name = '" + shopName + "' " +
                            "ORDER BY RAND() LIMIT 1");
            if (!resultSet.next()) {
                return null;
            }
            Book book = mapBook(resultSet);
            stmt.executeUpdate("UPDATE books SET title = '" + newTitle + "' WHERE id = " + book.getId());
            return book;
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public List<Book> getByAuthorLastNameFirstLetter(String letter) {
        return getListByQuery("SELECT * FROM books WHERE author_id IN (" +
                "SELECT id FROM authors WHERE last_name LIKE '" + letter + "%') " +
                "ORDER BY title");
    }
}
