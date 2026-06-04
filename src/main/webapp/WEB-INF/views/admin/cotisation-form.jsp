<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Nouvelle cotisation"/>
<c:set var="activeMenu" value="cotisations"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<jsp:include page="../layout/header.jsp"/>
<jsp:include page="../layout/sidebar-admin.jsp"/>

<div class="row">
    <div class="col-12">
        <div class="page-title-box d-sm-flex align-items-center justify-content-between">
            <h4 class="mb-sm-0 font-size-18">Enregistrer une cotisation</h4>
            <div class="page-title-right">
                <ol class="breadcrumb m-0">
                    <li class="breadcrumb-item"><a href="${ctx}/admin/cotisations">Cotisations</a></li>
                    <li class="breadcrumb-item active">Nouvelle</li>
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
    <div class="col-lg-8">
        <div class="card">
            <div class="card-header bg-transparent border-bottom">
                <h4 class="card-title mb-0"><i class="fas fa-money-bill-wave me-1"></i> Paiement</h4>
            </div>

            <form method="post" action="${ctx}/admin/cotisations/nouveau">
                <div class="card-body">

                    <div class="mb-3">
                        <label class="form-label" for="membre">Membre <span class="text-danger">*</span></label>
                        <select id="membre" name="membre" class="form-select" required>
                            <option value="">— Sélectionner —</option>
                            <c:forEach var="m" items="${membres}">
                                <c:if test="${m.statut == 'ACTIF'}">
                                    <option value="${m.numero}" ${param.membre == m.numero ? 'selected' : ''}>
                                        ${m.prenom} ${m.nom} — ${m.email}
                                    </option>
                                </c:if>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="row">
                        <div class="col-md-4 mb-3">
                            <label class="form-label" for="mois">Mois <span class="text-danger">*</span></label>
                            <select id="mois" name="mois" class="form-select" required>
                                <c:forEach var="i" begin="1" end="12">
                                    <option value="${i}" ${i == (param.mois != null ? param.mois : moisCourant) ? 'selected' : ''}>${i}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-4 mb-3">
                            <label class="form-label" for="annee">Année <span class="text-danger">*</span></label>
                            <input type="number" id="annee" name="annee" class="form-control"
                                   min="2020" max="${anneeCourante + 1}"
                                   value="${param.annee != null ? param.annee : anneeCourante}" required>
                        </div>
                        <div class="col-md-4 mb-3">
                            <label class="form-label" for="montant">Montant (FCFA) <span class="text-danger">*</span></label>
                            <input type="number" id="montant" name="montant" class="form-control"
                                   min="1" step="100"
                                   value="${param.montant != null ? param.montant : montantParDefaut}" required>
                        </div>
                    </div>

                    <div class="mb-3">
                        <label class="form-label d-block">Mode de paiement <span class="text-danger">*</span></label>
                        <c:forEach var="m" items="${modes}">
                            <div class="form-check form-check-inline">
                                <input type="radio" class="form-check-input" id="mode-${m}" name="modePaiement" value="${m}"
                                       ${(param.modePaiement != null ? param.modePaiement : 'ESPECES') == m ? 'checked' : ''} required>
                                <label class="form-check-label" for="mode-${m}">${m}</label>
                            </div>
                        </c:forEach>
                    </div>
                </div>

                <div class="card-footer bg-transparent border-top">
                    <button type="submit" class="btn btn-primary">
                        <i class="fas fa-save me-1"></i> Enregistrer
                    </button>
                    <a href="${ctx}/admin/cotisations" class="btn btn-light">
                        <i class="fas fa-times me-1"></i> Annuler
                    </a>
                </div>
            </form>
        </div>
    </div>
</div>

<jsp:include page="../layout/footer.jsp"/>
