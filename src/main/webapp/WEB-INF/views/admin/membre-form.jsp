<%@ page contentType="text/html;charset=UTF-8" language="java" import="java.time.LocalDate" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="today" value="<%= java.time.LocalDate.now() %>"/>
<c:set var="pageTitle" value="${modeEdition ? 'Modifier un membre' : 'Ajouter un membre'}"/>
<c:set var="activeMenu" value="membres"/>

<jsp:include page="../layout/header.jsp"/>
<jsp:include page="../layout/sidebar-admin.jsp"/>

<%-- Valeurs pré-remplies : soit depuis l'entité (mode édition), soit depuis les params (mode création en erreur) --%>
<c:set var="vPrenom" value="${modeEdition ? membre.prenom : formPrenom}"/>
<c:set var="vNom" value="${modeEdition ? membre.nom : formNom}"/>
<c:set var="vEmail" value="${modeEdition ? membre.email : formEmail}"/>
<c:set var="vDateNaissance" value="${modeEdition ? membre.dateNaissance : formDateNaissance}"/>
<c:set var="vRole" value="${modeEdition ? membre.role.name() : formRole}"/>

<div class="content-wrapper">

    <section class="content-header">
        <div class="container-fluid">
            <div class="row mb-2">
                <div class="col-sm-6"><h1>${pageTitle}</h1></div>
                <div class="col-sm-6">
                    <ol class="breadcrumb float-sm-right">
                        <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/admin/membres">Membres</a></li>
                        <li class="breadcrumb-item active">${modeEdition ? 'Modifier' : 'Nouveau'}</li>
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

            <div class="card card-primary">
                <div class="card-header">
                    <h3 class="card-title">
                        <i class="fas ${modeEdition ? 'fa-edit' : 'fa-user-plus'} mr-1"></i>
                        Informations du membre
                    </h3>
                </div>

                <form method="post"
                      action="${pageContext.request.contextPath}${modeEdition
                              ? '/admin/membres/edit?id='.concat(membre.numero)
                              : '/admin/membres/nouveau'}">
                    <input type="hidden" name="_csrf" value="${csrfToken}">
                    <div class="card-body">

                        <div class="form-row">
                            <div class="form-group col-md-6">
                                <label for="prenom">Prénom <span class="text-danger">*</span></label>
                                <input type="text" id="prenom" name="prenom" class="form-control"
                                       value="${vPrenom}" required minlength="2" maxlength="80"
                                       title="Au moins 2 caractères.">
                            </div>
                            <div class="form-group col-md-6">
                                <label for="nom">Nom <span class="text-danger">*</span></label>
                                <input type="text" id="nom" name="nom" class="form-control"
                                       value="${vNom}" required minlength="2" maxlength="80"
                                       title="Au moins 2 caractères.">
                            </div>
                        </div>

                        <div class="form-row">
                            <div class="form-group col-md-6">
                                <label for="email">Email <span class="text-danger">*</span></label>
                                <input type="email" id="email" name="email" class="form-control"
                                       value="${vEmail}" required maxlength="120">
                            </div>
                            <div class="form-group col-md-3">
                                <label for="dateNaissance">Date de naissance</label>
                                <input type="date" id="dateNaissance" name="dateNaissance" class="form-control"
                                       value="${vDateNaissance}" max="${today}"
                                       title="La date ne peut pas être dans le futur.">
                            </div>
                            <div class="form-group col-md-3">
                                <label for="role">Rôle</label>
                                <select id="role" name="role" class="form-control">
                                    <option value="MEMBRE" ${vRole == 'MEMBRE' || empty vRole ? 'selected' : ''}>Membre</option>
                                    <option value="ADMIN"  ${vRole == 'ADMIN' ? 'selected' : ''}>Administrateur</option>
                                </select>
                            </div>
                        </div>

                        <div class="form-row">
                            <div class="form-group col-md-6">
                                <label for="motDePasse">
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

                    <div class="card-footer">
                        <button type="submit" class="btn btn-primary">
                            <i class="fas fa-save mr-1"></i> ${modeEdition ? 'Enregistrer les modifications' : 'Créer le membre'}
                        </button>
                        <a href="${pageContext.request.contextPath}/admin/membres" class="btn btn-default">
                            <i class="fas fa-times mr-1"></i> Annuler
                        </a>
                    </div>
                </form>
            </div>

        </div>
    </section>
</div>

<jsp:include page="../layout/footer.jsp"/>
