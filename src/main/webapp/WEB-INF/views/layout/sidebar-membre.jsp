<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<!-- ===== Sidebar membre ===== -->
<div class="vertical-menu">
    <div data-simplebar class="h-100">
        <div id="sidebar-menu">
            <ul class="metismenu list-unstyled" id="side-menu">

                <li class="menu-title">Espace Membre</li>

                <li>
                    <a href="${ctx}/membre/dashboard" class="waves-effect ${activeMenu == 'dashboard' ? 'mm-active' : ''}">
                        <i class="fas fa-tachometer-alt"></i>
                        <span>Mon tableau de bord</span>
                    </a>
                </li>

                <li>
                    <a href="${ctx}/membre/cotisations" class="waves-effect ${activeMenu == 'cotisations' ? 'mm-active' : ''}">
                        <i class="fas fa-money-check-alt"></i>
                        <span>Mes cotisations</span>
                    </a>
                </li>

                <li>
                    <a href="${ctx}/membre/payer" class="waves-effect ${activeMenu == 'payer' ? 'mm-active' : ''}">
                        <i class="fas fa-credit-card"></i>
                        <span>Effectuer un paiement</span>
                    </a>
                </li>

                <li>
                    <a href="${ctx}/membre/amendes" class="waves-effect ${activeMenu == 'amendes' ? 'mm-active' : ''}">
                        <i class="fas fa-gavel"></i>
                        <span>Mes amendes</span>
                    </a>
                </li>

                <li class="menu-title">Compte</li>

                <li>
                    <a href="${ctx}/profil" class="waves-effect ${activeMenu == 'profil' ? 'mm-active' : ''}">
                        <i class="fas fa-id-card"></i>
                        <span>Mon profil</span>
                    </a>
                </li>
                <li>
                    <a href="${ctx}/logout" class="waves-effect">
                        <i class="fas fa-sign-out-alt"></i>
                        <span>Déconnexion</span>
                    </a>
                </li>

            </ul>
        </div>
    </div>
</div>

<!-- ===== Début du contenu principal ===== -->
<div class="main-content">
    <div class="page-content">
        <div class="container-fluid">
