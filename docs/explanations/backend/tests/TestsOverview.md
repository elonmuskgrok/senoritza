# Backend Unit Tests (`src/test/java`)

**Purpose:** This project contains extensive Unit Tests for both the Controller layer and the Service layer. The goal of these tests is to prove that the code works exactly as expected, including edge cases (like what happens if a user uploads a file that is too large).

## Core Concepts Used
- **JUnit 5:** The framework used to structure the tests. We use annotations like `@Test` (to mark a method as a test) and `@BeforeEach` (to run setup code before every test).
- **Mockito:** A framework used to create "fake" or "mock" objects. 
- **Code Coverage (JaCoCo/SonarQube):** A tool that measures exactly how many lines of our code were actually executed during the tests. Our goal is always >80% coverage.

## Service Tests (e.g., `TransactionServiceTest.java`)
Service tests focus heavily on **Business Logic**.
- **`@ExtendWith(MockitoExtension.class)`:** Tells JUnit to enable Mockito.
- **`@Mock`:** Used to create fake versions of our Repositories (like `TransactionRepository`). We do this because we want to test our *Java logic*, not our *Database connection*. 
- **`@InjectMocks`:** Used on the Service we are actually testing (`TransactionService`). It automatically takes all the `@Mock` databases and injects them into the Service.
- **`when(...).thenReturn(...)`:** This is Mockito magic. We can say: "When the Service asks the fake UserRepository for an email, return this specific fake User."
- **`assertThrows(...)`:** Used to test security. We write a test to deliberately pass an invalid financial year string and assert that the Service correctly throws an exception to block it.

## Controller Tests (e.g., `Form90CControllerTest.java`)
Controller tests focus heavily on **Web Routing and HTTP Codes**.
- **`@WebMvcTest(Form90CController.class)`:** Tells Spring Boot to load *only* the web layer (Controllers) and ignore the heavy database layer. This makes the tests lightning fast.
- **`@MockBean`:** Creates a fake version of the Service layer inside the Spring context.
- **`MockMvc`:** A tool that allows us to send fake HTTP GET and POST requests directly to our Controller without actually starting up a real web server.
- **`mockMvc.perform(post("/api/forms/90c/draft")...)`:** We send a fake JSON request and use `.andExpect(status().isOk())` to verify that the Controller replied with a `200 OK` status.

---
**If asked about these files, key things to remember:**
- We isolate our tests. Controller tests *never* talk to real Services, and Service tests *never* talk to real Databases. We use **Mockito** to "mock" the layers below.
- This ensures tests are incredibly fast and perfectly reliable (a test won't randomly fail just because your local MySQL database happens to be turned off).
