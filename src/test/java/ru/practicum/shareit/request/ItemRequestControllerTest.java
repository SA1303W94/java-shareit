package ru.practicum.shareit.request;

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

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {

    private ObjectMapper objectMapper;
    @InjectMocks
    private ItemRequestController requestController;
    @Mock
    private ItemRequestService requestService;
    private ItemRequestDto itemRequestDto;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(requestController).build();
        itemRequestDto = ItemRequestDto.builder().id(1L).description("Description").build();
    }

    @Test
    void createItemRequest() throws Exception {
        Mockito.when(requestService.create(Mockito.any(ItemRequestDto.class), anyLong())).thenReturn(itemRequestDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Description"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(requestService).create(Mockito.any(ItemRequestDto.class), anyLong());
    }

    @Test
    void findRequestById() throws Exception {
        Long userId = 1L;
        Long requestId = 1L;
        ItemRequestDto itemRequestDto = new ItemRequestDto();

        Mockito.when(requestService.findById(userId, requestId)).thenReturn(itemRequestDto);

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequestDto.getId()));

        verify(requestService).findById(userId, requestId);
    }

    @Test
    void findRequests() throws Exception {
        Long userId = 1L;
        int from = 0;
        int size = 10;
        List<ItemRequestDto> itemRequestDtoList = new ArrayList<>();

        Mockito.when(requestService.findRequests(userId, from, size)).thenReturn(itemRequestDtoList);

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(requestService).findRequests(userId, from, size);
    }

    @Test
    void getUserRequests() throws Exception {
        Long userId = 1L;
        List<ItemRequestDto> itemRequestDtoList = new ArrayList<>();

        Mockito.when(requestService.findUserRequests(userId)).thenReturn(itemRequestDtoList);

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(requestService).findUserRequests(userId);
    }
}