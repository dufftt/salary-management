package com.duft.salary_management;

import com.duft.salary_management.DTO.AnalyticsSummaryResponse;
import com.duft.salary_management.DTO.BreakdownItemResponse;
import com.duft.salary_management.DTO.SalaryDistributionItemResponse;
import com.duft.salary_management.Enums.Country;
import com.duft.salary_management.Enums.EmployeeStatus;
import com.duft.salary_management.Repository.EmployeeRepository;
import com.duft.salary_management.Service.AnalyticsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private AnalyticsService analyticsService;

    @Test
    void testGetSummaryOddHeadcount() {
        Object[] aggRow = new Object[]{3L, new BigDecimal("300000.00"), new BigDecimal("100000.00"), new BigDecimal("80000.00"), new BigDecimal("120000.00")};
        when(employeeRepository.getSummaryAggregates(EmployeeStatus.ACTIVE)).thenReturn(Collections.singletonList(aggRow));
        when(employeeRepository.getSalaryAtOffset(eq(EmployeeStatus.ACTIVE), eq(PageRequest.of(1, 1))))
                .thenReturn(List.of(new BigDecimal("100000.00")));

        AnalyticsSummaryResponse summary = analyticsService.getSummary(false);

        assertNotNull(summary);
        assertEquals(3L, summary.totalHeadcount());
        assertEquals(new BigDecimal("300000.00"), summary.totalAnnualPayroll());
        assertEquals(new BigDecimal("100000.00"), summary.averageAnnualSalary());
        assertEquals(new BigDecimal("100000.00"), summary.medianAnnualSalary());
        assertEquals(new BigDecimal("80000.00"), summary.minSalary());
        assertEquals(new BigDecimal("120000.00"), summary.maxSalary());
    }

    @Test
    void testGetSummaryEvenHeadcount() {
        Object[] aggRow = new Object[]{4L, new BigDecimal("400000.00"), new BigDecimal("100000.00"), new BigDecimal("80000.00"), new BigDecimal("130000.00")};
        when(employeeRepository.getSummaryAggregates(EmployeeStatus.ACTIVE)).thenReturn(Collections.singletonList(aggRow));
        when(employeeRepository.getSalaryAtOffset(eq(EmployeeStatus.ACTIVE), eq(PageRequest.of(1, 1))))
                .thenReturn(List.of(new BigDecimal("90000.00")));
        when(employeeRepository.getSalaryAtOffset(eq(EmployeeStatus.ACTIVE), eq(PageRequest.of(2, 1))))
                .thenReturn(List.of(new BigDecimal("110000.00")));

        AnalyticsSummaryResponse summary = analyticsService.getSummary(false);

        assertNotNull(summary);
        assertEquals(4L, summary.totalHeadcount());
        assertEquals(new BigDecimal("100000.00"), summary.medianAnnualSalary());
    }

    @Test
    void testGetSalaryByCountry() {
        List<Object[]> rows = List.of(
                new Object[]{Country.UNITED_STATES, 120000.00, 240000.00, 2L},
                new Object[]{Country.INDIA, 40000.00, 80000.00, 2L}
        );
        when(employeeRepository.getAverageSalaryByCountry(EmployeeStatus.ACTIVE)).thenReturn(rows);

        List<BreakdownItemResponse> result = analyticsService.getSalaryByCountry(false);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("UNITED_STATES", result.get(0).key());
        assertEquals(new BigDecimal("120000.00"), result.get(0).averageSalary());
    }

    @Test
    void testGetSalaryDistribution() {
        List<Object[]> rows = List.of(
                new Object[]{"0 - 30k", 2L},
                new Object[]{"30k - 50k", 8L}
        );
        when(employeeRepository.getSalaryDistribution(null)).thenReturn(rows);

        List<SalaryDistributionItemResponse> result = analyticsService.getSalaryDistribution(true);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("0 - 30k", result.get(0).band());
        assertEquals(2L, result.get(0).count());
        assertEquals(20.0, result.get(0).percentage());
        assertEquals("30k - 50k", result.get(1).band());
        assertEquals(80.0, result.get(1).percentage());
    }
}
