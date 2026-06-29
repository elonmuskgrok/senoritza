# Form90CService.java

**Purpose:** This file contains the complex business logic for filling out Form 90C. It saves drafts, securely handles document uploads (like checking for viruses disguised as PDFs), and finalizes form submissions.

## Annotations Used
- **`@Service`:** Tells Spring Boot this is a core logic file and should be created and made available automatically.
- **`@RequiredArgsConstructor`:** (Lombok tool) Automatically hooks up five different repositories (Database managers) so we can talk to the tables for Forms, History, Documents, Submissions, and Users.
- **`@Transactional`:** Used on methods that write multiple things to the database (e.g., saving a Form AND saving its Transaction History). If one fails, the other is rolled back (undone) so we don't have broken data.

## Methods Line-by-Line

### `saveDraft()`
```java
@Transactional
public Map<String, Object> saveDraft(String email, Form90CDraftRequestDTO request) {
    User user = userRepository.findByEmail(email).orElseThrow(...);

    Form90C form = form90cRepository.findByUserEmailAndFinancialYear(email, request.getFinancialYear())
            .orElse(new Form90C());
    // ... update form details ...
    form.setStatus("DRAFT");
    
    if (form.getId() != null) {
        historyRepository.deleteByFormId(form.getId());
    }
    
    Form90C savedForm = form90cRepository.save(form);
    // ... loops through request.getTransactionHistory() and saves them ...
    return response;
}
```
- Fetches the user by email.
- It tries to find an existing form for this specific `financialYear`. If it finds one, it updates it. If it doesn't (`orElse(new Form90C())`), it creates a brand new one.
- Sets the status strictly to `"DRAFT"`.
- `historyRepository.deleteByFormId(...)`: If the user is updating an existing draft, it first deletes all the old transactions attached to this form to avoid duplicates, then it saves the newly provided list of transactions.

### `saveForm()`
- This method is almost identical to `saveDraft`, except the Controller ensures that the `Form90CRequestDTO` is fully validated before this method even runs.
- It also includes strict validation for the `financialYear`: `!request.getFinancialYear().matches("^\\d{4}-\\d{4}$")`. If the user somehow bypassed the frontend and sent a year like "2023", the backend manually blocks it here.

### `getForm()`
- Simply asks the database for the user's form for a specific year.
- It also asks the `historyRepository` for all transactions attached to that form.
- It packages the form and its transactions together into a `Form90CResponseDTO` and sends it back.

### `uploadDocument()`
```java
@Transactional
public Map<String, Object> uploadDocument(String email, UploadRequestDTO request) throws Exception {
    // ... fetches user and form ...
    byte[] decodedBytes = Base64.getDecoder().decode(request.getData());

    if (decodedBytes.length > 2097152) {
        throw new RuntimeException("File size exceeds limit");
    }

    String fileType = detectFileType(decodedBytes);
    // ... creates a folder and saves the file ...
}
```
- **Security Check 1:** Decodes the Base64 string back into raw file data (bytes).
- **Security Check 2:** Ensures the file is not larger than 2MB (2,097,152 bytes) to prevent users from crashing the server with massive files.
- **Security Check 3 (`detectFileType`):** Calls a helper method to check the "magic bytes" (the hidden digital fingerprint at the start of every file). Hackers sometimes rename `virus.exe` to `image.jpg`. This check ensures the file is actually a real PDF or JPEG, regardless of its name.
- Finally, it saves the file to the server's hard drive and saves a record of it (`Document` entity) to the database.

### `submitForm()`
- This is the final step. It performs several strict checks:
  - Is the form already submitted? (`throw new RuntimeException("Form already submitted")`)
  - Did the user add any transactions? (`historyRepository.existsByFormId(...)`)
  - Did the user upload a file? (`documentRepository.existsByFormId(...)`)
- If all checks pass, it changes the form's status to `"SUBMITTED"`, records the timestamp (`setSubmittedAt`), and creates a `Submission` record in the database as a receipt.

## Connections to Other Files
- **Repositories:** It uses 5 different repositories to read/write from 5 different database tables.
- **`Form90CController.java`:** The Controller that catches the web requests and hands them to this Service.

---
**If asked about this file, key things to remember:**
- It uses **"Find or Create" logic**: when saving a draft, it looks for an existing record for that Financial Year first, preventing a user from accidentally creating two forms for the same year.
- It is highly secure regarding file uploads, enforcing a 2MB size limit and using magic byte detection rather than just trusting the file extension.
- It blocks final submission unless all requirements (transactions + documents) are met.
