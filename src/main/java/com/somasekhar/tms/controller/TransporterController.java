package com.somasekhar.tms.controller;

import com.somasekhar.tms.dto.TransporterRequest;
import com.somasekhar.tms.dto.TransporterResponse;
import com.somasekhar.tms.service.TransporterService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/transporter")
public class TransporterController {

    private final TransporterService service;

    public TransporterController(TransporterService service) {
        this.service = service;
    }

    // -----------------------------------------------------------
    // POST /transporter → Register transporter
    // -----------------------------------------------------------
    @PostMapping(consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public TransporterResponse create(@Valid @RequestBody TransporterRequest request) {
        return service.createTransporter(request);
    }

    // -----------------------------------------------------------
    // GET /transporter/{id} → Fetch transporter
    // -----------------------------------------------------------
    @GetMapping(value = "/{id}", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public TransporterResponse get(@PathVariable UUID id) {
        return service.getTransporter(id);
    }

    // -----------------------------------------------------------
    // PUT /transporter/{id}/trucks → Replace truck list
    // -----------------------------------------------------------
    @PutMapping(
            value = "/{id}/trucks",
            consumes = "application/json",
            produces = "application/json"
    )
    @ResponseStatus(HttpStatus.OK)
    public TransporterResponse updateTrucks(
            @PathVariable UUID id,
            @Valid @RequestBody TransporterRequest request
    ) {
        return service.updateTrucks(id, request);
    }
}
