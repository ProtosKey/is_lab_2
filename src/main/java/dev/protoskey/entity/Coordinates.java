package dev.protoskey.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import lombok.Getter;
import lombok.Setter;
import java.util.Objects;

@Entity
@Table(name = "coordinates")
@Getter
@Setter
public class Coordinates {
    private static final int MAX_VALUE = 974;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Max(value = MAX_VALUE, message = "Абсцисса не может быть больше {value}")
    @Column(name = "x", nullable = false, columnDefinition = "integer not null check (x <= " + MAX_VALUE + ")")
    private int x;

    @Column(name = "y", nullable = false)
    private int y;

    @Override
    public boolean equals(Object other) {
        return other instanceof Coordinates that && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
