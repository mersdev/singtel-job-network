package com.singtel.network.repository;

import com.singtel.network.entity.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Company entity operations.
 */
@Repository
public interface CompanyRepository extends JpaRepository<Company, UUID> {

    /**
     * Find company by registration number
     */
    Optional<Company> findByRegistrationNumber(String registrationNumber);

    /**
     * Find company by email
     */
    Optional<Company> findByEmail(String email);

    /**
     * Check if registration number exists
     */
    boolean existsByRegistrationNumber(String registrationNumber);

    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);

    /**
     * Find companies by status
     */
    List<Company> findByStatus(Company.CompanyStatus status);

    /**
     * Find companies by status with pagination
     */
    Page<Company> findByStatus(Company.CompanyStatus status, Pageable pageable);

    /**
     * Find companies by industry
     */
    List<Company> findByIndustry(String industry);

    /**
     * Find companies by company size
     */
    List<Company> findByCompanySize(String companySize);

    /**
     * Find companies by country
     */
    List<Company> findByCountry(String country);

    /**
     * Search companies by name
     */
    @Query("SELECT c FROM Company c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Company> findByNameContainingIgnoreCase(@Param("name") String name);

    /**
     * Search companies by name with pagination
     */
    @Query("SELECT c FROM Company c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Company> findByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);

    /**
     * Find active companies
     */
    @Query("SELECT c FROM Company c WHERE c.status = 'ACTIVE'")
    List<Company> findActiveCompanies();

    /**
     * Find active companies with pagination
     */
    @Query("SELECT c FROM Company c WHERE c.status = 'ACTIVE'")
    Page<Company> findActiveCompanies(Pageable pageable);

    /**
     * Search companies by multiple criteria
     */
    @Query("SELECT c FROM Company c WHERE " +
           "(:name IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:industry IS NULL OR c.industry = :industry) AND " +
           "(:companySize IS NULL OR c.companySize = :companySize) AND " +
           "(:status IS NULL OR c.status = :status)")
    Page<Company> searchCompanies(@Param("name") String name,
                                 @Param("industry") String industry,
                                 @Param("companySize") String companySize,
                                 @Param("status") Company.CompanyStatus status,
                                 Pageable pageable);

    /**
     * Count companies by status
     */
    long countByStatus(Company.CompanyStatus status);

    /**
     * Count companies by industry
     */
    long countByIndustry(String industry);

    /**
     * Get distinct industries
     */
    @Query("SELECT DISTINCT c.industry FROM Company c WHERE c.industry IS NOT NULL ORDER BY c.industry")
    List<String> findDistinctIndustries();

    /**
     * Get distinct company sizes
     */
    @Query("SELECT DISTINCT c.companySize FROM Company c WHERE c.companySize IS NOT NULL ORDER BY c.companySize")
    List<String> findDistinctCompanySizes();

    /**
     * Find companies with service instances
     */
    @Query("SELECT DISTINCT c FROM Company c JOIN c.serviceInstances si WHERE si.status = 'ACTIVE'")
    List<Company> findCompaniesWithActiveServices();

    /**
     * Find companies without any service instances
     */
    @Query("SELECT c FROM Company c WHERE c.serviceInstances IS EMPTY")
    List<Company> findCompaniesWithoutServices();
}
