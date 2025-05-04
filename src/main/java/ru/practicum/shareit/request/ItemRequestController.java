package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

/**
 * Контроллер для работы с запросами вещей.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;
    private final UserService userService;

    /**
     * Добавление нового запроса вещи.
     * Основная часть запроса — текст запроса, в котором пользователь описывает, какая именно вещь ему нужна.
     *
     * @param requestorId          ID пользователя.
     * @param itemRequestCreateDto текст запроса.
     * @return Сущность с данными по итоговому запросу.
     */
    @PostMapping
    public ItemRequestDto addRequest(@RequestHeader("X-Sharer-User-Id") long requestorId,
                                     @RequestBody ItemRequestCreateDto itemRequestCreateDto) {
        userService.get(requestorId);
        return itemRequestService.addRequest(requestorId, itemRequestCreateDto);
    }

    /**
     * Получение списка своих запросов вместе с данными об ответах на них.
     *
     * @param requestorId ID пользователя.
     * @return Сущность с данными по итоговому запросу. Возвращаются отсортированными от более новых к более старым.
     */
    @GetMapping
    public List<ItemRequestDto> getOwnRequests(@RequestHeader("X-Sharer-User-Id") long requestorId) {
        userService.get(requestorId);
        return itemRequestService.getOwnRequests(requestorId);
    }

    /**
     * Получение списка запросов, созданных другими пользователями.
     *
     * @param requestorId ID пользователя.
     * @return Сущность с данными по итоговому запросу. Запросы сортируются по дате создания от более новых к более старым.
     */
    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader("X-Sharer-User-Id") long requestorId) {
        userService.get(requestorId);
        return itemRequestService.getAllRequests(requestorId);
    }

    /**
     * Получение данных об одном конкретном запросе вместе с данными об ответах на него в том же формате, что и в эндпоинте GET /requests. Посмотреть данные об отдельном запросе может любой пользователь.
     *
     * @param requestorId ID пользователя.
     * @param requestId   ID запроса для просмотра.
     * @return Сущность с данными по итоговому запросу. Посмотреть данные об отдельном запросе может любой пользователь.
     */
    @GetMapping("/{requestId}")
    public ItemRequestDto getRequest(@RequestHeader("X-Sharer-User-Id") long requestorId,
                                     @PathVariable("requestId") long requestId) {
        userService.get(requestorId);
        return itemRequestService.getRequest(requestorId, requestId);
    }
}
