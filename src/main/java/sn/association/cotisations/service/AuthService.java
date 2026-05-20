package sn.association.cotisations.service;

import sn.association.cotisations.dao.MembreDAO;
import sn.association.cotisations.entity.Membre;
import sn.association.cotisations.entity.StatutMembre;
import sn.association.cotisations.util.PasswordUtil;

import java.util.Optional;

public class AuthService {

    private final MembreDAO membreDAO;

    public AuthService() {
        this.membreDAO = new MembreDAO();
    }

    public AuthService(MembreDAO membreDAO) {
        this.membreDAO = membreDAO;
    }

    /**
     * Authentifie un membre par email + mot de passe.
     * Retourne le Membre uniquement si l'email existe, le mot de passe correspond
     * et le compte est ACTIF.
     */
    public Optional<Membre> authenticate(String email, String motDePasse) {
        if (email == null || motDePasse == null) return Optional.empty();

        Optional<Membre> opt = membreDAO.findByEmail(email.trim().toLowerCase());
        if (opt.isEmpty()) return Optional.empty();

        Membre m = opt.get();
        if (m.getStatut() != StatutMembre.ACTIF) return Optional.empty();
        if (!PasswordUtil.matches(motDePasse, m.getMotDePasse())) return Optional.empty();

        return Optional.of(m);
    }
}
