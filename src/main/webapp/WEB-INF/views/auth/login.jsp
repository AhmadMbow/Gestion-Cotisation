<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Connexion | Gestion Cotisations</title>
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
                        <h4 class="font-size-18 text-muted mt-2 text-center">Bienvenue !</h4>
                        <p class="mb-4 text-center">Connectez-vous à votre espace.</p>

                        <jsp:include page="/WEB-INF/views/layout/flash-messages.jsp"/>

                        <c:if test="${not empty error}">
                            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                                <i class="fas fa-ban me-1"></i> ${error}
                                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                            </div>
                        </c:if>
                        <c:if test="${not empty success}">
                            <div class="alert alert-success alert-dismissible fade show" role="alert">
                                <i class="fas fa-check me-1"></i> ${success}
                                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                            </div>
                        </c:if>

                        <form action="${ctx}/login" method="post">
                            <div class="mb-3">
                                <label class="form-label" for="email">Email</label>
                                <input type="email" id="email" name="email" class="form-control"
                                       placeholder="Saisissez votre email" required value="${param.email}">
                            </div>
                            <div class="mb-3">
                                <label class="form-label" for="password">Mot de passe</label>
                                <input type="password" id="password" name="password" class="form-control"
                                       placeholder="Saisissez votre mot de passe" required>
                            </div>
                            <div class="row">
                                <div class="col">
                                    <div class="form-check">
                                        <input type="checkbox" class="form-check-input" id="remember" name="remember">
                                        <label class="form-check-label" for="remember">Se souvenir de moi</label>
                                    </div>
                                </div>
                                <div class="col-7">
                                    <div class="text-md-end mt-1 mt-md-0">
                                        <a href="${ctx}/forgot-password" class="text-muted">
                                            <i class="fas fa-lock"></i> Mot de passe oublié ?
                                        </a>
                                    </div>
                                </div>
                            </div>
                            <div class="d-grid mt-4">
                                <button class="btn btn-primary waves-effect waves-light" type="submit">Connexion</button>
                            </div>
                        </form>
                    </div>
                </div>
                <div class="mt-4 text-center">
                    <p class="text-muted">© <script>document.write(new Date().getFullYear())</script> Gestion Cotisations Association</p>
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
