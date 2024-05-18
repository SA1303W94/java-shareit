package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.user.User;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest(properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private EntityManager entityManager;

    @Test
    public void testFindAllUsersItems() {
        Long userId = 1L;
        Integer from = 0;
        Integer size = 10;

        // Создание пользователя
        User user = new User();
        user.setName("test");
        user.setEmail("test@mail.ru");
        entityManager.persist(user);
        entityManager.flush();

        User user1 = new User();
        user1.setName("test1");
        user1.setEmail("test1@mail.ru");
        entityManager.persist(user1);
        entityManager.flush();

        Item item1 = new Item();
        item1.setName("Item 1");
        item1.setOwnerId(user.getId());
        item1.setDescription("Item 1");
        item1.setAvailable(true);

        Item item2 = new Item();
        item2.setName("Item 2");
        item2.setOwnerId(user.getId());
        item2.setDescription("Item 2");
        item2.setAvailable(true);

        entityManager.persist(item1);
        entityManager.persist(item2);
        entityManager.flush();

        Booking booking1 = new Booking();
        booking1.setItem(item1);
        booking1.setBooker(user);
        booking1.setStart(LocalDateTime.now().plusHours(1));
        booking1.setEnd(LocalDateTime.now().plusHours(2));
        booking1.setStatus(BookingStatus.APPROVED);
        entityManager.persist(booking1);
        entityManager.flush();

        Booking booking2 = new Booking();
        booking2.setItem(item2);
        booking2.setBooker(user);
        booking2.setStart(LocalDateTime.now().plusHours(1));
        booking2.setEnd(LocalDateTime.now().plusHours(2));
        booking2.setStatus(BookingStatus.APPROVED);
        entityManager.persist(booking2);
        entityManager.flush();

        Comment comment1 = new Comment();
        comment1.setCreated(LocalDateTime.now().plusHours(1));
        comment1.setAuthor(user);
        comment1.setItem(item1);
        comment1.setText("Comment 1");
        entityManager.persist(comment1);
        entityManager.flush();

        Comment comment2 = new Comment();
        comment2.setCreated(LocalDateTime.now().plusHours(1));
        comment2.setAuthor(user);
        comment2.setItem(item2);
        comment2.setText("Comment 2");
        entityManager.persist(comment2);
        entityManager.flush();

        List<ItemDto> result = itemService.findAllUsersItems(userId, from, size);
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        for (ItemDto itemDto : result) {
            assertThat(itemDto.getComments()).isNotNull();
            assertThat(itemDto.getLastBooking()).isNull();
        }
    }
}