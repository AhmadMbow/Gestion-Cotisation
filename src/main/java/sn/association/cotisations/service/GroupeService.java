package sn.association.cotisations.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import sn.association.cotisations.dao.GroupeDAO;
import sn.association.cotisations.dao.MembreDAO;
import sn.association.cotisations.entity.Compte;
import sn.association.cotisations.entity.Forfait;
import sn.association.cotisations.entity.Groupe;
import sn.association.cotisations.entity.Membre;
import sn.association.cotisations.entity.Role;
import sn.association.cotisations.entity.StatutMembre;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Groupes de cotisation : création (selon le forfait), adhésion par code d'invitation,
 * réglages, et contrôle du nombre de membres.
 */
@ApplicationScoped
public class GroupeService {

    /** Sans 0/O ni 1/I/L : le code se dicte et se recopie sans erreur. */
    private static final String ALPHABET = "ABCDEFGHJKMNPQRSTUVWXYZ23456789";
    private static final int LONGUEUR_CODE = 6;

    @Inject GroupeDAO groupeDAO;
    @Inject MembreDAO membreDAO;
    @Inject NotificationService notificationService;

    private final SecureRandom random = new SecureRandom();

    public Optional<Groupe> trouver(Integer id) {
        return groupeDAO.findById(id);
    }

    /** Crée un groupe dont le compte devient propriétaire et administrateur. */
    public Membre creer(Compte proprietaire, String nom, BigDecimal montantCotisation, BigDecimal montantAmende) {
        if (nom == null || nom.isBlank()) throw new IllegalArgumentException("Donnez un nom au groupe.");
        if (nom.trim().length() > 80) throw new IllegalArgumentException("Le nom du groupe est trop long.");
        ParametreService.verifier(montantCotisation, montantAmende);

        Forfait f = proprietaire.forfaitEffectif();
        long possedes = groupeDAO.countParProprietaire(proprietaire.getId());
        if (possedes >= f.getMaxGroupes()) {
            Forfait conseille = Forfait.conseille((int) possedes + 1, 1);
            throw new LimiteForfaitException("Votre forfait " + f.getLibelle() + " permet de créer "
                    + f.getMaxGroupes() + " groupe" + (f.getMaxGroupes() > 1 ? "s" : "")
                    + ". Passez au forfait " + conseille.getLibelle() + " pour en créer davantage.", conseille);
        }

        Groupe g = new Groupe();
        g.setNom(nom.trim());
        g.setMontantCotisation(montantCotisation);
        g.setMontantAmende(montantAmende);
        g.setProprietaire(proprietaire);
        g.setCodeInvitation(codeLibre());
        g = groupeDAO.save(g);
        return membreDAO.save(new Membre(proprietaire, g, Role.ADMIN, LocalDate.now()));
    }

    /** Rejoint un groupe actif avec son code d'invitation. */
    public Membre rejoindre(Compte compte, String code) {
        String c = code == null ? "" : code.replaceAll("[\\s-]", "").toUpperCase();
        Groupe g = groupeDAO.findByCode(c).filter(Groupe::isActif)
                .orElseThrow(() -> new IllegalArgumentException("Aucun groupe ne correspond à ce code."));
        Optional<Membre> existant = membreDAO.findByCompteEtGroupe(compte.getId(), g.getId());
        if (existant.isPresent()) {
            throw new IllegalStateException(existant.get().getStatut() == StatutMembre.ACTIF
                    ? "Vous faites déjà partie de « " + g.getNom() + " »."
                    : "Votre adhésion à « " + g.getNom() + " » a été désactivée par un administrateur.");
        }
        try {
            verifierPlace(g);
        } catch (LimiteForfaitException e) {
            throw new IllegalStateException("« " + g.getNom() + " » est complet. Demandez à son administrateur "
                    + "de passer à un forfait supérieur.");
        }
        Membre m = membreDAO.save(new Membre(compte, g, Role.MEMBRE, LocalDate.now()));
        notificationService.membreRejoint(m);
        return m;
    }

    /** Lève {@link LimiteForfaitException} si le groupe a atteint le nombre de membres de son forfait. */
    public void verifierPlace(Groupe g) {
        Forfait f = g.forfait();
        long actifs = membreDAO.countActifs(g.getId());
        if (actifs >= f.getMaxMembresParGroupe()) {
            Forfait conseille = Forfait.conseille(1, (int) actifs + 1);
            throw new LimiteForfaitException("Le forfait " + f.getLibelle() + " est limité à "
                    + f.getMaxMembresParGroupe() + " membres par groupe. Passez au forfait "
                    + conseille.getLibelle() + " pour en ajouter.", conseille);
        }
    }

    public Groupe modifier(Groupe g, String nom, BigDecimal montantCotisation, BigDecimal montantAmende) {
        if (nom == null || nom.isBlank()) throw new IllegalArgumentException("Donnez un nom au groupe.");
        if (nom.trim().length() > 80) throw new IllegalArgumentException("Le nom du groupe est trop long.");
        ParametreService.verifier(montantCotisation, montantAmende);
        g.setNom(nom.trim());
        g.setMontantCotisation(montantCotisation);
        g.setMontantAmende(montantAmende);
        return groupeDAO.save(g);
    }

    /** Nouveau code d'invitation (l'ancien ne fonctionne plus). */
    public Groupe nouveauCode(Groupe g) {
        g.setCodeInvitation(codeLibre());
        return groupeDAO.save(g);
    }

    private String codeLibre() {
        for (int essai = 0; essai < 20; essai++) {
            String code = genererCode(random);
            if (groupeDAO.findByCode(code).isEmpty()) return code;
        }
        throw new IllegalStateException("Impossible de générer un code d'invitation. Réessayez.");
    }

    public static String genererCode(SecureRandom random) {
        StringBuilder sb = new StringBuilder(LONGUEUR_CODE);
        for (int i = 0; i < LONGUEUR_CODE; i++) sb.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        return sb.toString();
    }
}
