package com.singtel.network.api;

import com.singtel.network.dto.service.ServiceCategoryResponse;
import com.singtel.network.dto.service.ServiceDetailResponse;
import com.singtel.network.dto.service.ServiceSummaryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * API interface for service catalog operations.
 */
@Tag(name = "Service Catalog", description = "Service catalog and service management operations")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('VIEWER')")
public interface ServiceCatalogApi {

    /**
     * Get all service categories
     */
    @GetMapping("/categories")
    @Operation(summary = "Get all service categories", description = "Retrieve all active service categories with their services")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Categories retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ServiceCategoryResponse.class))),
        @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    ResponseEntity<List<ServiceCategoryResponse>> getAllCategories();

    /**
     * Get service categories with pagination
     */
    @GetMapping("/categories/paged")
    @Operation(summary = "Get service categories with pagination", description = "Retrieve service categories with pagination support")
    ResponseEntity<Page<ServiceCategoryResponse>> getCategoriesPaged(
            @PageableDefault(size = 10) Pageable pageable);

    /**
     * Get service category by ID
     */
    @GetMapping("/categories/{categoryId}")
    @Operation(summary = "Get service category by ID", description = "Retrieve a specific service category with its services")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Category retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Category not found"),
        @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    ResponseEntity<ServiceCategoryResponse> getCategoryById(
            @Parameter(description = "Service category ID") @PathVariable UUID categoryId);

    /**
     * Get all available services
     */
    @GetMapping
    @Operation(summary = "Get all available services", description = "Retrieve all available services in the catalog")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Services retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ServiceSummaryResponse.class))),
        @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    ResponseEntity<List<ServiceSummaryResponse>> getAllServices();

    /**
     * Get services with pagination
     */
    @GetMapping("/paged")
    @Operation(summary = "Get services with pagination", description = "Retrieve services with pagination support")
    ResponseEntity<Page<ServiceSummaryResponse>> getServicesPaged(
            @PageableDefault(size = 20) Pageable pageable);

    /**
     * Get service by ID
     */
    @GetMapping("/{serviceId}")
    @Operation(summary = "Get service by ID", description = "Retrieve detailed information about a specific service")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Service retrieved successfully",
                    content = @Content(
                        schema = @Schema(implementation = ServiceDetailResponse.class),
                        examples = @ExampleObject(
                            name = "Service Detail Response",
                            value = """
                                {
                                  "id": "660e8400-e29b-41d4-a716-446655440001",
                                  "name": "Business Fiber 500M",
                                  "description": "High-speed fiber internet with 500 Mbps download and upload",
                                  "serviceType": "FIBER",
                                  "baseBandwidthMbps": 500,
                                  "maxBandwidthMbps": 1000,
                                  "minBandwidthMbps": 100,
                                  "basePriceMonthly": 299.00,
                                  "pricePerMbps": 0.50,
                                  "setupFee": 150.00,
                                  "contractTermMonths": 24,
                                  "isBandwidthAdjustable": true,
                                  "isAvailable": true,
                                  "provisioningTimeHours": 72,
                                  "categoryName": "Business Internet",
                                  "features": {
                                    "static_ip": true,
                                    "sla_uptime": "99.9%",
                                    "support_level": "24x7",
                                    "backup_connection": false
                                  },
                                  "technicalSpecs": {
                                    "technology": "fiber_optic",
                                    "latency_ms": 5,
                                    "jitter_ms": 1,
                                    "packet_loss_max": 0.1
                                  }
                                }
                                """
                        )
                    )),
        @ApiResponse(responseCode = "404", description = "Service not found"),
        @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    ResponseEntity<ServiceDetailResponse> getServiceById(
            @Parameter(description = "Service ID", example = "660e8400-e29b-41d4-a716-446655440001") @PathVariable UUID serviceId);

    /**
     * Get services by category
     */
    @GetMapping("/categories/{categoryId}/services")
    @Operation(summary = "Get services by category", description = "Retrieve all services in a specific category")
    ResponseEntity<List<ServiceSummaryResponse>> getServicesByCategory(
            @Parameter(description = "Service category ID") @PathVariable UUID categoryId);

    /**
     * Get services by category with pagination
     */
    @GetMapping("/categories/{categoryId}/services/paged")
    @Operation(summary = "Get services by category with pagination", description = "Retrieve services in a category with pagination")
    ResponseEntity<Page<ServiceSummaryResponse>> getServicesByCategoryPaged(
            @Parameter(description = "Service category ID", example = "550e8400-e29b-41d4-a716-446655440001") @PathVariable UUID categoryId,
            @PageableDefault(size = 20) Pageable pageable);

    /**
     * Get services by type
     */
    @GetMapping("/type/{serviceType}")
    @Operation(summary = "Get services by type", description = "Retrieve all services of a specific type")
    ResponseEntity<List<ServiceSummaryResponse>> getServicesByType(
            @Parameter(description = "Service type", example = "FIBER") @PathVariable String serviceType);

    /**
     * Search services by name
     */
    @GetMapping("/search")
    @Operation(summary = "Search services by name", description = "Search for services by name and other criteria")
    ResponseEntity<Page<ServiceSummaryResponse>> searchServices(
            @Parameter(description = "Search term for service name", example = "fiber") @RequestParam(required = false) String name,
            @Parameter(description = "Service category ID", example = "550e8400-e29b-41d4-a716-446655440001") @RequestParam(required = false) UUID categoryId,
            @Parameter(description = "Service type", example = "FIBER") @RequestParam(required = false) String serviceType,
            @Parameter(description = "Minimum price in SGD", example = "100.00") @RequestParam(required = false) BigDecimal minPrice,
            @Parameter(description = "Maximum price in SGD", example = "500.00") @RequestParam(required = false) BigDecimal maxPrice,
            @Parameter(description = "Minimum bandwidth in Mbps", example = "100") @RequestParam(required = false) Integer minBandwidth,
            @Parameter(description = "Maximum bandwidth in Mbps", example = "1000") @RequestParam(required = false) Integer maxBandwidth,
            @Parameter(description = "Filter by bandwidth adjustable services", example = "true") @RequestParam(required = false) Boolean bandwidthAdjustable,
            @PageableDefault(size = 20) Pageable pageable);

    /**
     * Get popular services
     */
    @GetMapping("/popular")
    @Operation(summary = "Get popular services", description = "Retrieve most popular (frequently ordered) services")
    ResponseEntity<List<ServiceSummaryResponse>> getPopularServices(
            @Parameter(description = "Number of services to return", example = "5") @RequestParam(defaultValue = "10") int limit);

    /**
     * Get bandwidth adjustable services
     */
    @GetMapping("/bandwidth-adjustable")
    @Operation(summary = "Get bandwidth adjustable services", description = "Retrieve services that support bandwidth adjustment")
    @ApiResponse(responseCode = "200", description = "Bandwidth adjustable services retrieved successfully",
                content = @Content(
                    schema = @Schema(implementation = ServiceSummaryResponse.class),
                    examples = @ExampleObject(
                        name = "Bandwidth Adjustable Services",
                        value = """
                            [
                              {
                                "id": "660e8400-e29b-41d4-a716-446655440001",
                                "name": "Business Fiber 500M",
                                "description": "High-speed fiber internet with 500 Mbps download and upload",
                                "serviceType": "FIBER",
                                "baseBandwidthMbps": 500,
                                "maxBandwidthMbps": 1000,
                                "minBandwidthMbps": 100,
                                "basePriceMonthly": 299.00,
                                "pricePerMbps": 0.50,
                                "setupFee": 150.00,
                                "contractTermMonths": 24,
                                "isBandwidthAdjustable": true,
                                "isAvailable": true,
                                "provisioningTimeHours": 72,
                                "categoryName": "Business Internet"
                              }
                            ]
                            """
                    )
                ))
    ResponseEntity<List<ServiceSummaryResponse>> getBandwidthAdjustableServices();

    /**
     * Get service types
     */
    @GetMapping("/types")
    @Operation(summary = "Get service types", description = "Retrieve all available service types")
    @ApiResponse(responseCode = "200", description = "Service types retrieved successfully",
                content = @Content(
                    examples = @ExampleObject(
                        name = "Service Types",
                        value = """
                            ["FIBER", "VPN", "DEDICATED"]
                            """
                    )
                ))
    ResponseEntity<List<String>> getServiceTypes();

    /**
     * Calculate service cost
     */
    @GetMapping("/{serviceId}/cost")
    @Operation(summary = "Calculate service cost", description = "Calculate monthly cost for a service with specific bandwidth")
    @ApiResponse(responseCode = "200", description = "Service cost calculated successfully",
                content = @Content(
                    examples = @ExampleObject(
                        name = "Service Cost Calculation",
                        value = """
                            {
                              "serviceId": "660e8400-e29b-41d4-a716-446655440001",
                              "serviceName": "Business Fiber 500M",
                              "requestedBandwidthMbps": 750,
                              "basePriceMonthly": 299.00,
                              "additionalBandwidthCost": 125.00,
                              "totalMonthlyCost": 424.00,
                              "setupFee": 150.00,
                              "currency": "SGD"
                            }
                            """
                    )
                ))
    ResponseEntity<Map<String, Object>> calculateServiceCost(
            @Parameter(description = "Service ID", example = "660e8400-e29b-41d4-a716-446655440001") @PathVariable UUID serviceId,
            @Parameter(description = "Bandwidth in Mbps", example = "750") @RequestParam Integer bandwidthMbps);
}
