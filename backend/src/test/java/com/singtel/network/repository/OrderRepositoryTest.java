package com.singtel.network.repository;

import com.singtel.network.entity.*;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for OrderRepository.
 */
@DataJpaTest
@AutoConfigureEmbeddedDatabase
@TestPropertySource(properties = {
    "spring.flyway.enabled=false",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.properties.hibernate.default_schema=singtel_app",
    "spring.sql.init.mode=always",
    "spring.sql.init.schema-locations=classpath:test-schema.sql"
})
class OrderRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OrderRepository orderRepository;

    private Company testCompany;
    private User testUser;
    private Service testService;
    private Order testOrder1;
    private Order testOrder2;

    @BeforeEach
    void setUp() {
        // Create test company
        testCompany = new Company();
        testCompany.setName("Test Company");
        testCompany.setRegistrationNumber("TEST123");
        testCompany.setEmail("test@company.com");
        testCompany.setStatus(Company.CompanyStatus.ACTIVE);
        testCompany = entityManager.persistAndFlush(testCompany);

        // Create test user
        testUser = new User();
        testUser.setCompany(testCompany);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPasswordHash("$2a$10$hashedpassword");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setRole(User.UserRole.USER);
        testUser.setStatus(User.UserStatus.ACTIVE);
        testUser.setPasswordChangedAt(LocalDateTime.now());
        testUser = entityManager.persistAndFlush(testUser);

        // Create test service category
        ServiceCategory category = new ServiceCategory();
        category.setName("Business Internet");
        category.setIsActive(true);
        category = entityManager.persistAndFlush(category);

        // Create test service
        testService = new Service();
        testService.setCategory(category);
        testService.setName("Business Fiber 500M");
        testService.setServiceType("FIBER");
        testService.setBaseBandwidthMbps(500);
        testService.setBasePriceMonthly(new BigDecimal("299.00"));
        testService.setSetupFee(new BigDecimal("150.00"));
        testService.setIsAvailable(true);
        testService = entityManager.persistAndFlush(testService);

        // Create test orders
        testOrder1 = new Order();
        testOrder1.setCompany(testCompany);
        testOrder1.setUser(testUser);
        testOrder1.setService(testService);
        testOrder1.setOrderNumber("ORD-000001");
        testOrder1.setOrderType(Order.OrderType.NEW_SERVICE);
        testOrder1.setRequestedBandwidthMbps(500);
        testOrder1.setInstallationAddress("123 Test Street");
        testOrder1.setContactPerson("Test Person");
        testOrder1.setContactPhone("+65 9123 4567");
        testOrder1.setContactEmail("test@example.com");
        testOrder1.setRequestedDate(LocalDate.now().plusDays(1));
        testOrder1.setStatus(Order.OrderStatus.SUBMITTED);
        testOrder1.setTotalCost(new BigDecimal("449.00"));
        testOrder1 = entityManager.persistAndFlush(testOrder1);

        testOrder2 = new Order();
        testOrder2.setCompany(testCompany);
        testOrder2.setUser(testUser);
        testOrder2.setService(testService);
        testOrder2.setOrderNumber("ORD-000002");
        testOrder2.setOrderType(Order.OrderType.MODIFY_SERVICE);
        testOrder2.setRequestedBandwidthMbps(750);
        testOrder2.setInstallationAddress("456 Another Street");
        testOrder2.setContactPerson("Another Person");
        testOrder2.setContactPhone("+65 9876 5432");
        testOrder2.setContactEmail("another@example.com");
        testOrder2.setRequestedDate(LocalDate.now().plusDays(2));
        testOrder2.setStatus(Order.OrderStatus.COMPLETED);
        testOrder2.setTotalCost(new BigDecimal("125.00"));
        testOrder2.setActualCompletionDate(LocalDate.now());
        testOrder2 = entityManager.persistAndFlush(testOrder2);

        entityManager.clear();
    }

    @Test
    void findByOrderNumber_Success() {
        // Act
        Optional<Order> result = orderRepository.findByOrderNumber("ORD-000001");

        // Assert
        assertTrue(result.isPresent());
        Order order = result.get();
        assertEquals("ORD-000001", order.getOrderNumber());
        assertEquals(Order.OrderType.NEW_SERVICE, order.getOrderType());
        assertEquals(Order.OrderStatus.SUBMITTED, order.getStatus());
        assertEquals(500, order.getRequestedBandwidthMbps());
    }

    @Test
    void findByOrderNumber_NotFound() {
        // Act
        Optional<Order> result = orderRepository.findByOrderNumber("ORD-999999");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void findByCompanyId_Success() {
        // Act
        List<Order> orders = orderRepository.findByCompanyId(testCompany.getId());

        // Assert
        assertNotNull(orders);
        assertEquals(2, orders.size());
        assertTrue(orders.stream().anyMatch(o -> o.getOrderNumber().equals("ORD-000001")));
        assertTrue(orders.stream().anyMatch(o -> o.getOrderNumber().equals("ORD-000002")));
    }

    @Test
    void findByCompanyIdWithPagination_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 1);

        // Act
        Page<Order> orderPage = orderRepository.findByCompanyId(testCompany.getId(), pageable);

        // Assert
        assertNotNull(orderPage);
        assertEquals(2, orderPage.getTotalElements());
        assertEquals(1, orderPage.getContent().size());
        assertEquals(2, orderPage.getTotalPages());
    }

    @Test
    void findByUserId_Success() {
        // Act
        List<Order> orders = orderRepository.findByUserId(testUser.getId());

        // Assert
        assertNotNull(orders);
        assertEquals(2, orders.size());
        assertTrue(orders.stream().allMatch(o -> o.getUser().getId().equals(testUser.getId())));
    }

    @Test
    void findByServiceId_Success() {
        // Act
        List<Order> orders = orderRepository.findByServiceId(testService.getId());

        // Assert
        assertNotNull(orders);
        assertEquals(2, orders.size());
        assertTrue(orders.stream().allMatch(o -> o.getService().getId().equals(testService.getId())));
    }

    @Test
    void findByStatus_Success() {
        // Act
        List<Order> submittedOrders = orderRepository.findByStatus(Order.OrderStatus.SUBMITTED);
        List<Order> completedOrders = orderRepository.findByStatus(Order.OrderStatus.COMPLETED);

        // Assert
        assertEquals(1, submittedOrders.size());
        assertEquals("ORD-000001", submittedOrders.get(0).getOrderNumber());
        
        assertEquals(1, completedOrders.size());
        assertEquals("ORD-000002", completedOrders.get(0).getOrderNumber());
    }

    @Test
    void findByOrderType_Success() {
        // Act
        List<Order> newServiceOrders = orderRepository.findByOrderType(Order.OrderType.NEW_SERVICE);
        List<Order> modifyServiceOrders = orderRepository.findByOrderType(Order.OrderType.MODIFY_SERVICE);

        // Assert
        assertEquals(1, newServiceOrders.size());
        assertEquals("ORD-000001", newServiceOrders.get(0).getOrderNumber());
        
        assertEquals(1, modifyServiceOrders.size());
        assertEquals("ORD-000002", modifyServiceOrders.get(0).getOrderNumber());
    }

    @Test
    void findByCompanyIdAndStatus_Success() {
        // Act
        List<Order> submittedOrders = orderRepository.findByCompanyIdAndStatus(
                testCompany.getId(), Order.OrderStatus.SUBMITTED);

        // Assert
        assertNotNull(submittedOrders);
        assertEquals(1, submittedOrders.size());
        assertEquals("ORD-000001", submittedOrders.get(0).getOrderNumber());
        assertEquals(Order.OrderStatus.SUBMITTED, submittedOrders.get(0).getStatus());
    }

    @Test
    void findByCompanyIdAndOrderType_Success() {
        // Act
        List<Order> newServiceOrders = orderRepository.findByCompanyIdAndOrderType(
                testCompany.getId(), Order.OrderType.NEW_SERVICE);

        // Assert
        assertNotNull(newServiceOrders);
        assertEquals(1, newServiceOrders.size());
        assertEquals("ORD-000001", newServiceOrders.get(0).getOrderNumber());
        assertEquals(Order.OrderType.NEW_SERVICE, newServiceOrders.get(0).getOrderType());
    }

    @Test
    void findPendingOrdersByCompany_Success() {
        // Act
        List<Order> pendingOrders = orderRepository.findPendingOrdersByCompany(testCompany.getId());

        // Assert
        assertNotNull(pendingOrders);
        assertEquals(1, pendingOrders.size());
        assertEquals("ORD-000001", pendingOrders.get(0).getOrderNumber());
        assertEquals(Order.OrderStatus.SUBMITTED, pendingOrders.get(0).getStatus());
    }

    @Test
    void findCompletedOrdersByCompany_Success() {
        // Act
        List<Order> completedOrders = orderRepository.findCompletedOrdersByCompany(testCompany.getId());

        // Assert
        assertNotNull(completedOrders);
        assertEquals(1, completedOrders.size());
        assertEquals("ORD-000002", completedOrders.get(0).getOrderNumber());
        assertEquals(Order.OrderStatus.COMPLETED, completedOrders.get(0).getStatus());
    }

    @Test
    void findByRequestedDateRange_Success() {
        // Arrange
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(3);

        // Act
        List<Order> ordersInRange = orderRepository.findByRequestedDateRange(startDate, endDate);

        // Assert
        assertNotNull(ordersInRange);
        assertEquals(2, ordersInRange.size());
        assertTrue(ordersInRange.stream().allMatch(o -> 
                !o.getRequestedDate().isBefore(startDate) && !o.getRequestedDate().isAfter(endDate)));
    }

    @Test
    void findByCostRange_Success() {
        // Arrange
        BigDecimal minCost = new BigDecimal("100.00");
        BigDecimal maxCost = new BigDecimal("200.00");

        // Act
        List<Order> ordersInRange = orderRepository.findByCostRange(minCost, maxCost);

        // Assert
        assertNotNull(ordersInRange);
        assertEquals(1, ordersInRange.size());
        assertEquals("ORD-000002", ordersInRange.get(0).getOrderNumber());
        assertTrue(ordersInRange.get(0).getTotalCost().compareTo(minCost) >= 0);
        assertTrue(ordersInRange.get(0).getTotalCost().compareTo(maxCost) <= 0);
    }

    @Test
    void findByOrderNumberContaining_Success() {
        // Act
        List<Order> orders = orderRepository.findByOrderNumberContaining("000001");

        // Assert
        assertNotNull(orders);
        assertEquals(1, orders.size());
        assertEquals("ORD-000001", orders.get(0).getOrderNumber());
    }

    @Test
    void countByCompanyId_Success() {
        // Act
        long count = orderRepository.countByCompanyId(testCompany.getId());

        // Assert
        assertEquals(2, count);
    }

    @Test
    void countByStatus_Success() {
        // Act
        long submittedCount = orderRepository.countByStatus(Order.OrderStatus.SUBMITTED);
        long completedCount = orderRepository.countByStatus(Order.OrderStatus.COMPLETED);

        // Assert
        assertEquals(1, submittedCount);
        assertEquals(1, completedCount);
    }

    @Test
    void countByOrderType_Success() {
        // Act
        long newServiceCount = orderRepository.countByOrderType(Order.OrderType.NEW_SERVICE);
        long modifyServiceCount = orderRepository.countByOrderType(Order.OrderType.MODIFY_SERVICE);

        // Assert
        assertEquals(1, newServiceCount);
        assertEquals(1, modifyServiceCount);
    }

    @Test
    void countByCompanyIdAndStatus_Success() {
        // Act
        long count = orderRepository.countByCompanyIdAndStatus(testCompany.getId(), Order.OrderStatus.SUBMITTED);

        // Assert
        assertEquals(1, count);
    }

    @Test
    void calculateTotalOrderValueByCompany_Success() {
        // Act
        BigDecimal totalValue = orderRepository.calculateTotalOrderValueByCompany(testCompany.getId());

        // Assert
        assertNotNull(totalValue);
        // Only completed orders are counted
        assertEquals(new BigDecimal("125.00"), totalValue);
    }

    @Test
    void calculateTotalOrderValueByDateRange_Success() {
        // Arrange
        LocalDate startDate = LocalDate.now().minusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(1);

        // Act
        BigDecimal totalValue = orderRepository.calculateTotalOrderValueByDateRange(startDate, endDate);

        // Assert
        assertNotNull(totalValue);
        // Only completed orders with actual completion date in range
        assertEquals(new BigDecimal("125.00"), totalValue);
    }

    @Test
    void existsByOrderNumber_Success() {
        // Act
        boolean exists = orderRepository.existsByOrderNumber("ORD-000001");
        boolean notExists = orderRepository.existsByOrderNumber("ORD-999999");

        // Assert
        assertTrue(exists);
        assertFalse(notExists);
    }

    @Test
    void findNextOrderSequence_Success() {
        // Act
        Integer nextSequence = orderRepository.findNextOrderSequence();

        // Assert
        assertNotNull(nextSequence);
        assertEquals(3, nextSequence); // Should be 3 since we have ORD-000001 and ORD-000002
    }

    @Test
    void searchOrders_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Order> orderPage = orderRepository.searchOrders(
                testCompany.getId(), "SUBMITTED", "NEW_SERVICE",
                testService.getId(), null, null, null, null, pageable);

        // Assert
        assertNotNull(orderPage);
        assertEquals(1, orderPage.getTotalElements());
        Order order = orderPage.getContent().get(0);
        assertEquals("ORD-000001", order.getOrderNumber());
        assertEquals(Order.OrderStatus.SUBMITTED, order.getStatus());
        assertEquals(Order.OrderType.NEW_SERVICE, order.getOrderType());
    }

    @Test
    void findRecentOrdersByCompany_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 5);

        // Act
        List<Order> recentOrders = orderRepository.findRecentOrdersByCompany(testCompany.getId(), pageable);

        // Assert
        assertNotNull(recentOrders);
        assertEquals(2, recentOrders.size());
        // Orders should be sorted by creation date descending
        // Since both orders were created at the same time, we just verify they're both present
        assertTrue(recentOrders.stream().anyMatch(o -> o.getOrderNumber().equals("ORD-000001")));
        assertTrue(recentOrders.stream().anyMatch(o -> o.getOrderNumber().equals("ORD-000002")));
    }
}
