<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Amendes"/>
<c:set var="activeMenu" value="amendes"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<jsp:include page="../layout/header.jsp"/>
<jsp:include page="../layout/sidebar-admin.jsp"/>

<div class="row">
    <div class="col-12">
        <div class="page-title-box d-sm-flex align-items-center justify-content-between">
            <h4 class="mb-sm-0 font-size-18">Gestion des amendes</h4>
            <div class="page-title-right">
                <ol class="breadcrumb m-0">
                    <li class="breadcrumb-item"><a href="${ctx}/admin/dashboard">Accueil</a></li>
                    <li class="breadcrumb-item active">Amendes</li>
                </ol>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../layout/flash-messages.jsp"/>

<%-- Bandeau d'actions principales --%>
<div class="row">
    <div class="col-md-7">
        <div class="card border border-warning">
            <div class="card-body py-3">
                <h5 class="card-title mb-1"><i class="fas fa-bolt me-1"></i> Génération automatique</h5>
                <p class="text-muted small mb-2">
                    Crée une amende de <fmt:formatNumber value="${montantStandard}"/> FCFA pour chaque
                    membre actif qui n'a pas payé sa cotisation du mois courant
                    (les membres déjà amendés ce mois sont ignorés).
                </p>
                <form method="post" action="${ctx}/admin/amendes/generer-auto"
                      onsubmit="return confirm('Générer les amendes du mois pour tous les retardataires ?');">
                    <button type="submit" class="btn btn-warning">
                        <i class="fas fa-gavel me-1"></i> Générer les amendes du mois
                    </button>
                <input type="hidden" name="_csrf" value="${csrfToken}"/>
</form>
            </div>
        </div>
    </div>
    <div class="col-md-5">
        <div class="card border border-primary">
            <div class="card-body py-3">
                <h5 class="card-title mb-1"><i class="fas fa-plus me-1"></i> Amende manuelle</h5>
                <p class="text-muted small mb-2">Créer une amende ponctuelle pour un membre précis.</p>
                <a href="${ctx}/admin/amendes/nouveau" class="btn btn-primary">
                    <i class="fas fa-plus me-1"></i> Nouvelle amende
                </a>
            </div>
        </div>
    </div>
</div>

<div class="card">
    <div class="card-body">
        <h4 class="card-title mb-3">
            <i class="fas fa-list me-1"></i>
            Toutes les amendes (${amendes.size()})
        </h4>

        <table class="table table-bordered table-striped datatable" style="width:100%">
            <thead class="table-light">
                <tr>
                    <th>#</th>
                    <th>Membre</th>
                    <th>Email</th>
                    <th>Montant</th>
                    <th>Date génération</th>
                    <th>Statut</th>
                    <th class="text-center">Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="a" items="${amendes}">
                    <tr>
                        <td>${a.id}</td>
                        <td>${a.membre.prenom} ${a.membre.nom}</td>
                        <td>${a.membre.email}</td>
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
                        <td class="text-center" style="white-space:nowrap">
                            <c:if test="${a.statutPaiement == 'IMPAYEE'}">
                                <form action="${ctx}/admin/amendes/payer"
                                      method="post" style="display:inline"
                                      onsubmit="return confirm('Marquer cette amende comme payée ?');">
                                    <input type="hidden" name="id" value="${a.id}">
                                    <button type="submit" class="btn btn-success btn-sm" title="Marquer payée">
                                        <i class="fas fa-check"></i>
                                    </button>
                                <input type="hidden" name="_csrf" value="${csrfToken}"/>
</form>
                            </c:if>
                            <form action="${ctx}/admin/amendes/delete"
                                  method="post" style="display:inline"
                                  onsubmit="return confirm('Supprimer définitivement cette amende ?');">
                                <input type="hidden" name="id" value="${a.id}">
                                <button type="submit" class="btn btn-danger btn-sm" title="Supprimer">
                                    <i class="fas fa-trash"></i>
                                </button>
                            <input type="hidden" name="_csrf" value="${csrfToken}"/>
</form>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
</div>

<jsp:include page="../layout/footer.jsp"/>
