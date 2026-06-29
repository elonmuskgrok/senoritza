# SubmissionRepository.java

**Purpose:** This file manages the database table that acts as a final "receipt ledger". Once a Form 90C is finalized, a record is created here to confirm the submission was successful.

## Annotations & Core Concepts Used
- **`@Repository`:** Identifies this file as a database manager.
- **`extends JpaRepository<Submission, Long>`:** Provides standard database actions like `save()`.

## Methods Line-by-Line

### `existsByFormId(Long formId)`
- **Purpose:** Checks if a receipt/submission record already exists for a specific Form 90C. Returns `true` or `false`.
- **Usage:** This is an anti-spam / anti-duplicate feature. When the user clicks "Submit Final Form", the `Form90CService` first calls this method. If it returns `true`, it means the form was already submitted seconds ago (perhaps the user double-clicked the button), and the backend blocks the second attempt with an error (`"Submission already exists for this form"`).

---
**If asked about this file, key things to remember:**
- It is the simplest repository in the app.
- Its main job (via `existsByFormId`) is to prevent double-submissions if a user gets impatient and aggressively clicks the Submit button multiple times before the server can respond.
