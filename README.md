# Regula

**A live portal for internal complaince control.**

GitHub Repoistory: "https://github.com/siguraso/fullstack-project-v26"

## Contributors

- Alexander Owren Elton
- Edvard Emmanuel Klavenes
- Sigurd Andris Sørengen
- Liam Schreiner Lande

## Prerequisites

- JDK 21 (or newer)
- NPM package manager
- node.js 20 (or newer)
- Docker for dockerized setup

## Setup

1. Clone and cd into repository:

```bash
$ git clone https://github.com/siguraso/fullstack-project-v26.git regula

...

$ cd regula
```

2. install frontend dependencies:

```bash
$ cd frontend

$ npm install
```

## Running the application

### Option 1: Run using Docker (recommended):

In the project root run:

```bash
$ docker compose up
```

This will run the entire application stack, where you can access the frontend through http://localhost:8081.

### Option 2: Run locally (used in further development):

1. Run the frontend:

When in the repository root:

```bash
$ cd frontend
$ npm run dev
```

You can now access the frontend through the address prompted in the terminal.

2. Run the backend:

In a differetn terminal, navigate to the project root, and run using the Maven CLI:

```bash
$ cd backend
$ mvn spring-boot:run
```

## Testing

### Backend testing

Navigate to the backend and run:

```bash
$ cd backend
$ mvn test
```

### Frontend tests:

#### End to end tests

To run the e2e tests navigate to the frontend and run:

```bash
$ cd frontend
$ npm run test:e2e
```

(_The Cypress plugin is required to run the e2e tests, so you have to install it if you are prompted to._)

#### Unit tests

To run the unit tests navigate to the frontend and run:

```bash
$ cd frontend
$ npm run test:unit
```

(_The Vitest plugin is required to run the unit tests, so you have to install it if you are prompted to._)
