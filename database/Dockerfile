# ---- Conteneur BASE DE DONNÉES : MySQL 8.3 + schéma intégré ----
# Le schéma est copié dans /docker-entrypoint-initdb.d : MySQL l'exécute
# automatiquement au tout premier démarrage (quand le volume est vide).
FROM mysql:8.3

ENV MYSQL_ALLOW_EMPTY_PASSWORD=yes \
    MYSQL_DATABASE=cotisations_db

# Scripts d'initialisation (exécutés par ordre alphabétique)
COPY schema.sql /docker-entrypoint-initdb.d/01-schema.sql

EXPOSE 3306
