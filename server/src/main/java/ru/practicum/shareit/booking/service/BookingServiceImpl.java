package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.enums.RequestStates;
import ru.practicum.shareit.booking.enums.StatusEnum;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exeptions.NotFoundException;
import ru.practicum.shareit.exeptions.ResourceUnavailableException;

import java.time.LocalDateTime;
import java.util.Collection;

@Slf4j
@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;

    @Override
    public Booking create(Booking booking) {
        if (!booking.getItem().getIsAvailable()) {
            throw new ResourceUnavailableException(String
                    .format("Бронирование вещи %s не доступно", booking.getItem().getName()));
        }

        return bookingRepository.save(booking);
    }

    @Override
    public Booking get(long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("Бронирование с ID - %s не найдено.", bookingId)));
    }

    @Override
    public void save(Booking booking) {
        bookingRepository.save(booking);
    }

    @Override
    public Collection<Booking> getBookingsCurrentUserWithState(long bookerId, RequestStates state) {
        switch (state) {
            case ALL -> {
                return bookingRepository.findAllByBookerIdOrderByStartDateDesc(bookerId);
            }
            case CURRENT -> {
                return bookingRepository.findAllByBookerIdAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(bookerId,
                        LocalDateTime.now(), LocalDateTime.now());
            }
            case PAST -> {
                return bookingRepository.findAllByBookerIdAndEndDateBeforeOrderByStartDateDesc(bookerId,
                        LocalDateTime.now());
            }
            case FUTURE -> {
                return bookingRepository.findAllByBookerIdAndStartDateAfterOrderByStartDateDesc(bookerId,
                        LocalDateTime.now());
            }
            case WAITING -> {
                return bookingRepository.findAllByBookerIdAndStatusOrderByStartDateDesc(bookerId,
                        StatusEnum.WAITING);
            }
            case REJECTED -> {
                return bookingRepository.findAllByBookerIdAndStatusOrderByStartDateDesc(bookerId,
                        StatusEnum.REJECTED);
            }
            default -> throw new IllegalStateException("Unexpected value: " + state);
        }
    }

    @Override
    public Collection<Booking> getBookingsByOwner(long ownerId, RequestStates state) {
        switch (state) {
            case ALL -> {
                return bookingRepository.findAllByItemOwnerIdOrderByStartDateDesc(ownerId);
            }
            case CURRENT -> {
                return bookingRepository
                        .findAllByItemOwnerIdAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(ownerId,
                                LocalDateTime.now(), LocalDateTime.now());
            }
            case PAST -> {
                return bookingRepository.findAllByItemOwnerIdAndEndDateBeforeOrderByStartDateDesc(ownerId,
                        LocalDateTime.now());
            }
            case FUTURE -> {
                return bookingRepository.findAllByItemOwnerIdAndStartDateAfterOrderByStartDateDesc(ownerId,
                        LocalDateTime.now());
            }
            case WAITING -> {
                return bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDateDesc(ownerId,
                        StatusEnum.WAITING);
            }
            case REJECTED -> {
                return bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDateDesc(ownerId,
                        StatusEnum.REJECTED);
            }
            default -> throw new IllegalStateException("Unexpected value: " + state);
        }
    }
}
