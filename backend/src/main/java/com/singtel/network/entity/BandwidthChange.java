package com.singtel.network.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Bandwidth change entity for tracking bandwidth modifications.
 */
@Entity
@Table(name = "bandwidth_changes", schema = "singtel_app")
public class BandwidthChange extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_instance_id", nullable = false)
    private ServiceInstance serviceInstance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Min(value = 1, message = "Previous bandwidth must be at least 1 Mbps")
    @Column(name = "previous_bandwidth_mbps", nullable = false)
    private Integer previousBandwidthMbps;

    @Min(value = 1, message = "New bandwidth must be at least 1 Mbps")
    @Column(name = "new_bandwidth_mbps", nullable = false)
    private Integer newBandwidthMbps;

    @Size(max = 255, message = "Change reason must not exceed 255 characters")
    @Column(name = "change_reason")
    private String changeReason;

    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt;

    @Column(name = "applied_at")
    private LocalDateTime appliedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BandwidthChangeStatus status = BandwidthChangeStatus.PENDING;

    @Column(name = "cost_impact", precision = 10, scale = 2)
    private BigDecimal costImpact;

    @Size(max = 255, message = "Workflow ID must not exceed 255 characters")
    @Column(name = "workflow_id")
    private String workflowId;

    // Constructors
    public BandwidthChange() {
    }

    public BandwidthChange(ServiceInstance serviceInstance, User user, 
                          Integer previousBandwidthMbps, Integer newBandwidthMbps) {
        this.serviceInstance = serviceInstance;
        this.user = user;
        this.previousBandwidthMbps = previousBandwidthMbps;
        this.newBandwidthMbps = newBandwidthMbps;
    }

    // Getters and Setters
    public ServiceInstance getServiceInstance() {
        return serviceInstance;
    }

    public void setServiceInstance(ServiceInstance serviceInstance) {
        this.serviceInstance = serviceInstance;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getPreviousBandwidthMbps() {
        return previousBandwidthMbps;
    }

    public void setPreviousBandwidthMbps(Integer previousBandwidthMbps) {
        this.previousBandwidthMbps = previousBandwidthMbps;
    }

    public Integer getNewBandwidthMbps() {
        return newBandwidthMbps;
    }

    public void setNewBandwidthMbps(Integer newBandwidthMbps) {
        this.newBandwidthMbps = newBandwidthMbps;
    }

    public String getChangeReason() {
        return changeReason;
    }

    public void setChangeReason(String changeReason) {
        this.changeReason = changeReason;
    }

    public LocalDateTime getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(LocalDateTime scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public LocalDateTime getAppliedAt() {
        return appliedAt;
    }

    public void setAppliedAt(LocalDateTime appliedAt) {
        this.appliedAt = appliedAt;
    }

    public BandwidthChangeStatus getStatus() {
        return status;
    }

    public void setStatus(BandwidthChangeStatus status) {
        this.status = status;
    }

    public BigDecimal getCostImpact() {
        return costImpact;
    }

    public void setCostImpact(BigDecimal costImpact) {
        this.costImpact = costImpact;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    // Helper methods
    public boolean isIncrease() {
        return newBandwidthMbps > previousBandwidthMbps;
    }

    public boolean isDecrease() {
        return newBandwidthMbps < previousBandwidthMbps;
    }

    public int getBandwidthDifference() {
        return newBandwidthMbps - previousBandwidthMbps;
    }

    public void schedule(LocalDateTime scheduledTime) {
        this.scheduledAt = scheduledTime;
        this.status = BandwidthChangeStatus.SCHEDULED;
    }

    public void apply() {
        this.appliedAt = LocalDateTime.now();
        this.status = BandwidthChangeStatus.APPLIED;
        
        // Update the service instance bandwidth
        if (serviceInstance != null) {
            serviceInstance.updateBandwidth(newBandwidthMbps);
        }
    }

    public void cancel() {
        if (status == BandwidthChangeStatus.PENDING || status == BandwidthChangeStatus.SCHEDULED) {
            this.status = BandwidthChangeStatus.CANCELLED;
        }
    }

    public void fail() {
        this.status = BandwidthChangeStatus.FAILED;
    }

    public boolean canCancel() {
        return status == BandwidthChangeStatus.PENDING || status == BandwidthChangeStatus.SCHEDULED;
    }

    public boolean isApplied() {
        return status == BandwidthChangeStatus.APPLIED;
    }

    @Override
    public String toString() {
        return "BandwidthChange{" +
                "id=" + getId() +
                ", previousBandwidthMbps=" + previousBandwidthMbps +
                ", newBandwidthMbps=" + newBandwidthMbps +
                ", status=" + status +
                ", costImpact=" + costImpact +
                '}';
    }

    /**
     * Bandwidth change status enumeration
     */
    public enum BandwidthChangeStatus {
        PENDING, SCHEDULED, APPLIED, FAILED, CANCELLED
    }
}
