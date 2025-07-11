package com.singtel.network.dto.service;

import com.singtel.network.entity.Service;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO for service summary response (used in lists and catalogs).
 */
public class ServiceSummaryResponse {

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
    private String categoryName;

    // Constructors
    public ServiceSummaryResponse() {
    }

    public ServiceSummaryResponse(Service service) {
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
        
        if (service.getCategory() != null) {
            this.categoryName = service.getCategory().getName();
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

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
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

    public BigDecimal getTotalSetupCost(Integer bandwidthMbps) {
        BigDecimal monthlyCost = calculateMonthlyCost(bandwidthMbps);
        BigDecimal totalSetupCost = setupFee != null ? setupFee : BigDecimal.ZERO;
        return totalSetupCost.add(monthlyCost != null ? monthlyCost : BigDecimal.ZERO);
    }

    @Override
    public String toString() {
        return "ServiceSummaryResponse{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", serviceType='" + serviceType + '\'' +
                ", baseBandwidthMbps=" + baseBandwidthMbps +
                ", basePriceMonthly=" + basePriceMonthly +
                ", isAvailable=" + isAvailable +
                '}';
    }
}
