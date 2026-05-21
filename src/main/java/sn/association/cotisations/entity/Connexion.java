package sn.association.cotisations.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "connexion",
       indexes = @Index(name = "idx_connexion_membre", columnList = "membre_numero"))
public class Connexion implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "membre_numero", nullable = false)
    private Membre membre;

    @Column(name = "date_connexion", nullable = false)
    private LocalDateTime dateConnexion;

    @Column(length = 45)
    private String ip;

    @Column(name = "user_agent", length = 255)
    private String userAgent;

    public Connexion() {
    }

    public Connexion(Membre membre, LocalDateTime dateConnexion, String ip, String userAgent) {
        this.membre = membre;
        this.dateConnexion = dateConnexion;
        this.ip = ip;
        this.userAgent = userAgent;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Membre getMembre() { return membre; }
    public void setMembre(Membre membre) { this.membre = membre; }

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
