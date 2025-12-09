package com.somasekhar.tms.service;

import com.somasekhar.tms.dto.LoadListResponse;
import com.somasekhar.tms.dto.LoadRequest;
import com.somasekhar.tms.dto.LoadResponse;
import com.somasekhar.tms.enums.LoadStatus;

import java.util.UUID;

public interface LoadService {

    // POST /load
    LoadResponse createLoad(LoadRequest request);

    // GET /load?shipperId=&status=&page=&size=
    LoadListResponse getLoads(String shipperId, LoadStatus status, int page, int size);

    // GET /load/{loadId}
    LoadResponse getLoadById(UUID loadId);

    // PATCH /load/{loadId}/cancel
    LoadResponse cancelLoad(UUID loadId);
}
