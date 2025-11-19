package org.example.controller;

import org.example.controller.IController;
import org.example.service.LibraryService;
import org.example.service.LibraryServiceException;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.servlet.IServletWebExchange;

import java.io.Writer;

public class LibraryController implements IController {
    @Override
    public void process(IServletWebExchange webExchange,
                        ITemplateEngine templateEngine,
                        Writer writer) throws Exception {

        var request = webExchange.getRequest();

        LibraryService libraryService = new LibraryService();

        String action = request.getParameterValue("action");
        String bookTitle = request.getParameterValue("book");
        String author = request.getParameterValue("author");

        Object data = null;

        if ("freeCopies".equalsIgnoreCase(action)) {
            try {
                data = libraryService.getFreeCopiesOfBook(author, bookTitle);
            }
            catch (LibraryServiceException e){
                data = e.getMessage();
            }
        }
        else if ("getReadersWithDebt".equalsIgnoreCase(action)) {
            try {
                data = libraryService.getReadersWithDebt();
            }
            catch (LibraryServiceException e){
                data = e.getMessage();
            }
        }
        else if ("getForLanguage".equalsIgnoreCase(action)) {
            try {
                data = libraryService.getBooksForAuthor(author);
            }
            catch (LibraryServiceException e){
                data = e.getMessage();
            }
        }
        else if ("giveBook".equalsIgnoreCase(action)) {
            String readerName = request.getParameterValue("name");
            try {
                libraryService.giveBook(readerName, author, bookTitle);
                data = "Книга выдана успешно!";
            }
            catch (LibraryServiceException e){
                data = e.getMessage();
            }
        }
        else if ("removeBook".equalsIgnoreCase(action)) {
            try {
                libraryService.removeBook(author, bookTitle);
                data = "Экземпляр книги списан успешно!";
            }
            catch (LibraryServiceException e){
                data = e.getMessage();
            }
        }
        else {
            data = "Неверный параметр action";
        }

        WebContext context = new WebContext(webExchange, webExchange.getLocale());
        context.setVariable("data", data);

        templateEngine.process("library", context, writer);
    }
}
