# Database Entities (`com.siva.entity`)

**Purpose:** Entities are the exact Java blueprints for the tables in our MySQL database. Every class in this folder represents one database table, and every variable in the class represents one column in that table.

*(Note: Rather than creating 6 identical files explaining the same 3 annotations, they are grouped here so you can easily study how the database is structured.)*

## Common Annotations Used in All Entities
- **`@Entity`:** Tells Spring Boot and Hibernate: "This class is a database table."
- **`@Table(name = "users")`:** Explicitly sets the name of the table in the SQL database.
- **`@Id` & `@GeneratedValue(strategy = GenerationType.IDENTITY)`:** Marks a field as the Primary Key (like `id`). `GenerationType.IDENTITY` tells the database to automatically count up (1, 2, 3...) every time a new row is inserted.
- **`@Column(nullable = false)`:** Tells the database to throw an error if someone tries to save a row with this column blank (like `NOT NULL` in SQL).
- **Lombok Annotations (`@Data`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`):** These automatically generate the boring boilerplate code behind the scenes (like `getters`, `setters`, and constructors), keeping these files incredibly clean and short.

## 1. `User.java`
- **Table:** `users`
- **Purpose:** Stores the core account information (Name, Email, Mobile, Address, Password).
- **Key Detail:** The `password` column stores the *BCrypt hashed* version of the password, never the plain text. The `email` column is marked `unique = true`.

## 2. `Transaction.java`
- **Table:** `transactions`
- **Purpose:** The ledger of all tax transactions (TDS/TCS) the user enters.
- **Relationships:** Uses `@ManyToOne` and `@JoinColumn(name = "user_id")` to link every transaction back to a specific user. This creates a Foreign Key in the database.

## 3. `Form90C.java`
- **Table:** `form_90c`
- **Purpose:** The master record representing a single tax form for a specific financial year.
- **Key Detail:** It tracks the `status` (either `"DRAFT"` or `"SUBMITTED"`).

## 4. `Form90CTransactionHistory.java`
- **Table:** `form_90c_transaction_history`
- **Purpose:** When a user attaches a transaction to their Form 90C, it is saved here.
- **Why it exists:** Why not just use the main `transactions` table? Because the user might edit or delete their everyday transactions later. We need a frozen, permanent "snapshot" of the exact transactions they attached to the official form.

## 5. `Document.java`
- **Table:** `documents`
- **Purpose:** Stores metadata about uploaded PDFs/JPEGs (like `fileName`, `fileType`, and `fileSizeBytes`).
- **Key Detail:** It stores the `storagePath` (where the file actually lives on the server's hard drive), rather than storing the massive file directly in the database.

## 6. `Submission.java`
- **Table:** `submissions`
- **Purpose:** Acts as a final receipt. Once Form 90C is fully submitted, a row is created here with a `submittedAt` timestamp.

---
**If asked about these files, key things to remember:**
- They use **Hibernate/JPA**, meaning the backend developer never actually wrote the `CREATE TABLE` SQL scripts. When the Spring Boot app starts, it reads these `@Entity` classes and automatically builds the MySQL tables to match them.
- They heavily rely on **Lombok** (`@Data`) to hide hundreds of lines of getter and setter methods.
