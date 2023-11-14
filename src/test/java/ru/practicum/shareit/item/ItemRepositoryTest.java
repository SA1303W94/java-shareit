package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.ItemRequest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

class ItemRepositoryTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemService itemService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAllByOwnerIdValidUserId() {
        Long userId = 1L;
        int from = 0;
        int size = 10;

        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("Item 1");
        item1.setDescription("Description 1");

        Item item2 = new Item();
        item2.setId(2L);
        item2.setName("Item 2");
        item2.setDescription("Description 2");

        List<Item> itemList = Arrays.asList(item1, item2);

        when(itemRepository.findAllByOwnerId(userId, PageRequest.of(from, size))).thenReturn(itemList);

        List<Item> result = itemRepository.findAllByOwnerId(userId, PageRequest.of(from, size));

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(item1.getId(), result.get(0).getId());
        assertEquals(item1.getName(), result.get(0).getName());
        assertEquals(item1.getDescription(), result.get(0).getDescription());
        assertEquals(item2.getId(), result.get(1).getId());
        assertEquals(item2.getName(), result.get(1).getName());
        assertEquals(item2.getDescription(), result.get(1).getDescription());
    }

    @Test
    void searchAvailableItemsValidText() {
        String text = "item";
        int from = 0;
        int size = 10;

        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("Item 1");
        item1.setDescription("Description 1");

        Item item2 = new Item();
        item2.setId(2L);
        item2.setName("Item 2");
        item2.setDescription("Description 2");

        List<Item> itemList = Arrays.asList(item1, item2);

        when(itemRepository.searchAvailableItems(text, PageRequest.of(from, size))).thenReturn(itemList);

        List<Item> result = itemRepository.searchAvailableItems(text, PageRequest.of(from, size));

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(item1.getId(), result.get(0).getId());
        assertEquals(item1.getName(), result.get(0).getName());
        assertEquals(item1.getDescription(), result.get(0).getDescription());
        assertEquals(item2.getId(), result.get(1).getId());
        assertEquals(item2.getName(), result.get(1).getName());
        assertEquals(item2.getDescription(), result.get(1).getDescription());
    }

    @Test
    void findAllByItemRequestValidItemRequest() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Item Request 1");

        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("Item 1");
        item1.setDescription("Description 1");
        item1.setItemRequest(itemRequest);

        Item item2 = new Item();
        item2.setId(2L);
        item2.setName("Item 2");
        item2.setDescription("Description 2");
        item2.setItemRequest(itemRequest);

        List<Item> itemList = Arrays.asList(item1, item2);

        when(itemRepository.findAllByItemRequest(itemRequest)).thenReturn(itemList);

        List<Item> result = itemRepository.findAllByItemRequest(itemRequest);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(item1.getId(), result.get(0).getId());
        assertEquals(item1.getName(), result.get(0).getName());
        assertEquals(item1.getDescription(), result.get(0).getDescription());
        assertEquals(item2.getId(), result.get(1).getId());
        assertEquals(item2.getName(), result.get(1).getName());
        assertEquals(item2.getDescription(), result.get(1).getDescription());
    }

    @Test
    void findAllByItemRequestInValidItemRequestList() {
        ItemRequest itemRequest1 = new ItemRequest();
        itemRequest1.setId(1L);
        itemRequest1.setDescription("Item Request 1");

        ItemRequest itemRequest2 = new ItemRequest();
        itemRequest2.setId(2L);
        itemRequest2.setDescription("Item Request 2");

        List<ItemRequest> itemRequestList = Arrays.asList(itemRequest1, itemRequest2);

        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("Item 1");
        item1.setDescription("Description 1");
        item1.setItemRequest(itemRequest1);

        Item item2 = new Item();
        item2.setId(2L);
        item2.setName("Item 2");
        item2.setDescription("Description 2");
        item2.setItemRequest(itemRequest2);

        List<Item> itemList = Arrays.asList(item1, item2);

        when(itemRepository.findAllByItemRequestIn(itemRequestList)).thenReturn(itemList);

        List<Item> result = itemRepository.findAllByItemRequestIn(itemRequestList);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(item1.getId(), result.get(0).getId());
        assertEquals(item1.getName(), result.get(0).getName());
        assertEquals(item1.getDescription(), result.get(0).getDescription());
        assertEquals(item2.getId(), result.get(1).getId());
        assertEquals(item2.getName(), result.get(1).getName());
        assertEquals(item2.getDescription(), result.get(1).getDescription());
    }
}