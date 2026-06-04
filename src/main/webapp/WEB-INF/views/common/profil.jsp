<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Mon profil"/>
<c:set var="activeMenu" value="profil"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<jsp:include page="../layout/header.jsp"/>

<c:choose>
    <c:when test="${sessionScope.user.role == 'ADMIN'}">
        <jsp:include page="../layout/sidebar-admin.jsp"/>
    </c:when>
    <c:otherwise>
        <jsp:include page="../layout/sidebar-membre.jsp"/>
    </c:otherwise>
</c:choose>

<div class="row">
    <div class="col-12">
        <div class="page-title-box d-sm-flex align-items-center justify-content-between">
            <h4 class="mb-sm-0 font-size-18">Mon profil</h4>
            <div class="page-title-right">
                <ol class="breadcrumb m-0">
                    <li class="breadcrumb-item">
                        <a href="${ctx}/${sessionScope.user.role == 'ADMIN' ? 'admin' : 'membre'}/dashboard">Accueil</a>
                    </li>
                    <li class="breadcrumb-item active">Profil</li>
                </ol>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../layout/flash-messages.jsp"/>

<div class="row">
    <%-- ===== Informations personnelles ===== --%>
    <div class="col-md-7">
        <div class="card">
            <div class="card-header bg-transparent border-bottom">
                <h4 class="card-title mb-0"><i class="fas fa-id-card me-1"></i> Mes informations</h4>
            </div>
            <form method="post" action="${ctx}/profil">
                <input type="hidden" name="action" value="infos">
                <div class="card-body">

                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label class="form-label" for="prenom">Prénom <span class="text-danger">*</span></label>
                            <input type="text" id="prenom" name="prenom" class="form-control"
                                   value="${membre.prenom}" required maxlength="80">
                        </div>
                        <div class="col-md-6 mb-3">
                            <label class="form-label" for="nom">Nom <span class="text-danger">*</span></label>
                            <input type="text" id="nom" name="nom" class="form-control"
                                   value="${membre.nom}" required maxlength="80">
                        </div>
                    </div>

                    <div class="mb-3">
                        <label class="form-label" for="email">Email <span class="text-danger">*</span></label>
                        <input type="email" id="email" name="email" class="form-control"
                               value="${membre.email}" required maxlength="120">
                    </div>

                    <div class="mb-3">
                        <label class="form-label" for="dateNaissance">Date de naissance</label>
                        <input type="date" id="dateNaissance" name="dateNaissance" class="form-control"
                               value="${membre.dateNaissance}">
                    </div>

                    <hr>
                    <small class="text-muted">
                        <i class="fas fa-info-circle me-1"></i>
                        Adhérent depuis <b>${membre.dateAdhesion}</b> &middot;
                        Statut : <span class="badge bg-info">${membre.statut}</span> &middot;
                        Rôle : <span class="badge bg-secondary">${membre.role}</span>
                    </small>
                </div>
                <div class="card-footer bg-transparent border-top">
                    <button type="submit" class="btn btn-primary">
                        <i class="fas fa-save me-1"></i> Enregistrer
                    </button>
                </div>
            </form>
        </div>
    </div>

    <%-- ===== Changement de mot de passe ===== --%>
    <div class="col-md-5">
        <div class="card">
            <div class="card-header bg-transparent border-bottom">
                <h4 class="card-title mb-0"><i class="fas fa-key me-1"></i> Changer mon mot de passe</h4>
            </div>
            <form method="post" action="${ctx}/profil" autocomplete="off">
                <input type="hidden" name="action" value="password">
                <div class="card-body">

                    <div class="mb-3">
                        <label class="form-label" for="currentPassword">Mot de passe actuel <span class="text-danger">*</span></label>
                        <input type="password" id="currentPassword" name="currentPassword"
                               class="form-control" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label" for="newPassword">Nouveau mot de passe <span class="text-danger">*</span></label>
                        <input type="password" id="newPassword" name="newPassword"
                               class="form-control" required minlength="6">
                        <small class="form-text text-muted">6 caractères minimum.</small>
                    </div>
                    <div class="mb-3">
                        <label class="form-label" for="confirmPassword">Confirmer le nouveau <span class="text-danger">*</span></label>
                        <input type="password" id="confirmPassword" name="confirmPassword"
                               class="form-control" required minlength="6">
                    </div>
                </div>
                <div class="card-footer bg-transparent border-top">
                    <button type="submit" class="btn btn-warning">
                        <i class="fas fa-shield-alt me-1"></i> Modifier le mot de passe
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>

<jsp:include page="../layout/footer.jsp"/>
