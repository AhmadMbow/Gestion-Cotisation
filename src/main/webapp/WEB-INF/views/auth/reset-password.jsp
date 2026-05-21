<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Nouveau mot de passe | Gestion Cotisations</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/plugins/fontawesome-free/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/plugins/login-template/css/util.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/plugins/login-template/css/main.css">

    <style>
        .login100-form-title small { display:block; font-size:13px; font-weight:400; color:#666; margin-top:6px; }
        .alert-msg {
            width:100%; padding:10px 14px; border-radius:12px; font-size:13px;
            margin-bottom:12px; display:flex; align-items:flex-start; gap:8px;
        }
        .alert-msg.error   { background:#fdecea; color:#c62828; border:1px solid #f5c6cb; }
        .alert-msg.success { background:#e8f5e9; color:#2e7d32; border:1px solid #c8e6c9; }
        .form-link-row { display:flex; justify-content:space-between; align-items:center; width:100%; padding-top:14px; }
        .helper-text {
            font-size:13px; color:#666; line-height:1.5; text-align:center;
            margin-top: -28px; margin-bottom: 24px;
        }
        .btn-secondary-link {
            display:block; width:100%; text-align:center;
            padding:13px 0; margin-top:10px;
            border-radius:25px; background:#f0f0f0; color:#666;
            font-family:'Poppins',sans-serif; font-weight:500; font-size:14px;
            transition: all 0.4s;
        }
        .btn-secondary-link:hover { background:#e0e0e0; color:#333; text-decoration:none; }
    </style>
</head>
<body>

<div class="limiter">
    <div class="container-login100">
        <div class="wrap-login100">

            <div class="login100-pic js-tilt" data-tilt>
                <img src="${pageContext.request.contextPath}/assets/plugins/login-template/images/img-01.png" alt="Illustration">
            </div>

            <c:choose>
                <%-- ========================= Lien invalide / expiré ========================= --%>
                <c:when test="${invalidToken}">
                    <div class="login100-form validate-form">
                        <span class="login100-form-title">
                            Lien invalide
                            <small>Réinitialisation impossible</small>
                        </span>

                        <div class="alert-msg error">
                            <i class="fas fa-ban"></i>
                            <span>
                                Ce lien de réinitialisation est invalide ou a expiré
                                (les liens sont valables 30&nbsp;minutes).
                            </span>
                        </div>

                        <a class="login100-form-btn"
                           href="${pageContext.request.contextPath}/forgot-password"
                           style="text-decoration:none">
                            <i class="fa fa-paper-plane" aria-hidden="true" style="margin-right:8px"></i>
                            Demander un nouveau lien
                        </a>

                        <a class="btn-secondary-link" href="${pageContext.request.contextPath}/login">
                            <i class="fa fa-long-arrow-left" aria-hidden="true" style="margin-right:4px"></i>
                            Retour à la connexion
                        </a>
                    </div>
                </c:when>

                <%-- ========================= Formulaire de réinitialisation ========================= --%>
                <c:otherwise>
                    <form class="login100-form validate-form"
                          action="${pageContext.request.contextPath}/reset-password"
                          method="post" autocomplete="off">

                        <input type="hidden" name="_csrf" value="${csrfToken}">
                        <input type="hidden" name="token" value="${token}">

                        <span class="login100-form-title">
                            Nouveau mot de passe
                            <small>Au moins 6 caractères</small>
                        </span>

                        <c:if test="${not empty error}">
                            <div class="alert-msg error">
                                <i class="fas fa-exclamation-circle"></i>
                                <span>${error}</span>
                            </div>
                        </c:if>

                        <div class="wrap-input100 validate-input" data-validate="Mot de passe requis">
                            <input class="input100" type="password" name="newPassword" required
                                   minlength="6" placeholder="Nouveau mot de passe" autofocus>
                            <span class="focus-input100"></span>
                            <span class="symbol-input100">
                                <i class="fa fa-lock" aria-hidden="true"></i>
                            </span>
                        </div>

                        <div class="wrap-input100 validate-input" data-validate="Confirmation requise">
                            <input class="input100" type="password" name="confirmPassword" required
                                   minlength="6" placeholder="Confirmer le mot de passe">
                            <span class="focus-input100"></span>
                            <span class="symbol-input100">
                                <i class="fa fa-check-double" aria-hidden="true"></i>
                            </span>
                        </div>

                        <div class="container-login100-form-btn">
                            <button type="submit" class="login100-form-btn">
                                <i class="fa fa-shield-alt" aria-hidden="true" style="margin-right:8px"></i>
                                Modifier le mot de passe
                            </button>
                        </div>

                        <div class="form-link-row">
                            <span class="txt1">Vous vous souvenez ?</span>
                            <a class="txt2" href="${pageContext.request.contextPath}/login">
                                <i class="fa fa-long-arrow-left" aria-hidden="true" style="margin-right:4px"></i>
                                Connexion
                            </a>
                        </div>
                    </form>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>

<script src="${pageContext.request.contextPath}/assets/plugins/jquery/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/plugins/login-template/js/tilt.jquery.min.js"></script>
<script>
    $(function () { $('.js-tilt').tilt({ scale: 1.1 }); });
</script>
</body>
</html>
