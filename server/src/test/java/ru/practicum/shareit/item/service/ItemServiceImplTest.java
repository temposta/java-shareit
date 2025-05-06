package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.enums.StatusEnum;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exeptions.ForbiddenException;
import ru.practicum.shareit.exeptions.NotFoundException;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemOwnerView;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemOwnerViewRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тестирование ItemServiceImpl")
class ItemServiceImplTest {
    @Mock
    ItemRepository mockItemRepository;

    @Mock
    UserRepository mockUserRepository;

    @Mock
    ItemMapper mockItemMapper;

    @Mock
    CommentRepository mockCommentRepository;

    @Mock
    BookingRepository mockBookingRepository;

    @Mock
    ItemOwnerViewRepository mockItemOwnerViewRepository;

    @InjectMocks
    ItemServiceImpl itemService;


    @Test
    @DisplayName("вызовы методов при создании новой вещи")
    void addNewItem() {
        ItemCreateDto newItem = new ItemCreateDto();
        Item item = new Item();
        User user = new User();
        long ownerId = 1L;

        Mockito.when(mockUserRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        Mockito.when(mockItemMapper.fromCreateDto(newItem)).thenReturn(item);
        Mockito.when(mockItemRepository.save(item)).thenReturn(item);

        itemService.addNewItem(ownerId, newItem);

        Mockito.verify(mockItemRepository, Mockito.times(1)).save(item);
        Mockito.verify(mockItemMapper, Mockito.times(1)).fromCreateDto(newItem);
        Mockito.verify(mockUserRepository, Mockito.times(1)).findById(Mockito.anyLong());
    }

    @Test
    @DisplayName("создание вещи при не валидном ID пользователя")
    void addNewItemWithWrongId() {
        ItemCreateDto newItem = new ItemCreateDto();
        Item item = new Item();
        long ownerId = 1L;

        Mockito.when(mockUserRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> itemService.addNewItem(ownerId, newItem));

        assertEquals(exception.getMessage(), "Пользователь с id: 1 не найден");

        Mockito.verify(mockItemRepository, Mockito.times(0)).save(item);
        Mockito.verify(mockItemMapper, Mockito.times(0)).fromCreateDto(newItem);
        Mockito.verify(mockUserRepository, Mockito.times(1)).findById(Mockito.anyLong());
    }

    @Test
    @DisplayName("обновление вещи")
    void patchItem() {
        User owner = new User();
        owner.setId(5L);
        ItemPatchDto itemPatchDto = new ItemPatchDto();
        itemPatchDto.setName("new name");
        itemPatchDto.setDescription("new description");
        itemPatchDto.setAvailable(true);
        Item patchedItem = new Item();
        patchedItem.setId(1L);
        patchedItem.setName("name");
        patchedItem.setDescription("description");
        patchedItem.setOwner(owner);
        patchedItem.setIsAvailable(false);

        User user = new User();
        user.setId(6L);

        Mockito.when(mockUserRepository.findById(5L)).thenReturn(Optional.of(owner));
        Mockito.when(mockUserRepository.findById(6L)).thenReturn(Optional.of(user));
        Mockito.when(mockItemRepository.findById(1L)).thenReturn(Optional.of(patchedItem));
        Mockito.when(mockItemRepository.findById(2L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> itemService
                .patchItem(5L, 2L, itemPatchDto));
        assertEquals(exception.getMessage(), "Вещь с указанным id: 2 не найдена");

        Mockito.verify(mockUserRepository, Mockito.times(1)).findById(5L);
        Mockito.verify(mockItemRepository, Mockito.times(1)).findById(2L);

        ForbiddenException forbiddenException = assertThrows(ForbiddenException.class, () -> itemService
                .patchItem(6L, 1L, itemPatchDto));
        assertEquals(forbiddenException.getMessage(), "Пользователь с id: 6 не является владельцем вещи");
        Mockito.verify(mockUserRepository, Mockito.times(1)).findById(6L);
        Mockito.verify(mockItemRepository, Mockito.times(1)).findById(1L);

        itemService.patchItem(5L, 1L, itemPatchDto);
        Mockito.verify(mockUserRepository, Mockito.times(2)).findById(5L);
        Mockito.verify(mockItemRepository, Mockito.times(2)).findById(1L);

        assertEquals(patchedItem.getName(), itemPatchDto.getName());
        assertEquals(patchedItem.getDescription(), itemPatchDto.getDescription());
        assertEquals(patchedItem.getIsAvailable(), itemPatchDto.getAvailable());

    }

    @Test
    @DisplayName("получение вещи по ID")
    void getItem() {
        Mockito.when(mockItemRepository.findById(1L)).thenReturn(Optional.of(new Item()));
        Mockito.when(mockItemRepository.findById(2L)).thenReturn(Optional.empty());

        Item item = itemService.getItem(1L);
        Mockito.verify(mockItemRepository, Mockito.times(1)).findById(1L);
        assertNotNull(item);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> itemService.getItem(2L));
        assertEquals(exception.getMessage(), "Вещь с указанным id: 2 не найдена");
        Mockito.verify(mockItemRepository, Mockito.times(1)).findById(2L);
    }

    @Test
    @DisplayName("получение вещи по ID владельцем")
    void getItemForOwnerView() {
        Mockito.when(mockItemOwnerViewRepository.findById(1L)).thenReturn(Optional.of(new ItemOwnerView()));
        Mockito.when(mockItemOwnerViewRepository.findById(2L)).thenReturn(Optional.empty());

        ItemOwnerView item = itemService.getItemForOwnerView(1L);
        Mockito.verify(mockItemOwnerViewRepository, Mockito.times(1)).findById(1L);
        assertNotNull(item);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> itemService.getItemForOwnerView(2L));
        assertEquals(exception.getMessage(), "Вещь с id - 2 не найдена");
        Mockito.verify(mockItemOwnerViewRepository, Mockito.times(1)).findById(2L);
    }

    @Test
    @DisplayName("получение всех вещей по ID владельцем")
    void getItemsForOwner() {
        Mockito.when(mockItemOwnerViewRepository.findAllByOwnerId(1L))
                .thenReturn(List.of(new ItemOwnerView()));
        Mockito.when(mockUserRepository.findById(1L)).thenReturn(Optional.of(new User()));
        List<ItemOwnerView> list = itemService.getItemsForOwner(1L);
        Mockito.verify(mockItemOwnerViewRepository, Mockito.times(1)).findAllByOwnerId(1L);
        Mockito.verify(mockUserRepository, Mockito.times(1)).findById(1L);
        assertNotNull(list);
    }

    @Test
    @DisplayName("поиск вещей по тексту")
    void getItemsWithText() {
        Mockito.when(mockItemRepository
                        .findByIsAvailableIsTrueAndNameLikeIgnoreCaseOrDescriptionLikeIgnoreCase(Mockito.anyString(),
                                Mockito.anyString()))
                .thenReturn(List.of(new Item()));

        List<Item> items = itemService.getItemsWithText("text");
        Mockito.verify(mockItemRepository, Mockito.times(1))
                .findByIsAvailableIsTrueAndNameLikeIgnoreCaseOrDescriptionLikeIgnoreCase(Mockito.anyString(),
                        Mockito.anyString());
        assertNotNull(items);
    }

    @Test
    @DisplayName("добавление комментария")
    void addComment() {
        CommentCreateDto commentCreateDto = new CommentCreateDto();

        Mockito.when(mockItemRepository.findById(1L)).thenReturn(Optional.of(new Item()));
        Mockito.when(mockItemRepository.findById(2L)).thenReturn(Optional.empty());

        Mockito.when(mockBookingRepository
                        .findByItemIdAndBookerIdAndStartDateBeforeAndStatus(Mockito.anyLong(),
                                Mockito.anyLong(), Mockito.any(LocalDateTime.class), Mockito.any(StatusEnum.class)))
                .thenReturn(Optional.of(new Booking()));

        Mockito.when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User()));
        Mockito.when(mockCommentRepository.save(Mockito.any())).thenReturn(new Comment());

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemService.addComment(6L, 2L, commentCreateDto));
        assertEquals(notFoundException.getMessage(), "Вещи с ID - 2 - не существует");
        Mockito.verify(mockItemRepository, Mockito.times(1)).findById(2L);

        Comment comment = itemService.addComment(5L, 1L, commentCreateDto);
        Mockito.verify(mockItemRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(mockCommentRepository, Mockito.times(1)).save(Mockito.any());

        assertNotNull(comment);
    }

    @Test
    @DisplayName("удаление вещи владельцем")
    void deleteItem() {
        User user = new User();
        user.setId(1L);
        Item item = new Item();
        item.setOwner(user);
        Mockito.when(mockItemRepository.findById(1L)).thenReturn(Optional.of(item));
        Mockito.when(mockItemRepository.findById(2L)).thenReturn(Optional.empty());
        Mockito.when(mockUserRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        NotFoundException exception = assertThrows(NotFoundException.class, () -> itemService
                .deleteItem(1L, 2L));
        assertEquals(exception.getMessage(), "Вещь с указанным id: 2 не найдена");
        Mockito.verify(mockItemRepository, Mockito.times(1)).findById(2L);

        ForbiddenException forbiddenException = assertThrows(ForbiddenException.class,
                () -> itemService.deleteItem(2L, 1L));
        assertEquals(forbiddenException.getMessage(), "Пользователь с id: 2 не является владельцем вещи");
        Mockito.verify(mockItemRepository, Mockito.times(1)).findById(1L);
        itemService.deleteItem(1L, 1L);

        Mockito.verify(mockItemRepository, Mockito.times(1)).deleteById(1L);
    }
}