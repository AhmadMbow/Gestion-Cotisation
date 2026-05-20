<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Mot de passe oublié | Gestion Cotisations</title>
    <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Source+Sans+Pro:300,400,400i,700&display=fallback">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/plugins/fontawesome-free/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/dist/css/adminlte.min.css">
</head>
<body class="hold-transition login-page">
<div class="login-box">
    <div class="login-logo">
        <a href="${pageContext.request.contextPath}/"><b>Gestion</b>Cotisations</a>
    </div>
    <div class="card">
        <div class="card-body login-card-body text-center">
            <i class="fas fa-tools fa-4x text-warning mb-3"></i>
            <h4 class="mb-2">Module en construction</h4>
            <p class="login-box-msg text-muted">
                La réinitialisation par email sera bientôt disponible.<br>
                Contactez l'administrateur pour réinitialiser votre mot de passe.
            </p>
            <a href="${pageContext.request.contextPath}/login" class="btn btn-primary btn-block">
                <i class="fas fa-arrow-left mr-1"></i> Retour à la connexion
            </a>
        </div>
    </div>
</div>
</body>
</html>
