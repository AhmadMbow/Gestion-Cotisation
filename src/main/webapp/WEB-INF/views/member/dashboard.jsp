<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Mon tableau de bord"/>
<c:set var="activeMenu" value="dashboard"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<jsp:include page="../layout/header.jsp"/>
<jsp:include page="../layout/sidebar-membre.jsp"/>

<div class="row">
    <div class="col-12">
        <div class="page-title-box d-sm-flex align-items-center justify-content-between">
            <h4 class="mb-sm-0 font-size-18">Bienvenue, ${sessionScope.user.prenom}</h4>
            <div class="page-title-right">
                <ol class="breadcrumb m-0">
                    <li class="breadcrumb-item"><a href="${ctx}/">Accueil</a></li>
                    <li class="breadcrumb-item active">Mon espace</li>
                </ol>
            </div>
        </div>
    </div>
</div>

<%-- Alerte prioritaire si des mois sont dus --%>
<c:if test="${not empty moisDus}">
    <div class="alert alert-danger" role="alert">
        <div class="row align-items-center">
            <div class="col-md-8">
                <h5 class="mb-1">
                    <i class="fas fa-exclamation-triangle me-1"></i>
                    Vous devez <strong>${moisDus.size()} mois</strong> de cotisation —
                    Total : <strong><fmt:formatNumber value="${stats.montantDu}"/> FCFA</strong>
                </h5>
                <p class="mb-0 small text-muted">
                    Mois dus :
                    <c:forEach var="md" items="${moisDus}" varStatus="loop">
                        <span class="badge bg-light text-dark">${md.libelle}</span><c:if test="${!loop.last}"> </c:if>
                    </c:forEach>
                </p>
            </div>
            <div class="col-md-4 text-md-end mt-2 mt-md-0">
                <a href="${ctx}/membre/payer" class="btn btn-danger">
                    <i class="fas fa-credit-card me-1"></i> Payer maintenant
                </a>
            </div>
        </div>
    </div>
</c:if>

<div class="row">
    <div class="col-lg-4 col-md-6">
        <c:choose>
            <c:when test="${stats.cotisationsAjour}">
                <div class="card mini-stats-wid">
                    <div class="card-body">
                        <div class="d-flex">
                            <div class="flex-grow-1">
                                <p class="text-muted fw-medium mb-2">Statut cotisations</p>
                                <h4 class="mb-0 text-success">À jour</h4>
                            </div>
                            <div class="avatar-sm align-self-center mini-stat-icon">
                                <span class="avatar-title rounded-circle bg-success"><i class="fas fa-check-circle font-size-24"></i></span>
                            </div>
                        </div>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <div class="card mini-stats-wid">
                    <div class="card-body">
                        <div class="d-flex">
                            <div class="flex-grow-1">
                                <p class="text-muted fw-medium mb-2">Mois en retard</p>
                                <h4 class="mb-0 text-danger">${stats.nbMoisDus}</h4>
                            </div>
                            <div class="avatar-sm align-self-center mini-stat-icon">
                                <span class="avatar-title rounded-circle bg-danger"><i class="fas fa-exclamation-triangle font-size-24"></i></span>
                            </div>
                        </div>
                        <div class="mt-2"><a href="${ctx}/membre/payer" class="text-decoration-none">Payer maintenant <i class="fas fa-arrow-circle-right"></i></a></div>
                    </div>
                </div>
            </c:otherwise>
        </c:choose>
    </div>

    <div class="col-lg-4 col-md-6">
        <div class="card mini-stats-wid">
            <div class="card-body">
                <div class="d-flex">
                    <div class="flex-grow-1">
                        <p class="text-muted fw-medium mb-2">Total cotisé cette année</p>
                        <h4 class="mb-0"><fmt:formatNumber value="${stats.totalPaye != null ? stats.totalPaye : 0}"/> <small class="text-muted font-size-12">FCFA</small></h4>
                    </div>
                    <div class="avatar-sm align-self-center mini-stat-icon">
                        <span class="avatar-title rounded-circle bg-info"><i class="fas fa-piggy-bank font-size-24"></i></span>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="col-lg-4 col-md-6">
        <div class="card mini-stats-wid">
            <div class="card-body">
                <div class="d-flex">
                    <div class="flex-grow-1">
                        <p class="text-muted fw-medium mb-2">Amendes à régler</p>
                        <h4 class="mb-0"><fmt:formatNumber value="${stats.totalAmendes != null ? stats.totalAmendes : 0}"/> <small class="text-muted font-size-12">FCFA</small></h4>
                    </div>
                    <div class="avatar-sm align-self-center mini-stat-icon">
                        <span class="avatar-title rounded-circle bg-warning"><i class="fas fa-gavel font-size-24"></i></span>
                    </div>
                </div>
                <div class="mt-2"><a href="${ctx}/membre/amendes" class="text-decoration-none">Voir mes amendes <i class="fas fa-arrow-circle-right"></i></a></div>
            </div>
        </div>
    </div>
</div>

<div class="row">
    <div class="col-md-12">
        <div class="card">
            <div class="card-body">
                <div class="d-flex align-items-center justify-content-between mb-3">
                    <h4 class="card-title mb-0"><i class="fas fa-list me-1"></i> Mes 10 dernières cotisations</h4>
                    <a href="${ctx}/membre/payer" class="btn btn-success btn-sm">
                        <i class="fas fa-credit-card"></i> Payer une cotisation
                    </a>
                </div>
                <div class="table-responsive">
                    <table class="table table-striped mb-0">
                        <thead>
                            <tr>
                                <th>Mois</th>
                                <th>Année</th>
                                <th>Montant</th>
                                <th>Date</th>
                                <th>Mode</th>
                                <th>Statut</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="cot" items="${cotisations}">
                                <tr>
                                    <td>${cot.mois}</td>
                                    <td>${cot.annee}</td>
                                    <td><fmt:formatNumber value="${cot.montant}"/> FCFA</td>
                                    <td>${cot.datePaiement}</td>
                                    <td>${cot.modePaiement}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${cot.statut == 'PAYE'}">
                                                <span class="badge bg-success">Payé</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-warning">${cot.statut}</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty cotisations}">
                                <tr><td colspan="6" class="text-center text-muted">Aucune cotisation enregistrée.</td></tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../layout/footer.jsp"/>
