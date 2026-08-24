package com.duft.salary_management.Service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.duft.salary_management.DTO.AnalyticsSummaryResponse;
import com.duft.salary_management.DTO.BreakdownItemResponse;
import com.duft.salary_management.DTO.SalaryDistributionItemResponse;
import com.duft.salary_management.Enums.EmployeeStatus;
import com.duft.salary_management.Repository.EmployeeRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class AnalyticsService {

    private final EmployeeRepository employeeRepository;

    public AnalyticsService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public AnalyticsSummaryResponse getSummary(boolean includeInactive) {
        EmployeeStatus status = includeInactive ? null : EmployeeStatus.ACTIVE;

        List<Object[]> results = employeeRepository.getSummaryAggregates(status);
        Object[] result = results.get(0);

        long headcount = ((Number) result[0]).longValue();

        BigDecimal totalPayroll = toBigDecimal(result[1]);
        BigDecimal average = toBigDecimal(result[2]);
        BigDecimal min = toBigDecimal(result[3]);
        BigDecimal max = toBigDecimal(result[4]);

        BigDecimal median = calculateMedianSalary(status, headcount);

        return new AnalyticsSummaryResponse(
                headcount,
                totalPayroll.setScale(2, RoundingMode.HALF_UP),
                average.setScale(2, RoundingMode.HALF_UP),
                median.setScale(2, RoundingMode.HALF_UP),
                min.setScale(2, RoundingMode.HALF_UP),
                max.setScale(2, RoundingMode.HALF_UP)
        );
    }

    private BigDecimal calculateMedianSalary(EmployeeStatus status, long headcount) {
        if (headcount == 0) {
            return BigDecimal.ZERO;
        }

        if (headcount % 2 == 1) {
            int middleIndex = (int) (headcount / 2);
            List<BigDecimal> middleRow = employeeRepository.getSalaryAtOffset(
                    status, org.springframework.data.domain.PageRequest.of(middleIndex, 1)
            );
            return middleRow.isEmpty() ? BigDecimal.ZERO : toBigDecimal(middleRow.get(0));
        } else {
            int middleIndex1 = (int) (headcount / 2 - 1);
            int middleIndex2 = (int) (headcount / 2);
            List<BigDecimal> row1 = employeeRepository.getSalaryAtOffset(
                    status, org.springframework.data.domain.PageRequest.of(middleIndex1, 1)
            );
            List<BigDecimal> row2 = employeeRepository.getSalaryAtOffset(
                    status, org.springframework.data.domain.PageRequest.of(middleIndex2, 1)
            );
            if (row1.isEmpty() || row2.isEmpty()) {
                return BigDecimal.ZERO;
            }
            BigDecimal val1 = toBigDecimal(row1.get(0));
            BigDecimal val2 = toBigDecimal(row2.get(0));
            return val1.add(val2).divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
        }
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        if (value instanceof Number) {
            return BigDecimal.valueOf(((Number) value).doubleValue());
        }
        return new BigDecimal(value.toString());
    }

    public List<BreakdownItemResponse> getSalaryByCountry(boolean includeInactive) {
        return mapBreakdown(employeeRepository.getAverageSalaryByCountry(resolveStatus(includeInactive)));
    }

    public List<BreakdownItemResponse> getSalaryByDepartment(boolean includeInactive) {
        return mapBreakdown(employeeRepository.getAverageSalaryByDepartment(resolveStatus(includeInactive)));
    }

    public List<BreakdownItemResponse> getSalaryByJobLevel(boolean includeInactive) {
        return mapBreakdown(employeeRepository.getAverageSalaryByJobLevel(resolveStatus(includeInactive)));
    }

    public List<SalaryDistributionItemResponse> getSalaryDistribution(boolean includeInactive) {
        List<Object[]> rows = employeeRepository.getSalaryDistribution(resolveStatus(includeInactive));

        long total = rows.stream()
                .mapToLong(r -> ((Number) r[1]).longValue())
                .sum();

        return rows.stream()
                .map(r -> {
                    String band = (String) r[0];
                    long count = ((Number) r[1]).longValue();
                    double percentage = total == 0 ? 0 : (count * 100.0) / total;
                    return new SalaryDistributionItemResponse(band, count, Math.round(percentage * 100.0) / 100.0);
                })
                .toList();
    }

    private EmployeeStatus resolveStatus(boolean includeInactive) {
        return includeInactive ? null : EmployeeStatus.ACTIVE;
    }

    private List<BreakdownItemResponse> mapBreakdown(List<Object[]> rows) {
        return rows.stream()
                .map(r -> new BreakdownItemResponse(
                        r[0].toString(),
                        toBigDecimal(r[1]).setScale(2, RoundingMode.HALF_UP),
                        toBigDecimal(r[2]).setScale(2, RoundingMode.HALF_UP),
                        ((Number) r[3]).longValue()
                ))
                .toList();
    }
}
