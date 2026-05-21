<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Reçu N°${cotisation.id} | Gestion Cotisations</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/plugins/fontawesome-free/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/dist/css/adminlte.min.css">
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

    <div class="text-right mb-3 no-print">
        <button onclick="window.print()" class="btn btn-primary">
            <i class="fas fa-print mr-1"></i> Imprimer
        </button>
        <a href="?id=${cotisation.id}&format=pdf" target="_blank" class="btn btn-danger">
            <i class="fas fa-file-pdf mr-1"></i> Télécharger PDF
        </a>
        <a href="javascript:window.close()" class="btn btn-default">
            <i class="fas fa-times mr-1"></i> Fermer
        </a>
    </div>

    <div class="invoice p-3 mb-3 bg-white">
        <div class="row">
            <div class="col-12">
                <h4>
                    <i class="fas fa-hand-holding-usd mr-1"></i> Gestion Cotisations
                    <small class="float-right">Date : <c:out value="${cotisation.datePaiement}"/></small>
                </h4>
            </div>
        </div>

        <div class="row invoice-info">
            <div class="col-sm-4 invoice-col">
                <strong>Émis par</strong>
                <address>
                    <strong>Association — Gestion Cotisations</strong><br>
                    Dakar, Sénégal<br>
                    Email : contact@asso.sn<br>
                </address>
            </div>
            <div class="col-sm-4 invoice-col">
                <strong>Pour</strong>
                <address>
                    <strong>${cotisation.membre.prenom} ${cotisation.membre.nom}</strong><br>
                    ${cotisation.membre.email}<br>
                    Adhérent depuis : ${cotisation.membre.dateAdhesion}
                </address>
            </div>
            <div class="col-sm-4 invoice-col">
                <b>Reçu N° ${cotisation.id}</b><br>
                <b>Période :</b> ${cotisation.mois}/${cotisation.annee}<br>
                <b>Mode :</b> ${cotisation.modePaiement}<br>
                <b>Statut :</b>
                <c:choose>
                    <c:when test="${cotisation.statut == 'PAYE'}">
                        <span class="badge badge-success">Payé</span>
                    </c:when>
                    <c:otherwise>
                        <span class="badge badge-warning">${cotisation.statut}</span>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <div class="row mt-4">
            <div class="col-12 table-responsive">
                <table class="table table-bordered">
                    <thead class="thead-light">
                        <tr>
                            <th>Désignation</th>
                            <th class="text-right">Montant</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td>Cotisation mensuelle — ${cotisation.mois}/${cotisation.annee}</td>
                            <td class="text-right"><fmt:formatNumber value="${cotisation.montant}"/> FCFA</td>
                        </tr>
                        <tr>
                            <th>TOTAL</th>
                            <th class="text-right"><fmt:formatNumber value="${cotisation.montant}"/> FCFA</th>
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
</body>
</html>
