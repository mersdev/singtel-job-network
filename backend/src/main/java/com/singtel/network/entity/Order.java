package com.singtel.network.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Order entity for tracking service provisioning orders.
 */
@Entity
@Table(name = "orders", schema = "singtel_app")
public class Order extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_instance_id")
    private ServiceInstance serviceInstance;

    @NotBlank(message = "Order number is required")
    @Size(max = 50, message = "Order number must not exceed 50 characters")
    @Column(name = "order_number", nullable = false, unique = true)
    private String orderNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_type", nullable = false)
    private OrderType orderType;

    @Min(value = 1, message = "Requested bandwidth must be at least 1 Mbps")
    @Column(name = "requested_bandwidth_mbps")
    private Integer requestedBandwidthMbps;

    @Column(name = "installation_address", columnDefinition = "TEXT")
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

    @Column(name = "requested_date")
    private LocalDate requestedDate;

    @Column(name = "estimated_completion_date")
    private LocalDate estimatedCompletionDate;

    @Column(name = "actual_completion_date")
    private LocalDate actualCompletionDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status = OrderStatus.SUBMITTED;

    @DecimalMin(value = "0.0", message = "Total cost must be non-negative")
    @Column(name = "total_cost", precision = 10, scale = 2)
    private BigDecimal totalCost;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Size(max = 255, message = "Workflow ID must not exceed 255 characters")
    @Column(name = "workflow_id")
    private String workflowId;

    // Constructors
    public Order() {
    }

    public Order(Company company, User user, Service service, OrderType orderType, String orderNumber) {
        this.company = company;
        this.user = user;
        this.service = service;
        this.orderType = orderType;
        this.orderNumber = orderNumber;
    }

    // Getters and Setters
    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public ServiceInstance getServiceInstance() {
        return serviceInstance;
    }

    public void setServiceInstance(ServiceInstance serviceInstance) {
        this.serviceInstance = serviceInstance;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    public Integer getRequestedBandwidthMbps() {
        return requestedBandwidthMbps;
    }

    public void setRequestedBandwidthMbps(Integer requestedBandwidthMbps) {
        this.requestedBandwidthMbps = requestedBandwidthMbps;
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

    public LocalDate getRequestedDate() {
        return requestedDate;
    }

    public void setRequestedDate(LocalDate requestedDate) {
        this.requestedDate = requestedDate;
    }

    public LocalDate getEstimatedCompletionDate() {
        return estimatedCompletionDate;
    }

    public void setEstimatedCompletionDate(LocalDate estimatedCompletionDate) {
        this.estimatedCompletionDate = estimatedCompletionDate;
    }

    public LocalDate getActualCompletionDate() {
        return actualCompletionDate;
    }

    public void setActualCompletionDate(LocalDate actualCompletionDate) {
        this.actualCompletionDate = actualCompletionDate;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    // Helper methods
    public boolean isCompleted() {
        return status == OrderStatus.COMPLETED;
    }

    public boolean canCancel() {
        return status == OrderStatus.SUBMITTED || status == OrderStatus.APPROVED;
    }

    public void approve() {
        if (status == OrderStatus.SUBMITTED) {
            this.status = OrderStatus.APPROVED;
        }
    }

    public void startProcessing() {
        if (status == OrderStatus.APPROVED) {
            this.status = OrderStatus.IN_PROGRESS;
        }
    }

    public void complete() {
        if (status == OrderStatus.IN_PROGRESS) {
            this.status = OrderStatus.COMPLETED;
            this.actualCompletionDate = LocalDate.now();
        }
    }

    public void cancel() {
        if (canCancel()) {
            this.status = OrderStatus.CANCELLED;
        }
    }

    public void fail(String reason) {
        this.status = OrderStatus.FAILED;
        this.notes = (notes != null ? notes + "\n" : "") + "Failed: " + reason;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + getId() +
                ", orderNumber='" + orderNumber + '\'' +
                ", orderType=" + orderType +
                ", status=" + status +
                ", totalCost=" + totalCost +
                '}';
    }

    /**
     * Order type enumeration
     */
    public enum OrderType {
        NEW_SERVICE, MODIFY_SERVICE, TERMINATE_SERVICE
    }

    /**
     * Order status enumeration
     */
    public enum OrderStatus {
        SUBMITTED, APPROVED, IN_PROGRESS, COMPLETED, CANCELLED, FAILED
    }
}
