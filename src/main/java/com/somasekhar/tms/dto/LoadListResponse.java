package com.somasekhar.tms.dto;

import java.util.List;

public record LoadListResponse(
        List<LoadResponse> loads,
        long totalElements,
        int totalPages
) {}
