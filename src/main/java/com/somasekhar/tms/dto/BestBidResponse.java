package com.somasekhar.tms.dto;

import java.util.UUID;

public record BestBidResponse(
        UUID bidId,
        UUID transporterId,
        double proposedRate,
        double rating,
        double score,
        int trucksOffered
) {}
