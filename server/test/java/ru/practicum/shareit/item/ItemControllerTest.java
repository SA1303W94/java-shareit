package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.item.comment.CommentDto;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ItemControllerTest {

    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController itemController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createItemDto() {
        Long ownerId = 1L;

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Item 1");
        itemDto.setDescription("Description");

        ItemDto createdItem = new ItemDto();
        createdItem.setId(1L);
        createdItem.setName("Item 1");
        createdItem.setDescription("Description");

        when(itemService.create(ownerId, itemDto)).thenReturn(createdItem);

        ItemDto result = itemController.create(itemDto, ownerId);
        assertNotNull(result);
        assertEquals(createdItem.getId(), result.getId());
        assertEquals(createdItem.getName(), result.getName());
        assertEquals(createdItem.getDescription(), result.getDescription());
        verify(itemService).create(ownerId, itemDto);
    }

    @Test
    void saveValidItemDtoAndItemIdAndOwnerId() {
        Long itemId = 1L;
        Long ownerId = 1L;

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Item 1");
        itemDto.setDescription("Description");

        ItemDto savedItem = new ItemDto();
        savedItem.setId(itemId);
        savedItem.setName("Item 1");
        savedItem.setDescription("Description");

        when(itemService.save(itemDto, itemId, ownerId)).thenReturn(savedItem);

        ItemDto result = itemController.save(itemDto, itemId, ownerId);

        assertNotNull(result);
        assertEquals(savedItem.getId(), result.getId());
        assertEquals(savedItem.getName(), result.getName());
        assertEquals(savedItem.getDescription(), result.getDescription());
        verify(itemService).save(itemDto, itemId, ownerId);
    }

    @Test
    void findByIdItemExists() {
        Long userId = 1L;
        Long itemId = 1L;

        ItemDto itemDto = new ItemDto();
        itemDto.setId(itemId);
        itemDto.setName("Item 1");
        itemDto.setDescription("Description");

        when(itemService.findItemById(itemId, userId)).thenReturn(itemDto);
        ItemDto result = itemController.findById(userId, itemId);

        assertNotNull(result);
        assertEquals(itemDto.getId(), result.getId());
        assertEquals(itemDto.getName(), result.getName());
        assertEquals(itemDto.getDescription(), result.getDescription());
        verify(itemService).findItemById(itemId, userId);
    }

    @Test
    void findAllValidUserId() {
        Long userId = 1L;
        int from = 0;
        int size = 10;

        ItemDto itemDto1 = new ItemDto();
        itemDto1.setId(1L);
        itemDto1.setName("Item 1");
        itemDto1.setDescription("Description 1");

        ItemDto itemDto2 = new ItemDto();
        itemDto2.setId(2L);
        itemDto2.setName("Item 2");
        itemDto2.setDescription("Description 2");

        List<ItemDto> itemList = Arrays.asList(itemDto1, itemDto2);

        when(itemService.findAllUsersItems(userId, from, size)).thenReturn(itemList);

        List<ItemDto> result = itemController.findAll(userId, from, size);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(itemDto1.getId(), result.get(0).getId());
        assertEquals(itemDto1.getName(), result.get(0).getName());
        assertEquals(itemDto1.getDescription(), result.get(0).getDescription());
        assertEquals(itemDto2.getId(), result.get(1).getId());
        assertEquals(itemDto2.getName(), result.get(1).getName());
        assertEquals(itemDto2.getDescription(), result.get(1).getDescription());
        verify(itemService).findAllUsersItems(userId, from, size);
    }

    @Test
    void deleteValidItemIdAndOwnerId() {
        Long itemId = 1L;
        Long ownerId = 1L;

        itemController.delete(itemId, ownerId);
        verify(itemService).deleteById(itemId);
    }

    @Test
    void searchValidText() {
        String text = "item";
        int from = 0;
        int size = 10;

        ItemDto itemDto1 = new ItemDto();
        itemDto1.setId(1L);
        itemDto1.setName("Item 1");
        itemDto1.setDescription("Description 1");

        ItemDto itemDto2 = new ItemDto();
        itemDto2.setId(2L);
        itemDto2.setName("Item 2");
        itemDto2.setDescription("Description 2");

        List<ItemDto> itemCollection = Arrays.asList(itemDto1, itemDto2);

        when(itemService.search(text, from, size)).thenReturn(itemCollection);

        Collection<ItemDto> result = itemController.search(text, from, size);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(itemDto1));
        assertTrue(result.contains(itemDto2));
        verify(itemService).search(text, from, size);
    }

    @Test
    void createCommentValidUserIdAndItemIdAndCommentDto() {
        Long userId = 1L;
        Long itemId = 1L;

        CommentDto commentDto = new CommentDto();
        commentDto.setText("Comment 1");

        CommentDto createdComment = new CommentDto();
        createdComment.setId(1L);
        createdComment.setText("Comment 1");

        when(itemService.addComment(itemId, userId, commentDto)).thenReturn(createdComment);
        CommentDto result = itemController.createComment(userId, itemId, commentDto);
        assertNotNull(result);
        assertEquals(createdComment.getId(), result.getId());
        assertEquals(createdComment.getText(), result.getText());
        verify(itemService).addComment(itemId, userId, commentDto);
    }
}