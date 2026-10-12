package sn.association.cotisations.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import sn.association.cotisations.dao.CompteDAO;
import sn.association.cotisations.entity.Compte;
import sn.association.cotisations.util.PasswordUtil;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Optional;

/** Comptes de connexion : inscription libre depuis l'application, authentification, profil. */
@ApplicationScoped
public class CompteService {

    @Inject CompteDAO compteDAO;

    public Optional<Compte> trouver(Integer id) {
        return compteDAO.findById(id);
    }

    public Optional<Compte> parEmail(String email) {
        return email == null ? Optional.empty() : compteDAO.findByEmail(normaliser(email));
    }

    /** E-mail + mot de passe corrects → le compte. */
    public Optional<Compte> authentifier(String email, String motDePasse) {
        if (email == null || motDePasse == null) return Optional.empty();
        return parEmail(email).filter(c -> PasswordUtil.matches(motDePasse, c.getMotDePasse()));
    }

    /** Inscription depuis l'application (forfait gratuit). */
    public Compte inscrire(String prenom, String nom, String email, String telephone, String motDePasse) {
        valider(prenom, nom, email, motDePasse);
        return creer(prenom, nom, email, telephone, null, motDePasse);
    }

    /**
     * Crée un compte. Sans mot de passe, un mot de passe aléatoire est attribué :
     * la personne choisit le sien via le lien de l'e-mail de bienvenue.
     */
    public Compte creer(String prenom, String nom, String email, String telephone,
                        LocalDate dateNaissance, String motDePasse) {
        if (motDePasse == null || motDePasse.isBlank()) motDePasse = motDePasseAleatoire();
        valider(prenom, nom, email, motDePasse);
        String e = normaliser(email);
        if (compteDAO.findByEmail(e).isPresent()) {
            throw new IllegalArgumentException("Un compte existe déjà avec cet e-mail.");
        }
        Compte c = new Compte();
        c.setPrenom(prenom.trim());
        c.setNom(nom.trim());
        c.setEmail(e);
        c.setTelephone(nettoyer(telephone));
        c.setDateNaissance(dateNaissance);
        c.setMotDePasse(PasswordUtil.hash(motDePasse));
        return compteDAO.save(c);
    }

    /** Modification de son propre profil. */
    public Compte modifierProfil(Integer id, String prenom, String nom, String email, String telephone,
                                 LocalDate dateNaissance) {
        Compte c = compteDAO.findById(id).orElseThrow(() -> new IllegalArgumentException("Compte introuvable."));
        if (prenom == null || prenom.isBlank()) throw new IllegalArgumentException("Le prénom est obligatoire.");
        if (nom == null || nom.isBlank()) throw new IllegalArgumentException("Le nom est obligatoire.");
        if (email != null && !email.isBlank()) {
            String e = normaliser(email);
            if (!e.contains("@") || e.length() < 5) throw new IllegalArgumentException("Email invalide.");
            if (!e.equals(c.getEmail()) && compteDAO.findByEmail(e).isPresent()) {
                throw new IllegalArgumentException("Un autre compte utilise déjà cet e-mail.");
            }
            c.setEmail(e);
        }
        c.setPrenom(prenom.trim());
        c.setNom(nom.trim());
        c.setTelephone(nettoyer(telephone));
        c.setDateNaissance(dateNaissance);
        return compteDAO.save(c);
    }

    public Compte enregistrer(Compte c) {
        return compteDAO.save(c);
    }

    static String normaliser(String email) {
        return email.trim().toLowerCase();
    }

    private static String nettoyer(String v) {
        return (v == null || v.isBlank()) ? null : v.trim();
    }

    private static String motDePasseAleatoire() {
        byte[] bytes = new byte[18];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    static void valider(String prenom, String nom, String email, String motDePasse) {
        if (prenom == null || prenom.isBlank()) throw new IllegalArgumentException("Le prénom est obligatoire.");
        if (nom == null || nom.isBlank()) throw new IllegalArgumentException("Le nom est obligatoire.");
        if (email == null || email.isBlank()) throw new IllegalArgumentException("L'email est obligatoire.");
        if (!email.contains("@") || email.length() < 5) throw new IllegalArgumentException("Email invalide.");
        if (motDePasse == null || motDePasse.length() < 6) {
            throw new IllegalArgumentException("Le mot de passe doit faire au moins 6 caractères.");
        }
    }
}
