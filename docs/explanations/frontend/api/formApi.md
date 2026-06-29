# formApi.ts

**Purpose:** This file contains all the HTTP requests needed to guide a user through the Form 90C process (Drafting, Finalizing, Uploading, and Submitting).

## Core Concepts Used
- **`import axiosClient from './axiosClient'`:** Uses the custom `axios` setup to handle base URLs and security tokens automatically.

## Methods Line-by-Line

### `saveDraft: async (data: any)`
- POSTs the user's incomplete form data to `/forms/90c/draft`. The backend knows this is a draft because of the `/draft` URL, so it won't complain if required fields are missing.

### `saveForm: async (data: any)`
- POSTs the completed Part 1 form data to `/forms/90c`. The backend strictly checks this data.

### `getForm: async (financialYear: string)`
- GETs the user's existing form data (if any) for a specific financial year so the frontend can populate the inputs when they resume their work.

### `uploadDocument: async (data: any)`
- POSTs the file data (which the frontend converted to a Base64 string) to `/uploads`.

### `submitForm: async (formId: number)`
- POSTs the final `formId` to `/submissions`. This is the final step that locks the form and marks it as "SUBMITTED".

---
**If asked about this file, key things to remember:**
- It clearly separates `saveDraft` and `saveForm` into two distinct methods hitting two distinct backend URLs, reflecting the difference in how strictly the backend validates them.
