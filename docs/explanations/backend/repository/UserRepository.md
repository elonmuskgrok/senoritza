# UserRepository.java

**Purpose:** This file manages all database interactions related to Users, such as finding a user when they try to log in, or checking if an email is already taken during registration.

## Annotations & Core Concepts Used
- **`@Repository`:** Tells Spring Boot that this interface is responsible for communicating with the database.
- **`extends JpaRepository<User, Long>`:** This gives the file superpowers. Without writing any SQL code, it automatically inherits methods to save a user, delete a user, or find a user by their ID number.

## Methods Line-by-Line

### `findByEmail(String email)`
- Spring Data JPA uses "Query Methods". By simply naming the method `findByEmail`, Spring automatically understands that we want it to write and execute the SQL query: `SELECT * FROM users WHERE email = ?`.
- It returns an `Optional<User>`. This is a safe way of saying "I might find a user, or I might find nothing." It prevents the app from crashing with a `NullPointerException` if the user types an email that doesn't exist.

### `existsByEmail(String email)`
- By naming the method `existsBy...`, Spring automatically writes a highly efficient SQL query: `SELECT count(1) FROM users WHERE email = ?`.
- It returns a simple `boolean` (true or false). This is used during the Registration process to quickly check if someone is trying to sign up with an email that is already in use.

## Connections to Other Files
- **`AuthService.java`:** Uses these methods constantly to verify users during login and registration.
- **`TransactionService.java` & `Form90CService.java`:** Use `findByEmail` to look up the exact user ID before saving any transactions or forms, ensuring data is always linked to the correct person.

---
**If asked about this file, key things to remember:**
- It contains exactly zero implementation code. Spring Data JPA reads the method names (`findByEmail`) and automatically generates the necessary SQL queries in the background.
- It returns `Optional<User>`, enforcing safe programming practices so the rest of the application doesn't accidentally crash when dealing with non-existent users.
