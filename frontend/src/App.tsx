import { useEffect, useState } from "react";
import axios from "axios";
import type { Task } from "./types/Task";
import "./App.css";

function App() {
  const [tasks, setTasks] = useState<Task[]>([]);
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");

  const API_URL = "http://localhost:8080/api/tasks";

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
      alert("Title is required");
      return;
    }

    const newTask = {
      title,
      description,
      status: "PENDING",
      dueDate: "2026-06-01",
    };

    try {
      await axios.post(API_URL, newTask);
      setTitle("");
      setDescription("");
      fetchTasks();
    } catch (error) {
      console.error("Error adding task:", error);
    }
  };

  const deleteTask = async (id: number) => {
    try {
      await axios.delete(`${API_URL}/${id}`);
      fetchTasks();
    } catch (error) {
      console.error("Error deleting task:", error);
    }
  };

  const updateStatus = async (
    task: Task,
    newStatus: "PENDING" | "IN_PROGRESS" | "COMPLETED"
  ) => {
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
    fetchTasks();
  }, []);

  return (
    <div className="app-container">
      <h1>Student Task Manager</h1>
      <p className="subtitle">React + Spring Boot + MySQL</p>

      <div className="form-card">
        <h2>Add New Task</h2>

        <input
          type="text"
          placeholder="Task Title"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
        />

        <textarea
          placeholder="Task Description"
          value={description}
          onChange={(e) => setDescription(e.target.value)}
        />

        <button onClick={addTask}>Add Task</button>
      </div>

      <div className="task-list">
        <h2>My Tasks</h2>

        {tasks.length === 0 ? (
          <p>No tasks found.</p>
        ) : (
          tasks.map((task) => (
            <div className="task-card" key={task.id}>
              <h3>{task.title}</h3>
              <p>{task.description}</p>

              <p>
                <strong>Status:</strong> {task.status}
              </p>

              <p>
                <strong>Due:</strong> {task.dueDate}
              </p>

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
          ))
        )}
      </div>
    </div>
  );
}

export default App;