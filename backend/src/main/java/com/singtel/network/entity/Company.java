package com.singtel.network.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

/**
 * Company entity representing business entities that subscribe to network services.
 */
@Entity
@Table(name = "companies", schema = "singtel_app")
public class Company extends BaseEntity {

    @NotBlank(message = "Company name is required")
    @Size(max = 255, message = "Company name must not exceed 255 characters")
    @Column(name = "name", nullable = false)
    private String name;

    @NotBlank(message = "Registration number is required")
    @Size(max = 100, message = "Registration number must not exceed 100 characters")
    @Column(name = "registration_number", nullable = false, unique = true)
    private String registrationNumber;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    @Column(name = "email", nullable = false)
    private String email;

    @Size(max = 50, message = "Phone must not exceed 50 characters")
    @Column(name = "phone")
    private String phone;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Size(max = 20, message = "Postal code must not exceed 20 characters")
    @Column(name = "postal_code")
    private String postalCode;

    @Size(max = 100, message = "Country must not exceed 100 characters")
    @Column(name = "country")
    private String country = "Singapore";

    @Size(max = 100, message = "Industry must not exceed 100 characters")
    @Column(name = "industry")
    private String industry;

    @Size(max = 50, message = "Company size must not exceed 50 characters")
    @Column(name = "company_size")
    private String companySize;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CompanyStatus status = CompanyStatus.ACTIVE;

    // Relationships
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<User> users = new ArrayList<>();

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ServiceInstance> serviceInstances = new ArrayList<>();

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Order> orders = new ArrayList<>();

    // Constructors
    public Company() {
    }

    public Company(String name, String registrationNumber, String email) {
        this.name = name;
        this.registrationNumber = registrationNumber;
        this.email = email;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getCompanySize() {
        return companySize;
    }

    public void setCompanySize(String companySize) {
        this.companySize = companySize;
    }

    public CompanyStatus getStatus() {
        return status;
    }

    public void setStatus(CompanyStatus status) {
        this.status = status;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<ServiceInstance> getServiceInstances() {
        return serviceInstances;
    }

    public void setServiceInstances(List<ServiceInstance> serviceInstances) {
        this.serviceInstances = serviceInstances;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    // Helper methods
    public void addUser(User user) {
        users.add(user);
        user.setCompany(this);
    }

    public void removeUser(User user) {
        users.remove(user);
        user.setCompany(null);
    }

    public void addServiceInstance(ServiceInstance serviceInstance) {
        serviceInstances.add(serviceInstance);
        serviceInstance.setCompany(this);
    }

    public void removeServiceInstance(ServiceInstance serviceInstance) {
        serviceInstances.remove(serviceInstance);
        serviceInstance.setCompany(null);
    }

    public void addOrder(Order order) {
        orders.add(order);
        order.setCompany(this);
    }

    public void removeOrder(Order order) {
        orders.remove(order);
        order.setCompany(null);
    }

    @Override
    public String toString() {
        return "Company{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", registrationNumber='" + registrationNumber + '\'' +
                ", email='" + email + '\'' +
                ", status=" + status +
                '}';
    }

    /**
     * Company status enumeration
     */
    public enum CompanyStatus {
        ACTIVE, SUSPENDED, INACTIVE
    }
}
