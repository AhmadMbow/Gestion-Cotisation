<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Amendes"/>
<c:set var="activeMenu" value="amendes"/>

<jsp:include page="../layout/header.jsp"/>
<jsp:include page="../layout/sidebar-admin.jsp"/>

<div class="content-wrapper">

    <section class="content-header">
        <div class="container-fluid">
            <div class="row mb-2">
                <div class="col-sm-6"><h1>Gestion des amendes</h1></div>
                <div class="col-sm-6">
                    <ol class="breadcrumb float-sm-right">
                        <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/admin/dashboard">Accueil</a></li>
                        <li class="breadcrumb-item active">Amendes</li>
                    </ol>
                </div>
            </div>
        </div>
    </section>

    <section class="content">
        <div class="container-fluid">

            <jsp:include page="../layout/flash-messages.jsp"/>

            <%-- Bandeau d'actions principales --%>
            <div class="row mb-3">
                <div class="col-md-7">
                    <div class="card card-warning card-outline">
                        <div class="card-body py-3">
                            <h5 class="card-title mb-1"><i class="fas fa-bolt mr-1"></i> Génération automatique</h5>
                            <p class="text-muted small mb-2">
                                Crée une amende de <fmt:formatNumber value="${montantStandard}"/> FCFA pour chaque
                                membre actif qui n'a pas payé sa cotisation du mois courant
                                (les membres déjà amendés ce mois sont ignorés).
                            </p>
                            <form method="post" action="${pageContext.request.contextPath}/admin/amendes/generer-auto"
                                  onsubmit="return confirm('Générer les amendes du mois pour tous les retardataires ?');">
                                <button type="submit" class="btn btn-warning">
                                    <i class="fas fa-gavel mr-1"></i> Générer les amendes du mois
                                </button>
                            </form>
                        </div>
                    </div>
                </div>
                <div class="col-md-5">
                    <div class="card card-primary card-outline">
                        <div class="card-body py-3">
                            <h5 class="card-title mb-1"><i class="fas fa-plus mr-1"></i> Amende manuelle</h5>
                            <p class="text-muted small mb-2">Créer une amende ponctuelle pour un membre précis.</p>
                            <a href="${pageContext.request.contextPath}/admin/amendes/nouveau" class="btn btn-primary">
                                <i class="fas fa-plus mr-1"></i> Nouvelle amende
                            </a>
                        </div>
                    </div>
                </div>
            </div>

            <div class="card">
                <div class="card-header">
                    <h3 class="card-title">
                        <i class="fas fa-list mr-1"></i>
                        Toutes les amendes (${amendes.size()})
                    </h3>
                </div>

                <div class="card-body">
                    <table class="table table-bordered table-striped datatable" style="width:100%">
                        <thead class="thead-light">
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
                                                <span class="badge badge-success"><i class="fas fa-check mr-1"></i>Payée</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge badge-danger"><i class="fas fa-times mr-1"></i>Impayée</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td class="text-center" style="white-space:nowrap">
                                        <c:if test="${a.statutPaiement == 'IMPAYEE'}">
                                            <form action="${pageContext.request.contextPath}/admin/amendes/payer"
                                                  method="post" style="display:inline"
                                                  onsubmit="return confirm('Marquer cette amende comme payée ?');">
                                                <input type="hidden" name="id" value="${a.id}">
                                                <button type="submit" class="btn btn-success btn-xs" title="Marquer payée">
                                                    <i class="fas fa-check"></i>
                                                </button>
                                            </form>
                                        </c:if>
                                        <form action="${pageContext.request.contextPath}/admin/amendes/delete"
                                              method="post" style="display:inline"
                                              onsubmit="return confirm('Supprimer définitivement cette amende ?');">
                                            <input type="hidden" name="id" value="${a.id}">
                                            <button type="submit" class="btn btn-danger btn-xs" title="Supprimer">
                                                <i class="fas fa-trash"></i>
                                            </button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty amendes}">
                                <tr><td colspan="7" class="text-center text-muted">Aucune amende enregistrée.</td></tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </section>
</div>

<jsp:include page="../layout/footer.jsp"/>
