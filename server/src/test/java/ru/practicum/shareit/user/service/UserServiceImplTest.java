package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exeptions.ForbiddenException;
import ru.practicum.shareit.user.dto.UserPatchDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тестирование UserServiceImpl")
class UserServiceImplTest {

    @Mock
    UserRepository mockUserRepository;

    @InjectMocks
    UserServiceImpl userService;

    @Test
    @DisplayName("создание пользователя")
    void save() {
        when(mockUserRepository.save(Mockito.any(User.class))).thenReturn(new User());

        User user = userService.save(new User());
        assertNotNull(user);
        verify(mockUserRepository, times(1))
                .save(Mockito.any(User.class));
    }

    @Test
    @DisplayName("запрос пользователя с существующим ID")
    void getByValidId() {
        Long id = 1L;
        when(mockUserRepository.findById(id)).thenReturn(Optional.of(new User()));

        User user = userService.get(id);
        assertNotNull(user);
        verify(mockUserRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("запрос пользователя с НЕ существующим ID")
    void getByIncorrectId() {
        Long id = 1L;
        when(mockUserRepository.findById(id)).thenReturn(Optional.empty());

        ForbiddenException exception = assertThrows(ForbiddenException.class, () -> userService.get(id));

        assertEquals("Пользователь с id " + id + " не найден.", exception.getMessage());
        verify(mockUserRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("вызов метода репозитория при удалении пользователя")
    void delete() {
        userService.delete(1L);
        verify(mockUserRepository, times(1)).deleteById(anyLong());
    }

    @Test
    @DisplayName("обновление имени пользователя")
    void updateName() {
        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setName("name");
        currentUser.setEmail("email@email.com");

        UserPatchDto userPatchDto = new UserPatchDto();
        userPatchDto.setName("newName");

        when(mockUserRepository.save(currentUser)).thenReturn(currentUser);
        userService.update(currentUser, userPatchDto);
        verify(mockUserRepository, times(1)).save(currentUser);

        assertEquals(currentUser.getName(), "newName");
        assertEquals(currentUser.getEmail(), "email@email.com");
    }

    @Test
    @DisplayName("обновление адреса электронной почты")
    void updateEmail() {
        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setName("name");
        currentUser.setEmail("email@email.com");

        UserPatchDto userPatchDto = new UserPatchDto();
        userPatchDto.setEmail("newemail@email.com");

        when(mockUserRepository.save(currentUser)).thenReturn(currentUser);
        userService.update(currentUser, userPatchDto);
        verify(mockUserRepository, times(1)).save(currentUser);

        assertEquals(currentUser.getName(), "name");
        assertEquals(currentUser.getEmail(), "newemail@email.com");
    }

    @Test
    @DisplayName("обновление имени пользователя и адреса электронной почты")
    void updateNameAndEmail() {
        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setName("name");
        currentUser.setEmail("email@email.com");

        UserPatchDto userPatchDto = new UserPatchDto();
        userPatchDto.setName("newName");
        userPatchDto.setEmail("newemail@email.com");

        when(mockUserRepository.save(currentUser)).thenReturn(currentUser);
        userService.update(currentUser, userPatchDto);
        verify(mockUserRepository, times(1)).save(currentUser);

        assertEquals(currentUser.getName(), "newName");
        assertEquals(currentUser.getEmail(), "newemail@email.com");
    }

}