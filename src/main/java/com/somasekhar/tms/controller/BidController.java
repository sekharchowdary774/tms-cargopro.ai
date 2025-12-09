package com.somasekhar.tms.controller;

import com.somasekhar.tms.dto.BidListResponse;
import com.somasekhar.tms.dto.BidRequest;
import com.somasekhar.tms.dto.BidResponse;
import com.somasekhar.tms.enums.BidStatus;
import com.somasekhar.tms.service.BidService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/bid")
public class BidController {

    private final BidService bidService;

    public BidController(BidService bidService) {
        this.bidService = bidService;
    }

    // -----------------------------------------------------------
    // POST /bid → Create a new bid
    // -----------------------------------------------------------
    @PostMapping(consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public BidResponse createBid(@Valid @RequestBody BidRequest request) {
        return bidService.createBid(request);
    }

    // -----------------------------------------------------------
    // GET /bid/{bidId} → Fetch a single bid
    // -----------------------------------------------------------
    @GetMapping(value = "/{bidId}", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public BidResponse getBidById(@PathVariable UUID bidId) {
        return bidService.getBidById(bidId);
    }

    // -----------------------------------------------------------
    // GET /bid?loadId=&transporterId=&status=
    // -----------------------------------------------------------
    @GetMapping(produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public BidListResponse getBids(
            @RequestParam(required = false) UUID loadId,
            @RequestParam(required = false) UUID transporterId,
            @RequestParam(required = false) BidStatus status
    ) {
        return bidService.getBids(loadId, transporterId, status);
    }

    // -----------------------------------------------------------
    // PATCH /bid/{bidId}/reject → Reject a bid
    // -----------------------------------------------------------
    @PatchMapping(value = "/{bidId}/reject", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public BidResponse rejectBid(@PathVariable UUID bidId) {
        return bidService.rejectBid(bidId);
    }
}
