package com.somasekhar.tms.controller;

import com.somasekhar.tms.dto.BookingRequest;
import com.somasekhar.tms.dto.BookingResponse;
import com.somasekhar.tms.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/booking")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    // -----------------------------------------------------------
    // POST /booking → Accept bid & create booking
    // -----------------------------------------------------------
    @PostMapping(consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse createBooking(@Valid @RequestBody BookingRequest request) {
        return bookingService.createBooking(request);
    }

    // -----------------------------------------------------------
    // GET /booking/{bookingId} → Get booking details
    // -----------------------------------------------------------
    @GetMapping(value = "/{bookingId}", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public BookingResponse getBooking(@PathVariable UUID bookingId) {
        return bookingService.getBookingById(bookingId);
    }

    // -----------------------------------------------------------
    // PATCH /booking/{bookingId}/cancel → Cancel booking
    // -----------------------------------------------------------
    @PatchMapping(value = "/{bookingId}/cancel", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public BookingResponse cancelBooking(@PathVariable UUID bookingId) {
        return bookingService.cancelBooking(bookingId);
    }
}
