<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Gestion des membres"/>
<c:set var="activeMenu" value="membres"/>

<jsp:include page="../layout/header.jsp"/>
<jsp:include page="../layout/sidebar-admin.jsp"/>

<div class="content-wrapper">

    <section class="content-header">
        <div class="container-fluid">
            <div class="row mb-2">
                <div class="col-sm-6"><h1>Membres de l'association</h1></div>
                <div class="col-sm-6">
                    <ol class="breadcrumb float-sm-right">
                        <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/admin/dashboard">Accueil</a></li>
                        <li class="breadcrumb-item active">Membres</li>
                    </ol>
                </div>
            </div>
        </div>
    </section>

    <section class="content">
        <div class="container-fluid">

            <jsp:include page="../layout/flash-messages.jsp"/>

            <div class="card">
                <div class="card-header">
                    <h3 class="card-title"><i class="fas fa-users mr-1"></i> Liste des membres (${membres.size()})</h3>
                    <div class="card-tools">
                        <a href="${pageContext.request.contextPath}/admin/membres/nouveau" class="btn btn-primary btn-sm">
                            <i class="fas fa-plus"></i> Ajouter un membre
                        </a>
                    </div>
                </div>

                <div class="card-body">
                    <table class="table table-bordered table-striped datatable" style="width:100%">
                        <thead class="thead-light">
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
                                                <span class="badge badge-primary"><i class="fas fa-user-shield mr-1"></i>Admin</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge badge-secondary">Membre</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>${m.dateAdhesion}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${m.statut == 'ACTIF'}">
                                                <span class="badge badge-success">Actif</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge badge-warning">Inactif</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td class="text-center" style="white-space: nowrap;">
                                        <a href="${pageContext.request.contextPath}/admin/membres/edit?id=${m.numero}"
                                           class="btn btn-info btn-xs" title="Modifier">
                                            <i class="fas fa-edit"></i>
                                        </a>
                                        <form action="${pageContext.request.contextPath}/admin/membres/toggle"
                                              method="post" style="display:inline">
                                            <input type="hidden" name="_csrf" value="${csrfToken}">
                                            <input type="hidden" name="id" value="${m.numero}">
                                            <button type="submit" class="btn btn-warning btn-xs"
                                                    title="${m.statut == 'ACTIF' ? 'Désactiver' : 'Activer'}">
                                                <i class="fas ${m.statut == 'ACTIF' ? 'fa-toggle-on' : 'fa-toggle-off'}"></i>
                                            </button>
                                        </form>
                                        <c:if test="${m.numero != sessionScope.user.numero}">
                                            <form action="${pageContext.request.contextPath}/admin/membres/delete"
                                                  method="post" style="display:inline"
                                                  onsubmit="return confirm('Supprimer définitivement ${m.prenom} ${m.nom} ? Toutes ses cotisations et amendes seront aussi supprimées.');">
                                                <input type="hidden" name="_csrf" value="${csrfToken}">
                                                <input type="hidden" name="id" value="${m.numero}">
                                                <button type="submit" class="btn btn-danger btn-xs" title="Supprimer">
                                                    <i class="fas fa-trash"></i>
                                                </button>
                                            </form>
                                        </c:if>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty membres}">
                                <tr><td colspan="8" class="text-center text-muted">Aucun membre enregistré.</td></tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </div>

        </div>
    </section>
</div>

<jsp:include page="../layout/footer.jsp"/>
