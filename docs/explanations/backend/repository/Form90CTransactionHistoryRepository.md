# Form90CTransactionHistoryRepository.java

**Purpose:** This file manages the database table that stores the individual transaction rows specifically attached to a Form 90C. 

*Note: This is different from the main `TransactionRepository`, which stores the user's master list of everyday transactions. This table only stores the snapshot of transactions the user explicitly added to their Form 90C.*

## Annotations & Core Concepts Used
- **`@Repository`:** Identifies this file as a database manager.
- **`extends JpaRepository<Form90CTransactionHistory, Long>`:** Provides standard database actions (save, delete, find).

## Methods Line-by-Line

### `findByFormId(Long formId)`
- **Purpose:** Fetches all the transactions attached to a specific form.
- **Usage:** When the user loads a saved draft (Part 1 of the form), the backend uses this method to pull up their saved table rows so they don't have to re-type them.

### `existsByFormId(Long formId)`
- **Purpose:** Checks if there is at least one transaction attached to a form. It returns `true` or `false`.
- **Usage:** Used as a strict validation rule during final submission. The backend refuses to submit Form 90C if this method returns `false` (meaning the form is empty).

### `deleteByFormId(Long formId)`
- **Purpose:** Deletes all transactions currently attached to a form.
- **Usage:** When a user clicks "Save Draft" again on an existing form, the backend uses this method to wipe the old transaction rows and then saves the newly submitted ones. This prevents the database from accidentally creating duplicate rows every time the user hits save.

---
**If asked about this file, key things to remember:**
- It is heavily used to manage the "child" records (transactions) that belong to a "parent" record (Form 90C).
- The `deleteByFormId` method is crucial for safely updating a draft without accidentally duplicating data.
