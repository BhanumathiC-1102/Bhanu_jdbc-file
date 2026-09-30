package com.hospital.model;

/** Model: one row of the patients table. */
public class Patient {
    private final int id;
    private final String name;
    private final String phone;
    private final int age;

    public Patient(int id, String name, String phone, int age) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.age = age;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public int getAge() { return age; }
}
