com.acme.salary
├── SalaryApplication.java
│
├── controller
│   ├── EmployeeController.java
│   └── AnalyticsController.java
│
├── service
│   ├── EmployeeService.java
│   └── AnalyticsService.java
│
├── repository
│   └── EmployeeRepository.java
│
├── entity
│   └── Employee.java
│
├── dto
│   ├── request
│   │   ├── EmployeeCreateRequest.java
│   │   └── EmployeeUpdateRequest.java
│   └── response
│       ├── EmployeeResponse.java
│       ├── PageResponse.java
│       └── AnalyticsResponse.java
│
├── exception
│   ├── ResourceNotFoundException.java
│   └── GlobalExceptionHandler.java
│
├── enums
│   ├── Department.java
│   ├── JobLevel.java
│   ├── Country.java
│   ├── Currency.java
│   └── EmployeeStatus.java
│
└── config
    └── CorsConfig.java