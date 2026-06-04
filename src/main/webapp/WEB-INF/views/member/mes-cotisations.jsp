<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Mes cotisations"/>
<c:set var="activeMenu" value="cotisations"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<jsp:include page="../layout/header.jsp"/>
<jsp:include page="../layout/sidebar-membre.jsp"/>

<div class="row">
    <div class="col-12">
        <div class="page-title-box d-sm-flex align-items-center justify-content-between">
            <h4 class="mb-sm-0 font-size-18">Mes cotisations</h4>
            <div class="page-title-right">
                <ol class="breadcrumb m-0">
                    <li class="breadcrumb-item"><a href="${ctx}/membre/dashboard">Accueil</a></li>
                    <li class="breadcrumb-item active">Mes cotisations</li>
                </ol>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../layout/flash-messages.jsp"/>

<%-- Bandeau mois dus --%>
<c:choose>
    <c:when test="${empty moisDus}">
        <div class="alert alert-success" role="alert">
            <i class="fas fa-check-circle me-1"></i>
            <strong>Bravo !</strong> Toutes vos cotisations sont à jour.
        </div>
    </c:when>
    <c:otherwise>
        <div class="card border border-danger">
            <div class="card-body py-3">
                <h5 class="mb-2">
                    <i class="fas fa-exclamation-triangle text-danger me-1"></i>
                    ${moisDus.size()} mois à régler —
                    Total : <strong><fmt:formatNumber value="${montantDu}"/> FCFA</strong>
                </h5>
                <div>
                    <c:forEach var="md" items="${moisDus}">
                        <span class="badge bg-danger me-1 mb-1" style="font-size:0.9em">${md.libelle}</span>
                    </c:forEach>
                </div>
                <a href="${ctx}/membre/payer" class="btn btn-danger mt-3">
                    <i class="fas fa-credit-card me-1"></i> Payer le plus ancien (${moisDus[0].libelle})
                </a>
            </div>
        </div>
    </c:otherwise>
</c:choose>

<div class="card">
    <div class="card-body">
        <div class="d-flex align-items-center justify-content-between mb-3">
            <h4 class="card-title mb-0"><i class="fas fa-money-check-alt me-1"></i> Historique (${cotisations.size()})</h4>
            <a href="${ctx}/membre/payer" class="btn btn-success btn-sm">
                <i class="fas fa-credit-card"></i> Payer une cotisation
            </a>
        </div>
        <table class="table table-bordered table-striped datatable" style="width:100%">
            <thead class="table-light">
                <tr>
                    <th>Mois</th>
                    <th>Année</th>
                    <th>Montant</th>
                    <th>Date paiement</th>
                    <th>Mode</th>
                    <th>Statut</th>
                    <th class="text-center">Reçu</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="c" items="${cotisations}">
                    <tr>
                        <td>${c.mois}</td>
                        <td>${c.annee}</td>
                        <td><fmt:formatNumber value="${c.montant}"/> FCFA</td>
                        <td>${c.datePaiement}</td>
                        <td><span class="badge bg-info">${c.modePaiement}</span></td>
                        <td>
                            <c:choose>
                                <c:when test="${c.statut == 'PAYE'}"><span class="badge bg-success">Payé</span></c:when>
                                <c:otherwise><span class="badge bg-warning">${c.statut}</span></c:otherwise>
                            </c:choose>
                        </td>
                        <td class="text-center">
                            <a href="${ctx}/membre/recu?id=${c.id}"
                               target="_blank" class="btn btn-light btn-sm">
                                <i class="fas fa-file-invoice"></i>
                            </a>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
</div>

<jsp:include page="../layout/footer.jsp"/>
