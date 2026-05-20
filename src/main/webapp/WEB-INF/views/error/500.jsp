<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>500 — Erreur interne</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/plugins/fontawesome-free/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/dist/css/adminlte.min.css">
</head>
<body class="hold-transition sidebar-mini">
<div class="content-wrapper" style="margin-left:0;">
    <section class="content-header"><div class="container-fluid"><h1>Erreur 500</h1></div></section>
    <section class="content">
        <div class="error-page">
            <h2 class="headline text-danger">500</h2>
            <div class="error-content">
                <h3><i class="fas fa-exclamation-triangle text-danger"></i> Erreur interne du serveur.</h3>
                <p>Un problème est survenu. <a href="${pageContext.request.contextPath}/">Retour à l'accueil</a>.</p>
            </div>
        </div>
    </section>
</div>
</body>
</html>
