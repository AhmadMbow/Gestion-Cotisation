-- =========================================================
-- Migration : contrainte d'unicité (membre, mois, annee) sur cotisation
-- À exécuter UNE SEULE FOIS sur une base existante.
-- =========================================================
USE cotisations_db;

-- 1) Supprimer les doublons éventuels en gardant la cotisation la plus ancienne
DELETE c1 FROM cotisation c1
JOIN cotisation c2
  ON c1.membre_numero = c2.membre_numero
 AND c1.mois          = c2.mois
 AND c1.annee         = c2.annee
 AND c1.id > c2.id;

-- 2) Ajouter la contrainte (échouera si des doublons subsistent)
ALTER TABLE cotisation
  ADD CONSTRAINT uk_cotisation_periode UNIQUE (membre_numero, mois, annee);
