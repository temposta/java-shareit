package ru.practicum.shareit.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.user.dto.UserPatchDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest(classes = ShareItServer.class)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserIntegrationTest {

    @Autowired
    private final EntityManager entityManager;
    @Autowired
    private UserController userController;

    @Test
    void testUser() {
        User user = new User();
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");

        userController.createUser(user);

        TypedQuery<User> query = entityManager.createQuery("select u from User u where u.email = :email", User.class);
        query.setParameter("email", user.getEmail());
        User fromDB = query.getSingleResult();

        assertThat(fromDB.getId(), notNullValue());
        assertThat(fromDB.getName(), equalTo(user.getName()));
        assertThat(fromDB.getEmail(), equalTo(user.getEmail()));

        long userId = fromDB.getId();

        fromDB = userController.getUser(userId);
        assertThat(fromDB.getId(), notNullValue());
        assertThat(fromDB.getName(), equalTo(user.getName()));
        assertThat(fromDB.getEmail(), equalTo(user.getEmail()));

        User user1 = new User();
        user1.setName("Bill");
        user1.setEmail("bill@example.com");

        userController.createUser(user1);

        query = entityManager.createQuery("select u from User u where u.email = :email", User.class);
        query.setParameter("email", user1.getEmail());
        long user1Id = query.getSingleResult().getId();

        assertThat(user1Id, notNullValue());

        userController.deleteUser(user1Id);

        query = entityManager.createQuery("select u from User u where u.email = :email", User.class);
        query.setParameter("email", user1.getEmail());
        List<User> resultList = query.getResultList();

        assertThat(resultList.size(), equalTo(0));

        UserPatchDto patchDto = new UserPatchDto();
        patchDto.setName("J.John Doe");
        patchDto.setEmail("j.john.doe@example.com");

        userController.updateUser(userId, patchDto);
        query = entityManager.createQuery("select u from User u where u.id = :id", User.class);
        query.setParameter("id", userId);
        User fromDB2 = query.getSingleResult();
        assertThat(fromDB2.getId(), equalTo(userId));
        assertThat(fromDB2.getName(), equalTo(patchDto.getName()));
        assertThat(fromDB2.getEmail(), equalTo(patchDto.getEmail()));
    }
}
