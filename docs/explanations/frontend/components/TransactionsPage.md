# TransactionsPage.tsx

**Purpose:** This page acts as a ledger. It allows users to quickly add new transactions, view a paginated list of all their past transactions, filter them, and download them.

## React Concepts Used
- **`useState`:** 
  - `const [transactions, setTransactions] = useState<any[]>([]);`: Stores the list of transactions currently visible on the screen.
  - `const [filters, setFilters] = useState<any>({});`: Stores whatever the user has typed into the search boxes (e.g., `{ type: "TDS", month: "5" }`).
  - `const [page, setPage] = useState(0);`: Tracks which page of the table the user is looking at.
- **`useCallback`:**
  - `const fetchTransactions = useCallback(async () => { ... }, [filters, page]);`
  - This is an advanced React hook. It "memorizes" the function so React doesn't accidentally recreate it every single time the user clicks a button. The `[filters, page]` part means: "Only recreate this function if the filters or the page number changes."
- **`useEffect`:**
  - One `useEffect` fetches the available financial years when the page first loads.
  - Another `useEffect` runs `fetchTransactions()` whenever the `page` or `filters` change, keeping the table instantly up to date.
- **`useForm`:**
  - Used here specifically for the "Add Sample Transaction" form to handle inputs and validation without causing the whole page to reload.

## Bootstrap Classes Used
- **`card mb-4 shadow-sm bg-light`:** The filter section uses `bg-light` to give it a slightly gray background, visually separating it from the white "Add Transaction" box above it.
- **`row g-3`:** Creates a grid layout for the filter inputs, ensuring they stack neatly on mobile phones but sit side-by-side on laptops.
- **`table table-striped table-hover`:**
  - `table`: Applies basic Bootstrap table styling.
  - `table-striped`: Makes every other row slightly darker (zebra striping) so it's easier to read across long rows.
  - `table-hover`: Highlights a row in light gray when the user hovers their mouse over it.
- **`table-dark`:** Makes the header row (`<thead>`) of the table dark gray/black with white text.
- **`table-responsive`:** Wraps the table. If the screen is too small (like a phone), instead of breaking the layout, it adds a horizontal scrollbar just to the table itself.

## Functions

### `onAddSubmit()`
- **Trigger:** Runs when the user fills out the small "Add Transaction" form and hits submit.
- **Step-by-step:**
  1. It takes the text string amounts (like `"500.00"`) and uses `parseFloat()` to turn them into real math numbers (`500.00`).
  2. It sends the payload to the backend via `transactionApi.addTransaction(payload)`.
  3. If successful, it shows a green popup (`toast.success`), clears the form (`resetAdd()`), and immediately calls `fetchTransactions()` to refresh the table so the new transaction appears instantly.

### `handleFilterChange()`
- **Trigger:** Runs every single time the user types a letter into a filter box or changes a dropdown.
- **Step-by-step:** It updates the `filters` state with the new value. It also forces `setPage(0)`. (Why? Because if you are on Page 5, and you search for something that only has 2 results, staying on Page 5 would show a blank screen!).

### `downloadData()`
- **Trigger:** Runs when the user clicks the "Download" button.
- **Step-by-step:**
  1. It asks the backend for a `Blob` (a big chunk of raw file data) based on the current filters and the selected format (JSON, CSV, PDF, etc.).
  2. `const url = window.URL.createObjectURL(new Blob([blob]));`: It tricks the browser into thinking this chunk of memory is an actual file sitting on the internet.
  3. `const a = document.createElement('a'); ... a.click();`: It invisibly creates an `<a>` link on the page, sets its `href` to our fake file URL, clicks it automatically to trigger the browser's download prompt, and then deletes the link.

## Connections to Other Files
- **`transactionApi.ts`:** Used to make all the backend requests.
- **Backend `TransactionController.java`:** The `/api/transactions` endpoint directly powers the table and the `downloadData` function.

---
**If asked about this file, key things to remember:**
- It uses **Server-Side Pagination**. Instead of downloading 10,000 transactions and hiding 9,990 of them, it only asks the server for 10 at a time, making it very fast.
- The `handleFilterChange` automatically resets the user to Page 1 to prevent them from getting "stuck" on empty pages when filtering.
- The download function is highly dynamic; it sends whatever the *current* filters are to the backend, so if the user searches for "TDS", the downloaded Excel file will only contain "TDS" rows.
