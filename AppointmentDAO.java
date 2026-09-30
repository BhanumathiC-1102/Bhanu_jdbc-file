package com.hospital.dao;

import com.hospital.model.Appointment;
import com.hospital.util.DBConnection;
import com.hospital.util.DataAccessException;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * All JDBC code for the appointments table.
 * Double-booking is blocked by the UNIQUE key in schema.sql, so two people
 * clicking the same slot at once cannot both succeed.
 */
public class AppointmentDAO {
    private static final int DUPLICATE_KEY = 1062;
    private static final int FOREIGN_KEY_MISSING = 1452;

    public List<Appointment> findAll() {
        String sql = "SELECT a.id, a.patient_id, a.doctor_id, p.name AS patient_name, "
                   + "d.name AS doctor_name, d.specialization, a.appt_date, a.appt_time, a.status "
                   + "FROM appointments a "
                   + "JOIN patients p ON p.id = a.patient_id "
                   + "JOIN doctors d ON d.id = a.doctor_id "
                   + "ORDER BY a.appt_date, a.appt_time";
        List<Appointment> list = new ArrayList<>();
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Appointment(
                        rs.getInt("id"), rs.getInt("patient_id"), rs.getInt("doctor_id"),
                        rs.getString("patient_name"), rs.getString("doctor_name"),
                        rs.getString("specialization"),
                        rs.getDate("appt_date").toLocalDate().toString(),
                        rs.getTime("appt_time").toString().substring(0, 5),
                        rs.getString("status")));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not load appointments", e);
        }
        return list;
    }

    public void book(int patientId, int doctorId, String date, String time) {
        LocalDate d = parseDate(date);
        LocalTime t = parseTime(time);
        checkNotInPast(d, t);
        String sql = "INSERT INTO appointments (patient_id, doctor_id, appt_date, appt_time) VALUES (?, ?, ?, ?)";
        try (Connection c = DBConnection.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            ps.setInt(2, doctorId);
            ps.setDate(3, Date.valueOf(d));
            ps.setTime(4, Time.valueOf(t));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw translate(e, "Could not book the appointment");
        }
    }

    public void reschedule(int id, String date, String time) {
        LocalDate d = parseDate(date);
        LocalTime t = parseTime(time);
        checkNotInPast(d, t);
        String sql = "UPDATE appointments SET appt_date = ?, appt_time = ? WHERE id = ? AND status = 'BOOKED'";
        try (Connection c = DBConnection.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(d));
            ps.setTime(2, Time.valueOf(t));
            ps.setInt(3, id);
            if (ps.executeUpdate() == 0) {
                throw new IllegalArgumentException("Only booked appointments can be rescheduled.");
            }
        } catch (SQLException e) {
            throw translate(e, "Could not reschedule the appointment");
        }
    }

    public void cancel(int id) {
        // slot_active = NULL frees the slot for someone else (NULLs never collide in a UNIQUE key).
        String sql = "UPDATE appointments SET status = 'CANCELLED', slot_active = NULL "
                   + "WHERE id = ? AND status = 'BOOKED'";
        try (Connection c = DBConnection.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            if (ps.executeUpdate() == 0) {
                throw new IllegalArgumentException("That appointment is already cancelled or does not exist.");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not cancel the appointment", e);
        }
    }

    private RuntimeException translate(SQLException e, String fallback) {
        if (e.getErrorCode() == DUPLICATE_KEY) {
            return new IllegalArgumentException("That slot is already booked for this doctor. Pick another time.");
        }
        if (e.getErrorCode() == FOREIGN_KEY_MISSING) {
            return new IllegalArgumentException("Choose a valid patient and doctor.");
        }
        return new DataAccessException(fallback, e);
    }

    private LocalDate parseDate(String s) {
        try {
            return LocalDate.parse(s);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Choose a valid date.");
        }
    }

    private LocalTime parseTime(String s) {
        try {
            LocalTime t = LocalTime.parse(s);
            boolean onSlot = (t.getMinute() == 0 || t.getMinute() == 30) && t.getSecond() == 0;
            if (!onSlot || t.getHour() < 9 || t.getHour() > 16) {
                throw new IllegalArgumentException("Appointments run every 30 minutes from 09:00 to 16:30.");
            }
            return t;
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Choose a valid time.");
        }
    }

    private void checkNotInPast(LocalDate d, LocalTime t) {
        if (LocalDateTime.of(d, t).isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("That time has already passed. Choose a later slot.");
        }
    }
}
