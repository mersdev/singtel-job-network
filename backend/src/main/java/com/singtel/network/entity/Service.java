package com.singtel.network.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Service entity representing available network services in the catalog.
 */
@Entity
@Table(name = "services", schema = "singtel_app")
public class Service extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private ServiceCategory category;

    @NotBlank(message = "Service name is required")
    @Size(max = 255, message = "Service name must not exceed 255 characters")
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @NotBlank(message = "Service type is required")
    @Size(max = 100, message = "Service type must not exceed 100 characters")
    @Column(name = "service_type", nullable = false)
    private String serviceType;

    @Min(value = 1, message = "Base bandwidth must be at least 1 Mbps")
    @Column(name = "base_bandwidth_mbps")
    private Integer baseBandwidthMbps;

    @Min(value = 1, message = "Max bandwidth must be at least 1 Mbps")
    @Column(name = "max_bandwidth_mbps")
    private Integer maxBandwidthMbps;

    @Min(value = 1, message = "Min bandwidth must be at least 1 Mbps")
    @Column(name = "min_bandwidth_mbps")
    private Integer minBandwidthMbps;

    @DecimalMin(value = "0.0", message = "Base price must be non-negative")
    @Column(name = "base_price_monthly", precision = 10, scale = 2)
    private BigDecimal basePriceMonthly;

    @DecimalMin(value = "0.0", message = "Price per Mbps must be non-negative")
    @Column(name = "price_per_mbps", precision = 10, scale = 4)
    private BigDecimal pricePerMbps;

    @DecimalMin(value = "0.0", message = "Setup fee must be non-negative")
    @Column(name = "setup_fee", precision = 10, scale = 2)
    private BigDecimal setupFee = BigDecimal.ZERO;

    @Min(value = 1, message = "Contract term must be at least 1 month")
    @Column(name = "contract_term_months")
    private Integer contractTermMonths = 12;

    @Column(name = "is_bandwidth_adjustable")
    private Boolean isBandwidthAdjustable = false;

    @Column(name = "is_available")
    private Boolean isAvailable = true;

    @Min(value = 1, message = "Provisioning time must be at least 1 hour")
    @Column(name = "provisioning_time_hours")
    private Integer provisioningTimeHours = 24;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "features", columnDefinition = "jsonb")
    private Map<String, Object> features;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "technical_specs", columnDefinition = "jsonb")
    private Map<String, Object> technicalSpecs;

    // Relationships
    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ServiceInstance> serviceInstances = new ArrayList<>();

    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Order> orders = new ArrayList<>();

    // Constructors
    public Service() {
    }

    public Service(String name, String serviceType, Integer baseBandwidthMbps, BigDecimal basePriceMonthly) {
        this.name = name;
        this.serviceType = serviceType;
        this.baseBandwidthMbps = baseBandwidthMbps;
        this.basePriceMonthly = basePriceMonthly;
    }

    // Getters and Setters
    public ServiceCategory getCategory() {
        return category;
    }

    public void setCategory(ServiceCategory category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public Integer getBaseBandwidthMbps() {
        return baseBandwidthMbps;
    }

    public void setBaseBandwidthMbps(Integer baseBandwidthMbps) {
        this.baseBandwidthMbps = baseBandwidthMbps;
    }

    public Integer getMaxBandwidthMbps() {
        return maxBandwidthMbps;
    }

    public void setMaxBandwidthMbps(Integer maxBandwidthMbps) {
        this.maxBandwidthMbps = maxBandwidthMbps;
    }

    public Integer getMinBandwidthMbps() {
        return minBandwidthMbps;
    }

    public void setMinBandwidthMbps(Integer minBandwidthMbps) {
        this.minBandwidthMbps = minBandwidthMbps;
    }

    public BigDecimal getBasePriceMonthly() {
        return basePriceMonthly;
    }

    public void setBasePriceMonthly(BigDecimal basePriceMonthly) {
        this.basePriceMonthly = basePriceMonthly;
    }

    public BigDecimal getPricePerMbps() {
        return pricePerMbps;
    }

    public void setPricePerMbps(BigDecimal pricePerMbps) {
        this.pricePerMbps = pricePerMbps;
    }

    public BigDecimal getSetupFee() {
        return setupFee;
    }

    public void setSetupFee(BigDecimal setupFee) {
        this.setupFee = setupFee;
    }

    public Integer getContractTermMonths() {
        return contractTermMonths;
    }

    public void setContractTermMonths(Integer contractTermMonths) {
        this.contractTermMonths = contractTermMonths;
    }

    public Boolean getIsBandwidthAdjustable() {
        return isBandwidthAdjustable;
    }

    public void setIsBandwidthAdjustable(Boolean isBandwidthAdjustable) {
        this.isBandwidthAdjustable = isBandwidthAdjustable;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public Integer getProvisioningTimeHours() {
        return provisioningTimeHours;
    }

    public void setProvisioningTimeHours(Integer provisioningTimeHours) {
        this.provisioningTimeHours = provisioningTimeHours;
    }

    public Map<String, Object> getFeatures() {
        return features;
    }

    public void setFeatures(Map<String, Object> features) {
        this.features = features;
    }

    public Map<String, Object> getTechnicalSpecs() {
        return technicalSpecs;
    }

    public void setTechnicalSpecs(Map<String, Object> technicalSpecs) {
        this.technicalSpecs = technicalSpecs;
    }

    public List<ServiceInstance> getServiceInstances() {
        return serviceInstances;
    }

    public void setServiceInstances(List<ServiceInstance> serviceInstances) {
        this.serviceInstances = serviceInstances;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    // Helper methods
    public boolean isAvailable() {
        return Boolean.TRUE.equals(isAvailable);
    }

    public boolean isBandwidthAdjustable() {
        return Boolean.TRUE.equals(isBandwidthAdjustable);
    }

    public BigDecimal calculateMonthlyCost(Integer bandwidthMbps) {
        if (bandwidthMbps == null || basePriceMonthly == null) {
            return basePriceMonthly;
        }

        BigDecimal additionalCost = BigDecimal.ZERO;
        if (pricePerMbps != null && bandwidthMbps > baseBandwidthMbps) {
            int additionalBandwidth = bandwidthMbps - baseBandwidthMbps;
            additionalCost = pricePerMbps.multiply(BigDecimal.valueOf(additionalBandwidth));
        }

        return basePriceMonthly.add(additionalCost);
    }

    public boolean isValidBandwidth(Integer bandwidthMbps) {
        if (bandwidthMbps == null) {
            return false;
        }

        if (minBandwidthMbps != null && bandwidthMbps < minBandwidthMbps) {
            return false;
        }

        if (maxBandwidthMbps != null && bandwidthMbps > maxBandwidthMbps) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "Service{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", serviceType='" + serviceType + '\'' +
                ", baseBandwidthMbps=" + baseBandwidthMbps +
                ", basePriceMonthly=" + basePriceMonthly +
                ", isAvailable=" + isAvailable +
                '}';
    }
}
