# Connexion MySQL et création automatique des tables (KindConnect)

Ce guide explique comment lier le projet backend à une base de données MySQL "connect" et activer la création automatique des tables (développement).

## 1) Configuration dans `application.properties`
Fichier : `backend/src/main/resources/application.properties`

- Par défaut le projet est configuré pour utiliser la base de données `connect` :

```
spring.datasource.url=jdbc:mysql://localhost:3306/connect?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=root
```

- Par défaut, Hibernate DDL auto est activé en mode DEV pour créer automatiquement les tables :
```
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.hbm2ddl.auto=update
```
> Si vous préférez gérer le schéma via Flyway, basculez ces valeurs (`ddl-auto=none`) et activez Flyway (`spring.flyway.enabled=true`) et adaptez vos migrations.

## 2) Créer la base de données dans MySQL Workbench
Ouvrez MySQL Workbench et exécutez :

```sql
CREATE DATABASE IF NOT EXISTS `connect`;
-- (optionnel) Créer un utilisateur et lui donner les droits
CREATE USER IF NOT EXISTS 'kinduser'@'localhost' IDENTIFIED BY 'kindpass';
GRANT ALL PRIVILEGES ON connect.* TO 'kinduser'@'localhost';
FLUSH PRIVILEGES;
```

Puis mettez à jour `application.properties` pour utiliser `kinduser` / `kindpass` si vous créez cet utilisateur.

## 3) Lancer l'application (Maven)
Depuis le dossier `backend`, exécutez :

```powershell
cd c:\Users\Pc doctor\Desktop\kindconnect\backend
mvn clean package
mvn spring-boot:run
```

Ou exécuter le jar :

```powershell
java -jar target\kindconnect-backend-0.0.1-SNAPSHOT.jar
```

## 4) Vérifier la création des tables
Dans MySQL Workbench, actualisez le schéma `connect` puis faites :

```sql
USE connect;
SHOW TABLES;
```

Les tables seront créées automatiquement si `spring.jpa.hibernate.ddl-auto=update`.

## 4.1) Initialisation des données (seed)
Le projet contient un fichier `backend/src/main/resources/data.sql` qui insère automatiquement les données de référence (roles, status, types, categories et notes) après création des tables.

Assurez-vous des propriétés suivantes dans `application.properties` (elles sont déjà présentes) :

```
spring.sql.init.mode=always
spring.sql.init.continue-on-error=true
spring.jpa.defer-datasource-initialization=true
```

Ces propriétés garantissent que :
- Hibernate crée d'abord le schéma (tables),
- puis `data.sql` est exécuté pour insérer les valeurs de référence.

Vérifiez les valeurs insérées :

```sql
USE connect;
SELECT * FROM role;
SELECT * FROM status;
SELECT * FROM types;
SELECT * FROM categorie;
SELECT * FROM notes;
```

## 5) Utiliser Flyway (optionnel)
Si vous préférez les migrations (recommandé pour production) :

- Mettez dans `application.properties` :
```
spring.flyway.enabled=true
spring.jpa.hibernate.ddl-auto=none
spring.flyway.locations=classpath:db/migration
```
- Vérifiez votre fichier `backend/src/main/resources/db/migration/V1__init_tables.sql` et adaptez les noms de tables/colonnes pour correspondre aux entités JPA dans `backend/src/main/java/com/kindconnect/model`.
- Ensuite, lancez l'application ; Flyway exécutera les migrations automatiquement.

## Notes / Recommandations
- Pour le développement rapide, `hibernate.ddl-auto=update` est pratique mais ne doit pas être utilisé en production.
- Pour la production, utilisez Flyway pour gérer les versions du schéma.
- Si vos entités JPA ont des noms de tables différents de vos migrations SQL, synchronisez-les pour éviter les conflits.

---
Si vous voulez, je peux :
- Activer Flyway et adapter `V1__init_tables.sql` pour correspondre aux entités du projet automatiquement (je peux faire les changements SQL), ou
- Laisser Hibernate créer les tables (configuration déjà faite) et vous montrer comment vérifier et tester.

Dites-moi quelle option vous préférez (Flyway migration ou Hibernate auto-creation) et j’appliquerai les changements nécessaires.
## Utilisateur inscrit (Membre) — API et actions
En tant que membre (inscrit), l'utilisateur doit être en mesure de :

- Publier une offre/demande (POST /api/publications) — nécessite authentification et JWT ou session.
- Rechercher / filtrer les publications :
	- GET /api/publications - retourne toutes les publications (visiteur ou utilisateur)
	- GET /api/publications/available - retourne publications disponibles
	- GET /api/publications/search?q=mot - recherche sur titre/description (pagination avec ?page=0&size=10)
	- GET /api/publications/categorie/{code}, /api/publications/status/{code}, /api/publications/type/{code}
- Envoyer un message à un membre : POST /api/messages
	- Exemple body : { "recipientId": 2, "publicationId": 123, "content": "Je peux vous aider" }
	- Une notification sera créée pour le destinataire.
- Envoyer un remerciement : POST /api/publications/{id}/thank — envoie une notification au propriétaire de la publication.
- Marquer une action comme complétée : POST /api/publications/{id}/complete — marque publication `estDisponible=false` et notifie le propriétaire.
- Modifier son profil : PUT /api/users/me — body JSON des champs modifiables (firstName, lastName, phoneNumber, city, profilePicture)

### Exemples cURL pour les actions de Membre
Remplacez <TOKEN> par le `accessToken` JWT retourné par `/api/auth/signin`.

1) Créer une publication (membre) :
```powershell
curl -X POST "http://localhost:8081/api/publications" -H "Authorization: Bearer <TOKEN>" -H "Content-Type: application/json" -d "{
	\"title\": \"Propose courses\",
	\"description\": \"Je peux faire les courses pour les personnes âgées\",
	\"location\": \"Tunis\",
	\"estDisponible\": true,
	\"categorie\": { \"id\": 1 }
}"
```

Vérifier dans MySQL Workbench que la publication est associée à l'utilisateur (jointure) :
```sql
SELECT p.id, p.title, p.location, p.created_at, p.est_disponible, u.id AS user_id, u.username, u.email
FROM publications p
JOIN user_authenticated u ON p.user_id = u.id
ORDER BY p.created_at DESC;
```

2) Rechercher une publication :
```powershell
curl "http://localhost:8081/api/publications/search?q=courses&page=0&size=10"
```

3) Envoyer un message (membre) :
```powershell
curl -X POST "http://localhost:8081/api/messages" -H "Authorization: Bearer <TOKEN>" -H "Content-Type: application/json" -d "{
	\"recipientId\": 2,
	\"publicationId\": 3,
	\"content\": \"Je suis disponible pour aider demain matin\"
}"
```

4) Envoyer un remerciement :
```powershell
curl -X POST "http://localhost:8081/api/publications/3/thank" -H "Authorization: Bearer <TOKEN>"
```

5) Marquer publication comme complétée :
```powershell
curl -X POST "http://localhost:8081/api/publications/3/complete" -H "Authorization: Bearer <TOKEN>"
```

6) Modifier son profil :
```powershell
curl -X PUT "http://localhost:8081/api/users/me" -H "Authorization: Bearer <TOKEN>" -H "Content-Type: application/json" -d "{ \"firstName\": \"Paul\" }"
```


## Comportement Visiteur (non connecté)
Le rôle 'Visiteur' correspond à un utilisateur non authentifié. Voici ce qu'il peut faire:

- Consulter la page d'accueil (`/`) et voir les dernières offres/demandes.
- Consulter la liste complète des demandes/offres sur `/demandes`.
- S'inscrire via l'interface d'inscription `/connexion` (onglet inscription) — cela envoie une requête à `/api/auth/signup`.
- Se connecter via l'interface `/connexion` (onglet connexion) — cela envoie une requête à `/api/auth/signin` et sauvegarde le jeton JWT en local (si connexion réussie).

Les endpoints API publics incluent :
- `GET /api/publications/available` — récupérer les publications disponibles (pour visiteurs et utilisateurs non connectés).
- `GET /api/publications` — récupérer toutes les publications (si nécessaire).
- `POST /api/auth/signup`, `POST /api/auth/signin` — créent/s'authentifient via API JWT.

Pour vérifier le comportement Visiteur après avoir lancé l'application :
1) Ouvrez un navigateur en navigation privée (non connecté), chargez `http://localhost:8081/` et `http://localhost:8081/demandes`.
2) Vérifiez que vous voyez les annonces (ou aucun si la DB est vide).
3) Essayez de vous inscrire depuis `http://localhost:8081/connexion` et vérifier que l'API répond correctement.

