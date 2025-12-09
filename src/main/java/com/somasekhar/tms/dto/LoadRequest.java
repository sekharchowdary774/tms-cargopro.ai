package com.somasekhar.tms.dto;

import com.somasekhar.tms.enums.WeightUnit;

import java.time.Instant;

public record LoadRequest(
        String shipperId,
        String loadingCity,
        String unloadingCity,
        Instant loadingDate,
        String productType,
        double weight,
        WeightUnit weightUnit,
        String truckType,
        int noOfTrucks
) {}
