package ru.practicum.shareit.request;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest(classes = ShareItServer.class)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RequestIntegrationTest {

    @Autowired
    private final EntityManager entityManager;

    @Autowired
    private ItemRequestController controller;

    @Autowired
    private UserController userController;

    @Autowired
    private ItemController itemController;

    @Test
    void testRequest() {
        User user = new User();
        user.setName("test Item user");
        user.setEmail("testitemuser@test.com");

        User user1 = new User();
        user1.setName("test Item user1");
        user1.setEmail("testitemuser1@test.com");

        userController.createUser(user);
        userController.createUser(user1);

        ItemCreateDto itemCreateDto = new ItemCreateDto();
        itemCreateDto.setName("test item");
        itemCreateDto.setDescription("Test Item");

        ItemDto itemDto = itemController.createItem(user.getId(), itemCreateDto);
        assertThat(itemDto, notNullValue());

        ItemRequestCreateDto requestDto = new ItemRequestCreateDto();
        requestDto.setDescription("test item description");

        controller.addRequest(user.getId(), requestDto);

        TypedQuery<ItemRequest> query = entityManager.createQuery("select i from ItemRequest i where i.requestorId = :requestorId", ItemRequest.class);
        query.setParameter("requestorId", user.getId());
        ItemRequest request = query.getSingleResult();
        assertThat(request.getId(), notNullValue());
        assertThat(request.getDescription(), equalTo(requestDto.getDescription()));

        List<ItemRequestDto> requestDtos = controller.getOwnRequests(user.getId());
        assertThat(requestDtos.size(), equalTo(1));

        requestDtos = controller.getAllRequests(user1.getId());
        assertThat(requestDtos.size(), equalTo(1));

        ItemRequestDto itemRequestDto = controller.getRequest(user.getId(), requestDtos.getFirst().getId());
        assertThat(itemRequestDto.getId(), notNullValue());
        assertThat(itemRequestDto.getDescription(), equalTo(requestDto.getDescription()));

        itemCreateDto.setRequestId(requestDtos.getFirst().getId());
        itemDto = itemController.createItem(user.getId(), itemCreateDto);
        assertThat(itemDto.getId(), notNullValue());
        ItemRequestDto itemRequestDto1 = controller.getRequest(user.getId(), requestDtos.getFirst().getId());
        assertThat(itemRequestDto1.getItems().size(), equalTo(1));


    }


}
