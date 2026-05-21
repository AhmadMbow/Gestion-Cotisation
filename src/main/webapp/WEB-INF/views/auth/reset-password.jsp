<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Nouveau mot de passe | Gestion Cotisations</title>
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

            <c:choose>
                <c:when test="${invalidToken}">
                    <div class="alert alert-danger">
                        <i class="fas fa-ban mr-1"></i>
                        Ce lien de réinitialisation est invalide ou expiré.
                    </div>
                    <a href="${pageContext.request.contextPath}/forgot-password" class="btn btn-primary btn-block">
                        Demander un nouveau lien
                    </a>
                    <a href="${pageContext.request.contextPath}/login" class="btn btn-default btn-block mt-2">
                        Retour à la connexion
                    </a>
                </c:when>
                <c:otherwise>
                    <p class="login-box-msg">Choisissez un nouveau mot de passe</p>

                    <c:if test="${not empty error}">
                        <div class="alert alert-danger">
                            <i class="fas fa-exclamation-circle mr-1"></i> ${error}
                        </div>
                    </c:if>

                    <form action="${pageContext.request.contextPath}/reset-password" method="post" autocomplete="off">
                        <input type="hidden" name="_csrf" value="${csrfToken}">
                        <input type="hidden" name="token" value="${token}">

                        <div class="input-group mb-3">
                            <input type="password" name="newPassword" class="form-control"
                                   placeholder="Nouveau mot de passe" required minlength="6">
                            <div class="input-group-append">
                                <div class="input-group-text"><span class="fas fa-lock"></span></div>
                            </div>
                        </div>
                        <div class="input-group mb-3">
                            <input type="password" name="confirmPassword" class="form-control"
                                   placeholder="Confirmer" required minlength="6">
                            <div class="input-group-append">
                                <div class="input-group-text"><span class="fas fa-lock"></span></div>
                            </div>
                        </div>

                        <button type="submit" class="btn btn-primary btn-block">
                            <i class="fas fa-shield-alt mr-1"></i> Modifier mon mot de passe
                        </button>
                    </form>
                </c:otherwise>
            </c:choose>

        </div>
    </div>
</div>
<script src="${pageContext.request.contextPath}/assets/plugins/jquery/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/plugins/bootstrap/js/bootstrap.bundle.min.js"></script>
</body>
</html>
