package com.somasekhar.tms.dto;

import java.util.List;

public record BidListResponse(
        List<BidResponse> bids
) {}
