package ru.practicum.shareit.booking;

import jakarta.validation.ValidationException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.RequestStates;


@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createBooking(@RequestHeader("X-Sharer-User-Id") long bookerId,
                                                @RequestBody @Valid @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                BookingRequestDto bookingRequestDto) {
        if (bookingRequestDto.getStart().isAfter(bookingRequestDto.getEnd())) {
            throw new ValidationException("Окончание бронирования должно быть позже начала бронирования.");
        }
        return bookingClient.createBooking(bookerId, bookingRequestDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@RequestHeader("X-Sharer-User-Id") long bookerId,
                                                 @PathVariable("bookingId") long bookingId,
                                                 @RequestParam("approved") Boolean approved) {

        return bookingClient.approveBooking(bookerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") long bookerId,
                                             @PathVariable("bookingId") long bookingId) {
        return bookingClient.getBooking(bookerId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingsCurrentUserWithState(@RequestHeader("X-Sharer-User-Id") long bookerId,
                                                                  @RequestParam(value = "state", defaultValue = "ALL")
                                                                  RequestStates state) {
        return bookingClient.getBookingsCurrentUserWithState(bookerId, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsByOwner(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                                     @RequestParam(value = "state", defaultValue = "ALL")
                                                     RequestStates state) {
        return bookingClient.getBookingsByOwner(ownerId, state);
    }

}
