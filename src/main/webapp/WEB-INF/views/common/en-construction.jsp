<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="${pageTitre != null ? pageTitre : 'En construction'}"/>

<c:choose>
    <c:when test="${sidebar == 'admin'}">
        <jsp:include page="../layout/header.jsp"/>
        <jsp:include page="../layout/sidebar-admin.jsp"/>
    </c:when>
    <c:otherwise>
        <jsp:include page="../layout/header.jsp"/>
        <jsp:include page="../layout/sidebar-membre.jsp"/>
    </c:otherwise>
</c:choose>

<div class="content-wrapper">

    <section class="content-header">
        <div class="container-fluid">
            <div class="row mb-2">
                <div class="col-sm-6"><h1>${pageTitre}</h1></div>
                <div class="col-sm-6">
                    <ol class="breadcrumb float-sm-right">
                        <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/">Accueil</a></li>
                        <li class="breadcrumb-item active">${pageTitre}</li>
                    </ol>
                </div>
            </div>
        </div>
    </section>

    <section class="content">
        <div class="container-fluid">
            <div class="card card-warning card-outline">
                <div class="card-body text-center py-5">
                    <i class="fas fa-tools fa-5x text-warning mb-4"></i>
                    <h2 class="text-warning">Module en construction</h2>
                    <p class="lead text-muted">${description != null ? description : 'Cette section sera bientôt disponible.'}</p>
                    <a href="${pageContext.request.contextPath}/${sidebar == 'admin' ? 'admin' : 'membre'}/dashboard"
                       class="btn btn-primary mt-3">
                        <i class="fas fa-arrow-left mr-1"></i> Retour au tableau de bord
                    </a>
                </div>
            </div>
        </div>
    </section>
</div>

<jsp:include page="../layout/footer.jsp"/>
