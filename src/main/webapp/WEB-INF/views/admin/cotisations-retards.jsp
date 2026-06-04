<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Membres en retard"/>
<c:set var="activeMenu" value="cotisations"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<jsp:include page="../layout/header.jsp"/>
<jsp:include page="../layout/sidebar-admin.jsp"/>

<div class="row">
    <div class="col-12">
        <div class="page-title-box d-sm-flex align-items-center justify-content-between">
            <h4 class="mb-sm-0 font-size-18">Membres en retard — ${moisCourant}/${anneeCourante}</h4>
            <div class="page-title-right">
                <ol class="breadcrumb m-0">
                    <li class="breadcrumb-item"><a href="${ctx}/admin/cotisations">Cotisations</a></li>
                    <li class="breadcrumb-item active">Retards</li>
                </ol>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../layout/flash-messages.jsp"/>

<c:choose>
    <c:when test="${empty membresEnRetard}">
        <div class="alert alert-success" role="alert">
            <i class="fas fa-check-circle me-1"></i>
            <strong>Aucun retard pour ce mois.</strong> Tous les membres actifs sont à jour.
        </div>
    </c:when>
    <c:otherwise>
        <div class="card border border-warning">
            <div class="card-body d-flex justify-content-between align-items-center flex-wrap gap-2">
                <h5 class="mb-0 text-warning">
                    <i class="fas fa-exclamation-triangle me-1"></i>
                    ${membresEnRetard.size()} membre(s) n'ont pas payé pour ${moisCourant}/${anneeCourante}
                </h5>
                <form method="post" action="${ctx}/admin/rappels"
                      onsubmit="return confirm('Envoyer un rappel par email à tous les membres en retard ?');"
                      class="mb-0">
                    <button type="submit" class="btn btn-warning">
                        <i class="fas fa-paper-plane me-1"></i> Envoyer les rappels par email
                    </button>
                <input type="hidden" name="_csrf" value="${csrfToken}"/>
</form>
            </div>
        </div>

        <div class="card">
            <div class="card-body">
                <table class="table table-bordered table-striped datatable" style="width:100%">
                    <thead class="table-light">
                        <tr>
                            <th>#</th>
                            <th>Prénom</th>
                            <th>Nom</th>
                            <th>Email</th>
                            <th>Adhésion</th>
                            <th class="text-center">Mois dus</th>
                            <th class="text-end">Montant dû</th>
                            <th class="text-center">Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="m" items="${membresEnRetard}">
                            <tr>
                                <td>${m.numero}</td>
                                <td>${m.prenom}</td>
                                <td>${m.nom}</td>
                                <td>${m.email}</td>
                                <td>${m.dateAdhesion}</td>
                                <td class="text-center">
                                    <span class="badge bg-danger" style="font-size:0.95em">
                                        ${nbMoisDus[m.numero]}
                                    </span>
                                </td>
                                <td class="text-end">
                                    <strong><fmt:formatNumber value="${montantDu[m.numero]}"/> FCFA</strong>
                                </td>
                                <td class="text-center">
                                    <a href="${ctx}/admin/cotisations/nouveau?membre=${m.numero}&mois=${moisCourant}&annee=${anneeCourante}"
                                       class="btn btn-success btn-sm">
                                        <i class="fas fa-money-bill"></i> Enregistrer paiement
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </c:otherwise>
</c:choose>

<jsp:include page="../layout/footer.jsp"/>
