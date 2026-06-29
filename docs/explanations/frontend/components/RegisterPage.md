# RegisterPage.tsx

**Purpose:** This file creates the registration web page where new users can type in their details (name, email, password, address) to create a new TaxTracker account.

## React Concepts Used
- **`useState`:** 
  - `const [errorMsg, setErrorMsg] = useState('');` 
  - We use `useState` to remember if there is an error message we need to show the user (like "Email already taken"). It starts as empty `''`. If registration fails, we update this state, which forces React to redraw the screen and display the red error banner.
- **`useForm`:** (This is a custom hook from the `react-hook-form` library, not built-in React, but very important here).
  - `const { register, handleSubmit, formState: { errors } } = useForm();`
  - It tracks what the user is typing in every input box, checks if the inputs are valid (e.g., did they leave a required field blank?), and gathers all the data together when the user hits submit.
- **Hooks (`useNavigate`):** 
  - `const navigate = useNavigate();`
  - This hook gives us a function we can call to instantly change the URL in the browser, teleporting the user to a different page without reloading the tab.

## Bootstrap Classes Used
- **`container mt-5`:** `container` perfectly centers the content on the screen and limits how wide it can get. `mt-5` adds Margin to the Top (level 5), pushing the form down so it isn't glued to the top of the browser.
- **`row justify-content-center`:** `row` creates a horizontal slice of space. `justify-content-center` pushes whatever is inside it into the exact middle of the screen.
- **`col-md-8`:** On medium screens or larger (like laptops), this element will take up exactly 8 out of 12 columns of the grid, making the form a nice, readable width.
- **`card shadow`:** `card` draws a neat white box around the form. `shadow` adds a soft drop-shadow behind the box to make it look like it's floating.
- **`form-label` & `form-control`:** These style the text and the input boxes to look modern, giving them nice borders and blue glowing outlines when clicked.
- **`is-invalid` & `invalid-feedback`:** If the user makes a mistake (e.g. leaves a required field blank), we add `is-invalid` to make the input box border turn red, and use `invalid-feedback` to show a red warning message below it.
- **`d-grid gap-2`:** `d-grid` turns the container into a grid layout, and `gap-2` adds space between items. Combined with a button, it makes the "Register" button stretch nicely across the whole width of the form.

## Functions

### `onSubmit()`
- **Trigger:** This runs exactly when the user clicks the blue "Register" button at the bottom of the form (because the form tag has `onSubmit={handleSubmit(onSubmit)}`).
- **Step-by-step:**
  1. It receives a `data` object containing everything the user typed (like `{ firstName: "John", email: "john@test.com" }`).
  2. `await authApi.register(data);`: It tries to send this data to our backend server over the internet. Because it's an `await`, the code pauses here and waits for the server to reply.
  3. `navigate('/login');`: If the server replies "Success!", this line teleports the user to the Login page.
  4. `catch (error: any) { ... }`: If the server replies with an error (e.g., "User already exists") or if the internet is down, the code jumps into this catch block.
  5. `setErrorMsg(...)`: It takes the error message and saves it to the `errorMsg` state, which causes the red alert box to appear on screen.

## Connections to Other Files
- **`authApi.ts`:** This component doesn't know how to talk to the internet directly. It imports `authApi` and asks it to make the actual HTTP request.
- **`/login` (LoginPage.tsx):** If registration is successful, or if the user clicks the "Already have an account?" link, this file redirects them to the Login page.

---
**If asked about this file, key things to remember:**
- It relies entirely on `react-hook-form` to track inputs and validate them, rather than manually checking if every box is empty.
- It never touches the backend directly; it delegates all HTTP requests to `authApi`.
- Error messages from the backend are caught and displayed gracefully in a red Bootstrap alert banner at the top of the form.
