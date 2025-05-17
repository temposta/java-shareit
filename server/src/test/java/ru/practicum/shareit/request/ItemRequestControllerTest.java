package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {

    @Mock
    private ItemRequestService itemRequestService;

    @Mock
    private UserService userService;

    @InjectMocks
    private ItemRequestController itemRequestController;

    MockMvc mockMvc;

    ObjectMapper mapper = new ObjectMapper();

    ItemRequestCreateDto itemRequestCreateDto;

    ItemRequestDto itemRequestDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(itemRequestController)
                .build();

        itemRequestCreateDto = new ItemRequestCreateDto();
        itemRequestDto = new ItemRequestDto();
        itemRequestCreateDto.setDescription("test");
        itemRequestDto.setId(1L);
        itemRequestDto.setDescription("test");
        itemRequestDto.setItems(List.of());
        itemRequestDto.setCreated(Timestamp.from(Instant.now()));
    }

    @Test
    void addRequest() throws Exception {
        when(userService.get(anyLong())).thenReturn(new User());
        when(itemRequestService.addRequest(1L, itemRequestCreateDto)).thenReturn(itemRequestDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(mapper.writeValueAsString(itemRequestCreateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription()), String.class));
    }

    @Test
    void getOwnRequests() throws Exception {
        when(userService.get(anyLong())).thenReturn(new User());
        when(itemRequestService.getOwnRequests(1L)).thenReturn(List.of(itemRequestDto));

        mockMvc.perform(get("/requests")
                .header("X-Sharer-User-Id", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription()), String.class));
    }

    @Test
    void getAllRequests() throws Exception {
        when(userService.get(anyLong())).thenReturn(new User());
        when(itemRequestService.getAllRequests(1L)).thenReturn(List.of(itemRequestDto));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription()), String.class));
    }

    @Test
    void getRequest() throws Exception {
        when(userService.get(anyLong())).thenReturn(new User());
        when(itemRequestService.getRequest(1L, 1L)).thenReturn(itemRequestDto);

        mockMvc.perform(get("/requests/{requestId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(mapper.writeValueAsString(itemRequestCreateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription()), String.class));
    }
}