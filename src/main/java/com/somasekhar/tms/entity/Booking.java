package com.somasekhar.tms.entity;

import com.somasekhar.tms.enums.BookingStatus;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "bookings",
        indexes = {
                @Index(name = "idx_booking_load_id", columnList = "load_id"),
                @Index(name = "idx_booking_transporter_id", columnList = "transporter_id"),
                @Index(name = "idx_booking_bid_id", columnList = "bid_id")
        }
)
public class Booking {

    @Id
    @GeneratedValue
    @Column(name = "booking_id", nullable = false, updatable = false)
    private UUID bookingId;

    @Column(name = "load_id", nullable = false)
    private UUID loadId;

    @Column(name = "bid_id", nullable = false)
    private UUID bidId;

    @Column(name = "transporter_id", nullable = false)
    private UUID transporterId;

    @Column(name = "allocated_trucks", nullable = false)
    private int allocatedTrucks;

    @Column(name = "final_rate", nullable = false)
    private double finalRate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BookingStatus status;

    @Column(name = "booked_at", nullable = false)
    private Instant bookedAt;

    public Booking() {}

    public Booking(UUID loadId,
                   UUID bidId,
                   UUID transporterId,
                   int allocatedTrucks,
                   double finalRate) {
        this.loadId = loadId;
        this.bidId = bidId;
        this.transporterId = transporterId;
        this.allocatedTrucks = allocatedTrucks;
        this.finalRate = finalRate;
        this.status = BookingStatus.CONFIRMED;
        this.bookedAt = Instant.now();
    }

    public UUID getBookingId() {
        return bookingId;
    }

    public UUID getLoadId() {
        return loadId;
    }

    public UUID getBidId() {
        return bidId;
    }

    public UUID getTransporterId() {
        return transporterId;
    }

    public int getAllocatedTrucks() {
        return allocatedTrucks;
    }

    public double getFinalRate() {
        return finalRate;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public Instant getBookedAt() {
        return bookedAt;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }
}
