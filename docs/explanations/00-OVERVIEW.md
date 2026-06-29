# 00-OVERVIEW: How TaxTracker Works

**Purpose:** This document explains the big picture of the TaxTracker application. Think of this as the "bird's-eye view" of how the frontend (what the user sees) and the backend (where the data lives) talk to each other.

## The Architecture (The Big Picture)
TaxTracker is split into two completely separate halves:
1. **Frontend (React + TypeScript):** The visual interface running in the user's web browser. It uses HTML, CSS, and Bootstrap to draw buttons and forms. It has zero access to the database.
2. **Backend (Spring Boot + Java):** The hidden engine running on a server. It connects to the database, checks passwords, calculates math, and enforces rules.

These two halves communicate via an **API (Application Programming Interface)**. Think of the API as a waiter in a restaurant:
- The Frontend (customer) asks the API (waiter) for something (e.g., "Save this new user").
- The API takes the order to the Backend (kitchen).
- The Backend cooks the data and gives it back to the API.
- The API delivers the response (e.g., "User saved successfully!") back to the Frontend.

---

## Example: The Registration Flow
Let's walk through exactly what happens when a new user types their details into the Registration page and clicks "Sign Up".

### Step 1: The User Clicks "Sign Up" (Frontend UI)
*File: `frontend/src/components/RegisterPage.tsx`*
- The user fills out the form (Name, Email, Password, PAN, Phone).
- As they type, React tracks what they are typing in memory (using something called `useState` or `react-hook-form`).
- When they click "Sign Up", a function is triggered (e.g., `onSubmit`).
- This function gathers all the typed details into a neat package (a JavaScript Object).

### Step 2: Sending the Request (Frontend API)
*File: `frontend/src/api/authApi.ts`*
- The frontend doesn't talk to the database directly. Instead, it uses a library called `axios` to make an HTTP POST request over the internet.
- It sends the package of user details to a specific URL on the backend, usually something like `http://localhost:8080/api/auth/register`.

### Step 3: Catching the Request (Backend Controller)
*File: `backend/src/main/java/com/siva/api/AuthController.java`*
- The backend is listening for requests. The **Controller** is the bouncer at the door.
- The `AuthController` has a method specifically mapped to `/api/auth/register`.
- It catches the incoming package (which is in JSON format) and converts it into a Java object called a **DTO** (Data Transfer Object).
- The Controller checks if the data looks valid (e.g., "Is the email actually an email?"). If yes, it hands the data to the Service layer.

### Step 4: Applying Business Rules (Backend Service)
*File: `backend/src/main/java/com/siva/service/AuthService.java`*
- The **Service** is the brain of the operation. It contains the "business logic".
- The `AuthService` first asks: "Does this email already exist?"
- If the email is new, the Service hashes the password (turns it into unreadable gibberish for security) so that even if the database is hacked, the password is safe.
- It then creates a new `User` entity (a Java object that represents a row in the database) and hands it to the Repository.

### Step 5: Saving to the Database (Backend Repository)
*File: `backend/src/main/java/com/siva/repository/UserRepository.java`*
- The **Repository** is the only part of the application that directly touches the database.
- It takes the `User` entity from the Service and generates the raw SQL query behind the scenes (`INSERT INTO users ...`).
- The new user is permanently saved to the MySQL database.

### Step 6: The Journey Back
- The **Repository** tells the **Service**: "Done, it's saved."
- The **Service** tells the **Controller**: "Everything went well."
- The **Controller** sends an HTTP Response back to the **Frontend API** with a success code (`200 OK`) and a success message.
- The **Frontend API** gives the success message back to the **RegisterPage** component.
- The **RegisterPage** displays a green success alert to the user or redirects them to the Login page.

---
**If asked about the architecture, key things to remember:**
- The frontend and backend are completely separate and only talk via HTTP API requests.
- The backend is strictly layered: Controllers (web traffic) -> Services (logic) -> Repositories (database).
- DTOs (Data Transfer Objects) are used to carry data between the frontend and the Controller, while Entities are used to represent data exactly as it looks in the database.
