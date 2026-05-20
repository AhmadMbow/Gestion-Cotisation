<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Mon tableau de bord"/>
<c:set var="activeMenu" value="dashboard"/>

<jsp:include page="../layout/header.jsp"/>
<jsp:include page="../layout/sidebar-membre.jsp"/>

<div class="content-wrapper">

    <section class="content-header">
        <div class="container-fluid">
            <div class="row mb-2">
                <div class="col-sm-6">
                    <h1>Bienvenue, ${sessionScope.user.prenom}</h1>
                </div>
                <div class="col-sm-6">
                    <ol class="breadcrumb float-sm-right">
                        <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/">Accueil</a></li>
                        <li class="breadcrumb-item active">Mon espace</li>
                    </ol>
                </div>
            </div>
        </div>
    </section>

    <section class="content">
        <div class="container-fluid">

            <%-- Alerte prioritaire si des mois sont dus --%>
            <c:if test="${not empty moisDus}">
                <div class="callout callout-danger">
                    <div class="row align-items-center">
                        <div class="col-md-8">
                            <h5 class="mb-1">
                                <i class="fas fa-exclamation-triangle mr-1"></i>
                                Vous devez <strong>${moisDus.size()} mois</strong> de cotisation —
                                Total : <strong><fmt:formatNumber value="${stats.montantDu}"/> FCFA</strong>
                            </h5>
                            <p class="mb-0 small text-muted">
                                Mois dus :
                                <c:forEach var="md" items="${moisDus}" varStatus="loop">
                                    <span class="badge badge-light">${md.libelle}</span><c:if test="${!loop.last}"> </c:if>
                                </c:forEach>
                            </p>
                        </div>
                        <div class="col-md-4 text-md-right mt-2 mt-md-0">
                            <a href="${pageContext.request.contextPath}/membre/payer"
                               class="btn btn-danger">
                                <i class="fas fa-credit-card mr-1"></i> Payer maintenant
                            </a>
                        </div>
                    </div>
                </div>
            </c:if>

            <div class="row">
                <div class="col-lg-4 col-md-6">
                    <c:choose>
                        <c:when test="${stats.cotisationsAjour}">
                            <div class="small-box bg-success">
                                <div class="inner">
                                    <h3>À jour</h3>
                                    <p>Statut cotisations</p>
                                </div>
                                <div class="icon"><i class="fas fa-check-circle"></i></div>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="small-box bg-danger">
                                <div class="inner">
                                    <h3>${stats.nbMoisDus}</h3>
                                    <p>Mois en retard</p>
                                </div>
                                <div class="icon"><i class="fas fa-exclamation-triangle"></i></div>
                                <a href="${pageContext.request.contextPath}/membre/payer" class="small-box-footer">
                                    Payer maintenant <i class="fas fa-arrow-circle-right"></i>
                                </a>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>

                <div class="col-lg-4 col-md-6">
                    <div class="small-box bg-info">
                        <div class="inner">
                            <h3>
                                <fmt:formatNumber value="${stats.totalPaye != null ? stats.totalPaye : 0}"/>
                                <sup style="font-size: 20px">FCFA</sup>
                            </h3>
                            <p>Total cotisé cette année</p>
                        </div>
                        <div class="icon"><i class="fas fa-piggy-bank"></i></div>
                    </div>
                </div>

                <div class="col-lg-4 col-md-6">
                    <div class="small-box bg-warning">
                        <div class="inner">
                            <h3>
                                <fmt:formatNumber value="${stats.totalAmendes != null ? stats.totalAmendes : 0}"/>
                                <sup style="font-size: 20px">FCFA</sup>
                            </h3>
                            <p>Amendes à régler</p>
                        </div>
                        <div class="icon"><i class="fas fa-gavel"></i></div>
                        <a href="${pageContext.request.contextPath}/membre/amendes" class="small-box-footer">
                            Voir mes amendes <i class="fas fa-arrow-circle-right"></i>
                        </a>
                    </div>
                </div>
            </div>

            <div class="row">
                <div class="col-md-12">
                    <div class="card">
                        <div class="card-header">
                            <h3 class="card-title"><i class="fas fa-list mr-1"></i> Mes 10 dernières cotisations</h3>
                            <div class="card-tools">
                                <a href="${pageContext.request.contextPath}/membre/payer" class="btn btn-success btn-sm">
                                    <i class="fas fa-credit-card"></i> Payer une cotisation
                                </a>
                            </div>
                        </div>
                        <div class="card-body p-0">
                            <table class="table table-striped">
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
                                                        <span class="badge badge-success">Payé</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge badge-warning">${cot.statut}</span>
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
    </section>
</div>

<jsp:include page="../layout/footer.jsp"/>
