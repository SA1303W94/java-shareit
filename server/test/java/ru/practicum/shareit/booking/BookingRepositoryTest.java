package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.page.CustomPageRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class BookingRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private BookingRepository bookingRepository;

    @Test
    public void testSaveBooking() {
        User user = new User();
        user.setName("user");
        user.setEmail("user@example.com");
        entityManager.persist(user);
        entityManager.flush();

        Item item = new Item();
        item.setName("item");
        item.setDescription("item");
        item.setAvailable(true);
        item.setOwnerId(user.getId());
        entityManager.persist(item);
        entityManager.flush();

        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().plusHours(1));
        booking.setEnd(LocalDateTime.now().plusHours(2));
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        entityManager.persist(booking);
        entityManager.flush();

        Booking newBooking = bookingRepository.save(booking);
        assertNotNull(newBooking);
        assertEquals(newBooking, booking);
    }

    @Test
    public void testFindAllBookingsByOwner() {
        User user = new User();
        user.setName("user");
        user.setEmail("user@example.com");
        entityManager.persist(user);
        entityManager.flush();

        User otherUser = new User();
        otherUser.setName("otherUser");
        otherUser.setEmail("otherUser@example.com");
        entityManager.persist(otherUser);
        entityManager.flush();

        Item item = new Item();
        item.setName("item");
        item.setDescription("item");
        item.setAvailable(true);
        item.setOwnerId(user.getId());
        entityManager.persist(item);
        entityManager.flush();

        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().plusHours(1));
        booking.setEnd(LocalDateTime.now().plusHours(2));
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        entityManager.persist(booking);
        entityManager.flush();

        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);

        int from = 0;
        int size = 10;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        Pageable page = new CustomPageRequest(from, size, sort);

        List<Booking> newBooking = bookingRepository.findByItemOwnerId(user.getId(), page);
        assertNotNull(newBooking);
        assertEquals(newBooking, bookings);
    }
}