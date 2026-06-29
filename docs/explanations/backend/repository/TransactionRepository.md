# TransactionRepository.java

**Purpose:** This file acts as the direct bridge between the Java code and the `transactions` database table. It contains complex, custom SQL queries used to filter data and calculate dashboard totals.

## Annotations & Core Concepts Used
- **`@Repository`:** Tells Spring Boot this interface deals with database access.
- **`extends JpaRepository<Transaction, Long>`:** This single line of code magically gives us free, built-in methods like `save()`, `findById()`, and `delete()` without us having to write any SQL at all.
- **`@Query("...")`:** Used when we need to write custom SQL (specifically JPQL - Java Persistence Query Language) because the built-in methods aren't powerful enough.
- **`@Param("...")`:** Links a variable in the Java method (like `String type`) to a placeholder in the `@Query` string (like `:type`).

## Custom Queries Line-by-Line

### `findTransactionsWithFilters`
```java
@Query("SELECT t FROM Transaction t WHERE t.user.id = :userId " +
       "AND (:financialYear IS NULL OR t.financialYear = :financialYear) " +
       "AND (:month IS NULL OR t.txnMonth = :month) " + ... )
Page<Transaction> findTransactionsWithFilters(...)
```
- This is a highly dynamic search query used for the Transactions table.
- `(:financialYear IS NULL OR t.financialYear = :financialYear)`: This is a clever trick. If the user didn't select a financial year filter on the frontend (so it's `NULL`), this part of the query is ignored. If they did provide a year, it strictly matches it.
- It returns a `Page<Transaction>`, meaning it automatically limits the results (e.g., to just 10 items) and counts the total number of records for us.

### `sumTaxAmountByType`
```java
@Query("SELECT t.type, SUM(t.taxAmount), SUM(t.amount) FROM Transaction t WHERE t.user.id = :userId AND t.financialYear = :financialYear GROUP BY t.type")
List<Object[]> sumTaxAmountByType(...)
```
- Used for the Dashboard's blue/orange rings.
- It asks the database to add up (`SUM`) all the tax amounts and total amounts, but group the results into buckets based on the `type` (TDS vs TCS). 
- It returns a raw `Object[]` array because the result isn't a single "Transaction" anymore, it's a mix of Strings and Numbers (e.g., `["TDS", 5000.00, 50000.00]`).

### `sumTaxAmountByMonthAndYear`
```java
@Query("SELECT YEAR(t.transactionDate), MONTH(t.transactionDate), SUM(t.taxAmount) FROM Transaction t WHERE ... GROUP BY YEAR(t.transactionDate), MONTH(t.transactionDate)")
List<Object[]> sumTaxAmountByMonthAndYear(...)
```
- Used for the Dashboard's Line Chart.
- It extracts the `YEAR()` and `MONTH()` from the exact date the transaction occurred, and groups all transactions that happened in that specific month together to get a single sum.

### `findDistinctFinancialYearsByUserId`
```java
@Query("SELECT DISTINCT t.financialYear FROM Transaction t WHERE t.user.id = :userId AND t.financialYear IS NOT NULL ORDER BY t.financialYear DESC")
List<String> findDistinctFinancialYearsByUserId(...)
```
- Used to build the "Financial Year" dropdown menus on the frontend.
- `DISTINCT`: Ensures that even if the user has 500 transactions in "2023-2024", the database only returns the string "2023-2024" exactly one time.

## Connections to Other Files
- **`TransactionService.java`:** The Service layer calls these repository methods constantly to fetch and crunch data for the user.

---
**If asked about this file, key things to remember:**
- Because it's an `interface`, we never actually write the implementation code (the body) of these methods! Spring Boot uses "magic" to read the `@Query` annotation and automatically generate the Java code to execute it behind the scenes.
- The `(:field IS NULL OR ...)` trick is heavily used to allow the frontend to pass optional filters without us needing to write 20 different queries for every possible combination of filters.
