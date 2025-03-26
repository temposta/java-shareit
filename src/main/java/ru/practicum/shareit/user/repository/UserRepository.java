package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserPatchDto;

import java.util.Optional;

public interface UserRepository {

    User create(User user);

    Optional<User> getById(long userId);

    void delete(long userId);

    User update(long userId, UserPatchDto userPatchDto);
}
