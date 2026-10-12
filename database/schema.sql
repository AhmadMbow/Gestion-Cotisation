-- =========================================================
-- Schéma SQL initial — Gestion Cotisations Association
-- MySQL 8.x
-- =========================================================

-- Le fichier est en UTF-8 : sans cette ligne, MySQL le lit en latin1
-- et les accents sont enregistrés mal encodés (« SystÃ¨me »).
SET NAMES utf8mb4;

CREATE DATABASE IF NOT EXISTS cotisations_db
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE cotisations_db;

-- Modèle multi-groupes : un COMPTE (personne) peut être MEMBRE de plusieurs GROUPES.
-- Le groupe n° 1 est l'association d'origine, gérée aussi par le site web.
-- (notification, api_token, paiement_abonnement : créées par Hibernate au démarrage.)

-- ---------- Table COMPTE ----------
CREATE TABLE IF NOT EXISTS compte (
    id                  INT AUTO_INCREMENT PRIMARY KEY,
    prenom              VARCHAR(80) NOT NULL,
    nom                 VARCHAR(80) NOT NULL,
    email               VARCHAR(120) NOT NULL,
    telephone           VARCHAR(30),
    date_naissance      DATE,
    mot_de_passe        VARCHAR(255) NOT NULL,
    date_creation       DATETIME(6) NOT NULL,
    forfait             ENUM('GRATUIT','ESSENTIEL','PRO','ILLIMITE') NOT NULL DEFAULT 'GRATUIT',
    forfait_expire_le   DATE,
    CONSTRAINT uk_compte_email UNIQUE (email)
) ENGINE=InnoDB;

-- ---------- Table GROUPE ----------
CREATE TABLE IF NOT EXISTS groupe (
    id                  INT AUTO_INCREMENT PRIMARY KEY,
    nom                 VARCHAR(80) NOT NULL,
    code_invitation     VARCHAR(12) NOT NULL,
    montant_cotisation  DECIMAL(10,2) NOT NULL DEFAULT 5000,
    montant_amende      DECIMAL(10,2) NOT NULL DEFAULT 2000,
    proprietaire_id     INT,
    date_creation       DATETIME(6) NOT NULL,
    actif               BIT(1) NOT NULL DEFAULT b'1',
    CONSTRAINT uk_groupe_code UNIQUE (code_invitation),
    CONSTRAINT fk_groupe_proprietaire FOREIGN KEY (proprietaire_id) REFERENCES compte(id)
) ENGINE=InnoDB;

-- ---------- Table MEMBRE (adhésion d'un compte à un groupe) ----------
CREATE TABLE IF NOT EXISTS membre (
    numero          INT AUTO_INCREMENT PRIMARY KEY,
    compte_id       INT,
    groupe_id       INT,
    date_adhesion   DATE NOT NULL,
    statut          ENUM('ACTIF','INACTIF') NOT NULL DEFAULT 'ACTIF',
    role            ENUM('ADMIN','MEMBRE') NOT NULL DEFAULT 'MEMBRE',
    CONSTRAINT uk_membre_compte_groupe UNIQUE (compte_id, groupe_id),
    CONSTRAINT fk_membre_compte FOREIGN KEY (compte_id) REFERENCES compte(id),
    CONSTRAINT fk_membre_groupe FOREIGN KEY (groupe_id) REFERENCES groupe(id)
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
    CONSTRAINT fk_cotisation_membre FOREIGN KEY (membre_numero) REFERENCES membre(numero) ON DELETE CASCADE,
    CONSTRAINT uk_cotisation_periode UNIQUE (membre_numero, mois, annee)
) ENGINE=InnoDB;

-- ---------- Table AMENDE ----------
CREATE TABLE IF NOT EXISTS amende (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    membre_numero   INT NOT NULL,
    montant         DECIMAL(10,2) NOT NULL,
    date_generation DATE NOT NULL,
    statut_paiement ENUM('PAYEE','IMPAYEE') NOT NULL DEFAULT 'IMPAYEE',
    date_paiement   DATE,
    mode_paiement   ENUM('ESPECES','VIREMENT','WAVE','ORANGE_MONEY','FREE_MONEY'),
    CONSTRAINT fk_amende_membre FOREIGN KEY (membre_numero) REFERENCES membre(numero) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ---------- Table PASSWORD_RESET_TOKEN ----------
CREATE TABLE IF NOT EXISTS password_reset_token (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    compte_id       INT,
    token           VARCHAR(80) NOT NULL UNIQUE,
    expire_at       DATETIME NOT NULL,
    used_at         DATETIME,
    CONSTRAINT fk_prt_compte FOREIGN KEY (compte_id) REFERENCES compte(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ---------- Table CONNEXION (historique d'authentification) ----------
CREATE TABLE IF NOT EXISTS connexion (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    compte_id       INT,
    date_connexion  DATETIME NOT NULL,
    ip              VARCHAR(45),
    user_agent      VARCHAR(255),
    INDEX idx_connexion_compte (compte_id),
    CONSTRAINT fk_connexion_compte FOREIGN KEY (compte_id) REFERENCES compte(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ---------- Données de démo ----------
-- Mot de passe ci-dessous = "admin123" haché avec BCrypt (workload 12)
INSERT INTO compte (prenom, nom, email, date_naissance, mot_de_passe, date_creation, forfait, forfait_expire_le) VALUES
('Admin',   'Système', 'admin@asso.sn',   '1990-01-01', '$2a$12$ClDDYrII1.L0JWo7kdtRqe/fByk9bvzUKuYVj4FfynQJcT25zJgOe', NOW(), 'ILLIMITE', '2099-12-31'),
('Aminata', 'Diop',    'aminata@asso.sn', '1995-05-12', '$2a$12$ClDDYrII1.L0JWo7kdtRqe/fByk9bvzUKuYVj4FfynQJcT25zJgOe', NOW(), 'GRATUIT', NULL),
('Moussa',  'Ba',      'moussa@asso.sn',  '1992-08-23', '$2a$12$ClDDYrII1.L0JWo7kdtRqe/fByk9bvzUKuYVj4FfynQJcT25zJgOe', NOW(), 'GRATUIT', NULL);

-- Groupe historique n° 1 (code d'invitation ASSO26), dont l'admin est propriétaire
INSERT INTO groupe (nom, code_invitation, montant_cotisation, montant_amende, proprietaire_id, date_creation) VALUES
('Association Cotisen', 'ASSO26', 5000.00, 2000.00, 1, NOW());

INSERT INTO membre (compte_id, groupe_id, date_adhesion, statut, role) VALUES
(1, 1, CURDATE(), 'ACTIF', 'ADMIN'),
(2, 1, CURDATE(), 'ACTIF', 'MEMBRE'),
(3, 1, CURDATE(), 'ACTIF', 'MEMBRE');

-- Quelques cotisations de démo (membres 2 et 3)
INSERT INTO cotisation (membre_numero, montant, date_paiement, mois, annee, mode_paiement, statut) VALUES
(2, 5000.00, DATE_SUB(CURDATE(), INTERVAL 60 DAY), MONTH(DATE_SUB(CURDATE(), INTERVAL 60 DAY)), YEAR(DATE_SUB(CURDATE(), INTERVAL 60 DAY)), 'WAVE', 'PAYE'),
(2, 5000.00, DATE_SUB(CURDATE(), INTERVAL 30 DAY), MONTH(DATE_SUB(CURDATE(), INTERVAL 30 DAY)), YEAR(DATE_SUB(CURDATE(), INTERVAL 30 DAY)), 'WAVE', 'PAYE'),
(2, 5000.00, CURDATE(), MONTH(CURDATE()), YEAR(CURDATE()), 'ORANGE_MONEY', 'PAYE'),
(3, 5000.00, DATE_SUB(CURDATE(), INTERVAL 30 DAY), MONTH(DATE_SUB(CURDATE(), INTERVAL 30 DAY)), YEAR(DATE_SUB(CURDATE(), INTERVAL 30 DAY)), 'ESPECES', 'PAYE');

-- Une amende impayée pour Moussa
INSERT INTO amende (membre_numero, montant, date_generation, statut_paiement) VALUES
(3, 2000.00, CURDATE(), 'IMPAYEE');
