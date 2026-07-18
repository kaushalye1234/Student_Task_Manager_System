import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";

import { beforeEach, describe, expect, test, vi } from "vitest";

import App from "./App";

describe("App authentication screen", () => {
  beforeEach(() => {
    localStorage.clear();
  });

  test("shows the login form when no user is saved", () => {
    render(<App />);

    expect(
      screen.getByRole("heading", {
        level: 1,
        name: "Login",
      })
    ).toBeInTheDocument();

    expect(
      screen.getByPlaceholderText("example@email.com")
    ).toBeInTheDocument();

    expect(
      screen.getByPlaceholderText("Minimum 6 characters")
    ).toBeInTheDocument();

    expect(
      screen.getByRole("button", {
        name: "Login",
      })
    ).toBeInTheDocument();
  });

  test("switches from login to registration", async () => {
    const user = userEvent.setup();

    render(<App />);

    const registerSwitchButton =
      screen.getByRole("button", {
        name: "Register",
      });

    await user.click(registerSwitchButton);

    expect(
      screen.getByRole("heading", {
        level: 1,
        name: "Create Account",
      })
    ).toBeInTheDocument();

    expect(
      screen.getByPlaceholderText(
        "Enter your full name"
      )
    ).toBeInTheDocument();

    expect(
      screen.getByRole("button", {
        name: "Register",
      })
    ).toBeInTheDocument();
  });

  test("shows an alert when login fields are empty", async () => {
    const user = userEvent.setup();

    const alertSpy = vi
      .spyOn(window, "alert")
      .mockImplementation(() => {});

    render(<App />);

    const loginButton =
      screen.getByRole("button", {
        name: "Login",
      });

    await user.click(loginButton);

    expect(alertSpy).toHaveBeenCalledWith(
      "Please enter email and password"
    );
  });
});