# TransactionController.java

**Purpose:** This file handles all incoming web requests related to financial transactions, like saving a new transaction, fetching a list of past transactions, or calculating dashboard summaries.

## Annotations Used
- **`@RestController`:** Tells Spring Boot this class receives web requests and will return data directly as JSON (instead of an HTML webpage).
- **`@RequestMapping("/api/transactions")`:** Sets the base URL for this file. Every method inside will start with `http://localhost:8080/api/transactions`.
- **`@RequiredArgsConstructor`:** (Lombok tool) Automatically wires up the `TransactionService` dependency so we don't have to write boilerplate constructor code.
- **`@PostMapping`:** Maps a method to handle HTTP POST requests (used when creating a brand new transaction).
- **`@GetMapping`:** Maps a method to handle HTTP GET requests (used when asking the server for existing data, like a list of transactions).
- **`@Valid`:** Ensures the incoming transaction data obeys all the rules (like ensuring amount is positive) before letting the method run.
- **`@RequestBody`:** Pulls the JSON data out of the main body of the HTTP POST request.
- **`@RequestParam`:** Pulls small pieces of data out of the URL (e.g., in `/api/transactions?month=5`, it extracts the `5`).

## Methods Line-by-Line

### `addTransaction()`
```java
@PostMapping
public ResponseEntity<TransactionResponseDTO> addTransaction(
        @Valid @RequestBody TransactionRequestDTO request,
        Authentication authentication) {
    String email = authentication.getName();
    TransactionResponseDTO response = transactionService.addTransaction(email, request);
    return new ResponseEntity<>(response, HttpStatus.CREATED);
}
```
- `@PostMapping`: Triggered when the frontend sends a POST request to `/api/transactions`.
- `public ResponseEntity...`: Declares the method, promising to return the saved transaction data.
- `(@Valid @RequestBody TransactionRequestDTO request, Authentication authentication)`: It takes the validated JSON transaction data, and also takes an `Authentication` object. Because the user is logged in (via their secure token), Spring Security automatically provides this `Authentication` object to tell us *who* is making the request.
- `String email = authentication.getName();`: Extracts the logged-in user's email address from the security token.
- `TransactionResponseDTO response = transactionService.addTransaction(email, request);`: Passes the user's email and the transaction data to the Service layer to actually save it to the database.
- `return new ResponseEntity<>(response, HttpStatus.CREATED);`: Returns the newly saved data back to the frontend with a `201 Created` status code.

### `getTransactions()`
```java
@GetMapping
public ResponseEntity<Page<TransactionResponseDTO>> getTransactions(
        @RequestParam(required = false) String financialYear,
        @RequestParam(required = false) Integer month,
        @RequestParam(required = false) String type,
        @RequestParam(required = false) String organizationName,
        @RequestParam(defaultValue = "0") int pageNumber,
        @RequestParam(defaultValue = "10") int pageSize,
        Authentication authentication) {
    
    String email = authentication.getName();
    Page<TransactionResponseDTO> response = transactionService.getTransactions(
            email, financialYear, month, type, organizationName, pageNumber, pageSize);
    
    return ResponseEntity.ok(response);
}
```
- `@GetMapping`: Triggered when the frontend asks to view transactions.
- `public ResponseEntity<Page<TransactionResponseDTO>>`: It returns a `Page` of data (like Page 1 of 5), so the frontend doesn't crash trying to load 10,000 transactions at once.
- `@RequestParam(required = false) ...`: It grabs all the filter options from the URL (e.g., if the user only wants to see "TDS" type transactions). `required = false` means the user doesn't have to provide them.
- `@RequestParam(defaultValue = "0") int pageNumber`: If the user doesn't specify a page, it defaults to page 0 (the first page).
- `String email = authentication.getName();`: Figures out who is asking.
- `Page<...> response = transactionService.getTransactions(...);`: Asks the Service to run a complex database query using all those filters.
- `return ResponseEntity.ok(response);`: Returns the chunk of data with a `200 OK` status.

### `getDashboardSummary()`
```java
@GetMapping("/dashboard-summary")
public ResponseEntity<java.util.Map<String, Object>> getDashboardSummary(
        @RequestParam String financialYear,
        Authentication authentication) {
    String email = authentication.getName();
    return ResponseEntity.ok(transactionService.getDashboardSummary(email, financialYear));
}
```
- `@GetMapping("/dashboard-summary")`: Triggered when the dashboard page loads.
- It takes the logged-in user's email and a required `financialYear` from the URL.
- It asks the Service layer to calculate all the totals (Total Tax Saved, Month-by-Month trend) and returns it as a generic `Map` (which becomes a flexible JSON object) with a `200 OK` status.

### `getAvailableFinancialYears()`
```java
@GetMapping("/financial-years")
public ResponseEntity<java.util.List<String>> getAvailableFinancialYears(Authentication authentication) {
    return ResponseEntity.ok(transactionService.getAvailableFinancialYears(authentication.getName()));
}
```
- Simply asks the Service layer for a unique list of all financial years this specific user has transactions for (e.g., ["2022-2023", "2023-2024"]), so the frontend can populate a dropdown menu.

## Connections to Other Files
- **`TransactionService.java`:** The heavy lifting, database querying, and math happen here. This controller just routes the traffic to the Service.
- **`TransactionRequestDTO` / `TransactionResponseDTO`:** The envelopes used to carry data in and out of the API.
- **Frontend `transactionApi.ts`:** The frontend file that actively makes HTTP requests to these endpoints.

---
**If asked about this file, key things to remember:**
- It uses the `Authentication` object heavily. It never trusts the frontend to tell it who is making the request; it reads the email directly from the secure JWT token that Spring Security parsed.
- It supports **Pagination** (`Page<...>`) to prevent sending too much data over the internet at once.
- It uses `@RequestParam` to handle optional filters like `financialYear` or `type`.
