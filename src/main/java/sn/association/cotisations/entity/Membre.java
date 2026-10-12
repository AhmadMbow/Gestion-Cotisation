package sn.association.cotisations.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Adhésion d'un {@link Compte} à un {@link Groupe} : rôle, statut et date d'adhésion
 * propres au groupe. Les cotisations et amendes sont rattachées à l'adhésion.
 *
 * L'identité (nom, e-mail, mot de passe…) appartient au compte : les accesseurs
 * correspondants y sont délégués, ce qui garde le code et les vues existants inchangés.
 */
@Entity
@Table(name = "membre",
       uniqueConstraints = @UniqueConstraint(name = "uk_membre_compte_groupe",
               columnNames = {"compte_id", "groupe_id"}))
public class Membre implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "numero")
    private Integer numero;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "compte_id")
    private Compte compte;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "groupe_id")
    private Groupe groupe;

    @Column(name = "date_adhesion", nullable = false)
    private LocalDate dateAdhesion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private StatutMembre statut = StatutMembre.ACTIF;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Role role = Role.MEMBRE;

    @OneToMany(mappedBy = "membre", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Cotisation> cotisations = new ArrayList<>();

    @OneToMany(mappedBy = "membre", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Amende> amendes = new ArrayList<>();

    public Membre() {
    }

    public Membre(Compte compte, Groupe groupe, Role role, LocalDate dateAdhesion) {
        this.compte = compte;
        this.groupe = groupe;
        this.role = role;
        this.dateAdhesion = dateAdhesion;
    }

    public Integer getNumero() { return numero; }
    public void setNumero(Integer numero) { this.numero = numero; }

    public Compte getCompte() { return compte; }
    public void setCompte(Compte compte) { this.compte = compte; }

    public Groupe getGroupe() { return groupe; }
    public void setGroupe(Groupe groupe) { this.groupe = groupe; }

    public boolean appartientAuGroupe(int groupeId) {
        return groupe != null && groupe.getId() != null && groupe.getId() == groupeId;
    }

    // --- Identité : déléguée au compte ---

    public String getPrenom() { return compte.getPrenom(); }
    public void setPrenom(String prenom) { compte.setPrenom(prenom); }

    public String getNom() { return compte.getNom(); }
    public void setNom(String nom) { compte.setNom(nom); }

    public String getEmail() { return compte.getEmail(); }
    public void setEmail(String email) { compte.setEmail(email); }

    public String getTelephone() { return compte.getTelephone(); }
    public void setTelephone(String telephone) { compte.setTelephone(telephone); }

    public LocalDate getDateNaissance() { return compte.getDateNaissance(); }
    public void setDateNaissance(LocalDate dateNaissance) { compte.setDateNaissance(dateNaissance); }

    public String getMotDePasse() { return compte.getMotDePasse(); }
    public void setMotDePasse(String motDePasse) { compte.setMotDePasse(motDePasse); }

    // --- Adhésion ---

    public LocalDate getDateAdhesion() { return dateAdhesion; }
    public void setDateAdhesion(LocalDate dateAdhesion) { this.dateAdhesion = dateAdhesion; }

    public StatutMembre getStatut() { return statut; }
    public void setStatut(StatutMembre statut) { this.statut = statut; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public List<Cotisation> getCotisations() { return cotisations; }
    public void setCotisations(List<Cotisation> cotisations) { this.cotisations = cotisations; }

    public List<Amende> getAmendes() { return amendes; }
    public void setAmendes(List<Amende> amendes) { this.amendes = amendes; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Membre)) return false;
        Membre m = (Membre) o;
        return numero != null && numero.equals(m.numero);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(numero);
    }
}
