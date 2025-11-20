package org.example.controller;

import org.example.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.IWebRequest;
import org.thymeleaf.web.servlet.IServletWebExchange;

import java.io.Writer;

public class LibraryController implements IController {

    private static final Logger logger = LoggerFactory.getLogger(LibraryController.class);

    private final String path;

    public LibraryController(String path) {
        this.path = path;
    }

    @Override
    public void process(IServletWebExchange exchange,
                        ITemplateEngine engine,
                        Writer writer) throws Exception {

        WebContext ctx = new WebContext(exchange, exchange.getLocale());
        IWebRequest request = exchange.getRequest();

        LibraryService service = new LibraryService();

        String[] parts = path.split("/");
        if (parts.length < 2) {
            sendError(engine, writer, exchange, "Некорректный путь");
            return;
        }

        String action = parts[1];

        try {

            switch (action) {

                case "freeCopies":
                    try {
                        handleFreeCopies(request, ctx, service);
                    } catch (LibraryNotFoundException e) {
                        ctx.setVariable("error", e.getMessage());
                    }
                    engine.process("free-copies", ctx, writer);
                    return;

                case "booksByAuthor":
                    try {
                        handleBooksByAuthor(request, ctx, service);
                    } catch (LibraryNotFoundException e) {
                        ctx.setVariable("error", e.getMessage());
                    }
                    engine.process("books-author", ctx, writer);
                    return;

                case "readersWithDebt":
                    handleReadersWithDebt(ctx, service);
                    engine.process("readers-debt", ctx, writer);
                    return;

                case "giveBook":
                    try {
                        handleGiveBook(request, ctx, service);
                    } catch (LibraryNotFoundException e) {
                        ctx.setVariable("error", e.getMessage());
                    }
                    engine.process("give-book", ctx, writer);
                    return;

                case "removeBook":
                    try {
                        handleRemoveBook(request, ctx, service);
                    } catch (LibraryNotFoundException e) {
                        ctx.setVariable("error", e.getMessage());
                    }
                    engine.process("remove-book", ctx, writer);
                    return;

                default:
                    ctx.setVariable("message", "Неизвестное действие");
                    engine.process("error", ctx, writer);
            }

        } catch (LibraryDatabaseException e) {
            logger.error("Database error", e);
            sendError(engine, writer, exchange, "Ошибка базы данных");

        } catch (Exception e) {
            logger.error("Critical error in LibraryController", e);
            sendError(engine, writer, exchange, "Критическая ошибка сервера");
        }
    }

    private void handleFreeCopies(IWebRequest request,
                                  WebContext ctx,
                                  LibraryService service) {

        String title = request.getParameterValue("book");
        String author = request.getParameterValue("author");

        if (title == null || author == null) return;

        ctx.setVariable("data", service.getFreeCopiesOfBook(author, title));
    }

    private void handleBooksByAuthor(IWebRequest request,
                                     WebContext ctx,
                                     LibraryService service) {

        String author = request.getParameterValue("author");

        if (author == null) return;

        ctx.setVariable("data", service.getBooksForAuthor(author));
    }

    private void handleReadersWithDebt(WebContext ctx,
                                       LibraryService service) {

        ctx.setVariable("data", service.getReadersWithDebt());
    }

    private void handleGiveBook(IWebRequest request,
                                WebContext ctx,
                                LibraryService service) {

        String name = request.getParameterValue("name");
        String author = request.getParameterValue("author");
        String book = request.getParameterValue("book");

        if (name == null || author == null || book == null) return;

        service.giveBook(name, author, book);
        ctx.setVariable("data", "Книга выдана!");
    }

    private void handleRemoveBook(IWebRequest request,
                                  WebContext ctx,
                                  LibraryService service) {

        String author = request.getParameterValue("author");
        String book = request.getParameterValue("book");

        if (author == null || book == null) return;

        service.removeBook(author, book);
        ctx.setVariable("data", "Книга удалена.");
    }

    private void sendError(ITemplateEngine engine,
                           Writer writer,
                           IServletWebExchange exchange,
                           String message) {

        WebContext ctx = new WebContext(exchange, exchange.getLocale());
        ctx.setVariable("message", message);
        engine.process("error", ctx, writer);
    }
}
