package com.singtel.network.dto.service;

import com.singtel.network.entity.ServiceCategory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * DTO for service category response.
 */
public class ServiceCategoryResponse {

    private UUID id;
    private String name;
    private String description;
    private Integer displayOrder;
    private Boolean isActive;
    private Integer serviceCount;
    private List<ServiceSummaryResponse> services;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public ServiceCategoryResponse() {
    }

    public ServiceCategoryResponse(ServiceCategory category) {
        this.id = category.getId();
        this.name = category.getName();
        this.description = category.getDescription();
        this.displayOrder = category.getDisplayOrder();
        this.isActive = category.getIsActive();
        this.createdAt = category.getCreatedAt();
        this.updatedAt = category.getUpdatedAt();
        
        if (category.getServices() != null) {
            this.serviceCount = category.getServices().size();
            this.services = category.getServices().stream()
                    .filter(service -> service.isAvailable())
                    .map(ServiceSummaryResponse::new)
                    .collect(Collectors.toList());
        } else {
            this.serviceCount = 0;
        }
    }

    public ServiceCategoryResponse(ServiceCategory category, boolean includeServices) {
        this(category);
        if (!includeServices) {
            this.services = null;
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

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Integer getServiceCount() {
        return serviceCount;
    }

    public void setServiceCount(Integer serviceCount) {
        this.serviceCount = serviceCount;
    }

    public List<ServiceSummaryResponse> getServices() {
        return services;
    }

    public void setServices(List<ServiceSummaryResponse> services) {
        this.services = services;
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

    @Override
    public String toString() {
        return "ServiceCategoryResponse{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", displayOrder=" + displayOrder +
                ", isActive=" + isActive +
                ", serviceCount=" + serviceCount +
                '}';
    }
}
