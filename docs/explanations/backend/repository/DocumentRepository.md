# DocumentRepository.java

**Purpose:** This file manages the database table that stores records of uploaded files (like PDFs and JPEGs) that support a user's Form 90C.

*Note: It doesn't store the actual file itself (the PDF is saved on the server's hard drive), it just stores the metadata (e.g., file name, size, and where it is saved on the hard drive).*

## Annotations & Core Concepts Used
- **`@Repository`:** Identifies this file as a database manager.
- **`extends JpaRepository<Document, Long>`:** Provides standard database actions.

## Methods Line-by-Line

### `existsByFormId(Long formId)`
- **Purpose:** This simple method checks if the database has at least one document record attached to a specific Form 90C. It returns `true` if a document exists, or `false` if it doesn't.
- **Usage:** This is a critical security and validation check. When the user clicks the final "Submit Form" button in Part 2, the `Form90CService` calls this method. If it returns `false`, the backend throws an error (`"Mandatory File not uploaded"`) and blocks the submission.

---
**If asked about this file, key things to remember:**
- It only contains one custom method (`existsByFormId`), which acts as the final gatekeeper to ensure a user doesn't submit a tax form without attaching proof.
- Thanks to `JpaRepository`, it automatically inherits the `save()` method, which the Service layer uses to log the file details after successfully saving a file to the hard drive.
