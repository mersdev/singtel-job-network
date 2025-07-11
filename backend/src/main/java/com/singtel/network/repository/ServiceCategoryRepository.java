package com.singtel.network.repository;

import com.singtel.network.entity.ServiceCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for ServiceCategory entity operations.
 */
@Repository
public interface ServiceCategoryRepository extends JpaRepository<ServiceCategory, UUID> {

    /**
     * Find category by name
     */
    Optional<ServiceCategory> findByName(String name);

    /**
     * Find active categories
     */
    @Query("SELECT sc FROM ServiceCategory sc WHERE sc.isActive = true ORDER BY sc.displayOrder, sc.name")
    List<ServiceCategory> findActiveCategories();

    /**
     * Find active categories with pagination
     */
    @Query("SELECT sc FROM ServiceCategory sc WHERE sc.isActive = true ORDER BY sc.displayOrder, sc.name")
    Page<ServiceCategory> findActiveCategories(Pageable pageable);

    /**
     * Find categories by active status
     */
    List<ServiceCategory> findByIsActiveOrderByDisplayOrderAscNameAsc(Boolean isActive);

    /**
     * Find categories by active status with pagination
     */
    Page<ServiceCategory> findByIsActiveOrderByDisplayOrderAscNameAsc(Boolean isActive, Pageable pageable);

    /**
     * Search categories by name
     */
    @Query("SELECT sc FROM ServiceCategory sc WHERE LOWER(sc.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<ServiceCategory> findByNameContainingIgnoreCase(@Param("name") String name);

    /**
     * Search categories by name with pagination
     */
    @Query("SELECT sc FROM ServiceCategory sc WHERE LOWER(sc.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<ServiceCategory> findByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);

    /**
     * Find categories with services
     */
    @Query("SELECT DISTINCT sc FROM ServiceCategory sc JOIN sc.services s WHERE s.isAvailable = true")
    List<ServiceCategory> findCategoriesWithAvailableServices();

    /**
     * Find categories without services
     */
    @Query("SELECT sc FROM ServiceCategory sc WHERE sc.services IS EMPTY")
    List<ServiceCategory> findCategoriesWithoutServices();

    /**
     * Count categories by active status
     */
    long countByIsActive(Boolean isActive);

    /**
     * Count active categories
     */
    @Query("SELECT COUNT(sc) FROM ServiceCategory sc WHERE sc.isActive = true")
    long countActiveCategories();

    /**
     * Find categories ordered by display order
     */
    List<ServiceCategory> findAllByOrderByDisplayOrderAscNameAsc();

    /**
     * Find categories by display order range
     */
    @Query("SELECT sc FROM ServiceCategory sc WHERE sc.displayOrder BETWEEN :minOrder AND :maxOrder ORDER BY sc.displayOrder")
    List<ServiceCategory> findByDisplayOrderRange(@Param("minOrder") Integer minOrder, @Param("maxOrder") Integer maxOrder);

    /**
     * Check if category name exists
     */
    boolean existsByName(String name);

    /**
     * Check if category name exists excluding specific ID
     */
    @Query("SELECT CASE WHEN COUNT(sc) > 0 THEN true ELSE false END FROM ServiceCategory sc WHERE sc.name = :name AND sc.id != :id")
    boolean existsByNameAndIdNot(@Param("name") String name, @Param("id") UUID id);

    /**
     * Find next display order
     */
    @Query("SELECT COALESCE(MAX(sc.displayOrder), 0) + 1 FROM ServiceCategory sc")
    Integer findNextDisplayOrder();

    /**
     * Find categories with service count
     */
    @Query("SELECT sc, COUNT(s) as serviceCount FROM ServiceCategory sc LEFT JOIN sc.services s " +
           "WHERE sc.isActive = true GROUP BY sc ORDER BY sc.displayOrder, sc.name")
    List<Object[]> findActiveCategoriesWithServiceCount();
}
