package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.comment.CommentDto;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
public class ItemControllerMockMVCTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @Test
    void createValidItem() throws Exception {
        Long ownerId = 1L;
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test Item");
        itemDto.setDescription("Test description");
        itemDto.setAvailable(true);

        when(itemService.create(eq(ownerId), any(ItemDto.class))).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", ownerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Item"))
                .andExpect(jsonPath("$.description").value("Test description"))
                .andExpect(jsonPath("$.available").value(true));

        verify(itemService).create(eq(ownerId), any(ItemDto.class));
    }

    @Test
    void updateValidItem() throws Exception {
        Long ownerId = 1L;
        Long itemId = 1L;
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Updated Item");

        when(itemService.save(any(ItemDto.class), eq(itemId), eq(ownerId))).thenReturn(itemDto);

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", ownerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"name\": \"Updated Item\" }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Item"));

        verify(itemService).save(any(ItemDto.class), eq(itemId), eq(ownerId));
    }

    @Test
    void findItemByIdValidItemId() throws Exception {
        Long ownerId = 1L;
        Long itemId = 1L;
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test Item");

        when(itemService.findItemById(eq(itemId), eq(ownerId))).thenReturn(itemDto);

        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Item"));

        verify(itemService).findItemById(eq(itemId), eq(ownerId));
    }

    @Test
    void findAllItems() throws Exception {
        Long ownerId = 1L;
        ItemDto itemDto1 = new ItemDto();
        itemDto1.setName("Item 1");
        ItemDto itemDto2 = new ItemDto();
        itemDto2.setName("Item 2");
        List<ItemDto> itemList = Arrays.asList(itemDto1, itemDto2);

        when(itemService.findAllUsersItems(eq(ownerId), anyInt(), anyInt())).thenReturn(itemList);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Item 1"))
                .andExpect(jsonPath("$[1].name").value("Item 2"));

        verify(itemService).findAllUsersItems(eq(ownerId), anyInt(), anyInt());
    }

    @Test
    void deleteItemByIdValidItemId() throws Exception {
        Long ownerId = 1L;
        Long itemId = 1L;

        mockMvc.perform(delete("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isOk());

        verify(itemService).deleteById(eq(itemId));
    }

    @Test
    void searchItemsByKeyword() throws Exception {
        Long ownerId = 1L;
        String keyword = "test";
        ItemDto itemDto1 = new ItemDto();
        itemDto1.setName("Test Item 1");
        ItemDto itemDto2 = new ItemDto();
        itemDto2.setName("Test Item 2");
        List<ItemDto> itemList = Arrays.asList(itemDto1, itemDto2);

        when(itemService.search(eq(keyword), anyInt(), anyInt())).thenReturn(itemList);

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", ownerId)
                        .param("text", keyword))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Item 1"))
                .andExpect(jsonPath("$[1].name").value("Test Item 2"));

        verify(itemService).search(eq(keyword), anyInt(), anyInt());
    }

    @Test
    void createCommentValidUserIdAndItemIdAndCommentDto() throws Exception {
        Long ownerId = 1L;
        Long itemId = 1L;
        CommentDto commentDto = new CommentDto();
        commentDto.setText("This is a comment");

        when(itemService.addComment(eq(itemId), eq(ownerId), any(CommentDto.class))).thenReturn(commentDto);

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", ownerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"text\": \"This is a comment\" }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("This is a comment"));

        verify(itemService).addComment(eq(itemId), eq(ownerId), any(CommentDto.class));
    }
}