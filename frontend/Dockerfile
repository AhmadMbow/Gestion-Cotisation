# ---- Conteneur FRONTEND : Nginx ----
# Sert les ressources statiques (CSS / JS / images / fonts) et fait
# reverse-proxy de toutes les requêtes dynamiques vers le backend Tomcat.
#
# L'adresse du backend est configurable au runtime via les variables
# d'environnement BACKEND_HOST / BACKEND_PORT (utile pour déployer en cloud
# où le backend n'est pas joignable sous le nom "backend").
#
# NB : le contexte de build est la racine du projet (voir docker-compose.yml),
# afin de pouvoir copier les assets depuis src/main/webapp/assets.
FROM nginx:1.27-alpine

# Valeurs par défaut (réseau docker-compose)
ENV BACKEND_HOST=backend \
    BACKEND_PORT=8080

# Copie des assets statiques de l'application
COPY src/main/webapp/assets/ /usr/share/nginx/html/assets/

# Template de config : l'entrypoint nginx fait l'envsubst au démarrage
COPY frontend/default.conf.template /etc/nginx/templates/default.conf.template

EXPOSE 80
