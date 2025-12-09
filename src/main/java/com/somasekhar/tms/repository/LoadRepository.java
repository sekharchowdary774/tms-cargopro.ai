package com.somasekhar.tms.repository;

import com.somasekhar.tms.entity.Load;
import com.somasekhar.tms.enums.LoadStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LoadRepository extends JpaRepository<Load, UUID> {

    Page<Load> findByShipperIdAndStatus(String shipperId, LoadStatus status, Pageable pageable);

    Page<Load> findByShipperId(String shipperId, Pageable pageable);

    Page<Load> findByStatus(LoadStatus status, Pageable pageable);
}
