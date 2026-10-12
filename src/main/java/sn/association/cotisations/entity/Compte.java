package sn.association.cotisations.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Compte de connexion d'une personne (e-mail + mot de passe), indépendant des groupes.
 * Une même personne peut être membre de plusieurs groupes (voir {@link Membre})
 * et posséder ses propres groupes selon son {@link Forfait}.
 */
@Entity
@Table(name = "compte", uniqueConstraints = @UniqueConstraint(name = "uk_compte_email", columnNames = "email"))
public class Compte implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 80)
    private String prenom;

    @Column(nullable = false, length = 80)
    private String nom;

    @Column(nullable = false, length = 120)
    private String email;

    /** Numéro de téléphone (facultatif) — utilisé pour le paiement mobile. */
    @Column(length = 30)
    private String telephone;

    @Column(name = "date_naissance")
    private LocalDate dateNaissance;

    @Column(name = "mot_de_passe", nullable = false, length = 255)
    private String motDePasse;

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 12)
    private Forfait forfait = Forfait.GRATUIT;

    /** Dernier jour couvert par l'abonnement payant (null pour le forfait gratuit). */
    @Column(name = "forfait_expire_le")
    private LocalDate forfaitExpireLe;

    public Compte() {
    }

    /** Forfait réellement appliqué : un abonnement expiré redevient gratuit. */
    public Forfait forfaitEffectif() {
        if (forfait == null || forfait == Forfait.GRATUIT) return Forfait.GRATUIT;
        if (forfaitExpireLe == null || forfaitExpireLe.isBefore(LocalDate.now())) return Forfait.GRATUIT;
        return forfait;
    }

    public String getNomComplet() { return prenom + " " + nom; }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public LocalDate getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(LocalDate dateNaissance) { this.dateNaissance = dateNaissance; }

    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public Forfait getForfait() { return forfait; }
    public void setForfait(Forfait forfait) { this.forfait = forfait; }

    public LocalDate getForfaitExpireLe() { return forfaitExpireLe; }
    public void setForfaitExpireLe(LocalDate forfaitExpireLe) { this.forfaitExpireLe = forfaitExpireLe; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Compte c)) return false;
        return id != null && id.equals(c.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
