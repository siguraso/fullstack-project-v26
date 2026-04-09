# Regula

**A live portal for internal complaince control.**

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

## Option 1: Run using Docker (recommended):

```bash
$ docker compose up
```

This will run the entire application stack, where you can access the frontend through http://localhost:8080.

## Option 2: Run locally:

1. Run the frontend:

When in the repository root:

```bash
$ cd frontend
$ npm run dev
```

You can now access the frontend through the address prompted in the terminal.

2. Run the backend:

In a differetn terminal, navigate to the project root, and run:

```bash
$ cd backend
$ ./mvnw spring-boot:run
```
