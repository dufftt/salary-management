package com.duft.salary_management;

import com.duft.salary_management.DTO.AnalyticsSummaryResponse;
import com.duft.salary_management.DTO.BreakdownItemResponse;
import com.duft.salary_management.DTO.SalaryDistributionItemResponse;
import com.duft.salary_management.Service.AnalyticsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class SalaryManagementApplicationTests {

	@Autowired
	private AnalyticsService analyticsService;

	@Autowired
	private com.duft.salary_management.Service.EmployeeService employeeService;

	@Test
	void contextLoads() {
	}

	@Test
	void testSearchEmployeesIntegration() {
		var page = employeeService.searchEmployees(null, null, null, null, null, org.springframework.data.domain.PageRequest.of(0, 20));
		assertNotNull(page);
		assertTrue(page.totalElements() > 0);
		assertFalse(page.content().isEmpty());
		assertNotNull(page.content().get(0).dateOfJoining());
		assertNotNull(page.content().get(0).createdAt());

		// Test finding the employee by ID
		var firstEmployee = page.content().get(0);
		var found = employeeService.getEmployeeById(firstEmployee.id());
		assertNotNull(found);
		org.junit.jupiter.api.Assertions.assertEquals(firstEmployee.id(), found.id());
		org.junit.jupiter.api.Assertions.assertEquals(firstEmployee.employeeId(), found.employeeId());
	}

	@Test
	void testAnalyticsSummaryWithInactive() {
		AnalyticsSummaryResponse activeSummary = analyticsService.getSummary(false);
		assertNotNull(activeSummary);
		assertTrue(activeSummary.totalHeadcount() > 0);

		AnalyticsSummaryResponse allSummary = analyticsService.getSummary(true);
		assertNotNull(allSummary);
		assertTrue(allSummary.totalHeadcount() >= activeSummary.totalHeadcount());
	}

	@Test
	void testSalaryDistributionWithInactive() {
		List<SalaryDistributionItemResponse> activeDist = analyticsService.getSalaryDistribution(false);
		assertNotNull(activeDist);
		assertFalse(activeDist.isEmpty());

		List<SalaryDistributionItemResponse> allDist = analyticsService.getSalaryDistribution(true);
		assertNotNull(allDist);
		assertFalse(allDist.isEmpty());
	}

	@Test
	void testBreakdownsWithInactive() {
		List<BreakdownItemResponse> countries = analyticsService.getSalaryByCountry(true);
		assertNotNull(countries);
		assertFalse(countries.isEmpty());

		List<BreakdownItemResponse> departments = analyticsService.getSalaryByDepartment(true);
		assertNotNull(departments);
		assertFalse(departments.isEmpty());

		List<BreakdownItemResponse> jobLevels = analyticsService.getSalaryByJobLevel(true);
		assertNotNull(jobLevels);
		assertFalse(jobLevels.isEmpty());
	}
}

