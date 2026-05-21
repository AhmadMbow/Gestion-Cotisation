<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Exports PDF / Excel"/>
<c:set var="activeMenu" value="exports"/>

<jsp:include page="../layout/header.jsp"/>
<jsp:include page="../layout/sidebar-admin.jsp"/>

<div class="content-wrapper">

    <section class="content-header">
        <div class="container-fluid">
            <div class="row mb-2">
                <div class="col-sm-6"><h1>Exports PDF / Excel</h1></div>
                <div class="col-sm-6">
                    <ol class="breadcrumb float-sm-right">
                        <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/admin/dashboard">Accueil</a></li>
                        <li class="breadcrumb-item active">Exports</li>
                    </ol>
                </div>
            </div>
        </div>
    </section>

    <section class="content">
        <div class="container-fluid">

            <jsp:include page="../layout/flash-messages.jsp"/>

            <div class="row">

                <%-- Liste des membres ------------------------------------ --%>
                <div class="col-md-4">
                    <div class="card card-primary card-outline">
                        <div class="card-body text-center">
                            <i class="fas fa-users fa-3x text-primary mb-3"></i>
                            <h5>Liste des membres</h5>
                            <p class="text-muted">Numéro, identité, contact, statut, rôle, dates.</p>
                            <a href="${pageContext.request.contextPath}/admin/exports/membres.xlsx"
                               class="btn btn-success btn-block">
                                <i class="fas fa-file-excel mr-1"></i> Excel (.xlsx)
                            </a>
                        </div>
                    </div>
                </div>

                <%-- Toutes les cotisations -------------------------------- --%>
                <div class="col-md-4">
                    <div class="card card-primary card-outline">
                        <div class="card-body text-center">
                            <i class="fas fa-money-check-alt fa-3x text-success mb-3"></i>
                            <h5>Toutes les cotisations</h5>
                            <p class="text-muted">Historique complet des paiements de cotisations.</p>
                            <a href="${pageContext.request.contextPath}/admin/exports/cotisations.xlsx"
                               class="btn btn-success btn-block">
                                <i class="fas fa-file-excel mr-1"></i> Excel (.xlsx)
                            </a>
                        </div>
                    </div>
                </div>

                <%-- Toutes les amendes ------------------------------------ --%>
                <div class="col-md-4">
                    <div class="card card-primary card-outline">
                        <div class="card-body text-center">
                            <i class="fas fa-gavel fa-3x text-warning mb-3"></i>
                            <h5>Toutes les amendes</h5>
                            <p class="text-muted">Liste des amendes (payées et impayées).</p>
                            <a href="${pageContext.request.contextPath}/admin/exports/amendes.xlsx"
                               class="btn btn-success btn-block">
                                <i class="fas fa-file-excel mr-1"></i> Excel (.xlsx)
                            </a>
                        </div>
                    </div>
                </div>

            </div>

            <div class="row mt-3">
                <div class="col-12">
                    <div class="callout callout-info">
                        <h5><i class="fas fa-file-pdf mr-1"></i> Reçus PDF</h5>
                        <p class="mb-0">
                            Les reçus PDF des cotisations sont disponibles depuis la liste des cotisations
                            (<a href="${pageContext.request.contextPath}/admin/cotisations">Cotisations &raquo; Reçu PDF</a>)
                            ou via le lien direct
                            <code>/admin/recu?id=&lt;ID&gt;&amp;format=pdf</code>.
                        </p>
                    </div>
                </div>
            </div>

        </div>
    </section>
</div>

<jsp:include page="../layout/footer.jsp"/>
