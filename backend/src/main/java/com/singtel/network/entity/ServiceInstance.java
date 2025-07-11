package com.singtel.network.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service instance entity representing active services subscribed by companies.
 */
@Entity
@Table(name = "service_instances", schema = "singtel_app")
public class ServiceInstance extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;

    @NotBlank(message = "Instance name is required")
    @Size(max = 255, message = "Instance name must not exceed 255 characters")
    @Column(name = "instance_name", nullable = false)
    private String instanceName;

    @Min(value = 1, message = "Current bandwidth must be at least 1 Mbps")
    @Column(name = "current_bandwidth_mbps", nullable = false)
    private Integer currentBandwidthMbps;

    @NotBlank(message = "Installation address is required")
    @Column(name = "installation_address", nullable = false, columnDefinition = "TEXT")
    private String installationAddress;

    @Size(max = 20, message = "Postal code must not exceed 20 characters")
    @Column(name = "postal_code")
    private String postalCode;

    @Size(max = 255, message = "Contact person must not exceed 255 characters")
    @Column(name = "contact_person")
    private String contactPerson;

    @Size(max = 50, message = "Contact phone must not exceed 50 characters")
    @Column(name = "contact_phone")
    private String contactPhone;

    @Size(max = 255, message = "Contact email must not exceed 255 characters")
    @Column(name = "contact_email")
    private String contactEmail;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ServiceInstanceStatus status = ServiceInstanceStatus.PENDING;

    @DecimalMin(value = "0.0", message = "Monthly cost must be non-negative")
    @Column(name = "monthly_cost", precision = 10, scale = 2)
    private BigDecimal monthlyCost;

    @Column(name = "contract_start_date")
    private LocalDate contractStartDate;

    @Column(name = "contract_end_date")
    private LocalDate contractEndDate;

    @Column(name = "last_bandwidth_change_at")
    private LocalDateTime lastBandwidthChangeAt;

    @Column(name = "provisioned_at")
    private LocalDateTime provisionedAt;

    // Relationships
    @OneToMany(mappedBy = "serviceInstance", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BandwidthChange> bandwidthChanges = new ArrayList<>();

    @OneToOne(mappedBy = "serviceInstance", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Order order;

    // Constructors
    public ServiceInstance() {
    }

    public ServiceInstance(Company company, Service service, String instanceName, 
                          Integer currentBandwidthMbps, String installationAddress) {
        this.company = company;
        this.service = service;
        this.instanceName = instanceName;
        this.currentBandwidthMbps = currentBandwidthMbps;
        this.installationAddress = installationAddress;
    }

    // Getters and Setters
    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public Integer getCurrentBandwidthMbps() {
        return currentBandwidthMbps;
    }

    public void setCurrentBandwidthMbps(Integer currentBandwidthMbps) {
        this.currentBandwidthMbps = currentBandwidthMbps;
    }

    public String getInstallationAddress() {
        return installationAddress;
    }

    public void setInstallationAddress(String installationAddress) {
        this.installationAddress = installationAddress;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public ServiceInstanceStatus getStatus() {
        return status;
    }

    public void setStatus(ServiceInstanceStatus status) {
        this.status = status;
    }

    public BigDecimal getMonthlyCost() {
        return monthlyCost;
    }

    public void setMonthlyCost(BigDecimal monthlyCost) {
        this.monthlyCost = monthlyCost;
    }

    public LocalDate getContractStartDate() {
        return contractStartDate;
    }

    public void setContractStartDate(LocalDate contractStartDate) {
        this.contractStartDate = contractStartDate;
    }

    public LocalDate getContractEndDate() {
        return contractEndDate;
    }

    public void setContractEndDate(LocalDate contractEndDate) {
        this.contractEndDate = contractEndDate;
    }

    public LocalDateTime getLastBandwidthChangeAt() {
        return lastBandwidthChangeAt;
    }

    public void setLastBandwidthChangeAt(LocalDateTime lastBandwidthChangeAt) {
        this.lastBandwidthChangeAt = lastBandwidthChangeAt;
    }

    public LocalDateTime getProvisionedAt() {
        return provisionedAt;
    }

    public void setProvisionedAt(LocalDateTime provisionedAt) {
        this.provisionedAt = provisionedAt;
    }

    public List<BandwidthChange> getBandwidthChanges() {
        return bandwidthChanges;
    }

    public void setBandwidthChanges(List<BandwidthChange> bandwidthChanges) {
        this.bandwidthChanges = bandwidthChanges;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    // Helper methods
    public boolean isActive() {
        return status == ServiceInstanceStatus.ACTIVE;
    }

    public boolean canAdjustBandwidth() {
        return isActive() && service != null && service.isBandwidthAdjustable();
    }

    public void updateBandwidth(Integer newBandwidthMbps) {
        if (service != null && service.isValidBandwidth(newBandwidthMbps)) {
            this.currentBandwidthMbps = newBandwidthMbps;
            this.lastBandwidthChangeAt = LocalDateTime.now();
            
            // Recalculate monthly cost
            if (service.getBasePriceMonthly() != null) {
                this.monthlyCost = service.calculateMonthlyCost(newBandwidthMbps);
            }
        }
    }

    public void provision() {
        this.status = ServiceInstanceStatus.ACTIVE;
        this.provisionedAt = LocalDateTime.now();
        if (contractStartDate == null) {
            this.contractStartDate = LocalDate.now();
        }
        if (contractEndDate == null && service != null && service.getContractTermMonths() != null) {
            this.contractEndDate = contractStartDate.plusMonths(service.getContractTermMonths());
        }
    }

    @Override
    public String toString() {
        return "ServiceInstance{" +
                "id=" + getId() +
                ", instanceName='" + instanceName + '\'' +
                ", currentBandwidthMbps=" + currentBandwidthMbps +
                ", status=" + status +
                ", monthlyCost=" + monthlyCost +
                '}';
    }

    /**
     * Service instance status enumeration
     */
    public enum ServiceInstanceStatus {
        PENDING, PROVISIONING, ACTIVE, SUSPENDED, TERMINATED
    }
}
