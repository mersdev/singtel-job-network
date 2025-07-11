package com.singtel.network.repository;

import com.singtel.network.entity.BandwidthChange;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repository interface for BandwidthChange entity operations.
 */
@Repository
public interface BandwidthChangeRepository extends JpaRepository<BandwidthChange, UUID> {

    /**
     * Find bandwidth changes by service instance ID
     */
    List<BandwidthChange> findByServiceInstanceId(UUID serviceInstanceId);

    /**
     * Find bandwidth changes by service instance ID with pagination
     */
    Page<BandwidthChange> findByServiceInstanceId(UUID serviceInstanceId, Pageable pageable);

    /**
     * Find bandwidth changes by service instance ID ordered by creation date
     */
    List<BandwidthChange> findByServiceInstanceIdOrderByCreatedAtDesc(UUID serviceInstanceId);

    /**
     * Find bandwidth changes by user ID
     */
    List<BandwidthChange> findByUserId(UUID userId);

    /**
     * Find bandwidth changes by status
     */
    List<BandwidthChange> findByStatus(BandwidthChange.BandwidthChangeStatus status);

    /**
     * Find bandwidth changes by status with pagination
     */
    Page<BandwidthChange> findByStatus(BandwidthChange.BandwidthChangeStatus status, Pageable pageable);

    /**
     * Find pending bandwidth changes
     */
    @Query("SELECT bc FROM BandwidthChange bc WHERE bc.status IN ('PENDING', 'SCHEDULED')")
    List<BandwidthChange> findPendingChanges();

    /**
     * Find applied bandwidth changes
     */
    @Query("SELECT bc FROM BandwidthChange bc WHERE bc.status = 'APPLIED'")
    List<BandwidthChange> findAppliedChanges();

    /**
     * Find bandwidth changes by company
     */
    @Query("SELECT bc FROM BandwidthChange bc WHERE bc.serviceInstance.company.id = :companyId")
    List<BandwidthChange> findByCompanyId(@Param("companyId") UUID companyId);

    /**
     * Find bandwidth changes by company with pagination
     */
    @Query("SELECT bc FROM BandwidthChange bc WHERE bc.serviceInstance.company.id = :companyId")
    Page<BandwidthChange> findByCompanyId(@Param("companyId") UUID companyId, Pageable pageable);

    /**
     * Find bandwidth changes by company and status
     */
    @Query("SELECT bc FROM BandwidthChange bc WHERE bc.serviceInstance.company.id = :companyId AND bc.status = :status")
    List<BandwidthChange> findByCompanyIdAndStatus(@Param("companyId") UUID companyId, 
                                                  @Param("status") BandwidthChange.BandwidthChangeStatus status);

    /**
     * Find bandwidth changes scheduled for specific time range
     */
    @Query("SELECT bc FROM BandwidthChange bc WHERE bc.scheduledAt BETWEEN :startTime AND :endTime AND bc.status = 'SCHEDULED'")
    List<BandwidthChange> findScheduledInTimeRange(@Param("startTime") LocalDateTime startTime, 
                                                  @Param("endTime") LocalDateTime endTime);

    /**
     * Find bandwidth changes applied in date range
     */
    @Query("SELECT bc FROM BandwidthChange bc WHERE bc.appliedAt BETWEEN :startTime AND :endTime AND bc.status = 'APPLIED'")
    List<BandwidthChange> findAppliedInTimeRange(@Param("startTime") LocalDateTime startTime, 
                                                @Param("endTime") LocalDateTime endTime);

    /**
     * Find bandwidth increases
     */
    @Query("SELECT bc FROM BandwidthChange bc WHERE bc.newBandwidthMbps > bc.previousBandwidthMbps")
    List<BandwidthChange> findBandwidthIncreases();

    /**
     * Find bandwidth decreases
     */
    @Query("SELECT bc FROM BandwidthChange bc WHERE bc.newBandwidthMbps < bc.previousBandwidthMbps")
    List<BandwidthChange> findBandwidthDecreases();

    /**
     * Find bandwidth changes by workflow ID
     */
    List<BandwidthChange> findByWorkflowId(String workflowId);

    /**
     * Find bandwidth changes without workflow ID
     */
    @Query("SELECT bc FROM BandwidthChange bc WHERE bc.workflowId IS NULL")
    List<BandwidthChange> findChangesWithoutWorkflowId();

    /**
     * Find recent bandwidth changes by service instance
     */
    @Query("SELECT bc FROM BandwidthChange bc WHERE bc.serviceInstance.id = :serviceInstanceId ORDER BY bc.createdAt DESC")
    List<BandwidthChange> findRecentChangesByServiceInstance(@Param("serviceInstanceId") UUID serviceInstanceId, Pageable pageable);

    /**
     * Find latest bandwidth change by service instance
     */
    @Query("SELECT bc FROM BandwidthChange bc WHERE bc.serviceInstance.id = :serviceInstanceId ORDER BY bc.createdAt DESC")
    List<BandwidthChange> findLatestByServiceInstance(@Param("serviceInstanceId") UUID serviceInstanceId);

    /**
     * Count bandwidth changes by service instance
     */
    long countByServiceInstanceId(UUID serviceInstanceId);

    /**
     * Count bandwidth changes by status
     */
    long countByStatus(BandwidthChange.BandwidthChangeStatus status);

    /**
     * Count bandwidth changes by company
     */
    @Query("SELECT COUNT(bc) FROM BandwidthChange bc WHERE bc.serviceInstance.company.id = :companyId")
    long countByCompanyId(@Param("companyId") UUID companyId);

    /**
     * Find bandwidth changes with positive cost impact
     */
    @Query("SELECT bc FROM BandwidthChange bc WHERE bc.costImpact > 0")
    List<BandwidthChange> findChangesWithPositiveCostImpact();

    /**
     * Find bandwidth changes with negative cost impact
     */
    @Query("SELECT bc FROM BandwidthChange bc WHERE bc.costImpact < 0")
    List<BandwidthChange> findChangesWithNegativeCostImpact();

    /**
     * Calculate total cost impact by company
     */
    @Query("SELECT COALESCE(SUM(bc.costImpact), 0) FROM BandwidthChange bc WHERE bc.serviceInstance.company.id = :companyId AND bc.status = 'APPLIED'")
    java.math.BigDecimal calculateTotalCostImpactByCompany(@Param("companyId") UUID companyId);

    /**
     * Calculate total cost impact by service instance
     */
    @Query("SELECT COALESCE(SUM(bc.costImpact), 0) FROM BandwidthChange bc WHERE bc.serviceInstance.id = :serviceInstanceId AND bc.status = 'APPLIED'")
    java.math.BigDecimal calculateTotalCostImpactByServiceInstance(@Param("serviceInstanceId") UUID serviceInstanceId);

    /**
     * Find overdue scheduled changes
     */
    @Query("SELECT bc FROM BandwidthChange bc WHERE bc.scheduledAt < :currentTime AND bc.status = 'SCHEDULED'")
    List<BandwidthChange> findOverdueScheduledChanges(@Param("currentTime") LocalDateTime currentTime);

    /**
     * Search bandwidth changes with multiple criteria
     */
    @Query("SELECT bc FROM BandwidthChange bc WHERE " +
           "(:serviceInstanceId IS NULL OR bc.serviceInstance.id = :serviceInstanceId) AND " +
           "(:companyId IS NULL OR bc.serviceInstance.company.id = :companyId) AND " +
           "(:status IS NULL OR bc.status = :status) AND " +
           "(:startDate IS NULL OR bc.createdAt >= :startDate) AND " +
           "(:endDate IS NULL OR bc.createdAt <= :endDate) AND " +
           "(:userId IS NULL OR bc.user.id = :userId)")
    Page<BandwidthChange> searchBandwidthChanges(@Param("serviceInstanceId") UUID serviceInstanceId,
                                               @Param("companyId") UUID companyId,
                                               @Param("status") BandwidthChange.BandwidthChangeStatus status,
                                               @Param("startDate") LocalDateTime startDate,
                                               @Param("endDate") LocalDateTime endDate,
                                               @Param("userId") UUID userId,
                                               Pageable pageable);
}
