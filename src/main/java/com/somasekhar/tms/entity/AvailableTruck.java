package com.somasekhar.tms.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "available_trucks")
public class AvailableTruck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "truck_type", nullable = false)
    private String truckType;

    @Column(name = "count", nullable = false)
    private int count;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transporter_id", nullable = false)
    private Transporter transporter;

    public AvailableTruck() {}

    public AvailableTruck(String truckType, int count, Transporter transporter) {
        this.truckType = truckType;
        this.count = count;
        this.transporter = transporter;
    }

    // ----------------------------
    // Getters
    // ----------------------------
    public Long getId() {
        return id;
    }

    public String getTruckType() {
        return truckType;
    }

    public int getCount() {
        return count;
    }

    public Transporter getTransporter() {
        return transporter;
    }

    // Setters

    public void setTruckType(String truckType) {
        this.truckType = truckType;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setTransporter(Transporter transporter) {
        this.transporter = transporter;
    }


    // Helper Method
    public void updateCount(int newCount) {
        this.count = newCount;
    }
}
