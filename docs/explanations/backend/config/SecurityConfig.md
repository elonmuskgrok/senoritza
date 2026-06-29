# SecurityConfig.java

**Purpose:** This file is the main bouncer for the entire backend application. It decides who is allowed in, which URLs require passwords, and configures Cross-Origin Resource Sharing (CORS) so the React frontend is allowed to talk to it.

## Annotations & Core Concepts Used
- **`@Configuration`:** Tells Spring Boot "Hey, read this file when you first start up because it contains important global settings."
- **`@EnableWebSecurity`:** Turns on Spring Security's web security support, completely locking down the application by default until we tell it otherwise.

## Key Beans (Configurations)

### `filterChain(HttpSecurity http)`
This is the most important method. It configures the core security rules:
- `csrf().disable()`: Disables Cross-Site Request Forgery protection. We disable it because our app uses stateless JWT tokens, not browser session cookies, so CSRF attacks aren't a major risk in the standard way.
- `sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)`: Tells Spring *not* to remember users between requests. Every single time the React app asks for data, it MUST provide the JWT token.
- `authorizeHttpRequests().requestMatchers("/api/auth/**").permitAll()`: This is the exception to the lockdown. It allows anyone (even unauthenticated users) to access the login and registration endpoints.
- `.anyRequest().authenticated()`: For every other URL (like `/api/transactions`), the user MUST be logged in.
- `http.addFilterBefore(...)`: It inserts our custom `JwtAuthenticationFilter` into Spring's security chain, so it checks for a JWT token *before* it tries to do normal username/password checking.

### `corsConfigurationSource()`
- **Purpose:** Prevents CORS errors. By default, web browsers block a website on `localhost:3000` (our React frontend) from asking for data from `localhost:8080` (our Spring backend) for security reasons.
- This configuration explicitly tells the browser: "It is 100% okay for `http://localhost:3000` to send GET/POST requests here, and it is allowed to send the `Authorization` header."

### `passwordEncoder()`
- Configures Spring to use `BCryptPasswordEncoder`. This ensures that anytime the app saves a password or checks a password, it uses the strong BCrypt hashing algorithm instead of plain text.

---
**If asked about this file, key things to remember:**
- It uses a **Stateless** security model, meaning the server has zero memory of who is logged in. Every request is treated as brand new and must be proven with a token.
- It fixes the dreaded CORS errors that usually happen when React tries to talk to Spring Boot.
