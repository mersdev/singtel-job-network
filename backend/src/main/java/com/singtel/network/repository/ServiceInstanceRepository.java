package com.singtel.network.repository;

import com.singtel.network.entity.ServiceInstance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Repository interface for ServiceInstance entity operations.
 */
@Repository
public interface ServiceInstanceRepository extends JpaRepository<ServiceInstance, UUID> {

    /**
     * Find service instances by company ID
     */
    List<ServiceInstance> findByCompanyId(UUID companyId);

    /**
     * Find service instances by company ID with pagination
     */
    Page<ServiceInstance> findByCompanyId(UUID companyId, Pageable pageable);

    /**
     * Find service instances by service ID
     */
    List<ServiceInstance> findByServiceId(UUID serviceId);

    /**
     * Find service instances by status
     */
    List<ServiceInstance> findByStatus(ServiceInstance.ServiceInstanceStatus status);

    /**
     * Find service instances by status with pagination
     */
    Page<ServiceInstance> findByStatus(ServiceInstance.ServiceInstanceStatus status, Pageable pageable);

    /**
     * Find service instances by company and status
     */
    List<ServiceInstance> findByCompanyIdAndStatus(UUID companyId, ServiceInstance.ServiceInstanceStatus status);

    /**
     * Find service instances by company and status with pagination
     */
    Page<ServiceInstance> findByCompanyIdAndStatus(UUID companyId, ServiceInstance.ServiceInstanceStatus status, Pageable pageable);

    /**
     * Find active service instances by company
     */
    @Query("SELECT si FROM ServiceInstance si WHERE si.company.id = :companyId AND si.status = 'ACTIVE'")
    List<ServiceInstance> findActiveServiceInstancesByCompany(@Param("companyId") UUID companyId);

    /**
     * Find active service instances by company with pagination
     */
    @Query("SELECT si FROM ServiceInstance si WHERE si.company.id = :companyId AND si.status = 'ACTIVE'")
    Page<ServiceInstance> findActiveServiceInstancesByCompany(@Param("companyId") UUID companyId, Pageable pageable);

    /**
     * Find service instances by service type
     */
    @Query("SELECT si FROM ServiceInstance si WHERE si.service.serviceType = :serviceType")
    List<ServiceInstance> findByServiceType(@Param("serviceType") String serviceType);

    /**
     * Find service instances by bandwidth range
     */
    @Query("SELECT si FROM ServiceInstance si WHERE si.currentBandwidthMbps BETWEEN :minBandwidth AND :maxBandwidth")
    List<ServiceInstance> findByBandwidthRange(@Param("minBandwidth") Integer minBandwidth, @Param("maxBandwidth") Integer maxBandwidth);

    /**
     * Find service instances by cost range
     */
    @Query("SELECT si FROM ServiceInstance si WHERE si.monthlyCost BETWEEN :minCost AND :maxCost")
    List<ServiceInstance> findByCostRange(@Param("minCost") BigDecimal minCost, @Param("maxCost") BigDecimal maxCost);

    /**
     * Find service instances expiring soon
     */
    @Query("SELECT si FROM ServiceInstance si WHERE si.contractEndDate BETWEEN :startDate AND :endDate AND si.status = 'ACTIVE'")
    List<ServiceInstance> findExpiringSoon(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * Find expired service instances
     */
    @Query("SELECT si FROM ServiceInstance si WHERE si.contractEndDate < :currentDate AND si.status = 'ACTIVE'")
    List<ServiceInstance> findExpiredInstances(@Param("currentDate") LocalDate currentDate);

    /**
     * Search service instances by name
     */
    @Query("SELECT si FROM ServiceInstance si WHERE LOWER(si.instanceName) LIKE LOWER(CONCAT('%', :name, '%')) AND si.company.id = :companyId")
    List<ServiceInstance> findByInstanceNameContainingAndCompanyId(@Param("name") String name, @Param("companyId") UUID companyId);

    /**
     * Search service instances by name with pagination
     */
    @Query("SELECT si FROM ServiceInstance si WHERE LOWER(si.instanceName) LIKE LOWER(CONCAT('%', :name, '%')) AND si.company.id = :companyId")
    Page<ServiceInstance> findByInstanceNameContainingAndCompanyId(@Param("name") String name, @Param("companyId") UUID companyId, Pageable pageable);

    /**
     * Find service instances with bandwidth adjustable services
     */
    @Query("SELECT si FROM ServiceInstance si WHERE si.service.isBandwidthAdjustable = true AND si.status = 'ACTIVE'")
    List<ServiceInstance> findWithBandwidthAdjustableServices();

    /**
     * Count service instances by company
     */
    long countByCompanyId(UUID companyId);

    /**
     * Count active service instances by company
     */
    @Query("SELECT COUNT(si) FROM ServiceInstance si WHERE si.company.id = :companyId AND si.status = 'ACTIVE'")
    long countActiveByCompanyId(@Param("companyId") UUID companyId);

    /**
     * Count service instances by status
     */
    long countByStatus(ServiceInstance.ServiceInstanceStatus status);

    /**
     * Calculate total monthly cost by company
     */
    @Query("SELECT COALESCE(SUM(si.monthlyCost), 0) FROM ServiceInstance si WHERE si.company.id = :companyId AND si.status = 'ACTIVE'")
    BigDecimal calculateTotalMonthlyCostByCompany(@Param("companyId") UUID companyId);

    /**
     * Calculate total bandwidth by company
     */
    @Query("SELECT COALESCE(SUM(si.currentBandwidthMbps), 0) FROM ServiceInstance si WHERE si.company.id = :companyId AND si.status = 'ACTIVE'")
    Long calculateTotalBandwidthByCompany(@Param("companyId") UUID companyId);

    /**
     * Find service instances by postal code
     */
    List<ServiceInstance> findByPostalCode(String postalCode);

    /**
     * Find service instances by installation address containing
     */
    @Query("SELECT si FROM ServiceInstance si WHERE LOWER(si.installationAddress) LIKE LOWER(CONCAT('%', :address, '%'))")
    List<ServiceInstance> findByInstallationAddressContaining(@Param("address") String address);

    /**
     * Find service instances provisioned in date range
     */
    @Query("SELECT si FROM ServiceInstance si WHERE si.provisionedAt BETWEEN :startDate AND :endDate")
    List<ServiceInstance> findProvisionedInDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * Find service instances with recent bandwidth changes
     */
    @Query("SELECT si FROM ServiceInstance si WHERE si.lastBandwidthChangeAt >= :sinceDate")
    List<ServiceInstance> findWithRecentBandwidthChanges(@Param("sinceDate") LocalDate sinceDate);

    /**
     * Search service instances with multiple criteria
     */
    @Query("SELECT si FROM ServiceInstance si WHERE " +
           "si.company.id = :companyId AND " +
           "(:status IS NULL OR si.status = :status) AND " +
           "(:serviceType IS NULL OR si.service.serviceType = :serviceType) AND " +
           "(:minBandwidth IS NULL OR si.currentBandwidthMbps >= :minBandwidth) AND " +
           "(:maxBandwidth IS NULL OR si.currentBandwidthMbps <= :maxBandwidth) AND " +
           "(:minCost IS NULL OR si.monthlyCost >= :minCost) AND " +
           "(:maxCost IS NULL OR si.monthlyCost <= :maxCost)")
    Page<ServiceInstance> searchServiceInstances(@Param("companyId") UUID companyId,
                                               @Param("status") ServiceInstance.ServiceInstanceStatus status,
                                               @Param("serviceType") String serviceType,
                                               @Param("minBandwidth") Integer minBandwidth,
                                               @Param("maxBandwidth") Integer maxBandwidth,
                                               @Param("minCost") BigDecimal minCost,
                                               @Param("maxCost") BigDecimal maxCost,
                                               Pageable pageable);
}
