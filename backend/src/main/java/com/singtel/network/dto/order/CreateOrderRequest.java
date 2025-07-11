package com.singtel.network.dto.order;

import com.singtel.network.entity.Order;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO for creating a new order request.
 */
public class CreateOrderRequest {

    @NotNull(message = "Service ID is required")
    private UUID serviceId;

    @NotNull(message = "Order type is required")
    private Order.OrderType orderType;

    @NotNull(message = "Requested bandwidth is required")
    @Min(value = 1, message = "Requested bandwidth must be at least 1 Mbps")
    @Max(value = 100000, message = "Requested bandwidth must not exceed 100,000 Mbps")
    private Integer requestedBandwidthMbps;

    @NotBlank(message = "Installation address is required")
    @Size(max = 1000, message = "Installation address must not exceed 1000 characters")
    private String installationAddress;

    @Size(max = 20, message = "Postal code must not exceed 20 characters")
    private String postalCode;

    @NotBlank(message = "Contact person is required")
    @Size(max = 255, message = "Contact person must not exceed 255 characters")
    private String contactPerson;

    @NotBlank(message = "Contact phone is required")
    @Size(max = 50, message = "Contact phone must not exceed 50 characters")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Contact phone must be a valid phone number")
    private String contactPhone;

    @NotBlank(message = "Contact email is required")
    @Email(message = "Contact email must be a valid email address")
    @Size(max = 255, message = "Contact email must not exceed 255 characters")
    private String contactEmail;

    @Future(message = "Requested date must be in the future")
    private LocalDate requestedDate;

    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    private String notes;

    // For modify/terminate orders
    private UUID serviceInstanceId;

    // Constructors
    public CreateOrderRequest() {
    }

    public CreateOrderRequest(UUID serviceId, Order.OrderType orderType, Integer requestedBandwidthMbps,
                             String installationAddress, String contactPerson, String contactPhone, String contactEmail) {
        this.serviceId = serviceId;
        this.orderType = orderType;
        this.requestedBandwidthMbps = requestedBandwidthMbps;
        this.installationAddress = installationAddress;
        this.contactPerson = contactPerson;
        this.contactPhone = contactPhone;
        this.contactEmail = contactEmail;
    }

    // Getters and Setters
    public UUID getServiceId() {
        return serviceId;
    }

    public void setServiceId(UUID serviceId) {
        this.serviceId = serviceId;
    }

    public Order.OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(Order.OrderType orderType) {
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public UUID getServiceInstanceId() {
        return serviceInstanceId;
    }

    public void setServiceInstanceId(UUID serviceInstanceId) {
        this.serviceInstanceId = serviceInstanceId;
    }

    // Validation methods
    public boolean isNewServiceOrder() {
        return orderType == Order.OrderType.NEW_SERVICE;
    }

    public boolean isModifyServiceOrder() {
        return orderType == Order.OrderType.MODIFY_SERVICE;
    }

    public boolean isTerminateServiceOrder() {
        return orderType == Order.OrderType.TERMINATE_SERVICE;
    }

    @AssertTrue(message = "Service instance ID is required for modify/terminate orders")
    public boolean isServiceInstanceIdValidForOrderType() {
        if (isModifyServiceOrder() || isTerminateServiceOrder()) {
            return serviceInstanceId != null;
        }
        return true;
    }

    @AssertTrue(message = "Bandwidth is not required for terminate orders")
    public boolean isBandwidthValidForOrderType() {
        if (isTerminateServiceOrder()) {
            return requestedBandwidthMbps == null;
        }
        return requestedBandwidthMbps != null;
    }

    @Override
    public String toString() {
        return "CreateOrderRequest{" +
                "serviceId=" + serviceId +
                ", orderType=" + orderType +
                ", requestedBandwidthMbps=" + requestedBandwidthMbps +
                ", contactPerson='" + contactPerson + '\'' +
                ", contactEmail='" + contactEmail + '\'' +
                ", serviceInstanceId=" + serviceInstanceId +
                '}';
    }
}
