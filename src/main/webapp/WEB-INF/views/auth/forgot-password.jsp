<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
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
        <div class="card-body login-card-body">
            <p class="login-box-msg">
                Saisissez votre email. Un nouveau mot de passe temporaire vous sera envoyé.
            </p>

            <c:if test="${not empty error}">
                <div class="alert alert-danger alert-dismissible">
                    <button type="button" class="close" data-dismiss="alert">&times;</button>
                    <i class="fas fa-ban"></i> ${error}
                </div>
            </c:if>
            <c:if test="${not empty success}">
                <div class="alert alert-success alert-dismissible">
                    <button type="button" class="close" data-dismiss="alert">&times;</button>
                    <i class="fas fa-check"></i> ${success}
                </div>
            </c:if>

            <form action="${pageContext.request.contextPath}/forgot-password" method="post">
                <div class="input-group mb-3">
                    <input type="email" name="email" class="form-control" placeholder="Email" required
                           value="${param.email}">
                    <div class="input-group-append">
                        <div class="input-group-text"><span class="fas fa-envelope"></span></div>
                    </div>
                </div>
                <button type="submit" class="btn btn-primary btn-block">
                    <i class="fas fa-paper-plane mr-1"></i> Envoyer
                </button>
            </form>

            <p class="mt-3 mb-0">
                <a href="${pageContext.request.contextPath}/login">
                    <i class="fas fa-arrow-left mr-1"></i> Retour à la connexion
                </a>
            </p>
        </div>
    </div>
</div>

<script src="${pageContext.request.contextPath}/assets/plugins/jquery/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/plugins/bootstrap/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/dist/js/adminlte.min.js"></script>
</body>
</html>
