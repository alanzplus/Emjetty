package org.zlambda.projects.emjetty.examples;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(
        name = "HelloworldServlet",
        urlPatterns = {"/hello"}
)
public class HelloworldServlet extends HttpServlet {
    private static final Logger LOGGER = LogManager.getLogger(HelloworldServlet.class);
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LOGGER.info("get HttpRequest {}", req);
        resp.getWriter().println("hello world emjetty");
    }
}
