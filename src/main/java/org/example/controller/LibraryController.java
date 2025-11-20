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
            throw new Exception("Некорректный путь");
        }

        String action = parts[1];
        String templateName = null;

        try {

            switch (action) {

                case "freeCopies":
                    handleFreeCopies(request, ctx, service);
                    templateName = "free-copies";
                    break;

                case "booksByAuthor":
                    handleBooksByAuthor(request, ctx, service);
                    templateName = "books-author";
                    break;

                case "readersWithDebt":
                    handleReadersWithDebt(ctx, service);
                    templateName = "readers-debt";
                    break;

                case "giveBook":
                    handleGiveBook(request, ctx, service);
                    templateName = "give-book";
                    break;

                case "removeBook":
                    handleRemoveBook(request, ctx, service);
                    templateName = "remove-book";
                    break;

                default:
                    ctx.setVariable("message", "Неизвестное действие");
                    engine.process("error", ctx, writer);
                    return;
            }

            engine.process(templateName, ctx, writer);

        } catch (LibraryNotFoundException e) {

            logger.warn("LibraryNotFoundException: {}", e.getMessage());
            ctx.setVariable("error", e.getMessage());

            engine.process(templateName, ctx, writer);

        } catch (LibraryDatabaseException e) {

            logger.error("Database error", e);
            throw e;

        } catch (Exception e) {

            logger.error("Critical error in LibraryController", e);
            throw e;
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
}
