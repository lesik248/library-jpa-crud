package org.example;

import org.example.model.Book;
import org.example.service.LibraryService;
import org.example.service.LibraryServiceException;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class App
{
    public static void main( String[] args ) {
        LibraryService libraryService;
        libraryService = new LibraryService();

        Scanner myObj = new Scanner(System.in);
        String choice;
        System.out.println("Библиотека");
        while (true) {
            System.out.println("\"1\" - Вывести информацию о наличии свободных экземпляров заданной книги.");
            System.out.println("\"2\" - Вывести информацию о читателях, которые имеют задолженность более 1 месяца.");
            System.out.println("\"3\" - Вывести информацию о книгах заданного автора.");
            System.out.println("\"4\" - Выдать книгу читателю.");
            System.out.println("\"5\" - Cписать экземпляр книги.");
            System.out.println("\"0\" - Выйти из программы.");
            System.out.println("Введите команду: ");
            choice = myObj.nextLine();
            try {
                switch (choice) {
                    case "1": {
                        System.out.println("Введите автора книги: ");
                        String author = myObj.nextLine();
                        System.out.println("Введите название книги: ");
                        String bookTitle = myObj.nextLine();
                        System.out.println("Свободных копий: " + libraryService.getFreeCopiesOfBook(author, bookTitle));
                        break;
                    }
                    case "2": {
                        System.out.println("Читатели с задолженностью более 1 месяца: ");
                        libraryService.getReadersWithDebt().forEach(System.out::println);
                        break;
                    }
                    case "3": {
                        System.out.println("Введите автора: ");
                        String author = myObj.nextLine();
                        HashMap<Book, Integer> bookInfo = libraryService.getBooksForAuthor(author);
                        System.out.println("Книги автора " + author + ":");
                        for (Map.Entry<Book, Integer> entry : bookInfo.entrySet()) {
                            Book book = entry.getKey();
                            Integer freeCopies = entry.getValue();

                            System.out.println(book.getTitle() + ", свободных копий: " + freeCopies);
                        }
                        break;
                    }
                    case "4": {
                        System.out.println("Введите имя: ");
                        String name = myObj.nextLine();
                        System.out.println("Введите автора: ");
                        String author = myObj.nextLine();
                        System.out.println("Введите название книги: ");
                        String title = myObj.nextLine();
                        libraryService.giveBook(name, author, title);
                        System.out.println("Книга успешно выдана");
                        break;
                    }
                    case "5": {
                        System.out.println("Введите автора: ");
                        String author = myObj.nextLine();
                        System.out.println("Введите название книги: ");
                        String title = myObj.nextLine();
                        libraryService.removeBook(author, title);
                        System.out.println("Экземпляр книги успешно списан");
                        break;
                    }
                    case "0": {
                        return;
                    }
                    default: {
                        System.out.println("Неправильная команда");
                    }
                }
            } catch (LibraryServiceException e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        }
    }
}
