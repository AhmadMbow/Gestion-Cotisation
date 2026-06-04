<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Historique des connexions"/>
<c:set var="activeMenu" value="connexions"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<jsp:include page="../layout/header.jsp"/>
<jsp:include page="../layout/sidebar-admin.jsp"/>

<div class="row">
    <div class="col-12">
        <div class="page-title-box d-sm-flex align-items-center justify-content-between">
            <h4 class="mb-sm-0 font-size-18">Historique des connexions</h4>
            <div class="page-title-right">
                <ol class="breadcrumb m-0">
                    <li class="breadcrumb-item"><a href="${ctx}/admin/dashboard">Accueil</a></li>
                    <li class="breadcrumb-item active">Connexions</li>
                </ol>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../layout/flash-messages.jsp"/>

<div class="alert alert-info" role="alert">
    <h5 class="mb-1">
        <i class="fas fa-info-circle me-1"></i>
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
                    <i class="fas fa-history me-1"></i> Aucune connexion enregistrée pour le moment.
                </p>
            </c:when>
            <c:otherwise>
                <table class="table table-bordered table-striped datatable" style="width:100%">
                    <thead class="table-light">
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
                                            <span class="badge bg-danger">ADMIN</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge bg-secondary">MEMBRE</span>
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

<jsp:include page="../layout/footer.jsp"/>
