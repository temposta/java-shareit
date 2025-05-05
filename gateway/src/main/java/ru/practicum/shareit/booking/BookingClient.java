package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.RequestStates;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }


    public ResponseEntity<Object> createBooking(long bookerId, BookingRequestDto bookingRequestDto) {
        return post("", bookerId, bookingRequestDto);
    }

    public ResponseEntity<Object> approveBooking(long bookerId, long bookingId, Boolean approved) {
        return patch("/" + bookingId + "?approved=" + approved, bookerId);
    }

    public ResponseEntity<Object> getBooking(long bookerId, long bookingId) {
        return get("/" + bookingId, bookerId);
    }

    public ResponseEntity<Object> getBookingsCurrentUserWithState(long bookerId, RequestStates state) {
        Map<String, Object> params = Map.of(
                "state", state.name()
        );
        return get("?state={state}", bookerId, params);
    }

    public ResponseEntity<Object> getBookingsByOwner(long ownerId, RequestStates state) {
        Map<String, Object> params = Map.of(
                "state", state.name()
        );
        return get("/owner?state={state}", ownerId, params);
    }
}
