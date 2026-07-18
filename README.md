# Student Task Manager System

A full-stack web application that helps students organize academic tasks, deadlines, priorities, and study progress.

This project was created as an internship preparation project to demonstrate full-stack development, REST API design, JWT authentication, validation, automated testing, Docker, and continuous integration.

## Features

- User registration and login
- JWT-based authentication
- Password hashing with BCrypt
- User-specific task management
- Create, view, update, and delete tasks
- Task status management
- Task priority management
- Due-date validation
- Search tasks by title or description
- Filter tasks by status
- Filter tasks by priority
- Consistent API error responses
- Secure application logging
- Swagger/OpenAPI documentation
- Docker Compose setup
- Backend automated tests
- Frontend automated tests
- GitHub Actions continuous integration

## Task Status Values

- `PENDING`
- `IN_PROGRESS`
- `COMPLETED`

## Task Priority Values

- `LOW`
- `MEDIUM`
- `HIGH`

## Technology Stack

### Frontend

- React
- TypeScript
- Vite
- Axios
- CSS
- Vitest
- React Testing Library

### Backend

- Java 17
- Spring Boot
- Spring Web
- Spring Data JPA
- Hibernate
- Jakarta Validation
- JWT
- BCrypt
- Maven
- JUnit 5
- Mockito
- MockMvc

### Database

- MySQL 8
- H2 for repository integration tests

### DevOps

- Docker
- Docker Compose
- GitHub Actions

## Project Structure

```text
Student_Task_Manager_System
├── .github
│   └── workflows
│       ├── backend-ci.yml
│       └── frontend-ci.yml
├── backend
│   ├── src
│   │   ├── main
│   │   └── test
│   ├── Dockerfile
│   └── pom.xml
├── frontend
│   ├── src
│   ├── Dockerfile
│   ├── package.json
│   └── vite.config.ts
├── docker-compose.yml
├── .env.example
└── README.md
```

## Application Architecture

```text
React + TypeScript Frontend
            |
            | HTTP / JSON
            v
Spring Boot REST API
            |
            | Spring Data JPA
            v
        MySQL Database
```

Authentication uses JWT tokens.

After a successful login, the frontend sends the token with protected requests:

```http
Authorization: Bearer <token>
```

Each user can access only the tasks that belong to their account.

## Run the Project with Docker

### Requirements

Install:

- Git
- Docker Desktop

### 1. Clone the repository

```bash
git clone https://github.com/kaushalye1234/Student_Task_Manager_System.git
cd Student_Task_Manager_System
```

### 2. Create the environment file

On Windows PowerShell:

```powershell
Copy-Item .env.example .env
```

Open `.env` and replace the example values with secure values.

Never commit the real `.env` file.

### 3. Start the application

```bash
docker compose up --build -d
```

### 4. Open the application

| Service | Address |
|---|---|
| Frontend | `http://localhost` |
| Backend API | `http://localhost:8080` |
| Swagger UI | `http://localhost:8080/swagger-ui/index.html` |
| MySQL from host computer | `localhost:3307` |

### 5. Check the containers

```bash
docker compose ps
```

### 6. View backend logs

```bash
docker compose logs -f backend
```

Press `Ctrl + C` to stop watching the logs.

### 7. Stop the application

```bash
docker compose down
```

To also remove the MySQL Docker volume:

```bash
docker compose down -v
```

Warning: the second command deletes the Docker database data.

## Environment Variables

The Docker Compose configuration uses these variables:

| Variable | Description |
|---|---|
| `MYSQL_ROOT_PASSWORD` | MySQL root password |
| `APP_JWT_SECRET` | Secret used to sign JWT tokens |
| `APP_JWT_EXPIRATION_MS` | JWT expiration duration in milliseconds |

Example:

```env
MYSQL_ROOT_PASSWORD=replace-with-a-secure-password
APP_JWT_SECRET=replace-with-a-long-random-secret
APP_JWT_EXPIRATION_MS=86400000
```

Do not store real passwords or production secrets in GitHub.

## API Endpoints

### Authentication

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/auth/register` | Register a user |
| `POST` | `/api/auth/login` | Log in and receive a JWT |

### Tasks

The task endpoints require a valid JWT token.

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/tasks` | Get the logged-in user's tasks |
| `POST` | `/api/tasks` | Create a task |
| `GET` | `/api/tasks/{id}` | Get one task |
| `PUT` | `/api/tasks/{id}` | Update a task |
| `DELETE` | `/api/tasks/{id}` | Delete a task |

## Example Registration Request

```json
{
  "fullName": "Test Student",
  "email": "student@example.com",
  "password": "secret123"
}
```

## Example Login Request

```json
{
  "email": "student@example.com",
  "password": "secret123"
}
```

## Example Task Request

```json
{
  "title": "Study Spring Boot",
  "description": "Complete the REST API testing lesson",
  "status": "PENDING",
  "priority": "HIGH",
  "dueDate": "2099-12-31"
}
```

## Run Backend Tests

From the repository root:

```bash
mvn -f backend/pom.xml test
```

The backend test suite includes:

- Task controller unit tests
- Authentication controller unit tests
- HTTP validation and error-response tests
- Repository integration tests using H2

## Run Frontend Tests

```bash
cd frontend
npm ci
npm test
```

Run the production frontend build:

```bash
npm run build
```

## Continuous Integration

Two GitHub Actions workflows run automatically.

### Backend CI

Runs when backend files change:

```text
Install Java 17
Run Maven tests
Build the backend
```

### Frontend CI

Runs when frontend files change:

```text
Install Node.js
Install npm dependencies
Run Vitest tests
Build the frontend
```

The workflows also run for pull requests targeting the `main` branch.

## Validation and Error Handling

The backend validates task input and returns structured JSON error responses.

Example:

```json
{
  "timestamp": "2026-07-18T10:30:00",
  "status": 400,
  "error": "Validation Failed",
  "message": "title: Task title is required"
}
```

Common HTTP responses include:

| Status | Meaning |
|---|---|
| `400` | Invalid request or validation failure |
| `401` | Missing, invalid, or expired JWT |
| `404` | Requested task was not found |
| `500` | Unexpected backend error |

## Security Notes

- Passwords are stored as BCrypt hashes.
- JWT secrets are loaded from environment variables.
- Database passwords are not committed to Git.
- Tasks are queried using both task ID and user ID.
- Tokens, passwords, and secrets are not written to application logs.

## Author

Developed by Chamindu Kaushalya as a full-stack software engineering and internship preparation project.