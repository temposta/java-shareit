package ru.practicum.shareit.booking.dto;

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