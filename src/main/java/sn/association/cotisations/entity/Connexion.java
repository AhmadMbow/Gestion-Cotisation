package sn.association.cotisations.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Entity
@Table(name = "connexion",
       indexes = @Index(name = "idx_connexion_compte", columnList = "compte_id"))
public class Connexion implements Serializable {

    private static final DateTimeFormatter AFFICHAGE = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compte_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Compte compte;

    @Column(name = "date_connexion", nullable = false)
    private LocalDateTime dateConnexion;

    @Column(length = 45)
    private String ip;

    @Column(name = "user_agent", length = 255)
    private String userAgent;

    public Connexion() {
    }

    public Connexion(Compte compte, LocalDateTime dateConnexion, String ip, String userAgent) {
        this.compte = compte;
        this.dateConnexion = dateConnexion;
        this.ip = ip;
        this.userAgent = userAgent;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    /** Date lisible pour les pages web (« 12/10/2026 14:00:00 »). */
    public String getDateAffichee() {
        return dateConnexion == null ? "" : dateConnexion.format(AFFICHAGE);
    }

    public Compte getCompte() { return compte; }
    public void setCompte(Compte compte) { this.compte = compte; }

    public LocalDateTime getDateConnexion() { return dateConnexion; }
    public void setDateConnexion(LocalDateTime dateConnexion) { this.dateConnexion = dateConnexion; }

    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Connexion)) return false;
        Connexion c = (Connexion) o;
        return id != null && id.equals(c.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
