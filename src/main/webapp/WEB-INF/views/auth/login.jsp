<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Connexion | Gestion Cotisations</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/plugins/fontawesome-free/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/plugins/login-template/css/util.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/plugins/login-template/css/main.css">

    <style>
        /* Petits ajustements pour notre contexte */
        .login100-form-title small { display:block; font-size:13px; font-weight:400; color:#666; margin-top:6px; }
        .alert-msg {
            width:100%; padding:10px 14px; border-radius:12px; font-size:13px;
            margin-bottom:12px; display:flex; align-items:flex-start; gap:8px;
        }
        .alert-msg.error   { background:#fdecea; color:#c62828; border:1px solid #f5c6cb; }
        .alert-msg.success { background:#e8f5e9; color:#2e7d32; border:1px solid #c8e6c9; }
        .form-link-row { display:flex; justify-content:space-between; align-items:center; width:100%; padding-top:14px; }
    </style>
</head>
<body>

<div class="limiter">
    <div class="container-login100">
        <div class="wrap-login100">

            <div class="login100-pic js-tilt" data-tilt>
                <img src="${pageContext.request.contextPath}/assets/plugins/login-template/images/img-01.png" alt="Illustration">
            </div>

            <form class="login100-form validate-form" action="${pageContext.request.contextPath}/login" method="post" autocomplete="on">
                <input type="hidden" name="_csrf" value="${csrfToken}">

                <span class="login100-form-title">
                    Connexion
                    <small>Gestion Cotisations Association</small>
                </span>

                <jsp:include page="/WEB-INF/views/layout/flash-messages.jsp"/>

                <c:if test="${not empty error}">
                    <div class="alert-msg error">
                        <i class="fas fa-exclamation-circle"></i>
                        <span>${error}</span>
                    </div>
                </c:if>
                <c:if test="${not empty success}">
                    <div class="alert-msg success">
                        <i class="fas fa-check-circle"></i>
                        <span>${success}</span>
                    </div>
                </c:if>

                <div class="wrap-input100 validate-input" data-validate="Email requis">
                    <input class="input100" type="email" name="email" required autofocus
                           placeholder="Email" value="${param.email}">
                    <span class="focus-input100"></span>
                    <span class="symbol-input100">
                        <i class="fa fa-envelope" aria-hidden="true"></i>
                    </span>
                </div>

                <div class="wrap-input100 validate-input" data-validate="Mot de passe requis">
                    <input class="input100" type="password" name="password" required
                           placeholder="Mot de passe">
                    <span class="focus-input100"></span>
                    <span class="symbol-input100">
                        <i class="fa fa-lock" aria-hidden="true"></i>
                    </span>
                </div>

                <div class="container-login100-form-btn">
                    <button type="submit" class="login100-form-btn">
                        Se connecter
                    </button>
                </div>

                <div class="form-link-row">
                    <span class="txt1">Mot de passe perdu ?</span>
                    <a class="txt2" href="${pageContext.request.contextPath}/forgot-password">
                        Réinitialiser
                        <i class="fa fa-long-arrow-right" aria-hidden="true" style="margin-left:4px"></i>
                    </a>
                </div>
            </form>
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
