<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Nouvelle amende"/>
<c:set var="activeMenu" value="amendes"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<jsp:include page="../layout/header.jsp"/>
<jsp:include page="../layout/sidebar-admin.jsp"/>

<div class="row">
    <div class="col-12">
        <div class="page-title-box d-sm-flex align-items-center justify-content-between">
            <h4 class="mb-sm-0 font-size-18">Créer une amende manuelle</h4>
            <div class="page-title-right">
                <ol class="breadcrumb m-0">
                    <li class="breadcrumb-item"><a href="${ctx}/admin/amendes">Amendes</a></li>
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
    <div class="col-lg-6">
        <div class="card">
            <div class="card-header bg-transparent border-bottom">
                <h4 class="card-title mb-0"><i class="fas fa-gavel me-1"></i> Détails</h4>
            </div>
            <form method="post" action="${ctx}/admin/amendes/nouveau">
                <div class="card-body">
                    <div class="mb-3">
                        <label class="form-label" for="membre">Membre <span class="text-danger">*</span></label>
                        <select id="membre" name="membre" class="form-select" required>
                            <option value="">— Sélectionner —</option>
                            <c:forEach var="m" items="${membres}">
                                <c:if test="${m.statut == 'ACTIF' && m.role == 'MEMBRE'}">
                                    <option value="${m.numero}" ${param.membre == m.numero ? 'selected' : ''}>
                                        ${m.prenom} ${m.nom} — ${m.email}
                                    </option>
                                </c:if>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="mb-3">
                        <label class="form-label" for="montant">Montant (FCFA) <span class="text-danger">*</span></label>
                        <input type="number" id="montant" name="montant" class="form-control"
                               min="1" step="100"
                               value="${param.montant != null ? param.montant : montantStandard}" required>
                        <small class="form-text text-muted">Montant standard : ${montantStandard} FCFA.</small>
                    </div>
                </div>
                <div class="card-footer bg-transparent border-top">
                    <button type="submit" class="btn btn-primary">
                        <i class="fas fa-save me-1"></i> Créer l'amende
                    </button>
                    <a href="${ctx}/admin/amendes" class="btn btn-light">
                        <i class="fas fa-times me-1"></i> Annuler
                    </a>
                </div>
            </form>
        </div>
    </div>
</div>

<jsp:include page="../layout/footer.jsp"/>
