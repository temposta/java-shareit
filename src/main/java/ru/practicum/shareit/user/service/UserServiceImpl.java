package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeptions.ForbiddenException;
import ru.practicum.shareit.user.dto.UserPatchDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public User get(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ForbiddenException("Пользователь с id " + userId + " не найден."));
    }

    @Override
    public void delete(long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public User update(User currentUser, UserPatchDto userPatchDto) {
        if (userPatchDto.getName() != null) {
            currentUser.setName(userPatchDto.getName());
        }
        if (userPatchDto.getEmail() != null) {
            currentUser.setEmail(userPatchDto.getEmail());
        }
        return userRepository.save(currentUser);
    }
}
