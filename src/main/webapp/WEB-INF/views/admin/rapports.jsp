<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Statistiques & rapports"/>
<c:set var="activeMenu" value="rapports"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<jsp:include page="../layout/header.jsp"/>
<jsp:include page="../layout/sidebar-admin.jsp"/>

<div class="row">
    <div class="col-12">
        <div class="page-title-box d-sm-flex align-items-center justify-content-between">
            <h4 class="mb-sm-0 font-size-18">Statistiques &amp; rapports</h4>
            <div class="page-title-right">
                <ol class="breadcrumb m-0">
                    <li class="breadcrumb-item"><a href="${ctx}/admin/dashboard">Accueil</a></li>
                    <li class="breadcrumb-item active">Rapports</li>
                </ol>
            </div>
        </div>
    </div>
</div>

<%-- ===== Stats clés ===== --%>
<div class="row">
    <div class="col-md-3 col-sm-6">
        <div class="card mini-stats-wid">
            <div class="card-body">
                <div class="d-flex">
                    <div class="flex-grow-1">
                        <p class="text-muted fw-medium mb-2">Membres actifs</p>
                        <h4 class="mb-0">${stats.totalMembres}</h4>
                    </div>
                    <div class="avatar-sm align-self-center mini-stat-icon">
                        <span class="avatar-title rounded-circle bg-info"><i class="fas fa-users font-size-24"></i></span>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class="col-md-3 col-sm-6">
        <div class="card mini-stats-wid">
            <div class="card-body">
                <div class="d-flex">
                    <div class="flex-grow-1">
                        <p class="text-muted fw-medium mb-2">Cotisé sur 12 mois</p>
                        <h4 class="mb-0"><fmt:formatNumber value="${stats.totalCotise12Mois}"/> <small class="text-muted font-size-12">FCFA</small></h4>
                    </div>
                    <div class="avatar-sm align-self-center mini-stat-icon">
                        <span class="avatar-title rounded-circle bg-success"><i class="fas fa-money-bill-wave font-size-24"></i></span>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class="col-md-3 col-sm-6">
        <div class="card mini-stats-wid">
            <div class="card-body">
                <div class="d-flex">
                    <div class="flex-grow-1">
                        <p class="text-muted fw-medium mb-2">Membres à jour ce mois</p>
                        <h4 class="mb-0">${stats.ajourPourcent}%</h4>
                    </div>
                    <div class="avatar-sm align-self-center mini-stat-icon">
                        <span class="avatar-title rounded-circle bg-warning"><i class="fas fa-percentage font-size-24"></i></span>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class="col-md-3 col-sm-6">
        <div class="card mini-stats-wid">
            <div class="card-body">
                <div class="d-flex">
                    <div class="flex-grow-1">
                        <p class="text-muted fw-medium mb-2">Amendes impayées</p>
                        <h4 class="mb-0">${stats.nbAmendesImpayees}</h4>
                    </div>
                    <div class="avatar-sm align-self-center mini-stat-icon">
                        <span class="avatar-title rounded-circle bg-danger"><i class="fas fa-gavel font-size-24"></i></span>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<%-- ===== Graphes ===== --%>
<div class="row">
    <div class="col-md-8">
        <div class="card">
            <div class="card-body">
                <h4 class="card-title mb-4"><i class="fas fa-chart-line me-1"></i> Évolution des cotisations (12 derniers mois)</h4>
                <div style="position:relative; height:320px;">
                    <canvas id="evolutionChart"></canvas>
                </div>
            </div>
        </div>
    </div>

    <div class="col-md-4">
        <div class="card">
            <div class="card-body">
                <h4 class="card-title mb-4"><i class="fas fa-chart-pie me-1"></i> Modes de paiement</h4>
                <div style="position:relative; height:320px;">
                    <canvas id="modesChart"></canvas>
                </div>
            </div>
        </div>
    </div>
</div>

<%-- ===== Top retardataires ===== --%>
<div class="row">
    <div class="col-md-12">
        <div class="card">
            <div class="card-body">
                <h4 class="card-title mb-3"><i class="fas fa-exclamation-triangle me-1 text-danger"></i> Top 5 retardataires (montant dû)</h4>
                <div class="table-responsive">
                    <table class="table table-striped m-0">
                        <thead class="table-light">
                            <tr>
                                <th>#</th>
                                <th>Membre</th>
                                <th>Email</th>
                                <th class="text-center">Mois dus</th>
                                <th class="text-end">Montant dû</th>
                                <th class="text-center">Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="r" items="${topRetards}" varStatus="loop">
                                <tr>
                                    <td>${loop.index + 1}</td>
                                    <td>${r.membre.prenom} ${r.membre.nom}</td>
                                    <td>${r.membre.email}</td>
                                    <td class="text-center">
                                        <span class="badge bg-danger">${r.nbMoisDus}</span>
                                    </td>
                                    <td class="text-end">
                                        <strong><fmt:formatNumber value="${r.montantDu}"/> FCFA</strong>
                                    </td>
                                    <td class="text-center">
                                        <a href="${ctx}/admin/cotisations/nouveau?membre=${r.membre.numero}"
                                           class="btn btn-success btn-sm">
                                            <i class="fas fa-money-bill"></i> Enregistrer paiement
                                        </a>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty topRetards}">
                                <tr><td colspan="6" class="text-center text-muted p-3">
                                    <i class="fas fa-check-circle text-success me-1"></i>
                                    Excellent — aucun retard.
                                </td></tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../layout/footer.jsp"/>

<%-- ===== Données injectées pour Chart.js ===== --%>
<script>
    const evolutionLabels = [
        <c:forEach var="l" items="${evolution.labels}" varStatus="lp">'${l}'<c:if test="${!lp.last}">,</c:if></c:forEach>
    ];
    const evolutionData = [
        <c:forEach var="m" items="${evolution.montants}" varStatus="lp">${m}<c:if test="${!lp.last}">,</c:if></c:forEach>
    ];
    const modesLabels = [
        <c:forEach var="m" items="${modes}" varStatus="lp">'${m.label}'<c:if test="${!lp.last}">,</c:if></c:forEach>
    ];
    const modesData = [
        <c:forEach var="m" items="${modes}" varStatus="lp">${m.count}<c:if test="${!lp.last}">,</c:if></c:forEach>
    ];

    // ----- Line chart évolution -----
    const ctxEvo = document.getElementById('evolutionChart');
    if (ctxEvo) {
        new Chart(ctxEvo.getContext('2d'), {
            type: 'line',
            data: {
                labels: evolutionLabels,
                datasets: [{
                    label: 'Total cotisations (FCFA)',
                    data: evolutionData,
                    backgroundColor: 'rgba(52,195,143,0.15)',
                    borderColor: 'rgba(52,195,143,1)',
                    borderWidth: 2,
                    pointRadius: 4,
                    fill: true,
                    tension: 0.25
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: { y: { beginAtZero: true } }
            }
        });
    }

    // ----- Doughnut chart modes -----
    const ctxModes = document.getElementById('modesChart');
    if (ctxModes) {
        new Chart(ctxModes.getContext('2d'), {
            type: 'doughnut',
            data: {
                labels: modesLabels,
                datasets: [{
                    data: modesData,
                    backgroundColor: ['#50a5f1', '#34c38f', '#f1b44c', '#f46a6a', '#556ee6']
                }]
            },
            options: { responsive: true, maintainAspectRatio: false }
        });
    }
</script>
