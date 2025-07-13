package com.singtel.network.service;

import com.singtel.network.dto.order.CreateOrderRequest;
import com.singtel.network.dto.order.OrderResponse;
import com.singtel.network.entity.*;
import com.singtel.network.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for managing order operations.
 */
@Service
@Transactional
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private ServiceInstanceRepository serviceInstanceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ServiceCatalogService serviceCatalogService;

    /**
     * Create a new order
     */
    public OrderResponse createOrder(CreateOrderRequest request) {
        logger.info("Creating new order for service: {}", request.getServiceId());

        // Get current user
        User currentUser = getCurrentUser();
        
        // Validate service
        com.singtel.network.entity.Service service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new IllegalArgumentException("Service not found with ID: " + request.getServiceId()));
        
        if (!service.isAvailable()) {
            throw new IllegalArgumentException("Service is not available: " + request.getServiceId());
        }

        // Validate bandwidth for new service orders
        if (request.isNewServiceOrder() && !service.isValidBandwidth(request.getRequestedBandwidthMbps())) {
            throw new IllegalArgumentException("Invalid bandwidth for service: " + request.getRequestedBandwidthMbps() + " Mbps");
        }

        // Validate service instance for modify/terminate orders
        ServiceInstance serviceInstance = null;
        if (request.isModifyServiceOrder() || request.isTerminateServiceOrder()) {
            serviceInstance = serviceInstanceRepository.findById(request.getServiceInstanceId())
                    .orElseThrow(() -> new IllegalArgumentException("Service instance not found: " + request.getServiceInstanceId()));
            
            // Verify ownership
            if (!serviceInstance.getCompany().getId().equals(currentUser.getCompany().getId())) {
                throw new IllegalArgumentException("Service instance does not belong to your company");
            }
        }

        // Create order
        Order order = new Order();
        order.setCompany(currentUser.getCompany());
        order.setUser(currentUser);
        order.setService(service);
        order.setServiceInstance(serviceInstance);
        order.setOrderNumber(generateOrderNumber());
        order.setOrderType(request.getOrderType());
        order.setRequestedBandwidthMbps(request.getRequestedBandwidthMbps());
        order.setInstallationAddress(request.getInstallationAddress());
        order.setPostalCode(request.getPostalCode());
        order.setContactPerson(request.getContactPerson());
        order.setContactPhone(request.getContactPhone());
        order.setContactEmail(request.getContactEmail());
        order.setRequestedDate(request.getRequestedDate() != null ? request.getRequestedDate() : LocalDate.now().plusDays(1));
        order.setNotes(request.getNotes());

        // Calculate costs and estimated completion
        calculateOrderDetails(order, service);

        // Save order
        order = orderRepository.save(order);
        
        logger.info("Order created successfully: {}", order.getOrderNumber());
        return new OrderResponse(order);
    }

    /**
     * Get order by ID
     */
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(UUID orderId) {
        logger.debug("Fetching order by ID: {}", orderId);
        
        User currentUser = getCurrentUser();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderId));
        
        // Verify ownership
        if (!order.getCompany().getId().equals(currentUser.getCompany().getId())) {
            throw new IllegalArgumentException("Order does not belong to your company");
        }
        
        return new OrderResponse(order);
    }

    /**
     * Get order by order number
     */
    @Transactional(readOnly = true)
    public OrderResponse getOrderByNumber(String orderNumber) {
        logger.debug("Fetching order by number: {}", orderNumber);
        
        User currentUser = getCurrentUser();
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with number: " + orderNumber));
        
        // Verify ownership
        if (!order.getCompany().getId().equals(currentUser.getCompany().getId())) {
            throw new IllegalArgumentException("Order does not belong to your company");
        }
        
        return new OrderResponse(order);
    }

    /**
     * Get orders by company (current user's company)
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByCompany() {
        User currentUser = getCurrentUser();
        logger.debug("Fetching orders for company: {}", currentUser.getCompany().getId());
        
        List<Order> orders = orderRepository.findByCompanyId(currentUser.getCompany().getId());
        return orders.stream()
                .map(OrderResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * Get orders by company with pagination
     */
    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrdersByCompany(Pageable pageable) {
        User currentUser = getCurrentUser();
        logger.debug("Fetching orders for company with pagination: {}, {}", currentUser.getCompany().getId(), pageable);
        
        Page<Order> orders = orderRepository.findByCompanyId(currentUser.getCompany().getId(), pageable);
        return orders.map(OrderResponse::new);
    }

    /**
     * Get orders by status
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByStatus(Order.OrderStatus status) {
        User currentUser = getCurrentUser();
        logger.debug("Fetching orders by status: {} for company: {}", status, currentUser.getCompany().getId());
        
        List<Order> orders = orderRepository.findByCompanyIdAndStatus(currentUser.getCompany().getId(), status);
        return orders.stream()
                .map(OrderResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * Get pending orders
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> getPendingOrders() {
        User currentUser = getCurrentUser();
        logger.debug("Fetching pending orders for company: {}", currentUser.getCompany().getId());
        
        List<Order> orders = orderRepository.findPendingOrdersByCompany(currentUser.getCompany().getId());
        return orders.stream()
                .map(OrderResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * Cancel order
     */
    public OrderResponse cancelOrder(UUID orderId) {
        logger.info("Cancelling order: {}", orderId);
        
        User currentUser = getCurrentUser();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderId));
        
        // Verify ownership
        if (!order.getCompany().getId().equals(currentUser.getCompany().getId())) {
            throw new IllegalArgumentException("Order does not belong to your company");
        }
        
        if (!order.canCancel()) {
            throw new IllegalArgumentException("Order cannot be cancelled in current status: " + order.getStatus());
        }
        
        order.cancel();
        order = orderRepository.save(order);
        
        logger.info("Order cancelled successfully: {}", order.getOrderNumber());
        return new OrderResponse(order);
    }

    /**
     * Search orders with multiple criteria
     */
    @Transactional(readOnly = true)
    public Page<OrderResponse> searchOrders(Order.OrderStatus status, Order.OrderType orderType,
                                           UUID serviceId, LocalDate startDate, LocalDate endDate,
                                           BigDecimal minCost, BigDecimal maxCost, Pageable pageable) {
        User currentUser = getCurrentUser();
        logger.debug("Searching orders with criteria for company: {}", currentUser.getCompany().getId());

        Page<Order> orders = orderRepository.searchOrders(currentUser.getCompany().getId(),
                                                         status, pageable);

        // Initialize lazy relationships within the transaction
        return orders.map(order -> {
            try {
                // Force initialization of lazy relationships within the transaction
                if (order.getService() != null) {
                    order.getService().getName(); // Initialize service
                }
                if (order.getUser() != null) {
                    order.getUser().getUsername(); // Initialize user
                }
                if (order.getCompany() != null) {
                    order.getCompany().getName(); // Initialize company
                }
                if (order.getServiceInstance() != null) {
                    order.getServiceInstance().getInstanceName(); // Initialize service instance
                }
            } catch (Exception e) {
                logger.warn("Could not initialize lazy relationships for order {}: {}", order.getId(), e.getMessage());
                // Continue with OrderResponse creation - it handles null relationships gracefully
            }
            return new OrderResponse(order);
        });
    }

    /**
     * Get recent orders
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> getRecentOrders(int limit) {
        User currentUser = getCurrentUser();
        logger.debug("Fetching recent orders for company: {} with limit: {}", currentUser.getCompany().getId(), limit);
        
        Pageable pageable = Pageable.ofSize(limit);
        List<Order> orders = orderRepository.findRecentOrdersByCompany(currentUser.getCompany().getId(), pageable);
        return orders.stream()
                .map(OrderResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * Calculate total order value for company
     */
    @Transactional(readOnly = true)
    public BigDecimal getTotalOrderValue() {
        User currentUser = getCurrentUser();
        logger.debug("Calculating total order value for company: {}", currentUser.getCompany().getId());
        
        return orderRepository.calculateTotalOrderValueByCompany(currentUser.getCompany().getId());
    }

    // Private helper methods

    private User getCurrentUser() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Ensure the user and company relationship are properly loaded
        if (user.getCompany() == null) {
            // Reload user with company relationship if not loaded
            user = userRepository.findByIdWithCompany(user.getId())
                    .orElseThrow(() -> new IllegalStateException("Current user not found in database"));
        }

        return user;
    }

    private String generateOrderNumber() {
        Integer nextSequence = orderRepository.findNextOrderSequence();
        return String.format("ORD-%06d", nextSequence);
    }

    private void calculateOrderDetails(Order order, com.singtel.network.entity.Service service) {
        // Calculate total cost
        if (order.getOrderType() == Order.OrderType.NEW_SERVICE) {
            BigDecimal monthlyCost = service.calculateMonthlyCost(order.getRequestedBandwidthMbps());
            BigDecimal setupFee = service.getSetupFee() != null ? service.getSetupFee() : BigDecimal.ZERO;
            order.setTotalCost(monthlyCost.add(setupFee));
        } else if (order.getOrderType() == Order.OrderType.MODIFY_SERVICE) {
            // For modifications, calculate the difference in monthly cost
            ServiceInstance serviceInstance = order.getServiceInstance();
            BigDecimal currentCost = service.calculateMonthlyCost(serviceInstance.getCurrentBandwidthMbps());
            BigDecimal newCost = service.calculateMonthlyCost(order.getRequestedBandwidthMbps());
            order.setTotalCost(newCost.subtract(currentCost));
        } else {
            // Terminate service - no additional cost
            order.setTotalCost(BigDecimal.ZERO);
        }

        // Calculate estimated completion date
        LocalDate requestedDate = order.getRequestedDate() != null ? order.getRequestedDate() : LocalDate.now().plusDays(1);
        int provisioningHours = service.getProvisioningTimeHours() != null ? service.getProvisioningTimeHours() : 24;
        order.setEstimatedCompletionDate(requestedDate.plusDays(provisioningHours / 24));
    }
}
