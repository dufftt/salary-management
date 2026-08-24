package com.duft.salary_management.Service;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.duft.salary_management.DTO.EmployeeCreateRequest;
import com.duft.salary_management.DTO.EmployeeResponse;
import com.duft.salary_management.DTO.EmployeeUpdateRequest;
import com.duft.salary_management.DTO.PageResponse;
import com.duft.salary_management.Entity.Employee;
import com.duft.salary_management.Enums.Country;
import com.duft.salary_management.Enums.Department;
import com.duft.salary_management.Enums.EmployeeStatus;
import com.duft.salary_management.Enums.JobLevel;
import com.duft.salary_management.Exceptions.DuplicateResourceException;
import com.duft.salary_management.Exceptions.ResourceNotFoundException;
import com.duft.salary_management.Mapper.EmployeeMapper;
import com.duft.salary_management.Repository.EmployeeRepository;
import com.duft.salary_management.Repository.EmployeeSpecification;
import com.duft.salary_management.Utility.CurrencyConverter;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    public EmployeeService(EmployeeRepository employeeRepository, EmployeeMapper employeeMapper) {
        this.employeeRepository = employeeRepository;
        this.employeeMapper = employeeMapper;
    }

    public EmployeeResponse createEmployee(EmployeeCreateRequest request) {
        if (employeeRepository.existsByEmployeeId(request.employeeId())) {
            throw new DuplicateResourceException("Employee ID already exists: " + request.employeeId());
        }
        if (employeeRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Email already exists: " + request.email());
        }

        Employee employee = employeeMapper.toEntity(request);
        employee.setStatus(EmployeeStatus.ACTIVE);
        employee.setAnnualSalaryUsd(CurrencyConverter.toUsd(request.annualSalary(), request.currency()));

        return employeeMapper.toResponse(employeeRepository.save(employee));
    }

    public EmployeeResponse updateEmployee(UUID id, EmployeeUpdateRequest request) {
        Employee employee = findEmployeeById(id);

        if (!employee.getEmail().equals(request.email()) &&
                employeeRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Email already exists: " + request.email());
        }

        employeeMapper.updateEntity(request, employee);
        employee.setAnnualSalaryUsd(CurrencyConverter.toUsd(request.annualSalary(), request.currency()));

        return employeeMapper.toResponse(employeeRepository.save(employee));
    }

    @Transactional
    public EmployeeResponse getEmployeeById(UUID id) {
        return employeeMapper.toResponse(findEmployeeById(id));
    }

    public EmployeeResponse deactivateEmployee(UUID id) {
        Employee employee = findEmployeeById(id);
        employee.setStatus(EmployeeStatus.INACTIVE);
        return employeeMapper.toResponse(employeeRepository.save(employee));
    }

    @Transactional
    public PageResponse<EmployeeResponse> searchEmployees(
            String search,
            Country country,
            Department department,
            JobLevel jobLevel,
            EmployeeStatus status,
            Pageable pageable
    ) {
        Specification<Employee> spec = EmployeeSpecification.withFilters(
                search, country, department, jobLevel, status
        );

        Page<Employee> page = employeeRepository.findAll(spec, pageable);
        return employeeMapper.toPageResponse(page);
    }

    private Employee findEmployeeById(UUID id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
    }
}