package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserPatchDto;
import ru.practicum.shareit.user.model.User;

public interface UserService {

    User save(User user);

    User get(long userId);

    void delete(long userId);

    User update(User currentUser, UserPatchDto userPatchDto);
}
