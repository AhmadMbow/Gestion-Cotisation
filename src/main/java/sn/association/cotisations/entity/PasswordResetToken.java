package sn.association.cotisations.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "password_reset_token",
       indexes = @Index(name = "idx_prt_token", columnList = "token", unique = true))
public class PasswordResetToken implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "membre_numero", nullable = false)
    private Membre membre;

    @Column(nullable = false, length = 80, unique = true)
    private String token;

    @Column(name = "expire_at", nullable = false)
    private LocalDateTime expireAt;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    public PasswordResetToken() {
    }

    public PasswordResetToken(Membre membre, String token, LocalDateTime expireAt) {
        this.membre = membre;
        this.token = token;
        this.expireAt = expireAt;
    }

    public boolean isUsable() {
        return usedAt == null && LocalDateTime.now().isBefore(expireAt);
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Membre getMembre() { return membre; }
    public void setMembre(Membre membre) { this.membre = membre; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public LocalDateTime getExpireAt() { return expireAt; }
    public void setExpireAt(LocalDateTime expireAt) { this.expireAt = expireAt; }

    public LocalDateTime getUsedAt() { return usedAt; }
    public void setUsedAt(LocalDateTime usedAt) { this.usedAt = usedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PasswordResetToken)) return false;
        PasswordResetToken t = (PasswordResetToken) o;
        return id != null && id.equals(t.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
