package com.somasekhar.tms.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "transporters")
public class Transporter {

    @Id
    @GeneratedValue
    @Column(name = "transporter_id", nullable = false, updatable = false)
    private UUID transporterId;

    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(name = "rating", nullable = false)
    private double rating; // 1–5

    @OneToMany(mappedBy = "transporter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AvailableTruck> availableTrucks = new ArrayList<>();

    public Transporter() {}

    public Transporter(String companyName, double rating) {
        this.companyName = companyName;
        this.rating = rating;
    }

    // -------------------
    // Helper Methods
    // -------------------
    public void addAvailableTruck(AvailableTruck truck) {
        truck.setTransporter(this);   // VERY IMPORTANT
        availableTrucks.add(truck);
    }


    // Getters

    public UUID getTransporterId() {
        return transporterId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public double getRating() {
        return rating;
    }

    public List<AvailableTruck> getAvailableTrucks() {
        return availableTrucks;
    }
    //setters
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void setAvailableTrucks(List<AvailableTruck> availableTrucks) {
        this.availableTrucks = availableTrucks;
    }
}
