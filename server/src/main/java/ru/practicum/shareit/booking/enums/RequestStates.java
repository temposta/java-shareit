package ru.practicum.shareit.booking.enums;

/**
 * Допустимый перечень значений для параметра state
 * в endpoint - GET /bookings?state={state}
 */
public enum RequestStates {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED
}