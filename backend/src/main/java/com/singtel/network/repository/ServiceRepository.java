package com.singtel.network.repository;

import com.singtel.network.entity.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Repository interface for Service entity operations.
 */
@Repository
public interface ServiceRepository extends JpaRepository<Service, UUID> {

    /**
     * Find services by category ID
     */
    List<Service> findByCategoryId(UUID categoryId);

    /**
     * Find services by category ID with pagination
     */
    Page<Service> findByCategoryId(UUID categoryId, Pageable pageable);

    /**
     * Find services by service type
     */
    List<Service> findByServiceType(String serviceType);

    /**
     * Find services by service type with pagination
     */
    Page<Service> findByServiceType(String serviceType, Pageable pageable);

    /**
     * Find available services
     */
    @Query("SELECT s FROM Service s WHERE s.isAvailable = true")
    List<Service> findAvailableServices();

    /**
     * Find available services with pagination
     */
    @Query("SELECT s FROM Service s WHERE s.isAvailable = true")
    Page<Service> findAvailableServices(Pageable pageable);

    /**
     * Find services by availability status
     */
    List<Service> findByIsAvailable(Boolean isAvailable);

    /**
     * Find services with bandwidth adjustable feature
     */
    @Query("SELECT s FROM Service s WHERE s.isBandwidthAdjustable = true AND s.isAvailable = true")
    List<Service> findBandwidthAdjustableServices();

    /**
     * Find services by price range
     */
    @Query("SELECT s FROM Service s WHERE s.basePriceMonthly BETWEEN :minPrice AND :maxPrice AND s.isAvailable = true")
    List<Service> findByPriceRange(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);

    /**
     * Find services by bandwidth range
     */
    @Query("SELECT s FROM Service s WHERE s.baseBandwidthMbps BETWEEN :minBandwidth AND :maxBandwidth AND s.isAvailable = true")
    List<Service> findByBandwidthRange(@Param("minBandwidth") Integer minBandwidth, @Param("maxBandwidth") Integer maxBandwidth);

    /**
     * Search services by name
     */
    @Query("SELECT s FROM Service s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%')) AND s.isAvailable = true")
    List<Service> findByNameContainingIgnoreCase(@Param("name") String name);

    /**
     * Search services by name with pagination
     */
    @Query("SELECT s FROM Service s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%')) AND s.isAvailable = true")
    Page<Service> findByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);

    /**
     * Find services by category and type
     */
    @Query("SELECT s FROM Service s WHERE s.category.id = :categoryId AND s.serviceType = :serviceType AND s.isAvailable = true")
    List<Service> findByCategoryAndType(@Param("categoryId") UUID categoryId, @Param("serviceType") String serviceType);

    /**
     * Search services with multiple criteria
     */
    @Query("SELECT s FROM Service s WHERE " +
           "(:categoryId IS NULL OR s.category.id = :categoryId) AND " +
           "(:serviceType IS NULL OR s.serviceType = :serviceType) AND " +
           "(:minPrice IS NULL OR s.basePriceMonthly >= :minPrice) AND " +
           "(:maxPrice IS NULL OR s.basePriceMonthly <= :maxPrice) AND " +
           "(:minBandwidth IS NULL OR s.baseBandwidthMbps >= :minBandwidth) AND " +
           "(:maxBandwidth IS NULL OR s.baseBandwidthMbps <= :maxBandwidth) AND " +
           "(:bandwidthAdjustable IS NULL OR s.isBandwidthAdjustable = :bandwidthAdjustable) AND " +
           "s.isAvailable = true")
    Page<Service> searchServices(@Param("categoryId") UUID categoryId,
                                @Param("serviceType") String serviceType,
                                @Param("minPrice") BigDecimal minPrice,
                                @Param("maxPrice") BigDecimal maxPrice,
                                @Param("minBandwidth") Integer minBandwidth,
                                @Param("maxBandwidth") Integer maxBandwidth,
                                @Param("bandwidthAdjustable") Boolean bandwidthAdjustable,
                                Pageable pageable);

    /**
     * Get distinct service types
     */
    @Query("SELECT DISTINCT s.serviceType FROM Service s WHERE s.isAvailable = true ORDER BY s.serviceType")
    List<String> findDistinctServiceTypes();

    /**
     * Count services by category
     */
    long countByCategoryId(UUID categoryId);

    /**
     * Count available services
     */
    @Query("SELECT COUNT(s) FROM Service s WHERE s.isAvailable = true")
    long countAvailableServices();

    /**
     * Count services by service type
     */
    long countByServiceType(String serviceType);

    /**
     * Find popular services (most ordered)
     */
    @Query("SELECT s FROM Service s JOIN s.orders o WHERE s.isAvailable = true " +
           "GROUP BY s ORDER BY COUNT(o) DESC")
    List<Service> findPopularServices(Pageable pageable);

    /**
     * Find services with active instances
     */
    @Query("SELECT DISTINCT s FROM Service s JOIN s.serviceInstances si WHERE si.status = 'ACTIVE'")
    List<Service> findServicesWithActiveInstances();

    /**
     * Find services by contract term
     */
    List<Service> findByContractTermMonths(Integer contractTermMonths);

    /**
     * Find services with setup fee
     */
    @Query("SELECT s FROM Service s WHERE s.setupFee > 0 AND s.isAvailable = true")
    List<Service> findServicesWithSetupFee();

    /**
     * Find services without setup fee
     */
    @Query("SELECT s FROM Service s WHERE (s.setupFee IS NULL OR s.setupFee = 0) AND s.isAvailable = true")
    List<Service> findServicesWithoutSetupFee();
}
