<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Paramètres"/>
<c:set var="activeMenu" value="parametres"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<jsp:include page="../layout/header.jsp"/>
<jsp:include page="../layout/sidebar-admin.jsp"/>

<div class="row">
    <div class="col-12">
        <div class="page-title-box d-sm-flex align-items-center justify-content-between">
            <h4 class="mb-sm-0 font-size-18">Paramètres de l'association</h4>
            <div class="page-title-right">
                <ol class="breadcrumb m-0">
                    <li class="breadcrumb-item"><a href="${ctx}/admin/dashboard">Accueil</a></li>
                    <li class="breadcrumb-item active">Paramètres</li>
                </ol>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../layout/flash-messages.jsp"/>

<c:if test="${not empty erreur}">
    <div class="alert alert-danger alert-dismissible fade show" role="alert">
        <i class="fas fa-exclamation-circle me-1"></i> ${erreur}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
</c:if>

<div class="row">
    <div class="col-md-8 col-lg-6">
        <div class="card">
            <div class="card-body">
                <h5 class="card-title mb-4">
                    <i class="fas fa-sliders-h text-primary me-1"></i> Montants standards
                </h5>

                <form method="post" action="${ctx}/admin/parametres">
                    <input type="hidden" name="_csrf" value="${csrfToken}"/>

                    <div class="mb-3">
                        <label class="form-label" for="montantCotisation">
                            Montant de la cotisation mensuelle (FCFA) <span class="text-danger">*</span>
                        </label>
                        <input type="number" id="montantCotisation" name="montantCotisation"
                               class="form-control" min="1" step="1"
                               value="${parametre.montantCotisation}" required>
                        <small class="form-text text-muted">
                            Sert de montant par défaut lors des paiements et au calcul du montant dû par les retardataires.
                        </small>
                    </div>

                    <div class="mb-3">
                        <label class="form-label" for="montantAmende">
                            Montant d'une amende de retard (FCFA) <span class="text-danger">*</span>
                        </label>
                        <input type="number" id="montantAmende" name="montantAmende"
                               class="form-control" min="0" step="1"
                               value="${parametre.montantAmende}" required>
                        <small class="form-text text-muted">
                            Appliqué lors de la génération automatique des amendes du mois.
                        </small>
                    </div>

                    <div class="d-grid d-sm-block mt-4">
                        <button type="submit" class="btn btn-primary waves-effect waves-light">
                            <i class="fas fa-save me-1"></i> Enregistrer
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../layout/footer.jsp"/>
