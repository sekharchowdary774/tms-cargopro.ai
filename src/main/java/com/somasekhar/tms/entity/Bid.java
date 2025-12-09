package com.somasekhar.tms.entity;

import com.somasekhar.tms.enums.BidStatus;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "bids",
        indexes = {
                @Index(name = "idx_bid_load_id", columnList = "load_id"),
                @Index(name = "idx_bid_transporter_id", columnList = "transporter_id"),
                @Index(name = "idx_bid_status", columnList = "status")
        }
)
public class Bid {

    @Id
    @GeneratedValue
    @Column(name = "bid_id", nullable = false, updatable = false)
    private UUID bidId;

    // ------------------------------
    // Load (many bids on one load)
    // ------------------------------
    @Column(name = "load_id", nullable = false)
    private UUID loadId;

    // ------------------------------
    // Transporter (not a JPA relation)
    // ------------------------------
    @Column(name = "transporter_id", nullable = false)
    private UUID transporterId;

    @Column(name = "proposed_rate", nullable = false)
    private double proposedRate;

    @Column(name = "trucks_offered", nullable = false)
    private int trucksOffered;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BidStatus status;

    @Column(name = "submitted_at", nullable = false)
    private Instant submittedAt;

    public Bid() {}

    public Bid(UUID loadId, UUID transporterId, double proposedRate, int trucksOffered) {
        this.loadId = loadId;
        this.transporterId = transporterId;
        this.proposedRate = proposedRate;
        this.trucksOffered = trucksOffered;

        this.status = BidStatus.PENDING;
        this.submittedAt = Instant.now();
    }

    // --------------------------------
    // Getters and Setters
    // --------------------------------

    public UUID getBidId() {
        return bidId;
    }

    public UUID getLoadId() {
        return loadId;
    }

    public UUID getTransporterId() {
        return transporterId;
    }

    public double getProposedRate() {
        return proposedRate;
    }

    public int getTrucksOffered() {
        return trucksOffered;
    }

    public BidStatus getStatus() {
        return status;
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }

    public void setStatus(BidStatus status) {
        this.status = status;
    }
}
