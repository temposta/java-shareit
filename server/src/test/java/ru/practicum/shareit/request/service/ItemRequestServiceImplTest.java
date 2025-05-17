package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestWithItems;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.repository.ItemRequestWithItemsRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тестирование ItemRequestServiceImpl")
class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestMapper itemRequestMapper;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private ItemRequestWithItemsRepository itemRequestWithItemsRepository;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Test
    @DisplayName("создание запроса")
    void addRequest() {
        ItemRequestCreateDto requestDto = new ItemRequestCreateDto();
        ItemRequest itemRequest = new ItemRequest();
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        Mockito.when(itemRequestMapper.toItemRequest(1L, requestDto)).thenReturn(itemRequest);
        Mockito.when(itemRequestRepository.save(itemRequest)).thenReturn(itemRequest);
        Mockito.when(itemRequestMapper.toItemRequestDto(itemRequest)).thenReturn(itemRequestDto);

        itemRequestService.addRequest(1L, requestDto);
        Mockito.verify(itemRequestRepository).save(itemRequest);
        Mockito.verify(itemRequestMapper).toItemRequestDto(itemRequest);
        Mockito.verify(itemRequestMapper).toItemRequest(1L, requestDto);
    }

    @Test
    @DisplayName("получение своих запросов")
    void getOwnRequests() {
        List<ItemRequestWithItems> itemRequestWithItems = new ArrayList<>();
        List<ItemRequestDto> itemRequestDtos = new ArrayList<>();
        Mockito.when(itemRequestWithItemsRepository.findAllByRequestorIdOrderByCreatedDateDesc(Mockito.anyLong()))
                .thenReturn(itemRequestWithItems);
        Mockito.when(itemRequestMapper.toItemRequestDto(itemRequestWithItems)).thenReturn(itemRequestDtos);

        itemRequestService.getOwnRequests(1L);
        Mockito.verify(itemRequestWithItemsRepository).findAllByRequestorIdOrderByCreatedDateDesc(1L);
        Mockito.verify(itemRequestMapper).toItemRequestDto(itemRequestWithItems);
    }

    @Test
    @DisplayName("получение всех запросов")
    void getAllRequests() {
        List<ItemRequestWithItems> itemRequestWithItems = new ArrayList<>();
        List<ItemRequestDto> itemRequestDtos = new ArrayList<>();
        Mockito.when(itemRequestWithItemsRepository.findAllByRequestorIdIsNotOrderByCreatedDateDesc(1L))
                .thenReturn(itemRequestWithItems);
        Mockito.when(itemRequestMapper.toItemRequestDto(itemRequestWithItems)).thenReturn(itemRequestDtos);

        itemRequestService.getAllRequests(1L);
        Mockito.verify(itemRequestWithItemsRepository).findAllByRequestorIdIsNotOrderByCreatedDateDesc(1L);
        Mockito.verify(itemRequestMapper).toItemRequestDto(itemRequestWithItems);
    }

    @Test
    @DisplayName("получение информации о конкретном запросе")
    void getRequest() {
        ItemRequestWithItems itemRequestWithItems = new ItemRequestWithItems();
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        Mockito.when(itemRequestWithItemsRepository.findById(1L)).thenReturn(Optional.empty());
        Mockito.when(itemRequestWithItemsRepository.findById(2L)).thenReturn(Optional.of(itemRequestWithItems));
        Mockito.when(itemRequestMapper.toItemRequestDto(itemRequestWithItems)).thenReturn(itemRequestDto);

        ItemRequestDto nullItemRequestWithItems = itemRequestService.getRequest(1L, 1L);
        assertNull(nullItemRequestWithItems);

        Mockito.verify(itemRequestWithItemsRepository).findById(1L);

        itemRequestService.getRequest(1L, 2L);
        Mockito.verify(itemRequestWithItemsRepository).findById(2L);
        Mockito.verify(itemRequestMapper).toItemRequestDto(itemRequestWithItems);
    }
}