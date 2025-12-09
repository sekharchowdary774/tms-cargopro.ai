package com.somasekhar.tms.dto;

import java.util.List;
import java.util.UUID;

public record TransporterResponse(
        UUID transporterId,
        String companyName,
        double rating,
        List<TruckResponse> availableTrucks
) {}
