package com.hospital.model;

/** Model: an appointment joined with the patient and doctor details. */
public class Appointment {
    private final int id;
    private final int patientId;
    private final int doctorId;
    private final String patientName;
    private final String doctorName;
    private final String specialization;
    private final String date;   // yyyy-MM-dd
    private final String time;   // HH:mm
    private final String status; // BOOKED or CANCELLED

    public Appointment(int id, int patientId, int doctorId, String patientName, String doctorName,
                       String specialization, String date, String time, String status) {
        this.id = id;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.patientName = patientName;
        this.doctorName = doctorName;
        this.specialization = specialization;
        this.date = date;
        this.time = time;
        this.status = status;
    }

    public int getId() { return id; }
    public int getPatientId() { return patientId; }
    public int getDoctorId() { return doctorId; }
    public String getPatientName() { return patientName; }
    public String getDoctorName() { return doctorName; }
    public String getSpecialization() { return specialization; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getStatus() { return status; }
}
