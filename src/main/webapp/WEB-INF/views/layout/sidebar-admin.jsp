<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<!-- ===== Sidebar ===== -->
<div class="vertical-menu">
    <div data-simplebar class="h-100">
        <div id="sidebar-menu">
            <ul class="metismenu list-unstyled" id="side-menu">

                <li class="menu-title">Menu</li>

                <li>
                    <a href="${ctx}/admin/dashboard" class="waves-effect ${activeMenu == 'dashboard' ? 'mm-active' : ''}">
                        <i class="fas fa-tachometer-alt"></i>
                        <span>Tableau de bord</span>
                    </a>
                </li>

                <li class="menu-title">Gestion</li>

                <li>
                    <a href="javascript: void(0);" class="has-arrow waves-effect ${activeMenu == 'membres' ? 'mm-active' : ''}">
                        <i class="fas fa-users"></i>
                        <span>Membres</span>
                    </a>
                    <ul class="sub-menu ${activeMenu == 'membres' ? 'mm-show' : ''}" aria-expanded="${activeMenu == 'membres'}">
                        <li><a href="${ctx}/admin/membres">Liste des membres</a></li>
                        <li><a href="${ctx}/admin/membres/nouveau">Ajouter un membre</a></li>
                    </ul>
                </li>

                <li>
                    <a href="javascript: void(0);" class="has-arrow waves-effect ${activeMenu == 'cotisations' ? 'mm-active' : ''}">
                        <i class="fas fa-money-check-alt"></i>
                        <span>Cotisations</span>
                    </a>
                    <ul class="sub-menu ${activeMenu == 'cotisations' ? 'mm-show' : ''}" aria-expanded="${activeMenu == 'cotisations'}">
                        <li><a href="${ctx}/admin/cotisations">Toutes les cotisations</a></li>
                        <li><a href="${ctx}/admin/cotisations/retards">Membres en retard</a></li>
                    </ul>
                </li>

                <li>
                    <a href="${ctx}/admin/amendes" class="waves-effect ${activeMenu == 'amendes' ? 'mm-active' : ''}">
                        <i class="fas fa-gavel"></i>
                        <span>Amendes</span>
                    </a>
                </li>

                <li class="menu-title">Rapports</li>

                <li>
                    <a href="${ctx}/admin/rapports" class="waves-effect ${activeMenu == 'rapports' ? 'mm-active' : ''}">
                        <i class="fas fa-chart-bar"></i>
                        <span>Statistiques</span>
                    </a>
                </li>
                <li>
                    <a href="${ctx}/admin/exports" class="waves-effect ${activeMenu == 'exports' ? 'mm-active' : ''}">
                        <i class="fas fa-file-export"></i>
                        <span>Export PDF / Excel</span>
                    </a>
                </li>
                <li>
                    <a href="${ctx}/admin/connexions" class="waves-effect ${activeMenu == 'connexions' ? 'mm-active' : ''}">
                        <i class="fas fa-history"></i>
                        <span>Historique connexions</span>
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
