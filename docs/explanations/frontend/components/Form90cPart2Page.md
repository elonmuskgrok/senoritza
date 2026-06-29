# Form90cPart2Page.tsx

**Purpose:** This is the final step of filing Form 90C. It forces the user to upload at least one supporting document (like a PDF receipt) before they can finalize and submit the entire form.

## React Concepts Used
- **`useLocation`:**
  - `const formId = location.state?.formId;`
  - In Part 1, when the user clicked "Save & Continue", we teleported them to this page and packed the `formId` (e.g. `123`) into their `location.state`. We pull it out here so we know exactly *which* form to attach the uploaded documents to.
  - If a user tries to access this page directly by typing `/form90c/upload` into their browser URL bar, `formId` will be undefined, and the page will show an error stopping them.
- **`useState`:**
  - `const [files, setFiles] = useState<any[]>([]);`: Keeps track of the names of the files the user has successfully uploaded so we can display them in a list on the screen.
  - `const [submitting, setSubmitting] = useState(false);`: Used to disable the final submit button. If the user clicks it, we set this to `true` so they can't impatiently click it 5 times in a row and submit duplicates.

## Bootstrap Classes Used
- **`card shadow-sm border-0 bg-light`:** Draws a soft gray box with a drop shadow, making the upload area stand out from the white background of the page.
- **`form-control form-control-lg`:** Makes the actual file selection box (`<input type="file">`) look modern and slightly larger (`lg`) than normal.
- **`list-group` and `list-group-item`:** Used to display the uploaded file names as a neat, bordered list.
- **`d-grid`:** Turns the container for the Submit button into a grid. This is a very quick way to force a button inside it to stretch all the way across the width of the card.

## Functions

### `handleFileUpload()`
- **Trigger:** Runs the moment a user selects a file from their computer using the file picker.
- **Step-by-step:**
  1. **Frontend Validation:** It immediately checks the file size (`> 2 * 1024 * 1024` bytes, or 2MB) and the file type. If the file is too big or the wrong type, it stops immediately and shows a red `toast.error` popup without even bothering the backend server.
  2. **FileReader:** It uses the browser's built-in `FileReader` tool to read the file off the user's hard drive and convert it into a `Base64` string (a very long string of random-looking text that represents the file's data).
  3. **API Call:** It sends the `formId`, file name, and the Base64 data to `formApi.uploadDocument`.
  4. If the backend accepts the file (meaning it passed the backend's strict magic-byte security checks), the filename is added to the `files` array so it appears on screen.

### `handleSubmit()`
- **Trigger:** Clicking the green "Submit Final Form" button.
- **Step-by-step:**
  1. First, it ensures `files.length` is greater than 0. The user *must* upload a document to proceed.
  2. It sets `submitting(true)` to lock the button.
  3. It calls `formApi.submitForm(formId)`. The backend does a final check to ensure everything is perfect.
  4. If successful, it shows a green success popup, waits exactly 2 seconds (`setTimeout`), and then navigates the user back to the `/dashboard`.

## Connections to Other Files
- **`Form90cPart1Page.tsx`:** This page entirely depends on Part 1 successfully navigating to it and providing the `formId`.
- **`formApi.ts`:** Used to upload the individual files and make the final submission request.
- **Backend `Form90CController.java`:** The `/api/uploads` and `/api/submissions` endpoints are what process the data sent by this file.

---
**If asked about this file, key things to remember:**
- It uses a double-validation system for files: the frontend checks the size and type immediately for a snappy user experience, but the backend also double-checks them (using magic bytes) for actual security.
- Files are uploaded instantly the moment they are selected using `FileReader` and Base64 encoding. The final "Submit Final Form" button does not upload files; it just tells the backend to finalize the entire process.
