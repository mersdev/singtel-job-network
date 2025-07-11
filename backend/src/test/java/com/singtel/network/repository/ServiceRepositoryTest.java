package com.singtel.network.repository;

import com.singtel.network.entity.Service;
import com.singtel.network.entity.ServiceCategory;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for ServiceRepository.
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
class ServiceRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ServiceRepository serviceRepository;

    private ServiceCategory testCategory;
    private Service testService1;
    private Service testService2;

    @BeforeEach
    void setUp() {
        // Create test category
        testCategory = new ServiceCategory();
        testCategory.setName("Business Internet");
        testCategory.setDescription("High-speed internet connectivity");
        testCategory.setDisplayOrder(1);
        testCategory.setIsActive(true);
        testCategory = entityManager.persistAndFlush(testCategory);

        // Create test services
        testService1 = new Service();
        testService1.setCategory(testCategory);
        testService1.setName("Business Fiber 500M");
        testService1.setDescription("High-speed fiber internet");
        testService1.setServiceType("FIBER");
        testService1.setBaseBandwidthMbps(500);
        testService1.setMaxBandwidthMbps(1000);
        testService1.setMinBandwidthMbps(100);
        testService1.setBasePriceMonthly(new BigDecimal("299.00"));
        testService1.setPricePerMbps(new BigDecimal("0.50"));
        testService1.setSetupFee(new BigDecimal("150.00"));
        testService1.setContractTermMonths(24);
        testService1.setIsBandwidthAdjustable(true);
        testService1.setIsAvailable(true);
        testService1.setProvisioningTimeHours(72);
        testService1 = entityManager.persistAndFlush(testService1);

        testService2 = new Service();
        testService2.setCategory(testCategory);
        testService2.setName("Business VPN Premium");
        testService2.setDescription("Secure VPN connectivity");
        testService2.setServiceType("VPN");
        testService2.setBaseBandwidthMbps(100);
        testService2.setMaxBandwidthMbps(500);
        testService2.setMinBandwidthMbps(50);
        testService2.setBasePriceMonthly(new BigDecimal("199.00"));
        testService2.setPricePerMbps(new BigDecimal("1.00"));
        testService2.setSetupFee(new BigDecimal("100.00"));
        testService2.setContractTermMonths(12);
        testService2.setIsBandwidthAdjustable(false);
        testService2.setIsAvailable(true);
        testService2.setProvisioningTimeHours(48);
        testService2 = entityManager.persistAndFlush(testService2);

        entityManager.clear();
    }

    @Test
    void findByCategoryId_Success() {
        // Act
        List<Service> services = serviceRepository.findByCategoryId(testCategory.getId());

        // Assert
        assertNotNull(services);
        assertEquals(2, services.size());
        assertTrue(services.stream().anyMatch(s -> s.getName().equals("Business Fiber 500M")));
        assertTrue(services.stream().anyMatch(s -> s.getName().equals("Business VPN Premium")));
    }

    @Test
    void findByCategoryIdWithPagination_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 1);

        // Act
        Page<Service> servicePage = serviceRepository.findByCategoryId(testCategory.getId(), pageable);

        // Assert
        assertNotNull(servicePage);
        assertEquals(2, servicePage.getTotalElements());
        assertEquals(1, servicePage.getContent().size());
        assertEquals(2, servicePage.getTotalPages());
    }

    @Test
    void findByServiceType_Success() {
        // Act
        List<Service> fiberServices = serviceRepository.findByServiceType("FIBER");

        // Assert
        assertNotNull(fiberServices);
        assertEquals(1, fiberServices.size());
        assertEquals("Business Fiber 500M", fiberServices.get(0).getName());
        assertEquals("FIBER", fiberServices.get(0).getServiceType());
    }

    @Test
    void findAvailableServices_Success() {
        // Act
        List<Service> availableServices = serviceRepository.findAvailableServices();

        // Assert
        assertNotNull(availableServices);
        assertEquals(2, availableServices.size());
        assertTrue(availableServices.stream().allMatch(Service::isAvailable));
    }

    @Test
    void findAvailableServicesWithPagination_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 1);

        // Act
        Page<Service> servicePage = serviceRepository.findAvailableServices(pageable);

        // Assert
        assertNotNull(servicePage);
        assertEquals(2, servicePage.getTotalElements());
        assertEquals(1, servicePage.getContent().size());
        assertTrue(servicePage.getContent().get(0).isAvailable());
    }

    @Test
    void findByIsAvailable_Success() {
        // Act
        List<Service> availableServices = serviceRepository.findByIsAvailable(true);

        // Assert
        assertNotNull(availableServices);
        assertEquals(2, availableServices.size());
        assertTrue(availableServices.stream().allMatch(s -> s.getIsAvailable()));
    }

    @Test
    void findBandwidthAdjustableServices_Success() {
        // Act
        List<Service> adjustableServices = serviceRepository.findBandwidthAdjustableServices();

        // Assert
        assertNotNull(adjustableServices);
        assertEquals(1, adjustableServices.size());
        assertEquals("Business Fiber 500M", adjustableServices.get(0).getName());
        assertTrue(adjustableServices.get(0).isBandwidthAdjustable());
    }

    @Test
    void findByPriceRange_Success() {
        // Arrange
        BigDecimal minPrice = new BigDecimal("150.00");
        BigDecimal maxPrice = new BigDecimal("250.00");

        // Act
        List<Service> servicesInRange = serviceRepository.findByPriceRange(minPrice, maxPrice);

        // Assert
        assertNotNull(servicesInRange);
        assertEquals(1, servicesInRange.size());
        assertEquals("Business VPN Premium", servicesInRange.get(0).getName());
        assertTrue(servicesInRange.get(0).getBasePriceMonthly().compareTo(minPrice) >= 0);
        assertTrue(servicesInRange.get(0).getBasePriceMonthly().compareTo(maxPrice) <= 0);
    }

    @Test
    void findByBandwidthRange_Success() {
        // Arrange
        Integer minBandwidth = 400;
        Integer maxBandwidth = 600;

        // Act
        List<Service> servicesInRange = serviceRepository.findByBandwidthRange(minBandwidth, maxBandwidth);

        // Assert
        assertNotNull(servicesInRange);
        assertEquals(1, servicesInRange.size());
        assertEquals("Business Fiber 500M", servicesInRange.get(0).getName());
        assertTrue(servicesInRange.get(0).getBaseBandwidthMbps() >= minBandwidth);
        assertTrue(servicesInRange.get(0).getBaseBandwidthMbps() <= maxBandwidth);
    }

    @Test
    void findByNameContainingIgnoreCase_Success() {
        // Act
        List<Service> services = serviceRepository.findByNameContainingIgnoreCase("fiber");

        // Assert
        assertNotNull(services);
        assertEquals(1, services.size());
        assertEquals("Business Fiber 500M", services.get(0).getName());
    }

    @Test
    void findByNameContainingIgnoreCaseWithPagination_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Service> servicePage = serviceRepository.findByNameContainingIgnoreCase("business", pageable);

        // Assert
        assertNotNull(servicePage);
        assertEquals(2, servicePage.getTotalElements());
        assertTrue(servicePage.getContent().stream()
                .allMatch(s -> s.getName().toLowerCase().contains("business")));
    }

    @Test
    void findByCategoryAndType_Success() {
        // Act
        List<Service> services = serviceRepository.findByCategoryAndType(testCategory.getId(), "FIBER");

        // Assert
        assertNotNull(services);
        assertEquals(1, services.size());
        assertEquals("Business Fiber 500M", services.get(0).getName());
        assertEquals("FIBER", services.get(0).getServiceType());
    }

    @Test
    void searchServices_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Service> servicePage = serviceRepository.searchServices(
                testCategory.getId(), "FIBER", new BigDecimal("200.00"), new BigDecimal("400.00"),
                400, 600, true, pageable);

        // Assert
        assertNotNull(servicePage);
        assertEquals(1, servicePage.getTotalElements());
        Service service = servicePage.getContent().get(0);
        assertEquals("Business Fiber 500M", service.getName());
        assertEquals("FIBER", service.getServiceType());
        assertTrue(service.isBandwidthAdjustable());
    }

    @Test
    void findDistinctServiceTypes_Success() {
        // Act
        List<String> serviceTypes = serviceRepository.findDistinctServiceTypes();

        // Assert
        assertNotNull(serviceTypes);
        assertEquals(2, serviceTypes.size());
        assertTrue(serviceTypes.contains("FIBER"));
        assertTrue(serviceTypes.contains("VPN"));
    }

    @Test
    void countByCategoryId_Success() {
        // Act
        long count = serviceRepository.countByCategoryId(testCategory.getId());

        // Assert
        assertEquals(2, count);
    }

    @Test
    void countAvailableServices_Success() {
        // Act
        long count = serviceRepository.countAvailableServices();

        // Assert
        assertEquals(2, count);
    }

    @Test
    void countByServiceType_Success() {
        // Act
        long fiberCount = serviceRepository.countByServiceType("FIBER");
        long vpnCount = serviceRepository.countByServiceType("VPN");

        // Assert
        assertEquals(1, fiberCount);
        assertEquals(1, vpnCount);
    }

    @Test
    void findByContractTermMonths_Success() {
        // Act
        List<Service> services24Months = serviceRepository.findByContractTermMonths(24);
        List<Service> services12Months = serviceRepository.findByContractTermMonths(12);

        // Assert
        assertEquals(1, services24Months.size());
        assertEquals("Business Fiber 500M", services24Months.get(0).getName());
        
        assertEquals(1, services12Months.size());
        assertEquals("Business VPN Premium", services12Months.get(0).getName());
    }

    @Test
    void findServicesWithSetupFee_Success() {
        // Act
        List<Service> servicesWithSetupFee = serviceRepository.findServicesWithSetupFee();

        // Assert
        assertNotNull(servicesWithSetupFee);
        assertEquals(2, servicesWithSetupFee.size());
        assertTrue(servicesWithSetupFee.stream()
                .allMatch(s -> s.getSetupFee() != null && s.getSetupFee().compareTo(BigDecimal.ZERO) > 0));
    }

    @Test
    void findServicesWithoutSetupFee_Success() {
        // Arrange - Create a service without setup fee
        Service noSetupFeeService = new Service();
        noSetupFeeService.setCategory(testCategory);
        noSetupFeeService.setName("Free Setup Service");
        noSetupFeeService.setServiceType("BASIC");
        noSetupFeeService.setBaseBandwidthMbps(100);
        noSetupFeeService.setBasePriceMonthly(new BigDecimal("99.00"));
        noSetupFeeService.setSetupFee(BigDecimal.ZERO);
        noSetupFeeService.setIsAvailable(true);
        entityManager.persistAndFlush(noSetupFeeService);

        // Act
        List<Service> servicesWithoutSetupFee = serviceRepository.findServicesWithoutSetupFee();

        // Assert
        assertNotNull(servicesWithoutSetupFee);
        assertEquals(1, servicesWithoutSetupFee.size());
        assertEquals("Free Setup Service", servicesWithoutSetupFee.get(0).getName());
        assertTrue(servicesWithoutSetupFee.get(0).getSetupFee() == null || 
                  servicesWithoutSetupFee.get(0).getSetupFee().equals(BigDecimal.ZERO));
    }
}
