INSERT INTO role (name)
SELECT 'ROLE_USER' WHERE NOT EXISTS (SELECT 1 FROM role WHERE name='ROLE_USER');
INSERT INTO role (name)
SELECT 'ROLE_ADMIN' WHERE NOT EXISTS (SELECT 1 FROM role WHERE name='ROLE_ADMIN');

-- Seed statuses (code, libelle)
INSERT INTO status (code, libelle)
SELECT 'OPEN', 'Ouvert' WHERE NOT EXISTS (SELECT 1 FROM status WHERE code='OPEN');
INSERT INTO status (code, libelle)
SELECT 'IN_PROGRESS', 'En cours' WHERE NOT EXISTS (SELECT 1 FROM status WHERE code='IN_PROGRESS');
INSERT INTO status (code, libelle)
SELECT 'CLOSED', 'Fermé' WHERE NOT EXISTS (SELECT 1 FROM status WHERE code='CLOSED');

-- Seed types
INSERT INTO types (code, libelle)
SELECT 'DONATION', 'Donation' WHERE NOT EXISTS (SELECT 1 FROM types WHERE code='DONATION');
INSERT INTO types (code, libelle)
SELECT 'REQUEST', 'Demande' WHERE NOT EXISTS (SELECT 1 FROM types WHERE code='REQUEST');

-- Seed categories (note: table name "categorie" uses column "label")
INSERT INTO categorie (code, label)
SELECT 'FOOD', 'Nourriture' WHERE NOT EXISTS (SELECT 1 FROM categorie WHERE code='FOOD');
INSERT INTO categorie (code, label)
SELECT 'CLOTHES', 'Vêtements' WHERE NOT EXISTS (SELECT 1 FROM categorie WHERE code='CLOTHES');

-- Seed notes (code, libelle, valeur)
INSERT INTO notes (code, libelle, valeur)
SELECT 'EXCEL', 'Excellent', 5 WHERE NOT EXISTS (SELECT 1 FROM notes WHERE code='EXCEL');
INSERT INTO notes (code, libelle, valeur)
SELECT 'GOOD', 'Bon', 4 WHERE NOT EXISTS (SELECT 1 FROM notes WHERE code='GOOD');

-- Continue adding default rows as needed
