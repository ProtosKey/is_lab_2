package dev.protoskey.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "app_user")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Логин не может быть пустым")
    @Column(name = "login", nullable = false, unique = true)
    private String login;

    @NotBlank(message = "Пароль не может быть пустым")
    @NotNull(message = "Необходимо задать пароль")
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "user_level", nullable = false)
    private UserLevel userLevel;
}
