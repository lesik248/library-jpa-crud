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

@WebFilter("/*")
public class FrontControllerFilter implements Filter {

    private static final Logger logger = Logger.getLogger(FrontControllerFilter.class.getName());

    private JakartaServletWebApplication application;
    private ITemplateEngine templateEngine;

    @Override
    public void init(FilterConfig filterConfig) {
        this.application = JakartaServletWebApplication.buildApplication(
                filterConfig.getServletContext()
        );
        this.templateEngine = buildTemplateEngine(this.application);
        logger.info("FrontControllerFilter initialized");
    }

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

        if (uri.startsWith("/lab4_Vyshnikova/css")
                || uri.startsWith("/lab4_Vyshnikova/js")
                || uri.startsWith("/lab4_Vyshnikova/images")) {
            chain.doFilter(req, resp);
            return;
        }

        handleCookies(request, response);

        try (Writer writer = response.getWriter()) {

            IController controller;

            String path = uri.substring(request.getContextPath().length());

            if (path.equals("/") || path.equals("")) {
                controller = new HomeController();

            } else if (path.startsWith("/library")) {
                controller = new LibraryController(path.substring(1));   // library/...

            } else if (path.startsWith("/auth")) {
                controller = new AuthController(path.substring(1));      // auth/...

            } else {
                controller = new HomeController();
            }

            IServletWebExchange exchange = application.buildExchange(request, response);
            controller.process(exchange, templateEngine, writer);

        } catch (Exception e) {
            logger.severe("Critical filter error: " + e.getMessage());
            sendErrorPage(response, "Ошибка сервера: " + e.getMessage());
        }
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

