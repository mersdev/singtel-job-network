package com.singtel.network.service;

import com.singtel.network.dto.service.ServiceCategoryResponse;
import com.singtel.network.dto.service.ServiceDetailResponse;
import com.singtel.network.dto.service.ServiceSummaryResponse;
import com.singtel.network.entity.Service;
import com.singtel.network.entity.ServiceCategory;
import com.singtel.network.repository.ServiceCategoryRepository;
import com.singtel.network.repository.ServiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ServiceCatalogService.
 */
@ExtendWith(MockitoExtension.class)
class ServiceCatalogServiceTest {

    @Mock
    private ServiceRepository serviceRepository;

    @Mock
    private ServiceCategoryRepository serviceCategoryRepository;

    @InjectMocks
    private ServiceCatalogService serviceCatalogService;

    private ServiceCategory testCategory;
    private Service testService;
    private UUID categoryId;
    private UUID serviceId;

    @BeforeEach
    void setUp() {
        categoryId = UUID.randomUUID();
        serviceId = UUID.randomUUID();

        // Create test category
        testCategory = new ServiceCategory();
        testCategory.setId(categoryId);
        testCategory.setName("Business Internet");
        testCategory.setDescription("High-speed internet connectivity");
        testCategory.setDisplayOrder(1);
        testCategory.setIsActive(true);

        // Create test service
        testService = new Service();
        testService.setId(serviceId);
        testService.setCategory(testCategory);
        testService.setName("Business Fiber 500M");
        testService.setDescription("High-speed fiber internet");
        testService.setServiceType("FIBER");
        testService.setBaseBandwidthMbps(500);
        testService.setMaxBandwidthMbps(1000);
        testService.setMinBandwidthMbps(100);
        testService.setBasePriceMonthly(new BigDecimal("299.00"));
        testService.setPricePerMbps(new BigDecimal("0.50"));
        testService.setSetupFee(new BigDecimal("150.00"));
        testService.setContractTermMonths(24);
        testService.setIsBandwidthAdjustable(true);
        testService.setIsAvailable(true);
        testService.setProvisioningTimeHours(72);
    }

    @Test
    void getAllActiveCategories_Success() {
        // Arrange
        List<ServiceCategory> categories = Arrays.asList(testCategory);
        when(serviceCategoryRepository.findActiveCategories()).thenReturn(categories);

        // Act
        List<ServiceCategoryResponse> result = serviceCatalogService.getAllActiveCategories();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        ServiceCategoryResponse response = result.get(0);
        assertEquals(testCategory.getId(), response.getId());
        assertEquals(testCategory.getName(), response.getName());
        assertEquals(testCategory.getDescription(), response.getDescription());
        assertEquals(testCategory.getDisplayOrder(), response.getDisplayOrder());
        assertEquals(testCategory.getIsActive(), response.getIsActive());

        verify(serviceCategoryRepository).findActiveCategories();
    }

    @Test
    void getAllActiveCategoriesWithPagination_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<ServiceCategory> categoryPage = new PageImpl<>(Arrays.asList(testCategory));
        when(serviceCategoryRepository.findActiveCategories(pageable)).thenReturn(categoryPage);

        // Act
        Page<ServiceCategoryResponse> result = serviceCatalogService.getAllActiveCategories(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        ServiceCategoryResponse response = result.getContent().get(0);
        assertEquals(testCategory.getId(), response.getId());
        assertEquals(testCategory.getName(), response.getName());

        verify(serviceCategoryRepository).findActiveCategories(pageable);
    }

    @Test
    void getCategoryById_Success() {
        // Arrange
        when(serviceCategoryRepository.findById(categoryId)).thenReturn(Optional.of(testCategory));

        // Act
        ServiceCategoryResponse result = serviceCatalogService.getCategoryById(categoryId);

        // Assert
        assertNotNull(result);
        assertEquals(testCategory.getId(), result.getId());
        assertEquals(testCategory.getName(), result.getName());
        assertEquals(testCategory.getDescription(), result.getDescription());

        verify(serviceCategoryRepository).findById(categoryId);
    }

    @Test
    void getCategoryById_NotFound() {
        // Arrange
        when(serviceCategoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            serviceCatalogService.getCategoryById(categoryId);
        });

        assertEquals("Service category not found with ID: " + categoryId, exception.getMessage());
        verify(serviceCategoryRepository).findById(categoryId);
    }

    @Test
    void getAllAvailableServices_Success() {
        // Arrange
        List<Service> services = Arrays.asList(testService);
        when(serviceRepository.findAvailableServices()).thenReturn(services);

        // Act
        List<ServiceSummaryResponse> result = serviceCatalogService.getAllAvailableServices();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        ServiceSummaryResponse response = result.get(0);
        assertEquals(testService.getId(), response.getId());
        assertEquals(testService.getName(), response.getName());
        assertEquals(testService.getServiceType(), response.getServiceType());
        assertEquals(testService.getBaseBandwidthMbps(), response.getBaseBandwidthMbps());
        assertEquals(testService.getBasePriceMonthly(), response.getBasePriceMonthly());

        verify(serviceRepository).findAvailableServices();
    }

    @Test
    void getServiceById_Success() {
        // Arrange
        when(serviceRepository.findById(serviceId)).thenReturn(Optional.of(testService));

        // Act
        ServiceDetailResponse result = serviceCatalogService.getServiceById(serviceId);

        // Assert
        assertNotNull(result);
        assertEquals(testService.getId(), result.getId());
        assertEquals(testService.getName(), result.getName());
        assertEquals(testService.getDescription(), result.getDescription());
        assertEquals(testService.getServiceType(), result.getServiceType());
        assertEquals(testService.getBaseBandwidthMbps(), result.getBaseBandwidthMbps());
        assertEquals(testService.getBasePriceMonthly(), result.getBasePriceMonthly());
        assertEquals(testService.getIsBandwidthAdjustable(), result.getIsBandwidthAdjustable());

        verify(serviceRepository).findById(serviceId);
    }

    @Test
    void getServiceById_NotFound() {
        // Arrange
        when(serviceRepository.findById(serviceId)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            serviceCatalogService.getServiceById(serviceId);
        });

        assertEquals("Service not found with ID: " + serviceId, exception.getMessage());
        verify(serviceRepository).findById(serviceId);
    }

    @Test
    void getServiceById_NotAvailable() {
        // Arrange
        testService.setIsAvailable(false);
        when(serviceRepository.findById(serviceId)).thenReturn(Optional.of(testService));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            serviceCatalogService.getServiceById(serviceId);
        });

        assertEquals("Service is not available: " + serviceId, exception.getMessage());
        verify(serviceRepository).findById(serviceId);
    }

    @Test
    void getServicesByCategory_Success() {
        // Arrange
        List<Service> services = Arrays.asList(testService);
        when(serviceRepository.findByCategoryId(categoryId)).thenReturn(services);

        // Act
        List<ServiceSummaryResponse> result = serviceCatalogService.getServicesByCategory(categoryId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        ServiceSummaryResponse response = result.get(0);
        assertEquals(testService.getId(), response.getId());
        assertEquals(testService.getName(), response.getName());

        verify(serviceRepository).findByCategoryId(categoryId);
    }

    @Test
    void getServicesByType_Success() {
        // Arrange
        String serviceType = "FIBER";
        List<Service> services = Arrays.asList(testService);
        when(serviceRepository.findByServiceType(serviceType)).thenReturn(services);

        // Act
        List<ServiceSummaryResponse> result = serviceCatalogService.getServicesByType(serviceType);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        ServiceSummaryResponse response = result.get(0);
        assertEquals(testService.getId(), response.getId());
        assertEquals(testService.getServiceType(), response.getServiceType());

        verify(serviceRepository).findByServiceType(serviceType);
    }

    @Test
    void searchServicesByName_Success() {
        // Arrange
        String searchName = "Fiber";
        List<Service> services = Arrays.asList(testService);
        when(serviceRepository.findByNameContainingIgnoreCase(searchName)).thenReturn(services);

        // Act
        List<ServiceSummaryResponse> result = serviceCatalogService.searchServicesByName(searchName);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        ServiceSummaryResponse response = result.get(0);
        assertEquals(testService.getId(), response.getId());
        assertEquals(testService.getName(), response.getName());

        verify(serviceRepository).findByNameContainingIgnoreCase(searchName);
    }

    @Test
    void getServicesByPriceRange_Success() {
        // Arrange
        BigDecimal minPrice = new BigDecimal("200.00");
        BigDecimal maxPrice = new BigDecimal("400.00");
        List<Service> services = Arrays.asList(testService);
        when(serviceRepository.findByPriceRange(minPrice, maxPrice)).thenReturn(services);

        // Act
        List<ServiceSummaryResponse> result = serviceCatalogService.getServicesByPriceRange(minPrice, maxPrice);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        ServiceSummaryResponse response = result.get(0);
        assertEquals(testService.getId(), response.getId());
        assertTrue(response.getBasePriceMonthly().compareTo(minPrice) >= 0);
        assertTrue(response.getBasePriceMonthly().compareTo(maxPrice) <= 0);

        verify(serviceRepository).findByPriceRange(minPrice, maxPrice);
    }

    @Test
    void calculateServiceCost_Success() {
        // Arrange
        Integer bandwidthMbps = 750; // Above base bandwidth
        when(serviceRepository.findById(serviceId)).thenReturn(Optional.of(testService));

        // Act
        BigDecimal result = serviceCatalogService.calculateServiceCost(serviceId, bandwidthMbps);

        // Assert
        assertNotNull(result);
        // Expected: 299.00 + (750-500) * 0.50 = 299.00 + 125.00 = 424.00
        BigDecimal expected = new BigDecimal("424.00");
        assertEquals(0, expected.compareTo(result));

        verify(serviceRepository).findById(serviceId);
    }

    @Test
    void calculateServiceCost_ServiceNotFound() {
        // Arrange
        Integer bandwidthMbps = 500;
        when(serviceRepository.findById(serviceId)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            serviceCatalogService.calculateServiceCost(serviceId, bandwidthMbps);
        });

        assertEquals("Service not found with ID: " + serviceId, exception.getMessage());
        verify(serviceRepository).findById(serviceId);
    }

    @Test
    void calculateServiceCost_InvalidBandwidth() {
        // Arrange
        Integer invalidBandwidth = 50; // Below minimum
        when(serviceRepository.findById(serviceId)).thenReturn(Optional.of(testService));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            serviceCatalogService.calculateServiceCost(serviceId, invalidBandwidth);
        });

        assertEquals("Invalid bandwidth for service: " + invalidBandwidth + " Mbps", exception.getMessage());
        verify(serviceRepository).findById(serviceId);
    }

    @Test
    void validateServiceOrder_Success() {
        // Arrange
        Integer validBandwidth = 500;
        when(serviceRepository.findById(serviceId)).thenReturn(Optional.of(testService));

        // Act
        boolean result = serviceCatalogService.validateServiceOrder(serviceId, validBandwidth);

        // Assert
        assertTrue(result);
        verify(serviceRepository).findById(serviceId);
    }

    @Test
    void validateServiceOrder_ServiceNotFound() {
        // Arrange
        Integer bandwidth = 500;
        when(serviceRepository.findById(serviceId)).thenReturn(Optional.empty());

        // Act
        boolean result = serviceCatalogService.validateServiceOrder(serviceId, bandwidth);

        // Assert
        assertFalse(result);
        verify(serviceRepository).findById(serviceId);
    }

    @Test
    void validateServiceOrder_ServiceNotAvailable() {
        // Arrange
        testService.setIsAvailable(false);
        Integer bandwidth = 500;
        when(serviceRepository.findById(serviceId)).thenReturn(Optional.of(testService));

        // Act
        boolean result = serviceCatalogService.validateServiceOrder(serviceId, bandwidth);

        // Assert
        assertFalse(result);
        verify(serviceRepository).findById(serviceId);
    }

    @Test
    void validateServiceOrder_InvalidBandwidth() {
        // Arrange
        Integer invalidBandwidth = 2000; // Above maximum
        when(serviceRepository.findById(serviceId)).thenReturn(Optional.of(testService));

        // Act
        boolean result = serviceCatalogService.validateServiceOrder(serviceId, invalidBandwidth);

        // Assert
        assertFalse(result);
        verify(serviceRepository).findById(serviceId);
    }
}
