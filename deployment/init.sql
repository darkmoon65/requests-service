
CREATE TABLE state (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255)
);

CREATE TABLE loan_type (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    min_amount NUMERIC(15,2) NOT NULL,
    max_amount NUMERIC(15,2) NOT NULL,
    interest_rate NUMERIC(5,2) NOT NULL,
    auto_validation BOOLEAN DEFAULT FALSE
);

CREATE TABLE solicitude (
    id SERIAL PRIMARY KEY,
    amount NUMERIC(15,2) NOT NULL,
    term INTEGER NOT NULL,
    email VARCHAR(255) NOT NULL,
    state_id INT NOT NULL,
    loan_type_id INT NOT NULL,
    CONSTRAINT fk_solicitude_loantype FOREIGN KEY (loan_type_id) REFERENCES loan_type(id)
);

INSERT INTO loan_type (id, name, min_amount, max_amount, interest_rate, auto_validation) VALUES
    (1, 'Prestamo personal', 500, 10000, 16, TRUE),
    (2, 'Prestamo familiar', 10000, 20000, 12, TRUE),
    (3, 'Prestamo pro', 20000, 50000, 8, FALSE);

INSERT INTO state (id, name, description) VALUES
    (1, 'Pendiente', 'La solicitud fue registrada y está a la espera de validación.'),
    (2, 'Aprobado', 'La solicitud ha sido revisada y aprobada.'),
    (3, 'Rechazado', 'La solicitud fue revisada pero no cumple con los criterios.');
    (4, 'Manual', 'La solicitud fue revisada pero necesita revision manual.');