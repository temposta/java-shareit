package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserPatchDto;
import ru.practicum.shareit.user.model.User;

public interface UserService {

    User create(User user);

    User get(long userId);

    void delete(long userId);

    User update(long userId, UserPatchDto userPatchDto);
}
