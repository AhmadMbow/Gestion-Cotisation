<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Historique des connexions"/>
<c:set var="activeMenu" value="connexions"/>

<jsp:include page="../layout/header.jsp"/>
<jsp:include page="../layout/sidebar-admin.jsp"/>

<div class="content-wrapper">

    <section class="content-header">
        <div class="container-fluid">
            <div class="row mb-2">
                <div class="col-sm-6"><h1>Historique des connexions</h1></div>
                <div class="col-sm-6">
                    <ol class="breadcrumb float-sm-right">
                        <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/admin/dashboard">Accueil</a></li>
                        <li class="breadcrumb-item active">Connexions</li>
                    </ol>
                </div>
            </div>
        </div>
    </section>

    <section class="content">
        <div class="container-fluid">

            <jsp:include page="../layout/flash-messages.jsp"/>

            <div class="callout callout-info">
                <h5 class="mb-1">
                    <i class="fas fa-info-circle mr-1"></i>
                    ${total} connexion(s) au total &mdash; ${limite} plus récentes affichées
                </h5>
                <p class="text-muted small mb-0">
                    Chaque connexion réussie est enregistrée avec l'adresse IP et le navigateur utilisé.
                </p>
            </div>

            <div class="card">
                <div class="card-body">
                    <c:choose>
                        <c:when test="${empty connexions}">
                            <p class="text-muted text-center mb-0">
                                <i class="fas fa-history mr-1"></i> Aucune connexion enregistrée pour le moment.
                            </p>
                        </c:when>
                        <c:otherwise>
                            <table class="table table-bordered table-striped datatable" style="width:100%">
                                <thead class="thead-light">
                                    <tr>
                                        <th>Date / Heure</th>
                                        <th>Membre</th>
                                        <th>Email</th>
                                        <th>Rôle</th>
                                        <th>IP</th>
                                        <th>Navigateur (User-Agent)</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="c" items="${connexions}">
                                        <tr>
                                            <td>
                                                <fmt:parseDate value="${c.dateConnexion}" pattern="yyyy-MM-dd'T'HH:mm:ss" var="dt" type="both"/>
                                                <fmt:formatDate value="${dt}" pattern="dd/MM/yyyy HH:mm:ss"/>
                                            </td>
                                            <td>${c.membre.prenom} ${c.membre.nom}</td>
                                            <td>${c.membre.email}</td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${c.membre.role == 'ADMIN'}">
                                                        <span class="badge badge-danger">ADMIN</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge badge-secondary">MEMBRE</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td><code>${c.ip}</code></td>
                                            <td class="small text-muted">
                                                <c:out value="${c.userAgent}" default="—"/>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

        </div>
    </section>
</div>

<jsp:include page="../layout/footer.jsp"/>
