package sn.association.cotisations.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Notification affichée dans l'application (cloche).
 * Sans membre, elle est destinée aux administrateurs.
 */
@Entity
@Table(name = "notification",
       indexes = @Index(name = "idx_notification_membre", columnList = "membre_numero"))
public class Notification implements Serializable {

    /** Catégorie : l'application choisit l'icône et la couleur en fonction. */
    public enum Type { PAIEMENT, AMENDE, RAPPEL, MEMBRE, INFO }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "membre_numero")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Membre membre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Type type;

    @Column(nullable = false, length = 120)
    private String titre;

    @Column(nullable = false, length = 500)
    private String message;

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;

    @Column(nullable = false)
    private boolean lue;

    public Notification() {
    }

    public Notification(Membre membre, Type type, String titre, String message) {
        this.membre = membre;
        this.type = type;
        this.titre = titre;
        this.message = message.length() > 500 ? message.substring(0, 500) : message;
        this.dateCreation = LocalDateTime.now();
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Membre getMembre() { return membre; }
    public void setMembre(Membre membre) { this.membre = membre; }

    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public boolean isLue() { return lue; }
    public void setLue(boolean lue) { this.lue = lue; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Notification)) return false;
        Notification n = (Notification) o;
        return id != null && id.equals(n.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
