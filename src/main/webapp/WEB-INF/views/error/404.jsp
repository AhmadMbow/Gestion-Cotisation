<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>404 — Page introuvable</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/plugins/fontawesome-free/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/dist/css/adminlte.min.css">
</head>
<body class="hold-transition sidebar-mini">
<div class="content-wrapper" style="margin-left:0;">
    <section class="content-header"><div class="container-fluid"><h1>Erreur 404</h1></div></section>
    <section class="content">
        <div class="error-page">
            <h2 class="headline text-warning">404</h2>
            <div class="error-content">
                <h3><i class="fas fa-exclamation-triangle text-warning"></i> Oups ! Page introuvable.</h3>
                <p>La page que vous recherchez n'existe pas. <a href="${pageContext.request.contextPath}/">Retour à l'accueil</a>.</p>
            </div>
        </div>
    </section>
</div>
</body>
</html>
