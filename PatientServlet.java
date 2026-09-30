package com.hospital.controller;

import com.hospital.dao.PatientDAO;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/api/patients")
public class PatientServlet extends BaseServlet {
    private final PatientDAO dao = new PatientDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        send(res, 200, dao.findAll());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String name = text(req, "name", "patient's name", 80);
        String phone = text(req, "phone", "phone number", 15);
        if (!phone.matches("[0-9]{10,15}")) {
            throw new IllegalArgumentException("The phone number must have 10 to 15 digits.");
        }
        int age = integer(req, "age", "age");
        if (age < 0 || age > 120) throw new IllegalArgumentException("The age must be between 0 and 120.");
        dao.add(name, phone, age);
        ok(res, "Patient added");
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res) throws IOException {
        dao.delete(integer(req, "id", "patient"));
        ok(res, "Patient removed");
    }
}
