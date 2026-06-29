# JwtAuthenticationFilter.java

**Purpose:** This file acts like a security guard stationed at the front door. For every single web request that comes in, it stops the request, checks if it has a valid VIP pass (a JWT token), and if so, tells the rest of the application exactly who the user is.

## Core Concepts Used
- **`extends OncePerRequestFilter`:** This guarantees that Spring will execute this filter exactly one time for every single HTTP request that hits the server.
- **`SecurityContextHolder`:** A special box in Spring Security where we store the identity of the user for the duration of this single request.

## Methods Line-by-Line

### `doFilterInternal(...)`
```java
protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
    String jwt = getJwtFromRequest(request);

    if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
        String email = tokenProvider.getEmailFromJWT(jwt);
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
        
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
                
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
    filterChain.doFilter(request, response);
}
```
1. **Extract Token:** It tries to pull the token out of the incoming request.
2. **Validate:** It checks if a token exists and asks `tokenProvider` if it is valid (not expired, not forged).
3. **Identify:** If valid, it extracts the user's email from the token.
4. **Load Details:** It asks the database to load the full `UserDetails` based on that email.
5. **Authenticate:** It creates an official Spring Security `Authentication` object and sticks it into the `SecurityContextHolder`. From this point forward, if any Controller calls `authentication.getName()`, they will reliably get the user's email.
6. **Pass it along:** `filterChain.doFilter(request, response)` lets the request continue on to the Controller. If the token was missing or invalid, it still passes it along, but because the `SecurityContext` is empty, the `SecurityConfig` will immediately reject it with a 403 Forbidden error.

### `getJwtFromRequest(HttpServletRequest request)`
- **Purpose:** Looks inside the raw HTTP request specifically for a header named `Authorization`.
- If it finds a value that starts with `"Bearer "`, it chops off the word `"Bearer "` (the first 7 characters) and returns the actual raw token string.

---
**If asked about this file, key things to remember:**
- It intercepts **every** request.
- It translates a raw string token into an official Spring `Authentication` object, which is how methods like `addTransaction(..., Authentication auth)` are able to safely read the user's email without trusting the frontend.
