package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.enums.RequestStates;
import ru.practicum.shareit.booking.enums.StatusEnum;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exeptions.ForbiddenException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;

/**
 * Контроллер для работы с бронированием.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final ItemService itemService;
    private final UserService userService;
    private final BookingMapper bookingMapper;

    /**
     * Метод для обработки создания бронирования вещи.
     *
     * @param bookerId         ID пользователя, который инициировал бронирование.
     * @param bookingCreateDto исходная информация для бронирования.
     * @return BookingDto с информацией о бронировании вещи.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto createBooking(@RequestHeader("X-Sharer-User-Id") long bookerId,
                                    @RequestBody @Valid @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                    BookingCreateDto bookingCreateDto) {
        User booker = userService.get(bookerId);
        Item item = itemService.getItem(bookingCreateDto.getItemId());
        if (bookingCreateDto.getStart().isAfter(bookingCreateDto.getEnd())) {
            throw new ValidationException("Окончание бронирования должно быть позже начала бронирования.");
        }
        Booking booking = bookingMapper.fromDto(bookingCreateDto, item, booker, StatusEnum.WAITING);
        booking = bookingService.create(booking);

        return bookingMapper.toDto(booking);
    }

    /**
     * Метод для подтверждения/отклонения статуса бронирования вещи. Может быть выполнено только по запросу
     * от владельца вещи.
     *
     * @param bookerId  ID пользователя, который инициировал бронирование.
     * @param bookingId ID бронирования для изменения статуса.
     * @param approved  логическое значение статуса бронирования для подтверждения/отклонения.
     * @return BookingDto с обновленной информацией о бронировании вещи.
     */
    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@RequestHeader("X-Sharer-User-Id") long bookerId,
                                     @PathVariable("bookingId") long bookingId,
                                     @RequestParam("approved") Boolean approved) {
        User booker = userService.get(bookerId);
        Booking booking = bookingService.get(bookingId);
        if (bookerId != booking.getItem().getOwner().getId()) {
            throw new ForbiddenException(String
                    .format("Пользователь - %s - не имеет права подтверждать/отклонять " +
                            "бронирование, т.к. не является владельцем бронируемой вещи",
                            booker));
        }
        if (approved) {
            booking.setStatus(StatusEnum.APPROVED);
        } else {
            booking.setStatus(StatusEnum.REJECTED);
        }
        bookingService.save(booking);
        return bookingMapper.toDto(booking);
    }

    /**
     * Метод для просмотра владельцем вещи или автором бронирования сведений о бронировании.
     *
     * @param bookerId  ID пользователя, от которого поступил запрос на просмотр сведений.
     * @param bookingId ID бронирования по которому необходимо получить информацию.
     * @return BookingDto с обновленной информацией о бронировании вещи.
     */
    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader("X-Sharer-User-Id") long bookerId,
                                 @PathVariable("bookingId") long bookingId) {
        User booker = userService.get(bookerId);
        Booking booking = bookingService.get(bookingId);
        if (bookerId != booking.getItem().getOwner().getId() && bookerId != booking.getBooker().getId()) {
            throw new ForbiddenException(String
                    .format("Пользователь - %s - не может получить информацию о бронировании, " +
                            "т.к. не является ни владельцем вещи, ни автором бронирования.", booker));
        }
        return bookingMapper.toDto(booking);
    }

    /**
     * Параметризованный метод для просмотра списка всех бронирований текущего пользователя
     *
     * @param bookerId ID пользователя, от которого поступил запрос на просмотр сведений.
     * @param state    необязательный параметр запроса. По умолчанию равен ALL. Возможные
     *                 значения: ALL,CURRENT,PAST,FUTURE,WAITING,REJECTED
     * @return список бронирований, отсортированных по дате от более новых к более старым.
     */
    @GetMapping
    public Collection<BookingDto> getBookingsCurrentUserWithState(@RequestHeader("X-Sharer-User-Id") long bookerId,
                                                                  @RequestParam(value = "state", defaultValue = "ALL")
                                                                  RequestStates state) {
        userService.get(bookerId);
        Collection<Booking> bookings = bookingService.getBookingsCurrentUserWithState(bookerId, state);
        return bookingMapper.toDto(bookings);
    }

    /**
     * Метод для получения списка бронирований для всех вещей текущего пользователя.
     *
     * @param ownerId ID пользователя (владельца), от которого поступил запрос на просмотр сведений.
     * @param state   необязательный параметр запроса. По умолчанию равен ALL. Возможные
     *                значения: ALL,CURRENT,PAST,FUTURE,WAITING,REJECTED
     * @return список бронирований, отсортированных по дате от более новых к более старым.
     */
    @GetMapping("/owner")
    public Collection<BookingDto> getBookingsByOwner(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                                     @RequestParam(value = "state", defaultValue = "ALL")
                                                     RequestStates state) {
        userService.get(ownerId);
        Collection<Booking> bookings = bookingService.getBookingsByOwner(ownerId, state);
        return bookingMapper.toDto(bookings);
    }

}
