package com.somasekhar.tms.repository;

import com.somasekhar.tms.entity.Bid;
import com.somasekhar.tms.enums.BidStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BidRepository extends JpaRepository<Bid, UUID> {

    List<Bid> findByLoadId(UUID loadId);

    List<Bid> findByTransporterId(UUID transporterId);

    List<Bid> findByLoadIdAndStatus(UUID loadId, BidStatus status);

    List<Bid> findByStatus(BidStatus status);
}
