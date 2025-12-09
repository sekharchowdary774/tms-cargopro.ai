package com.somasekhar.tms.service;

import com.somasekhar.tms.dto.BestBidResponse;
import com.somasekhar.tms.dto.BidListResponse;
import com.somasekhar.tms.dto.BidRequest;
import com.somasekhar.tms.dto.BidResponse;
import com.somasekhar.tms.enums.BidStatus;

import java.util.List;
import java.util.UUID;

public interface BidService {

    // POST /bid
    BidResponse createBid(BidRequest request);

    // GET /bid/{bidId}
    BidResponse getBidById(UUID bidId);

    // GET /bid?loadId=&transporterId=&status=
    BidListResponse getBids(UUID loadId, UUID transporterId, BidStatus status);

    // PATCH /bid/{bidId}/reject
    BidResponse rejectBid(UUID bidId);

    // GET /load/{loadId}/best-bids
    List<BestBidResponse> getBestBids(UUID loadId);
}
