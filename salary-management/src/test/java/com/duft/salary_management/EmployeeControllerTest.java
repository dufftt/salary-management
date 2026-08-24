package com.duft.salary_management;

import com.duft.salary_management.Controller.EmployeeController;
import com.duft.salary_management.DTO.EmployeeCreateRequest;
import com.duft.salary_management.DTO.EmployeeResponse;
import com.duft.salary_management.DTO.PageResponse;
import com.duft.salary_management.Enums.Country;
import com.duft.salary_management.Enums.Currency;
import com.duft.salary_management.Enums.Department;
import com.duft.salary_management.Enums.EmployeeStatus;
import com.duft.salary_management.Enums.JobLevel;
import com.duft.salary_management.Exceptions.GlobalExceptionHandler;
import com.duft.salary_management.Exceptions.ResourceNotFoundException;
import com.duft.salary_management.Service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EmployeeControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeController employeeController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(employeeController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void testCreateEmployeeSuccess() throws Exception {
        UUID id = UUID.randomUUID();
        EmployeeResponse response = new EmployeeResponse(
                id, "ACME-00001", "Jane Doe", "jane.doe@acme.com",
                Department.HUMAN_RESOURCES, "HR Specialist", JobLevel.L2,
                Country.UNITED_STATES, Currency.USD,
                new BigDecimal("65000.00"), new BigDecimal("65000.00"),
                EmployeeStatus.ACTIVE, LocalDate.of(2023, 5, 1),
                Instant.now(), Instant.now()
        );

        when(employeeService.createEmployee(any(EmployeeCreateRequest.class))).thenReturn(response);

        String json = """
                {
                    "employeeId": "ACME-00001",
                    "fullName": "Jane Doe",
                    "email": "jane.doe@acme.com",
                    "department": "HUMAN_RESOURCES",
                    "jobTitle": "HR Specialist",
                    "jobLevel": "L2",
                    "country": "UNITED_STATES",
                    "currency": "USD",
                    "annualSalary": 65000.00,
                    "dateOfJoining": "2023-05-01"
                }
                """;

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.employeeId").value("ACME-00001"))
                .andExpect(jsonPath("$.fullName").value("Jane Doe"));
    }

    @Test
    void testGetEmployeeByIdSuccess() throws Exception {
        UUID id = UUID.randomUUID();
        EmployeeResponse response = new EmployeeResponse(
                id, "ACME-00001", "Jane Doe", "jane.doe@acme.com",
                Department.HUMAN_RESOURCES, "HR Specialist", JobLevel.L2,
                Country.UNITED_STATES, Currency.USD,
                new BigDecimal("65000.00"), new BigDecimal("65000.00"),
                EmployeeStatus.ACTIVE, LocalDate.of(2023, 5, 1),
                Instant.now(), Instant.now()
        );

        when(employeeService.getEmployeeById(id)).thenReturn(response);

        mockMvc.perform(get("/api/employees/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.employeeId").value("ACME-00001"));
    }

    @Test
    void testGetEmployeeByIdNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(employeeService.getEmployeeById(id)).thenThrow(new ResourceNotFoundException("Not found"));

        mockMvc.perform(get("/api/employees/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeactivateEmployee() throws Exception {
        UUID id = UUID.randomUUID();
        EmployeeResponse response = new EmployeeResponse(
                id, "ACME-00001", "Jane Doe", "jane.doe@acme.com",
                Department.HUMAN_RESOURCES, "HR Specialist", JobLevel.L2,
                Country.UNITED_STATES, Currency.USD,
                new BigDecimal("65000.00"), new BigDecimal("65000.00"),
                EmployeeStatus.INACTIVE, LocalDate.of(2023, 5, 1),
                Instant.now(), Instant.now()
        );

        when(employeeService.deactivateEmployee(id)).thenReturn(response);

        mockMvc.perform(patch("/api/employees/{id}/deactivate", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("INACTIVE"));
    }

    @Test
    void testSearchEmployees() throws Exception {
        EmployeeResponse response = new EmployeeResponse(
                UUID.randomUUID(), "ACME-00001", "Jane Doe", "jane.doe@acme.com",
                Department.HUMAN_RESOURCES, "HR Specialist", JobLevel.L2,
                Country.UNITED_STATES, Currency.USD,
                new BigDecimal("65000.00"), new BigDecimal("65000.00"),
                EmployeeStatus.ACTIVE, LocalDate.of(2023, 5, 1),
                Instant.now(), Instant.now()
        );
        PageResponse<EmployeeResponse> pageResponse = new PageResponse<>(List.of(response), 0, 20, 1, 1, true, true);

        when(employeeService.searchEmployees(any(), any(), any(), any(), any(), any())).thenReturn(pageResponse);

        mockMvc.perform(get("/api/employees")
                        .param("search", "Jane")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].fullName").value("Jane Doe"));
    }
}
