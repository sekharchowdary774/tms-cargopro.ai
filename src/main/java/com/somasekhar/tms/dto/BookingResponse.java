package com.somasekhar.tms.dto;

import com.somasekhar.tms.enums.BookingStatus;

import java.time.Instant;
import java.util.UUID;

public record BookingResponse(
        UUID bookingId,
        UUID loadId,
        UUID bidId,
        UUID transporterId,
        int allocatedTrucks,
        double finalRate,
        BookingStatus status,
        Instant bookedAt
) {}
