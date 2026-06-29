# DashboardPage.tsx

**Purpose:** This is the main "Home" screen of the application after a user logs in. It shows a summary of their tax savings, graphs, and quick links to view transactions or file Form 90C.

## React Concepts Used
- **`useState`:**
  - `const [financialYear, setFinancialYear] = useState('');`: Keeps track of which financial year the user currently wants to look at. If they change the dropdown menu, this state updates, and the charts redraw themselves.
  - `const [summary, setSummary] = useState<any>(null);`: Stores the complex math and chart data (like `totalTaxSaved`) that comes back from the backend server.
- **`useEffect`:**
  - **First `useEffect` (Runs once):** When the dashboard first opens, this effect runs automatically to ask the server: "What financial years does this user have data for?" It then sets the dropdown to the current year.
  - **Second `useEffect` (Runs when `financialYear` changes):** This watches the `financialYear` state. Every time the user changes the dropdown, this effect says: "Oh, the year changed! Let me fetch the new dashboard summary data for this specific year."
- **Props (in `ProgressRing`):**
  - `const ProgressRing = ({ radius, stroke, progress, color }: any) => { ... }`
  - This is a tiny custom "Component inside a component". It takes in `Props` (settings) like `radius` (how big to draw the circle) and `progress` (how full to make it), so we can reuse the exact same code to draw both the blue TDS ring and the orange TCS ring.

## Bootstrap Classes Used
- **`container mt-5`:** Keeps the dashboard centered on the screen and adds space at the top.
- **`row g-4 mb-4`:** `row` creates a horizontal slice. `g-4` adds a nice, even "gap" between all the boxes inside the row so they don't touch. `mb-4` adds a margin to the bottom.
- **`col-md-5` and `col-md-7`:** Splits the top row into two uneven columns. The green "Total Tax Saved" box takes up 5/12 of the screen, and the Chart takes up 7/12.
- **`card h-100 shadow-sm border rounded p-3`:** 
  - `card`: Draws a box.
  - `h-100`: Forces the box to stretch to 100% of the height of the row (so the small box matches the height of the tall chart next to it).
  - `shadow-sm`, `border`, `rounded`: Makes it look like a clean, modern, floating white widget.
- **`display-4 fw-bold`:** Used on the big ₹ total. `display-4` makes the text massive, and `fw-bold` makes it bold.
- **`badge bg-success fs-6`:** Used for the "Submitted" status. `badge` creates a small pill-shaped background, `bg-success` makes it green, and `fs-6` (font-size 6) makes the text readable.

## Functions

### `getCurrentFY()`
- **Trigger:** Runs automatically when the dashboard first loads.
- **Step-by-step:** It gets today's date from the user's computer. Since the Indian financial year runs from April to March, it checks if the current month is April or later (`>= 4`). If yes, it returns something like "2023-2024". If it's January-March, it returns the previous block (e.g., "2022-2023").

### `handleForm90cClick()`
- **Trigger:** Runs when the user clicks the "Form 90C" navigation card at the bottom of the screen.
- **Step-by-step:** It checks the `summary` data we got from the server. If the status is already `'SUBMITTED'`, it pops up a warning toast saying "Already submitted!". Otherwise, it teleports the user to the Form 90C page, carrying the currently selected `financialYear` along with them.

## Connections to Other Files
- **`transactionApi.ts`:** Used to make the HTTP requests to `/api/transactions/financial-years` and `/api/transactions/dashboard-summary`.
- **`Recharts` Library:** Notice the `<LineChart>` and `<ProgressRing>`. We import an external library called `recharts` to easily draw the graph without doing crazy math.
- **`/form90c` (Form90cPart1Page.tsx):** Navigates the user to the form page when clicked.

---
**If asked about this file, key things to remember:**
- It uses two `useEffect` hooks heavily: one to get the list of available years, and another that "listens" to the year dropdown and fetches fresh data whenever it changes.
- It dynamically generates SVG (Scalable Vector Graphics) for the circular progress rings using plain math, without needing any heavy charting libraries for that specific part.
- It prevents users from trying to start a Form 90C if the backend has already marked that year as "SUBMITTED".
