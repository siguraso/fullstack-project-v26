# Project Overview - Regula

## Summary

Regula is a fullstack internal compliance platform with a Vue frontend and a Spring Boot backend.
The app supports operational compliance workflows such as deviations, checklists, inspections, temperature logs, alcohol logs, document handling, tenant/user management, and invitations.

## Goals

- Centralize compliance logging and follow-up tasks.
- Give teams a dashboard overview of alerts, activity, and daily checklist progress.
- Support multi-tenant operations with role-based access and authenticated API access.
- Keep local development simple with either Docker or local runtime.

## High-Level Architecture

- Frontend:
  - Vue 3 + TypeScript + Vite
  - Pinia for state management
  - Vue Router for routing
  - Service layer for API communication
- Backend:
  - Spring Boot 4.0.4
  - Spring Web, Spring Data JPA, Spring Security, Validation
  - JWT-based authentication flow
  - H2 in-memory database in local/dev configuration
  - OpenAPI/Swagger UI via springdoc
- Infra:
  - Docker Compose setup for frontend and backend
  - Nginx container serving frontend assets

## Repository Layout

- backend/: Java Spring application
- frontend/: Vue application
- docs/: supporting project documentation (includes API contract reference)
- docker/: Nginx configuration
- docker-compose.yml: local multi-container orchestration
- project-overview.md: this overview

## Backend Domain Map

Main package root:

- edu.ntnu.idi.idatt2105.backend

Key modules:

- common:
  - Security, auth, config, exceptions, shared DTOs, email integration
- core:
  - dashboard
  - tenant, user, invitation
  - document
  - compliance:
    - checklist
    - deviation
    - inspection
    - base log abstractions
- ikfood:
  - temperaturezone
  - temperaturelog
- ikalcohol:
  - alcohol log

Typical layering pattern per domain:

- controller -> service -> repository -> entity
- dto + mapper classes for API-facing contract transformations

## Frontend Application Map

Core frontend structure:

- src/views/: feature pages and page-specific components
- src/components/: shared UI components (including sidebar/navbar/ui cards)
- src/services/: API service layer by domain
- src/stores/: Pinia stores
- src/router/: route definitions
- src/interfaces/: TypeScript interfaces for domain models

Notable feature views:

- Dashboard
- Deviations
- Temperature logs and zones
- Alcohol logs
- Checklists and checklist builder
- Documents
- Inspections
- Settings
- Invite accept
- Login and main layout

## API Design

The backend API primarily returns a shared response envelope:

- success
- message
- data

Reference documentation is available in docs/api-contract.md, covering:

- status code conventions
- frontend-facing endpoints by module
- behavior expectations for create/update/delete and error handling

### Endpoints

All endpoints can be viewed through Swagger UI when running directly on your machine (_does NOT work when running through docker!_):

localhost:8080/swagger_ui.html

## Runtime and Configuration

Default local backend configuration includes:

- H2 in-memory DB at jdbc:h2:mem:regula
- SQL initialization from schema.sql and data.sql
- JWT configuration from environment (with local defaults)
- Invite link base URL targeting local frontend
- Resend email settings controlled through environment variables

Important note:

- Current application.properties includes concrete default values for security/email settings intended for local development.
- Production deployment should use secure environment-managed secrets and disable dev-only defaults.

## Testing and Quality

Backend tests:

- Uses Spring Boot test stack and JUnit.
- Coverage exists across controllers/services/repositories for key domains:
  - security/auth
  - deviation
  - checklist
  - dashboard
  - documents
  - alcohol logs
  - temperature logs

Frontend tests:

- Unit tests with Vitest
- E2E tests with Cypress (responsive smoke, temperature log view, documents view)
- Type checking with vue-tsc

Useful frontend commands:

- `npm run dev`
- `npm run type-check`
- `npm run test:unit`
- `npm run test:e2e`
- `npm run build`

## Current Status Snapshot (as of 10th of April 2026)

- Backend startup and run flow: successful in local terminal session.
- `npm run type-check` has run successfully in local terminal session.

## Follow-Up

- Consider toggling off dev-only endpoints/features in production (e.g. the H2 console)
- Move away from in-memory database implementation

## Suggested Next Improvements

- Add a short Architecture Decision Record section in docs/ for security, multitenancy, and persistence strategy.
- Add a health/readiness endpoint check summary to this overview.
- Add CI status badges and test command matrix to root README.

## Test data

The backend includes some test data, which can be viewed in the data.sql file in the backend resources. Here are some of the test users and their credentials:

**User 1:**
Email: sigurd@test.no
Password: Password123
User role: ADMIN

**User 2:**
Email: john@test.no
Password: Sigurd123
User role: MANAGER

**User 3:**
Email: jane@test.no
Password: trygtpassord!
User role: STAFF
