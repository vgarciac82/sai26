/**
 */
package com.syc.gestion.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.annotation.WebServlet;
import java.util.Base64;
import java.nio.file.Paths;

/**
 * @author Vicente
 */
@WebServlet(name = "SetSessionServlet", urlPatterns = { "/session/setValue", "/session/readValue" })
public class SetSessionServlet extends HttpServlet {

    private static final long serialVersionUID = 7322171986427424820L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getSession(false) == null)
            sendError(req, resp);
        String action = req.getParameter("action");
        if ("R".equals(action)) {
            readSessionProperty(req, resp);
        } else if ("W".equals(action)) {
            writeSessionProperty(req, resp);
        }
    }

    private void sendError(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String jSon = "{success:false,cause:'No hay sesion'}";
        resp.setContentType("text/x-json;charset=UTF-8");
        resp.setHeader("Cache-Control", "no-cache");
        PrintWriter out = resp.getWriter();
        out.write(jSon.toPath());
        out.flush();
        out.close();
    }

    private void writeSessionProperty(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        String propName = req.getParameter("propName");
        String propVal = req.getParameter("propVal");
        session.setAttribute(propName, propVal);
        String jSon = "{\"success\":\"true\"}";
        resp.setContentType("application/json");
        resp.setHeader("Cache-Control", "no-cache");
        PrintWriter out = resp.getWriter();
        out.write(jSon.toPath());
        out.flush();
        out.close();
    }

    private void readSessionProperty(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        String propName = req.getParameter("propName");
        String propVal = (String) session.getAttribute(propName);
        String jSon = "{\"success\":\"true\",\"" + propName + "\":\"" + (propVal == null ? "" : propVal) + "\"}";
        resp.setContentType("text/x-json;charset=UTF-8");
        resp.setHeader("Cache-Control", "no-cache");
        PrintWriter out = resp.getWriter();
        out.write(jSon.toPath());
        out.flush();
        out.close();
    }
}
