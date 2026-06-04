<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Cotisations"/>
<c:set var="activeMenu" value="cotisations"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<jsp:include page="../layout/header.jsp"/>
<jsp:include page="../layout/sidebar-admin.jsp"/>

<div class="row">
    <div class="col-12">
        <div class="page-title-box d-sm-flex align-items-center justify-content-between">
            <h4 class="mb-sm-0 font-size-18">Toutes les cotisations</h4>
            <div class="page-title-right">
                <ol class="breadcrumb m-0">
                    <li class="breadcrumb-item"><a href="${ctx}/admin/dashboard">Accueil</a></li>
                    <li class="breadcrumb-item active">Cotisations</li>
                </ol>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../layout/flash-messages.jsp"/>

<div class="card">
    <div class="card-body">
        <div class="d-flex align-items-center justify-content-between mb-3 flex-wrap gap-2">
            <h4 class="card-title mb-0"><i class="fas fa-money-check-alt me-1"></i> Historique (${cotisations.size()} entrées)</h4>
            <div>
                <a href="${ctx}/admin/cotisations/nouveau" class="btn btn-primary btn-sm">
                    <i class="fas fa-plus"></i> Enregistrer une cotisation
                </a>
                <a href="${ctx}/admin/cotisations/retards" class="btn btn-warning btn-sm">
                    <i class="fas fa-exclamation-triangle"></i> Voir les retards
                </a>
            </div>
        </div>

        <table class="table table-bordered table-striped datatable" style="width:100%">
            <thead class="table-light">
                <tr>
                    <th>#</th>
                    <th>Membre</th>
                    <th>Période</th>
                    <th>Montant</th>
                    <th>Date de paiement</th>
                    <th>Mode</th>
                    <th>Statut</th>
                    <th class="text-center">Reçu</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="c" items="${cotisations}">
                    <tr>
                        <td>${c.id}</td>
                        <td>${c.membre.prenom} ${c.membre.nom}</td>
                        <td>${c.mois}/${c.annee}</td>
                        <td><fmt:formatNumber value="${c.montant}" type="number"/> FCFA</td>
                        <td>${c.datePaiement}</td>
                        <td><span class="badge bg-info">${c.modePaiement}</span></td>
                        <td>
                            <c:choose>
                                <c:when test="${c.statut == 'PAYE'}"><span class="badge bg-success">Payé</span></c:when>
                                <c:when test="${c.statut == 'EN_ATTENTE'}"><span class="badge bg-warning">En attente</span></c:when>
                                <c:otherwise><span class="badge bg-secondary">${c.statut}</span></c:otherwise>
                            </c:choose>
                        </td>
                        <td class="text-center">
                            <a href="${ctx}/admin/recu?id=${c.id}"
                               target="_blank" class="btn btn-light btn-sm" title="Voir le reçu">
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
