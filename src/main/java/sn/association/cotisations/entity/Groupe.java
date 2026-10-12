package sn.association.cotisations.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Groupe de cotisation (association, tontine, famille…). Chaque groupe a ses membres,
 * ses montants et un code d'invitation pour le rejoindre depuis l'application.
 *
 * Le groupe n° {@link #HISTORIQUE} est l'association d'origine : c'est lui que gère
 * l'interface web d'administration.
 */
@Entity
@Table(name = "groupe",
       uniqueConstraints = @UniqueConstraint(name = "uk_groupe_code", columnNames = "code_invitation"))
public class Groupe implements Serializable {

    /** Association d'origine (données d'avant le passage en multi-groupes). */
    public static final int HISTORIQUE = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 80)
    private String nom;

    @Column(name = "code_invitation", nullable = false, length = 12)
    private String codeInvitation;

    @Column(name = "montant_cotisation", nullable = false, precision = 10, scale = 2)
    private BigDecimal montantCotisation = new BigDecimal("5000");

    @Column(name = "montant_amende", nullable = false, precision = 10, scale = 2)
    private BigDecimal montantAmende = new BigDecimal("2000");

    /** Compte qui a créé le groupe : son forfait fixe les limites du groupe. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "proprietaire_id")
    private Compte proprietaire;

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation = LocalDateTime.now();

    @Column(nullable = false)
    private boolean actif = true;

    public Groupe() {
    }

    /** Forfait du propriétaire (gratuit si le groupe n'a pas de propriétaire). */
    public Forfait forfait() {
        return proprietaire == null ? Forfait.GRATUIT : proprietaire.forfaitEffectif();
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getCodeInvitation() { return codeInvitation; }
    public void setCodeInvitation(String codeInvitation) { this.codeInvitation = codeInvitation; }

    public BigDecimal getMontantCotisation() { return montantCotisation; }
    public void setMontantCotisation(BigDecimal montantCotisation) { this.montantCotisation = montantCotisation; }

    public BigDecimal getMontantAmende() { return montantAmende; }
    public void setMontantAmende(BigDecimal montantAmende) { this.montantAmende = montantAmende; }

    public Compte getProprietaire() { return proprietaire; }
    public void setProprietaire(Compte proprietaire) { this.proprietaire = proprietaire; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public boolean isActif() { return actif; }
    public void setActif(boolean actif) { this.actif = actif; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Groupe g)) return false;
        return id != null && id.equals(g.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
