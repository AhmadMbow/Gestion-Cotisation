package sn.association.cotisations.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import sn.association.cotisations.dao.MembreDAO;
import sn.association.cotisations.entity.Compte;
import sn.association.cotisations.entity.Groupe;
import sn.association.cotisations.entity.Membre;
import sn.association.cotisations.entity.Role;
import sn.association.cotisations.entity.StatutMembre;
import sn.association.cotisations.util.PasswordUtil;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Membres (adhésions) d'un groupe. Les variantes sans groupe servent l'interface web,
 * qui gère le groupe historique.
 */
@ApplicationScoped
public class MembreService {

    @Inject MembreDAO membreDAO = new MembreDAO();
    @Inject CompteService compteService;
    @Inject GroupeService groupeService;
    @Inject ParametreService parametreService;

    /** Résultat d'un ajout : le membre, et si son compte vient d'être créé (e-mail de bienvenue). */
    public record Ajout(Membre membre, boolean compteCree) {
    }

    public List<Membre> listerTous() {
        return membreDAO.findAll();
    }

    public Optional<Membre> trouver(Integer numero) {
        return membreDAO.findById(numero);
    }

    /** Formulaire web : ajout au groupe historique, mot de passe obligatoire. */
    public Membre creer(String prenom, String nom, String email, LocalDate dateNaissance,
                        Role role, String motDePasseClair) {
        CompteService.valider(prenom, nom, email, motDePasseClair);
        return ajouter(parametreService.get(), prenom, nom, email, null, dateNaissance, null, role,
                motDePasseClair).membre();
    }

    /**
     * Ajoute une personne au groupe. Si un compte existe déjà avec cet e-mail, il est simplement
     * rattaché au groupe (son identité n'est pas modifiée) ; sinon le compte est créé.
     * Sans date d'adhésion, c'est la date du jour.
     */
    public Ajout ajouter(Groupe g, String prenom, String nom, String email, String telephone,
                         LocalDate dateNaissance, LocalDate dateAdhesion, Role role, String motDePasseClair) {
        if (email == null || email.isBlank()) throw new IllegalArgumentException("L'email est obligatoire.");
        Optional<Compte> existant = compteService.parEmail(email);
        if (existant.isPresent()
                && membreDAO.findByCompteEtGroupe(existant.get().getId(), g.getId()).isPresent()) {
            throw new IllegalArgumentException("Cette personne fait déjà partie du groupe.");
        }
        groupeService.verifierPlace(g);
        Compte c = existant.orElseGet(() ->
                compteService.creer(prenom, nom, email, telephone, dateNaissance, motDePasseClair));
        Membre m = new Membre(c, g, role != null ? role : Role.MEMBRE,
                dateAdhesion != null ? dateAdhesion : LocalDate.now());
        return new Ajout(membreDAO.save(m), existant.isEmpty());
    }

    /**
     * Formulaire web : modification complète (identité comprise) d'un membre du groupe historique.
     * Le mot de passe n'est changé que s'il est non vide.
     */
    public Membre modifier(Integer numero, String prenom, String nom, String email,
                           LocalDate dateNaissance, Role role, String nouveauMotDePasse) {
        Membre m = membreDAO.findById(numero)
                .orElseThrow(() -> new IllegalArgumentException("Membre introuvable."));
        compteService.modifierProfil(m.getCompte().getId(), prenom, nom, email, m.getTelephone(), dateNaissance);
        if (nouveauMotDePasse != null && !nouveauMotDePasse.isBlank()) {
            if (nouveauMotDePasse.length() < 6) {
                throw new IllegalArgumentException("Le mot de passe doit faire au moins 6 caractères.");
            }
            Compte c = compteService.trouver(m.getCompte().getId()).orElseThrow();
            c.setMotDePasse(PasswordUtil.hash(nouveauMotDePasse));
            compteService.enregistrer(c);
        }
        return modifierAdhesion(numero, role, null);
    }

    /**
     * Ce qu'un administrateur de groupe peut changer : le rôle et la date d'adhésion.
     * L'identité appartient au compte de la personne.
     */
    public Membre modifierAdhesion(Integer numero, Role role, LocalDate dateAdhesion) {
        Membre m = membreDAO.findById(numero)
                .orElseThrow(() -> new IllegalArgumentException("Membre introuvable."));
        if (role != null) m.setRole(role);
        if (dateAdhesion != null) m.setDateAdhesion(dateAdhesion);
        return membreDAO.save(m);
    }

    /** Retire la personne du groupe (son compte et ses autres groupes ne changent pas). */
    public void supprimer(Integer numero) {
        membreDAO.delete(numero);
    }

    public Membre basculerStatut(Integer numero) {
        Membre m = membreDAO.findById(numero)
                .orElseThrow(() -> new IllegalArgumentException("Membre introuvable."));
        if (m.getStatut() == StatutMembre.INACTIF) groupeService.verifierPlace(m.getGroupe());
        m.setStatut(m.getStatut() == StatutMembre.ACTIF ? StatutMembre.INACTIF : StatutMembre.ACTIF);
        return membreDAO.save(m);
    }
}
