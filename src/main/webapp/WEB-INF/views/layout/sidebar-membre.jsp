<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<aside class="main-sidebar sidebar-dark-primary elevation-4">
    <a href="${pageContext.request.contextPath}/membre/dashboard" class="brand-link">
        <i class="fas fa-user-circle brand-image ml-3" style="opacity:.9"></i>
        <span class="brand-text font-weight-light">Espace <b>Membre</b></span>
    </a>

    <div class="sidebar">
        <div class="user-panel mt-3 pb-3 mb-3 d-flex">
            <div class="image">
                <i class="fas fa-user fa-2x text-white"></i>
            </div>
            <div class="info">
                <a href="#" class="d-block">${sessionScope.user.prenom} ${sessionScope.user.nom}</a>
                <small class="text-success"><i class="fas fa-circle text-success"></i> En ligne</small>
            </div>
        </div>

        <nav class="mt-2">
            <ul class="nav nav-pills nav-sidebar flex-column" data-widget="treeview" role="menu" data-accordion="false">

                <li class="nav-item">
                    <a href="${pageContext.request.contextPath}/membre/dashboard"
                       class="nav-link ${activeMenu == 'dashboard' ? 'active' : ''}">
                        <i class="nav-icon fas fa-tachometer-alt"></i>
                        <p>Mon tableau de bord</p>
                    </a>
                </li>

                <li class="nav-item">
                    <a href="${pageContext.request.contextPath}/membre/cotisations"
                       class="nav-link ${activeMenu == 'cotisations' ? 'active' : ''}">
                        <i class="nav-icon fas fa-money-check-alt"></i>
                        <p>Mes cotisations</p>
                    </a>
                </li>

                <li class="nav-item">
                    <a href="${pageContext.request.contextPath}/membre/payer"
                       class="nav-link ${activeMenu == 'payer' ? 'active' : ''}">
                        <i class="nav-icon fas fa-credit-card text-success"></i>
                        <p>Effectuer un paiement</p>
                    </a>
                </li>

                <li class="nav-item">
                    <a href="${pageContext.request.contextPath}/membre/amendes"
                       class="nav-link ${activeMenu == 'amendes' ? 'active' : ''}">
                        <i class="nav-icon fas fa-gavel"></i>
                        <p>Mes amendes</p>
                    </a>
                </li>

                <li class="nav-item">
                    <a href="${pageContext.request.contextPath}/profil"
                       class="nav-link ${activeMenu == 'profil' ? 'active' : ''}">
                        <i class="nav-icon fas fa-id-card"></i>
                        <p>Mon profil</p>
                    </a>
                </li>

                <li class="nav-header">COMPTE</li>
                <li class="nav-item">
                    <a href="${pageContext.request.contextPath}/logout" class="nav-link">
                        <i class="nav-icon fas fa-sign-out-alt"></i>
                        <p>Déconnexion</p>
                    </a>
                </li>
            </ul>
        </nav>
    </div>
</aside>
