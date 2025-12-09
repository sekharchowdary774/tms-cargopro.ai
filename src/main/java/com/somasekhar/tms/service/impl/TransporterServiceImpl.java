package com.somasekhar.tms.service.impl;

import com.somasekhar.tms.entity.AvailableTruck;
import com.somasekhar.tms.entity.Transporter;
import com.somasekhar.tms.exception.ResourceNotFoundException;
import com.somasekhar.tms.repository.TransporterRepository;
import com.somasekhar.tms.service.TransporterService;
import com.somasekhar.tms.dto.TransporterRequest;
import com.somasekhar.tms.dto.TransporterResponse;
import com.somasekhar.tms.dto.TruckRequest;
import com.somasekhar.tms.dto.TruckResponse;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TransporterServiceImpl implements TransporterService {

    private final TransporterRepository repo;

    public TransporterServiceImpl(TransporterRepository repo) {
        this.repo = repo;
    }

    // -----------------------------------------------------------
    // POST /transporter → Create transporter with trucks
    // -----------------------------------------------------------
    @Override
    public TransporterResponse createTransporter(TransporterRequest request) {

        Transporter transporter = new Transporter(
                request.companyName(),
                request.rating()
        );

        // Add trucks
        for (TruckRequest truckReq : request.availableTrucks()) {
            AvailableTruck truck = new AvailableTruck(
                    truckReq.truckType(),
                    truckReq.count(),
                    transporter
            );
            transporter.addAvailableTruck(truck);
        }

        Transporter saved = repo.save(transporter);
        return toResponse(saved);
    }

    // -----------------------------------------------------------
    // GET /transporter/{id} → Fetch transporter
    // -----------------------------------------------------------
    @Override
    public TransporterResponse getTransporter(UUID id) {
        Transporter transporter = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transporter not found"));
        return toResponse(transporter);
    }

    // -----------------------------------------------------------
    // PUT /transporter/{id}/trucks → Replace truck list
    // -----------------------------------------------------------
    @Override
    public TransporterResponse updateTrucks(UUID id, TransporterRequest request) {

        Transporter transporter = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transporter not found"));

        // Clear existing trucks
        transporter.getAvailableTrucks().clear();

        // Add new trucks
        for (TruckRequest truckReq : request.availableTrucks()) {
            AvailableTruck truck = new AvailableTruck(
                    truckReq.truckType(),
                    truckReq.count(),
                    transporter
            );
            transporter.addAvailableTruck(truck);
        }

        Transporter saved = repo.save(transporter);
        return toResponse(saved);
    }

    // -----------------------------------------------------------
    // Helper: Map transporter → response
    // -----------------------------------------------------------
    private TransporterResponse toResponse(Transporter transporter) {
        return new TransporterResponse(
                transporter.getTransporterId(),
                transporter.getCompanyName(),
                transporter.getRating(),
                transporter.getAvailableTrucks().stream()
                        .map(truck -> new TruckResponse(truck.getTruckType(), truck.getCount()))
                        .toList()
        );
    }
}
