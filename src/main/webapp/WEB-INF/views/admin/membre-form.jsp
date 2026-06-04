<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="${modeEdition ? 'Modifier un membre' : 'Ajouter un membre'}"/>
<c:set var="activeMenu" value="membres"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<jsp:include page="../layout/header.jsp"/>
<jsp:include page="../layout/sidebar-admin.jsp"/>

<%-- Valeurs pré-remplies : soit depuis l'entité (mode édition), soit depuis les params (mode création en erreur) --%>
<c:set var="vPrenom" value="${modeEdition ? membre.prenom : formPrenom}"/>
<c:set var="vNom" value="${modeEdition ? membre.nom : formNom}"/>
<c:set var="vEmail" value="${modeEdition ? membre.email : formEmail}"/>
<c:set var="vDateNaissance" value="${modeEdition ? membre.dateNaissance : formDateNaissance}"/>
<c:set var="vRole" value="${modeEdition ? membre.role.name() : formRole}"/>

<div class="row">
    <div class="col-12">
        <div class="page-title-box d-sm-flex align-items-center justify-content-between">
            <h4 class="mb-sm-0 font-size-18">${pageTitle}</h4>
            <div class="page-title-right">
                <ol class="breadcrumb m-0">
                    <li class="breadcrumb-item"><a href="${ctx}/admin/membres">Membres</a></li>
                    <li class="breadcrumb-item active">${modeEdition ? 'Modifier' : 'Nouveau'}</li>
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
                <h4 class="card-title mb-0">
                    <i class="fas ${modeEdition ? 'fa-edit' : 'fa-user-plus'} me-1"></i>
                    Informations du membre
                </h4>
            </div>

            <form method="post"
                  action="${ctx}${modeEdition
                          ? '/admin/membres/edit?id='.concat(membre.numero)
                          : '/admin/membres/nouveau'}">
                <div class="card-body">

                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label class="form-label" for="prenom">Prénom <span class="text-danger">*</span></label>
                            <input type="text" id="prenom" name="prenom" class="form-control"
                                   value="${vPrenom}" required maxlength="80">
                        </div>
                        <div class="col-md-6 mb-3">
                            <label class="form-label" for="nom">Nom <span class="text-danger">*</span></label>
                            <input type="text" id="nom" name="nom" class="form-control"
                                   value="${vNom}" required maxlength="80">
                        </div>
                    </div>

                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label class="form-label" for="email">Email <span class="text-danger">*</span></label>
                            <input type="email" id="email" name="email" class="form-control"
                                   value="${vEmail}" required maxlength="120">
                        </div>
                        <div class="col-md-3 mb-3">
                            <label class="form-label" for="dateNaissance">Date de naissance</label>
                            <input type="date" id="dateNaissance" name="dateNaissance" class="form-control"
                                   value="${vDateNaissance}">
                        </div>
                        <div class="col-md-3 mb-3">
                            <label class="form-label" for="role">Rôle</label>
                            <select id="role" name="role" class="form-select">
                                <option value="MEMBRE" ${vRole == 'MEMBRE' || empty vRole ? 'selected' : ''}>Membre</option>
                                <option value="ADMIN"  ${vRole == 'ADMIN' ? 'selected' : ''}>Administrateur</option>
                            </select>
                        </div>
                    </div>

                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label class="form-label" for="motDePasse">
                                Mot de passe <c:if test="${!modeEdition}"><span class="text-danger">*</span></c:if>
                            </label>
                            <input type="password" id="motDePasse" name="motDePasse" class="form-control"
                                   minlength="6" ${!modeEdition ? 'required' : ''}>
                            <c:if test="${modeEdition}">
                                <small class="form-text text-muted">Laisser vide pour conserver le mot de passe actuel.</small>
                            </c:if>
                        </div>
                    </div>
                </div>

                <div class="card-footer bg-transparent border-top">
                    <button type="submit" class="btn btn-primary">
                        <i class="fas fa-save me-1"></i> ${modeEdition ? 'Enregistrer les modifications' : 'Créer le membre'}
                    </button>
                    <a href="${ctx}/admin/membres" class="btn btn-light">
                        <i class="fas fa-times me-1"></i> Annuler
                    </a>
                </div>
            </form>
        </div>
    </div>
</div>

<jsp:include page="../layout/footer.jsp"/>
