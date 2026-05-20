<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Nouvelle cotisation"/>
<c:set var="activeMenu" value="cotisations"/>

<jsp:include page="../layout/header.jsp"/>
<jsp:include page="../layout/sidebar-admin.jsp"/>

<div class="content-wrapper">

    <section class="content-header">
        <div class="container-fluid">
            <div class="row mb-2">
                <div class="col-sm-6"><h1>Enregistrer une cotisation</h1></div>
                <div class="col-sm-6">
                    <ol class="breadcrumb float-sm-right">
                        <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/admin/cotisations">Cotisations</a></li>
                        <li class="breadcrumb-item active">Nouvelle</li>
                    </ol>
                </div>
            </div>
        </div>
    </section>

    <section class="content">
        <div class="container-fluid">

            <c:if test="${not empty erreur}">
                <div class="alert alert-danger alert-dismissible">
                    <button type="button" class="close" data-dismiss="alert">&times;</button>
                    <i class="fas fa-exclamation-circle mr-1"></i> ${erreur}
                </div>
            </c:if>

            <div class="card card-primary">
                <div class="card-header">
                    <h3 class="card-title"><i class="fas fa-money-bill-wave mr-1"></i> Paiement</h3>
                </div>

                <form method="post" action="${pageContext.request.contextPath}/admin/cotisations/nouveau">
                    <div class="card-body">

                        <div class="form-group">
                            <label for="membre">Membre <span class="text-danger">*</span></label>
                            <select id="membre" name="membre" class="form-control" required>
                                <option value="">— Sélectionner —</option>
                                <c:forEach var="m" items="${membres}">
                                    <c:if test="${m.statut == 'ACTIF'}">
                                        <option value="${m.numero}" ${param.membre == m.numero ? 'selected' : ''}>
                                            ${m.prenom} ${m.nom} — ${m.email}
                                        </option>
                                    </c:if>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="form-row">
                            <div class="form-group col-md-4">
                                <label for="mois">Mois <span class="text-danger">*</span></label>
                                <select id="mois" name="mois" class="form-control" required>
                                    <c:forEach var="i" begin="1" end="12">
                                        <option value="${i}" ${i == (param.mois != null ? param.mois : moisCourant) ? 'selected' : ''}>${i}</option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="form-group col-md-4">
                                <label for="annee">Année <span class="text-danger">*</span></label>
                                <input type="number" id="annee" name="annee" class="form-control"
                                       min="2020" max="${anneeCourante + 1}"
                                       value="${param.annee != null ? param.annee : anneeCourante}" required>
                            </div>
                            <div class="form-group col-md-4">
                                <label for="montant">Montant (FCFA) <span class="text-danger">*</span></label>
                                <input type="number" id="montant" name="montant" class="form-control"
                                       min="1" step="100"
                                       value="${param.montant != null ? param.montant : montantParDefaut}" required>
                            </div>
                        </div>

                        <div class="form-group">
                            <label>Mode de paiement <span class="text-danger">*</span></label>
                            <div class="d-flex flex-wrap">
                                <c:forEach var="m" items="${modes}">
                                    <div class="icheck-primary mr-4">
                                        <input type="radio" id="mode-${m}" name="modePaiement" value="${m}"
                                               ${(param.modePaiement != null ? param.modePaiement : 'ESPECES') == m ? 'checked' : ''} required>
                                        <label for="mode-${m}">${m}</label>
                                    </div>
                                </c:forEach>
                            </div>
                        </div>
                    </div>

                    <div class="card-footer">
                        <button type="submit" class="btn btn-primary">
                            <i class="fas fa-save mr-1"></i> Enregistrer
                        </button>
                        <a href="${pageContext.request.contextPath}/admin/cotisations" class="btn btn-default">
                            <i class="fas fa-times mr-1"></i> Annuler
                        </a>
                    </div>
                </form>
            </div>
        </div>
    </section>
</div>

<jsp:include page="../layout/footer.jsp"/>
