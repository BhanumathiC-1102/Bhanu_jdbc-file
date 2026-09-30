package com.hospital.controller;

import com.google.gson.Gson;
import com.hospital.util.DataAccessException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

/** Shared helpers for every controller: JSON output, parameter checks, error handling. */
public abstract class BaseServlet extends HttpServlet {
    private static final Gson GSON = new Gson();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        try {
            super.service(req, res);
        } catch (IllegalArgumentException e) {          // validation or business-rule problem
            send(res, 400, Collections.singletonMap("error", e.getMessage()));
        } catch (DataAccessException e) {                // JDBC problem
            log(e.getMessage(), e);
            send(res, 500, Collections.singletonMap("error",
                    "Database error. Check that MySQL is running and db.properties is correct."));
        }
    }

    protected void send(HttpServletResponse res, int status, Object body) throws IOException {
        res.setStatus(status);
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        res.getWriter().write(GSON.toJson(body));
    }

    protected void ok(HttpServletResponse res, String message) throws IOException {
        send(res, 200, Collections.singletonMap("message", message));
    }

    protected String text(HttpServletRequest req, String name, String label, int maxLength) {
        String v = req.getParameter(name);
        if (v == null || v.trim().isEmpty()) {
            throw new IllegalArgumentException("Enter the " + label + ".");
        }
        v = v.trim();
        if (v.length() > maxLength) {
            throw new IllegalArgumentException("The " + label + " must be " + maxLength + " characters or fewer.");
        }
        return v;
    }

    protected int integer(HttpServletRequest req, String name, String label) {
        try {
            return Integer.parseInt(text(req, name, label, 12));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("The " + label + " must be a whole number.");
        }
    }

    protected double decimal(HttpServletRequest req, String name, String label) {
        try {
            return Double.parseDouble(text(req, name, label, 12));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("The " + label + " must be a number.");
        }
    }
}
