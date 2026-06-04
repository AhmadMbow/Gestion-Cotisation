<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Reçu N°${cotisation.id} | Gestion Cotisations</title>
    <link href="${ctx}/assets/upzet/css/bootstrap.min.css" rel="stylesheet" type="text/css">
    <link href="${ctx}/assets/plugins/fontawesome-free/css/all.min.css" rel="stylesheet" type="text/css">
    <link href="${ctx}/assets/upzet/css/app.min.css" rel="stylesheet" type="text/css">
    <style>
        body { background:#f4f6f9; }
        .recu-box { max-width: 800px; margin: 30px auto; }
        @media print {
            .no-print { display:none !important; }
            body { background:white; }
        }
    </style>
</head>
<body>
<div class="recu-box">

    <div class="text-end mb-3 no-print">
        <button onclick="window.print()" class="btn btn-primary">
            <i class="fas fa-print me-1"></i> Imprimer
        </button>
        <a href="?id=${cotisation.id}&format=pdf" target="_blank" class="btn btn-danger">
            <i class="fas fa-file-pdf me-1"></i> Télécharger PDF
        </a>
        <a href="#" onclick="fermerRecu(); return false;" class="btn btn-light">
            <i class="fas fa-times me-1"></i> Fermer
        </a>
    </div>

    <div class="card">
        <div class="card-body p-4">
            <div class="row">
                <div class="col-12">
                    <h4 class="d-flex justify-content-between align-items-center">
                        <span><i class="fas fa-hand-holding-usd me-1"></i> Gestion Cotisations</span>
                        <small class="text-muted">Date : <c:out value="${cotisation.datePaiement}"/></small>
                    </h4>
                    <hr>
                </div>
            </div>

            <div class="row">
                <div class="col-sm-4">
                    <strong>Émis par</strong>
                    <address class="mt-2">
                        <strong>Association — Gestion Cotisations</strong><br>
                        Dakar, Sénégal<br>
                        Email : contact@asso.sn<br>
                    </address>
                </div>
                <div class="col-sm-4">
                    <strong>Pour</strong>
                    <address class="mt-2">
                        <strong>${cotisation.membre.prenom} ${cotisation.membre.nom}</strong><br>
                        ${cotisation.membre.email}<br>
                        Adhérent depuis : ${cotisation.membre.dateAdhesion}
                    </address>
                </div>
                <div class="col-sm-4">
                    <b>Reçu N° ${cotisation.id}</b><br>
                    <b>Période :</b> ${cotisation.mois}/${cotisation.annee}<br>
                    <b>Mode :</b> ${cotisation.modePaiement}<br>
                    <b>Statut :</b>
                    <c:choose>
                        <c:when test="${cotisation.statut == 'PAYE'}">
                            <span class="badge bg-success">Payé</span>
                        </c:when>
                        <c:otherwise>
                            <span class="badge bg-warning">${cotisation.statut}</span>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

            <div class="row mt-4">
                <div class="col-12 table-responsive">
                    <table class="table table-bordered">
                        <thead class="table-light">
                            <tr>
                                <th>Désignation</th>
                                <th class="text-end">Montant</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td>Cotisation mensuelle — ${cotisation.mois}/${cotisation.annee}</td>
                                <td class="text-end"><fmt:formatNumber value="${cotisation.montant}"/> FCFA</td>
                            </tr>
                            <tr>
                                <th>TOTAL</th>
                                <th class="text-end"><fmt:formatNumber value="${cotisation.montant}"/> FCFA</th>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>

            <div class="row mt-4">
                <div class="col-12 text-center">
                    <p class="text-muted mb-0">Merci pour votre participation à la vie de l'association !</p>
                    <small class="text-muted">Document généré le ${cotisation.datePaiement} — Reçu N°${cotisation.id}</small>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
    function fermerRecu() {
        // Tente de fermer l'onglet (fonctionne s'il a été ouvert par script)
        window.open('', '_self');
        window.close();
        // Repli : si le navigateur a refusé la fermeture, revenir à la page précédente
        setTimeout(function () {
            if (!window.closed) {
                if (document.referrer) {
                    window.location.href = document.referrer;
                } else {
                    history.back();
                }
            }
        }, 150);
    }
</script>
</body>
</html>
