package org.example.jdbc_lab.statement.action;

import org.example.jdbc_lab.statement.dao.AuthorDao;
import org.example.jdbc_lab.statement.dao.BookDao;
import org.example.jdbc_lab.statement.dao.ShopBookDao;
import org.example.jdbc_lab.statement.dao.ShopDao;
import org.example.jdbc_lab.statement.model.Author;
import org.example.jdbc_lab.statement.model.Book;
import org.example.jdbc_lab.statement.model.Shop;
import org.example.jdbc_lab.statement.model.ShopBook;
import org.example.jdbc_lab.utils.InputManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class StatementAction {
    private static final Logger logger = Logger.getLogger(StatementAction.class.getName());
    AuthorDao authorDao = new AuthorDao();
    BookDao bookDao = new BookDao();
    ShopDao shopDao = new ShopDao();
    ShopBookDao shopBookDao = new ShopBookDao();

    public void deleteAllInfo() {
        System.out.print("Подтвердите удаление всей информации (y / Y): ");
        final String choice = InputManager.getNextLine();
        if (!choice.equals("Y") && !choice.equals("y")) {
            System.out.println("Удаление было отменено!");
            return;
        }
        try {
            shopBookDao.clearTable();
            bookDao.clearTable();
            authorDao.clearTable();
            shopDao.clearTable();
            System.out.println("Информация из таблиц была успешно удалена!");
        } catch (RuntimeException e) {
            logger.severe("При удалении информации из таблиц ошибка: " + e.getMessage());
        }
    }

    public void addDefaultInfo() {
        try {
            if (authorDao.getCount() != 0 || bookDao.getCount() != 0 ||
                    shopDao.getCount() != 0 || shopBookDao.getCount() != 0) {
                System.out.println("Таблицы не пусты. Вставка информации по умолчанию невозможна!");
                return;
            }
            for (Author author : getDefaultAuthors()) {
                authorDao.insert(author);
            }
            List<Author> authors = authorDao.getAll();

            for (Book book : getDefaultBooks(authors)) {
                bookDao.insert(book);
            }
            List<Book> books = bookDao.getAll();

            for (Shop shop : getDefaultShops()) {
                shopDao.insert(shop);
            }
            List<Shop> shops = shopDao.getAll();

            for (ShopBook shopBook : getDefaultShopBooks(shops, books)) {
                shopBookDao.insert(shopBook);
            }
            System.out.println("Информация по умолчанию вставлена успешно!");
        } catch (RuntimeException e) {
            logger.severe("При добавлении информации в таблицы возникла ошибка: " + e.getMessage());
        }
    }

    public void getAllInfo() {
        try {
            printList("Авторы", authorDao.getAll());
            printList("Книги", bookDao.getAll());
            printList("Магазины", shopDao.getAll());
            printList("Книги в магазинах", shopBookDao.getAll());

            printList("Авторы, отсортированные по имени и фамилии", authorDao.getAllSortedByName());

            System.out.print("\nВведи год, после которого искать книги: ");
            int year = InputManager.getNextInt();
            printList("Книги, изданные после " + year + " года", bookDao.getPublishedAfter(year));

            System.out.print("\nВведи первую букву фамилии автора: ");
            String letter = InputManager.getNextLine().trim();
            printList("Книги авторов с фамилией на «" + letter + "»",
                    bookDao.getByAuthorLastNameFirstLetter(letter));

            System.out.println("\n[Количество книг у каждого автора]");
            Map<String, Integer> counts = authorDao.getBookCountByAuthor();
            if (counts.isEmpty()) {
                System.out.println("(пусто)");
            }
            counts.forEach((author, count) -> System.out.println(author + " — " + count));

        } catch (RuntimeException e) {
            logger.severe("При просмотре информации возникла ошибка: " + e.getMessage());
        }
    }

    private <T> void printList(String title, List<T> list) {
        System.out.println("\n[" + title + "]");
        if (list.isEmpty()) {
            System.out.println("(пусто)");
            return;
        }
        for (T item : list) {
            System.out.println(item);
        }
    }

    public void updateRandomBookTitle() {
        System.out.print("Введи название магазина (Белкнига / Букинист / Oz): ");
        String shopName = InputManager.getNextLine().trim();
        System.out.print("Введи новое название книги: ");
        String newTitle = InputManager.getNextLine().trim();
        try {
            Book oldBook = bookDao.updateRandomBookTitleInShop(shopName, newTitle);
            if (oldBook == null) {
                System.out.println("Магазин не найден или в нём нет книг!");
            } else {
                System.out.println("Книга «" + oldBook.getTitle() + "» (id = " + oldBook.getId() +
                        ") переименована в «" + newTitle + "»");
            }
        } catch (RuntimeException e) {
            logger.severe("Ошибка при обновлении книги: " + e.getMessage());
        }
    }

    private List<Author> getDefaultAuthors() {
        List<Author> authors = new ArrayList<>();
        authors.add(new Author("Лев", "Толстой"));
        authors.add(new Author("Фёдор", "Достоевский"));
        authors.add(new Author("Александр", "Пушкин"));
        authors.add(new Author("Антон", "Чехов"));
        return authors;
    }

    private Long authorId(List<Author> authors, String lastName) {
        return authors.stream().filter(x -> x.getLastName().equals(lastName))
                .findFirst().orElseThrow().getId();
    }

    private Long shopId(List<Shop> shops, String name) {
        return shops.stream().filter(x -> x.getName().equals(name))
                .findFirst().orElseThrow().getId();
    }

    private Long bookId(List<Book> books, String title) {
        return books.stream().filter(x -> x.getTitle().equals(title))
                .findFirst().orElseThrow().getId();
    }

    private List<Book> getDefaultBooks(List<Author> authors) {
        List<Book> books = new ArrayList<>();
        books.add(new Book("Война и мир", authorId(authors, "Толстой"), 1869));
        books.add(new Book("Анна Каренина", authorId(authors, "Толстой"), 1877));
        books.add(new Book("Преступление и наказание", authorId(authors, "Достоевский"), 1866));
        books.add(new Book("Идиот", authorId(authors, "Достоевский"), 1869));
        books.add(new Book("Евгений Онегин", authorId(authors, "Пушкин"), 1833));
        books.add(new Book("Капитанская дочка", authorId(authors, "Пушкин"), 1836));
        books.add(new Book("Чайка", authorId(authors, "Чехов"), 1896));
        books.add(new Book("Вишнёвый сад", authorId(authors, "Чехов"), 1904));
        return books;
    }

    private List<Shop> getDefaultShops() {
        List<Shop> shops = new ArrayList<>();
        shops.add(new Shop("Белкнига", "проспект Независимости, 14"));
        shops.add(new Shop("Букинист", "проспект Независимости, 53"));
        shops.add(new Shop("Oz", "улица Сурганова, 21"));
        return shops;
    }

    private List<ShopBook> getDefaultShopBooks(List<Shop> shops, List<Book> books) {
        List<ShopBook> shopBooks = new ArrayList<>();
        shopBooks.add(new ShopBook(shopId(shops, "Белкнига"), bookId(books, "Война и мир")));
        shopBooks.add(new ShopBook(shopId(shops, "Белкнига"), bookId(books, "Чайка")));
        shopBooks.add(new ShopBook(shopId(shops, "Белкнига"), bookId(books, "Капитанская дочка")));
        shopBooks.add(new ShopBook(shopId(shops, "Букинист"), bookId(books, "Анна Каренина")));
        shopBooks.add(new ShopBook(shopId(shops, "Букинист"), bookId(books, "Идиот")));
        shopBooks.add(new ShopBook(shopId(shops, "Букинист"), bookId(books, "Евгений Онегин")));
        shopBooks.add(new ShopBook(shopId(shops, "Oz"), bookId(books, "Преступление и наказание")));
        shopBooks.add(new ShopBook(shopId(shops, "Oz"), bookId(books, "Вишнёвый сад")));
        return shopBooks;
    }

    public void updateAuthorsNameByShop() {
        System.out.print("Введи название магазина (Белкнига / Букинист / Oz): ");
        String shopName = InputManager.getNextLine().trim();
        System.out.print("Введи новое имя для авторов: ");
        String newName = InputManager.getNextLine().trim();
        try {
            int updated = authorDao.updateFirstNameByShop(shopName, newName);
            System.out.println("Обновлено авторов: " + updated);
        } catch (RuntimeException e) {
            logger.severe("Ошибка при обновлении авторов: " + e.getMessage());
        }
    }
}