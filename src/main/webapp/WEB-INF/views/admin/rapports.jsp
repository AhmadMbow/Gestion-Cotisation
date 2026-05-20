<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Statistiques & rapports"/>
<c:set var="activeMenu" value="rapports"/>

<jsp:include page="../layout/header.jsp"/>
<jsp:include page="../layout/sidebar-admin.jsp"/>

<div class="content-wrapper">

    <section class="content-header">
        <div class="container-fluid">
            <div class="row mb-2">
                <div class="col-sm-6"><h1>Statistiques & rapports</h1></div>
                <div class="col-sm-6">
                    <ol class="breadcrumb float-sm-right">
                        <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/admin/dashboard">Accueil</a></li>
                        <li class="breadcrumb-item active">Rapports</li>
                    </ol>
                </div>
            </div>
        </div>
    </section>

    <section class="content">
        <div class="container-fluid">

            <%-- ===== Stats clés ===== --%>
            <div class="row">
                <div class="col-md-3 col-sm-6">
                    <div class="info-box">
                        <span class="info-box-icon bg-info"><i class="fas fa-users"></i></span>
                        <div class="info-box-content">
                            <span class="info-box-text">Membres actifs</span>
                            <span class="info-box-number">${stats.totalMembres}</span>
                        </div>
                    </div>
                </div>
                <div class="col-md-3 col-sm-6">
                    <div class="info-box">
                        <span class="info-box-icon bg-success"><i class="fas fa-money-bill-wave"></i></span>
                        <div class="info-box-content">
                            <span class="info-box-text">Cotisé sur 12 mois</span>
                            <span class="info-box-number">
                                <fmt:formatNumber value="${stats.totalCotise12Mois}"/> <small>FCFA</small>
                            </span>
                        </div>
                    </div>
                </div>
                <div class="col-md-3 col-sm-6">
                    <div class="info-box">
                        <span class="info-box-icon bg-warning"><i class="fas fa-percentage"></i></span>
                        <div class="info-box-content">
                            <span class="info-box-text">Membres à jour ce mois</span>
                            <span class="info-box-number">${stats.ajourPourcent}%</span>
                        </div>
                    </div>
                </div>
                <div class="col-md-3 col-sm-6">
                    <div class="info-box">
                        <span class="info-box-icon bg-danger"><i class="fas fa-gavel"></i></span>
                        <div class="info-box-content">
                            <span class="info-box-text">Amendes impayées</span>
                            <span class="info-box-number">${stats.nbAmendesImpayees}</span>
                        </div>
                    </div>
                </div>
            </div>

            <%-- ===== Graphes ===== --%>
            <div class="row">
                <div class="col-md-8">
                    <div class="card card-info">
                        <div class="card-header">
                            <h3 class="card-title"><i class="fas fa-chart-line mr-1"></i> Évolution des cotisations (12 derniers mois)</h3>
                        </div>
                        <div class="card-body">
                            <canvas id="evolutionChart" style="height:320px;"></canvas>
                        </div>
                    </div>
                </div>

                <div class="col-md-4">
                    <div class="card card-success">
                        <div class="card-header">
                            <h3 class="card-title"><i class="fas fa-chart-pie mr-1"></i> Modes de paiement</h3>
                        </div>
                        <div class="card-body">
                            <canvas id="modesChart" style="height:320px;"></canvas>
                        </div>
                    </div>
                </div>
            </div>

            <%-- ===== Top retardataires ===== --%>
            <div class="row">
                <div class="col-md-12">
                    <div class="card card-danger">
                        <div class="card-header">
                            <h3 class="card-title"><i class="fas fa-exclamation-triangle mr-1"></i> Top 5 retardataires (montant dû)</h3>
                        </div>
                        <div class="card-body p-0">
                            <table class="table table-striped m-0">
                                <thead class="thead-light">
                                    <tr>
                                        <th>#</th>
                                        <th>Membre</th>
                                        <th>Email</th>
                                        <th class="text-center">Mois dus</th>
                                        <th class="text-right">Montant dû</th>
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
                                                <span class="badge badge-danger">${r.nbMoisDus}</span>
                                            </td>
                                            <td class="text-right">
                                                <strong><fmt:formatNumber value="${r.montantDu}"/> FCFA</strong>
                                            </td>
                                            <td class="text-center">
                                                <a href="${pageContext.request.contextPath}/admin/cotisations/nouveau?membre=${r.membre.numero}"
                                                   class="btn btn-success btn-xs">
                                                    <i class="fas fa-money-bill"></i> Enregistrer paiement
                                                </a>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                    <c:if test="${empty topRetards}">
                                        <tr><td colspan="6" class="text-center text-muted p-3">
                                            <i class="fas fa-check-circle text-success mr-1"></i>
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
    </section>
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
                    backgroundColor: 'rgba(40,167,69,0.15)',
                    borderColor: 'rgba(40,167,69,1)',
                    borderWidth: 2,
                    pointRadius: 4,
                    fill: true,
                    tension: 0.25
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: { yAxes: [{ ticks: { beginAtZero: true } }] }
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
                    backgroundColor: ['#17a2b8', '#28a745', '#ffc107', '#dc3545', '#6610f2']
                }]
            },
            options: { responsive: true, maintainAspectRatio: false }
        });
    }
</script>
