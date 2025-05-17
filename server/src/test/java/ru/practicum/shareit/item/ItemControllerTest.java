package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemOwnerView;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {
    @Mock
    private ItemService itemService;
    @Mock
    private ItemMapper itemMapper;
    @Mock
    private CommentMapper commentMapper;
    @InjectMocks
    private ItemController itemController;

    ItemCreateDto itemCreateDto;
    ItemDto itemDto;
    ItemPatchDto itemPatchDto;
    ItemOwnerView itemOwnerView;
    CommentCreateDto commentCreateDto;
    CommentDto commentDto;

    ObjectMapper mapper = new ObjectMapper();

    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .build();

        itemCreateDto = new ItemCreateDto();
        itemDto = ItemDto.builder().build();
        itemPatchDto = new ItemPatchDto();
        itemOwnerView = new ItemOwnerView();
        commentCreateDto = new CommentCreateDto();
        commentDto = CommentDto.builder().build();
    }

    @Test
    void createItem() throws Exception {
        when(itemService.addNewItem(1L, itemCreateDto)).thenReturn(new Item());
        when(itemMapper.toDto(any(Item.class))).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                .header("X-Sharer-User-Id", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(mapper.writeValueAsString(itemCreateDto)))
                .andExpect(status().isCreated());
    }

    @Test
    void patchItem() throws Exception {
        when(itemService.patchItem(1L, 1L, itemPatchDto)).thenReturn(new Item());
        when(itemMapper.toDto(any(Item.class))).thenReturn(itemDto);

        mockMvc.perform(patch("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(mapper.writeValueAsString(itemPatchDto)))
                .andExpect(status().isOk());
    }

    @Test
    void getItem() throws Exception {
        when(itemService.getItemForOwnerView(1L)).thenReturn(itemOwnerView);

        mockMvc.perform(get("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                .andExpect(status().isOk());
    }

    @Test
    void getItems() throws Exception {
        when(itemService.getItemsForOwner(1L)).thenReturn(List.of(itemOwnerView));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getItemsWithText() throws Exception {
        when(itemService.getItemsWithText(anyString())).thenReturn(List.of(new Item()));
        when(itemMapper.toDto(Mockito.anyList())).thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .param("text", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void deleteItem() throws Exception {
        mockMvc.perform(delete("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                .andExpect(status().isOk());
    }

    @Test
    void addComment() throws Exception {
        when(itemService.addComment(1L, 1L, commentCreateDto)).thenReturn(new Comment());
        when(commentMapper.toDto(any(Comment.class))).thenReturn(commentDto);

        mockMvc.perform(post("/items/{itemId}/comment", 1L)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(mapper.writeValueAsString(commentCreateDto)))
                .andExpect(status().isOk());
    }
}