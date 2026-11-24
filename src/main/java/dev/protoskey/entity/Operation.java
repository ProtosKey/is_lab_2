package dev.protoskey.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "operation")
@Getter
@Setter
public class Operation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private OperationType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OperationStatus status;

    @Column(name = "object_count")
    private Integer objectCount;

    @Column(name = "message", columnDefinition = "text")
    private String message;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    private void fillCreatedAt() {
        createdAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Operation that && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
