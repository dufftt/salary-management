src/app/
├── core/                     # App-wide singletons (services, interceptors)
├── shared/                   # Shared UI components, pipes, directives
├── features/
│   ├── employees/            # List, Create, Edit, Detail pages
│   └── analytics/            # Dashboard + Charts
├── models/                   # TypeScript interfaces matching backend DTOs
└── app.routes.ts