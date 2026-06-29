# AuthController.java

**Purpose:** This file acts as the front door for user authentication; it receives login and registration requests from the frontend and sends back the appropriate responses.

## Annotations Used
- **`@RestController`:** This tells Spring Boot that this class is in charge of handling web requests (HTTP requests). It also ensures that any data returned by the methods in this class is automatically converted into JSON format (the standard format used to talk to the frontend), instead of trying to load an HTML web page.
- **`@RequestMapping("/api/auth")`:** This sets a base URL for all the methods in this file. It means every request that hits this controller must start with `http://localhost:8080/api/auth`.
- **`@RequiredArgsConstructor`:** This is a shortcut provided by a tool called Lombok. Instead of us having to manually write a "constructor" method to plug in the `AuthService`, Lombok automatically generates one behind the scenes for any variable marked `final`.
- **`@PostMapping`:** This tells Spring that the method below it should specifically handle HTTP POST requests (which are used when sending data to the server, like a username and password).
- **`@Valid`:** This tells Spring to check the incoming data against the validation rules we defined (e.g., checking if the email is valid, or if the password is long enough) before letting the method run. If it fails, Spring automatically throws an error back to the frontend.
- **`@RequestBody`:** This tells Spring to look in the "body" (the main payload) of the incoming HTTP request to find the data, and then convert that JSON data into our Java object (like `RegisterRequestDTO`).

## Methods Line-by-Line

### `register()`
```java
@PostMapping("/register")
public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterRequestDTO request) {
    authService.register(request);
    return new ResponseEntity<>(
            new ApiResponse("Registration successful", true, null),
            HttpStatus.CREATED
    );
}
```
- `@PostMapping("/register")`: This method will be triggered when the frontend sends a POST request to `/api/auth/register`.
- `public ResponseEntity<ApiResponse> register(...)`: This defines the method. It promises to return an HTTP Response containing an `ApiResponse` object.
- `(@Valid @RequestBody RegisterRequestDTO request)`: This takes the JSON data sent by the frontend, validates it, and packs it into a Java object named `request`.
- `authService.register(request);`: This passes the validated data to the `AuthService` (the brain of the app), which does the heavy lifting of saving the user to the database.
- `return new ResponseEntity<>(...);`: This prepares the package to send back to the frontend.
- `new ApiResponse("Registration successful", true, null)`: This creates a standard success message in JSON.
- `HttpStatus.CREATED`: This attaches the HTTP status code `201 Created` to the response, telling the frontend that a new user was successfully created.

### `login()`
```java
@PostMapping("/login")
public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequestDTO request) {
    AuthResponse response = authService.login(request);
    return ResponseEntity.ok(response);
}
```
- `@PostMapping("/login")`: This method will be triggered when the frontend sends a POST request to `/api/auth/login`.
- `public ResponseEntity<AuthResponse> login(...)`: This defines the method, promising to return an `AuthResponse` (which contains the security token).
- `(@Valid @RequestBody LoginRequestDTO request)`: This grabs the login credentials (email and password) from the frontend.
- `AuthResponse response = authService.login(request);`: This asks the `AuthService` to verify the email and password. If successful, the Service generates a secure JWT token and gives it back as an `AuthResponse`.
- `return ResponseEntity.ok(response);`: This sends the token back to the frontend along with an HTTP status code `200 OK`.

## Connections to Other Files
- **`AuthService.java`:** This controller heavily relies on the `AuthService` to actually execute the login and registration logic. The controller just catches the ball and passes it to the service.
- **`RegisterRequestDTO` and `LoginRequestDTO`:** These are the "envelopes" used to carry the incoming data from the frontend.
- **`ApiResponse` and `AuthResponse`:** These are the "envelopes" used to carry the outgoing data back to the frontend.

---
**If asked about this file, key things to remember:**
- It is strictly a "traffic cop" — it takes incoming web requests and routes them to the `AuthService`. It does not contain any database logic itself.
- It uses `@Valid` to ensure no bad data even makes it past the front door.
- It translates between the web world (JSON and HTTP codes like 201 CREATED) and the Java world.
