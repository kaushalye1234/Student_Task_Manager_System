import { useCallback, useEffect, useState } from "react";
import axios from "axios";
import type { Task } from "./types/Task";
import "./App.css";

type TaskStatus = "PENDING" | "IN_PROGRESS" | "COMPLETED";
type FilterStatus = "ALL" | TaskStatus;

function App() {
  const [tasks, setTasks] = useState<Task[]>([]);
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [dueDate, setDueDate] = useState("");
  const [filterStatus, setFilterStatus] = useState<FilterStatus>("ALL");

  const API_URL = "http://localhost:8080/api/tasks";

  const fetchTasks = useCallback(async () => {
    try {
      const response = await axios.get<Task[]>(API_URL);
      setTasks(response.data);
    } catch (error) {
      console.error("Error fetching tasks:", error);
    }
  }, [API_URL]);

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

  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect -- load initial tasks from API.
    void fetchTasks();
  }, [fetchTasks]);

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

  return (
    <div className="page">
      <div className="app-container">
        <header className="hero-section">
          <div>
            <p className="small-title">Internship Practice Project</p>
            <h1>Student Task Manager</h1>
            <p className="subtitle">
              Manage your study tasks, assignments, and deadlines using React,
              Spring Boot, and MySQL.
            </p>
          </div>
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
                onChange={(e) => setFilterStatus(e.target.value as FilterStatus)}
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