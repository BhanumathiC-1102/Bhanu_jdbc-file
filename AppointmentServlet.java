package com.hospital.controller;

import com.hospital.dao.AppointmentDAO;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * GET lists, POST books, PUT reschedules, DELETE cancels.
 * PUT and DELETE send their values in the query string (?id=..&date=..&time=..),
 * because servlet containers do not parse PUT bodies as form parameters.
 */
@WebServlet("/api/appointments")
public class AppointmentServlet extends BaseServlet {
    private final AppointmentDAO dao = new AppointmentDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        send(res, 200, dao.findAll());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        dao.book(integer(req, "patientId", "patient"), integer(req, "doctorId", "doctor"),
                text(req, "date", "date", 10), text(req, "time", "time", 5));
        ok(res, "Appointment booked");
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res) throws IOException {
        dao.reschedule(integer(req, "id", "appointment"), text(req, "date", "date", 10), text(req, "time", "time", 5));
        ok(res, "Appointment rescheduled");
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res) throws IOException {
        dao.cancel(integer(req, "id", "appointment"));
        ok(res, "Appointment cancelled");
    }
}
