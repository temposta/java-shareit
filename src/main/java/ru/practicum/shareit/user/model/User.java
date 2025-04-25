package ru.practicum.shareit.user.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Основной объект для пользователя.
 */
@Getter
@Setter
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(name = "uq_user_email", columnNames = {"email"})
})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 255)
    @NotBlank(message = "Имя пользователя не должно быть пустым")
    @Column(name = "name", nullable = false)
    private String name;

    @Size(max = 512)
    @NotBlank(message = "Электронная почта должна быть указана")
    @Email(message = "Адрес электронной почты не соответствует формату")
    @Column(name = "email", nullable = false, length = 512)
    private String email;
}