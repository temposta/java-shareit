package ru.practicum.shareit.user.repository;

import jakarta.validation.ValidationException;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exeptions.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserPatchDto;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class InMemoryUserRepositoryImpl implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();
    private long currentUserId = 0L;

    @Override
    public User create(User user) {
        checkEmail(user.getEmail());
        user.setId(nextId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> getById(long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public void delete(long userId) {
        User user = users.remove(userId);
        if (user == null) throw new NotFoundException(String
                .format("Пользователь с id %s не существует", userId));
    }

    @Override
    public User update(long userId, UserPatchDto userPatchDto) {
        User user = users.get(userId);
        if (userPatchDto.getName() != null) user.setName(userPatchDto.getName());
        if (userPatchDto.getEmail() != null) {
            checkEmail(userPatchDto.getEmail());
            user.setEmail(userPatchDto.getEmail());
        }
        return user;
    }

    private void checkEmail(String email) {
        users.forEach((key, value) -> {
            if (value.getEmail().equals(email)) throw new ValidationException(String
                    .format("Указанный Email %s уже используется", email));
        });
    }

    private Long nextId() {
        return ++currentUserId;
    }
}
