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
import org.example.controller.IController;
import org.example.controller.LibraryController;

import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

@WebServlet(name="DemoServlet", urlPatterns = "/DemoServlet/*")
public class DemoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private JakartaServletWebApplication application;
    private ITemplateEngine templateEngine;

    @Override
    public void init(){
        this.application = JakartaServletWebApplication.buildApplication(getServletContext());

        this.templateEngine = buildTemplateEngine(this.application);

    }

    private ITemplateEngine buildTemplateEngine(final IWebApplication application) {
        final WebApplicationTemplateResolver templateResolver = new WebApplicationTemplateResolver(application);

        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setPrefix("/WEB-INF/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setCacheTTLMs(Long.valueOf(3600000L));

        templateResolver.setCacheable(true);

        final TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        return templateEngine;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("text/html;charset=UTF-8");

        handleCookies(request, response);


        try (Writer writer = response.getWriter()) {

            IController controller;

            String requestURI = request.getRequestURI();
            String query = request.getQueryString();

            if ("/DemoServlet".equals(requestURI) && query == null) {
                controller = new HomeController();
            }
            else {
                controller = new LibraryController();
            }

            final IServletWebExchange webExchange = this.application.buildExchange(request, response);

            controller.process(webExchange, templateEngine, writer);

        }  catch (Exception e) {
            e.printStackTrace();
            if (!response.isCommitted()) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            }
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
                Date date = new Date(ts);

                SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
                lastVisitStr = fmt.format(date);
            } catch (Exception ignored) {}
        }

        request.setAttribute("lastVisit", lastVisitStr);
        request.setAttribute("visits", visits);
    }

}