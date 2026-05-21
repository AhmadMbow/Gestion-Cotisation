<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Payer une cotisation"/>
<c:set var="activeMenu" value="payer"/>

<jsp:include page="../layout/header.jsp"/>
<jsp:include page="../layout/sidebar-membre.jsp"/>

<div class="content-wrapper">

    <section class="content-header">
        <div class="container-fluid">
            <div class="row mb-2">
                <div class="col-sm-6"><h1>Payer ma cotisation</h1></div>
                <div class="col-sm-6">
                    <ol class="breadcrumb float-sm-right">
                        <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/membre/dashboard">Accueil</a></li>
                        <li class="breadcrumb-item active">Paiement</li>
                    </ol>
                </div>
            </div>
        </div>
    </section>

    <section class="content">
        <div class="container-fluid">

            <c:if test="${not empty erreur}">
                <div class="alert alert-danger alert-dismissible">
                    <button type="button" class="close" data-dismiss="alert">&times;</button>
                    <i class="fas fa-exclamation-circle mr-1"></i> ${erreur}
                </div>
            </c:if>

            <div class="row">
                <div class="col-md-8">
                    <div class="card card-success">
                        <div class="card-header">
                            <h3 class="card-title"><i class="fas fa-credit-card mr-1"></i> Détails du paiement</h3>
                        </div>

                        <form method="post" action="${pageContext.request.contextPath}/membre/payer">
                            <input type="hidden" name="_csrf" value="${csrfToken}">
                            <div class="card-body">

                                <div class="form-row">
                                    <div class="form-group col-md-4">
                                        <label for="mois">Mois <span class="text-danger">*</span></label>
                                        <select id="mois" name="mois" class="form-control" required>
                                            <c:forEach var="i" begin="1" end="12">
                                                <option value="${i}" ${i == moisDefaut ? 'selected' : ''}>${i}</option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                    <div class="form-group col-md-4">
                                        <label for="annee">Année <span class="text-danger">*</span></label>
                                        <input type="number" id="annee" name="annee" class="form-control"
                                               min="2020" max="${anneeCourante}"
                                               value="${anneeDefaut}" required>
                                    </div>
                                    <div class="form-group col-md-4">
                                        <label for="montant">Montant (FCFA) <span class="text-danger">*</span></label>
                                        <input type="number" id="montant" name="montant" class="form-control"
                                               min="1" step="100" value="${montantParDefaut}" required>
                                    </div>
                                </div>

                                <div class="form-group">
                                    <label>Mode de paiement <span class="text-danger">*</span></label>
                                    <div class="row">
                                        <c:forEach var="m" items="${modes}">
                                            <div class="col-md-4 mb-2">
                                                <div class="icheck-success">
                                                    <input type="radio" id="mode-${m}" name="modePaiement"
                                                           value="${m}" ${m == 'WAVE' ? 'checked' : ''} required>
                                                    <label for="mode-${m}">
                                                        <c:choose>
                                                            <c:when test="${m == 'WAVE'}"><i class="fas fa-mobile-alt text-primary mr-1"></i></c:when>
                                                            <c:when test="${m == 'ORANGE_MONEY'}"><i class="fas fa-mobile-alt text-warning mr-1"></i></c:when>
                                                            <c:when test="${m == 'FREE_MONEY'}"><i class="fas fa-mobile-alt text-danger mr-1"></i></c:when>
                                                            <c:when test="${m == 'VIREMENT'}"><i class="fas fa-university text-info mr-1"></i></c:when>
                                                            <c:otherwise><i class="fas fa-money-bill text-success mr-1"></i></c:otherwise>
                                                        </c:choose>
                                                        ${m}
                                                    </label>
                                                </div>
                                            </div>
                                        </c:forEach>
                                    </div>
                                </div>
                            </div>

                            <div class="card-footer">
                                <button type="submit" class="btn btn-success btn-lg">
                                    <i class="fas fa-check mr-1"></i> Confirmer le paiement
                                </button>
                                <a href="${pageContext.request.contextPath}/membre/cotisations" class="btn btn-default">
                                    <i class="fas fa-times mr-1"></i> Annuler
                                </a>
                            </div>
                        </form>
                    </div>
                </div>

                <div class="col-md-4">
                    <div class="card card-info">
                        <div class="card-header"><h3 class="card-title"><i class="fas fa-info-circle mr-1"></i> Infos</h3></div>
                        <div class="card-body">
                            <p><strong>Membre :</strong> ${sessionScope.user.prenom} ${sessionScope.user.nom}</p>
                            <p><strong>Email :</strong> ${sessionScope.user.email}</p>
                            <p class="text-muted small mb-0">
                                Le montant par défaut est de ${montantParDefaut} FCFA. Un reçu vous sera affiché après confirmation.
                            </p>
                        </div>
                    </div>

                    <c:if test="${not empty moisDus}">
                        <div class="card card-danger">
                            <div class="card-header">
                                <h3 class="card-title"><i class="fas fa-exclamation-triangle mr-1"></i> Mois à régler</h3>
                            </div>
                            <div class="card-body p-0">
                                <ul class="list-group list-group-flush">
                                    <c:forEach var="md" items="${moisDus}" varStatus="loop">
                                        <li class="list-group-item d-flex justify-content-between align-items-center py-2">
                                            <span>
                                                <c:if test="${loop.first}"><i class="fas fa-arrow-right text-danger mr-1"></i></c:if>
                                                ${md.libelle}
                                            </span>
                                            <span class="badge badge-danger badge-pill">${montantParDefaut}</span>
                                        </li>
                                    </c:forEach>
                                </ul>
                            </div>
                            <div class="card-footer text-muted small text-center py-2">
                                Le plus ancien est pré-sélectionné dans le formulaire.
                            </div>
                        </div>
                    </c:if>
                </div>
            </div>
        </div>
    </section>
</div>

<jsp:include page="../layout/footer.jsp"/>
