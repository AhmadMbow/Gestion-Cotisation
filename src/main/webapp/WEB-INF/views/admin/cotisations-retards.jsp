<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Membres en retard"/>
<c:set var="activeMenu" value="cotisations"/>

<jsp:include page="../layout/header.jsp"/>
<jsp:include page="../layout/sidebar-admin.jsp"/>

<div class="content-wrapper">

    <section class="content-header">
        <div class="container-fluid">
            <div class="row mb-2">
                <div class="col-sm-6"><h1>Membres en retard — ${moisCourant}/${anneeCourante}</h1></div>
                <div class="col-sm-6">
                    <ol class="breadcrumb float-sm-right">
                        <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/admin/cotisations">Cotisations</a></li>
                        <li class="breadcrumb-item active">Retards</li>
                    </ol>
                </div>
            </div>
        </div>
    </section>

    <section class="content">
        <div class="container-fluid">

            <jsp:include page="../layout/flash-messages.jsp"/>

            <c:choose>
                <c:when test="${empty membresEnRetard}">
                    <div class="alert alert-success">
                        <i class="fas fa-check-circle mr-1"></i>
                        <strong>Aucun retard pour ce mois.</strong> Tous les membres actifs sont à jour.
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="callout callout-warning d-flex justify-content-between align-items-center">
                        <h5 class="mb-0">
                            <i class="fas fa-exclamation-triangle mr-1"></i>
                            ${membresEnRetard.size()} membre(s) n'ont pas payé pour ${moisCourant}/${anneeCourante}
                        </h5>
                        <form method="post" action="${pageContext.request.contextPath}/admin/rappels"
                              onsubmit="return confirm('Envoyer un rappel par email à tous les membres en retard ?');"
                              class="mb-0">
                            <button type="submit" class="btn btn-warning">
                                <i class="fas fa-paper-plane mr-1"></i> Envoyer les rappels par email
                            </button>
                        </form>
                    </div>

                    <div class="card">
                        <div class="card-body">
                            <table class="table table-bordered table-striped datatable" style="width:100%">
                                <thead class="thead-light">
                                    <tr>
                                        <th>#</th>
                                        <th>Prénom</th>
                                        <th>Nom</th>
                                        <th>Email</th>
                                        <th>Adhésion</th>
                                        <th class="text-center">Mois dus</th>
                                        <th class="text-right">Montant dû</th>
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
                                                <span class="badge badge-danger" style="font-size:0.95em">
                                                    ${nbMoisDus[m.numero]}
                                                </span>
                                            </td>
                                            <td class="text-right">
                                                <strong><fmt:formatNumber value="${montantDu[m.numero]}"/> FCFA</strong>
                                            </td>
                                            <td class="text-center">
                                                <a href="${pageContext.request.contextPath}/admin/cotisations/nouveau?membre=${m.numero}&mois=${moisCourant}&annee=${anneeCourante}"
                                                   class="btn btn-success btn-xs">
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
        </div>
    </section>
</div>

<jsp:include page="../layout/footer.jsp"/>
