package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.booking.*;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class MockMVCTest {

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createValidBookingDtoAndUserId() {
        long userId = 1L;
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1L);

        BookingDto createdBooking = new BookingDto();
        createdBooking.setId(1L);
        createdBooking.setItemId(1L);
        when(bookingService.create(bookingDto, userId)).thenReturn(createdBooking);

        BookingDto result = bookingController.create(bookingDto, userId);

        assertEquals(createdBooking, result);
    }

    @Test
    void saveValidUserIdAndBookingIdAndApproved() {
        long userId = 1L;
        long bookingId = 1L;
        boolean approved = true;
        BookingDto savedBooking = new BookingDto();
        savedBooking.setId(bookingId);
        savedBooking.setStatus(BookingStatus.APPROVED);
        when(bookingService.approve(bookingId, userId, approved)).thenReturn(savedBooking);
        BookingDto result = bookingController.save(userId, bookingId, approved);
        assertEquals(savedBooking, result);
    }

    @Test
    void findByIdValidUserIdAndBookingId() {
        long userId = 1L;
        long bookingId = 1L;

        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(bookingId);

        when(bookingService.findBookingById(bookingId, userId)).thenReturn(bookingDto);

        BookingDto result = bookingController.findById(userId, bookingId);
        assertEquals(bookingDto, result);
    }

    @Test
    void findAllByUserIdValidUserId() {
        long userId = 1L;
        int from = 0;
        int size = 10;
        BookingDto bookingDto1 = new BookingDto();
        bookingDto1.setId(1L);

        BookingDto bookingDto2 = new BookingDto();
        bookingDto2.setId(2L);
        List<BookingDto> bookingList = Arrays.asList(bookingDto1, bookingDto2);

        when(bookingService.findAllBookingsByUser(State.ALL, userId, from, size)).thenReturn(bookingList);

        List<BookingDto> result = bookingController.findAllByUserId(userId, State.ALL, from, size);
        assertEquals(bookingList, result);
    }

    @Test
    void findAllByOwnerIdValidUserId() {
        long userId = 1L;
        int from = 0;
        int size = 10;
        BookingDto bookingDto1 = new BookingDto();
        bookingDto1.setId(1L);
        BookingDto bookingDto2 = new BookingDto();
        bookingDto2.setId(2L);
        List<BookingDto> bookingList = Arrays.asList(bookingDto1, bookingDto2);

        when(bookingService.findAllBookingsByOwner(State.ALL, userId, from, size)).thenReturn(bookingList);
        List<BookingDto> result = bookingController.findAllByOwnerId(userId, State.ALL, from, size);
        assertEquals(bookingList, result);
    }
}