package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.enums.RequestStates;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exeptions.NotFoundException;
import ru.practicum.shareit.exeptions.ResourceUnavailableException;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тестирование BookingServiceImpl")
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    @DisplayName("создание бронирования вещи")
    void create() {
        Booking booking = new Booking();
        Item item = new Item();
        item.setIsAvailable(true);
        item.setName("test");
        booking.setItem(item);
        Mockito.when(bookingRepository.save(Mockito.any(Booking.class))).thenReturn(new Booking());

        Booking bookingCreated = bookingService.create(booking);
        assertNotNull(bookingCreated);
        Mockito.verify(bookingRepository).save(Mockito.any(Booking.class));

        item.setIsAvailable(false);
        ResourceUnavailableException resourceUnavailableException = assertThrows(ResourceUnavailableException.class,
                () -> bookingService.create(booking));
        assertEquals(resourceUnavailableException.getMessage(), "Бронирование вещи test не доступно");
    }

    @Test
    @DisplayName("поиск бронирования по ID")
    void get() {
        Mockito.when(bookingRepository.findById(1L)).thenReturn(Optional.of(new Booking()));
        Mockito.when(bookingRepository.findById(999L)).thenReturn(Optional.empty());
        Booking booking = bookingService.get(1L);
        assertNotNull(booking);
        Mockito.verify(bookingRepository).findById(1L);

        NotFoundException notFoundException = assertThrows(NotFoundException.class, () -> bookingService.get(999L));
        assertEquals(notFoundException.getMessage(), "Бронирование с ID - 999 не найдено.");
    }

    @Test
    @DisplayName("вызов метода репозитория при сохранении")
    void save() {
        Mockito.when(bookingRepository.save(Mockito.any(Booking.class))).thenReturn(new Booking());
        bookingService.save(new Booking());
        Mockito.verify(bookingRepository).save(Mockito.any(Booking.class));
    }

    @Test
    @DisplayName("получение списка бронирований для текущего пользователя")
    void getBookingsCurrentUserWithState() {
        Mockito.when(bookingRepository
                        .findAllByBookerIdOrderByStartDateDesc(Mockito.anyLong()))
                .thenReturn(List.of(new Booking()));
        Mockito.when(bookingRepository
                        .findAllByBookerIdAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(List.of(new Booking()));
        Mockito.when(bookingRepository
                        .findAllByBookerIdAndEndDateBeforeOrderByStartDateDesc(Mockito.anyLong(), Mockito.any()))
                .thenReturn(List.of(new Booking()));
        Mockito.when(bookingRepository
                        .findAllByBookerIdAndStartDateAfterOrderByStartDateDesc(Mockito.anyLong(), Mockito.any()))
                .thenReturn(List.of(new Booking()));
        Mockito.when(bookingRepository
                        .findAllByBookerIdAndStatusOrderByStartDateDesc(Mockito.anyLong(), Mockito.any()))
                .thenReturn(List.of(new Booking()));

        for (RequestStates state : RequestStates.values()) {
            Collection<Booking> bookings = bookingService.getBookingsCurrentUserWithState(1L, state);
            assertNotNull(bookings);
        }

        Mockito.verify(bookingRepository).findAllByBookerIdOrderByStartDateDesc(Mockito.anyLong());
        Mockito.verify(bookingRepository)
                .findAllByBookerIdAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(Mockito.anyLong(),
                        Mockito.any(), Mockito.any());
        Mockito.verify(bookingRepository).findAllByBookerIdAndEndDateBeforeOrderByStartDateDesc(Mockito.anyLong(),
                Mockito.any());
        Mockito.verify(bookingRepository).findAllByBookerIdAndStartDateAfterOrderByStartDateDesc(Mockito.anyLong(),
                Mockito.any());
        Mockito.verify(bookingRepository, Mockito.times(2))
                .findAllByBookerIdAndStatusOrderByStartDateDesc(Mockito.anyLong(),
                        Mockito.any());
    }

    @Test
    @DisplayName("получение бронирований для владельца")
    void getBookingsByOwner() {
        Mockito.when(bookingRepository.findAllByItemOwnerIdOrderByStartDateDesc(Mockito.anyLong()))
                .thenReturn(List.of(new Booking()));
        Mockito.when(bookingRepository
                .findAllByItemOwnerIdAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(Mockito.anyLong(),
                        Mockito.any(), Mockito.any())).thenReturn(List.of(new Booking()));
        Mockito.when(bookingRepository.findAllByItemOwnerIdAndEndDateBeforeOrderByStartDateDesc(Mockito.anyLong(),
                Mockito.any())).thenReturn(List.of(new Booking()));
        Mockito.when(bookingRepository.findAllByItemOwnerIdAndStartDateAfterOrderByStartDateDesc(Mockito.anyLong(),
                Mockito.any())).thenReturn(List.of(new Booking()));
        Mockito.when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDateDesc(Mockito.anyLong(),
                Mockito.any())).thenReturn(List.of(new Booking()));

        for (RequestStates state : RequestStates.values()) {
            Collection<Booking> bookings = bookingService.getBookingsByOwner(1L, state);
            assertNotNull(bookings);
        }

        Mockito.verify(bookingRepository).findAllByItemOwnerIdOrderByStartDateDesc(Mockito.anyLong());
        Mockito.verify(bookingRepository).findAllByItemOwnerIdAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(Mockito.anyLong(),
                Mockito.any(), Mockito.any());
        Mockito.verify(bookingRepository).findAllByItemOwnerIdAndEndDateBeforeOrderByStartDateDesc(Mockito.anyLong(),
                Mockito.any());
        Mockito.verify(bookingRepository).findAllByItemOwnerIdAndStartDateAfterOrderByStartDateDesc(Mockito.anyLong(),
                Mockito.any());
        Mockito.verify(bookingRepository, Mockito.times(2)).findAllByItemOwnerIdAndStatusOrderByStartDateDesc(Mockito.anyLong(),
                Mockito.any());
    }
}