package sn.association.cotisations.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "amende",
       indexes = @Index(name = "idx_amende_membre", columnList = "membre_numero"))
public class Amende implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "membre_numero", nullable = false)
    private Membre membre;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal montant;

    @Column(name = "date_generation", nullable = false)
    private LocalDate dateGeneration;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut_paiement", nullable = false, length = 10)
    private StatutAmende statutPaiement = StatutAmende.IMPAYEE;

    public Amende() {
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Membre getMembre() { return membre; }
    public void setMembre(Membre membre) { this.membre = membre; }

    public BigDecimal getMontant() { return montant; }
    public void setMontant(BigDecimal montant) { this.montant = montant; }

    public LocalDate getDateGeneration() { return dateGeneration; }
    public void setDateGeneration(LocalDate dateGeneration) { this.dateGeneration = dateGeneration; }

    public StatutAmende getStatutPaiement() { return statutPaiement; }
    public void setStatutPaiement(StatutAmende statut) { this.statutPaiement = statut; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Amende)) return false;
        Amende a = (Amende) o;
        return id != null && id.equals(a.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
