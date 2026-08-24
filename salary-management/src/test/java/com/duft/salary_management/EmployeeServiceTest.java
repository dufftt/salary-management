package com.duft.salary_management;

import com.duft.salary_management.DTO.EmployeeCreateRequest;
import com.duft.salary_management.DTO.EmployeeResponse;
import com.duft.salary_management.DTO.EmployeeUpdateRequest;
import com.duft.salary_management.DTO.PageResponse;
import com.duft.salary_management.Entity.Employee;
import com.duft.salary_management.Enums.Country;
import com.duft.salary_management.Enums.Currency;
import com.duft.salary_management.Enums.Department;
import com.duft.salary_management.Enums.EmployeeStatus;
import com.duft.salary_management.Enums.JobLevel;
import com.duft.salary_management.Exceptions.DuplicateResourceException;
import com.duft.salary_management.Exceptions.ResourceNotFoundException;
import com.duft.salary_management.Mapper.EmployeeMapper;
import com.duft.salary_management.Repository.EmployeeRepository;
import com.duft.salary_management.Service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private EmployeeMapper employeeMapper;

    @InjectMocks
    private EmployeeService employeeService;

    private Employee employee;
    private EmployeeResponse employeeResponse;
    private UUID employeeId;

    @BeforeEach
    void setUp() {
        employeeId = UUID.randomUUID();
        employee = new Employee();
        employee.setId(employeeId);
        employee.setEmployeeId("ACME-00001");
        employee.setFullName("John Doe");
        employee.setEmail("john.doe@acme.com");
        employee.setDepartment(Department.ENGINEERING);
        employee.setJobTitle("Software Engineer");
        employee.setJobLevel(JobLevel.L3);
        employee.setCountry(Country.UNITED_STATES);
        employee.setCurrency(Currency.USD);
        employee.setAnnualSalary(new BigDecimal("100000.00"));
        employee.setAnnualSalaryUsd(new BigDecimal("100000.00"));
        employee.setStatus(EmployeeStatus.ACTIVE);
        employee.setDateOfJoining(LocalDate.of(2023, 1, 15));

        employeeResponse = new EmployeeResponse(
                employeeId, "ACME-00001", "John Doe", "john.doe@acme.com",
                Department.ENGINEERING, "Software Engineer", JobLevel.L3,
                Country.UNITED_STATES, Currency.USD,
                new BigDecimal("100000.00"), new BigDecimal("100000.00"),
                EmployeeStatus.ACTIVE, LocalDate.of(2023, 1, 15),
                Instant.now(), Instant.now()
        );
    }

    @Test
    void testCreateEmployeeSuccess() {
        EmployeeCreateRequest request = new EmployeeCreateRequest(
                "ACME-00001", "John Doe", "john.doe@acme.com",
                Department.ENGINEERING, "Software Engineer", JobLevel.L3,
                Country.UNITED_STATES, Currency.USD,
                new BigDecimal("100000.00"), LocalDate.of(2023, 1, 15)
        );

        when(employeeRepository.existsByEmployeeId("ACME-00001")).thenReturn(false);
        when(employeeRepository.existsByEmail("john.doe@acme.com")).thenReturn(false);
        when(employeeMapper.toEntity(request)).thenReturn(employee);
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
        when(employeeMapper.toResponse(employee)).thenReturn(employeeResponse);

        EmployeeResponse result = employeeService.createEmployee(request);

        assertNotNull(result);
        assertEquals("ACME-00001", result.employeeId());
        verify(employeeRepository, times(1)).save(employee);
    }

    @Test
    void testCreateEmployeeDuplicateEmployeeIdThrowsException() {
        EmployeeCreateRequest request = new EmployeeCreateRequest(
                "ACME-00001", "John Doe", "john.doe@acme.com",
                Department.ENGINEERING, "Software Engineer", JobLevel.L3,
                Country.UNITED_STATES, Currency.USD,
                new BigDecimal("100000.00"), LocalDate.of(2023, 1, 15)
        );

        when(employeeRepository.existsByEmployeeId("ACME-00001")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> employeeService.createEmployee(request));
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void testCreateEmployeeDuplicateEmailThrowsException() {
        EmployeeCreateRequest request = new EmployeeCreateRequest(
                "ACME-00001", "John Doe", "john.doe@acme.com",
                Department.ENGINEERING, "Software Engineer", JobLevel.L3,
                Country.UNITED_STATES, Currency.USD,
                new BigDecimal("100000.00"), LocalDate.of(2023, 1, 15)
        );

        when(employeeRepository.existsByEmployeeId("ACME-00001")).thenReturn(false);
        when(employeeRepository.existsByEmail("john.doe@acme.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> employeeService.createEmployee(request));
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void testGetEmployeeByIdSuccess() {
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(employeeMapper.toResponse(employee)).thenReturn(employeeResponse);

        EmployeeResponse result = employeeService.getEmployeeById(employeeId);

        assertNotNull(result);
        assertEquals(employeeId, result.id());
    }

    @Test
    void testGetEmployeeByIdNotFoundThrowsException() {
        UUID nonExistentId = UUID.randomUUID();
        when(employeeRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> employeeService.getEmployeeById(nonExistentId));
    }

    @Test
    void testDeactivateEmployee() {
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
        when(employeeMapper.toResponse(employee)).thenReturn(employeeResponse);

        EmployeeResponse result = employeeService.deactivateEmployee(employeeId);

        assertNotNull(result);
        assertEquals(EmployeeStatus.INACTIVE, employee.getStatus());
        verify(employeeRepository, times(1)).save(employee);
    }

    @Test
    void testUpdateEmployeeSuccess() {
        EmployeeUpdateRequest request = new EmployeeUpdateRequest(
                "John Doe Updated", "john.updated@acme.com",
                Department.ENGINEERING, "Senior Software Engineer", JobLevel.L4,
                Country.UNITED_STATES, Currency.USD,
                new BigDecimal("120000.00"), LocalDate.of(2023, 1, 15), EmployeeStatus.ACTIVE
        );

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(employeeRepository.existsByEmail("john.updated@acme.com")).thenReturn(false);
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
        when(employeeMapper.toResponse(employee)).thenReturn(employeeResponse);

        EmployeeResponse result = employeeService.updateEmployee(employeeId, request);

        assertNotNull(result);
        verify(employeeMapper).updateEntity(request, employee);
        verify(employeeRepository).save(employee);
    }

    @Test
    void testSearchEmployees() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Employee> page = new PageImpl<>(List.of(employee), pageable, 1);
        PageResponse<EmployeeResponse> pageResponse = new PageResponse<>(List.of(employeeResponse), 0, 10, 1, 1, true, true);

        when(employeeRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        when(employeeMapper.toPageResponse(page)).thenReturn(pageResponse);

        PageResponse<EmployeeResponse> result = employeeService.searchEmployees("John", null, null, null, null, pageable);

        assertNotNull(result);
        assertEquals(1, result.totalElements());
        assertEquals(1, result.content().size());
    }
}
