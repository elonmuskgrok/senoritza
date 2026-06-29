# Form90cPart1Page.tsx

**Purpose:** This page represents Part 1 of Form 90C. It allows users to enter their basic details, list all their relevant financial transactions for the year, save their progress as a draft, or proceed to Part 2.

## React Concepts Used
- **`useRef`:**
  - `const hasLoadedDraft = useRef(false);`
  - `useRef` is like a secret sticky note that React remembers, but updating it doesn't cause the screen to redraw. We use it here to track if we've already shown the "Draft Loaded" popup to the user, so we don't accidentally annoy them by showing it 5 times.
- **`useFieldArray`:**
  - `const { fields, append, remove } = useFieldArray({ control, name: "transactionHistory" });`
  - A special tool from `react-hook-form` used for dynamic lists. Because a user might have 1 transaction or 50 transactions, `useFieldArray` lets us easily add new empty rows (`append`) or delete rows (`remove`) on the fly.
- **`useLocation`:**
  - `const location = useLocation();`
  - Sometimes a user arrives at this page by clicking a specific year on the Dashboard. This hook lets us peek at the "luggage" (`location.state`) they brought with them from the previous page so we can automatically select the right Financial Year in the dropdown.

## Bootstrap Classes Used
- **`table table-bordered`:** Draws a solid line around every single cell in the transaction table so it looks like a spreadsheet.
- **`table-light`:** Gives the table header (`<thead>`) a light gray background color.
- **`btn-outline-danger` & `btn-outline-secondary`:** 
  - `btn-outline-danger` is used for the "Remove" button (a white button with a red border that turns solid red on hover).
  - `btn-outline-secondary` is used for "Add Row", making it look less important than the bright blue "Save & Continue" button.

## Functions

### `useEffect` (Draft Loader)
```typescript
useEffect(() => {
  const fetchDraft = async () => { ... }
}, [email, selectedFY]);
```
- **Trigger:** This runs automatically whenever the page loads or the user changes the `selectedFY` dropdown.
- **Step-by-step:** It asks the backend, "Does this user already have a draft saved for 2023-2024?" If yes, it pulls that data and injects it into the form using the `reset()` function, instantly populating all their old rows.

### `saveForm()`
- **Trigger:** This is a helper function called by either "Save Draft" or "Save & Continue".
- **Step-by-step:**
  1. It loops through all the transactions the user typed and forces the amounts into real numbers using `parseFloat()`. (Because HTML inputs treat numbers as text by default, which can cause math errors later).
  2. If `navigateToNext` is true (they clicked "Save & Continue"), it calls `formApi.saveForm`. The backend checks everything strictly. If successful, it navigates the user to Part 2 (`/form90c/upload`), passing the `formId` in the luggage.
  3. If `navigateToNext` is false (they clicked "Save Draft"), it calls `formApi.saveDraft`, which tells the backend to save the data without strictly checking if all required fields are filled.

### `onSaveDraft()`
- **Trigger:** Clicking the gray "Save Draft" button.
- Notice we use `getValues()` instead of `handleSubmit()`. `handleSubmit` will block the save if required fields are missing. `getValues` just grabs whatever the user typed so far, ignoring the rules, which is exactly what we want for a draft!

### `onSaveAndNext()`
- **Trigger:** Clicking the blue "Save & Continue" button.
- Notice the button has `onClick={handleSubmit(onSaveAndNext)}`. This means it *will* block the user and show red warnings if they forgot to type their name or an organization.

## Connections to Other Files
- **`formApi.ts`:** Used to save/load the form data.
- **`transactionApi.ts`:** Used just once to get the list of valid Financial Years.
- **`/form90c/upload` (Form90cPart2Page.tsx):** The exact destination this page sends the user to when they successfully click "Save & Continue".

---
**If asked about this file, key things to remember:**
- It is highly dynamic, relying on `useFieldArray` to allow users to infinitely add or remove rows in the transaction table.
- It differentiates between a "Draft" (which bypasses form validation rules) and a "Final Save" (which enforces them using `handleSubmit`).
- It uses `useLocation` to smoothly carry over the Financial Year selected on the dashboard.
