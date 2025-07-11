package com.singtel.network.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

/**
 * Service category entity for organizing network services.
 */
@Entity
@Table(name = "service_categories", schema = "singtel_app")
public class ServiceCategory extends BaseEntity {

    @NotBlank(message = "Category name is required")
    @Size(max = 100, message = "Category name must not exceed 100 characters")
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "display_order")
    private Integer displayOrder = 0;

    @Column(name = "is_active")
    private Boolean isActive = true;

    // Relationships
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Service> services = new ArrayList<>();

    // Constructors
    public ServiceCategory() {
    }

    public ServiceCategory(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Getters and Setters
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

    public List<Service> getServices() {
        return services;
    }

    public void setServices(List<Service> services) {
        this.services = services;
    }

    // Helper methods
    public void addService(Service service) {
        services.add(service);
        service.setCategory(this);
    }

    public void removeService(Service service) {
        services.remove(service);
        service.setCategory(null);
    }

    public boolean isActive() {
        return Boolean.TRUE.equals(isActive);
    }

    @Override
    public String toString() {
        return "ServiceCategory{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", displayOrder=" + displayOrder +
                ", isActive=" + isActive +
                '}';
    }
}
