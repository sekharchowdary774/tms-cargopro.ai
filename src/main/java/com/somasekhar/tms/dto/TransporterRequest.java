package com.somasekhar.tms.dto;

import java.util.List;

public record TransporterRequest(
        String companyName,
        double rating,
        List<TruckRequest> availableTrucks
) {}
