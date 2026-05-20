-- =========================================================
-- Schéma SQL initial — Gestion Cotisations Association
-- MySQL 8.x
-- =========================================================

CREATE DATABASE IF NOT EXISTS cotisations_db
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE cotisations_db;

-- ---------- Table MEMBRE ----------
CREATE TABLE IF NOT EXISTS membre (
    numero          INT AUTO_INCREMENT PRIMARY KEY,
    prenom          VARCHAR(80) NOT NULL,
    nom             VARCHAR(80) NOT NULL,
    email           VARCHAR(120) NOT NULL UNIQUE,
    date_naissance  DATE,
    date_adhesion   DATE NOT NULL,
    statut          ENUM('ACTIF','INACTIF') NOT NULL DEFAULT 'ACTIF',
    role            ENUM('ADMIN','MEMBRE') NOT NULL DEFAULT 'MEMBRE',
    mot_de_passe    VARCHAR(255) NOT NULL
) ENGINE=InnoDB;

-- ---------- Table COTISATION ----------
CREATE TABLE IF NOT EXISTS cotisation (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    membre_numero   INT NOT NULL,
    montant         DECIMAL(10,2) NOT NULL,
    date_paiement   DATE NOT NULL,
    mois            INT NOT NULL,
    annee           INT NOT NULL,
    mode_paiement   ENUM('ESPECES','VIREMENT','WAVE','ORANGE_MONEY','FREE_MONEY') NOT NULL,
    statut          ENUM('PAYE','EN_ATTENTE','ANNULE') NOT NULL DEFAULT 'PAYE',
    CONSTRAINT fk_cotisation_membre FOREIGN KEY (membre_numero) REFERENCES membre(numero) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ---------- Table AMENDE ----------
CREATE TABLE IF NOT EXISTS amende (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    membre_numero   INT NOT NULL,
    montant         DECIMAL(10,2) NOT NULL,
    date_generation DATE NOT NULL,
    statut_paiement ENUM('PAYEE','IMPAYEE') NOT NULL DEFAULT 'IMPAYEE',
    CONSTRAINT fk_amende_membre FOREIGN KEY (membre_numero) REFERENCES membre(numero) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ---------- Données de démo ----------
-- Mot de passe ci-dessous = "admin123" haché avec BCrypt (workload 12)
INSERT INTO membre (prenom, nom, email, date_naissance, date_adhesion, statut, role, mot_de_passe) VALUES
('Admin',   'Système', 'admin@asso.sn',   '1990-01-01', CURDATE(), 'ACTIF', 'ADMIN',  '$2a$12$ClDDYrII1.L0JWo7kdtRqe/fByk9bvzUKuYVj4FfynQJcT25zJgOe'),
('Aminata', 'Diop',    'aminata@asso.sn', '1995-05-12', CURDATE(), 'ACTIF', 'MEMBRE', '$2a$12$ClDDYrII1.L0JWo7kdtRqe/fByk9bvzUKuYVj4FfynQJcT25zJgOe'),
('Moussa',  'Ba',      'moussa@asso.sn',  '1992-08-23', CURDATE(), 'ACTIF', 'MEMBRE', '$2a$12$ClDDYrII1.L0JWo7kdtRqe/fByk9bvzUKuYVj4FfynQJcT25zJgOe');

-- Quelques cotisations de démo (membres 2 et 3)
INSERT INTO cotisation (membre_numero, montant, date_paiement, mois, annee, mode_paiement, statut) VALUES
(2, 5000.00, DATE_SUB(CURDATE(), INTERVAL 60 DAY), MONTH(DATE_SUB(CURDATE(), INTERVAL 60 DAY)), YEAR(DATE_SUB(CURDATE(), INTERVAL 60 DAY)), 'WAVE', 'PAYE'),
(2, 5000.00, DATE_SUB(CURDATE(), INTERVAL 30 DAY), MONTH(DATE_SUB(CURDATE(), INTERVAL 30 DAY)), YEAR(DATE_SUB(CURDATE(), INTERVAL 30 DAY)), 'WAVE', 'PAYE'),
(2, 5000.00, CURDATE(), MONTH(CURDATE()), YEAR(CURDATE()), 'ORANGE_MONEY', 'PAYE'),
(3, 5000.00, DATE_SUB(CURDATE(), INTERVAL 30 DAY), MONTH(DATE_SUB(CURDATE(), INTERVAL 30 DAY)), YEAR(DATE_SUB(CURDATE(), INTERVAL 30 DAY)), 'ESPECES', 'PAYE');

-- Une amende impayée pour Moussa
INSERT INTO amende (membre_numero, montant, date_generation, statut_paiement) VALUES
(3, 2000.00, CURDATE(), 'IMPAYEE');
