package com.singtel.network.service;

import com.singtel.network.dto.order.CreateOrderRequest;
import com.singtel.network.dto.order.OrderResponse;
import com.singtel.network.entity.*;
import com.singtel.network.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for OrderService.
 */
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ServiceRepository serviceRepository;

    @Mock
    private ServiceInstanceRepository serviceInstanceRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ServiceCatalogService serviceCatalogService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private OrderService orderService;

    private User testUser;
    private Company testCompany;
    private Service testService;
    private ServiceInstance testServiceInstance;
    private Order testOrder;
    private CreateOrderRequest createOrderRequest;

    @BeforeEach
    void setUp() {
        // Create test company
        testCompany = new Company();
        testCompany.setId(UUID.randomUUID());
        testCompany.setName("Test Company");
        testCompany.setRegistrationNumber("TEST123");
        testCompany.setEmail("test@company.com");
        testCompany.setStatus(Company.CompanyStatus.ACTIVE);

        // Create test user
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setRole(User.UserRole.USER);
        testUser.setStatus(User.UserStatus.ACTIVE);
        testUser.setCompany(testCompany);

        // Create test service
        testService = new Service();
        testService.setId(UUID.randomUUID());
        testService.setName("Business Fiber 500M");
        testService.setServiceType("FIBER");
        testService.setBaseBandwidthMbps(500);
        testService.setMaxBandwidthMbps(1000);
        testService.setMinBandwidthMbps(100);
        testService.setBasePriceMonthly(new BigDecimal("299.00"));
        testService.setPricePerMbps(new BigDecimal("0.50"));
        testService.setSetupFee(new BigDecimal("150.00"));
        testService.setIsAvailable(true);
        testService.setProvisioningTimeHours(72);

        // Create test service instance
        testServiceInstance = new ServiceInstance();
        testServiceInstance.setId(UUID.randomUUID());
        testServiceInstance.setCompany(testCompany);
        testServiceInstance.setService(testService);
        testServiceInstance.setInstanceName("Test Instance");
        testServiceInstance.setCurrentBandwidthMbps(500);
        testServiceInstance.setStatus(ServiceInstance.ServiceInstanceStatus.ACTIVE);

        // Create test order
        testOrder = new Order();
        testOrder.setId(UUID.randomUUID());
        testOrder.setCompany(testCompany);
        testOrder.setUser(testUser);
        testOrder.setService(testService);
        testOrder.setOrderNumber("ORD-000001");
        testOrder.setOrderType(Order.OrderType.NEW_SERVICE);
        testOrder.setRequestedBandwidthMbps(500);
        testOrder.setStatus(Order.OrderStatus.SUBMITTED);
        testOrder.setTotalCost(new BigDecimal("449.00"));

        // Create order request
        createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setServiceId(testService.getId());
        createOrderRequest.setOrderType(Order.OrderType.NEW_SERVICE);
        createOrderRequest.setRequestedBandwidthMbps(500);
        createOrderRequest.setInstallationAddress("123 Test Street");
        createOrderRequest.setPostalCode("123456");
        createOrderRequest.setContactPerson("Test Person");
        createOrderRequest.setContactPhone("+65 9123 4567");
        createOrderRequest.setContactEmail("test@example.com");
        createOrderRequest.setRequestedDate(LocalDate.now().plusDays(1));
    }

    @Test
    void createOrder_NewService_Success() {
        // Arrange
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(testUser);

            when(serviceRepository.findById(testService.getId())).thenReturn(Optional.of(testService));
            when(orderRepository.findNextOrderSequence()).thenReturn(1);
            when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

            // Act
            OrderResponse result = orderService.createOrder(createOrderRequest);

            // Assert
            assertNotNull(result);
            assertEquals(testOrder.getOrderNumber(), result.getOrderNumber());
            assertEquals(testOrder.getOrderType(), result.getOrderType());
            assertEquals(testOrder.getStatus(), result.getStatus());
            assertEquals(testOrder.getRequestedBandwidthMbps(), result.getRequestedBandwidthMbps());
            assertEquals(testOrder.getTotalCost(), result.getTotalCost());

            verify(serviceRepository).findById(testService.getId());
            verify(orderRepository).findNextOrderSequence();
            verify(orderRepository).save(any(Order.class));
        }
    }

    @Test
    void createOrder_ServiceNotFound() {
        // Arrange
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(testUser);

            when(serviceRepository.findById(testService.getId())).thenReturn(Optional.empty());

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                orderService.createOrder(createOrderRequest);
            });

            assertEquals("Service not found with ID: " + testService.getId(), exception.getMessage());
            verify(serviceRepository).findById(testService.getId());
            verifyNoInteractions(orderRepository);
        }
    }

    @Test
    void createOrder_ServiceNotAvailable() {
        // Arrange
        testService.setIsAvailable(false);
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(testUser);

            when(serviceRepository.findById(testService.getId())).thenReturn(Optional.of(testService));

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                orderService.createOrder(createOrderRequest);
            });

            assertEquals("Service is not available: " + testService.getId(), exception.getMessage());
            verify(serviceRepository).findById(testService.getId());
            verifyNoInteractions(orderRepository);
        }
    }

    @Test
    void createOrder_InvalidBandwidth() {
        // Arrange
        createOrderRequest.setRequestedBandwidthMbps(50); // Below minimum
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(testUser);

            when(serviceRepository.findById(testService.getId())).thenReturn(Optional.of(testService));

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                orderService.createOrder(createOrderRequest);
            });

            assertEquals("Invalid bandwidth for service: 50 Mbps", exception.getMessage());
            verify(serviceRepository).findById(testService.getId());
            verifyNoInteractions(orderRepository);
        }
    }

    @Test
    void createOrder_ModifyService_Success() {
        // Arrange
        createOrderRequest.setOrderType(Order.OrderType.MODIFY_SERVICE);
        createOrderRequest.setServiceInstanceId(testServiceInstance.getId());
        createOrderRequest.setRequestedBandwidthMbps(750);

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(testUser);

            when(serviceRepository.findById(testService.getId())).thenReturn(Optional.of(testService));
            when(serviceInstanceRepository.findById(testServiceInstance.getId())).thenReturn(Optional.of(testServiceInstance));
            when(orderRepository.findNextOrderSequence()).thenReturn(1);
            when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

            // Act
            OrderResponse result = orderService.createOrder(createOrderRequest);

            // Assert
            assertNotNull(result);
            verify(serviceRepository).findById(testService.getId());
            verify(serviceInstanceRepository).findById(testServiceInstance.getId());
            verify(orderRepository).save(any(Order.class));
        }
    }

    @Test
    void createOrder_ModifyService_ServiceInstanceNotFound() {
        // Arrange
        createOrderRequest.setOrderType(Order.OrderType.MODIFY_SERVICE);
        createOrderRequest.setServiceInstanceId(UUID.randomUUID());

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(testUser);

            when(serviceRepository.findById(testService.getId())).thenReturn(Optional.of(testService));
            when(serviceInstanceRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                orderService.createOrder(createOrderRequest);
            });

            assertTrue(exception.getMessage().startsWith("Service instance not found:"));
            verify(serviceRepository).findById(testService.getId());
            verify(serviceInstanceRepository).findById(any(UUID.class));
            verifyNoInteractions(orderRepository);
        }
    }

    @Test
    void getOrderById_Success() {
        // Arrange
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(testUser);

            when(orderRepository.findById(testOrder.getId())).thenReturn(Optional.of(testOrder));

            // Act
            OrderResponse result = orderService.getOrderById(testOrder.getId());

            // Assert
            assertNotNull(result);
            assertEquals(testOrder.getId(), result.getId());
            assertEquals(testOrder.getOrderNumber(), result.getOrderNumber());
            assertEquals(testOrder.getOrderType(), result.getOrderType());
            assertEquals(testOrder.getStatus(), result.getStatus());

            verify(orderRepository).findById(testOrder.getId());
        }
    }

    @Test
    void getOrderById_NotFound() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(testUser);

            when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                orderService.getOrderById(orderId);
            });

            assertEquals("Order not found with ID: " + orderId, exception.getMessage());
            verify(orderRepository).findById(orderId);
        }
    }

    @Test
    void getOrdersByCompany_Success() {
        // Arrange
        List<Order> orders = Arrays.asList(testOrder);
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(testUser);

            when(orderRepository.findByCompanyId(testCompany.getId())).thenReturn(orders);

            // Act
            List<OrderResponse> result = orderService.getOrdersByCompany();

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            OrderResponse response = result.get(0);
            assertEquals(testOrder.getId(), response.getId());
            assertEquals(testOrder.getOrderNumber(), response.getOrderNumber());

            verify(orderRepository).findByCompanyId(testCompany.getId());
        }
    }

    @Test
    void cancelOrder_Success() {
        // Arrange
        testOrder.setStatus(Order.OrderStatus.SUBMITTED);
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(testUser);

            when(orderRepository.findById(testOrder.getId())).thenReturn(Optional.of(testOrder));
            when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

            // Act
            OrderResponse result = orderService.cancelOrder(testOrder.getId());

            // Assert
            assertNotNull(result);
            assertEquals(Order.OrderStatus.CANCELLED, testOrder.getStatus());

            verify(orderRepository).findById(testOrder.getId());
            verify(orderRepository).save(testOrder);
        }
    }

    @Test
    void cancelOrder_CannotCancel() {
        // Arrange
        testOrder.setStatus(Order.OrderStatus.COMPLETED);
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(testUser);

            when(orderRepository.findById(testOrder.getId())).thenReturn(Optional.of(testOrder));

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                orderService.cancelOrder(testOrder.getId());
            });

            assertEquals("Order cannot be cancelled in current status: COMPLETED", exception.getMessage());
            verify(orderRepository).findById(testOrder.getId());
            verify(orderRepository, never()).save(any(Order.class));
        }
    }

    @Test
    void getTotalOrderValue_Success() {
        // Arrange
        BigDecimal totalValue = new BigDecimal("1500.00");
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(testUser);

            when(orderRepository.calculateTotalOrderValueByCompany(testCompany.getId())).thenReturn(totalValue);

            // Act
            BigDecimal result = orderService.getTotalOrderValue();

            // Assert
            assertNotNull(result);
            assertEquals(totalValue, result);

            verify(orderRepository).calculateTotalOrderValueByCompany(testCompany.getId());
        }
    }
}
