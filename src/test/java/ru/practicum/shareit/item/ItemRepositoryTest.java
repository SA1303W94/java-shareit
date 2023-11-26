package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private ItemRepository itemRepository;
    @InjectMocks
    private ItemService itemService;

    @Test
    public void testFindAllBookingsByOwner() {
        Item item = new Item();
        item.setName("item");
        item.setDescription("item");
        item.setAvailable(true);
        item.setOwnerId(1L);
        entityManager.persist(item);
        entityManager.flush();

        int from = 0;
        int size = 10;
        Pageable page = PageRequest.of(from / size, size);
        String text = "item";

        List<Item> newItem = itemRepository.searchAvailableItems(text, page);
        assertNotNull(newItem);
        assertEquals(newItem.get(0).getName(), text);
    }
}