# AuthService.java

**Purpose:** This file contains the "business logic" for authentication; it enforces the rules for creating new users (like checking for duplicate emails), securely encrypts passwords, and verifies login credentials.

## Annotations Used
- **`@Service`:** This tells Spring Boot that this class holds business logic. Spring will automatically create one instance of this class and plug it into any Controller that needs it (like `AuthController`).
- **`@RequiredArgsConstructor`:** A shortcut from Lombok. It tells the code to automatically plug in (inject) all the final variables listed at the top (like `userRepository`, `passwordEncoder`, etc.) so we don't have to write the repetitive setup code manually.
- **`@Transactional`:** This tells Spring that the `register` method is a "transaction". If anything fails halfway through this method (e.g., the database crashes after saving the user but before something else finishes), Spring will automatically "roll back" and undo everything so we don't end up with broken, half-saved data.

## Methods Line-by-Line

### `register()`
```java
@Transactional
public void register(RegisterRequestDTO request) {
    if (userRepository.existsByEmail(request.getEmail())) {
        throw new RuntimeException("User already exists");
    }

    User user = User.builder()
            .name(request.getName())
            .email(request.getEmail())
            .passwordHash(passwordEncoder.encode(request.getPassword()))
            // ... (other fields mapped here)
            .build();

    User savedUser = userRepository.save(user);
    emailService.sendRegistrationConfirmation(savedUser.getEmail(), savedUser.getName());
}
```
- `@Transactional`: As explained above, keeps the database safe if an error happens mid-way.
- `public void register(RegisterRequestDTO request) {`: Starts the method. It takes the data package (`request`) that the `AuthController` handed to it.
- `if (userRepository.existsByEmail(request.getEmail())) {`: It asks the database (via `UserRepository`) if anyone is already registered with this email.
- `throw new RuntimeException("User already exists");`: If the email does exist, it throws an error immediately, stopping the registration.
- `User user = User.builder()...`: This creates a brand new `User` object (which represents a row in our database table).
- `.passwordHash(passwordEncoder.encode(request.getPassword()))`: **Crucial security step.** It takes the plain-text password the user typed (like "password123") and scrambles it into a secure hash before saving it. We never save raw passwords!
- `.build();`: Finishes building the `User` object.
- `User savedUser = userRepository.save(user);`: It hands the new `User` to the `UserRepository`, which executes the actual SQL command to insert it into the MySQL database.
- `emailService.sendRegistrationConfirmation(...)`: After successfully saving to the database, it asks the `EmailService` to send a welcome email to the new user.

### `login()`
```java
public AuthResponse login(LoginRequestDTO request) {
    Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                    request.getEmail(),
                    request.getPassword()
            )
    );

    SecurityContextHolder.getContext().setAuthentication(authentication);
    String jwt = tokenProvider.generateToken(authentication);

    User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("User not found"));

    return AuthResponse.builder()
            .token(jwt)
            .type("Bearer")
            .id(user.getId())
            // ...
            .build();
}
```
- `public AuthResponse login(LoginRequestDTO request) {`: Starts the method, taking the login data from the Controller.
- `Authentication authentication = authenticationManager.authenticate(...)`: This asks Spring Security's built-in `AuthenticationManager` to take the provided email and password, go to the database, and check if they match. If they don't match, this will throw an error and stop.
- `SecurityContextHolder.getContext().setAuthentication(authentication);`: If they do match, this tells Spring Security to formally log the user in for this request.
- `String jwt = tokenProvider.generateToken(authentication);`: It asks the `JwtTokenProvider` to create a digital ID card (a JWT, or JSON Web Token) for the user. This token proves they are logged in.
- `User user = userRepository.findByEmail(request.getEmail())...`: It fetches the user's details from the database so we can return their name and ID.
- `return AuthResponse.builder()...`: It packages the secure token, the user's ID, name, and email into an `AuthResponse` and hands it back to the `AuthController`.

## Connections to Other Files
- **`AuthController.java`:** The Controller calls this Service.
- **`UserRepository.java`:** The Service calls the Repository to check for existing emails, save new users, and find user details.
- **`EmailService.java`:** The Service calls this to fire off welcome emails.
- **`JwtTokenProvider.java`:** The Service calls this to generate the secure login token.

---
**If asked about this file, key things to remember:**
- The Service layer is where the actual "thinking" happens. The Controller just directs traffic, but the Service makes the decisions.
- Passwords are never saved in plain text; they are always hashed using `passwordEncoder`.
- The `login` method doesn't return a session cookie; it generates a JWT token, which is the modern standard for securing React + Spring Boot apps.
