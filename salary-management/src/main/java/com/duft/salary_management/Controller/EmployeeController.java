package com.duft.salary_management.Controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.duft.salary_management.DTO.EmployeeCreateRequest;
import com.duft.salary_management.DTO.EmployeeResponse;
import com.duft.salary_management.DTO.EmployeeUpdateRequest;
import com.duft.salary_management.DTO.PageResponse;
import com.duft.salary_management.Enums.Country;
import com.duft.salary_management.Enums.Department;
import com.duft.salary_management.Enums.EmployeeStatus;
import com.duft.salary_management.Enums.JobLevel;
import com.duft.salary_management.Service.EmployeeService;

import java.util.UUID;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // ==================== Create ====================

    @PostMapping
    public ResponseEntity<EmployeeResponse> createEmployee(
            @Valid @RequestBody EmployeeCreateRequest request
    ) {
        EmployeeResponse response = employeeService.createEmployee(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ==================== Get by ID ====================

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> getEmployee(@PathVariable UUID id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    // ==================== Update ====================

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponse> updateEmployee(
            @PathVariable UUID id,
            @Valid @RequestBody EmployeeUpdateRequest request
    ) {
        return ResponseEntity.ok(employeeService.updateEmployee(id, request));
    }

    // ==================== Soft Delete (Deactivate) ====================

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<EmployeeResponse> deactivateEmployee(@PathVariable UUID id) {
        return ResponseEntity.ok(employeeService.deactivateEmployee(id));
    }

    // ==================== Search + Filter + Pagination ====================

    @GetMapping
    public ResponseEntity<PageResponse<EmployeeResponse>> searchEmployees(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Country country,
            @RequestParam(required = false) Department department,
            @RequestParam(required = false) JobLevel jobLevel,
            @RequestParam(required = false) EmployeeStatus status,
            @PageableDefault(size = 20, sort = "fullName", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        PageResponse<EmployeeResponse> response = employeeService.searchEmployees(
                search, country, department, jobLevel, status, pageable
        );
        return ResponseEntity.ok(response);
    }
}
