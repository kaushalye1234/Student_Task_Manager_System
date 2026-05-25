# Student Task Manager System

A full-stack task management web application built for students to manage academic tasks, assignments, deadlines, and study progress.

This project was developed as part of my internship preparation program to improve my skills in full-stack software engineering using React, TypeScript, Spring Boot, and MySQL.

---

## Tech Stack

### Frontend
- React
- TypeScript
- Vite
- Axios
- CSS

### Backend
- Java 17
- Spring Boot
- Spring Web
- Spring Data JPA
- Hibernate

### Database
- MySQL

### Tools
- Git
- GitHub
- VS Code
- Postman
- Maven

---

## Features

- Add new tasks
- View all tasks
- Update task status
- Mark tasks as in progress
- Mark tasks as completed
- Delete tasks
- Store task data in MySQL database
- REST API backend
- React frontend connected to Spring Boot backend

---

## Task Status Types

- PENDING
- IN_PROGRESS
- COMPLETED

---

## API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/tasks` | Get all tasks |
| POST | `/api/tasks` | Create a new task |
| GET | `/api/tasks/{id}` | Get task by ID |
| PUT | `/api/tasks/{id}` | Update task |
| DELETE | `/api/tasks/{id}` | Delete task |

---

## Example JSON Request

```json
{
  "title": "Study Spring Boot",
  "description": "Learn CRUD API for internship project",
  "status": "PENDING",
  "dueDate": "2026-06-01"
}