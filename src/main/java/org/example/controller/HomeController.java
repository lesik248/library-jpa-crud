package org.example.controller;

import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.servlet.IServletWebExchange;

import java.io.Writer;

public class HomeController implements IController {

    @Override
    public void process(IServletWebExchange webExchange,
                        ITemplateEngine templateEngine,
                        Writer writer) throws Exception {

        WebContext context = new WebContext(webExchange, webExchange.getLocale());

        context.setVariable("title", "Library Application");
        context.setVariable("message", "Электронная библиотека");

        templateEngine.process("home", context, writer);
    }
}
