# Form90CRepository.java

**Purpose:** This file handles all database interactions for the main Form 90C records, allowing the application to save drafts, fetch forms by financial year, or check if a form has already been submitted.

## Annotations & Core Concepts Used
- **`@Repository`:** Tells Spring Boot this interface deals with the database.
- **`extends JpaRepository<Form90C, Long>`:** Provides all the built-in database methods like `save()` and `findById()`.

## Methods Line-by-Line

### `findByIdAndUserId(Long id, Long userId)`
- Used extensively for **Security**. When a user tries to upload a document or submit a form, we don't just look up the `formId`. We look up the `formId` AND the `userId` together. If a hacker tries to modify Form ID 123 (which belongs to someone else), this query will return empty, and the server will block the action.

### `findByUserEmailAndFinancialYear(String email, String financialYear)`
- This is the most frequently used method in this file. 
- A user is only allowed to have exactly **one** Form 90C per financial year. 
- When the user opens the Form 90C page, the backend uses this method to check if a form (or draft) already exists for that year, ensuring they can resume their work instead of creating a duplicate.

### `findByUserEmail(String email)` & `findByUserEmailAndStatus(String email, String status)`
- These are fallback methods to fetch forms based on the user's email, or based on their email and a specific status (like "DRAFT" or "SUBMITTED").

## Connections to Other Files
- **`Form90CService.java`:** Heavily relies on this repository to execute the "Find or Create" logic for drafts.
- **`TransactionService.java`:** Uses this repository in the `getDashboardSummary` method to check the status of the form for the current year, so the dashboard can display a "Pending" or "Submitted" badge.

---
**If asked about this file, key things to remember:**
- It does not contain any complex `@Query` SQL strings. All queries are simple enough that Spring Data JPA can automatically write the SQL just by reading the method names.
- The `findByIdAndUserId` method is a critical security feature to prevent users from accessing or modifying each other's tax forms.
