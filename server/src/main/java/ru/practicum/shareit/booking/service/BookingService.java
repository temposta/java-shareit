package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.enums.RequestStates;
import ru.practicum.shareit.booking.model.Booking;

import java.util.Collection;

public interface BookingService {
    Booking create(Booking booking);

    Booking get(long bookingId);

    void save(Booking booking);

    Collection<Booking> getBookingsCurrentUserWithState(long bookerId, RequestStates state);

    Collection<Booking> getBookingsByOwner(long ownerId, RequestStates state);
}
