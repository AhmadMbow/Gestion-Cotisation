package sn.association.cotisations.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import sn.association.cotisations.dao.MembreDAO;
import sn.association.cotisations.entity.Groupe;
import sn.association.cotisations.entity.Membre;
import sn.association.cotisations.entity.StatutMembre;

import java.util.Optional;

@ApplicationScoped
public class AuthService {

    @Inject CompteService compteService;
    @Inject MembreDAO membreDAO;

    /**
     * Connexion au site web, réservé au groupe historique : le compte doit être correct
     * et membre ACTIF de ce groupe.
     */
    public Optional<Membre> authenticate(String email, String motDePasse) {
        return compteService.authentifier(email, motDePasse)
                .flatMap(c -> membreDAO.findByCompteEtGroupe(c.getId(), Groupe.HISTORIQUE))
                .filter(m -> m.getStatut() == StatutMembre.ACTIF);
    }
}
