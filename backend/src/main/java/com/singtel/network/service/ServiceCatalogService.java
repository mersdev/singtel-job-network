package com.singtel.network.service;

import com.singtel.network.dto.service.ServiceCategoryResponse;
import com.singtel.network.dto.service.ServiceDetailResponse;
import com.singtel.network.dto.service.ServiceSummaryResponse;
import com.singtel.network.entity.ServiceCategory;
import com.singtel.network.repository.ServiceCategoryRepository;
import com.singtel.network.repository.ServiceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for managing service catalog operations.
 */
@org.springframework.stereotype.Service
@Transactional(readOnly = true)
public class ServiceCatalogService {

    private static final Logger logger = LoggerFactory.getLogger(ServiceCatalogService.class);

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private ServiceCategoryRepository serviceCategoryRepository;

    /**
     * Get all active service categories with their services
     */
    public List<ServiceCategoryResponse> getAllActiveCategories() {
        logger.debug("Fetching all active service categories");
        
        List<ServiceCategory> categories = serviceCategoryRepository.findActiveCategories();
        return categories.stream()
                .map(category -> new ServiceCategoryResponse(category, true))
                .collect(Collectors.toList());
    }

    /**
     * Get all active service categories with pagination
     */
    public Page<ServiceCategoryResponse> getAllActiveCategories(Pageable pageable) {
        logger.debug("Fetching active service categories with pagination: {}", pageable);
        
        Page<ServiceCategory> categories = serviceCategoryRepository.findActiveCategories(pageable);
        return categories.map(category -> new ServiceCategoryResponse(category, false));
    }

    /**
     * Get service category by ID
     */
    public ServiceCategoryResponse getCategoryById(UUID categoryId) {
        logger.debug("Fetching service category by ID: {}", categoryId);
        
        ServiceCategory category = serviceCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Service category not found with ID: " + categoryId));
        
        return new ServiceCategoryResponse(category, true);
    }

    /**
     * Get all available services
     */
    public List<ServiceSummaryResponse> getAllAvailableServices() {
        logger.debug("Fetching all available services");
        
        List<com.singtel.network.entity.Service> services = serviceRepository.findAvailableServices();
        return services.stream()
                .map(ServiceSummaryResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * Get all available services with pagination
     */
    public Page<ServiceSummaryResponse> getAllAvailableServices(Pageable pageable) {
        logger.debug("Fetching available services with pagination: {}", pageable);
        
        Page<com.singtel.network.entity.Service> services = serviceRepository.findAvailableServices(pageable);
        return services.map(ServiceSummaryResponse::new);
    }

    /**
     * Get service by ID
     */
    public ServiceDetailResponse getServiceById(UUID serviceId) {
        logger.debug("Fetching service by ID: {}", serviceId);
        
        com.singtel.network.entity.Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("Service not found with ID: " + serviceId));
        
        if (!service.isAvailable()) {
            throw new IllegalArgumentException("Service is not available: " + serviceId);
        }
        
        return new ServiceDetailResponse(service);
    }

    /**
     * Get services by category ID
     */
    public List<ServiceSummaryResponse> getServicesByCategory(UUID categoryId) {
        logger.debug("Fetching services by category ID: {}", categoryId);
        
        List<com.singtel.network.entity.Service> services = serviceRepository.findByCategoryId(categoryId);
        return services.stream()
                .filter(com.singtel.network.entity.Service::isAvailable)
                .map(ServiceSummaryResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * Get services by category ID with pagination
     */
    public Page<ServiceSummaryResponse> getServicesByCategory(UUID categoryId, Pageable pageable) {
        logger.debug("Fetching services by category ID with pagination: {}, {}", categoryId, pageable);
        
        Page<com.singtel.network.entity.Service> services = serviceRepository.findByCategoryId(categoryId, pageable);
        return services.map(ServiceSummaryResponse::new);
    }

    /**
     * Get services by service type
     */
    public List<ServiceSummaryResponse> getServicesByType(String serviceType) {
        logger.debug("Fetching services by type: {}", serviceType);
        
        List<com.singtel.network.entity.Service> services = serviceRepository.findByServiceType(serviceType);
        return services.stream()
                .filter(com.singtel.network.entity.Service::isAvailable)
                .map(ServiceSummaryResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * Search services by name
     */
    public List<ServiceSummaryResponse> searchServicesByName(String name) {
        logger.debug("Searching services by name: {}", name);
        
        List<com.singtel.network.entity.Service> services = serviceRepository.findByNameContainingIgnoreCase(name);
        return services.stream()
                .map(ServiceSummaryResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * Search services by name with pagination
     */
    public Page<ServiceSummaryResponse> searchServicesByName(String name, Pageable pageable) {
        logger.debug("Searching services by name with pagination: {}, {}", name, pageable);
        
        Page<com.singtel.network.entity.Service> services = serviceRepository.findByNameContainingIgnoreCase(name, pageable);
        return services.map(ServiceSummaryResponse::new);
    }

    /**
     * Get services by price range
     */
    public List<ServiceSummaryResponse> getServicesByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        logger.debug("Fetching services by price range: {} - {}", minPrice, maxPrice);
        
        List<com.singtel.network.entity.Service> services = serviceRepository.findByPriceRange(minPrice, maxPrice);
        return services.stream()
                .map(ServiceSummaryResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * Get services by bandwidth range
     */
    public List<ServiceSummaryResponse> getServicesByBandwidthRange(Integer minBandwidth, Integer maxBandwidth) {
        logger.debug("Fetching services by bandwidth range: {} - {} Mbps", minBandwidth, maxBandwidth);
        
        List<com.singtel.network.entity.Service> services = serviceRepository.findByBandwidthRange(minBandwidth, maxBandwidth);
        return services.stream()
                .map(ServiceSummaryResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * Get bandwidth adjustable services
     */
    public List<ServiceSummaryResponse> getBandwidthAdjustableServices() {
        logger.debug("Fetching bandwidth adjustable services");
        
        List<com.singtel.network.entity.Service> services = serviceRepository.findBandwidthAdjustableServices();
        return services.stream()
                .map(ServiceSummaryResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * Search services with multiple criteria
     */
    public Page<ServiceSummaryResponse> searchServices(UUID categoryId, String serviceType,
                                                      BigDecimal minPrice, BigDecimal maxPrice,
                                                      Integer minBandwidth, Integer maxBandwidth,
                                                      Boolean bandwidthAdjustable, Pageable pageable) {
        logger.debug("Searching services with criteria - Category: {}, Type: {}, Price: {}-{}, Bandwidth: {}-{}, Adjustable: {}",
                    categoryId, serviceType, minPrice, maxPrice, minBandwidth, maxBandwidth, bandwidthAdjustable);
        
        Page<com.singtel.network.entity.Service> services = serviceRepository.searchServices(categoryId, serviceType, minPrice, maxPrice,
                                                                 minBandwidth, maxBandwidth, bandwidthAdjustable, pageable);
        return services.map(ServiceSummaryResponse::new);
    }

    /**
     * Get popular services (most ordered)
     */
    public List<ServiceSummaryResponse> getPopularServices(int limit) {
        logger.debug("Fetching popular services with limit: {}", limit);
        
        Pageable pageable = Pageable.ofSize(limit);
        List<com.singtel.network.entity.Service> services = serviceRepository.findPopularServices(pageable);
        return services.stream()
                .map(ServiceSummaryResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * Get distinct service types
     */
    public List<String> getServiceTypes() {
        logger.debug("Fetching distinct service types");
        
        return serviceRepository.findDistinctServiceTypes();
    }

    /**
     * Calculate service cost for specific bandwidth
     */
    public BigDecimal calculateServiceCost(UUID serviceId, Integer bandwidthMbps) {
        logger.debug("Calculating service cost for service: {} with bandwidth: {} Mbps", serviceId, bandwidthMbps);
        
        com.singtel.network.entity.Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("Service not found with ID: " + serviceId));
        
        if (!service.isAvailable()) {
            throw new IllegalArgumentException("Service is not available: " + serviceId);
        }
        
        if (!service.isValidBandwidth(bandwidthMbps)) {
            throw new IllegalArgumentException("Invalid bandwidth for service: " + bandwidthMbps + " Mbps");
        }
        
        return service.calculateMonthlyCost(bandwidthMbps);
    }

    /**
     * Validate service availability and bandwidth
     */
    public boolean validateServiceOrder(UUID serviceId, Integer bandwidthMbps) {
        logger.debug("Validating service order - Service: {}, Bandwidth: {} Mbps", serviceId, bandwidthMbps);
        
        try {
            com.singtel.network.entity.Service service = serviceRepository.findById(serviceId)
                    .orElse(null);
            
            if (service == null || !service.isAvailable()) {
                return false;
            }
            
            return service.isValidBandwidth(bandwidthMbps);
        } catch (Exception e) {
            logger.error("Error validating service order", e);
            return false;
        }
    }
}
