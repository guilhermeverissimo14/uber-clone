-- ============================================
-- UBER CLONE - DATABASE SCHEMA
-- ============================================

-- Extensão para trabalhar com geolocalização
CREATE EXTENSION IF NOT EXISTS postgis;

-- ============================================
-- 1. USERS (Usuários base)
-- ============================================
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    phone VARCHAR(20) UNIQUE NOT NULL,
    cpf VARCHAR(14) UNIQUE,
    birth_date DATE,
    user_type VARCHAR(20) NOT NULL, -- PASSENGER, DRIVER, BOTH
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE, INACTIVE, SUSPENDED, BANNED
    rating_avg DECIMAL(3,2) DEFAULT 5.00,
    total_ratings INTEGER DEFAULT 0,
    profile_photo_url TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_phone ON users(phone);
CREATE INDEX idx_users_status ON users(status);

-- ============================================
-- 2. ADDRESSES (Endereços salvos)
-- ============================================
CREATE TABLE addresses (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    street VARCHAR(255) NOT NULL,
    number VARCHAR(20),
    complement VARCHAR(255),
    neighborhood VARCHAR(100),
    city VARCHAR(100) NOT NULL,
    state VARCHAR(2) NOT NULL,
    zip_code VARCHAR(10),
    latitude DECIMAL(10,8) NOT NULL,
    longitude DECIMAL(11,8) NOT NULL,
    label VARCHAR(50), -- HOME, WORK, OTHER
    is_default BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_addresses_user_id ON addresses(user_id);

-- ============================================
-- 3. PASSENGERS (Dados específicos de passageiros)
-- ============================================
CREATE TABLE passengers (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT UNIQUE NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    preferences JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_passengers_user_id ON passengers(user_id);

-- ============================================
-- 4. DRIVERS (Dados específicos de motoristas)
-- ============================================
CREATE TABLE drivers (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT UNIQUE NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    license_number VARCHAR(50) UNIQUE NOT NULL,
    license_category VARCHAR(5) NOT NULL,
    license_expires_at DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING', -- PENDING, APPROVED, REJECTED, SUSPENDED
    is_available BOOLEAN DEFAULT FALSE,
    current_latitude DECIMAL(10,8),
    current_longitude DECIMAL(11,8),
    last_location_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_drivers_user_id ON drivers(user_id);
CREATE INDEX idx_drivers_status ON drivers(status);
CREATE INDEX idx_drivers_available ON drivers(is_available);
CREATE INDEX idx_drivers_location ON drivers(current_latitude, current_longitude);

-- ============================================
-- 5. VEHICLES (Veículos dos motoristas)
-- ============================================
CREATE TABLE vehicles (
    id BIGSERIAL PRIMARY KEY,
    driver_id BIGINT NOT NULL REFERENCES drivers(id) ON DELETE CASCADE,
    brand VARCHAR(50) NOT NULL,
    model VARCHAR(50) NOT NULL,
    year INTEGER NOT NULL,
    color VARCHAR(30) NOT NULL,
    license_plate VARCHAR(10) UNIQUE NOT NULL,
    vehicle_type VARCHAR(20) NOT NULL, -- ECONOMY, COMFORT, PREMIUM
    seats INTEGER NOT NULL DEFAULT 4,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_vehicles_driver_id ON vehicles(driver_id);
CREATE INDEX idx_vehicles_active ON vehicles(is_active);

-- ============================================
-- 6. DOCUMENTS (Documentos para verificação)
-- ============================================
CREATE TABLE documents (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    document_type VARCHAR(30) NOT NULL, -- CNH, RG, VEHICLE_REGISTRATION
    document_number VARCHAR(50),
    file_url TEXT NOT NULL,
    verified BOOLEAN DEFAULT FALSE,
    verified_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_documents_user_id ON documents(user_id);

-- ============================================
-- 7. RIDES (Corridas)
-- ============================================
CREATE TABLE rides (
    id BIGSERIAL PRIMARY KEY,
    passenger_id BIGINT NOT NULL REFERENCES passengers(id),
    driver_id BIGINT REFERENCES drivers(id),
    vehicle_id BIGINT REFERENCES vehicles(id),
    
    status VARCHAR(20) NOT NULL DEFAULT 'REQUESTED',
    
    -- Pickup
    pickup_address TEXT NOT NULL,
    pickup_latitude DECIMAL(10,8) NOT NULL,
    pickup_longitude DECIMAL(11,8) NOT NULL,
    
    -- Dropoff
    dropoff_address TEXT NOT NULL,
    dropoff_latitude DECIMAL(10,8) NOT NULL,
    dropoff_longitude DECIMAL(11,8) NOT NULL,
    
    -- Pricing
    distance_km DECIMAL(8,2),
    estimated_duration_min INTEGER,
    base_price DECIMAL(10,2),
    surge_multiplier DECIMAL(3,2) DEFAULT 1.00,
    total_price DECIMAL(10,2),
    
    -- Payment
    payment_method VARCHAR(20) DEFAULT 'CASH',
    payment_status VARCHAR(20) DEFAULT 'PENDING',
    
    -- Timestamps
    requested_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    accepted_at TIMESTAMP,
    driver_arrived_at TIMESTAMP,
    started_at TIMESTAMP,
    completed_at TIMESTAMP,
    cancelled_at TIMESTAMP,
    cancellation_reason TEXT,
    cancelled_by BIGINT REFERENCES users(id),
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_rides_passenger_id ON rides(passenger_id);
CREATE INDEX idx_rides_driver_id ON rides(driver_id);
CREATE INDEX idx_rides_status ON rides(status);
CREATE INDEX idx_rides_requested_at ON rides(requested_at);

-- ============================================
-- 8. RIDE_LOCATIONS (Rastreamento GPS)
-- ============================================
CREATE TABLE ride_locations (
    id BIGSERIAL PRIMARY KEY,
    ride_id BIGINT NOT NULL REFERENCES rides(id) ON DELETE CASCADE,
    latitude DECIMAL(10,8) NOT NULL,
    longitude DECIMAL(11,8) NOT NULL,
    bearing DECIMAL(5,2),
    speed DECIMAL(5,2),
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_ride_locations_ride_id ON ride_locations(ride_id);

-- ============================================
-- 9. RATINGS (Avaliações)
-- ============================================
CREATE TABLE ratings (
    id BIGSERIAL PRIMARY KEY,
    ride_id BIGINT UNIQUE NOT NULL REFERENCES rides(id),
    rated_by BIGINT NOT NULL REFERENCES users(id),
    rated_user BIGINT NOT NULL REFERENCES users(id),
    rating INTEGER NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_ratings_rated_user ON ratings(rated_user);

-- ============================================
-- 10. RIDE_EVENTS (Log de eventos - Auditoria)
-- ============================================
CREATE TABLE ride_events (
    id BIGSERIAL PRIMARY KEY,
    ride_id BIGINT NOT NULL REFERENCES rides(id) ON DELETE CASCADE,
    event_type VARCHAR(50) NOT NULL,
    event_data JSONB,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_ride_events_ride_id ON ride_events(ride_id);

-- ============================================
-- 11. NOTIFICATIONS (Notificações)
-- ============================================
CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    type VARCHAR(30) NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    data JSONB,
    is_read BOOLEAN DEFAULT FALSE,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    read_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_notifications_user_id ON notifications(user_id);
CREATE INDEX idx_notifications_is_read ON notifications(is_read);

-- ============================================
-- DADOS DE TESTE (Opcional - comentar em produção)
-- ============================================

-- Usuário passageiro de teste
INSERT INTO users (email, password_hash, full_name, phone, user_type) 
VALUES ('passenger@test.com', '$2a$10$xYzExample', 'João Silva', '11999999999', 'PASSENGER'); 

-- Usuário motorista de teste
INSERT INTO users (email, password_hash, full_name, phone, user_type) 
VALUES ('driver@test.com', '$2a$10$xYzExample', 'Maria Santos', '11988888888', 'DRIVER');