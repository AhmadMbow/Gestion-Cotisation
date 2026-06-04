<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Gestion des membres"/>
<c:set var="activeMenu" value="membres"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<jsp:include page="../layout/header.jsp"/>
<jsp:include page="../layout/sidebar-admin.jsp"/>

<div class="row">
    <div class="col-12">
        <div class="page-title-box d-sm-flex align-items-center justify-content-between">
            <h4 class="mb-sm-0 font-size-18">Membres de l'association</h4>
            <div class="page-title-right">
                <ol class="breadcrumb m-0">
                    <li class="breadcrumb-item"><a href="${ctx}/admin/dashboard">Accueil</a></li>
                    <li class="breadcrumb-item active">Membres</li>
                </ol>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../layout/flash-messages.jsp"/>

<div class="card">
    <div class="card-body">
        <div class="d-flex align-items-center justify-content-between mb-3">
            <h4 class="card-title mb-0"><i class="fas fa-users me-1"></i> Liste des membres (${membres.size()})</h4>
            <a href="${ctx}/admin/membres/nouveau" class="btn btn-primary btn-sm">
                <i class="fas fa-plus"></i> Ajouter un membre
            </a>
        </div>

        <table class="table table-bordered table-striped datatable" style="width:100%">
            <thead class="table-light">
                <tr>
                    <th>#</th>
                    <th>Prénom</th>
                    <th>Nom</th>
                    <th>Email</th>
                    <th>Rôle</th>
                    <th>Adhésion</th>
                    <th>Statut</th>
                    <th class="text-center">Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="m" items="${membres}">
                    <tr>
                        <td>${m.numero}</td>
                        <td>${m.prenom}</td>
                        <td>${m.nom}</td>
                        <td>${m.email}</td>
                        <td>
                            <c:choose>
                                <c:when test="${m.role == 'ADMIN'}">
                                    <span class="badge bg-primary"><i class="fas fa-user-shield me-1"></i>Admin</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge bg-secondary">Membre</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td>${m.dateAdhesion}</td>
                        <td>
                            <c:choose>
                                <c:when test="${m.statut == 'ACTIF'}">
                                    <span class="badge bg-success">Actif</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge bg-warning">Inactif</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td class="text-center" style="white-space: nowrap;">
                            <a href="${ctx}/admin/membres/edit?id=${m.numero}"
                               class="btn btn-info btn-sm" title="Modifier">
                                <i class="fas fa-edit"></i>
                            </a>
                            <form action="${ctx}/admin/membres/toggle"
                                  method="post" style="display:inline">
                                <input type="hidden" name="id" value="${m.numero}">
                                <button type="submit" class="btn btn-warning btn-sm"
                                        title="${m.statut == 'ACTIF' ? 'Désactiver' : 'Activer'}">
                                    <i class="fas ${m.statut == 'ACTIF' ? 'fa-toggle-on' : 'fa-toggle-off'}"></i>
                                </button>
                            </form>
                            <c:if test="${m.numero != sessionScope.user.numero}">
                                <form action="${ctx}/admin/membres/delete"
                                      method="post" style="display:inline"
                                      onsubmit="return confirm('Supprimer définitivement ${m.prenom} ${m.nom} ? Toutes ses cotisations et amendes seront aussi supprimées.');">
                                    <input type="hidden" name="id" value="${m.numero}">
                                    <button type="submit" class="btn btn-danger btn-sm" title="Supprimer">
                                        <i class="fas fa-trash"></i>
                                    </button>
                                </form>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
</div>

<jsp:include page="../layout/footer.jsp"/>
