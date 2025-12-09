package com.somasekhar.tms.service.impl;

import com.somasekhar.tms.dto.LoadListResponse;
import com.somasekhar.tms.dto.LoadRequest;
import com.somasekhar.tms.dto.LoadResponse;
import com.somasekhar.tms.entity.Load;
import com.somasekhar.tms.enums.LoadStatus;
import com.somasekhar.tms.exception.InvalidStatusTransitionException;
import com.somasekhar.tms.exception.ResourceNotFoundException;
import com.somasekhar.tms.repository.LoadRepository;
import com.somasekhar.tms.service.LoadService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class LoadServiceImpl implements LoadService {

    private final LoadRepository loadRepository;

    public LoadServiceImpl(LoadRepository loadRepository) {
        this.loadRepository = loadRepository;
    }

    // -----------------------------------------------------------
    // POST /load
    // -----------------------------------------------------------
    @Override
    public LoadResponse createLoad(LoadRequest request) {

        Load load = new Load(
                request.shipperId(),
                request.loadingCity(),
                request.unloadingCity(),
                request.loadingDate(),
                request.productType(),
                request.weight(),
                request.weightUnit(),
                request.truckType(),
                request.noOfTrucks()
        );

        Load saved = loadRepository.save(load);
        return toResponse(saved);
    }


    // -----------------------------------------------------------
    // GET /load?shipperId=&status=&page=&size=
    // -----------------------------------------------------------
    @Override
    public LoadListResponse getLoads(String shipperId, LoadStatus status, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Load> result;

        if (shipperId != null && status != null) {
            result = loadRepository.findByShipperIdAndStatus(shipperId, status, pageable);
        } else if (shipperId != null) {
            result = loadRepository.findByShipperId(shipperId, pageable);
        } else if (status != null) {
            result = loadRepository.findByStatus(status, pageable);
        } else {
            result = loadRepository.findAll(pageable);
        }

        List<LoadResponse> loads = result.getContent()
                .stream()
                .map(this::toResponse)
                .toList();

        return new LoadListResponse(
                loads,
                result.getTotalElements(),
                result.getTotalPages()
        );
    }


    // -----------------------------------------------------------
    // GET /load/{loadId}
    // -----------------------------------------------------------
    @Override
    public LoadResponse getLoadById(UUID loadId) {
        Load load = loadRepository.findById(loadId)
                .orElseThrow(() -> new ResourceNotFoundException("Load not found"));

        return toResponse(load);
    }


    // -----------------------------------------------------------
    // PATCH /load/{loadId}/cancel
    // -----------------------------------------------------------
    @Override
    public LoadResponse cancelLoad(UUID loadId) {

        Load load = loadRepository.findById(loadId)
                .orElseThrow(() -> new ResourceNotFoundException("Load not found"));

        // Cannot cancel a BOOKED load
        if (load.getStatus() == LoadStatus.BOOKED) {
            throw new InvalidStatusTransitionException("Cannot cancel a BOOKED load");
        }

        // If already cancelled → nothing to do
        if (load.getStatus() == LoadStatus.CANCELLED) {
            return toResponse(load);
        }

        // POSTED or OPEN_FOR_BIDS → allowed
        load.setStatus(LoadStatus.CANCELLED);

        Load saved = loadRepository.save(load);
        return toResponse(saved);
    }


    // -----------------------------------------------------------
    // Helper: Entity → DTO
    // -----------------------------------------------------------
    private LoadResponse toResponse(Load load) {
        return new LoadResponse(
                load.getLoadId(),
                load.getShipperId(),
                load.getLoadingCity(),
                load.getUnloadingCity(),
                load.getLoadingDate(),
                load.getProductType(),
                load.getWeight(),
                load.getWeightUnit(),
                load.getTruckType(),
                load.getNoOfTrucks(),
                load.getStatus(),
                load.getDatePosted()
        );
    }
}
