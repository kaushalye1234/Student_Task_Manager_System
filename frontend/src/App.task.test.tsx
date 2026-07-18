import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";

import axios, { type AxiosResponse } from "axios";

import {
  beforeEach,
  describe,
  expect,
  test,
  vi,
} from "vitest";

import App from "./App";
import type { Task } from "./types/Task";

vi.mock("axios", () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn(),
    isAxiosError: vi.fn(),
  },
}));

const sampleTasks: Task[] = [
  {
    id: 1,
    title: "Study React",
    description: "Review React hooks",
    status: "PENDING",
    priority: "HIGH",
    dueDate: "2099-08-01",
    createdAt: "2099-07-01T10:00:00",
  },
  {
    id: 2,
    title: "Prepare OS exam",
    description: "Study process scheduling",
    status: "IN_PROGRESS",
    priority: "MEDIUM",
    dueDate: "2099-08-02",
    createdAt: "2099-07-02T10:00:00",
  },
];

describe("App task management", () => {
  let token: string;

  beforeEach(() => {
    vi.clearAllMocks();
    localStorage.clear();

    const tokenPayload = btoa(
      JSON.stringify({
        exp: Math.floor(Date.now() / 1000) + 3600,
      })
    );

    token = `header.${tokenPayload}.signature`;

    localStorage.setItem(
      "taskManagerUser",
      JSON.stringify({
        id: 1,
        fullName: "Test Student",
        email: "student@example.com",
        message: "Login successful",
        token,
      })
    );

    vi.mocked(axios.get).mockResolvedValue({
      data: sampleTasks,
    } as AxiosResponse<Task[]>);

    vi.mocked(axios.post).mockResolvedValue({
      data: {},
    } as AxiosResponse<unknown>);
  });

  test("loads and displays tasks for the saved user", async () => {
    render(<App />);

    expect(
      await screen.findByText("Study React")
    ).toBeInTheDocument();

    expect(
      screen.getByText("Prepare OS exam")
    ).toBeInTheDocument();

    expect(
      screen.getByText("Review React hooks")
    ).toBeInTheDocument();

    await waitFor(() => {
      expect(axios.get).toHaveBeenCalledWith(
        "http://localhost:8080/api/tasks",
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
    });
  });

  test("filters displayed tasks using the search box", async () => {
    const user = userEvent.setup();

    render(<App />);

    await screen.findByText("Study React");

    const searchInput =
      screen.getByPlaceholderText("Search tasks...");

    await user.type(searchInput, "react");

    expect(
      screen.getByText("Study React")
    ).toBeInTheDocument();

    expect(
      screen.queryByText("Prepare OS exam")
    ).not.toBeInTheDocument();

    expect(
      screen.getByText("1 task(s) shown")
    ).toBeInTheDocument();
  });

  test("sends the correct request when adding a task", async () => {
    const user = userEvent.setup();

    render(<App />);

    await screen.findByText("Study React");

    const titleInput =
      screen.getByPlaceholderText("Example: Study React");

    const descriptionInput =
      screen.getByPlaceholderText(
        "Example: Complete frontend task manager UI"
      );

    const dueDateInput =
      document.querySelector(
        'input[type="date"]'
      ) as HTMLInputElement;

    const selectBoxes =
      screen.getAllByRole("combobox");

    const taskPrioritySelect =
      selectBoxes[0];

    await user.type(
      titleInput,
      "Write frontend tests"
    );

    await user.type(
      descriptionInput,
      "Test adding a new task"
    );

    await user.type(
      dueDateInput,
      "2099-12-31"
    );

    await user.selectOptions(
      taskPrioritySelect,
      "HIGH"
    );

    await user.click(
      screen.getByRole("button", {
        name: "Add Task",
      })
    );

    await waitFor(() => {
      expect(axios.post).toHaveBeenCalledWith(
        "http://localhost:8080/api/tasks?userId=1",
        {
          title: "Write frontend tests",
          description: "Test adding a new task",
          status: "PENDING",
          priority: "HIGH",
          dueDate: "2099-12-31",
        },
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
    });
  });
});