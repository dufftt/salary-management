package com.duft.salary_management.Repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.duft.salary_management.Entity.Employee;
import com.duft.salary_management.Enums.EmployeeStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface EmployeeRepository extends JpaRepository<Employee, UUID>,
                                            JpaSpecificationExecutor<Employee> {

    boolean existsByEmployeeId(String employeeId);

    boolean existsByEmail(String email);

    // ==================== Analytics – Summary ====================

    @Query("""
    SELECT COUNT(e),
           COALESCE(SUM(e.annualSalaryUsd), 0),
           COALESCE(AVG(e.annualSalaryUsd), 0),
           COALESCE(MIN(e.annualSalaryUsd), 0),
           COALESCE(MAX(e.annualSalaryUsd), 0)
    FROM Employee e
    WHERE (:status IS NULL OR e.status = :status)
    """)
List<Object[]> getSummaryAggregates(@Param("status") EmployeeStatus status);

    // Query for median salary calculation using database-agnostic JPQL pagination
    @Query("SELECT e.annualSalaryUsd FROM Employee e WHERE (:status IS NULL OR e.status = :status) ORDER BY e.annualSalaryUsd ASC")
    List<BigDecimal> getSalaryAtOffset(@Param("status") EmployeeStatus status, org.springframework.data.domain.Pageable pageable);

    // ==================== Analytics – Breakdowns ====================

    @Query("""
        SELECT e.country, 
               AVG(e.annualSalaryUsd), 
               SUM(e.annualSalaryUsd), 
               COUNT(e)
        FROM Employee e
        WHERE (:status IS NULL OR e.status = :status)
        GROUP BY e.country
        ORDER BY AVG(e.annualSalaryUsd) DESC
        """)
    List<Object[]> getAverageSalaryByCountry(@Param("status") EmployeeStatus status);

    @Query("""
        SELECT e.department, 
               AVG(e.annualSalaryUsd), 
               SUM(e.annualSalaryUsd), 
               COUNT(e)
        FROM Employee e
        WHERE (:status IS NULL OR e.status = :status)
        GROUP BY e.department
        ORDER BY AVG(e.annualSalaryUsd) DESC
        """)
    List<Object[]> getAverageSalaryByDepartment(@Param("status") EmployeeStatus status);

    @Query("""
        SELECT e.jobLevel, 
               AVG(e.annualSalaryUsd), 
               SUM(e.annualSalaryUsd), 
               COUNT(e)
        FROM Employee e
        WHERE (:status IS NULL OR e.status = :status)
        GROUP BY e.jobLevel
        ORDER BY AVG(e.annualSalaryUsd) DESC
        """)
    List<Object[]> getAverageSalaryByJobLevel(@Param("status") EmployeeStatus status);

    // ==================== Salary Distribution ====================

    @Query("""
        SELECT 
            CASE
                WHEN e.annualSalaryUsd < 30000 THEN '0 - 30k'
                WHEN e.annualSalaryUsd < 50000 THEN '30k - 50k'
                WHEN e.annualSalaryUsd < 80000 THEN '50k - 80k'
                WHEN e.annualSalaryUsd < 120000 THEN '80k - 120k'
                WHEN e.annualSalaryUsd < 200000 THEN '120k - 200k'
                ELSE '200k+'
            END,
            COUNT(e)
        FROM Employee e
        WHERE (:status IS NULL OR e.status = :status)
        GROUP BY 
            CASE
                WHEN e.annualSalaryUsd < 30000 THEN '0 - 30k'
                WHEN e.annualSalaryUsd < 50000 THEN '30k - 50k'
                WHEN e.annualSalaryUsd < 80000 THEN '50k - 80k'
                WHEN e.annualSalaryUsd < 120000 THEN '80k - 120k'
                WHEN e.annualSalaryUsd < 200000 THEN '120k - 200k'
                ELSE '200k+'
            END
        ORDER BY MIN(e.annualSalaryUsd) ASC
        """)
    List<Object[]> getSalaryDistribution(@Param("status") EmployeeStatus status);
}