package com.somasekhar.tms.dto;

import java.util.UUID;

public record BidRequest(
        UUID loadId,
        UUID transporterId,
        double proposedRate,
        int trucksOffered
) {}
