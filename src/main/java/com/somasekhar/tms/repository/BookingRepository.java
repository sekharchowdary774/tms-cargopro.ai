package com.somasekhar.tms.repository;

import com.somasekhar.tms.entity.Booking;
import com.somasekhar.tms.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {

    List<Booking> findByLoadId(UUID loadId);

    List<Booking> findByLoadIdAndStatus(UUID loadId, BookingStatus status);
}
