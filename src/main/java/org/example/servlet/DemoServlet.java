package org.example.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;
import org.thymeleaf.web.IWebApplication;
import org.thymeleaf.web.servlet.IServletWebExchange;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import org.example.controller.HomeController;
import org.example.controller.LibraryController;
import org.example.controller.IController;

import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

@WebServlet(name="DemoServlet", urlPatterns = "/DemoServlet/*")
public class DemoServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(DemoServlet.class.getName());

    private JakartaServletWebApplication application;
    private ITemplateEngine templateEngine;

    @Override
    public void init() {
        this.application = JakartaServletWebApplication.buildApplication(getServletContext());
        this.templateEngine = buildTemplateEngine(this.application);
        logger.info("DemoServlet initialized");
    }

    private ITemplateEngine buildTemplateEngine(final IWebApplication application) {

        final WebApplicationTemplateResolver resolver =
                new WebApplicationTemplateResolver(application);

        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setPrefix("/WEB-INF/templates/");
        resolver.setSuffix(".html");
        resolver.setCacheable(false);

        final TemplateEngine engine = new TemplateEngine();
        engine.setTemplateResolver(resolver);

        return engine;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("text/html;charset=UTF-8");
        handleCookies(request, response);

        try (Writer writer = response.getWriter()) {

            IController controller;

            String uri = request.getRequestURI();
            String path = uri.replaceFirst(".*/DemoServlet/", "");

            if (path.equals("") || path.equals("/")) {
                controller = new HomeController();
            }
            else if (path.startsWith("library")) {
                controller = new LibraryController(path);
            }
            else {
                controller = new HomeController();
            }

            IServletWebExchange exchange = application.buildExchange(request, response);
            controller.process(exchange, templateEngine, writer);

        } catch (Exception e) {
            logger.severe("Critical error: " + e.getMessage());
            sendErrorPage(response, "Ошибка сервера: " + e.getMessage());
        }
    }

    private void sendErrorPage(HttpServletResponse response, String message) throws IOException {
        if (response.isCommitted()) return;

        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.sendRedirect("/DemoServlet/error?msg=" + message);
    }

    private void handleCookies(HttpServletRequest request, HttpServletResponse response) {

        String lastVisitRaw = null;
        int visits = 0;

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if ("lastVisit".equals(c.getName())) {
                    lastVisitRaw = c.getValue();
                } else if ("visits".equals(c.getName())) {
                    try {
                        visits = Integer.parseInt(c.getValue());
                    } catch (NumberFormatException ignored) {}
                }
            }
        }

        visits++;

        Cookie visitCookie = new Cookie("visits", String.valueOf(visits));
        Cookie dateCookie = new Cookie("lastVisit", String.valueOf(System.currentTimeMillis()));

        visitCookie.setPath("/");
        dateCookie.setPath("/");

        int month = 60 * 60 * 24 * 30;
        visitCookie.setMaxAge(month);
        dateCookie.setMaxAge(month);

        response.addCookie(visitCookie);
        response.addCookie(dateCookie);

        String lastVisitStr = null;
        if (lastVisitRaw != null) {
            try {
                long ts = Long.parseLong(lastVisitRaw);
                Date date = new Date(ts);
                lastVisitStr = new SimpleDateFormat("yyyy-MM-dd").format(date);
            } catch (Exception ignored) {}
        }

        request.setAttribute("lastVisit", lastVisitStr);
        request.setAttribute("visits", visits);
    }
}
