<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${not empty pageTitle ? pageTitle : 'Gestion Cotisations'} | Association</title>

    <link rel="shortcut icon" href="${ctx}/assets/upzet/images/favicon.ico">

    <!-- Bootstrap 5 (Upzet) -->
    <link href="${ctx}/assets/upzet/css/bootstrap.min.css" id="bootstrap-style" rel="stylesheet" type="text/css">
    <!-- Upzet icons -->
    <link href="${ctx}/assets/upzet/css/icons.min.css" rel="stylesheet" type="text/css">
    <!-- Font Awesome (icônes utilisées dans les vues) -->
    <link href="${ctx}/assets/plugins/fontawesome-free/css/all.min.css" rel="stylesheet" type="text/css">
    <!-- DataTables (style Bootstrap fourni par Upzet) -->
    <link href="${ctx}/assets/upzet/libs/datatables.net-bs4/css/dataTables.bootstrap4.min.css" rel="stylesheet" type="text/css">
    <link href="${ctx}/assets/upzet/libs/datatables.net-responsive-bs4/css/responsive.bootstrap4.min.css" rel="stylesheet" type="text/css">
    <!-- App CSS (Upzet) -->
    <link href="${ctx}/assets/upzet/css/app.min.css" id="app-style" rel="stylesheet" type="text/css">

    <!-- Applique le thème mémorisé avant le rendu (évite le flash clair) -->
    <script>
        (function () {
            try {
                var t = localStorage.getItem('theme');
                if (t === 'dark') document.documentElement.setAttribute('data-bs-theme', 'dark');
            } catch (e) {}
        })();
    </script>
</head>

<body data-sidebar="dark">
<div id="layout-wrapper">

    <!-- ===== Topbar ===== -->
    <header id="page-topbar">
        <div class="navbar-header">
            <div class="d-flex">
                <!-- Logo -->
                <div class="navbar-brand-box">
                    <a href="${ctx}/" class="logo logo-dark">
                        <span class="logo-sm"><i class="fas fa-hand-holding-usd" style="font-size:22px;color:#556ee6"></i></span>
                        <span class="logo-lg"><i class="fas fa-hand-holding-usd me-1" style="color:#556ee6"></i> Cotisations</span>
                    </a>
                    <a href="${ctx}/" class="logo logo-light">
                        <span class="logo-sm"><i class="fas fa-hand-holding-usd" style="font-size:22px;color:#fff"></i></span>
                        <span class="logo-lg"><i class="fas fa-hand-holding-usd me-1"></i> Cotisations</span>
                    </a>
                </div>

                <button type="button" class="btn btn-sm px-3 font-size-24 header-item waves-effect" id="vertical-menu-btn">
                    <i class="fas fa-bars"></i>
                </button>
            </div>

            <div class="d-flex">
                <!-- Bascule clair / sombre -->
                <div class="dropdown d-inline-block ms-1">
                    <button type="button" class="btn header-item noti-icon waves-effect" id="dark-toggle" title="Mode clair / sombre">
                        <i class="fas fa-moon font-size-18"></i>
                    </button>
                </div>

                <!-- Plein écran -->
                <div class="dropdown d-none d-lg-inline-block ms-1">
                    <button type="button" class="btn header-item noti-icon waves-effect" data-toggle="fullscreen">
                        <i class="fas fa-expand font-size-18"></i>
                    </button>
                </div>

                <!-- Profil -->
                <div class="dropdown d-inline-block">
                    <button type="button" class="btn header-item waves-effect" id="page-header-user-dropdown"
                            data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                        <i class="far fa-user-circle font-size-18 align-middle me-1"></i>
                        <span class="d-none d-xl-inline-block ms-1">${sessionScope.user.prenom} ${sessionScope.user.nom}</span>
                        <i class="fas fa-chevron-down d-none d-xl-inline-block ms-1 font-size-12"></i>
                    </button>
                    <div class="dropdown-menu dropdown-menu-end">
                        <a class="dropdown-item" href="${ctx}/profil">
                            <i class="fas fa-id-card font-size-16 align-middle me-1"></i> Mon profil
                        </a>
                        <div class="dropdown-divider"></div>
                        <a class="dropdown-item text-danger" href="${ctx}/logout">
                            <i class="fas fa-sign-out-alt font-size-16 align-middle me-1 text-danger"></i> Déconnexion
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </header>
