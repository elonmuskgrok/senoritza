# authApi.ts

**Purpose:** This file acts as a clean, easy-to-read dictionary of the HTTP requests needed for Authentication (Logging in and Registering).

## Core Concepts Used
- **`import axiosClient from './axiosClient'`:** It uses our custom-configured `axiosClient` so that it automatically points to `http://localhost:8080/api`.
- **`async / await`:** These keywords are used because talking to the internet takes time. `await` tells the code to pause and wait for the server to reply before moving to the next line.

## Methods Line-by-Line

### `login: async (data: any)`
```typescript
login: async (data: any) => {
  const response = await axiosClient.post('/auth/login', data);
  return response.data;
}
```
- It accepts the user's email and password (`data`).
- It sends an HTTP POST request to `http://localhost:8080/api/auth/login`.
- When the backend replies (hopefully with a success message and the JWT token), it strips away all the raw HTTP wrapper data and just returns `response.data` so the React components get a clean JSON object.

### `register: async (data: any)`
- Exactly the same as login, but it points to `/auth/register` and passes the full registration form data (Name, Email, Mobile, Password).

---
**If asked about this file, key things to remember:**
- It abstracts the complexity of HTTP requests. Because of this file, the `LoginPage` component can simply call `authApi.login(data)` instead of having to write messy `axios` code directly inside the UI component.
