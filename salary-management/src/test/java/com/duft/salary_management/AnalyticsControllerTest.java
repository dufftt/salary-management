package com.duft.salary_management;

import com.duft.salary_management.Controller.AnalyticsController;
import com.duft.salary_management.DTO.AnalyticsSummaryResponse;
import com.duft.salary_management.DTO.BreakdownItemResponse;
import com.duft.salary_management.DTO.SalaryDistributionItemResponse;
import com.duft.salary_management.Service.AnalyticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AnalyticsControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AnalyticsService analyticsService;

    @InjectMocks
    private AnalyticsController analyticsController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(analyticsController).build();
    }

    @Test
    void testGetSummary() throws Exception {
        AnalyticsSummaryResponse summary = new AnalyticsSummaryResponse(
                10000L, new BigDecimal("100000000.00"), new BigDecimal("100000.00"),
                new BigDecimal("95000.00"), new BigDecimal("25000.00"), new BigDecimal("350000.00")
        );
        when(analyticsService.getSummary(false)).thenReturn(summary);

        mockMvc.perform(get("/api/analytics/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalHeadcount").value(10000))
                .andExpect(jsonPath("$.averageAnnualSalary").value(100000.00));
    }

    @Test
    void testGetByCountry() throws Exception {
        List<BreakdownItemResponse> response = List.of(
                new BreakdownItemResponse("UNITED_STATES", new BigDecimal("120000.00"), new BigDecimal("24000000.00"), 200L)
        );
        when(analyticsService.getSalaryByCountry(false)).thenReturn(response);

        mockMvc.perform(get("/api/analytics/by-country"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].key").value("UNITED_STATES"))
                .andExpect(jsonPath("$[0].headcount").value(200));
    }

    @Test
    void testGetSalaryDistribution() throws Exception {
        List<SalaryDistributionItemResponse> response = List.of(
                new SalaryDistributionItemResponse("0 - 30k", 2000L, 20.0)
        );
        when(analyticsService.getSalaryDistribution(false)).thenReturn(response);

        mockMvc.perform(get("/api/analytics/salary-distribution"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].band").value("0 - 30k"))
                .andExpect(jsonPath("$[0].count").value(2000));
    }
}
