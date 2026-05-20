# Gestion Cotisations Association — Jakarta EE

Application Web Jakarta EE pour la gestion des **membres**, **cotisations mensuelles** et **amendes** d'une association.

Template UI : **AdminLTE 3.2.0** (Bootstrap 4, MIT License) — déjà intégré dans `src/main/webapp/assets/`.

---

## Stack technique

| Couche        | Technologie                                              |
|---------------|----------------------------------------------------------|
| Présentation  | JSP / JSTL 3.0, AdminLTE 3 (Bootstrap 4), Chart.js       |
| Métier        | Servlets Jakarta EE 10, services CDI                     |
| Persistance   | JPA 3.1 / Hibernate 6                                    |
| Base          | MySQL 8                                                  |
| Serveur       | Apache Tomcat 10+ (ou TomEE 10)                          |
| Build         | Maven 3.9+, Java 17                                      |

> ⚠️ **Important** — Jakarta EE 10 utilise le namespace `jakarta.*` (et non `javax.*`). Il faut donc **Tomcat 10+**, pas Tomcat 9.

---

## Structure du projet

```
gestion-cotisations/
├── pom.xml
├── database/
│   └── schema.sql                  ← Script de création MySQL
├── src/main/java/sn/association/cotisations/
│   ├── entity/                     ← Entités JPA (Membre, Cotisation, Amende)
│   ├── dao/                        ← Accès aux données
│   ├── service/                    ← Logique métier
│   ├── servlet/                    ← Contrôleurs HTTP
│   ├── filter/                     ← Filtres (auth, encoding…)
│   └── util/                       ← Utilitaires (BCrypt, PDF, Excel…)
├── src/main/resources/META-INF/
│   └── persistence.xml             ← Config JPA / Hibernate
└── src/main/webapp/
    ├── WEB-INF/
    │   ├── web.xml
    │   └── views/
    │       ├── auth/login.jsp
    │       ├── admin/dashboard.jsp
    │       ├── member/dashboard.jsp
    │       ├── layout/             ← header, sidebar, footer
    │       └── error/              ← 404, 500
    ├── assets/                     ← AdminLTE (dist + plugins)
    └── index.jsp
```

---

## Mise en route

### 1. Prérequis
- JDK 17+
- Maven 3.9+
- MySQL 8 démarré en local
- Tomcat 10+ (configuré dans l'IDE)

### 2. Base de données
```bash
mysql -u root -p < database/schema.sql
```
Ajustez ensuite l'utilisateur / mot de passe dans `src/main/resources/META-INF/persistence.xml`.

### 3. Build
```bash
mvn clean package
```
Produit `target/gestion-cotisations.war`.

### 4. Déploiement
- **IntelliJ / NetBeans / Eclipse** : ajoutez le projet à un serveur Tomcat 10.
- **Tomcat en ligne de commande** : copiez le `.war` dans `$CATALINA_HOME/webapps/`.

L'application est ensuite accessible sur : `http://localhost:8080/gestion-cotisations/`

### 5. Comptes de démo (mot de passe : `admin123`)
| Email             | Rôle    |
|-------------------|---------|
| admin@asso.sn     | ADMIN   |
| aminata@asso.sn   | MEMBRE  |
| moussa@asso.sn    | MEMBRE  |

---

## Conventions

- Les vues JSP sont protégées dans `WEB-INF/views/` (non accessibles directement).
- Les assets statiques (`/assets/**`) servent CSS / JS d'AdminLTE.
- Les fragments communs (header, sidebar, footer) sont dans `layout/` et inclus via `<jsp:include>`.
- Variable `${activeMenu}` à définir dans chaque page pour surligner l'élément de menu actif.

---

## TODO côté code

Cette base contient l'UI complète + 3 servlets de démonstration. Il reste à implémenter :

1. **Entités JPA** : `Membre`, `Cotisation`, `Amende` (mapping dans `persistence.xml`).
2. **DAO** : EntityManager + opérations CRUD.
3. **Services** : `AuthService` (BCrypt), `MembreService`, `CotisationService`, `AmendeService`.
4. **Filtre d'authentification** : protéger `/admin/**` et `/membre/**`.
5. **Servlets CRUD** pour membres, cotisations, amendes.
6. **Génération PDF** (iText) et **Excel** (Apache POI).
7. **Envoi d'emails** (Jakarta Mail) pour les rappels de cotisation.

---

## Licence

- Code applicatif : à définir
- AdminLTE 3.2.0 : [MIT License](https://github.com/ColorlibHQ/AdminLTE/blob/master/LICENSE)
