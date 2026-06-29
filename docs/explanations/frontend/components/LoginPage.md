# LoginPage.tsx

**Purpose:** This file creates the login page where users type their email and password to access their TaxTracker dashboard.

## React Concepts Used
- **`useState`:** 
  - `const [errorMsg, setErrorMsg] = useState('');`
  - It tracks if there's a login error (like "Incorrect password"). If the user types the wrong password, we update `errorMsg`, and React instantly draws a red alert box on the screen to show the message.
- **`useForm`:** (From `react-hook-form`)
  - `const { register, handleSubmit, formState: { errors } } = useForm();`
  - This tool tracks everything typed into the email and password boxes. If the user clicks "Log In" but left the email blank, this tool stops the form from submitting and automatically provides the `errors.email` object so we can show a warning.
- **Hooks (`useNavigate`):**
  - `const navigate = useNavigate();`
  - This gives us a function we can use to instantly move the user to a different page (like the Dashboard) if their login is successful.

## Bootstrap Classes Used
- **`container mt-5`:** `container` stops the form from stretching too wide on big screens. `mt-5` (Margin-Top 5) pushes the form down a bit so it looks nice and isn't crammed against the top of the browser.
- **`row justify-content-center`:** This creates a row and shoves everything inside it exactly into the center of the screen.
- **`col-md-6`:** On medium and large screens, this restricts the white login box to take up exactly half (6 out of 12 columns) of the screen width.
- **`card shadow`:** `card` puts a neat border around the login form. `shadow` makes it look 3D by adding a shadow behind it.
- **`form-label` & `form-control`:** Styles the "Email" text and the actual typing box to look professional, adding a soft blue glow when clicked.
- **`alert alert-danger`:** Draws a bright red box. We use this to show the `errorMsg` if login fails.
- **`d-grid gap-2`:** Makes the "Log In" button span across the entire width of the form box.

## Functions

### `onSubmit()`
- **Trigger:** This runs when the user clicks the "Log In" button (because of `onSubmit={handleSubmit(onSubmit)}` on the form).
- **Step-by-step:**
  1. It receives the `data` (the email and password the user typed).
  2. `const response = await authApi.login(data);`: It sends the email and password to the backend server. The code waits (`await`) until the server replies.
  3. If the server says "Yes, credentials are correct", it returns a secure token (like a VIP pass) inside `response.token`.
  4. `localStorage.setItem('token', response.token);`: It saves this secure token directly into the browser's memory (LocalStorage). Now, whenever the user tries to view their dashboard or save a tax form, the browser can automatically show this VIP pass to the server to prove they are logged in.
  5. `localStorage.setItem('user', JSON.stringify(response));`: It also saves the user's basic details (like their name) in the browser so the app can say "Welcome, John!" without having to ask the server again.
  6. `navigate('/dashboard');`: It teleports the user straight to their dashboard.
  7. `catch (error: any) { ... }`: If the server replies "Wrong password", it jumps here and updates `errorMsg` to display the error on screen.

## Connections to Other Files
- **`authApi.ts`:** This component uses `authApi` to actually send the data over the internet to the backend.
- **`/dashboard` (DashboardPage.tsx):** The file navigates the user here upon success.
- **`/register` (RegisterPage.tsx):** It has a `<Link>` at the bottom to jump to the registration page if the user doesn't have an account yet.

---
**If asked about this file, key things to remember:**
- It is responsible for storing the **JWT token** in the browser's `localStorage` after a successful login. This token is what keeps the user logged in as they click around the app.
- It prevents empty submissions using `react-hook-form` before even bothering the backend server.
- It gracefully handles incorrect passwords by showing a red Bootstrap alert.
