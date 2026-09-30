package com.hospital.dao;

import com.hospital.model.Doctor;
import com.hospital.util.DBConnection;
import com.hospital.util.DataAccessException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** All JDBC code for the doctors table. */
public class DoctorDAO {

    public List<Doctor> findAll(String specialization) {
        String sql = "SELECT id, name, specialization, fee FROM doctors "
                   + "WHERE specialization LIKE ? ORDER BY name";
        List<Doctor> list = new ArrayList<>();
        try (Connection c = DBConnection.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "%" + specialization + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Doctor(rs.getInt("id"), rs.getString("name"),
                            rs.getString("specialization"), rs.getDouble("fee")));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not load doctors", e);
        }
        return list;
    }

    public void add(String name, String specialization, double fee) {
        String sql = "INSERT INTO doctors (name, specialization, fee) VALUES (?, ?, ?)";
        try (Connection c = DBConnection.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, specialization);
            ps.setDouble(3, fee);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Could not add doctor", e);
        }
    }

    public void delete(int id) {
        try (Connection c = DBConnection.get();
             PreparedStatement ps = c.prepareStatement("DELETE FROM doctors WHERE id = ?")) {
            ps.setInt(1, id);
            if (ps.executeUpdate() == 0) {
                throw new IllegalArgumentException("That doctor no longer exists.");
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 1451) { // foreign key: still referenced by appointments
                throw new IllegalArgumentException("This doctor has appointments, so they cannot be removed.");
            }
            throw new DataAccessException("Could not delete doctor", e);
        }
    }
}
