<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Nouvelle amende"/>
<c:set var="activeMenu" value="amendes"/>

<jsp:include page="../layout/header.jsp"/>
<jsp:include page="../layout/sidebar-admin.jsp"/>

<div class="content-wrapper">

    <section class="content-header">
        <div class="container-fluid">
            <div class="row mb-2">
                <div class="col-sm-6"><h1>Créer une amende manuelle</h1></div>
                <div class="col-sm-6">
                    <ol class="breadcrumb float-sm-right">
                        <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/admin/amendes">Amendes</a></li>
                        <li class="breadcrumb-item active">Nouvelle</li>
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
                    <h3 class="card-title"><i class="fas fa-gavel mr-1"></i> Détails</h3>
                </div>
                <form method="post" action="${pageContext.request.contextPath}/admin/amendes/nouveau">
                    <input type="hidden" name="_csrf" value="${csrfToken}">
                    <div class="card-body">
                        <div class="form-group">
                            <label for="membre">Membre <span class="text-danger">*</span></label>
                            <select id="membre" name="membre" class="form-control" required>
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

                        <div class="form-group">
                            <label for="montant">Montant (FCFA) <span class="text-danger">*</span></label>
                            <input type="number" id="montant" name="montant" class="form-control"
                                   min="1" step="100"
                                   value="${param.montant != null ? param.montant : montantStandard}" required>
                            <small class="form-text text-muted">Montant standard : ${montantStandard} FCFA.</small>
                        </div>
                    </div>
                    <div class="card-footer">
                        <button type="submit" class="btn btn-primary">
                            <i class="fas fa-save mr-1"></i> Créer l'amende
                        </button>
                        <a href="${pageContext.request.contextPath}/admin/amendes" class="btn btn-default">
                            <i class="fas fa-times mr-1"></i> Annuler
                        </a>
                    </div>
                </form>
            </div>
        </div>
    </section>
</div>

<jsp:include page="../layout/footer.jsp"/>
