package com.somasekhar.tms.controller;

import com.somasekhar.tms.dto.BestBidResponse;
import com.somasekhar.tms.dto.LoadListResponse;
import com.somasekhar.tms.dto.LoadRequest;
import com.somasekhar.tms.dto.LoadResponse;
import com.somasekhar.tms.enums.LoadStatus;
import com.somasekhar.tms.service.BidService;
import com.somasekhar.tms.service.LoadService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/load")
public class LoadController {

    private final LoadService loadService;
    private final BidService bidService;

    public LoadController(LoadService loadService, BidService bidService) {
        this.loadService = loadService;
        this.bidService = bidService;
    }

    // -----------------------------------------------------------
    // POST /load → Create new load
    // -----------------------------------------------------------
    @PostMapping(consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public LoadResponse createLoad(@Valid @RequestBody LoadRequest request) {
        return loadService.createLoad(request);
    }

    // -----------------------------------------------------------
    // GET /load?shipperId=&status=&page=&size=
    // -----------------------------------------------------------
    @GetMapping(produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public LoadListResponse getLoads(
            @RequestParam(required = false) String shipperId,
            @RequestParam(required = false) LoadStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return loadService.getLoads(shipperId, status, page, size);
    }

    // -----------------------------------------------------------
    // GET /load/{loadId} → Get single load
    // -----------------------------------------------------------
    @GetMapping(value = "/{loadId}", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public LoadResponse getLoadById(@PathVariable UUID loadId) {
        return loadService.getLoadById(loadId);
    }

    // -----------------------------------------------------------
    // PATCH /load/{loadId}/cancel → Cancel load
    // -----------------------------------------------------------
    @PatchMapping(value = "/{loadId}/cancel", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public LoadResponse cancelLoad(@PathVariable UUID loadId) {
        return loadService.cancelLoad(loadId);
    }

    // -----------------------------------------------------------
    // GET /load/{loadId}/best-bids → Best bid recommendations
    // -----------------------------------------------------------
    @GetMapping(value = "/{loadId}/best-bids", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public List<BestBidResponse> getBestBids(@PathVariable UUID loadId) {
        return bidService.getBestBids(loadId);
    }
}
