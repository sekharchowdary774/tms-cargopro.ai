package com.somasekhar.tms.service.impl;

import com.somasekhar.tms.dto.BookingRequest;
import com.somasekhar.tms.dto.BookingResponse;
import com.somasekhar.tms.entity.*;
import com.somasekhar.tms.enums.BidStatus;
import com.somasekhar.tms.enums.BookingStatus;
import com.somasekhar.tms.enums.LoadStatus;
import com.somasekhar.tms.exception.*;
import com.somasekhar.tms.repository.*;
import com.somasekhar.tms.service.BookingService;
import jakarta.persistence.OptimisticLockException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final BidRepository bidRepository;
    private final LoadRepository loadRepository;
    private final TransporterRepository transporterRepository;

    public BookingServiceImpl(BookingRepository bookingRepository,
                              BidRepository bidRepository,
                              LoadRepository loadRepository,
                              TransporterRepository transporterRepository) {
        this.bookingRepository = bookingRepository;
        this.bidRepository = bidRepository;
        this.loadRepository = loadRepository;
        this.transporterRepository = transporterRepository;
    }

    // -----------------------------------------------------------
    // POST /booking → Accept bid & create booking
    // -----------------------------------------------------------
    @Override
    @Transactional
    public BookingResponse createBooking(BookingRequest request) {
        try {
            // 1️⃣ Fetch Bid
            Bid bid = bidRepository.findById(request.bidId())
                    .orElseThrow(() -> new ResourceNotFoundException("Bid not found"));

            if (bid.getStatus() != BidStatus.PENDING) {
                throw new InvalidStatusTransitionException("Only PENDING bids can be booked");
            }

            // 2️⃣ Load & Transporter
            Load load = loadRepository.findById(bid.getLoadId())
                    .orElseThrow(() -> new ResourceNotFoundException("Load not found"));

            Transporter transporter = transporterRepository.findById(bid.getTransporterId())
                    .orElseThrow(() -> new ResourceNotFoundException("Transporter not found"));

            if (load.getStatus() == LoadStatus.CANCELLED) {
                throw new InvalidStatusTransitionException("Cannot book a CANCELLED load");
            }

            // 3️⃣ Trucks to Allocate
            int requestedTrucks =
                    request.allocatedTrucks() != null ? request.allocatedTrucks() : bid.getTrucksOffered();

            if (requestedTrucks <= 0 || requestedTrucks > bid.getTrucksOffered()) {
                throw new InvalidStatusTransitionException("Invalid allocatedTrucks value");
            }

            // 4️⃣ Remaining trucks after previous confirmed bookings
            int previouslyAllocated = bookingRepository.findByLoadIdAndStatus(load.getLoadId(), BookingStatus.CONFIRMED)
                    .stream()
                    .mapToInt(Booking::getAllocatedTrucks)
                    .sum();

            int remainingTrucks = load.getNoOfTrucks() - previouslyAllocated;

            if (remainingTrucks <= 0) {
                throw new LoadAlreadyBookedException("Load already fully booked");
            }

            if (requestedTrucks > remainingTrucks) {
                throw new InsufficientCapacityException("Not enough remaining trucks for this load");
            }

            // 5️⃣ Transporter capacity validation
            AvailableTruck truck = transporter.getAvailableTrucks().stream()
                    .filter(t -> t.getTruckType().equalsIgnoreCase(load.getTruckType()))
                    .findFirst()
                    .orElseThrow(() ->
                            new InsufficientCapacityException("Transporter has no trucks of required type")
                    );

            if (truck.getCount() < requestedTrucks) {
                throw new InsufficientCapacityException(
                        "Transporter has only " + truck.getCount() + " trucks available"
                );
            }

            // Deduct trucks
            truck.setCount(truck.getCount() - requestedTrucks);

            // 6️⃣ Accept bid
            bid.setStatus(BidStatus.ACCEPTED);

            // 7️⃣ Create booking
            Booking booking = new Booking(
                    load.getLoadId(),
                    bid.getBidId(),
                    transporter.getTransporterId(),
                    requestedTrucks,
                    bid.getProposedRate()
            );

            // 8️⃣ Update load status
            int remainingAfter = remainingTrucks - requestedTrucks;
            load.setStatus(remainingAfter == 0 ? LoadStatus.BOOKED : LoadStatus.OPEN_FOR_BIDS);

            // Persist changes
            transporterRepository.save(transporter);
            bidRepository.save(bid);
            loadRepository.save(load);
            Booking savedBooking = bookingRepository.save(booking);

            return toResponse(savedBooking);

        } catch (OptimisticLockException e) {
            throw new ConflictException("Load already booked by another transaction. Retry.");
        }
    }

    // -----------------------------------------------------------
    // GET /booking/{bookingId}
    // -----------------------------------------------------------
    @Override
    public BookingResponse getBookingById(UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        return toResponse(booking);
    }

    // -----------------------------------------------------------
    // PATCH /booking/{bookingId}/cancel
    // -----------------------------------------------------------
    @Override
    @Transactional
    public BookingResponse cancelBooking(UUID bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            return toResponse(booking); // idempotent
        }

        Load load = loadRepository.findById(booking.getLoadId())
                .orElseThrow(() -> new ResourceNotFoundException("Load not found"));

        Transporter transporter = transporterRepository.findById(booking.getTransporterId())
                .orElseThrow(() -> new ResourceNotFoundException("Transporter not found"));

        // Restore capacity
        AvailableTruck truck = transporter.getAvailableTrucks().stream()
                .filter(t -> t.getTruckType().equalsIgnoreCase(load.getTruckType()))
                .findFirst()
                .orElseThrow(() ->
                        new ResourceNotFoundException("Transporter missing truck type: " + load.getTruckType())
                );

        truck.setCount(truck.getCount() + booking.getAllocatedTrucks());

        // Cancel the booking
        booking.setStatus(BookingStatus.CANCELLED);

        // Remaining confirmed bookings
        int stillAllocated = bookingRepository.findByLoadIdAndStatus(load.getLoadId(), BookingStatus.CONFIRMED)
                .stream()
                .mapToInt(Booking::getAllocatedTrucks)
                .sum();

        if (stillAllocated == 0) {
            load.setStatus(LoadStatus.CANCELLED);
        } else {
            load.setStatus(LoadStatus.OPEN_FOR_BIDS);
        }

        transporterRepository.save(transporter);
        loadRepository.save(load);
        Booking saved = bookingRepository.save(booking);

        return toResponse(saved);
    }

    // -----------------------------------------------------------
    // Convert entity → response
    // -----------------------------------------------------------
    private BookingResponse toResponse(Booking booking) {
        return new BookingResponse(
                booking.getBookingId(),
                booking.getLoadId(),
                booking.getBidId(),
                booking.getTransporterId(),
                booking.getAllocatedTrucks(),
                booking.getFinalRate(),
                booking.getStatus(),
                booking.getBookedAt()
        );
    }
}
