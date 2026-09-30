package com.hospital.controller;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Collections;

/** GET checks the session, POST signs in, DELETE signs out. */
@WebServlet("/api/login")
public class LoginServlet extends BaseServlet {
    // Demo credentials for the college project. Replace with a users table for real use.
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "admin123";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        HttpSession s = req.getSession(false);
        boolean in = s != null && s.getAttribute("user") != null;
        send(res, 200, Collections.singletonMap("signedIn", in));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String user = text(req, "username", "username", 40);
        String pass = text(req, "password", "password", 80);
        if (!USERNAME.equals(user) || !PASSWORD.equals(pass)) {
            send(res, 401, Collections.singletonMap("error", "Wrong username or password."));
            return;
        }
        req.getSession(true).setAttribute("user", user);
        ok(res, "Signed in");
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res) throws IOException {
        HttpSession s = req.getSession(false);
        if (s != null) s.invalidate();
        ok(res, "Signed out");
    }
}
