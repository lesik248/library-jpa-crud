package org.example.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.example.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.IWebRequest;
import org.thymeleaf.web.servlet.IServletWebExchange;

import java.io.Writer;

public class AuthController implements IController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final String path;

    public AuthController(String path) {
        this.path = path;
    }

    @Override
    public void process(IServletWebExchange exchange,
                        ITemplateEngine engine,
                        Writer writer) throws Exception {

        WebContext ctx = new WebContext(exchange, exchange.getLocale());
        IWebRequest request = exchange.getRequest();

        AuthService service = new AuthService();

        String[] parts = path.split("/");
        if (parts.length < 2) {
            throw new Exception("Invalid auth path");
        }

        String action = parts[1];
        String templateName;

        try {
            switch (action) {
                case "register":
                    templateName = handleRegister(request, ctx, service);
                    break;

                case "sign-in":
                    templateName = handleSignIn(exchange, request, ctx, service);
                    break;

                case "sign-out":
                    templateName = handleSignOut(exchange, ctx);
                    break;

                default:
                    ctx.setVariable("error", "Unknown auth action");
                    engine.process("error", ctx, writer);
                    return;
            }

            engine.process(templateName, ctx, writer);

        } catch (Exception e) {
            logger.error("Auth error", e);
            ctx.setVariable("error", e.getMessage());
            engine.process("error", ctx, writer);
        }
    }

    private String handleRegister(IWebRequest request,
                                  WebContext ctx,
                                  AuthService service) {

        String username = request.getParameterValue("username");
        String password = request.getParameterValue("password");

        if (username != null && password != null) {

            boolean ok = service.register(username, password);

            if (ok) {
                ctx.setVariable("message", "Registration successful!");
            } else {
                ctx.setVariable("error", "User already exists");
            }
        }

        return "register";
    }

    private String handleSignIn(IServletWebExchange exchange,
                                IWebRequest request,
                                WebContext ctx,
                                AuthService service) {

        String username = request.getParameterValue("username");
        String password = request.getParameterValue("password");

        if (username != null && password != null) {

            boolean ok = service.signIn(username, password);

            if (ok) {
                HttpServletRequest httpReq =
                        (HttpServletRequest) exchange.getNativeRequestObject(); // ✔ ВАЖНО

                HttpSession session = httpReq.getSession(true);
                session.setAttribute("user", username);
                session.setAttribute("role", service.getRole(username));

                ctx.setVariable("message", "Signed in!");
            } else {
                ctx.setVariable("error", "Invalid credentials");
            }
        }

        return "sign-in";
    }
    private String handleSignOut(IServletWebExchange exchange, WebContext ctx) {

        HttpServletRequest httpReq =
                (HttpServletRequest) exchange.getNativeRequestObject(); // ✔

        HttpSession session = httpReq.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        ctx.setVariable("message", "You have been signed out.");
        return "sign-out";
    }

}
