package com.duft.salary_management.Mapper;


import org.mapstruct.*;
import org.springframework.data.domain.Page;

import com.duft.salary_management.DTO.EmployeeCreateRequest;
import com.duft.salary_management.DTO.EmployeeResponse;
import com.duft.salary_management.DTO.EmployeeUpdateRequest;
import com.duft.salary_management.DTO.PageResponse;
import com.duft.salary_management.Entity.Employee;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    // ==================== Create ====================

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "annualSalaryUsd", ignore = true)   // calculated in service
    @Mapping(target = "status", ignore = true)             // set to ACTIVE in service
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Employee toEntity(EmployeeCreateRequest request);

    // ==================== Update ====================

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employeeId", ignore = true)         // not updatable
    @Mapping(target = "annualSalaryUsd", ignore = true)    // recalculated in service
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(EmployeeUpdateRequest request, @MappingTarget Employee employee);

    // ==================== Response ====================

    EmployeeResponse toResponse(Employee employee);

    List<EmployeeResponse> toResponseList(List<Employee> employees);

    // ==================== Pagination ====================

    default PageResponse<EmployeeResponse> toPageResponse(Page<Employee> page) {
        List<EmployeeResponse> content = toResponseList(page.getContent());

        return new PageResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }
}
