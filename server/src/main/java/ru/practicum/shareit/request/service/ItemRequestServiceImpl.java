package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestWithItems;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.repository.ItemRequestWithItemsRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestMapper itemRequestMapper;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestWithItemsRepository itemRequestWithItemsRepository;

    @Override
    public ItemRequestDto addRequest(long requestorId, ItemRequestCreateDto itemRequestCreateDto) {
        ItemRequest itemRequest = itemRequestMapper.toItemRequest(requestorId, itemRequestCreateDto);
        itemRequestRepository.save(itemRequest);
        return itemRequestMapper.toItemRequestDto(itemRequest);
    }

    @Override
    public List<ItemRequestDto> getOwnRequests(long requestorId) {
        List<ItemRequestWithItems> itemRequestWithItems = itemRequestWithItemsRepository
                .findAllByRequestorIdOrderByCreatedDateDesc(requestorId);
        return itemRequestMapper.toItemRequestDto(itemRequestWithItems);
    }

    @Override
    public List<ItemRequestDto> getAllRequests(long requestorId) {
        List<ItemRequestWithItems> itemRequestWithItems = itemRequestWithItemsRepository
                .findAllByRequestorIdIsNotOrderByCreatedDateDesc(requestorId);
        return itemRequestMapper.toItemRequestDto(itemRequestWithItems);
    }

    @Override
    public ItemRequestDto getRequest(long requestorId, long requestId) {
        ItemRequestWithItems request = itemRequestWithItemsRepository.findById(requestId).orElse(null);
        if (request == null) {
            return null;
        }
        return itemRequestMapper.toItemRequestDto(request);
    }
}
