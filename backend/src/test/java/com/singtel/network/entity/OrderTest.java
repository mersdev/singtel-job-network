package com.singtel.network.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Order entity.
 */
class OrderTest {

    private Order order;

    @BeforeEach
    void setUp() {
        order = new Order();
        order.setOrderNumber("ORD-000001");
        order.setOrderType(Order.OrderType.NEW_SERVICE);
        order.setStatus(Order.OrderStatus.SUBMITTED);
        order.setRequestedBandwidthMbps(500);
        order.setTotalCost(new BigDecimal("449.00"));
        order.setRequestedDate(LocalDate.now().plusDays(1));
    }

    @Test
    void isCompleted_True() {
        // Arrange
        order.setStatus(Order.OrderStatus.COMPLETED);

        // Act & Assert
        assertTrue(order.isCompleted());
    }

    @Test
    void isCompleted_False() {
        // Act & Assert
        assertFalse(order.isCompleted());
    }

    @Test
    void canCancel_Submitted() {
        // Arrange
        order.setStatus(Order.OrderStatus.SUBMITTED);

        // Act & Assert
        assertTrue(order.canCancel());
    }

    @Test
    void canCancel_Approved() {
        // Arrange
        order.setStatus(Order.OrderStatus.APPROVED);

        // Act & Assert
        assertTrue(order.canCancel());
    }

    @Test
    void canCancel_InProgress() {
        // Arrange
        order.setStatus(Order.OrderStatus.IN_PROGRESS);

        // Act & Assert
        assertFalse(order.canCancel());
    }

    @Test
    void canCancel_Completed() {
        // Arrange
        order.setStatus(Order.OrderStatus.COMPLETED);

        // Act & Assert
        assertFalse(order.canCancel());
    }

    @Test
    void approve_FromSubmitted() {
        // Arrange
        order.setStatus(Order.OrderStatus.SUBMITTED);

        // Act
        order.approve();

        // Assert
        assertEquals(Order.OrderStatus.APPROVED, order.getStatus());
    }

    @Test
    void approve_FromOtherStatus() {
        // Arrange
        order.setStatus(Order.OrderStatus.IN_PROGRESS);

        // Act
        order.approve();

        // Assert
        assertEquals(Order.OrderStatus.IN_PROGRESS, order.getStatus()); // Should not change
    }

    @Test
    void startProcessing_FromApproved() {
        // Arrange
        order.setStatus(Order.OrderStatus.APPROVED);

        // Act
        order.startProcessing();

        // Assert
        assertEquals(Order.OrderStatus.IN_PROGRESS, order.getStatus());
    }

    @Test
    void startProcessing_FromOtherStatus() {
        // Arrange
        order.setStatus(Order.OrderStatus.SUBMITTED);

        // Act
        order.startProcessing();

        // Assert
        assertEquals(Order.OrderStatus.SUBMITTED, order.getStatus()); // Should not change
    }

    @Test
    void complete_FromInProgress() {
        // Arrange
        order.setStatus(Order.OrderStatus.IN_PROGRESS);

        // Act
        order.complete();

        // Assert
        assertEquals(Order.OrderStatus.COMPLETED, order.getStatus());
        assertEquals(LocalDate.now(), order.getActualCompletionDate());
    }

    @Test
    void complete_FromOtherStatus() {
        // Arrange
        order.setStatus(Order.OrderStatus.SUBMITTED);

        // Act
        order.complete();

        // Assert
        assertEquals(Order.OrderStatus.SUBMITTED, order.getStatus()); // Should not change
        assertNull(order.getActualCompletionDate());
    }

    @Test
    void cancel_FromSubmitted() {
        // Arrange
        order.setStatus(Order.OrderStatus.SUBMITTED);

        // Act
        order.cancel();

        // Assert
        assertEquals(Order.OrderStatus.CANCELLED, order.getStatus());
    }

    @Test
    void cancel_FromApproved() {
        // Arrange
        order.setStatus(Order.OrderStatus.APPROVED);

        // Act
        order.cancel();

        // Assert
        assertEquals(Order.OrderStatus.CANCELLED, order.getStatus());
    }

    @Test
    void cancel_FromInProgress() {
        // Arrange
        order.setStatus(Order.OrderStatus.IN_PROGRESS);

        // Act
        order.cancel();

        // Assert
        assertEquals(Order.OrderStatus.IN_PROGRESS, order.getStatus()); // Should not change
    }

    @Test
    void fail_WithReason() {
        // Arrange
        String reason = "Service not available";

        // Act
        order.fail(reason);

        // Assert
        assertEquals(Order.OrderStatus.FAILED, order.getStatus());
        assertTrue(order.getNotes().contains("Failed: " + reason));
    }

    @Test
    void fail_WithExistingNotes() {
        // Arrange
        order.setNotes("Existing notes");
        String reason = "Service not available";

        // Act
        order.fail(reason);

        // Assert
        assertEquals(Order.OrderStatus.FAILED, order.getStatus());
        assertTrue(order.getNotes().contains("Existing notes"));
        assertTrue(order.getNotes().contains("Failed: " + reason));
    }

    @Test
    void constructor_WithParameters() {
        // Arrange
        Company company = new Company();
        User user = new User();
        Service service = new Service();

        // Act
        Order newOrder = new Order(company, user, service, Order.OrderType.NEW_SERVICE, "ORD-000002");

        // Assert
        assertEquals(company, newOrder.getCompany());
        assertEquals(user, newOrder.getUser());
        assertEquals(service, newOrder.getService());
        assertEquals(Order.OrderType.NEW_SERVICE, newOrder.getOrderType());
        assertEquals("ORD-000002", newOrder.getOrderNumber());
    }

    @Test
    void toString_ContainsExpectedFields() {
        // Arrange
        order.setId(java.util.UUID.randomUUID());

        // Act
        String result = order.toString();

        // Assert
        assertTrue(result.contains("Order{"));
        assertTrue(result.contains("orderNumber='ORD-000001'"));
        assertTrue(result.contains("orderType=NEW_SERVICE"));
        assertTrue(result.contains("status=SUBMITTED"));
        assertTrue(result.contains("totalCost=449.00"));
    }
}
