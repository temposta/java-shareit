package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.user.dto.UserPatchDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private final ObjectMapper mapper = new ObjectMapper();

    private MockMvc mvc;

    private User user;

    private UserPatchDto userPatchDto;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(userController)
                .build();

        user = new User();
        user.setId(1L);
        user.setName("test");
        user.setEmail("test@test.com");

        userPatchDto = new UserPatchDto();
        userPatchDto.setName("new name");
        userPatchDto.setEmail("newtest@test.com");
    }

    @Test
    void createUser() throws Exception {
        when(userService.save(any())).thenReturn(user);

        mvc.perform(post("/users")
                .content(mapper.writeValueAsString(user))
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(user.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(user.getName()), String.class));

    }

    @Test
    void getUser() throws Exception {
        when(userService.get(anyLong())).thenReturn(user);

        mvc.perform(get("/users/{id}", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(user.getName()), String.class));
    }

    @Test
    void deleteUser() throws Exception {
        mvc.perform(delete("/users/{id}", "1")
        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void updateUser() throws Exception {
        when(userService.get(anyLong())).thenReturn(user);
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setName("new name");
        updatedUser.setEmail("newtest@test.com");
        when(userService.update(user, userPatchDto)).thenReturn(updatedUser);

        mvc.perform(patch("/users/{id}", "1")
        .content(mapper.writeValueAsString(userPatchDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(updatedUser.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(updatedUser.getName()), String.class))
                .andExpect(jsonPath("$.email", is(updatedUser.getEmail())));
    }
}