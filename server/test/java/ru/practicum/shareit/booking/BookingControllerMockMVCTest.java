package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
public class BookingControllerMockMVCTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    @Test
    void createValidBookingDtoAndUserId() throws Exception {
        long userId = 2L;
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setItemId(1L);
        bookingDto.setStart(LocalDateTime.now().plusHours(1));
        bookingDto.setEnd(LocalDateTime.now().plusHours(2));
        when(bookingService.create(any(BookingDto.class), eq(userId))).thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.itemId").value(1L));
        verify(bookingService).create(any(BookingDto.class), eq(userId));
    }

    @Test
    void findByIdValidUserIdAndBookingId() throws Exception {
        long userId = 1L;
        long bookingId = 1L;
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(bookingId);
        when(bookingService.findBookingById(bookingId, userId)).thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void findAllByUserIdValidUserId() throws Exception {
        long userId = 1L;
        int from = 0;
        int size = 10;
        BookingDto bookingDto1 = new BookingDto();
        bookingDto1.setId(1L);
        BookingDto bookingDto2 = new BookingDto();
        bookingDto2.setId(2L);
        List<BookingDto> bookingList = Arrays.asList(bookingDto1, bookingDto2);
        when(bookingService.findAllBookingsByUser(State.ALL, userId, from, size)).thenReturn(bookingList);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));
    }

    @Test
    void findAllByOwnerIdValidUserId() throws Exception {
        long userId = 1L;
        int from = 0;
        int size = 10;
        BookingDto bookingDto1 = new BookingDto();
        bookingDto1.setId(1L);
        BookingDto bookingDto2 = new BookingDto();
        bookingDto2.setId(2L);
        List<BookingDto> bookingList = Arrays.asList(bookingDto1, bookingDto2);
        when(bookingService.findAllBookingsByOwner(State.ALL, userId, from, size)).thenReturn(bookingList);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));
    }
}