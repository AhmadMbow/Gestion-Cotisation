package sn.association.cotisations.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import sn.association.cotisations.dao.MembreDAO;
import sn.association.cotisations.entity.Membre;
import sn.association.cotisations.entity.Role;
import sn.association.cotisations.entity.StatutMembre;
import sn.association.cotisations.util.PasswordUtil;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class MembreService {

    @Inject MembreDAO membreDAO = new MembreDAO();

    public List<Membre> listerTous() {
        return membreDAO.findAll();
    }

    public Optional<Membre> trouver(Integer numero) {
        return membreDAO.findById(numero);
    }

    /**
     * Crée un nouveau membre. Lève IllegalArgumentException si l'email existe déjà
     * ou si un champ obligatoire est manquant.
     */
    public Membre creer(String prenom, String nom, String email, LocalDate dateNaissance,
                        Role role, String motDePasseClair) {
        valider(prenom, nom, email, motDePasseClair);

        String emailNorm = email.trim().toLowerCase();
        if (membreDAO.findByEmail(emailNorm).isPresent()) {
            throw new IllegalArgumentException("Un membre avec cet email existe déjà.");
        }

        Membre m = new Membre();
        m.setPrenom(prenom.trim());
        m.setNom(nom.trim());
        m.setEmail(emailNorm);
        m.setDateNaissance(dateNaissance);
        m.setDateAdhesion(LocalDate.now());
        m.setStatut(StatutMembre.ACTIF);
        m.setRole(role != null ? role : Role.MEMBRE);
        m.setMotDePasse(PasswordUtil.hash(motDePasseClair));
        return membreDAO.save(m);
    }

    /**
     * Met à jour un membre existant. Le mot de passe n'est changé que s'il est non-vide.
     */
    public Membre modifier(Integer numero, String prenom, String nom, String email,
                           LocalDate dateNaissance, Role role, String nouveauMotDePasse) {
        Membre m = membreDAO.findById(numero)
                .orElseThrow(() -> new IllegalArgumentException("Membre introuvable."));

        if (prenom == null || prenom.isBlank()) throw new IllegalArgumentException("Le prénom est obligatoire.");
        if (nom == null || nom.isBlank()) throw new IllegalArgumentException("Le nom est obligatoire.");
        if (email == null || email.isBlank()) throw new IllegalArgumentException("L'email est obligatoire.");

        String emailNorm = email.trim().toLowerCase();
        if (!emailNorm.equals(m.getEmail())) {
            // L'email a changé — vérifier l'unicité
            if (membreDAO.findByEmail(emailNorm).isPresent()) {
                throw new IllegalArgumentException("Un autre membre utilise déjà cet email.");
            }
        }

        m.setPrenom(prenom.trim());
        m.setNom(nom.trim());
        m.setEmail(emailNorm);
        m.setDateNaissance(dateNaissance);
        if (role != null) m.setRole(role);
        if (nouveauMotDePasse != null && !nouveauMotDePasse.isBlank()) {
            m.setMotDePasse(PasswordUtil.hash(nouveauMotDePasse));
        }
        return membreDAO.save(m);
    }

    public void supprimer(Integer numero) {
        membreDAO.delete(numero);
    }

    public Membre basculerStatut(Integer numero) {
        Membre m = membreDAO.findById(numero)
                .orElseThrow(() -> new IllegalArgumentException("Membre introuvable."));
        m.setStatut(m.getStatut() == StatutMembre.ACTIF ? StatutMembre.INACTIF : StatutMembre.ACTIF);
        return membreDAO.save(m);
    }

    private void valider(String prenom, String nom, String email, String motDePasse) {
        if (prenom == null || prenom.isBlank()) throw new IllegalArgumentException("Le prénom est obligatoire.");
        if (nom == null || nom.isBlank()) throw new IllegalArgumentException("Le nom est obligatoire.");
        if (email == null || email.isBlank()) throw new IllegalArgumentException("L'email est obligatoire.");
        if (!email.contains("@") || email.length() < 5) throw new IllegalArgumentException("Email invalide.");
        if (motDePasse == null || motDePasse.length() < 6) {
            throw new IllegalArgumentException("Le mot de passe doit faire au moins 6 caractères.");
        }
    }
}
