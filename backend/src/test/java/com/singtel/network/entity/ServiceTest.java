package com.singtel.network.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Service entity.
 */
class ServiceTest {

    private Service service;

    @BeforeEach
    void setUp() {
        service = new Service();
        service.setName("Business Fiber 500M");
        service.setServiceType("FIBER");
        service.setBaseBandwidthMbps(500);
        service.setMaxBandwidthMbps(1000);
        service.setMinBandwidthMbps(100);
        service.setBasePriceMonthly(new BigDecimal("299.00"));
        service.setPricePerMbps(new BigDecimal("0.50"));
        service.setSetupFee(new BigDecimal("150.00"));
        service.setContractTermMonths(24);
        service.setIsBandwidthAdjustable(true);
        service.setIsAvailable(true);
        service.setProvisioningTimeHours(72);
    }

    @Test
    void isAvailable_True() {
        // Act & Assert
        assertTrue(service.isAvailable());
    }

    @Test
    void isAvailable_False() {
        // Arrange
        service.setIsAvailable(false);

        // Act & Assert
        assertFalse(service.isAvailable());
    }

    @Test
    void isAvailable_Null() {
        // Arrange
        service.setIsAvailable(null);

        // Act & Assert
        assertFalse(service.isAvailable());
    }

    @Test
    void isBandwidthAdjustable_True() {
        // Act & Assert
        assertTrue(service.isBandwidthAdjustable());
    }

    @Test
    void isBandwidthAdjustable_False() {
        // Arrange
        service.setIsBandwidthAdjustable(false);

        // Act & Assert
        assertFalse(service.isBandwidthAdjustable());
    }

    @Test
    void isBandwidthAdjustable_Null() {
        // Arrange
        service.setIsBandwidthAdjustable(null);

        // Act & Assert
        assertFalse(service.isBandwidthAdjustable());
    }

    @Test
    void calculateMonthlyCost_BaseBandwidth() {
        // Act
        BigDecimal cost = service.calculateMonthlyCost(500);

        // Assert
        assertEquals(new BigDecimal("299.00"), cost);
    }

    @Test
    void calculateMonthlyCost_HigherBandwidth() {
        // Act
        BigDecimal cost = service.calculateMonthlyCost(750);

        // Assert
        // Expected: 299.00 + (750-500) * 0.50 = 299.00 + 125.00 = 424.00
        assertEquals(new BigDecimal("424.00"), cost);
    }

    @Test
    void calculateMonthlyCost_LowerBandwidth() {
        // Act
        BigDecimal cost = service.calculateMonthlyCost(300);

        // Assert
        // Should return base price even if bandwidth is lower
        assertEquals(new BigDecimal("299.00"), cost);
    }

    @Test
    void calculateMonthlyCost_NullBandwidth() {
        // Act
        BigDecimal cost = service.calculateMonthlyCost(null);

        // Assert
        assertEquals(new BigDecimal("299.00"), cost);
    }

    @Test
    void calculateMonthlyCost_NullBasePrice() {
        // Arrange
        service.setBasePriceMonthly(null);

        // Act
        BigDecimal cost = service.calculateMonthlyCost(500);

        // Assert
        assertNull(cost);
    }

    @Test
    void isValidBandwidth_Valid() {
        // Act & Assert
        assertTrue(service.isValidBandwidth(500));
        assertTrue(service.isValidBandwidth(100));
        assertTrue(service.isValidBandwidth(1000));
        assertTrue(service.isValidBandwidth(750));
    }

    @Test
    void isValidBandwidth_TooLow() {
        // Act & Assert
        assertFalse(service.isValidBandwidth(50));
    }

    @Test
    void isValidBandwidth_TooHigh() {
        // Act & Assert
        assertFalse(service.isValidBandwidth(1500));
    }

    @Test
    void isValidBandwidth_Null() {
        // Act & Assert
        assertFalse(service.isValidBandwidth(null));
    }

    @Test
    void isValidBandwidth_NoLimits() {
        // Arrange
        service.setMinBandwidthMbps(null);
        service.setMaxBandwidthMbps(null);

        // Act & Assert
        assertTrue(service.isValidBandwidth(500));
        assertTrue(service.isValidBandwidth(1));
        assertTrue(service.isValidBandwidth(10000));
    }

    @Test
    void constructor_WithParameters() {
        // Act
        Service newService = new Service("Test Service", "VPN", 100, new BigDecimal("199.00"));

        // Assert
        assertEquals("Test Service", newService.getName());
        assertEquals("VPN", newService.getServiceType());
        assertEquals(100, newService.getBaseBandwidthMbps());
        assertEquals(new BigDecimal("199.00"), newService.getBasePriceMonthly());
    }

    @Test
    void toString_ContainsExpectedFields() {
        // Arrange
        service.setId(java.util.UUID.randomUUID());

        // Act
        String result = service.toString();

        // Assert
        assertTrue(result.contains("Service{"));
        assertTrue(result.contains("name='Business Fiber 500M'"));
        assertTrue(result.contains("serviceType='FIBER'"));
        assertTrue(result.contains("baseBandwidthMbps=500"));
        assertTrue(result.contains("basePriceMonthly=299.00"));
        assertTrue(result.contains("isAvailable=true"));
    }
}
