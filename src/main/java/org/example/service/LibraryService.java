package org.example.service;

import jakarta.persistence.PersistenceException;
import org.example.dao.DAOBook;
import org.example.dao.DAOLog;
import org.example.dao.DAOReader;
import org.example.model.Book;
import org.example.model.Log;
import org.example.model.Reader;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LibraryService {

    private static final Logger logger = Logger.getLogger(LibraryService.class.getName());

    private final DAOBook daoBook;
    private final DAOLog daoLog;
    private final DAOReader daoReader;

    public LibraryService() {
        daoBook = new DAOBook();
        daoLog = new DAOLog();
        daoReader = new DAOReader();
    }

    private Book getBookByTitle(String title, String author) {
        try {
            logger.info("Поиск книги: \"" + title + "\" автора \"" + author + "\"");
            List<Book> books = daoBook.getAll();

            for (Book b : books) {
                if (b.getTitle().equalsIgnoreCase(title) && b.getAuthor().equalsIgnoreCase(author)) {
                    logger.info("Книга найдена: " + b);
                    return b;
                }
            }

            throw new LibraryNotFoundException("Книга \"" + title + "\" автора \"" + author + "\" не найдена");

        } catch (PersistenceException e) {
            throw new LibraryDatabaseException("Ошибка БД при поиске книги", e);
        }
    }

    private Reader getReaderByNameInternal(String name) {
        try {
            logger.info("Поиск читателя: " + name);
            List<Reader> readers = daoReader.getAll();

            for (Reader r : readers) {
                if (r.getName().equalsIgnoreCase(name)) {
                    logger.info("Читатель найден: " + r);
                    return r;
                }
            }

            logger.info("Читатель не найден: " + name);
            return null;

        } catch (PersistenceException e) {
            throw new LibraryDatabaseException("Ошибка БД при поиске читателя", e);
        }
    }

    public int getFreeCopiesOfBook(String author, String title) {
        try {
            logger.info("Подсчёт свободных копий книги \"" + title + "\" автора \"" + author + "\"");
            Book book = getBookByTitle(title, author);

            int free = book.getCopies();
            List<Log> logs = daoLog.getAll();

            for (Log log : logs) {
                if (log.getBookId() == book.getId()) {
                    free--;
                }
            }

            logger.info("Свободных копий найдено: " + free);
            return free;

        } catch (LibraryNotFoundException e) {
            throw e;
        } catch (PersistenceException e) {
            throw new LibraryDatabaseException("Ошибка БД при подсчёте копий", e);
        }
    }

    public List<Reader> getReadersWithDebt() {
        try {
            logger.info("Поиск читателей с задолженностью");
            List<Log> logs = daoLog.getAll();
            List<Reader> result = new ArrayList<>();
            LocalDate today = LocalDate.now();

            for (Log log : logs) {
                long days = ChronoUnit.DAYS.between(log.getReturnDate(), today);
                if (days > 30) {
                    Reader r = daoReader.read(log.getReaderId());
                    if (r != null) {
                        result.add(r);
                        logger.info("Читатель с задолженностью: " + r);
                    }
                }
            }

            return result;

        } catch (PersistenceException e) {
            throw new LibraryDatabaseException("Ошибка БД при поиске должников", e);
        }
    }

    public Map<Book, Integer> getBooksForAuthor(String author) {
        try {
            logger.info("Поиск книг автора: " + author);
            Map<Book, Integer> result = new HashMap<>();
            List<Book> books = daoBook.getAll();

            for (Book b : books) {
                if (b.getAuthor().equalsIgnoreCase(author)) {
                    int free = getFreeCopiesOfBook(author, b.getTitle());
                    result.put(b, free);
                    logger.info("Книга: " + b + ", свободно: " + free);
                }
            }

            if (result.isEmpty()) {
                logger.warning("Книг автора \"" + author + "\" не найдено");
                throw new LibraryNotFoundException("Книг автора \"" + author + "\" не найдено");
            }

            return result;

        } catch (LibraryNotFoundException e) {
            throw e;
        } catch (PersistenceException e) {
            throw new LibraryDatabaseException("Ошибка БД при поиске книг автора", e);
        }
    }

    public void giveBook(String readerName, String author, String title) {
        try {
            logger.info("Выдача книги \"" + title + "\" автору \"" + author + "\" читателю \"" + readerName + "\"");
            Book book = getBookByTitle(title, author);

            Reader reader = getReaderByNameInternal(readerName);
            if (reader == null) {
                logger.info("Создание нового читателя: " + readerName);
                daoReader.create(new Reader(0, readerName));
                reader = getReaderByNameInternal(readerName);
            }

            LocalDate issue = LocalDate.now();
            LocalDate ret = issue.plusWeeks(2);

            Log log = new Log(null, book.getId(), reader.getId(), issue.toString(), ret, 0);
            daoLog.create(log);
            logger.info("Книга выдана, запись в журнале: " + log);

        } catch (LibraryNotFoundException e) {
            throw e;
        } catch (PersistenceException e) {
            throw new LibraryDatabaseException("Ошибка БД при выдаче книги", e);
        }
    }

    public void removeBook(String author, String title) {
        try {
            logger.info("Списание книги \"" + title + "\" автора \"" + author + "\"");
            if (getFreeCopiesOfBook(author, title) > 0) {
                Book book = getBookByTitle(title, author);
                book.setCopies(book.getCopies() - 1);
                daoBook.update(book);
                logger.info("Обновлённое количество копий книги: " + book.getCopies());

                if (book.getCopies() == 0) {
                    daoBook.delete(book.getId());
                    logger.info("Книга полностью списана, удалена из базы: " + book);
                }
            } else {
                throw new LibraryNotFoundException("Нет свободных экземпляров для списания");
            }

        } catch (LibraryNotFoundException e) {
            throw e;
        } catch (PersistenceException e) {
            throw new LibraryDatabaseException("Ошибка БД при списании книги", e);
        }
    }

}
