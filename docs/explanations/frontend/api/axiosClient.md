# axiosClient.ts

**Purpose:** This file configures `axios` (the library used to make HTTP requests) so that we don't have to repeatedly type out the base URL or attach the secure JWT token for every single request made by the frontend.

## Core Concepts Used
- **`axios.create(...)`:**
  - Creates a custom version of `axios` named `axiosClient`.
  - Sets the `baseURL` to `http://localhost:8080/api`. This means that in the rest of our app, we can just say `axiosClient.post('/transactions')` instead of typing out the full URL every time.
- **Interceptors:**
  - Interceptors are like toll booths. Every HTTP request leaving the app, and every response coming back, *must* pass through these interceptors first.

## Code Line-by-Line

### Request Interceptor
```typescript
axiosClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token && config.headers) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});
```
- **What it does:** Right before a request leaves the browser, this pauses it. It checks if the user has a `token` saved in their browser memory (`localStorage`). If they do, it attaches it to the request header (`Bearer XYZ123...`).
- **Why it's important:** Without this, the frontend developers would have to manually fetch the token and attach it to the headers on every single API call they write. This automates the security.

### Response Interceptor
```typescript
axiosClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);
```
- **What it does:** When the backend server replies, this pauses the reply. If the reply was successful, it just passes it along to the app. 
- **Handling 401 Unauthorized:** If the backend replies with a `401 Unauthorized` error, it means the user's secure token has expired (or is invalid). 
- **Auto-Logout:** Instead of letting the app crash or show weird errors, this interceptor automatically deletes the expired token and instantly teleports the user back to the `/login` screen so they can sign in again.

---
**If asked about this file, key things to remember:**
- It is the backbone of the frontend's communication. All API files (`authApi`, `formApi`) use `axiosClient` instead of the default `axios`.
- It implements "Auto-Logout" globally. If a token expires while a user is working, they are safely redirected to the login page without the app breaking.
