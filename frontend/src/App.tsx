import { useEffect, useState } from "react";
import axios from "axios";
import type { Task } from "./types/Task";
import "./App.css";

type TaskStatus = "PENDING" | "IN_PROGRESS" | "COMPLETED";
type FilterStatus = "ALL" | TaskStatus;
type AuthMode = "LOGIN" | "REGISTER";

interface AuthUser {
  id: number;
  fullName: string;
  email: string;
  message: string;
}

function App() {
  const [currentUser, setCurrentUser] = useState<AuthUser | null>(null);
  const [authMode, setAuthMode] = useState<AuthMode>("LOGIN");
  const [fullName, setFullName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const [tasks, setTasks] = useState<Task[]>([]);
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [dueDate, setDueDate] = useState("");
  const [filterStatus, setFilterStatus] = useState<FilterStatus>("ALL");

  const [aiTopic, setAiTopic] = useState("");
  const [suggestions, setSuggestions] = useState<string[]>([]);
  const [isGenerating, setIsGenerating] = useState(false);

  const API_URL = "http://localhost:8080/api/tasks";
  const AUTH_URL = "http://localhost:8080/api/auth";

  useEffect(() => {
    const savedUser = localStorage.getItem("taskManagerUser");

    if (savedUser) {
      setCurrentUser(JSON.parse(savedUser));
    }
  }, []);

  useEffect(() => {
    if (currentUser) {
      fetchTasks();
    }
  }, [currentUser]);

  const registerUser = async () => {
    if (!fullName.trim() || !email.trim() || !password.trim()) {
      alert("Please fill all fields");
      return;
    }

    try {
      const response = await axios.post<AuthUser>(`${AUTH_URL}/register`, {
        fullName,
        email,
        password,
      });

      alert(response.data.message);
      setAuthMode("LOGIN");
      setFullName("");
      setPassword("");
    } catch (error) {
      console.error("Registration error:", error);
      alert("Registration failed. Email may already be registered.");
    }
  };

  const loginUser = async () => {
    if (!email.trim() || !password.trim()) {
      alert("Please enter email and password");
      return;
    }

    try {
      const response = await axios.post<AuthUser>(`${AUTH_URL}/login`, {
        email,
        password,
      });

      setCurrentUser(response.data);
      localStorage.setItem("taskManagerUser", JSON.stringify(response.data));

      setEmail("");
      setPassword("");
    } catch (error) {
      console.error("Login error:", error);
      alert("Invalid email or password");
    }
  };

  const logoutUser = () => {
    localStorage.removeItem("taskManagerUser");
    setCurrentUser(null);
    setTasks([]);
    setSuggestions([]);
  };

  const fetchTasks = async () => {
    try {
      const response = await axios.get<Task[]>(API_URL);
      setTasks(response.data);
    } catch (error) {
      console.error("Error fetching tasks:", error);
    }
  };

  const addTask = async () => {
    if (!title.trim()) {
      alert("Task title is required");
      return;
    }

    if (!dueDate) {
      alert("Due date is required");
      return;
    }

    const newTask = {
      title,
      description,
      status: "PENDING" as TaskStatus,
      dueDate,
    };

    try {
      await axios.post(API_URL, newTask);
      setTitle("");
      setDescription("");
      setDueDate("");
      fetchTasks();
    } catch (error) {
      console.error("Error adding task:", error);
    }
  };

  const deleteTask = async (id: number) => {
    const confirmDelete = confirm("Are you sure you want to delete this task?");

    if (!confirmDelete) {
      return;
    }

    try {
      await axios.delete(`${API_URL}/${id}`);
      fetchTasks();
    } catch (error) {
      console.error("Error deleting task:", error);
    }
  };

  const updateStatus = async (task: Task, newStatus: TaskStatus) => {
    const updatedTask = {
      ...task,
      status: newStatus,
    };

    try {
      await axios.put(`${API_URL}/${task.id}`, updatedTask);
      fetchTasks();
    } catch (error) {
      console.error("Error updating task:", error);
    }
  };

  const generateSuggestions = async () => {
    if (!aiTopic.trim()) {
      alert("Please enter a topic");
      return;
    }

    setIsGenerating(true);

    try {
      const response = await axios.post<{ suggestions: string[] }>(
        "http://localhost:8080/api/ai/suggest-tasks",
        { topic: aiTopic }
      );

      setSuggestions(response.data.suggestions);
    } catch (error) {
      console.error("Error generating suggestions:", error);
    } finally {
      setIsGenerating(false);
    }
  };

  const addSuggestionAsTask = async (suggestion: string) => {
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);

    const newTask = {
      title: suggestion,
      description: "AI suggested study task",
      status: "PENDING" as TaskStatus,
      dueDate: tomorrow.toISOString().split("T")[0],
    };

    try {
      await axios.post(API_URL, newTask);
      fetchTasks();
    } catch (error) {
      console.error("Error adding AI suggestion as task:", error);
    }
  };

  const filteredTasks =
    filterStatus === "ALL"
      ? tasks
      : tasks.filter((task) => task.status === filterStatus);

  const pendingCount = tasks.filter((task) => task.status === "PENDING").length;
  const inProgressCount = tasks.filter(
    (task) => task.status === "IN_PROGRESS"
  ).length;
  const completedCount = tasks.filter(
    (task) => task.status === "COMPLETED"
  ).length;

  if (!currentUser) {
    return (
      <div className="auth-page">
        <div className="auth-card">
          <p className="small-title-dark">AI-Powered Student Task Manager</p>

          <h1>{authMode === "LOGIN" ? "Login" : "Create Account"}</h1>

          <p className="auth-subtitle">
            {authMode === "LOGIN"
              ? "Login to manage your study tasks."
              : "Register to start using your task manager."}
          </p>

          {authMode === "REGISTER" && (
            <>
              <label>Full Name</label>
              <input
                type="text"
                placeholder="Enter your full name"
                value={fullName}
                onChange={(e) => setFullName(e.target.value)}
              />
            </>
          )}

          <label>Email</label>
          <input
            type="email"
            placeholder="example@email.com"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />

          <label>Password</label>
          <input
            type="password"
            placeholder="Minimum 6 characters"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />

          {authMode === "LOGIN" ? (
            <button className="primary-btn" onClick={loginUser}>
              Login
            </button>
          ) : (
            <button className="primary-btn" onClick={registerUser}>
              Register
            </button>
          )}

          <p className="auth-switch">
            {authMode === "LOGIN"
              ? "Don't have an account?"
              : "Already have an account?"}{" "}
            <button
              onClick={() =>
                setAuthMode(authMode === "LOGIN" ? "REGISTER" : "LOGIN")
              }
            >
              {authMode === "LOGIN" ? "Register" : "Login"}
            </button>
          </p>
        </div>
      </div>
    );
  }

  return (
    <div className="page">
      <div className="app-container">
        <header className="hero-section">
          <div>
            <p className="small-title">Internship Practice Project</p>
            <h1>Student Task Manager</h1>
            <p className="subtitle">
              Welcome, {currentUser.fullName}. Manage your study tasks,
              deadlines, and AI-generated study suggestions.
            </p>
          </div>

          <button className="logout-btn" onClick={logoutUser}>
            Logout
          </button>
        </header>

        <section className="stats-grid">
          <div className="stat-card">
            <h3>{tasks.length}</h3>
            <p>Total Tasks</p>
          </div>

          <div className="stat-card">
            <h3>{pendingCount}</h3>
            <p>Pending</p>
          </div>

          <div className="stat-card">
            <h3>{inProgressCount}</h3>
            <p>In Progress</p>
          </div>

          <div className="stat-card">
            <h3>{completedCount}</h3>
            <p>Completed</p>
          </div>
        </section>

        <section className="ai-section">
          <div className="ai-card">
            <div>
              <p className="small-title-dark">AI Study Planner</p>
              <h2>Generate Study Task Suggestions</h2>
              <p>
                Enter a topic like React, Spring Boot, MySQL, OS, or internship
                interview. The system will suggest useful study tasks.
              </p>
            </div>

            <div className="ai-input-row">
              <input
                type="text"
                placeholder="Example: React final exam"
                value={aiTopic}
                onChange={(e) => setAiTopic(e.target.value)}
              />

              <button className="primary-btn" onClick={generateSuggestions}>
                {isGenerating ? "Generating..." : "Generate"}
              </button>
            </div>

            {suggestions.length > 0 && (
              <div className="suggestion-list">
                {suggestions.map((suggestion, index) => (
                  <div className="suggestion-card" key={index}>
                    <span>{suggestion}</span>
                    <button onClick={() => addSuggestionAsTask(suggestion)}>
                      Add as Task
                    </button>
                  </div>
                ))}
              </div>
            )}
          </div>
        </section>

        <section className="content-grid">
          <div className="form-card">
            <h2>Add New Task</h2>

            <label>Task Title</label>
            <input
              type="text"
              placeholder="Example: Study React"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
            />

            <label>Description</label>
            <textarea
              placeholder="Example: Complete frontend task manager UI"
              value={description}
              onChange={(e) => setDescription(e.target.value)}
            />

            <label>Due Date</label>
            <input
              type="date"
              value={dueDate}
              onChange={(e) => setDueDate(e.target.value)}
            />

            <button className="primary-btn" onClick={addTask}>
              Add Task
            </button>
          </div>

          <div className="task-section">
            <div className="task-header">
              <div>
                <h2>My Tasks</h2>
                <p>{filteredTasks.length} task(s) shown</p>
              </div>

              <select
                value={filterStatus}
                onChange={(e) =>
                  setFilterStatus(e.target.value as FilterStatus)
                }
              >
                <option value="ALL">All Tasks</option>
                <option value="PENDING">Pending</option>
                <option value="IN_PROGRESS">In Progress</option>
                <option value="COMPLETED">Completed</option>
              </select>
            </div>

            {filteredTasks.length === 0 ? (
              <div className="empty-box">
                <h3>No tasks found</h3>
                <p>Add a new task or change the filter.</p>
              </div>
            ) : (
              <div className="task-list">
                {filteredTasks.map((task) => (
                  <div className="task-card" key={task.id}>
                    <div className="task-card-header">
                      <h3>{task.title}</h3>
                      <span
                        className={`status-badge ${task.status
                          .toLowerCase()
                          .replace("_", "-")}`}
                      >
                        {task.status.replace("_", " ")}
                      </span>
                    </div>

                    <p className="task-description">{task.description}</p>

                    <div className="task-meta">
                      <span>Due: {task.dueDate}</span>
                    </div>

                    <div className="button-group">
                      <button
                        className="progress-btn"
                        onClick={() => updateStatus(task, "IN_PROGRESS")}
                      >
                        In Progress
                      </button>

                      <button
                        className="complete-btn"
                        onClick={() => updateStatus(task, "COMPLETED")}
                      >
                        Complete
                      </button>

                      <button
                        className="delete-btn"
                        onClick={() => deleteTask(task.id)}
                      >
                        Delete
                      </button>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </section>
      </div>
    </div>
  );
}

export default App;