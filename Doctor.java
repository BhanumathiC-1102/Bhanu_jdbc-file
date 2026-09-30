package com.hospital.model;

/** Model: one row of the doctors table. */
public class Doctor {
    private final int id;
    private final String name;
    private final String specialization;
    private final double fee;

    public Doctor(int id, String name, String specialization, double fee) {
        this.id = id;
        this.name = name;
        this.specialization = specialization;
        this.fee = fee;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getSpecialization() { return specialization; }
    public double getFee() { return fee; }
}
