package org.example.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import org.example.controller.HomeController;
import org.example.controller.IController;
import org.example.controller.LibraryController;
import org.example.controller.AuthController;

import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.web.IWebApplication;
import org.thymeleaf.web.servlet.IServletWebExchange;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

// Фильтр перехватывает все запросы
@WebFilter("/*")
public class FrontControllerFilter implements Filter {

    private static final Logger logger = Logger.getLogger(FrontControllerFilter.class.getName());

    private JakartaServletWebApplication application;
    private ITemplateEngine templateEngine;

    // init() — запускается один раз при старте приложения
    @Override
    public void init(FilterConfig filterConfig) {
        this.application = JakartaServletWebApplication.buildApplication(
                filterConfig.getServletContext()
        );
        this.templateEngine = buildTemplateEngine(this.application);
        logger.info("FrontControllerFilter initialized");
    }

    // Все HTML лежат в /WEB-INF/templates/
    private ITemplateEngine buildTemplateEngine(final IWebApplication application) {
        WebApplicationTemplateResolver resolver = new WebApplicationTemplateResolver(application);

        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setPrefix("/WEB-INF/templates/");
        resolver.setSuffix(".html");
        resolver.setCacheable(false);

        TemplateEngine engine = new TemplateEngine();
        engine.setTemplateResolver(resolver);

        return engine;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {

        req.setCharacterEncoding("UTF-8");

        HttpServletResponse response = (HttpServletResponse) resp;
        HttpServletRequest request = (HttpServletRequest) req;

        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        String uri = request.getRequestURI();
        String path = uri.substring(request.getContextPath().length());

        // если приходит запрос на статический файл, мы передаем его DefaultServlet
        // и он просто возвращает файл
        if (path.startsWith("/css")) {
            chain.doFilter(req, resp);
            return;
        }

        handleCookies(request, response);

        HttpSession session = request.getSession(false);

        // Если пользователь не имеет права — отправляем на home.
        String role = "guest";
        if (session != null && session.getAttribute("role") != null) {
            role = (String) session.getAttribute("role");
        }
        // Если роли нет → роль "guest".

        // Проверяем совпадение URL и роли
        if (!checkAccess(path, role)) {
            response.sendRedirect("/lab4_Vyshnikova/home");
            return;
        }

        // выбор контроллера для URL
        try (Writer writer = response.getWriter()) {

            IController controller;

            if (path.equals("/") || path.equals("")) {
                controller = new HomeController();

            } else if (path.startsWith("/library")) {
                controller = new LibraryController(path.substring(1));

            } else if (path.startsWith("/auth")) {
                controller = new AuthController(path.substring(1));

            } else {
                controller = new HomeController();
            }

            IServletWebExchange exchange = application.buildExchange(request, response);
            // Контроллер сам рендерит нужный HTML-шаблон.
            controller.process(exchange, templateEngine, writer);

        } catch (Exception e) {
            logger.severe("Critical filter error: " + e.getMessage());
            sendErrorPage(response, "Ошибка сервера: " + e.getMessage());
        }
    }

    private boolean checkAccess(String path, String role) {

        // эти URL доступны всем
        if (path.startsWith("/auth") || path.equals("/") || path.equals("/home")) {
            return true;
        }

        if (path.startsWith("/library")) {

            // админ может всё
            if (role.equals("admin")) return true;

            // пользователь может только это:
            if (role.equals("user")) {
                return path.startsWith("/library/freeCopies")
                        || path.startsWith("/library/booksByAuthor");
            }

            return false;
        }

        return true;
    }

    private void sendErrorPage(HttpServletResponse response, String message) throws IOException {
        if (!response.isCommitted()) {
            response.sendRedirect("/error?msg=" + message);
        }
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
                lastVisitStr = new SimpleDateFormat("yyyy-MM-dd").format(new Date(ts));
            } catch (Exception ignored) {}
        }

        request.setAttribute("lastVisit", lastVisitStr);
        request.setAttribute("visits", visits);
    }
}

