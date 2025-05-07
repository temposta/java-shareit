package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.dto.UserPatchDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

/**
 * Контроллер для User
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    /**
     * Метод для создания нового пользователя
     *
     * @param user Данные для создания пользователя
     * @return созданный пользователь
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody @Valid User user) {
        log.info("Creating user: {}", user);
        user = userService.save(user);
        log.info("Created user: {}", user);
        return user;
    }

    /**
     * Метод для получения пользователя по ID
     *
     * @param userId ID пользователя для получения
     * @return пользователь с указанным ID
     */
    @GetMapping("/{userId}")
    public User getUser(@PathVariable long userId) {
        log.info("Getting user: {}", userId);
        User user = userService.get(userId);
        log.info("Got user: {}", user);
        return user;
    }

    /**
     * Метод для удаления пользователя с указанным ID
     *
     * @param userId ID пользователя для удаления
     */
    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable long userId) {
        log.info("Deleting user: {}", userId);
        userService.delete(userId);
        log.info("Deleted user: {}", userId);
    }

    /**
     * Метод для внесения изменений в данные пользователя
     *
     * @param userId       ID пользователя для обновления данных
     * @param userPatchDto данные, подлежащие изменению
     * @return пользователь с новыми данными после обновления
     */
    @PatchMapping("{userId}")
    public User updateUser(@PathVariable long userId, @RequestBody @Valid UserPatchDto userPatchDto) {
        log.info("Updating user: {}", userId);
        User currentUser = userService.get(userId);
        User updated = userService.update(currentUser, userPatchDto);
        log.info("Updated user: {}", updated);
        return updated;
    }
}
