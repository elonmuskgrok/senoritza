SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    email VARCHAR(120) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    mobile_number VARCHAR(10) NOT NULL,
    address_line1 VARCHAR(120) NOT NULL,
    address_line2 VARCHAR(120) NOT NULL,
    area VARCHAR(80) NOT NULL,
    city VARCHAR(80) NOT NULL,
    state VARCHAR(60) NOT NULL,
    pincode CHAR(6) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uq_users_email UNIQUE (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;



CREATE TABLE IF NOT EXISTS transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    transaction_date DATE NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    tax_amount DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    type VARCHAR(20) NOT NULL,
    organization_name VARCHAR(150) NOT NULL,
    financial_year VARCHAR(9) NOT NULL,
    txn_month TINYINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_transactions_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT chk_txn_type CHECK (type IN ('TDS','TCS','OTHER')),
    CONSTRAINT chk_txn_month CHECK (txn_month BETWEEN 1 AND 12)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_transactions_user_fy ON transactions (user_id, financial_year);
CREATE INDEX idx_transactions_user_month ON transactions (user_id, txn_month);
CREATE INDEX idx_transactions_org ON transactions (organization_name);

CREATE TABLE IF NOT EXISTS form_90c (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(150) NOT NULL,
    mobile_number VARCHAR(10) NOT NULL,
    financial_year VARCHAR(9) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    submitted_at DATETIME NULL,
    CONSTRAINT fk_form90c_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT chk_form90c_status CHECK (status IN ('DRAFT','SUBMITTED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_form90c_user_fy ON form_90c (user_id, financial_year);

CREATE TABLE IF NOT EXISTS form_90c_transaction_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    form_id BIGINT NOT NULL,
    organization_name VARCHAR(150) NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    tax_amount DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    type VARCHAR(20) NOT NULL,
    CONSTRAINT fk_form90c_history_form FOREIGN KEY (form_id) REFERENCES form_90c(id) ON DELETE CASCADE,
    CONSTRAINT chk_form90c_history_type CHECK (type IN ('TDS','TCS','OTHER'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_form90c_history_form ON form_90c_transaction_history (form_id);

CREATE TABLE IF NOT EXISTS documents (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    form_id BIGINT NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_type VARCHAR(10) NOT NULL,
    file_size_bytes INT NOT NULL,
    storage_path VARCHAR(500) NOT NULL,
    uploaded_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_documents_form FOREIGN KEY (form_id) REFERENCES form_90c(id) ON DELETE CASCADE,
    CONSTRAINT chk_documents_type CHECK (file_type IN ('PDF','JPG','JPEG')),
    CONSTRAINT chk_documents_size CHECK (file_size_bytes <= 2097152)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_documents_form ON documents (form_id);

CREATE TABLE IF NOT EXISTS submissions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    form_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'CONFIRMED',
    confirmation_message VARCHAR(255) NOT NULL DEFAULT 'Form 90C submitted successfully',
    submitted_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_submissions_form UNIQUE (form_id),
    CONSTRAINT fk_submissions_form FOREIGN KEY (form_id) REFERENCES form_90c(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
