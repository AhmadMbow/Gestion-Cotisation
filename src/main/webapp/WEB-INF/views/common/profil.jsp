<%@ page contentType="text/html;charset=UTF-8" language="java" import="java.time.LocalDate" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="today" value="<%= java.time.LocalDate.now() %>"/>
<c:set var="pageTitle" value="Mon profil"/>
<c:set var="activeMenu" value="profil"/>

<jsp:include page="../layout/header.jsp"/>

<c:choose>
    <c:when test="${sessionScope.user.role == 'ADMIN'}">
        <jsp:include page="../layout/sidebar-admin.jsp"/>
    </c:when>
    <c:otherwise>
        <jsp:include page="../layout/sidebar-membre.jsp"/>
    </c:otherwise>
</c:choose>

<div class="content-wrapper">

    <section class="content-header">
        <div class="container-fluid">
            <div class="row mb-2">
                <div class="col-sm-6"><h1>Mon profil</h1></div>
                <div class="col-sm-6">
                    <ol class="breadcrumb float-sm-right">
                        <li class="breadcrumb-item">
                            <a href="${pageContext.request.contextPath}/${sessionScope.user.role == 'ADMIN' ? 'admin' : 'membre'}/dashboard">Accueil</a>
                        </li>
                        <li class="breadcrumb-item active">Profil</li>
                    </ol>
                </div>
            </div>
        </div>
    </section>

    <section class="content">
        <div class="container-fluid">

            <jsp:include page="../layout/flash-messages.jsp"/>

            <div class="row">
                <%-- ===== Informations personnelles ===== --%>
                <div class="col-md-7">
                    <div class="card card-primary">
                        <div class="card-header">
                            <h3 class="card-title"><i class="fas fa-id-card mr-1"></i> Mes informations</h3>
                        </div>
                        <form method="post" action="${pageContext.request.contextPath}/profil">
                            <input type="hidden" name="_csrf" value="${csrfToken}">
                            <input type="hidden" name="action" value="infos">
                            <div class="card-body">

                                <div class="form-row">
                                    <div class="form-group col-md-6">
                                        <label for="prenom">Prénom <span class="text-danger">*</span></label>
                                        <input type="text" id="prenom" name="prenom" class="form-control"
                                               value="${membre.prenom}" required minlength="2" maxlength="80">
                                    </div>
                                    <div class="form-group col-md-6">
                                        <label for="nom">Nom <span class="text-danger">*</span></label>
                                        <input type="text" id="nom" name="nom" class="form-control"
                                               value="${membre.nom}" required minlength="2" maxlength="80">
                                    </div>
                                </div>

                                <div class="form-group">
                                    <label for="email">Email <span class="text-danger">*</span></label>
                                    <input type="email" id="email" name="email" class="form-control"
                                           value="${membre.email}" required maxlength="120">
                                </div>

                                <div class="form-group">
                                    <label for="dateNaissance">Date de naissance</label>
                                    <input type="date" id="dateNaissance" name="dateNaissance" class="form-control"
                                           value="${membre.dateNaissance}" max="${today}">
                                </div>

                                <hr>
                                <small class="text-muted">
                                    <i class="fas fa-info-circle mr-1"></i>
                                    Adhérent depuis <b>${membre.dateAdhesion}</b> &middot;
                                    Statut : <span class="badge badge-info">${membre.statut}</span> &middot;
                                    Rôle : <span class="badge badge-secondary">${membre.role}</span>
                                </small>
                            </div>
                            <div class="card-footer">
                                <button type="submit" class="btn btn-primary">
                                    <i class="fas fa-save mr-1"></i> Enregistrer
                                </button>
                            </div>
                        </form>
                    </div>
                </div>

                <%-- ===== Changement de mot de passe ===== --%>
                <div class="col-md-5">
                    <div class="card card-warning">
                        <div class="card-header">
                            <h3 class="card-title"><i class="fas fa-key mr-1"></i> Changer mon mot de passe</h3>
                        </div>
                        <form method="post" action="${pageContext.request.contextPath}/profil" autocomplete="off">
                            <input type="hidden" name="_csrf" value="${csrfToken}">
                            <input type="hidden" name="action" value="password">
                            <div class="card-body">

                                <div class="form-group">
                                    <label for="currentPassword">Mot de passe actuel <span class="text-danger">*</span></label>
                                    <input type="password" id="currentPassword" name="currentPassword"
                                           class="form-control" required>
                                </div>
                                <div class="form-group">
                                    <label for="newPassword">Nouveau mot de passe <span class="text-danger">*</span></label>
                                    <input type="password" id="newPassword" name="newPassword"
                                           class="form-control" required minlength="6">
                                    <small class="form-text text-muted">6 caractères minimum.</small>
                                </div>
                                <div class="form-group">
                                    <label for="confirmPassword">Confirmer le nouveau <span class="text-danger">*</span></label>
                                    <input type="password" id="confirmPassword" name="confirmPassword"
                                           class="form-control" required minlength="6">
                                </div>
                            </div>
                            <div class="card-footer">
                                <button type="submit" class="btn btn-warning">
                                    <i class="fas fa-shield-alt mr-1"></i> Modifier le mot de passe
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>

        </div>
    </section>
</div>

<jsp:include page="../layout/footer.jsp"/>
