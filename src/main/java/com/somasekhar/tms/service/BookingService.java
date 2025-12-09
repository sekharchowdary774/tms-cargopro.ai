package com.somasekhar.tms.service;

import com.somasekhar.tms.dto.BookingRequest;
import com.somasekhar.tms.dto.BookingResponse;

import java.util.UUID;

public interface BookingService {

    // POST /booking
    BookingResponse createBooking(BookingRequest request);

    // GET /booking/{bookingId}
    BookingResponse getBookingById(UUID bookingId);

    // PATCH /booking/{bookingId}/cancel
    BookingResponse cancelBooking(UUID bookingId);
}
