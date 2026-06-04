package sn.association.cotisations.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Paramètres globaux de l'association, configurables par l'administrateur.
 * Table à ligne unique (id = 1) : montant standard de la cotisation mensuelle
 * et montant standard d'une amende de retard.
 */
@Entity
@Table(name = "parametre")
public class Parametre implements Serializable {

    /** Identifiant fixe : il n'existe qu'une seule ligne de paramètres. */
    @Id
    private Integer id = 1;

    @Column(name = "montant_cotisation", nullable = false, precision = 10, scale = 2)
    private BigDecimal montantCotisation = new BigDecimal("5000");

    @Column(name = "montant_amende", nullable = false, precision = 10, scale = 2)
    private BigDecimal montantAmende = new BigDecimal("2000");

    public Parametre() {
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public BigDecimal getMontantCotisation() { return montantCotisation; }
    public void setMontantCotisation(BigDecimal montantCotisation) { this.montantCotisation = montantCotisation; }

    public BigDecimal getMontantAmende() { return montantAmende; }
    public void setMontantAmende(BigDecimal montantAmende) { this.montantAmende = montantAmende; }
}
