package com.hospital.controller;

import com.hospital.dao.DoctorDAO;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/api/doctors")
public class DoctorServlet extends BaseServlet {
    private final DoctorDAO dao = new DoctorDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String spec = req.getParameter("spec");
        send(res, 200, dao.findAll(spec == null ? "" : spec.trim()));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String name = text(req, "name", "doctor's name", 80);
        String spec = text(req, "specialization", "specialization", 60);
        double fee = decimal(req, "fee", "consultation fee");
        if (fee < 0) throw new IllegalArgumentException("The consultation fee cannot be negative.");
        dao.add(name, spec, fee);
        ok(res, "Doctor added");
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res) throws IOException {
        dao.delete(integer(req, "id", "doctor"));
        ok(res, "Doctor removed");
    }
}
