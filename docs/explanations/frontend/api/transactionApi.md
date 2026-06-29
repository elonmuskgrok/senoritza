# transactionApi.ts

**Purpose:** This file acts as a dictionary of all the HTTP requests related to transactions and dashboard summaries. It provides clean, easy-to-use methods for the React components.

## Core Concepts Used
- **`import axiosClient from './axiosClient'`:** It uses our custom, pre-configured `axiosClient`. This means it doesn't need to specify `http://localhost:8080/api` or worry about attaching the JWT token; `axiosClient` does all that automatically.

## Methods Line-by-Line

### `addTransaction: async (data: any)`
- Takes the transaction details typed by the user (like Amount and Date) and POSTs them to `/transactions` to save them in the database.

### `getTransactions: async (params: any)`
- Fetches the paginated list of transactions for the table on the Transactions page.
- Note the `{ params }` part: `axiosClient.get('/transactions', { params })`. This automatically takes a JavaScript object like `{ type: "TDS", pageNumber: 0 }` and formats it into a URL string like `/transactions?type=TDS&pageNumber=0` for us.

### `downloadTransactions: async (params: any, format: string = 'JSON')`
```typescript
downloadTransactions: async (params: any, format: string = 'JSON') => {
  const response = await axiosClient.get('/transactions/download', {
    params: { ...params, format },
    responseType: 'blob'
  });
  return response.data;
}
```
- **Crucial Detail:** Notice the `responseType: 'blob'`. Normally, `axios` expects the server to reply with text or JSON. A file download is binary data (a Blob). If we forget `responseType: 'blob'`, the downloaded PDF or Excel file would be corrupted and unreadable.

### `getDashboardSummary` & `getAvailableFinancialYears`
- Simple GET requests used specifically by the `DashboardPage` to fetch the math for the charts and the options for the dropdown menus.

---
**If asked about this file, key things to remember:**
- It uses `{ params }` in GET requests to cleanly pass URL query strings to the backend without messy string concatenation.
- The `downloadTransactions` method absolutely requires `responseType: 'blob'` to ensure the downloaded file isn't corrupted.
