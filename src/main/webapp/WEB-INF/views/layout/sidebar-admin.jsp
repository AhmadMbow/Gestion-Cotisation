<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<aside class="main-sidebar sidebar-dark-primary elevation-4">
    <a href="${pageContext.request.contextPath}/admin/dashboard" class="brand-link">
        <i class="fas fa-hand-holding-usd brand-image ml-3" style="opacity:.9"></i>
        <span class="brand-text font-weight-light">Cotisations <b>Admin</b></span>
    </a>

    <div class="sidebar">
        <div class="user-panel mt-3 pb-3 mb-3 d-flex">
            <div class="image">
                <i class="fas fa-user-shield fa-2x text-white"></i>
            </div>
            <div class="info">
                <a href="#" class="d-block">${sessionScope.user.prenom} ${sessionScope.user.nom}</a>
                <small class="text-success"><i class="fas fa-circle text-success"></i> En ligne</small>
            </div>
        </div>

        <nav class="mt-2">
            <ul class="nav nav-pills nav-sidebar flex-column" data-widget="treeview" role="menu" data-accordion="false">

                <li class="nav-item">
                    <a href="${pageContext.request.contextPath}/admin/dashboard"
                       class="nav-link ${activeMenu == 'dashboard' ? 'active' : ''}">
                        <i class="nav-icon fas fa-tachometer-alt"></i>
                        <p>Tableau de bord</p>
                    </a>
                </li>

                <li class="nav-header">GESTION</li>

                <li class="nav-item ${activeMenu == 'membres' ? 'menu-open' : ''}">
                    <a href="#" class="nav-link ${activeMenu == 'membres' ? 'active' : ''}">
                        <i class="nav-icon fas fa-users"></i>
                        <p>Membres <i class="right fas fa-angle-left"></i></p>
                    </a>
                    <ul class="nav nav-treeview">
                        <li class="nav-item">
                            <a href="${pageContext.request.contextPath}/admin/membres" class="nav-link">
                                <i class="far fa-circle nav-icon"></i><p>Liste des membres</p>
                            </a>
                        </li>
                        <li class="nav-item">
                            <a href="${pageContext.request.contextPath}/admin/membres/nouveau" class="nav-link">
                                <i class="far fa-circle nav-icon"></i><p>Ajouter un membre</p>
                            </a>
                        </li>
                    </ul>
                </li>

                <li class="nav-item ${activeMenu == 'cotisations' ? 'menu-open' : ''}">
                    <a href="#" class="nav-link ${activeMenu == 'cotisations' ? 'active' : ''}">
                        <i class="nav-icon fas fa-money-check-alt"></i>
                        <p>Cotisations <i class="right fas fa-angle-left"></i></p>
                    </a>
                    <ul class="nav nav-treeview">
                        <li class="nav-item">
                            <a href="${pageContext.request.contextPath}/admin/cotisations" class="nav-link">
                                <i class="far fa-circle nav-icon"></i><p>Toutes les cotisations</p>
                            </a>
                        </li>
                        <li class="nav-item">
                            <a href="${pageContext.request.contextPath}/admin/cotisations/retards" class="nav-link">
                                <i class="far fa-circle nav-icon text-warning"></i><p>Membres en retard</p>
                            </a>
                        </li>
                    </ul>
                </li>

                <li class="nav-item">
                    <a href="${pageContext.request.contextPath}/admin/amendes"
                       class="nav-link ${activeMenu == 'amendes' ? 'active' : ''}">
                        <i class="nav-icon fas fa-gavel"></i>
                        <p>Amendes</p>
                    </a>
                </li>

                <li class="nav-header">RAPPORTS</li>

                <li class="nav-item">
                    <a href="${pageContext.request.contextPath}/admin/rapports"
                       class="nav-link ${activeMenu == 'rapports' ? 'active' : ''}">
                        <i class="nav-icon fas fa-chart-bar"></i>
                        <p>Statistiques</p>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="${pageContext.request.contextPath}/admin/exports"
                       class="nav-link ${activeMenu == 'exports' ? 'active' : ''}">
                        <i class="nav-icon fas fa-file-export"></i>
                        <p>Export PDF / Excel</p>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="${pageContext.request.contextPath}/admin/connexions"
                       class="nav-link ${activeMenu == 'connexions' ? 'active' : ''}">
                        <i class="nav-icon fas fa-history"></i>
                        <p>Historique connexions</p>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="${pageContext.request.contextPath}/admin/backup"
                       class="nav-link">
                        <i class="nav-icon fas fa-database"></i>
                        <p>Sauvegarde SQL</p>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="${pageContext.request.contextPath}/admin/parametres"
                       class="nav-link ${activeMenu == 'parametres' ? 'active' : ''}">
                        <i class="nav-icon fas fa-sliders-h"></i>
                        <p>Paramètres</p>
                    </a>
                </li>

                <li class="nav-header">COMPTE</li>
                <li class="nav-item">
                    <a href="${pageContext.request.contextPath}/profil"
                       class="nav-link ${activeMenu == 'profil' ? 'active' : ''}">
                        <i class="nav-icon fas fa-id-card"></i>
                        <p>Mon profil</p>
                    </a>
                </li>
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
