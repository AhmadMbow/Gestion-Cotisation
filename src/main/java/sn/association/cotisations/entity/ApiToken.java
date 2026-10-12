package sn.association.cotisations.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Jeton de connexion de l'application mobile.
 * Seule l'empreinte SHA-256 du jeton est stockée : une fuite de la base
 * ne permet pas de se connecter à la place d'un membre.
 */
@Entity
@Table(name = "api_token",
       indexes = @Index(name = "idx_api_token_hash", columnList = "token_hash", unique = true))
public class ApiToken implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compte_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Compte compte;

    @Column(name = "token_hash", nullable = false, length = 64, unique = true)
    private String tokenHash;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "expire_at", nullable = false)
    private LocalDateTime expireAt;

    @Column(length = 255)
    private String appareil;

    public ApiToken() {
    }

    public ApiToken(Compte compte, String tokenHash, LocalDateTime expireAt, String appareil) {
        this.compte = compte;
        this.tokenHash = tokenHash;
        this.createdAt = LocalDateTime.now();
        this.expireAt = expireAt;
        this.appareil = appareil;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expireAt);
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Compte getCompte() { return compte; }
    public void setCompte(Compte compte) { this.compte = compte; }

    public String getTokenHash() { return tokenHash; }
    public void setTokenHash(String tokenHash) { this.tokenHash = tokenHash; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getExpireAt() { return expireAt; }
    public void setExpireAt(LocalDateTime expireAt) { this.expireAt = expireAt; }

    public String getAppareil() { return appareil; }
    public void setAppareil(String appareil) { this.appareil = appareil; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ApiToken)) return false;
        ApiToken t = (ApiToken) o;
        return id != null && id.equals(t.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
