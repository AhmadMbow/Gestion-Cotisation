<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Nouveau mot de passe | Gestion Cotisations</title>
    <link rel="shortcut icon" href="${ctx}/assets/upzet/images/favicon.ico">
    <link href="${ctx}/assets/upzet/css/bootstrap.min.css" id="bootstrap-style" rel="stylesheet" type="text/css">
    <link href="${ctx}/assets/upzet/css/icons.min.css" rel="stylesheet" type="text/css">
    <link href="${ctx}/assets/plugins/fontawesome-free/css/all.min.css" rel="stylesheet" type="text/css">
    <link href="${ctx}/assets/upzet/css/app.min.css" id="app-style" rel="stylesheet" type="text/css">
</head>

<body class="bg-pattern">

<div class="account-pages my-5 pt-5">
    <div class="container">
        <div class="row justify-content-center">
            <div class="col-xl-4 col-lg-6 col-md-8">
                <div class="card">
                    <div class="card-body p-4">
                        <div class="text-center mb-4">
                            <a href="${ctx}/" class="d-inline-flex align-items-center">
                                <i class="fas fa-hand-holding-usd font-size-24 text-primary me-2"></i>
                                <span class="font-size-20 fw-semibold">Gestion Cotisations</span>
                            </a>
                        </div>

                        <c:choose>
                            <%-- ===================== Lien invalide / expiré ===================== --%>
                            <c:when test="${invalidToken}">
                                <h4 class="font-size-18 text-muted mt-2 text-center">Lien invalide</h4>
                                <div class="alert alert-danger mt-3" role="alert">
                                    <i class="fas fa-ban me-1"></i>
                                    Ce lien de réinitialisation est invalide ou a expiré
                                    (les liens sont valables 30&nbsp;minutes).
                                </div>
                                <div class="d-grid mt-4">
                                    <a href="${ctx}/forgot-password" class="btn btn-primary waves-effect waves-light">
                                        <i class="fas fa-paper-plane me-1"></i> Demander un nouveau lien
                                    </a>
                                </div>
                            </c:when>

                            <%-- ===================== Formulaire de réinitialisation ===================== --%>
                            <c:otherwise>
                                <h4 class="font-size-18 text-muted mt-2 text-center">Nouveau mot de passe</h4>
                                <p class="mb-4 text-center">Choisissez un mot de passe d'au moins 6 caractères.</p>

                                <c:if test="${not empty error}">
                                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                                        <i class="fas fa-exclamation-circle me-1"></i> ${error}
                                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                                    </div>
                                </c:if>

                                <form action="${ctx}/reset-password" method="post" autocomplete="off">
                                    <input type="hidden" name="_csrf" value="${csrfToken}"/>
                                    <input type="hidden" name="token" value="${token}"/>

                                    <div class="mb-3">
                                        <label class="form-label" for="newPassword">Nouveau mot de passe</label>
                                        <input type="password" id="newPassword" name="newPassword" class="form-control"
                                               placeholder="Nouveau mot de passe" required minlength="6" autofocus>
                                    </div>

                                    <div class="mb-3">
                                        <label class="form-label" for="confirmPassword">Confirmer le mot de passe</label>
                                        <input type="password" id="confirmPassword" name="confirmPassword" class="form-control"
                                               placeholder="Confirmer le mot de passe" required minlength="6">
                                    </div>

                                    <div class="d-grid mt-4">
                                        <button type="submit" class="btn btn-primary waves-effect waves-light">
                                            <i class="fas fa-shield-alt me-1"></i> Modifier le mot de passe
                                        </button>
                                    </div>
                                </form>
                            </c:otherwise>
                        </c:choose>

                        <div class="mt-4 text-center">
                            <a href="${ctx}/login" class="text-muted">
                                <i class="fas fa-arrow-left me-1"></i> Retour à la connexion
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="${ctx}/assets/upzet/libs/jquery/jquery.min.js"></script>
<script src="${ctx}/assets/upzet/libs/bootstrap/js/bootstrap.bundle.min.js"></script>
<script src="${ctx}/assets/upzet/js/app.js"></script>
</body>
</html>
