package com.somasekhar.tms.dto;

import com.somasekhar.tms.enums.LoadStatus;
import com.somasekhar.tms.enums.WeightUnit;

import java.time.Instant;
import java.util.UUID;

public record LoadResponse(
        UUID loadId,
        String shipperId,
        String loadingCity,
        String unloadingCity,
        Instant loadingDate,
        String productType,
        double weight,
        WeightUnit weightUnit,
        String truckType,
        int noOfTrucks,
        LoadStatus status,
        Instant datePosted
) {}
