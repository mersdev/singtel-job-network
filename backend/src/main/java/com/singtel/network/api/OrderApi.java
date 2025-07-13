package com.singtel.network.api;

import com.singtel.network.dto.order.CreateOrderRequest;
import com.singtel.network.dto.order.OrderResponse;
import com.singtel.network.entity.Order;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * API interface for order management operations.
 */
@Tag(name = "Order Management", description = "Order placement and management operations")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
public interface OrderApi {

    /**
     * Create a new order
     */
    @PostMapping
    @Operation(summary = "Create a new order", description = "Place a new order for network services")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Order created successfully",
                    content = @Content(
                        schema = @Schema(implementation = OrderResponse.class),
                        examples = @ExampleObject(
                            name = "New Order Response",
                            value = """
                                {
                                  "id": "aa0e8400-e29b-41d4-a716-446655440003",
                                  "orderNumber": "ORD-2024-003",
                                  "orderType": "NEW_SERVICE",
                                  "status": "SUBMITTED",
                                  "requestedBandwidthMbps": 1000,
                                  "installationAddress": "123 Business Park, #05-01",
                                  "postalCode": "569874",
                                  "contactPerson": "John Doe",
                                  "contactPhone": "+65 9123 4567",
                                  "contactEmail": "john.doe@techstart.sg",
                                  "requestedDate": "2024-07-15",
                                  "estimatedCompletionDate": "2024-07-22",
                                  "totalCost": 649.00,
                                  "notes": "New office setup - priority installation",
                                  "service": {
                                    "id": "660e8400-e29b-41d4-a716-446655440002",
                                    "name": "Business Fiber 1G",
                                    "serviceType": "FIBER"
                                  },
                                  "company": {
                                    "id": "770e8400-e29b-41d4-a716-446655440001",
                                    "name": "TechStart Pte Ltd"
                                  },
                                  "createdAt": "2024-07-12T03:27:35.207436",
                                  "updatedAt": "2024-07-12T03:27:35.207436"
                                }
                                """
                        )
                    )),
        @ApiResponse(responseCode = "400", description = "Invalid order data",
                    content = @Content(
                        examples = @ExampleObject(
                            name = "Validation Error",
                            value = """
                                {
                                  "status": 400,
                                  "error": "Validation failed",
                                  "message": "Invalid input data",
                                  "path": "uri=/api/orders",
                                  "timestamp": "2024-07-12T03:27:35.207436",
                                  "validationErrors": {
                                    "serviceId": "Service ID is required",
                                    "requestedBandwidthMbps": "Requested bandwidth must be at least 1 Mbps",
                                    "installationAddress": "Installation address is required"
                                  }
                                }
                                """
                        )
                    )),
        @ApiResponse(responseCode = "401", description = "User not authenticated"),
        @ApiResponse(responseCode = "403", description = "User not authorized")
    })
    ResponseEntity<OrderResponse> createOrder(
        @Valid @RequestBody @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                schema = @Schema(implementation = CreateOrderRequest.class),
                examples = {
                    @ExampleObject(
                        name = "New Service Order",
                        value = """
                            {
                              "serviceId": "660e8400-e29b-41d4-a716-446655440002",
                              "orderType": "NEW_SERVICE",
                              "requestedBandwidthMbps": 1000,
                              "installationAddress": "123 Business Park, #05-01",
                              "postalCode": "569874",
                              "contactPerson": "John Doe",
                              "contactPhone": "+65 9123 4567",
                              "contactEmail": "john.doe@techstart.sg",
                              "requestedDate": "2024-07-15",
                              "notes": "New office setup - priority installation"
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "Modify Service Order",
                        value = """
                            {
                              "serviceId": "660e8400-e29b-41d4-a716-446655440001",
                              "orderType": "MODIFY_SERVICE",
                              "requestedBandwidthMbps": 750,
                              "serviceInstanceId": "990e8400-e29b-41d4-a716-446655440001",
                              "contactPerson": "John Doe",
                              "contactPhone": "+65 9123 4567",
                              "contactEmail": "john.doe@techstart.sg",
                              "requestedDate": "2024-07-20",
                              "notes": "Bandwidth upgrade for increased traffic"
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "Terminate Service Order",
                        value = """
                            {
                              "serviceId": "660e8400-e29b-41d4-a716-446655440001",
                              "orderType": "TERMINATE_SERVICE",
                              "serviceInstanceId": "990e8400-e29b-41d4-a716-446655440001",
                              "contactPerson": "John Doe",
                              "contactPhone": "+65 9123 4567",
                              "contactEmail": "john.doe@techstart.sg",
                              "requestedDate": "2024-08-01",
                              "notes": "Office relocation - service no longer needed"
                            }
                            """
                    )
                }
            )
        ) CreateOrderRequest request);

    /**
     * Get order by ID
     */
    @GetMapping("/{orderId}")
    @Operation(summary = "Get order by ID", description = "Retrieve a specific order by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order retrieved successfully",
                    content = @Content(
                        schema = @Schema(implementation = OrderResponse.class),
                        examples = @ExampleObject(
                            name = "Order Details Response",
                            value = """
                                {
                                  "id": "aa0e8400-e29b-41d4-a716-446655440001",
                                  "orderNumber": "ORD-2024-001",
                                  "orderType": "NEW_SERVICE",
                                  "status": "COMPLETED",
                                  "requestedBandwidthMbps": 500,
                                  "installationAddress": "1 Marina Bay Sands, Level 10",
                                  "postalCode": "018956",
                                  "contactPerson": "John Doe",
                                  "contactPhone": "+65 9123 4567",
                                  "contactEmail": "john.doe@techstart.sg",
                                  "requestedDate": "2023-12-15",
                                  "estimatedCompletionDate": "2024-01-01",
                                  "actualCompletionDate": "2024-01-01",
                                  "totalCost": 449.00,
                                  "notes": "Initial service setup for new office",
                                  "service": {
                                    "id": "660e8400-e29b-41d4-a716-446655440001",
                                    "name": "Business Fiber 500M",
                                    "serviceType": "FIBER"
                                  },
                                  "serviceInstance": {
                                    "id": "990e8400-e29b-41d4-a716-446655440001",
                                    "instanceName": "TechStart Main Office Internet"
                                  },
                                  "company": {
                                    "id": "770e8400-e29b-41d4-a716-446655440001",
                                    "name": "TechStart Pte Ltd"
                                  },
                                  "user": {
                                    "id": "880e8400-e29b-41d4-a716-446655440001",
                                    "username": "john.doe",
                                    "firstName": "John",
                                    "lastName": "Doe"
                                  },
                                  "createdAt": "2023-12-15T10:30:00.000000",
                                  "updatedAt": "2024-01-01T14:20:00.000000"
                                }
                                """
                        )
                    )),
        @ApiResponse(responseCode = "404", description = "Order not found"),
        @ApiResponse(responseCode = "401", description = "User not authenticated"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('VIEWER')")
    ResponseEntity<OrderResponse> getOrderById(
            @Parameter(description = "Order ID", example = "aa0e8400-e29b-41d4-a716-446655440001") @PathVariable UUID orderId);

    /**
     * Get order by order number
     */
    @GetMapping("/number/{orderNumber}")
    @Operation(summary = "Get order by order number", description = "Retrieve a specific order by its order number")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Order not found"),
        @ApiResponse(responseCode = "401", description = "User not authenticated"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('VIEWER')")
    ResponseEntity<OrderResponse> getOrderByNumber(
            @Parameter(description = "Order number", example = "ORD-2024-001") @PathVariable String orderNumber);

    /**
     * Get all orders for current user's company
     */
    @GetMapping
    @Operation(summary = "Get company orders", description = "Retrieve all orders for the current user's company")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Orders retrieved successfully",
                    content = @Content(schema = @Schema(implementation = OrderResponse.class))),
        @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('VIEWER')")
    ResponseEntity<List<OrderResponse>> getCompanyOrders();

    /**
     * Get orders with pagination
     */
    @GetMapping("/paged")
    @Operation(summary = "Get orders with pagination", description = "Retrieve orders with pagination support")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('VIEWER')")
    ResponseEntity<Page<OrderResponse>> getOrdersPaged(
            @PageableDefault(size = 20) Pageable pageable);

    /**
     * Get orders by status
     */
    @GetMapping("/status/{status}")
    @Operation(summary = "Get orders by status", description = "Retrieve orders filtered by status")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('VIEWER')")
    ResponseEntity<List<OrderResponse>> getOrdersByStatus(
            @Parameter(description = "Order status") @PathVariable Order.OrderStatus status);

    /**
     * Get pending orders
     */
    @GetMapping("/pending")
    @Operation(summary = "Get pending orders", description = "Retrieve all pending orders (submitted, approved, in progress)")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('VIEWER')")
    ResponseEntity<List<OrderResponse>> getPendingOrders();

    /**
     * Get recent orders
     */
    @GetMapping("/recent")
    @Operation(summary = "Get recent orders", description = "Retrieve most recent orders")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('VIEWER')")
    ResponseEntity<List<OrderResponse>> getRecentOrders(
            @Parameter(description = "Number of orders to return") @RequestParam(defaultValue = "10") int limit);

    /**
     * Search orders
     */
    @GetMapping("/search")
    @Operation(summary = "Search orders", description = "Search orders with multiple criteria")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('VIEWER')")
    ResponseEntity<Page<OrderResponse>> searchOrders(
            @Parameter(description = "Order status", example = "COMPLETED") @RequestParam(required = false) Order.OrderStatus status,
            @Parameter(description = "Order type", example = "NEW_SERVICE") @RequestParam(required = false) Order.OrderType orderType,
            @Parameter(description = "Service ID", example = "660e8400-e29b-41d4-a716-446655440001") @RequestParam(required = false) UUID serviceId,
            @Parameter(description = "Start date (YYYY-MM-DD)", example = "2024-01-01") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "Minimum cost") @RequestParam(required = false) BigDecimal minCost,
            @Parameter(description = "Maximum cost") @RequestParam(required = false) BigDecimal maxCost,
            @PageableDefault(size = 20) Pageable pageable);

    /**
     * Cancel order
     */
    @PutMapping("/{orderId}/cancel")
    @Operation(summary = "Cancel order", description = "Cancel a pending order")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order cancelled successfully"),
        @ApiResponse(responseCode = "400", description = "Order cannot be cancelled"),
        @ApiResponse(responseCode = "404", description = "Order not found"),
        @ApiResponse(responseCode = "401", description = "User not authenticated"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    ResponseEntity<OrderResponse> cancelOrder(
            @Parameter(description = "Order ID") @PathVariable UUID orderId);

    /**
     * Get order statistics
     */
    @GetMapping("/statistics")
    @Operation(summary = "Get order statistics", description = "Retrieve order statistics for the company")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('VIEWER')")
    ResponseEntity<Map<String, Object>> getOrderStatistics();
}
