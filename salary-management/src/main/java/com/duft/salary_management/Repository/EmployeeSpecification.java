package com.duft.salary_management.Repository;


import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.duft.salary_management.Entity.Employee;
import com.duft.salary_management.Enums.Country;
import com.duft.salary_management.Enums.Department;
import com.duft.salary_management.Enums.EmployeeStatus;
import com.duft.salary_management.Enums.JobLevel;

public class EmployeeSpecification {

    private EmployeeSpecification() {}

    public static Specification<Employee> withFilters(
            String search,
            Country country,
            Department department,
            JobLevel jobLevel,
            EmployeeStatus status
    ) {
        return Specification
                .where(hasSearch(search))
                .and(hasCountry(country))
                .and(hasDepartment(department))
                .and(hasJobLevel(jobLevel))
                .and(hasStatus(status));
    }

    private static Specification<Employee> hasSearch(String search) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(search)) {
                return cb.conjunction();
            }
            String pattern = "%" + search.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("fullName")), pattern),
                    cb.like(cb.lower(root.get("employeeId")), pattern),
                    cb.like(cb.lower(root.get("email")), pattern)
            );
        };
    }

    private static Specification<Employee> hasCountry(Country country) {
        return (root, query, cb) ->
                country == null ? cb.conjunction() : cb.equal(root.get("country"), country);
    }

    private static Specification<Employee> hasDepartment(Department department) {
        return (root, query, cb) ->
                department == null ? cb.conjunction() : cb.equal(root.get("department"), department);
    }

    private static Specification<Employee> hasJobLevel(JobLevel jobLevel) {
        return (root, query, cb) ->
                jobLevel == null ? cb.conjunction() : cb.equal(root.get("jobLevel"), jobLevel);
    }

    private static Specification<Employee> hasStatus(EmployeeStatus status) {
        return (root, query, cb) ->
                status == null ? cb.conjunction() : cb.equal(root.get("status"), status);
    }
}
