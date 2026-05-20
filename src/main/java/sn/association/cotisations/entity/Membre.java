package sn.association.cotisations.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "membre", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class Membre implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "numero")
    private Integer numero;

    @Column(nullable = false, length = 80)
    private String prenom;

    @Column(nullable = false, length = 80)
    private String nom;

    @Column(nullable = false, length = 120, unique = true)
    private String email;

    @Column(name = "date_naissance")
    private LocalDate dateNaissance;

    @Column(name = "date_adhesion", nullable = false)
    private LocalDate dateAdhesion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private StatutMembre statut = StatutMembre.ACTIF;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Role role = Role.MEMBRE;

    @Column(name = "mot_de_passe", nullable = false, length = 255)
    private String motDePasse;

    @OneToMany(mappedBy = "membre", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Cotisation> cotisations = new ArrayList<>();

    @OneToMany(mappedBy = "membre", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Amende> amendes = new ArrayList<>();

    public Membre() {
    }

    public Integer getNumero() { return numero; }
    public void setNumero(Integer numero) { this.numero = numero; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDate getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(LocalDate dateNaissance) { this.dateNaissance = dateNaissance; }

    public LocalDate getDateAdhesion() { return dateAdhesion; }
    public void setDateAdhesion(LocalDate dateAdhesion) { this.dateAdhesion = dateAdhesion; }

    public StatutMembre getStatut() { return statut; }
    public void setStatut(StatutMembre statut) { this.statut = statut; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }

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
