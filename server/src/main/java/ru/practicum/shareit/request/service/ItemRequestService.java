package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto addRequest(long requestorId, ItemRequestCreateDto itemRequestService);

    List<ItemRequestDto> getOwnRequests(long requestorId);

    List<ItemRequestDto> getAllRequests(long requestorId);

    ItemRequestDto getRequest(long requestorId, long requestId);
}
