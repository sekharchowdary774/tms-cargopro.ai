package com.somasekhar.tms.dto;

import java.util.UUID;

public record BookingRequest(
        UUID bidId,
        Integer allocatedTrucks // if null → use full bid.trucksOffered
) {}
