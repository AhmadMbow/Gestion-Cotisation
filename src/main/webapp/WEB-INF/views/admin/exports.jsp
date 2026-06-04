<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Exports PDF / Excel"/>
<c:set var="activeMenu" value="exports"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<jsp:include page="../layout/header.jsp"/>
<jsp:include page="../layout/sidebar-admin.jsp"/>

<div class="row">
    <div class="col-12">
        <div class="page-title-box d-sm-flex align-items-center justify-content-between">
            <h4 class="mb-sm-0 font-size-18">Exports PDF / Excel</h4>
            <div class="page-title-right">
                <ol class="breadcrumb m-0">
                    <li class="breadcrumb-item"><a href="${ctx}/admin/dashboard">Accueil</a></li>
                    <li class="breadcrumb-item active">Exports</li>
                </ol>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../layout/flash-messages.jsp"/>

<div class="row">

    <%-- Liste des membres ------------------------------------ --%>
    <div class="col-md-4">
        <div class="card">
            <div class="card-body text-center">
                <i class="fas fa-users fa-3x text-primary mb-3"></i>
                <h5>Liste des membres</h5>
                <p class="text-muted">Numéro, identité, contact, statut, rôle, dates.</p>
                <div class="d-grid">
                    <a href="${ctx}/admin/exports/membres.xlsx" class="btn btn-success">
                        <i class="fas fa-file-excel me-1"></i> Excel (.xlsx)
                    </a>
                </div>
            </div>
        </div>
    </div>

    <%-- Toutes les cotisations -------------------------------- --%>
    <div class="col-md-4">
        <div class="card">
            <div class="card-body text-center">
                <i class="fas fa-money-check-alt fa-3x text-success mb-3"></i>
                <h5>Toutes les cotisations</h5>
                <p class="text-muted">Historique complet des paiements de cotisations.</p>
                <div class="d-grid">
                    <a href="${ctx}/admin/exports/cotisations.xlsx" class="btn btn-success">
                        <i class="fas fa-file-excel me-1"></i> Excel (.xlsx)
                    </a>
                </div>
            </div>
        </div>
    </div>

    <%-- Toutes les amendes ------------------------------------ --%>
    <div class="col-md-4">
        <div class="card">
            <div class="card-body text-center">
                <i class="fas fa-gavel fa-3x text-warning mb-3"></i>
                <h5>Toutes les amendes</h5>
                <p class="text-muted">Liste des amendes (payées et impayées).</p>
                <div class="d-grid">
                    <a href="${ctx}/admin/exports/amendes.xlsx" class="btn btn-success">
                        <i class="fas fa-file-excel me-1"></i> Excel (.xlsx)
                    </a>
                </div>
            </div>
        </div>
    </div>

</div>

<div class="row">
    <div class="col-12">
        <div class="alert alert-info" role="alert">
            <h5><i class="fas fa-file-pdf me-1"></i> Reçus PDF</h5>
            <p class="mb-0">
                Les reçus PDF des cotisations sont disponibles depuis la liste des cotisations
                (<a href="${ctx}/admin/cotisations">Cotisations &raquo; Reçu PDF</a>)
                ou via le lien direct
                <code>/admin/recu?id=&lt;ID&gt;&amp;format=pdf</code>.
            </p>
        </div>
    </div>
</div>

<jsp:include page="../layout/footer.jsp"/>
