<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>500 — Erreur interne</title>
    <link href="${pageContext.request.contextPath}/assets/upzet/css/bootstrap.min.css" rel="stylesheet" type="text/css">
    <link href="${pageContext.request.contextPath}/assets/plugins/fontawesome-free/css/all.min.css" rel="stylesheet" type="text/css">
    <link href="${pageContext.request.contextPath}/assets/upzet/css/app.min.css" rel="stylesheet" type="text/css">
</head>
<body class="bg-pattern">
<div class="account-pages my-5 pt-5">
    <div class="container">
        <div class="row justify-content-center">
            <div class="col-xl-6 col-lg-8 text-center">
                <h1 class="display-1 fw-bold text-danger">500</h1>
                <h3 class="mt-3"><i class="fas fa-exclamation-triangle text-danger me-1"></i> Erreur interne du serveur.</h3>
                <p class="text-muted">Un problème est survenu. Veuillez réessayer plus tard.</p>
                <a href="${pageContext.request.contextPath}/" class="btn btn-primary mt-2">
                    <i class="fas fa-home me-1"></i> Retour à l'accueil
                </a>
            </div>
        </div>
    </div>
</div>
</body>
</html>
