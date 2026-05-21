# Gestion Cotisations Association — Jakarta EE

Application Web Jakarta EE 10 pour la gestion des **membres**, **cotisations mensuelles** et **amendes** d'une association.

Template UI : **AdminLTE 3.2.0** (Bootstrap 4, MIT License) — intégré dans `src/main/webapp/assets/`.

---

## Stack technique

| Couche        | Technologie                                              |
|---------------|----------------------------------------------------------|
| Présentation  | JSP / JSTL 3.0, AdminLTE 3 (Bootstrap 4), Chart.js       |
| Métier        | Servlets Jakarta EE 10, services **CDI** (Weld)          |
| Persistance   | JPA 3.1 / Hibernate 6                                    |
| Base          | MySQL 8 (prod), H2 in-memory (tests)                     |
| Serveur       | Apache Tomcat 10+ (ou TomEE 10)                          |
| Build         | Maven 3.9+, Java 17                                      |
| Tests         | JUnit 5 (20 tests : 16 unitaires + 4 d'intégration H2)   |

> ⚠️ **Important** — Jakarta EE 10 utilise le namespace `jakarta.*` (et non `javax.*`). Il faut **Tomcat 10+**, pas Tomcat 9.

---

## Structure du projet

```
gestion-cotisations/
├── pom.xml
├── database/
│   ├── schema.sql                       ← Script de création MySQL
│   └── migration_unique_cotisation.sql  ← Migration contrainte unique
├── src/main/java/sn/association/cotisations/
│   ├── entity/                          ← Entités JPA (Membre, Cotisation, Amende, Connexion, PasswordResetToken)
│   ├── dao/                             ← DAO @ApplicationScoped
│   ├── service/                         ← Services métier CDI
│   ├── servlet/                         ← Contrôleurs HTTP (avec @Inject)
│   ├── filter/                          ← AuthFilter, CsrfFilter
│   └── util/                            ← BCrypt, CSRF, JPA, rate-limiter
├── src/main/resources/META-INF/
│   └── persistence.xml                  ← Config JPA prod (MySQL)
├── src/main/webapp/
│   ├── WEB-INF/
│   │   ├── web.xml
│   │   ├── beans.xml                    ← Config CDI
│   │   └── views/                       ← JSP
│   └── assets/                          ← AdminLTE
└── src/test/
    ├── java/.../service/                ← Tests unitaires validation
    ├── java/.../integration/            ← Tests d'intégration H2
    └── resources/META-INF/
        └── persistence.xml              ← Config JPA test (H2)
```

---

## Mise en route

### 1. Prérequis
- JDK 17+
- Maven 3.9+
- MySQL 8 démarré en local
- Tomcat 10+

### 2. Base de données
```bash
mysql -u root -p < database/schema.sql
```
Si vous avez une base déjà créée à partir d'une ancienne version, appliquez la migration :
```bash
mysql -u root cotisations_db < database/migration_unique_cotisation.sql
```

Ajustez l'utilisateur / mot de passe dans `src/main/resources/META-INF/persistence.xml` ou via les variables d'environnement (voir plus bas).

### 3. Build
```bash
mvn clean package
```
Produit `target/gestion-cotisations.war`.

### 4. Tests
```bash
mvn test
```
20 tests passent :
- **16 unitaires** (sans BDD) : hash BCrypt, validations métier
- **4 d'intégration** sur H2 in-memory : persistence membre / cotisation / unicité

### 5. Déploiement
- **IDE** (IntelliJ / Eclipse / NetBeans) : ajoutez le projet à un serveur Tomcat 10.
- **Tomcat CLI** : copiez le `.war` dans `$CATALINA_HOME/webapps/`.

L'appli est ensuite accessible sur : `http://localhost:8080/gestion-cotisations/`

### 6. Comptes de démo (mot de passe : `admin123`)
| Email             | Rôle    |
|-------------------|---------|
| admin@asso.sn     | ADMIN   |
| aminata@asso.sn   | MEMBRE  |
| moussa@asso.sn    | MEMBRE  |

---

## Modules

### Fonctionnels
- **Membres** : CRUD, activer/désactiver, recherche (DataTables).
- **Cotisations** : enregistrement, historique, retards, mois dus, reçu PDF (iText).
- **Amendes** : génération auto pour retards + amendes manuelles, paiement, historique.
- **Authentification** : email/mot de passe BCrypt, rôles ADMIN/MEMBRE, anti session-fixation.
- **Profil** : édition des infos personnelles + changement de mot de passe.
- **Rapports** : dashboard, statistiques, top retardataires, évolution 12 mois (Chart.js).
- **Exports Excel** (Apache POI) — membres, cotisations, amendes.
- **Sauvegarde SQL** (`/admin/backup`) — dump complet téléchargeable.
- **Emails** (Jakarta Mail) — rappels de cotisation + lien de réinitialisation de mot de passe.
- **Historique des connexions** (`/admin/connexions`).

### Sécurité
- **Protection CSRF** sur tous les POST (token base64-32B en session, comparaison constant-time).
- **Rate-limit /login** : 5 tentatives par IP / 10 min → blocage 10 min.
- **Reset password par lien sécurisé** : token aléatoire en base, expiration 30 min, usage unique.
- **Anti-énumération** : `/forgot-password` répond la même chose quelle que soit l'existence de l'email.
- **Contrainte d'unicité BD** `(membre, mois, annee)` empêchant les doublons de cotisation même en concurrence.
- **Session HTTP-only** (cookie non lisible en JS).

---

## Variables d'environnement

| Variable        | Défaut | Rôle |
|-----------------|--------|------|
| `DB_URL`        | (persistence.xml) | URL JDBC MySQL |
| `DB_USER`       | (persistence.xml) | Utilisateur MySQL |
| `DB_PASSWORD`   | (persistence.xml) | Mot de passe MySQL |
| `SMTP_HOST`     | — | Serveur SMTP (ex. `smtp.gmail.com`). Si vide, les emails sont logués. |
| `SMTP_PORT`     | `587` | Port SMTP |
| `SMTP_USER`     | — | Compte SMTP |
| `SMTP_PASSWORD` | — | Mot de passe ou _app password_ |
| `SMTP_FROM`     | = `SMTP_USER` | Adresse expéditeur |
| `SMTP_STARTTLS` | `true` | Active STARTTLS |

Pour Gmail, créer un **App Password** sur https://myaccount.google.com/apppasswords.

---

## Passage en production

Modifier `src/main/resources/META-INF/persistence.xml` :

```xml
<property name="hibernate.hbm2ddl.auto" value="validate"/>
<property name="hibernate.show_sql"    value="false"/>
<property name="hibernate.format_sql"  value="false"/>
```

Activer HTTPS sur Tomcat puis dans `web.xml` :

```xml
<session-config>
    <cookie-config>
        <http-only>true</http-only>
        <secure>true</secure>
    </cookie-config>
</session-config>
```

---

## Licence

- Code applicatif : à définir
- AdminLTE 3.2.0 : [MIT License](https://github.com/ColorlibHQ/AdminLTE/blob/master/LICENSE)
