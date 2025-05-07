package ru.practicum.shareit.booking.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.enums.StatusEnum;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Преобразователь для набора сущностей бронирования
 */
@Component
public class BookingMapper {

    /**
     * Преобразование из BookingCreateDto в Booking
     *
     * @param dto    параметр класса BookingCreateDto для преобразования.
     * @param item   бронируемая вещь.
     * @param booker пользователь, создавший бронь.
     * @param status статус бронирования.
     * @return Новый объект Booking.
     */
    public Booking fromDto(BookingCreateDto dto, Item item, User booker, StatusEnum status) {
        Booking booking = new Booking();
        booking.setStartDate(dto.getStart());
        booking.setEndDate(dto.getEnd());
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(status);
        return booking;
    }

    /**
     * Преобразование из Booking в BookingDto.
     *
     * @param booking Исходный объект для преобразования.
     * @return объект BookingDto.
     */
    public BookingDto toDto(Booking booking) {
        BookingDto dto = new BookingDto();
        dto.setId(booking.getId());
        dto.setStart(booking.getStartDate());
        dto.setEnd(booking.getEndDate());
        dto.setItem(booking.getItem());
        dto.setBooker(booking.getBooker());
        dto.setStatus(booking.getStatus());
        return dto;
    }

    /**
     * Преобразование списка из Booking в список из BookingDto.
     *
     * @param bookings Исходный список для преобразования.
     * @return список BookingDto.
     */
    public Collection<BookingDto> toDto(Collection<Booking> bookings) {
        Collection<BookingDto> dtoCollection = new ArrayList<>();
        for (Booking booking : bookings) {
            dtoCollection.add(toDto(booking));
        }
        return dtoCollection;
    }
}
