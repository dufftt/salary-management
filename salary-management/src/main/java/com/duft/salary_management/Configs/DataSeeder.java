package com.duft.salary_management.Configs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.duft.salary_management.Enums.Country;
import com.duft.salary_management.Enums.Currency;
import com.duft.salary_management.Enums.Department;
import com.duft.salary_management.Enums.EmployeeStatus;
import com.duft.salary_management.Enums.JobLevel;
import com.duft.salary_management.Utility.CurrencyConverter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);
    private static final int TOTAL_EMPLOYEES = 10_000;

    private final JdbcTemplate jdbcTemplate;
    private final Random random = new Random();

    public DataSeeder(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private record SeedEmployee(
            UUID id,
            String employeeId,
            String fullName,
            String email,
            Department department,
            String jobTitle,
            JobLevel jobLevel,
            Country country,
            Currency currency,
            BigDecimal annualSalary,
            BigDecimal annualSalaryUsd,
            EmployeeStatus status,
            LocalDate dateOfJoining
    ) {}

    @Override
    @Transactional
    public void run(String... args) {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM employees", Integer.class);
        if (count != null && count > 0) {
            log.info("Database already contains {} employees. Skipping seeding.", count);
            return;
        }

        log.info("Starting ultra-fast batch seeding for {} employees...", TOTAL_EMPLOYEES);
        long startTime = System.currentTimeMillis();

        List<SeedEmployee> employees = new ArrayList<>(TOTAL_EMPLOYEES);
        for (int i = 1; i <= TOTAL_EMPLOYEES; i++) {
            employees.add(generateEmployee(i));
        }

        String sql = """
            INSERT INTO employees (
                id, employee_id, full_name, email, department, job_title, job_level,
                country, currency, annual_salary, annual_salary_usd, status,
                date_of_joining, created_at, updated_at
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        Instant now = Instant.now();

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                SeedEmployee emp = employees.get(i);
                ps.setString(1, emp.id.toString());
                ps.setString(2, emp.employeeId);
                ps.setString(3, emp.fullName);
                ps.setString(4, emp.email);
                ps.setString(5, emp.department.name());
                ps.setString(6, emp.jobTitle);
                ps.setString(7, emp.jobLevel.name());
                ps.setString(8, emp.country.name());
                ps.setString(9, emp.currency.name());
                ps.setBigDecimal(10, emp.annualSalary);
                ps.setBigDecimal(11, emp.annualSalaryUsd);
                ps.setString(12, emp.status.name());
                ps.setDate(13, java.sql.Date.valueOf(emp.dateOfJoining));
                ps.setTimestamp(14, java.sql.Timestamp.from(now));
                ps.setTimestamp(15, java.sql.Timestamp.from(now));
            }

            @Override
            public int getBatchSize() {
                return employees.size();
            }
        });

        long duration = System.currentTimeMillis() - startTime;
        log.info("Successfully seeded {} employees in {} ms.", TOTAL_EMPLOYEES, duration);
    }

    private SeedEmployee generateEmployee(int sequence) {
        Country country = randomCountry();
        Currency currency = currencyForCountry(country);
        JobLevel jobLevel = randomJobLevel();
        Department department = randomDepartment();

        BigDecimal annualSalary = generateSalary(jobLevel, country);
        BigDecimal annualSalaryUsd = CurrencyConverter.toUsd(annualSalary, currency);

        String employeeId = String.format("ACME-%05d", sequence);
        String fullName = generateName();
        String email = generateEmail(fullName, sequence);

        return new SeedEmployee(
                UUID.randomUUID(),
                employeeId,
                fullName,
                email,
                department,
                generateJobTitle(jobLevel, department),
                jobLevel,
                country,
                currency,
                annualSalary,
                annualSalaryUsd,
                random.nextDouble() < 0.92 ? EmployeeStatus.ACTIVE : EmployeeStatus.INACTIVE,
                randomJoiningDate()
        );
    }

    // ==================== Helper Methods ====================

    private Country randomCountry() {
        // Weighted distribution – more employees in India & US
        double r = random.nextDouble();
        if (r < 0.38) return Country.INDIA;
        if (r < 0.58) return Country.UNITED_STATES;
        if (r < 0.68) return Country.UNITED_KINGDOM;
        if (r < 0.75) return Country.GERMANY;
        if (r < 0.81) return Country.SINGAPORE;
        if (r < 0.86) return Country.AUSTRALIA;
        if (r < 0.90) return Country.CANADA;
        if (r < 0.94) return Country.NETHERLANDS;
        if (r < 0.97) return Country.UAE;
        return Country.JAPAN;
    }

    private Currency currencyForCountry(Country country) {
        return switch (country) {
            case INDIA -> Currency.INR;
            case UNITED_STATES -> Currency.USD;
            case UNITED_KINGDOM -> Currency.GBP;
            case GERMANY, NETHERLANDS -> Currency.EUR;
            case SINGAPORE -> Currency.SGD;
            case AUSTRALIA -> Currency.AUD;
            case CANADA -> Currency.CAD;
            case UAE -> Currency.AED;
            case JAPAN -> Currency.JPY;
        };
    }

    private JobLevel randomJobLevel() {
        double r = random.nextDouble();
        if (r < 0.18) return JobLevel.L1;
        if (r < 0.38) return JobLevel.L2;
        if (r < 0.58) return JobLevel.L3;
        if (r < 0.73) return JobLevel.L4;
        if (r < 0.84) return JobLevel.L5;
        if (r < 0.92) return JobLevel.MANAGER;
        if (r < 0.97) return JobLevel.SENIOR_MANAGER;
        if (r < 0.99) return JobLevel.DIRECTOR;
        return JobLevel.VP;
    }

    private Department randomDepartment() {
        Department[] departments = Department.values();
        return departments[random.nextInt(departments.length)];
    }

    private BigDecimal generateSalary(JobLevel level, Country country) {
        // Base salary in USD equivalent, then we convert back roughly
        double baseUsd = switch (level) {
            case L1 -> 25_000 + random.nextDouble() * 15_000;
            case L2 -> 40_000 + random.nextDouble() * 20_000;
            case L3 -> 60_000 + random.nextDouble() * 25_000;
            case L4 -> 85_000 + random.nextDouble() * 30_000;
            case L5 -> 110_000 + random.nextDouble() * 40_000;
            case MANAGER -> 130_000 + random.nextDouble() * 40_000;
            case SENIOR_MANAGER -> 160_000 + random.nextDouble() * 50_000;
            case DIRECTOR -> 200_000 + random.nextDouble() * 80_000;
            case VP -> 280_000 + random.nextDouble() * 120_000;
        };

        // Country adjustment (cost of living / market)
        double multiplier = switch (country) {
            case INDIA -> 0.35;
            case UNITED_STATES -> 1.0;
            case UNITED_KINGDOM -> 0.95;
            case GERMANY, NETHERLANDS -> 0.90;
            case SINGAPORE -> 0.85;
            case AUSTRALIA, CANADA -> 0.88;
            case UAE -> 0.80;
            case JAPAN -> 0.75;
        };

        double adjustedUsd = baseUsd * multiplier;

        // Convert back to local currency (approximate inverse)
        double localAmount = switch (country) {
            case INDIA -> adjustedUsd / 0.012;
            case UNITED_STATES -> adjustedUsd;
            case UNITED_KINGDOM -> adjustedUsd / 1.27;
            case GERMANY, NETHERLANDS -> adjustedUsd / 1.08;
            case SINGAPORE -> adjustedUsd / 0.74;
            case AUSTRALIA -> adjustedUsd / 0.65;
            case CANADA -> adjustedUsd / 0.73;
            case UAE -> adjustedUsd / 0.27;
            case JAPAN -> adjustedUsd / 0.0067;
        };

        return BigDecimal.valueOf(localAmount).setScale(2, RoundingMode.HALF_UP);
    }

    private String generateName() {
        String[] firstNames = {
                "Aarav", "Aditi", "Arjun", "Ananya", "Rohan", "Priya", "Vikram", "Sneha",
                "James", "Emily", "Michael", "Sophia", "David", "Olivia", "John", "Emma",
                "Liu", "Wei", "Yuki", "Hana", "Omar", "Fatima", "Lucas", "Marie"
        };
        String[] lastNames = {
                "Sharma", "Patel", "Singh", "Gupta", "Kumar", "Smith", "Johnson", "Brown",
                "Williams", "Jones", "Garcia", "Miller", "Davis", "Wilson", "Anderson", "Thomas"
        };

        return firstNames[random.nextInt(firstNames.length)] + " " +
               lastNames[random.nextInt(lastNames.length)];
    }

    private String generateEmail(String fullName, int sequence) {
        String cleaned = fullName.toLowerCase().replaceAll("[^a-z]", ".");
        return cleaned + sequence + "@acme.com";
    }

    private String generateJobTitle(JobLevel level, Department department) {
        return level.name().replace("_", " ") + " - " + department.name().replace("_", " ");
    }

    private LocalDate randomJoiningDate() {
        long minDay = LocalDate.of(2015, 1, 1).toEpochDay();
        long maxDay = LocalDate.now().toEpochDay();
        long randomDay = ThreadLocalRandom.current().nextLong(minDay, maxDay);
        return LocalDate.ofEpochDay(randomDay);
    }
}
