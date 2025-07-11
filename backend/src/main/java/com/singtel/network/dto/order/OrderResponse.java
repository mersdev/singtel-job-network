package com.singtel.network.dto.order;

import com.singtel.network.entity.Order;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for order response.
 */
public class OrderResponse {

    private UUID id;
    private String orderNumber;
    private Order.OrderType orderType;
    private Order.OrderStatus status;
    private Integer requestedBandwidthMbps;
    private String installationAddress;
    private String postalCode;
    private String contactPerson;
    private String contactPhone;
    private String contactEmail;
    private LocalDate requestedDate;
    private LocalDate estimatedCompletionDate;
    private LocalDate actualCompletionDate;
    private BigDecimal totalCost;
    private String notes;
    private String workflowId;
    private ServiceInfo service;
    private ServiceInstanceInfo serviceInstance;
    private UserInfo user;
    private CompanyInfo company;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public OrderResponse() {
    }

    public OrderResponse(Order order) {
        this.id = order.getId();
        this.orderNumber = order.getOrderNumber();
        this.orderType = order.getOrderType();
        this.status = order.getStatus();
        this.requestedBandwidthMbps = order.getRequestedBandwidthMbps();
        this.installationAddress = order.getInstallationAddress();
        this.postalCode = order.getPostalCode();
        this.contactPerson = order.getContactPerson();
        this.contactPhone = order.getContactPhone();
        this.contactEmail = order.getContactEmail();
        this.requestedDate = order.getRequestedDate();
        this.estimatedCompletionDate = order.getEstimatedCompletionDate();
        this.actualCompletionDate = order.getActualCompletionDate();
        this.totalCost = order.getTotalCost();
        this.notes = order.getNotes();
        this.workflowId = order.getWorkflowId();
        this.createdAt = order.getCreatedAt();
        this.updatedAt = order.getUpdatedAt();

        if (order.getService() != null) {
            this.service = new ServiceInfo(order.getService().getId(), 
                                         order.getService().getName(), 
                                         order.getService().getServiceType());
        }

        if (order.getServiceInstance() != null) {
            this.serviceInstance = new ServiceInstanceInfo(order.getServiceInstance().getId(),
                                                          order.getServiceInstance().getInstanceName(),
                                                          order.getServiceInstance().getStatus());
        }

        if (order.getUser() != null) {
            this.user = new UserInfo(order.getUser().getId(),
                                   order.getUser().getUsername(),
                                   order.getUser().getFullName());
        }

        if (order.getCompany() != null) {
            this.company = new CompanyInfo(order.getCompany().getId(),
                                         order.getCompany().getName());
        }
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Order.OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(Order.OrderType orderType) {
        this.orderType = orderType;
    }

    public Order.OrderStatus getStatus() {
        return status;
    }

    public void setStatus(Order.OrderStatus status) {
        this.status = status;
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

    public ServiceInfo getService() {
        return service;
    }

    public void setService(ServiceInfo service) {
        this.service = service;
    }

    public ServiceInstanceInfo getServiceInstance() {
        return serviceInstance;
    }

    public void setServiceInstance(ServiceInstanceInfo serviceInstance) {
        this.serviceInstance = serviceInstance;
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    public CompanyInfo getCompany() {
        return company;
    }

    public void setCompany(CompanyInfo company) {
        this.company = company;
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
    public boolean isCompleted() {
        return status == Order.OrderStatus.COMPLETED;
    }

    public boolean canCancel() {
        return status == Order.OrderStatus.SUBMITTED || status == Order.OrderStatus.APPROVED;
    }

    public boolean isPending() {
        return status == Order.OrderStatus.SUBMITTED || 
               status == Order.OrderStatus.APPROVED || 
               status == Order.OrderStatus.IN_PROGRESS;
    }

    // Nested classes for related entity information
    public static class ServiceInfo {
        private UUID id;
        private String name;
        private String serviceType;

        public ServiceInfo() {}

        public ServiceInfo(UUID id, String name, String serviceType) {
            this.id = id;
            this.name = name;
            this.serviceType = serviceType;
        }

        // Getters and setters
        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getServiceType() { return serviceType; }
        public void setServiceType(String serviceType) { this.serviceType = serviceType; }
    }

    public static class ServiceInstanceInfo {
        private UUID id;
        private String instanceName;
        private String status;

        public ServiceInstanceInfo() {}

        public ServiceInstanceInfo(UUID id, String instanceName, Object status) {
            this.id = id;
            this.instanceName = instanceName;
            this.status = status != null ? status.toString() : null;
        }

        // Getters and setters
        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }
        public String getInstanceName() { return instanceName; }
        public void setInstanceName(String instanceName) { this.instanceName = instanceName; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    public static class UserInfo {
        private UUID id;
        private String username;
        private String fullName;

        public UserInfo() {}

        public UserInfo(UUID id, String username, String fullName) {
            this.id = id;
            this.username = username;
            this.fullName = fullName;
        }

        // Getters and setters
        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
    }

    public static class CompanyInfo {
        private UUID id;
        private String name;

        public CompanyInfo() {}

        public CompanyInfo(UUID id, String name) {
            this.id = id;
            this.name = name;
        }

        // Getters and setters
        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    @Override
    public String toString() {
        return "OrderResponse{" +
                "id=" + id +
                ", orderNumber='" + orderNumber + '\'' +
                ", orderType=" + orderType +
                ", status=" + status +
                ", totalCost=" + totalCost +
                '}';
    }
}
