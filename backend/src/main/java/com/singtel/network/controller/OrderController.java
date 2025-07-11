package com.singtel.network.controller;

import com.singtel.network.api.OrderApi;
import com.singtel.network.dto.order.CreateOrderRequest;
import com.singtel.network.dto.order.OrderResponse;
import com.singtel.network.entity.Order;
import com.singtel.network.service.OrderService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * REST controller for order management operations.
 */
@RestController
@RequestMapping("/orders")
public class OrderController implements OrderApi {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

    @Override
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        logger.info("Creating new order for service: {}", request.getServiceId());
        
        OrderResponse order = orderService.createOrder(request);
        
        logger.info("Order created successfully: {}", order.getOrderNumber());
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @Override
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable UUID orderId) {
        logger.info("Fetching order by ID: {}", orderId);
        
        OrderResponse order = orderService.getOrderById(orderId);
        
        logger.info("Retrieved order: {}", order.getOrderNumber());
        return ResponseEntity.ok(order);
    }

    @Override
    public ResponseEntity<OrderResponse> getOrderByNumber(@PathVariable String orderNumber) {
        logger.info("Fetching order by number: {}", orderNumber);
        
        OrderResponse order = orderService.getOrderByNumber(orderNumber);
        
        logger.info("Retrieved order: {}", order.getOrderNumber());
        return ResponseEntity.ok(order);
    }

    @Override
    public ResponseEntity<List<OrderResponse>> getCompanyOrders() {
        logger.info("Fetching orders for current user's company");
        
        List<OrderResponse> orders = orderService.getOrdersByCompany();
        
        logger.info("Retrieved {} orders", orders.size());
        return ResponseEntity.ok(orders);
    }

    @Override
    public ResponseEntity<Page<OrderResponse>> getOrdersPaged(@PageableDefault(size = 20) Pageable pageable) {
        logger.info("Fetching orders with pagination: {}", pageable);
        
        Page<OrderResponse> orders = orderService.getOrdersByCompany(pageable);
        
        logger.info("Retrieved {} orders (page {} of {})", 
                   orders.getNumberOfElements(), orders.getNumber() + 1, orders.getTotalPages());
        return ResponseEntity.ok(orders);
    }

    @Override
    public ResponseEntity<List<OrderResponse>> getOrdersByStatus(@PathVariable Order.OrderStatus status) {
        logger.info("Fetching orders by status: {}", status);
        
        List<OrderResponse> orders = orderService.getOrdersByStatus(status);
        
        logger.info("Retrieved {} orders with status: {}", orders.size(), status);
        return ResponseEntity.ok(orders);
    }

    @Override
    public ResponseEntity<List<OrderResponse>> getPendingOrders() {
        logger.info("Fetching pending orders");
        
        List<OrderResponse> orders = orderService.getPendingOrders();
        
        logger.info("Retrieved {} pending orders", orders.size());
        return ResponseEntity.ok(orders);
    }

    @Override
    public ResponseEntity<List<OrderResponse>> getRecentOrders(
            @RequestParam(defaultValue = "10") int limit) {
        logger.info("Fetching recent orders with limit: {}", limit);
        
        List<OrderResponse> orders = orderService.getRecentOrders(limit);
        
        logger.info("Retrieved {} recent orders", orders.size());
        return ResponseEntity.ok(orders);
    }

    @Override
    public ResponseEntity<Page<OrderResponse>> searchOrders(
            @RequestParam(required = false) Order.OrderStatus status,
            @RequestParam(required = false) Order.OrderType orderType,
            @RequestParam(required = false) UUID serviceId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) BigDecimal minCost,
            @RequestParam(required = false) BigDecimal maxCost,
            @PageableDefault(size = 20) Pageable pageable) {
        
        logger.info("Searching orders with criteria - Status: {}, Type: {}, Service: {}", status, orderType, serviceId);
        
        Page<OrderResponse> orders = orderService.searchOrders(status, orderType, serviceId, 
                                                              startDate, endDate, minCost, maxCost, pageable);
        
        logger.info("Search returned {} orders (page {} of {})", 
                   orders.getNumberOfElements(), orders.getNumber() + 1, orders.getTotalPages());
        return ResponseEntity.ok(orders);
    }

    @Override
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable UUID orderId) {
        logger.info("Cancelling order: {}", orderId);
        
        OrderResponse order = orderService.cancelOrder(orderId);
        
        logger.info("Order cancelled successfully: {}", order.getOrderNumber());
        return ResponseEntity.ok(order);
    }

    @Override
    public ResponseEntity<Map<String, Object>> getOrderStatistics() {
        logger.info("Fetching order statistics");
        
        BigDecimal totalOrderValue = orderService.getTotalOrderValue();
        List<OrderResponse> pendingOrders = orderService.getPendingOrders();
        List<OrderResponse> recentOrders = orderService.getRecentOrders(5);
        
        Map<String, Object> statistics = Map.of(
            "totalOrderValue", totalOrderValue,
            "pendingOrdersCount", pendingOrders.size(),
            "recentOrdersCount", recentOrders.size(),
            "currency", "SGD"
        );
        
        logger.info("Retrieved order statistics - Total value: {}, Pending: {}", 
                   totalOrderValue, pendingOrders.size());
        return ResponseEntity.ok(statistics);
    }
}
