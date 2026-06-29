# JwtTokenProvider.java

**Purpose:** This is the factory and inspector for JSON Web Tokens (JWTs). It is responsible for creating a new token when a user logs in, and it provides the mathematical logic to prove a token is authentic when it comes back.

## Core Concepts Used
- **`io.jsonwebtoken.Jwts` (JJWT Library):** The external Java library used to build, sign, and parse the JWT tokens.
- **`@Value`:** 
  - `@Value("${app.jwt.secret}") String secret`
  - This annotation tells Spring to look inside the `application.properties` file and find the secret key used for security. By storing the key in the properties file, we avoid hardcoding passwords directly in the source code.

## Methods Line-by-Line

### Constructor `JwtTokenProvider(...)`
- When the app starts, this constructor reads the secret string from the properties file, decodes it, and converts it into a cryptographic `Key` object using `Keys.hmacShaKeyFor()`. This key is mathematically impossible to guess and is used to sign all tokens.

### `generateToken(Authentication authentication)`
- **Purpose:** Used by the `AuthService` right after a user successfully logs in.
- **Step-by-step:**
  1. Grabs the user's email (`authentication.getName()`).
  2. Calculates the exact millisecond when the token should expire (e.g., 24 hours from now).
  3. Uses `Jwts.builder()` to create the token:
     - `.setSubject(email)`: Embeds the user's email inside the token payload.
     - `.setExpiration(expiryDate)`: Tells the token when to self-destruct.
     - `.signWith(key, SignatureAlgorithm.HS256)`: This is the most important part. It uses the secret server key and the HS256 algorithm to mathematically sign the token. If anyone tries to modify the email inside the token later, the signature will break, and the server will reject it.

### `getEmailFromJWT(String token)`
- **Purpose:** Used by `JwtAuthenticationFilter` to extract the user's identity from a valid token.
- It uses `Jwts.parserBuilder().setSigningKey(key)` to parse the token using the same secret key, and then calls `.getSubject()` to extract the email we embedded earlier.

### `validateToken(String authToken)`
- **Purpose:** Checks if a token is legitimate.
- It attempts to parse the token. If the token was forged, modified, or has passed its expiration date, the `Jwts.parserBuilder()` will throw an exception (like `JwtException`).
- The method catches the exception and returns `false`. If it parses successfully without throwing an exception, it returns `true`.

---
**If asked about this file, key things to remember:**
- It relies on a secret key (`app.jwt.secret`) configured in `application.properties`. If this key is leaked, hackers could forge tokens.
- It ensures tokens are tamper-proof. Because of the `HS256` signature, a hacker cannot simply decode the token, change the email from "user@test.com" to "admin@test.com", and encode it back. The server's secret key verification would immediately fail.
