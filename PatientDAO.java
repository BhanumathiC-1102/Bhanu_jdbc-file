package com.hospital.dao;

import com.hospital.model.Patient;
import com.hospital.util.DBConnection;
import com.hospital.util.DataAccessException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** All JDBC code for the patients table. */
public class PatientDAO {

    public List<Patient> findAll() {
        List<Patient> list = new ArrayList<>();
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement("SELECT id, name, phone, age FROM patients ORDER BY name");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Patient(rs.getInt("id"), rs.getString("name"),
                        rs.getString("phone"), rs.getInt("age")));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not load patients", e);
        }
        return list;
    }

    public void add(String name, String phone, int age) {
        String sql = "INSERT INTO patients (name, phone, age) VALUES (?, ?, ?)";
        try (Connection c = DBConnection.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, phone);
            ps.setInt(3, age);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Could not add patient", e);
        }
    }

    public void delete(int id) {
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement("DELETE FROM patients WHERE id = ?")) {
            ps.setInt(1, id);
            if (ps.executeUpdate() == 0) {
                throw new IllegalArgumentException("That patient no longer exists.");
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 1451) {
                throw new IllegalArgumentException("This patient has appointments, so they cannot be removed.");
            }
            throw new DataAccessException("Could not delete patient", e);
        }
    }
}
