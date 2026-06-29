CREATE TABLE IF NOT EXISTS client (
    clientId UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    clientName VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    contactPerson VARCHAR(255),
    createdAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt TIMESTAMP
);