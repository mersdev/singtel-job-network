package com.singtel.network.controller;

import com.singtel.network.api.ServiceCatalogApi;
import com.singtel.network.dto.service.ServiceCategoryResponse;
import com.singtel.network.dto.service.ServiceDetailResponse;
import com.singtel.network.dto.service.ServiceSummaryResponse;
import com.singtel.network.service.ServiceCatalogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * REST controller for service catalog operations.
 */
@RestController
@RequestMapping("/services")
public class ServiceCatalogController implements ServiceCatalogApi {

    private static final Logger logger = LoggerFactory.getLogger(ServiceCatalogController.class);

    @Autowired
    private ServiceCatalogService serviceCatalogService;

    @Override
    public ResponseEntity<List<ServiceCategoryResponse>> getAllCategories() {
        logger.info("Fetching all service categories");
        
        List<ServiceCategoryResponse> categories = serviceCatalogService.getAllActiveCategories();
        
        logger.info("Retrieved {} service categories", categories.size());
        return ResponseEntity.ok(categories);
    }

    @Override
    public ResponseEntity<Page<ServiceCategoryResponse>> getCategoriesPaged(
            @PageableDefault(size = 10) Pageable pageable) {
        logger.info("Fetching service categories with pagination: {}", pageable);
        
        Page<ServiceCategoryResponse> categories = serviceCatalogService.getAllActiveCategories(pageable);
        
        logger.info("Retrieved {} service categories (page {} of {})", 
                   categories.getNumberOfElements(), categories.getNumber() + 1, categories.getTotalPages());
        return ResponseEntity.ok(categories);
    }

    @Override
    public ResponseEntity<ServiceCategoryResponse> getCategoryById(@PathVariable UUID categoryId) {
        logger.info("Fetching service category by ID: {}", categoryId);
        
        ServiceCategoryResponse category = serviceCatalogService.getCategoryById(categoryId);
        
        logger.info("Retrieved service category: {}", category.getName());
        return ResponseEntity.ok(category);
    }

    @Override
    public ResponseEntity<List<ServiceSummaryResponse>> getAllServices() {
        logger.info("Fetching all available services");
        
        List<ServiceSummaryResponse> services = serviceCatalogService.getAllAvailableServices();
        
        logger.info("Retrieved {} available services", services.size());
        return ResponseEntity.ok(services);
    }

    @Override
    public ResponseEntity<Page<ServiceSummaryResponse>> getServicesPaged(
            @PageableDefault(size = 20) Pageable pageable) {
        logger.info("Fetching services with pagination: {}", pageable);
        
        Page<ServiceSummaryResponse> services = serviceCatalogService.getAllAvailableServices(pageable);
        
        logger.info("Retrieved {} services (page {} of {})", 
                   services.getNumberOfElements(), services.getNumber() + 1, services.getTotalPages());
        return ResponseEntity.ok(services);
    }

    @Override
    public ResponseEntity<ServiceDetailResponse> getServiceById(@PathVariable UUID serviceId) {
        logger.info("Fetching service by ID: {}", serviceId);
        
        ServiceDetailResponse service = serviceCatalogService.getServiceById(serviceId);
        
        logger.info("Retrieved service: {}", service.getName());
        return ResponseEntity.ok(service);
    }

    @Override
    public ResponseEntity<List<ServiceSummaryResponse>> getServicesByCategory(@PathVariable UUID categoryId) {
        logger.info("Fetching services by category: {}", categoryId);
        
        List<ServiceSummaryResponse> services = serviceCatalogService.getServicesByCategory(categoryId);
        
        logger.info("Retrieved {} services for category: {}", services.size(), categoryId);
        return ResponseEntity.ok(services);
    }

    @Override
    public ResponseEntity<Page<ServiceSummaryResponse>> getServicesByCategoryPaged(
            @PathVariable UUID categoryId,
            @PageableDefault(size = 20) Pageable pageable) {
        logger.info("Fetching services by category with pagination: {}, {}", categoryId, pageable);
        
        Page<ServiceSummaryResponse> services = serviceCatalogService.getServicesByCategory(categoryId, pageable);
        
        logger.info("Retrieved {} services for category: {} (page {} of {})", 
                   services.getNumberOfElements(), categoryId, services.getNumber() + 1, services.getTotalPages());
        return ResponseEntity.ok(services);
    }

    @Override
    public ResponseEntity<List<ServiceSummaryResponse>> getServicesByType(@PathVariable String serviceType) {
        logger.info("Fetching services by type: {}", serviceType);
        
        List<ServiceSummaryResponse> services = serviceCatalogService.getServicesByType(serviceType);
        
        logger.info("Retrieved {} services for type: {}", services.size(), serviceType);
        return ResponseEntity.ok(services);
    }

    @Override
    public ResponseEntity<Page<ServiceSummaryResponse>> searchServices(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) String serviceType,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Integer minBandwidth,
            @RequestParam(required = false) Integer maxBandwidth,
            @RequestParam(required = false) Boolean bandwidthAdjustable,
            @PageableDefault(size = 20) Pageable pageable) {

        logger.info("Searching services with criteria - Name: {}, Category: {}, Type: {}, Price: {}-{}, Bandwidth: {}-{}, Adjustable: {}",
                   name, categoryId, serviceType, minPrice, maxPrice, minBandwidth, maxBandwidth, bandwidthAdjustable);

        try {
            Page<ServiceSummaryResponse> services;

            // Check if any search criteria is provided
            boolean hasSearchCriteria = (name != null && !name.trim().isEmpty()) ||
                                      categoryId != null ||
                                      serviceType != null ||
                                      minPrice != null ||
                                      maxPrice != null ||
                                      minBandwidth != null ||
                                      maxBandwidth != null ||
                                      bandwidthAdjustable != null;

            if (!hasSearchCriteria) {
                // No search criteria provided, return all available services
                services = serviceCatalogService.getAllAvailableServices(pageable);
            } else if (name != null && !name.trim().isEmpty()) {
                // Search by name
                services = serviceCatalogService.searchServicesByName(name.trim(), pageable);
            } else {
                // Search with other criteria
                services = serviceCatalogService.searchServices(categoryId, serviceType, minPrice, maxPrice,
                                                               minBandwidth, maxBandwidth, bandwidthAdjustable, pageable);
            }

            logger.info("Search returned {} services (page {} of {})",
                       services.getNumberOfElements(), services.getNumber() + 1, services.getTotalPages());
            return ResponseEntity.ok(services);

        } catch (Exception e) {
            logger.error("Error searching services", e);
            throw new RuntimeException("Failed to search services", e);
        }
    }

    @Override
    public ResponseEntity<List<ServiceSummaryResponse>> getPopularServices(
            @RequestParam(defaultValue = "10") int limit) {
        logger.info("Fetching popular services with limit: {}", limit);
        
        List<ServiceSummaryResponse> services = serviceCatalogService.getPopularServices(limit);
        
        logger.info("Retrieved {} popular services", services.size());
        return ResponseEntity.ok(services);
    }

    @Override
    public ResponseEntity<List<ServiceSummaryResponse>> getBandwidthAdjustableServices() {
        logger.info("Fetching bandwidth adjustable services");
        
        List<ServiceSummaryResponse> services = serviceCatalogService.getBandwidthAdjustableServices();
        
        logger.info("Retrieved {} bandwidth adjustable services", services.size());
        return ResponseEntity.ok(services);
    }

    @Override
    public ResponseEntity<List<String>> getServiceTypes() {
        logger.info("Fetching service types");
        
        List<String> serviceTypes = serviceCatalogService.getServiceTypes();
        
        logger.info("Retrieved {} service types", serviceTypes.size());
        return ResponseEntity.ok(serviceTypes);
    }

    @Override
    public ResponseEntity<Map<String, Object>> calculateServiceCost(
            @PathVariable UUID serviceId,
            @RequestParam Integer bandwidthMbps) {
        logger.info("Calculating cost for service: {} with bandwidth: {} Mbps", serviceId, bandwidthMbps);
        
        BigDecimal cost = serviceCatalogService.calculateServiceCost(serviceId, bandwidthMbps);
        
        Map<String, Object> response = Map.of(
            "serviceId", serviceId,
            "bandwidthMbps", bandwidthMbps,
            "monthlyCost", cost,
            "currency", "SGD"
        );
        
        logger.info("Calculated cost: {} SGD for service: {} with bandwidth: {} Mbps", cost, serviceId, bandwidthMbps);
        return ResponseEntity.ok(response);
    }
}
