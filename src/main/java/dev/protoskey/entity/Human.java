package dev.protoskey.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "human")
@Getter
@Setter
public class Human {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Имя не может быть пустым")
    @Column(name = "name", nullable = false)
    private String name;

    @Positive(message = "Возраст должен быть положительным")
    @Column(name = "age", nullable = false, columnDefinition = "bigint not null check (age > 0)")
    private long age;

    @Column(name = "birthday")
    private Date birthday;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Override
    public boolean equals(Object other) {
        return other instanceof Human that && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
