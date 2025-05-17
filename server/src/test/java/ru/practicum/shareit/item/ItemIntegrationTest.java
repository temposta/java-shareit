package ru.practicum.shareit.item;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.booking.enums.StatusEnum;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemOwnerView;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest(classes = ShareItServer.class)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemIntegrationTest {

    @Autowired
    private final EntityManager entityManager;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemController itemController;

    @Autowired
    private UserController userController;

    @Test
    void testItemIntegration() {
        ItemCreateDto itemCreateDto = new ItemCreateDto();
        itemCreateDto.setDescription("This is a test");
        itemCreateDto.setName("test");
        itemCreateDto.setAvailable(true);

        User user = new User();
        user.setName("test Item user");
        user.setEmail("testitemuser@testitem.com");

        userController.createUser(user);

        itemController.createItem(user.getId(), itemCreateDto);

        TypedQuery<Item> query = entityManager.createQuery("select i from Item i where i.description = :description", Item.class);
        query.setParameter("description", itemCreateDto.getDescription());
        Item item = query.getSingleResult();
        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo(itemCreateDto.getName()));
        assertThat(item.getDescription(), equalTo(itemCreateDto.getDescription()));
        assertThat(item.getIsAvailable(), is(true));

        ItemPatchDto itemPatchDto = new ItemPatchDto();
        itemPatchDto.setName("new name");
        itemPatchDto.setDescription("new description");
        itemPatchDto.setAvailable(false);

        itemController.patchItem(user.getId(), item.getId(), itemPatchDto);
        query = entityManager.createQuery("select i from Item i where i.id = :id", Item.class);
        query.setParameter("id", item.getId());
        item = query.getSingleResult();
        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo(itemPatchDto.getName()));
        assertThat(item.getDescription(), equalTo(itemPatchDto.getDescription()));
        assertThat(item.getIsAvailable(), is(false));

        ItemOwnerView itemOwnerView = itemController.getItem(user.getId(), item.getId());
        assertThat(itemOwnerView.getId(), notNullValue());
        assertThat(itemOwnerView.getName(), equalTo(item.getName()));
        assertThat(itemOwnerView.getDescription(), equalTo(item.getDescription()));
        assertThat(itemOwnerView.getAvailable(), is(false));
        assertThat(itemOwnerView.getOwnerId(), equalTo(user.getId()));
        assertThat(itemOwnerView.getComments().size(), equalTo(0));

        List<ItemOwnerView> items = itemController.getItems(user.getId());
        assertThat(items.size(), equalTo(1));

        itemPatchDto.setAvailable(true);
        itemController.patchItem(user.getId(), item.getId(), itemPatchDto);

        User user1 = new User();
        user1.setName("test item user 1");
        user1.setEmail("testitemuser1@testitemuser.com");
        user1 = userController.createUser(user1);

        List<ItemDto> itemDtos = itemController.getItemsWithText(user1.getId(), "new");
        assertThat(itemDtos.size(), equalTo(0));

        item = itemService.getItem(item.getId());

        Booking booking = new Booking();
        booking.setBooker(user1);
        booking.setItem(item);
        booking.setStatus(StatusEnum.APPROVED);
        booking.setStartDate(LocalDateTime.now().minusDays(1));
        booking.setEndDate(LocalDateTime.now());

        bookingService.create(booking);

        CommentCreateDto commentCreateDto = new CommentCreateDto();
        commentCreateDto.setText("comment test");

        CommentDto commentDto = itemController.addComment(user1.getId(), item.getId(), commentCreateDto);

        assertThat(commentDto.getId(), notNullValue());
        assertThat(commentDto.getText(), equalTo(commentCreateDto.getText()));

        itemCreateDto.setName("new name");

        ItemDto itemDto = itemController.createItem(user.getId(), itemCreateDto);

        itemController.deleteItem(user.getId(), itemDto.getId());
        items = itemController.getItems(user.getId());
        assertThat(items.size(), equalTo(1));
    }

}
