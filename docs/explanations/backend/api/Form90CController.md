# Form90CController.java

**Purpose:** This file acts as the traffic director for all actions related to "Form 90C", allowing users to save drafts, submit final forms, and upload supporting documents.

## Annotations Used
- **`@RestController`:** Tells Spring Boot this class receives web requests and will return data directly as JSON (instead of an HTML webpage).
- **`@RequiredArgsConstructor`:** (Lombok tool) Automatically wires up the `Form90CService` dependency without us having to write boilerplate constructor code.
- **`@PostMapping("...")`:** Maps a method to handle HTTP POST requests at a specific URL. POST is used when we want to send new data to the server (like saving a form or uploading a file).
- **`@GetMapping("...")`:** Maps a method to handle HTTP GET requests. GET is used when we want to retrieve existing data from the server (like loading an already saved draft).
- **`@Valid`:** Tells Spring to strictly check the incoming data against rules (e.g., "is the mobile number exactly 10 digits?") before running the method. Notice it is *not* used on the `saveDraft` method, because drafts are allowed to have missing or incomplete data!
- **`@RequestBody`:** Pulls the JSON data out of the main body of the HTTP request.
- **`@RequestParam`:** Extracts data from the URL itself (e.g., the `financialYear` in `/api/forms/90c?financialYear=2023-2024`).

## Methods Line-by-Line

### `saveDraft()`
```java
@PostMapping("/api/forms/90c/draft")
public ResponseEntity<Map<String, Object>> saveDraft(
        @RequestBody Form90CDraftRequestDTO request,
        Authentication authentication) {
    String email = authentication.getName();
    return ResponseEntity.ok(form90cService.saveDraft(email, request));
}
```
- `@PostMapping("/api/forms/90c/draft")`: Triggered when the user clicks "Save Draft".
- `public ResponseEntity...`: Declares the method, promising to return a generic JSON map (like `{ "formId": 123 }`).
- `(@RequestBody Form90CDraftRequestDTO request, Authentication authentication)`: It takes the draft data. Notice there is no `@Valid` here, meaning the user can save a draft even if they left required fields completely blank. It also takes the user's secure token (`Authentication`).
- `String email = authentication.getName();`: Extracts the logged-in user's email address.
- `return ResponseEntity.ok(form90cService.saveDraft(email, request));`: Passes the data to the Service layer to save the draft to the database, and returns the result with a `200 OK` status.

### `saveForm()`
```java
@PostMapping("/api/forms/90c")
public ResponseEntity<Map<String, Object>> saveForm(
        @Valid @RequestBody Form90CRequestDTO request,
        Authentication authentication) {
    String email = authentication.getName();
    return ResponseEntity.ok(form90cService.saveForm(email, request));
}
```
- Extremely similar to `saveDraft`, except it is triggered when the user clicks "Save & Continue" (moving from Part 1 to Part 2).
- It uses `@Valid @RequestBody Form90CRequestDTO`. Because it has `@Valid`, if the user left a field blank, Spring will block the request and throw a 400 Bad Request error back to the frontend immediately.

### `getForm()`
```java
@GetMapping("/api/forms/90c")
public ResponseEntity<Form90CResponseDTO> getForm(
        @RequestParam String financialYear,
        Authentication authentication) {
    String email = authentication.getName();
    return ResponseEntity.ok(form90cService.getForm(email, financialYear));
}
```
- `@GetMapping("/api/forms/90c")`: Triggered when the user opens the Form 90C page, trying to load any existing saved data.
- `@RequestParam String financialYear`: The frontend must provide the financial year in the URL so the backend knows which form to fetch.
- `return ResponseEntity.ok(form90cService.getForm(email, financialYear));`: Fetches the existing form from the database (if it exists) and returns it.

### `uploadDocument()`
```java
@PostMapping("/api/uploads")
public ResponseEntity<Map<String, Object>> uploadDocument(
        @Valid @RequestBody UploadRequestDTO request,
        Authentication authentication) throws Exception {
    String email = authentication.getName();
    return ResponseEntity.ok(form90cService.uploadDocument(email, request));
}
```
- Triggered when the user uploads a PDF or Image in Part 2 of the form.
- The `UploadRequestDTO` contains the file data (usually converted to a long text string called Base64 by the frontend).
- It passes the file to the Service layer to verify it (e.g., checking "magic bytes" to ensure it's actually a PDF and not a virus disguised as a PDF).

### `submitForm()`
```java
@PostMapping("/api/submissions")
public ResponseEntity<Map<String, Object>> submitForm(
        @Valid @RequestBody SubmissionRequestDTO request,
        Authentication authentication) {
    String email = authentication.getName();
    return ResponseEntity.ok(form90cService.submitForm(email, request));
}
```
- Triggered when the user clicks the final "Submit" button at the very end of Part 2.
- It changes the form's status in the database from "DRAFT" to "SUBMITTED", locking it from further edits.

## Connections to Other Files
- **`Form90CService.java`:** The actual logic (like validating PDF magic bytes or checking if a form is already submitted) lives here.
- **DTO Files:** `Form90CRequestDTO`, `Form90CDraftRequestDTO`, etc. act as envelopes for data. Note that Draft and Final requests use *different* envelopes because their validation rules are different.
- **Frontend `formApi.ts`:** The frontend file that actively makes HTTP requests to these exact URLs.

---
**If asked about this file, key things to remember:**
- It distinguishes between Drafts and Final Saves purely by the URL (`.../draft` vs `.../90c`) and the presence of the `@Valid` annotation.
- Like all controllers in this app, it reads the user's email directly from the secure `Authentication` token, meaning a hacker can't easily spoof a request for another user's form.
