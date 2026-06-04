<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Paramètres"/>
<c:set var="activeMenu" value="parametres"/>

<jsp:include page="../layout/header.jsp"/>
<jsp:include page="../layout/sidebar-admin.jsp"/>

<div class="content-wrapper">

    <section class="content-header">
        <div class="container-fluid">
            <div class="row mb-2">
                <div class="col-sm-6"><h1>Paramètres de l'association</h1></div>
                <div class="col-sm-6">
                    <ol class="breadcrumb float-sm-right">
                        <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/admin/dashboard">Accueil</a></li>
                        <li class="breadcrumb-item active">Paramètres</li>
                    </ol>
                </div>
            </div>
        </div>
    </section>

    <section class="content">
        <div class="container-fluid">

            <jsp:include page="../layout/flash-messages.jsp"/>

            <c:if test="${not empty erreur}">
                <div class="alert alert-danger alert-dismissible">
                    <button type="button" class="close" data-dismiss="alert">&times;</button>
                    <i class="fas fa-exclamation-circle mr-1"></i> ${erreur}
                </div>
            </c:if>

            <div class="row">
                <div class="col-md-8 col-lg-6">
                    <div class="card card-primary">
                        <div class="card-header">
                            <h3 class="card-title"><i class="fas fa-sliders-h mr-1"></i> Montants standards</h3>
                        </div>

                        <form method="post" action="${pageContext.request.contextPath}/admin/parametres">
                            <input type="hidden" name="_csrf" value="${csrfToken}">
                            <div class="card-body">

                                <div class="form-group">
                                    <label for="montantCotisation">
                                        Montant de la cotisation mensuelle (FCFA) <span class="text-danger">*</span>
                                    </label>
                                    <input type="number" id="montantCotisation" name="montantCotisation"
                                           class="form-control" min="1" step="100"
                                           value="${parametre.montantCotisation}" required>
                                    <small class="form-text text-muted">
                                        Sert de montant par défaut lors des paiements et au calcul du montant dû par les retardataires.
                                    </small>
                                </div>

                                <div class="form-group">
                                    <label for="montantAmende">
                                        Montant d'une amende de retard (FCFA) <span class="text-danger">*</span>
                                    </label>
                                    <input type="number" id="montantAmende" name="montantAmende"
                                           class="form-control" min="0" step="100"
                                           value="${parametre.montantAmende}" required>
                                    <small class="form-text text-muted">
                                        Appliqué lors de la génération automatique des amendes du mois.
                                    </small>
                                </div>
                            </div>

                            <div class="card-footer">
                                <button type="submit" class="btn btn-primary">
                                    <i class="fas fa-save mr-1"></i> Enregistrer
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>

        </div>
    </section>
</div>

<jsp:include page="../layout/footer.jsp"/>
