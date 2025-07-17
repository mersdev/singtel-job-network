package com.singtel.network.repository;

import com.singtel.network.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Order entity operations.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    /**
     * Find order by order number
     */
    Optional<Order> findByOrderNumber(String orderNumber);

    /**
     * Find orders by company ID
     */
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.service LEFT JOIN FETCH o.user LEFT JOIN FETCH o.company LEFT JOIN FETCH o.serviceInstance WHERE o.company.id = :companyId")
    List<Order> findByCompanyId(@Param("companyId") UUID companyId);

    /**
     * Find orders by company ID with pagination
     */
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.service LEFT JOIN FETCH o.user LEFT JOIN FETCH o.company LEFT JOIN FETCH o.serviceInstance WHERE o.company.id = :companyId")
    Page<Order> findByCompanyId(@Param("companyId") UUID companyId, Pageable pageable);

    /**
     * Find orders by user ID
     */
    List<Order> findByUserId(UUID userId);

    /**
     * Find orders by user ID with pagination
     */
    Page<Order> findByUserId(UUID userId, Pageable pageable);

    /**
     * Find orders by service ID
     */
    List<Order> findByServiceId(UUID serviceId);

    /**
     * Find orders by status
     */
    List<Order> findByStatus(Order.OrderStatus status);

    /**
     * Find orders by status with pagination
     */
    Page<Order> findByStatus(Order.OrderStatus status, Pageable pageable);

    /**
     * Find orders by order type
     */
    List<Order> findByOrderType(Order.OrderType orderType);

    /**
     * Find orders by order type with pagination
     */
    Page<Order> findByOrderType(Order.OrderType orderType, Pageable pageable);

    /**
     * Find orders by company and status
     */
    List<Order> findByCompanyIdAndStatus(UUID companyId, Order.OrderStatus status);

    /**
     * Find orders by company and status with pagination
     */
    Page<Order> findByCompanyIdAndStatus(UUID companyId, Order.OrderStatus status, Pageable pageable);

    /**
     * Find orders by company and order type
     */
    List<Order> findByCompanyIdAndOrderType(UUID companyId, Order.OrderType orderType);

    /**
     * Find pending orders by company
     */
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.service LEFT JOIN FETCH o.user LEFT JOIN FETCH o.company LEFT JOIN FETCH o.serviceInstance WHERE o.company.id = :companyId AND o.status IN ('SUBMITTED', 'APPROVED', 'IN_PROGRESS')")
    List<Order> findPendingOrdersByCompany(@Param("companyId") UUID companyId);

    /**
     * Find completed orders by company
     */
    @Query("SELECT o FROM Order o WHERE o.company.id = :companyId AND o.status = 'COMPLETED'")
    List<Order> findCompletedOrdersByCompany(@Param("companyId") UUID companyId);

    /**
     * Find completed orders by company with pagination
     */
    @Query("SELECT o FROM Order o WHERE o.company.id = :companyId AND o.status = 'COMPLETED'")
    Page<Order> findCompletedOrdersByCompany(@Param("companyId") UUID companyId, Pageable pageable);

    /**
     * Find orders by date range
     */
    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate")
    List<Order> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * Find orders by requested date range
     */
    @Query("SELECT o FROM Order o WHERE o.requestedDate BETWEEN :startDate AND :endDate")
    List<Order> findByRequestedDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * Find orders by estimated completion date range
     */
    @Query("SELECT o FROM Order o WHERE o.estimatedCompletionDate BETWEEN :startDate AND :endDate")
    List<Order> findByEstimatedCompletionDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * Find overdue orders
     */
    @Query("SELECT o FROM Order o WHERE o.estimatedCompletionDate < :currentDate AND o.status IN ('SUBMITTED', 'APPROVED', 'IN_PROGRESS')")
    List<Order> findOverdueOrders(@Param("currentDate") LocalDate currentDate);

    /**
     * Find orders by cost range
     */
    @Query("SELECT o FROM Order o WHERE o.totalCost BETWEEN :minCost AND :maxCost")
    List<Order> findByCostRange(@Param("minCost") BigDecimal minCost, @Param("maxCost") BigDecimal maxCost);

    /**
     * Search orders by order number containing
     */
    @Query("SELECT o FROM Order o WHERE LOWER(o.orderNumber) LIKE LOWER(CONCAT('%', :orderNumber, '%'))")
    List<Order> findByOrderNumberContaining(@Param("orderNumber") String orderNumber);

    /**
     * Search orders by order number containing with pagination
     */
    @Query("SELECT o FROM Order o WHERE LOWER(o.orderNumber) LIKE LOWER(CONCAT('%', :orderNumber, '%'))")
    Page<Order> findByOrderNumberContaining(@Param("orderNumber") String orderNumber, Pageable pageable);

    /**
     * Count orders by company
     */
    long countByCompanyId(UUID companyId);

    /**
     * Count orders by status
     */
    long countByStatus(Order.OrderStatus status);

    /**
     * Count orders by order type
     */
    long countByOrderType(Order.OrderType orderType);

    /**
     * Count orders by company and status
     */
    long countByCompanyIdAndStatus(UUID companyId, Order.OrderStatus status);

    /**
     * Calculate total order value by company
     */
    @Query("SELECT COALESCE(SUM(o.totalCost), 0) FROM Order o WHERE o.company.id = :companyId AND o.status = 'COMPLETED'")
    BigDecimal calculateTotalOrderValueByCompany(@Param("companyId") UUID companyId);

    /**
     * Calculate total order value by date range
     */
    @Query("SELECT COALESCE(SUM(o.totalCost), 0) FROM Order o WHERE o.actualCompletionDate BETWEEN :startDate AND :endDate AND o.status = 'COMPLETED'")
    BigDecimal calculateTotalOrderValueByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * Find orders with workflow ID
     */
    List<Order> findByWorkflowId(String workflowId);

    /**
     * Find orders without workflow ID
     */
    @Query("SELECT o FROM Order o WHERE o.workflowId IS NULL")
    List<Order> findOrdersWithoutWorkflowId();

    /**
     * Find recent orders by company
     */
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.service LEFT JOIN FETCH o.user LEFT JOIN FETCH o.company LEFT JOIN FETCH o.serviceInstance WHERE o.company.id = :companyId ORDER BY o.createdAt DESC")
    List<Order> findRecentOrdersByCompany(@Param("companyId") UUID companyId, Pageable pageable);

    /**
     * Search orders with multiple criteria (simple query for pagination compatibility)
     */
    @Query("SELECT o FROM Order o WHERE " +
           "o.company.id = :companyId AND " +
           "(:status IS NULL OR o.status = :status)")
    Page<Order> searchOrders(@Param("companyId") UUID companyId,
                           @Param("status") Order.OrderStatus status,
                           Pageable pageable);

    /**
     * Check if order number exists
     */
    boolean existsByOrderNumber(String orderNumber);

    /**
     * Find next order number sequence
     */
    @Query("SELECT COALESCE(COUNT(o), 0) + 1 FROM Order o")
    Integer findNextOrderSequence();
}
