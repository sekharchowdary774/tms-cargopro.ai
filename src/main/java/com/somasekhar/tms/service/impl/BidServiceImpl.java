package com.somasekhar.tms.service.impl;

import com.somasekhar.tms.dto.BestBidResponse;
import com.somasekhar.tms.dto.BidListResponse;
import com.somasekhar.tms.dto.BidRequest;
import com.somasekhar.tms.dto.BidResponse;
import com.somasekhar.tms.entity.AvailableTruck;
import com.somasekhar.tms.entity.Bid;
import com.somasekhar.tms.entity.Load;
import com.somasekhar.tms.entity.Transporter;
import com.somasekhar.tms.enums.BidStatus;
import com.somasekhar.tms.enums.LoadStatus;
import com.somasekhar.tms.exception.InvalidStatusTransitionException;
import com.somasekhar.tms.exception.InsufficientCapacityException;
import com.somasekhar.tms.exception.ResourceNotFoundException;
import com.somasekhar.tms.repository.BidRepository;
import com.somasekhar.tms.repository.LoadRepository;
import com.somasekhar.tms.repository.TransporterRepository;
import com.somasekhar.tms.service.BidService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class BidServiceImpl implements BidService {

    private final BidRepository bidRepository;
    private final LoadRepository loadRepository;
    private final TransporterRepository transporterRepository;

    public BidServiceImpl(BidRepository bidRepository,
                          LoadRepository loadRepository,
                          TransporterRepository transporterRepository) {
        this.bidRepository = bidRepository;
        this.loadRepository = loadRepository;
        this.transporterRepository = transporterRepository;
    }

    // -----------------------------------------------------------
    // 1. POST /bid → Create a new bid
    // -----------------------------------------------------------
    @Override
    public BidResponse createBid(BidRequest request) {

        Load load = loadRepository.findById(request.loadId())
                .orElseThrow(() -> new ResourceNotFoundException("Load not found"));

        Transporter transporter = transporterRepository.findById(request.transporterId())
                .orElseThrow(() -> new ResourceNotFoundException("Transporter not found"));

        // Cannot bid on BOOKED or CANCELLED loads
        if (load.getStatus() == LoadStatus.BOOKED || load.getStatus() == LoadStatus.CANCELLED) {
            throw new InvalidStatusTransitionException("Cannot bid on BOOKED or CANCELLED load");
        }

        // Transporter must have the required truck type
        AvailableTruck truck = transporter.getAvailableTrucks().stream()
                .filter(t -> t.getTruckType().equalsIgnoreCase(load.getTruckType()))
                .findFirst()
                .orElseThrow(() ->
                        new InsufficientCapacityException("Transporter has no trucks of required type")
                );

        // Check capacity
        if (request.trucksOffered() > truck.getCount()) {
            throw new InsufficientCapacityException(
                    "Insufficient trucks. Available: " + truck.getCount()
            );
        }

        // First bid → move POSTED → OPEN_FOR_BIDS
        if (load.getStatus() == LoadStatus.POSTED) {
            load.setStatus(LoadStatus.OPEN_FOR_BIDS);
            loadRepository.save(load);
        }

        Bid bid = new Bid(
                request.loadId(),
                request.transporterId(),
                request.proposedRate(),
                request.trucksOffered()
        );

        Bid saved = bidRepository.save(bid);
        return toResponse(saved);
    }

    // -----------------------------------------------------------
    // 2. GET /bid/{bidId}
    // -----------------------------------------------------------
    @Override
    public BidResponse getBidById(UUID bidId) {
        Bid bid = bidRepository.findById(bidId)
                .orElseThrow(() -> new ResourceNotFoundException("Bid not found"));
        return toResponse(bid);
    }

    // -----------------------------------------------------------
    // 3. GET /bid?loadId=&transporterId=&status=
    // -----------------------------------------------------------
    @Override
    public BidListResponse getBids(UUID loadId, UUID transporterId, BidStatus status) {

        List<Bid> result;

        if (loadId != null && transporterId != null && status != null) {
            result = bidRepository.findAll().stream()
                    .filter(b -> b.getLoadId().equals(loadId)
                            && b.getTransporterId().equals(transporterId)
                            && b.getStatus() == status)
                    .toList();
        } else if (loadId != null && status != null) {
            result = bidRepository.findByLoadIdAndStatus(loadId, status);
        } else if (transporterId != null && status != null) {
            result = bidRepository.findAll().stream()
                    .filter(b -> b.getTransporterId().equals(transporterId)
                            && b.getStatus() == status)
                    .toList();
        } else if (loadId != null) {
            result = bidRepository.findByLoadId(loadId);
        } else if (transporterId != null) {
            result = bidRepository.findByTransporterId(transporterId);
        } else if (status != null) {
            result = bidRepository.findByStatus(status);
        } else {
            result = bidRepository.findAll();
        }

        return new BidListResponse(
                result.stream().map(this::toResponse).toList()
        );
    }

    // -----------------------------------------------------------
    // 4. PATCH /bid/{bidId}/reject
    // -----------------------------------------------------------
    @Override
    public BidResponse rejectBid(UUID bidId) {
        Bid bid = bidRepository.findById(bidId)
                .orElseThrow(() -> new ResourceNotFoundException("Bid not found"));

        if (bid.getStatus() == BidStatus.ACCEPTED) {
            throw new InvalidStatusTransitionException("Cannot reject an ACCEPTED bid");
        }

        bid.setStatus(BidStatus.REJECTED);
        return toResponse(bidRepository.save(bid));
    }

    // -----------------------------------------------------------
    // 5. GET /load/{loadId}/best-bids
    // -----------------------------------------------------------
    @Override
    public List<BestBidResponse> getBestBids(UUID loadId) {

        Load load = loadRepository.findById(loadId)
                .orElseThrow(() -> new ResourceNotFoundException("Load not found"));

        List<Bid> bids = bidRepository.findByLoadIdAndStatus(loadId, BidStatus.PENDING);

        return bids.stream().map(bid -> {

                    Transporter transporter = transporterRepository.findById(bid.getTransporterId())
                            .orElseThrow(() -> new ResourceNotFoundException("Transporter not found"));

                    double score =
                            (1.0 / bid.getProposedRate()) * 0.7 +
                                    (transporter.getRating() / 5.0) * 0.3;

                    return new BestBidResponse(
                            bid.getBidId(),
                            bid.getTransporterId(),
                            bid.getProposedRate(),
                            transporter.getRating(),
                            score,
                            bid.getTrucksOffered()
                    );

                }).sorted((a, b) -> Double.compare(b.score(), a.score())) // DESC by score
                .toList();
    }

    // -----------------------------------------------------------
    // Helper : Entity → DTO
    // -----------------------------------------------------------
    private BidResponse toResponse(Bid bid) {
        return new BidResponse(
                bid.getBidId(),
                bid.getLoadId(),
                bid.getTransporterId(),
                bid.getProposedRate(),
                bid.getTrucksOffered(),
                bid.getStatus(),
                bid.getSubmittedAt()
        );
    }
}
