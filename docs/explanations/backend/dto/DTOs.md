# Data Transfer Objects (DTOs) (`com.siva.dto`)

**Purpose:** DTOs are the "envelopes" or "suitcases" used to carry data between the React Frontend and the Spring Boot Backend. 

*(Note: Rather than creating 10 identical files, they are grouped here to explain the core concept.)*

## Why do we need DTOs? Why not just send the `User` Entity directly?
If a user asks to view their profile, and we just send the `User.java` entity directly to the frontend, we would accidentally send their hashed password too! 
DTOs allow us to **filter** exactly what data goes out (Response DTOs) and strictly validate exactly what data comes in (Request DTOs).

## Core Annotations Used
- **Lombok (`@Data`, `@Builder`, etc.):** Automatically generates getters and setters.
- **Jakarta Validation (`@NotBlank`, `@NotNull`, `@Pattern`, `@Min`):** These are the security guards. If the frontend sends data that breaks these rules, Spring automatically rejects the request with a `400 Bad Request` error before the data even reaches our Controller.

## Key Request DTOs (Data coming IN from React)

### `RegisterRequestDTO.java`
- Contains the Name, Email, Password, etc.
- **Key Detail:** Heavily annotated with rules like `@Pattern(regexp = "^\\d{10}$", message = "Mobile number must be exactly 10 digits")`. This is backend validation, mirroring the frontend validation we do in React.

### `Form90CRequestDTO.java` vs `Form90CDraftRequestDTO.java`
- **`Form90CRequestDTO`:** Used when the user hits "Save & Continue". Every field is strictly annotated with `@NotBlank` because the form must be complete.
- **`Form90CDraftRequestDTO`:** Used when the user hits "Save Draft". Notice it has *zero* validation annotations! This allows the user to save a half-empty form.

### `UploadRequestDTO.java`
- Contains `String data;`. This holds the massive Base64 text string representing the uploaded PDF file.

## Key Response DTOs (Data going OUT to React)

### `AuthResponse.java`
- Contains the `token` (the secure JWT string), `email`, and `name`. We specifically *do not* include the password here.

### `TransactionResponseDTO.java`
- Contains clean, formatted transaction data. Notice it adds a `financialYear` field that might not have been provided by the frontend, but was calculated safely by the backend before sending it back.

---
**If asked about these files, key things to remember:**
- **Request DTOs** are for inbound data and use strict `@Valid` annotations to protect the server from bad data.
- **Response DTOs** are for outbound data, ensuring we only send exactly what the frontend needs to see, preventing accidental data leaks.
