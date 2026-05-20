<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Cotisations"/>
<c:set var="activeMenu" value="cotisations"/>

<jsp:include page="../layout/header.jsp"/>
<jsp:include page="../layout/sidebar-admin.jsp"/>

<div class="content-wrapper">

    <section class="content-header">
        <div class="container-fluid">
            <div class="row mb-2">
                <div class="col-sm-6"><h1>Toutes les cotisations</h1></div>
                <div class="col-sm-6">
                    <ol class="breadcrumb float-sm-right">
                        <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/admin/dashboard">Accueil</a></li>
                        <li class="breadcrumb-item active">Cotisations</li>
                    </ol>
                </div>
            </div>
        </div>
    </section>

    <section class="content">
        <div class="container-fluid">

            <jsp:include page="../layout/flash-messages.jsp"/>

            <div class="card">
                <div class="card-header">
                    <h3 class="card-title"><i class="fas fa-money-check-alt mr-1"></i> Historique (${cotisations.size()} entrées)</h3>
                    <div class="card-tools">
                        <a href="${pageContext.request.contextPath}/admin/cotisations/nouveau" class="btn btn-primary btn-sm">
                            <i class="fas fa-plus"></i> Enregistrer une cotisation
                        </a>
                        <a href="${pageContext.request.contextPath}/admin/cotisations/retards" class="btn btn-warning btn-sm">
                            <i class="fas fa-exclamation-triangle"></i> Voir les retards
                        </a>
                    </div>
                </div>

                <div class="card-body">
                    <table class="table table-bordered table-striped datatable" style="width:100%">
                        <thead class="thead-light">
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
                                    <td><span class="badge badge-info">${c.modePaiement}</span></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${c.statut == 'PAYE'}"><span class="badge badge-success">Payé</span></c:when>
                                            <c:when test="${c.statut == 'EN_ATTENTE'}"><span class="badge badge-warning">En attente</span></c:when>
                                            <c:otherwise><span class="badge badge-secondary">${c.statut}</span></c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td class="text-center">
                                        <a href="${pageContext.request.contextPath}/admin/recu?id=${c.id}"
                                           target="_blank" class="btn btn-default btn-xs" title="Voir le reçu">
                                            <i class="fas fa-file-invoice"></i>
                                        </a>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty cotisations}">
                                <tr><td colspan="8" class="text-center text-muted">Aucune cotisation enregistrée.</td></tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </section>
</div>

<jsp:include page="../layout/footer.jsp"/>
