package com.singtel.network.dto.service;

import com.singtel.network.entity.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for detailed service response.
 */
public class ServiceDetailResponse {

    private UUID id;
    private String name;
    private String description;
    private String serviceType;
    private Integer baseBandwidthMbps;
    private Integer maxBandwidthMbps;
    private Integer minBandwidthMbps;
    private BigDecimal basePriceMonthly;
    private BigDecimal pricePerMbps;
    private BigDecimal setupFee;
    private Integer contractTermMonths;
    private Boolean isBandwidthAdjustable;
    private Boolean isAvailable;
    private Integer provisioningTimeHours;
    private Map<String, Object> features;
    private Map<String, Object> technicalSpecs;
    private ServiceCategoryInfo category;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public ServiceDetailResponse() {
    }

    public ServiceDetailResponse(Service service) {
        this.id = service.getId();
        this.name = service.getName();
        this.description = service.getDescription();
        this.serviceType = service.getServiceType();
        this.baseBandwidthMbps = service.getBaseBandwidthMbps();
        this.maxBandwidthMbps = service.getMaxBandwidthMbps();
        this.minBandwidthMbps = service.getMinBandwidthMbps();
        this.basePriceMonthly = service.getBasePriceMonthly();
        this.pricePerMbps = service.getPricePerMbps();
        this.setupFee = service.getSetupFee();
        this.contractTermMonths = service.getContractTermMonths();
        this.isBandwidthAdjustable = service.getIsBandwidthAdjustable();
        this.isAvailable = service.getIsAvailable();
        this.provisioningTimeHours = service.getProvisioningTimeHours();
        this.features = service.getFeatures();
        this.technicalSpecs = service.getTechnicalSpecs();
        this.createdAt = service.getCreatedAt();
        this.updatedAt = service.getUpdatedAt();
        
        if (service.getCategory() != null) {
            this.category = new ServiceCategoryInfo(service.getCategory().getId(), 
                                                   service.getCategory().getName());
        }
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public ServiceCategoryInfo getCategory() {
        return category;
    }

    public void setCategory(ServiceCategoryInfo category) {
        this.category = category;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Helper methods
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

    /**
     * Nested class for service category information
     */
    public static class ServiceCategoryInfo {
        private UUID id;
        private String name;

        public ServiceCategoryInfo() {
        }

        public ServiceCategoryInfo(UUID id, String name) {
            this.id = id;
            this.name = name;
        }

        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "ServiceCategoryInfo{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "ServiceDetailResponse{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", serviceType='" + serviceType + '\'' +
                ", baseBandwidthMbps=" + baseBandwidthMbps +
                ", basePriceMonthly=" + basePriceMonthly +
                ", isAvailable=" + isAvailable +
                '}';
    }
}
