<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Tableau de bord administrateur"/>
<c:set var="activeMenu" value="dashboard"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<jsp:include page="../layout/header.jsp"/>
<jsp:include page="../layout/sidebar-admin.jsp"/>

<!-- En-tête de page -->
<div class="row">
    <div class="col-12">
        <div class="page-title-box d-sm-flex align-items-center justify-content-between">
            <h4 class="mb-sm-0 font-size-18">Tableau de bord</h4>
            <div class="page-title-right">
                <ol class="breadcrumb m-0">
                    <li class="breadcrumb-item"><a href="${ctx}/admin/dashboard">Accueil</a></li>
                    <li class="breadcrumb-item active">Tableau de bord</li>
                </ol>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../layout/flash-messages.jsp"/>

<!-- Cartes statistiques -->
<div class="row">
    <div class="col-md-6 col-xl-3">
        <div class="card mini-stats-wid">
            <div class="card-body">
                <div class="d-flex">
                    <div class="flex-grow-1">
                        <p class="text-muted fw-medium mb-2">Membres actifs</p>
                        <h4 class="mb-0">${stats.totalMembres != null ? stats.totalMembres : 0}</h4>
                    </div>
                    <div class="avatar-sm align-self-center mini-stat-icon">
                        <span class="avatar-title rounded-circle bg-info"><i class="fas fa-users font-size-24"></i></span>
                    </div>
                </div>
                <div class="mt-2"><a href="${ctx}/admin/membres" class="text-decoration-none">Voir la liste <i class="fas fa-arrow-circle-right"></i></a></div>
            </div>
        </div>
    </div>

    <div class="col-md-6 col-xl-3">
        <div class="card mini-stats-wid">
            <div class="card-body">
                <div class="d-flex">
                    <div class="flex-grow-1">
                        <p class="text-muted fw-medium mb-2">Cotisations du mois</p>
                        <h4 class="mb-0">
                            <fmt:formatNumber value="${stats.totalCotisationsMois != null ? stats.totalCotisationsMois : 0}" type="number"/>
                            <small class="text-muted font-size-12">FCFA</small>
                        </h4>
                    </div>
                    <div class="avatar-sm align-self-center mini-stat-icon">
                        <span class="avatar-title rounded-circle bg-success"><i class="fas fa-money-bill-wave font-size-24"></i></span>
                    </div>
                </div>
                <div class="mt-2"><a href="${ctx}/admin/cotisations" class="text-decoration-none">Détails <i class="fas fa-arrow-circle-right"></i></a></div>
            </div>
        </div>
    </div>

    <div class="col-md-6 col-xl-3">
        <div class="card mini-stats-wid">
            <div class="card-body">
                <div class="d-flex">
                    <div class="flex-grow-1">
                        <p class="text-muted fw-medium mb-2">Membres en retard</p>
                        <h4 class="mb-0">${stats.membresEnRetard != null ? stats.membresEnRetard : 0}</h4>
                    </div>
                    <div class="avatar-sm align-self-center mini-stat-icon">
                        <span class="avatar-title rounded-circle bg-warning"><i class="fas fa-exclamation-triangle font-size-24"></i></span>
                    </div>
                </div>
                <div class="mt-2"><a href="${ctx}/admin/cotisations/retards" class="text-decoration-none">Voir les retards <i class="fas fa-arrow-circle-right"></i></a></div>
            </div>
        </div>
    </div>

    <div class="col-md-6 col-xl-3">
        <div class="card mini-stats-wid">
            <div class="card-body">
                <div class="d-flex">
                    <div class="flex-grow-1">
                        <p class="text-muted fw-medium mb-2">Amendes impayées</p>
                        <h4 class="mb-0">${stats.totalAmendes != null ? stats.totalAmendes : 0}</h4>
                    </div>
                    <div class="avatar-sm align-self-center mini-stat-icon">
                        <span class="avatar-title rounded-circle bg-danger"><i class="fas fa-gavel font-size-24"></i></span>
                    </div>
                </div>
                <div class="mt-2"><a href="${ctx}/admin/amendes" class="text-decoration-none">Détails <i class="fas fa-arrow-circle-right"></i></a></div>
            </div>
        </div>
    </div>
</div>
<!-- /.row -->

<!-- Graphiques et derniers paiements -->
<div class="row">
    <div class="col-md-8">
        <div class="card">
            <div class="card-body">
                <h4 class="card-title mb-4"><i class="fas fa-chart-line me-1"></i> Évolution des cotisations (12 derniers mois)</h4>
                <div style="position:relative; height:300px;">
                    <canvas id="cotisationsChart"></canvas>
                </div>
            </div>
        </div>
    </div>

    <div class="col-md-4">
        <div class="card">
            <div class="card-body">
                <h4 class="card-title mb-4"><i class="fas fa-history me-1"></i> Derniers paiements</h4>
                <div class="list-group list-group-flush">
                    <c:forEach var="paiement" items="${derniersPaiements}">
                        <div class="list-group-item px-0">
                            <div class="d-flex justify-content-between">
                                <div>
                                    <h6 class="mb-1">${paiement.membre.prenom} ${paiement.membre.nom}</h6>
                                    <small class="text-muted">${paiement.mois}/${paiement.annee} — ${paiement.datePaiement}</small>
                                </div>
                                <span class="badge bg-success align-self-center">
                                    <fmt:formatNumber value="${paiement.montant}"/> FCFA
                                </span>
                            </div>
                        </div>
                    </c:forEach>
                    <c:if test="${empty derniersPaiements}">
                        <div class="text-muted text-center p-3">Aucun paiement récent.</div>
                    </c:if>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../layout/footer.jsp"/>

<script>
    const dashLabels = [
        <c:forEach var="l" items="${evolution.labels}" varStatus="lp">'${l}'<c:if test="${!lp.last}">,</c:if></c:forEach>
    ];
    const dashData = [
        <c:forEach var="m" items="${evolution.montants}" varStatus="lp">${m}<c:if test="${!lp.last}">,</c:if></c:forEach>
    ];
    const cotisationsCtx = document.getElementById('cotisationsChart');
    if (cotisationsCtx) {
        new Chart(cotisationsCtx.getContext('2d'), {
            type: 'line',
            data: {
                labels: dashLabels,
                datasets: [{
                    label: 'Total cotisations (FCFA)',
                    data: dashData,
                    backgroundColor: 'rgba(85,110,230,0.1)',
                    borderColor: 'rgba(85,110,230,1)',
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
