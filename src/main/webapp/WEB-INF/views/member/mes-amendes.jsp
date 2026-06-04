<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Mes amendes"/>
<c:set var="activeMenu" value="amendes"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<jsp:include page="../layout/header.jsp"/>
<jsp:include page="../layout/sidebar-membre.jsp"/>

<div class="row">
    <div class="col-12">
        <div class="page-title-box d-sm-flex align-items-center justify-content-between">
            <h4 class="mb-sm-0 font-size-18">Mes amendes</h4>
            <div class="page-title-right">
                <ol class="breadcrumb m-0">
                    <li class="breadcrumb-item"><a href="${ctx}/membre/dashboard">Accueil</a></li>
                    <li class="breadcrumb-item active">Amendes</li>
                </ol>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../layout/flash-messages.jsp"/>

<c:choose>
    <c:when test="${totalImpayees == 0 && not empty amendes}">
        <div class="alert alert-success" role="alert">
            <i class="fas fa-check-circle me-1"></i>
            <strong>Bravo !</strong> Vous n'avez aucune amende impayée.
        </div>
    </c:when>
    <c:when test="${totalImpayees > 0}">
        <div class="alert alert-danger" role="alert">
            <h5 class="mb-0"><i class="fas fa-exclamation-triangle me-1"></i>
                Total à régler :
                <strong><fmt:formatNumber value="${totalImpayees}"/> FCFA</strong>
            </h5>
        </div>
    </c:when>
</c:choose>

<div class="card">
    <div class="card-body">
        <h4 class="card-title mb-3"><i class="fas fa-gavel me-1"></i> Historique (${amendes.size()})</h4>
        <table class="table table-bordered table-striped datatable" style="width:100%">
            <thead class="table-light">
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
                                    <span class="badge bg-success"><i class="fas fa-check me-1"></i>Payée</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge bg-danger"><i class="fas fa-times me-1"></i>Impayée</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td class="text-center">
                            <c:if test="${a.statutPaiement == 'IMPAYEE'}">
                                <form action="${ctx}/membre/amendes/payer"
                                      method="post" style="display:inline"
                                      onsubmit="return confirm('Confirmer le paiement de ${a.montant} FCFA ?');">
                                    <input type="hidden" name="id" value="${a.id}">
                                    <button type="submit" class="btn btn-success btn-sm">
                                        <i class="fas fa-credit-card me-1"></i> Payer
                                    </button>
                                <input type="hidden" name="_csrf" value="${csrfToken}"/>
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
