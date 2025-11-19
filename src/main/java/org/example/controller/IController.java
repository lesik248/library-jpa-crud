package org.example.controller;

import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.web.servlet.IServletWebExchange;

import java.io.Writer;

public interface IController {
    void process(IServletWebExchange webExchange,
                 ITemplateEngine templateEngine,
                 Writer writer) throws Exception;
}
