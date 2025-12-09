package com.somasekhar.tms.entity;

import com.somasekhar.tms.enums.LoadStatus;
import com.somasekhar.tms.enums.WeightUnit;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "loads",
        indexes = {
                @Index(name = "idx_load_shipper_id", columnList = "shipper_id"),
                @Index(name = "idx_load_status", columnList = "status")
        }
)
public class Load {

    @Id
    @GeneratedValue
    @Column(name = "load_id", updatable = false, nullable = false)
    private UUID loadId;

    @Column(name = "shipper_id", nullable = false)
    private String shipperId;

    @Column(name = "loading_city", nullable = false)
    private String loadingCity;

    @Column(name = "unloading_city", nullable = false)
    private String unloadingCity;

    @Column(name = "loading_date", nullable = false)
    private Instant loadingDate;

    @Column(name = "product_type", nullable = false)
    private String productType;

    @Column(name = "weight", nullable = false)
    private double weight;

    @Enumerated(EnumType.STRING)
    @Column(name = "weight_unit", nullable = false, length = 10)
    private WeightUnit weightUnit;

    @Column(name = "truck_type", nullable = false)
    private String truckType;

    @Column(name = "no_of_trucks", nullable = false)
    private int noOfTrucks;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private LoadStatus status;

    @Column(name = "date_posted", nullable = false, updatable = false)
    private Instant datePosted;

    /**
     * OPTIMISTIC LOCKING
     * Must be Long (nullable), NOT primitive long.
     * Hibernate initializes NULL → 0 automatically on insert.
     */
    @Version
    @Column(name = "version")
    private Long version;

    // ---------- Constructors ----------

    protected Load() {
        // JPA requires no-args constructor
    }

    public Load(
            String shipperId,
            String loadingCity,
            String unloadingCity,
            Instant loadingDate,
            String productType,
            double weight,
            WeightUnit weightUnit,
            String truckType,
            int noOfTrucks
    ) {
        this.shipperId = shipperId;
        this.loadingCity = loadingCity;
        this.unloadingCity = unloadingCity;
        this.loadingDate = loadingDate;
        this.productType = productType;
        this.weight = weight;
        this.weightUnit = weightUnit;
        this.truckType = truckType;
        this.noOfTrucks = noOfTrucks;

        // Business defaults
        this.status = LoadStatus.POSTED;
        this.datePosted = Instant.now();
    }

    // ---------- Getters & Setters ----------

    public UUID getLoadId() {
        return loadId;
    }

    public String getShipperId() {
        return shipperId;
    }

    public void setShipperId(String shipperId) {
        this.shipperId = shipperId;
    }

    public String getLoadingCity() {
        return loadingCity;
    }

    public void setLoadingCity(String loadingCity) {
        this.loadingCity = loadingCity;
    }

    public String getUnloadingCity() {
        return unloadingCity;
    }

    public void setUnloadingCity(String unloadingCity) {
        this.unloadingCity = unloadingCity;
    }

    public Instant getLoadingDate() {
        return loadingDate;
    }

    public void setLoadingDate(Instant loadingDate) {
        this.loadingDate = loadingDate;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public WeightUnit getWeightUnit() {
        return weightUnit;
    }

    public void setWeightUnit(WeightUnit weightUnit) {
        this.weightUnit = weightUnit;
    }

    public String getTruckType() {
        return truckType;
    }

    public void setTruckType(String truckType) {
        this.truckType = truckType;
    }

    public int getNoOfTrucks() {
        return noOfTrucks;
    }

    public void setNoOfTrucks(int noOfTrucks) {
        this.noOfTrucks = noOfTrucks;
    }

    public LoadStatus getStatus() {
        return status;
    }

    public void setStatus(LoadStatus status) {
        this.status = status;
    }

    public Instant getDatePosted() {
        return datePosted;
    }

    public Long getVersion() {
        return version;
    }

    // ---------- Domain Helpers ----------

    public boolean isPosted() {
        return this.status == LoadStatus.POSTED;
    }

    public boolean isOpenForBids() {
        return this.status == LoadStatus.OPEN_FOR_BIDS;
    }

    public boolean isBooked() {
        return this.status == LoadStatus.BOOKED;
    }

    public boolean isCancelled() {
        return this.status == LoadStatus.CANCELLED;
    }
}
