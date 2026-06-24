-- Sample transactions for testing
-- Assumes user_id = 1 exists. If no users exist, these will fail gracefully due to foreign keys, or we can insert a dummy user.
-- Wait, we should probably insert a dummy user if it doesn't exist, but since passwords are BCrypt, it's easier to just assume user_id=1 will be the first registered user.

INSERT IGNORE INTO users (id, name, email, password_hash, mobile_number, address_line1, address_line2, area, city, state, pincode) 
VALUES (1, 'John Doe', 'johndoe@example.com', '$2a$10$wX9bZ...', '6234567890', '123 Main St', 'Apt 4B', 'Downtown', 'Metropolis', 'New York', '100001');

INSERT IGNORE INTO transactions (user_id, transaction_date, amount, tax_amount, type, organization_name, financial_year, txn_month) VALUES 
(1, '2023-01-15', 50000.00, 500.00, 'TDS', 'Google India', '2022-2023', 1),
(1, '2023-02-15', 50000.00, 500.00, 'TDS', 'Google India', '2022-2023', 2),
(1, '2023-03-20', 10000.00, 100.00, 'TCS', 'Amazon Pay', '2022-2023', 3),
(1, '2023-04-10', 55000.00, 550.00, 'TDS', 'Google India', '2023-2024', 4),
(1, '2023-05-15', 55000.00, 550.00, 'TDS', 'Google India', '2023-2024', 5),
(1, '2023-06-18', 20000.00, 200.00, 'TCS', 'Amazon Pay', '2023-2024', 6),
(1, '2023-07-20', 15000.00, 150.00, 'OTHER', 'Freelance Corp', '2023-2024', 7),
(1, '2023-08-15', 60000.00, 600.00, 'TDS', 'Microsoft India', '2023-2024', 8),
(1, '2023-09-12', 60000.00, 600.00, 'TDS', 'Microsoft India', '2023-2024', 9),
(1, '2023-10-05', 5000.00, 50.00, 'TCS', 'Flipkart', '2023-2024', 10);
