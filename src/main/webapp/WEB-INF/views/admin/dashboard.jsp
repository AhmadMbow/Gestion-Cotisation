<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Tableau de bord administrateur"/>
<c:set var="activeMenu" value="dashboard"/>

<jsp:include page="../layout/header.jsp"/>
<jsp:include page="../layout/sidebar-admin.jsp"/>

<div class="content-wrapper">

    <!-- En-tête de page -->
    <section class="content-header">
        <div class="container-fluid">
            <div class="row mb-2">
                <div class="col-sm-6">
                    <h1>Tableau de bord</h1>
                </div>
                <div class="col-sm-6">
                    <ol class="breadcrumb float-sm-right">
                        <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/admin/dashboard">Accueil</a></li>
                        <li class="breadcrumb-item active">Tableau de bord</li>
                    </ol>
                </div>
            </div>
        </div>
    </section>

    <section class="content">
        <div class="container-fluid">

            <jsp:include page="../layout/flash-messages.jsp"/>

            <!-- Cartes statistiques -->
            <div class="row">
                <div class="col-lg-3 col-6">
                    <div class="small-box bg-info">
                        <div class="inner">
                            <h3>${stats.totalMembres != null ? stats.totalMembres : 0}</h3>
                            <p>Membres actifs</p>
                        </div>
                        <div class="icon"><i class="fas fa-users"></i></div>
                        <a href="${pageContext.request.contextPath}/admin/membres" class="small-box-footer">
                            Voir la liste <i class="fas fa-arrow-circle-right"></i>
                        </a>
                    </div>
                </div>

                <div class="col-lg-3 col-6">
                    <div class="small-box bg-success">
                        <div class="inner">
                            <h3>
                                <fmt:formatNumber value="${stats.totalCotisationsMois != null ? stats.totalCotisationsMois : 0}" type="number"/>
                                <sup style="font-size: 20px">FCFA</sup>
                            </h3>
                            <p>Cotisations du mois</p>
                        </div>
                        <div class="icon"><i class="fas fa-money-bill-wave"></i></div>
                        <a href="${pageContext.request.contextPath}/admin/cotisations" class="small-box-footer">
                            Détails <i class="fas fa-arrow-circle-right"></i>
                        </a>
                    </div>
                </div>

                <div class="col-lg-3 col-6">
                    <div class="small-box bg-warning">
                        <div class="inner">
                            <h3>${stats.membresEnRetard != null ? stats.membresEnRetard : 0}</h3>
                            <p>Membres en retard</p>
                        </div>
                        <div class="icon"><i class="fas fa-exclamation-triangle"></i></div>
                        <a href="${pageContext.request.contextPath}/admin/cotisations/retards" class="small-box-footer">
                            Voir les retards <i class="fas fa-arrow-circle-right"></i>
                        </a>
                    </div>
                </div>

                <div class="col-lg-3 col-6">
                    <div class="small-box bg-danger">
                        <div class="inner">
                            <h3>${stats.totalAmendes != null ? stats.totalAmendes : 0}</h3>
                            <p>Amendes impayées</p>
                        </div>
                        <div class="icon"><i class="fas fa-gavel"></i></div>
                        <a href="${pageContext.request.contextPath}/admin/amendes" class="small-box-footer">
                            Détails <i class="fas fa-arrow-circle-right"></i>
                        </a>
                    </div>
                </div>
            </div>
            <!-- /.row -->

            <!-- Graphiques et derniers paiements -->
            <div class="row">
                <div class="col-md-8">
                    <div class="card">
                        <div class="card-header">
                            <h3 class="card-title"><i class="fas fa-chart-line mr-1"></i> Évolution des cotisations (12 derniers mois)</h3>
                        </div>
                        <div class="card-body">
                            <canvas id="cotisationsChart" style="height:300px;"></canvas>
                        </div>
                    </div>
                </div>

                <div class="col-md-4">
                    <div class="card">
                        <div class="card-header">
                            <h3 class="card-title"><i class="fas fa-history mr-1"></i> Derniers paiements</h3>
                        </div>
                        <div class="card-body p-0">
                            <ul class="products-list product-list-in-card pl-2 pr-2">
                                <c:forEach var="paiement" items="${derniersPaiements}">
                                    <li class="item">
                                        <div class="product-info">
                                            <a href="#" class="product-title">
                                                ${paiement.membre.prenom} ${paiement.membre.nom}
                                                <span class="badge badge-success float-right">
                                                    <fmt:formatNumber value="${paiement.montant}"/> FCFA
                                                </span>
                                            </a>
                                            <span class="product-description">
                                                ${paiement.mois}/${paiement.annee} — ${paiement.datePaiement}
                                            </span>
                                        </div>
                                    </li>
                                </c:forEach>
                                <c:if test="${empty derniersPaiements}">
                                    <li class="item text-muted text-center p-3">Aucun paiement récent.</li>
                                </c:if>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>

        </div>
    </section>
</div>

<jsp:include page="../layout/footer.jsp"/>

<script>
    const dashLabels = [
        <c:forEach var="l" items="${evolution.labels}" varStatus="lp">'${l}'<c:if test="${!lp.last}">,</c:if></c:forEach>
    ];
    const dashData = [
        <c:forEach var="m" items="${evolution.montants}" varStatus="lp">${m}<c:if test="${!lp.last}">,</c:if></c:forEach>
    ];
    const ctx = document.getElementById('cotisationsChart');
    if (ctx) {
        new Chart(ctx.getContext('2d'), {
            type: 'line',
            data: {
                labels: dashLabels,
                datasets: [{
                    label: 'Total cotisations (FCFA)',
                    data: dashData,
                    backgroundColor: 'rgba(60,141,188,0.1)',
                    borderColor: 'rgba(60,141,188,1)',
                    borderWidth: 2,
                    pointRadius: 3,
                    fill: true,
                    tension: 0.25
                }]
            },
            options: { responsive: true, maintainAspectRatio: false }
        });
    }
</script>
