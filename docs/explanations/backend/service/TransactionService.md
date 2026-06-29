# TransactionService.java

**Purpose:** This file is the mathematical and logical brain for everything related to transactions; it calculates financial years, sums up tax totals for the dashboard, and builds complex charts of month-by-month data.

## Annotations Used
- **`@Service`:** Tells Spring Boot that this class holds heavy business logic and should be made available to any Controller that asks for it.
- **`@RequiredArgsConstructor`:** (Lombok tool) Automatically hooks up the `TransactionRepository`, `UserRepository`, and `Form90CRepository` without us writing a constructor.

## Methods Line-by-Line

### `addTransaction()`
```java
public TransactionResponseDTO addTransaction(String email, TransactionRequestDTO request) {
    User user = userRepository.findByEmail(email).orElseThrow(...);
    
    int month = request.getTransactionDate().getMonthValue();
    int year = request.getTransactionDate().getYear();
    
    String financialYear;
    if (month >= 4) {
        financialYear = year + "-" + (year + 1);
    } else {
        financialYear = (year - 1) + "-" + year;
    }
    // ... builds and saves the transaction ...
}
```
- It first finds the specific user by their email. If they don't exist, it throws an error.
- **Financial Year Logic:** In India, the financial year runs from April to March. If the transaction happened in April or later (`month >= 4`), the financial year is `CurrentYear - NextYear` (e.g. 2023-2024). If it happened in Jan-Mar, it belongs to the previous year's block (e.g. 2022-2023). This logic automatically calculates the correct FY string without trusting the frontend to get it right!
- Finally, it builds a `Transaction` entity, saves it via the `transactionRepository`, and converts it into a safe DTO to return.

### `getTransactions()`
```java
public Page<TransactionResponseDTO> getTransactions(String email, String financialYear, Integer month, String type, String organizationName, int page, int size) {
    User user = userRepository.findByEmail(email).orElseThrow(...);

    Pageable pageable = PageRequest.of(page, size, Sort.by("transactionDate").descending());
    Page<Transaction> transactions = transactionRepository.findTransactionsWithFilters(
            user.getId(), financialYear, month, type, organizationName, pageable);

    return transactions.map(this::mapToResponse);
}
```
- It looks up the user to ensure they only see their own transactions.
- `PageRequest.of(...)`: It creates a "Pageable" object telling the database exactly which chunk of data to fetch (e.g. Page 0, 10 items per page) and to sort them from newest to oldest (`descending`).
- It calls `transactionRepository.findTransactionsWithFilters(...)` which runs a dynamic SQL query that only applies the filters (like `type = "TDS"`) if the user actually provided them.
- `transactions.map(...)`: It converts the raw database rows into clean `TransactionResponseDTO` objects for the frontend.

### `getDashboardSummary()`
This is the longest and most complex method in the app. Let's break it down into chunks:

**Chunk 1: Type Totals**
- `List<Object[]> typeTotals = transactionRepository.sumTaxAmountByType(...)`: It asks the database to add up all amounts, grouped by type (TDS vs TCS).
- A `for` loop goes through the results. It calculates the raw totals and then calculates the percentages: `(taxDeducted / totalAmount) * 100`.

**Chunk 2: Active Form Status**
- `Optional<Form90C> formOpt = form90cRepository.findByUserEmailAndFinancialYear(...)`: It checks if the user has started a Form 90C for this specific financial year.
- It returns `"SUBMITTED"` if the form is final, `"PENDING"` if it's a draft, or `"NONE"` if they haven't started one yet.

**Chunk 3: Tax Savings Trend (The Chart)**
- `List<Object[]> monthTotals = transactionRepository.sumTaxAmountByMonthAndYear(...)`: It asks the database to group tax amounts by month.
- `int[] fyMonths = {4, 5, 6, 7, 8, 9, 10, 11, 12, 1, 2, 3};`: This array represents the Indian Financial Year (April is month 4, March is month 3).
- It loops through these 12 months exactly in this order. If the user had transactions in that month, it adds the sum. If not, it enters `0`. This guarantees the frontend chart always has 12 perfectly ordered data points from Apr to Mar!
- **Trend Percentage:** At the end, it finds the *current* real-world month, looks at the previous month's total, and calculates the `% increase or decrease` to show the user on the dashboard.

### `getAvailableFinancialYears()`
- `transactionRepository.findDistinctFinancialYearsByUserId(...)`: This queries the database to find all unique `financialYear` strings linked to this user (e.g. "2022-2023", "2023-2024"). The frontend uses this to build the dashboard dropdown menu automatically based on real data.

## Connections to Other Files
- **`TransactionController.java`:** The API controller that calls all these methods.
- **`TransactionRepository.java`:** Used heavily here to run complex `SUM()` and `GROUP BY` SQL queries.
- **`Form90CRepository.java`:** Used specifically in the dashboard summary to check the user's form status.

---
**If asked about this file, key things to remember:**
- It uses Java's `LocalDate` to automatically figure out the Indian Financial Year (April - March) based on when the transaction happened.
- The `getDashboardSummary` method does the heavy mathematical lifting so the frontend React code doesn't have to; the frontend just receives a nice, pre-calculated JSON object and draws it.
- It uses Pagination (`PageRequest`) so the app stays lightning fast even if a user has 50,000 transactions.
