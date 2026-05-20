<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Mes cotisations"/>
<c:set var="activeMenu" value="cotisations"/>

<jsp:include page="../layout/header.jsp"/>
<jsp:include page="../layout/sidebar-membre.jsp"/>

<div class="content-wrapper">

    <section class="content-header">
        <div class="container-fluid">
            <div class="row mb-2">
                <div class="col-sm-6"><h1>Mes cotisations</h1></div>
                <div class="col-sm-6">
                    <ol class="breadcrumb float-sm-right">
                        <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/membre/dashboard">Accueil</a></li>
                        <li class="breadcrumb-item active">Mes cotisations</li>
                    </ol>
                </div>
            </div>
        </div>
    </section>

    <section class="content">
        <div class="container-fluid">

            <jsp:include page="../layout/flash-messages.jsp"/>

            <%-- Bandeau mois dus --%>
            <c:choose>
                <c:when test="${empty moisDus}">
                    <div class="alert alert-success">
                        <i class="fas fa-check-circle mr-1"></i>
                        <strong>Bravo !</strong> Toutes vos cotisations sont à jour.
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="card card-danger card-outline">
                        <div class="card-body py-3">
                            <h5 class="mb-2">
                                <i class="fas fa-exclamation-triangle text-danger mr-1"></i>
                                ${moisDus.size()} mois à régler —
                                Total : <strong><fmt:formatNumber value="${montantDu}"/> FCFA</strong>
                            </h5>
                            <div>
                                <c:forEach var="md" items="${moisDus}">
                                    <span class="badge badge-danger mr-1 mb-1" style="font-size:0.9em">${md.libelle}</span>
                                </c:forEach>
                            </div>
                            <a href="${pageContext.request.contextPath}/membre/payer" class="btn btn-danger mt-3">
                                <i class="fas fa-credit-card mr-1"></i> Payer le plus ancien (${moisDus[0].libelle})
                            </a>
                        </div>
                    </div>
                </c:otherwise>
            </c:choose>

            <div class="card">
                <div class="card-header">
                    <h3 class="card-title"><i class="fas fa-money-check-alt mr-1"></i> Historique (${cotisations.size()})</h3>
                    <div class="card-tools">
                        <a href="${pageContext.request.contextPath}/membre/payer" class="btn btn-success btn-sm">
                            <i class="fas fa-credit-card"></i> Payer une cotisation
                        </a>
                    </div>
                </div>
                <div class="card-body">
                    <table class="table table-bordered table-striped datatable" style="width:100%">
                        <thead class="thead-light">
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
                                    <td><span class="badge badge-info">${c.modePaiement}</span></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${c.statut == 'PAYE'}"><span class="badge badge-success">Payé</span></c:when>
                                            <c:otherwise><span class="badge badge-warning">${c.statut}</span></c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td class="text-center">
                                        <a href="${pageContext.request.contextPath}/membre/recu?id=${c.id}"
                                           target="_blank" class="btn btn-default btn-xs">
                                            <i class="fas fa-file-invoice"></i>
                                        </a>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty cotisations}">
                                <tr><td colspan="7" class="text-center text-muted">Aucune cotisation enregistrée.</td></tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </section>
</div>

<jsp:include page="../layout/footer.jsp"/>
