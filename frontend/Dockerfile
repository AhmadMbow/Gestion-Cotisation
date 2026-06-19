# ---- Conteneur FRONTEND : Nginx ----
# Sert les ressources statiques (CSS / JS / images / fonts) et fait
# reverse-proxy de toutes les requêtes dynamiques vers le backend Tomcat.
#
# NB : le contexte de build est la racine du projet (voir docker-compose.yml),
# afin de pouvoir copier les assets depuis src/main/webapp/assets.
FROM nginx:1.27-alpine

# Copie des assets statiques de l'application
COPY src/main/webapp/assets/ /usr/share/nginx/html/assets/

# Configuration Nginx (static + reverse-proxy)
COPY frontend/nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
