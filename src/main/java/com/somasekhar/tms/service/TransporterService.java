package com.somasekhar.tms.service;

import com.somasekhar.tms.dto.TransporterRequest;
import com.somasekhar.tms.dto.TransporterResponse;

import java.util.UUID;

public interface TransporterService {

    TransporterResponse createTransporter(TransporterRequest request);

    TransporterResponse getTransporter(UUID transporterId);

    TransporterResponse updateTrucks(UUID transporterId, TransporterRequest request);
}
