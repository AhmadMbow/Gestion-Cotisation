<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Mes amendes"/>
<c:set var="activeMenu" value="amendes"/>

<jsp:include page="../layout/header.jsp"/>
<jsp:include page="../layout/sidebar-membre.jsp"/>

<div class="content-wrapper">

    <section class="content-header">
        <div class="container-fluid">
            <div class="row mb-2">
                <div class="col-sm-6"><h1>Mes amendes</h1></div>
                <div class="col-sm-6">
                    <ol class="breadcrumb float-sm-right">
                        <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/membre/dashboard">Accueil</a></li>
                        <li class="breadcrumb-item active">Amendes</li>
                    </ol>
                </div>
            </div>
        </div>
    </section>

    <section class="content">
        <div class="container-fluid">

            <jsp:include page="../layout/flash-messages.jsp"/>

            <c:choose>
                <c:when test="${totalImpayees == 0 && not empty amendes}">
                    <div class="alert alert-success">
                        <i class="fas fa-check-circle mr-1"></i>
                        <strong>Bravo !</strong> Vous n'avez aucune amende impayée.
                    </div>
                </c:when>
                <c:when test="${totalImpayees > 0}">
                    <div class="callout callout-danger">
                        <h5><i class="fas fa-exclamation-triangle mr-1"></i>
                            Total à régler :
                            <strong><fmt:formatNumber value="${totalImpayees}"/> FCFA</strong>
                        </h5>
                    </div>
                </c:when>
            </c:choose>

            <div class="card">
                <div class="card-header">
                    <h3 class="card-title"><i class="fas fa-gavel mr-1"></i> Historique (${amendes.size()})</h3>
                </div>
                <div class="card-body">
                    <table class="table table-bordered table-striped datatable" style="width:100%">
                        <thead class="thead-light">
                            <tr>
                                <th>#</th>
                                <th>Montant</th>
                                <th>Date génération</th>
                                <th>Statut</th>
                                <th class="text-center">Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="a" items="${amendes}">
                                <tr>
                                    <td>${a.id}</td>
                                    <td><fmt:formatNumber value="${a.montant}"/> FCFA</td>
                                    <td>${a.dateGeneration}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${a.statutPaiement == 'PAYEE'}">
                                                <span class="badge badge-success"><i class="fas fa-check mr-1"></i>Payée</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge badge-danger"><i class="fas fa-times mr-1"></i>Impayée</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td class="text-center">
                                        <c:if test="${a.statutPaiement == 'IMPAYEE'}">
                                            <form action="${pageContext.request.contextPath}/membre/amendes/payer"
                                                  method="post" style="display:inline"
                                                  onsubmit="return confirm('Confirmer le paiement de ${a.montant} FCFA ?');">
                                                <input type="hidden" name="id" value="${a.id}">
                                                <button type="submit" class="btn btn-success btn-sm">
                                                    <i class="fas fa-credit-card mr-1"></i> Payer
                                                </button>
                                            </form>
                                        </c:if>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty amendes}">
                                <tr><td colspan="5" class="text-center text-muted">Aucune amende — continuez comme ça !</td></tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </section>
</div>

<jsp:include page="../layout/footer.jsp"/>
