package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exeptions.ExceptionsHandler;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @Mock
    private BookingService bookingService;
    @Mock
    private ItemService itemService;
    @Mock
    private UserService userService;
    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private BookingController bookingController;

    MockMvc mockMvc;

    ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    BookingCreateDto bookingCreateDto;
    BookingDto bookingDto;
    User user;
    Booking booking;
    Item bookingItem;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(bookingController)
                .setControllerAdvice(new ExceptionsHandler())
                .build();

        bookingCreateDto = new BookingCreateDto();
        bookingDto = new BookingDto();
        user = new User();
        booking = new Booking();
        bookingItem = new Item();
        bookingItem.setId(1L);
    }

    @Test
    void createBooking() throws Exception {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        bookingCreateDto.setStart(start);
        bookingCreateDto.setEnd(end);
        bookingCreateDto.setItemId(1L);

        when(userService.get(anyLong())).thenReturn(user);
        when(itemService.getItem(anyLong())).thenReturn(bookingItem);
        when(bookingMapper.fromDto(any(), any(), any(), any())).thenReturn(booking);
        when(bookingService.create(booking)).thenReturn(booking);
        when(bookingMapper.toDto(booking)).thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                .header("X-Sharer-User-Id",1)
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(bookingCreateDto)))
                .andExpect(status().isCreated());

        bookingCreateDto.setStart(end);
        bookingCreateDto.setEnd(start);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id",1)
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingCreateDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error")
                        .value("Окончание бронирования должно быть позже начала бронирования."));
    }

    @Test
    void approveBooking() throws Exception {
        user.setId(5L);
        Item item = new Item();
        item.setId(1L);
        item.setOwner(user);
        booking.setItem(item);

        when(userService.get(anyLong())).thenReturn(user);
        when(bookingService.get(anyLong())).thenReturn(booking);
        when(bookingMapper.toDto(booking)).thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/{bookingId}", 1)
                .header("X-Sharer-User-Id",1)
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .param("approved", "true"))
                .andExpect(status().isForbidden());

        mockMvc.perform(patch("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id",5)
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("approved", "true"))
                .andExpect(status().isOk());

        mockMvc.perform(patch("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id",5)
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("approved", "false"))
                .andExpect(status().isOk());
    }

    @Test
    void getBooking() throws Exception {
        user.setId(5L);
        Item item = new Item();
        item.setId(1L);
        item.setOwner(user);
        booking.setItem(item);
        booking.setBooker(user);


        when(userService.get(anyLong())).thenReturn(user);
        when(bookingService.get(anyLong())).thenReturn(booking);
        when(bookingMapper.toDto(booking)).thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id",1)
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id",5)
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getBookingsCurrentUserWithState() throws Exception {
        when(bookingService.getBookingsCurrentUserWithState(anyLong(),any())).thenReturn(List.of(booking));
        when(bookingMapper.toDto(anyList())).thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id",5)
                        .param("state", "FUTURE")
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getBookingsByOwner() throws Exception {
        when(bookingService.getBookingsByOwner(anyLong(),any())).thenReturn(List.of(booking));
        when(bookingMapper.toDto(anyList())).thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id",5)
                        .param("state", "FUTURE")
                        .characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}