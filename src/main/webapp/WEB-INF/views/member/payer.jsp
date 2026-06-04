<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Payer une cotisation"/>
<c:set var="activeMenu" value="payer"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<jsp:include page="../layout/header.jsp"/>
<jsp:include page="../layout/sidebar-membre.jsp"/>

<div class="row">
    <div class="col-12">
        <div class="page-title-box d-sm-flex align-items-center justify-content-between">
            <h4 class="mb-sm-0 font-size-18">Payer ma cotisation</h4>
            <div class="page-title-right">
                <ol class="breadcrumb m-0">
                    <li class="breadcrumb-item"><a href="${ctx}/membre/dashboard">Accueil</a></li>
                    <li class="breadcrumb-item active">Paiement</li>
                </ol>
            </div>
        </div>
    </div>
</div>

<c:if test="${not empty erreur}">
    <div class="alert alert-danger alert-dismissible fade show" role="alert">
        <i class="fas fa-exclamation-circle me-1"></i> ${erreur}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
</c:if>

<div class="row">
    <div class="col-md-8">
        <div class="card">
            <div class="card-header bg-transparent border-bottom">
                <h4 class="card-title mb-0"><i class="fas fa-credit-card me-1"></i> Détails du paiement</h4>
            </div>

            <form method="post" action="${ctx}/membre/payer">
                <div class="card-body">

                    <div class="row">
                        <div class="col-md-4 mb-3">
                            <label class="form-label" for="mois">Mois <span class="text-danger">*</span></label>
                            <select id="mois" name="mois" class="form-select" required>
                                <c:forEach var="i" begin="1" end="12">
                                    <option value="${i}" ${i == moisDefaut ? 'selected' : ''}>${i}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-4 mb-3">
                            <label class="form-label" for="annee">Année <span class="text-danger">*</span></label>
                            <input type="number" id="annee" name="annee" class="form-control"
                                   min="2020" max="${anneeCourante}"
                                   value="${anneeDefaut}" required>
                        </div>
                        <div class="col-md-4 mb-3">
                            <label class="form-label" for="montant">Montant (FCFA) <span class="text-danger">*</span></label>
                            <input type="number" id="montant" name="montant" class="form-control"
                                   min="1" step="100" value="${montantParDefaut}" required>
                        </div>
                    </div>

                    <div class="mb-3">
                        <label class="form-label d-block">Mode de paiement <span class="text-danger">*</span></label>
                        <div class="row">
                            <c:forEach var="m" items="${modes}">
                                <div class="col-md-4 mb-2">
                                    <div class="form-check">
                                        <input type="radio" class="form-check-input" id="mode-${m}" name="modePaiement"
                                               value="${m}" ${m == 'WAVE' ? 'checked' : ''} required>
                                        <label class="form-check-label" for="mode-${m}">
                                            <c:choose>
                                                <c:when test="${m == 'WAVE'}"><i class="fas fa-mobile-alt text-primary me-1"></i></c:when>
                                                <c:when test="${m == 'ORANGE_MONEY'}"><i class="fas fa-mobile-alt text-warning me-1"></i></c:when>
                                                <c:when test="${m == 'FREE_MONEY'}"><i class="fas fa-mobile-alt text-danger me-1"></i></c:when>
                                                <c:when test="${m == 'VIREMENT'}"><i class="fas fa-university text-info me-1"></i></c:when>
                                                <c:otherwise><i class="fas fa-money-bill text-success me-1"></i></c:otherwise>
                                            </c:choose>
                                            ${m}
                                        </label>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </div>
                </div>

                <div class="card-footer bg-transparent border-top">
                    <button type="submit" class="btn btn-success btn-lg">
                        <i class="fas fa-check me-1"></i> Confirmer le paiement
                    </button>
                    <a href="${ctx}/membre/cotisations" class="btn btn-light">
                        <i class="fas fa-times me-1"></i> Annuler
                    </a>
                </div>
            <input type="hidden" name="_csrf" value="${csrfToken}"/>
</form>
        </div>
    </div>

    <div class="col-md-4">
        <div class="card">
            <div class="card-header bg-transparent border-bottom"><h4 class="card-title mb-0"><i class="fas fa-info-circle me-1"></i> Infos</h4></div>
            <div class="card-body">
                <p><strong>Membre :</strong> ${sessionScope.user.prenom} ${sessionScope.user.nom}</p>
                <p><strong>Email :</strong> ${sessionScope.user.email}</p>
                <p class="text-muted small mb-0">
                    Le montant par défaut est de ${montantParDefaut} FCFA. Un reçu vous sera affiché après confirmation.
                </p>
            </div>
        </div>

        <c:if test="${not empty moisDus}">
            <div class="card border border-danger">
                <div class="card-header bg-transparent border-bottom">
                    <h4 class="card-title mb-0 text-danger"><i class="fas fa-exclamation-triangle me-1"></i> Mois à régler</h4>
                </div>
                <div class="card-body p-0">
                    <ul class="list-group list-group-flush">
                        <c:forEach var="md" items="${moisDus}" varStatus="loop">
                            <li class="list-group-item d-flex justify-content-between align-items-center py-2">
                                <span>
                                    <c:if test="${loop.first}"><i class="fas fa-arrow-right text-danger me-1"></i></c:if>
                                    ${md.libelle}
                                </span>
                                <span class="badge bg-danger rounded-pill">${montantParDefaut}</span>
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

<jsp:include page="../layout/footer.jsp"/>
