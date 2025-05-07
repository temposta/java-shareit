package ru.practicum.shareit.booking;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.RequestStates;
import ru.practicum.shareit.booking.enums.StatusEnum;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest(classes = ShareItServer.class)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BookingIntegrationTest {
    @Autowired
    private final EntityManager entityManager;
    @Autowired
    private BookingController bookingController;
    @Autowired
    private ItemController itemController;
    @Autowired
    private UserController userController;

    @Test
    void testBooking() {
        User user = new User();
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
        userController.createUser(user);
        ItemCreateDto itemCreateDto = new ItemCreateDto();
        itemCreateDto.setName("Item 1");
        itemCreateDto.setDescription("Item 1");
        itemCreateDto.setAvailable(true);
        ItemDto itemDto = itemController.createItem(user.getId(), itemCreateDto);

        BookingCreateDto bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setItemId(itemDto.getId());
        bookingCreateDto.setStart(LocalDateTime.now().plusDays(1));
        bookingCreateDto.setEnd(LocalDateTime.now().plusDays(2));

        bookingController.createBooking(user.getId(), bookingCreateDto);
        TypedQuery<Booking> query = entityManager.createQuery("select b from Booking b where b.booker.id = :userId", Booking.class);
        query.setParameter("userId", user.getId());
        List<Booking> bookings = query.getResultList();
        assertThat(bookings.size(), equalTo(1));
        Booking booking = bookings.getFirst();
        assertThat(booking.getId(), notNullValue());
        assertThat(booking.getStatus(), equalTo(StatusEnum.WAITING));

        Collection<BookingDto> bookingDtos = bookingController.getBookingsByOwner(user.getId(), RequestStates.ALL);
        assertThat(bookingDtos.size(), equalTo(1));

    }

}
