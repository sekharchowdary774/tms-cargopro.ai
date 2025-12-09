package com.somasekhar.tms.dto;

import com.somasekhar.tms.enums.BidStatus;

import java.time.Instant;
import java.util.UUID;

public record BidResponse(
        UUID bidId,
        UUID loadId,
        UUID transporterId,
        double proposedRate,
        int trucksOffered,
        BidStatus status,
        Instant submittedAt
) {}
