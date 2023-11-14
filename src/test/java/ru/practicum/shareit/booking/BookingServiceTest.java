package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.OperationAccessException;
import ru.practicum.shareit.exception.TimeDataException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    BookingDto bookingDto;
    Item item;
    Booking booking;
    UserDto bookerDto;
    User booker;
    UserDto userDto;
    User user;
    @InjectMocks
    private BookingService bookingService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemService itemService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        bookingService = new BookingService(bookingRepository, userRepository, itemService);

        bookerDto = UserDto.builder().id(1L).name("a").email("a@mail.ru").build();
        booker = UserMapper.toUser(bookerDto);

        userDto = UserDto.builder().id(2L).name("b").email("b@mail.ru").build();
        user = UserMapper.toUser(userDto);

        item = Item.builder().id(1L).name("").description("").available(true).ownerId(user.getId()).build();

        booking = Booking.builder().id(1L).start(LocalDateTime.now()).end(LocalDateTime.now().plusHours(2))
                .item(item).booker(booker).build();
        bookingDto = BookingMapper.toBookingDto(booking);
        bookingDto.setItemId(item.getId());

    }

    @Test
    public void testCreateValidData() {
        long bookerId = booker.getId();
        when(userRepository.findById(bookerId)).thenReturn(Optional.ofNullable(booker));
        when(itemService.findItemById(bookingDto.getItemId(), bookerId)).thenReturn(ItemMapper.toItemDto(item));
        when(itemService.findOwnerId(item.getId())).thenReturn(2L);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        BookingDto result = bookingService.create(bookingDto, bookerId);
        assertNotNull(result);
    }

    @Test
    public void testCreateInvalidTimeData() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setEnd(LocalDateTime.now().minusHours(2));
        assertThrows(TimeDataException.class, () -> bookingService.create(bookingDto, 1L));
    }

    @Test
    public void testCreateOwnerAsBooker() {
        long bookerId = 1L;
        BookingDto bookingDto = new BookingDto();
        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setEnd(LocalDateTime.now().plusHours(2));
        bookingDto.setItemId(1L);
        User booker = new User();
        booker.setId(bookerId);
        Item item = new Item();
        item.setId(bookingDto.getItemId());
        item.setAvailable(true);
        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(itemService.findItemById(bookingDto.getItemId(), bookerId)).thenReturn(ItemMapper.toItemDto(item));
        when(itemService.findOwnerId(item.getId())).thenReturn(bookerId);
        assertThrows(OperationAccessException.class, () -> bookingService.create(bookingDto, bookerId));
    }

    @Test
    public void testCreateItemNotAvailable() {
        Long bookerId = booker.getId();
        item.setAvailable(false);
        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(itemService.findItemById(bookingDto.getItemId(), bookerId)).thenReturn(ItemMapper.toItemDto(item));
        when(itemService.findOwnerId(item.getId())).thenReturn(2L);
        assertThrows(NotAvailableException.class, () -> bookingService.create(bookingDto, bookerId));
    }

    @Test
    public void testFindBookingByIdValid() {
        long bookingId = booking.getId();
        long userId = 1L;
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        BookingDto result = bookingService.findBookingById(bookingId, userId);
        assertEquals(bookingDto.getId(), result.getId());
    }

    @Test
    public void testFindBookingByIdInvalidBookingId() {
        long bookingId = 1L;
        long userId = 2L;
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> bookingService.findBookingById(bookingId, userId));
    }

    @Test
    public void findAllBookingsByUserValidTest() {
        long userId = 2L;
        int from = 0;
        int size = 10;
        List<Booking> bookings = Collections.singletonList(booking);
        List<BookingDto> expectedBookingDtoList = BookingMapper.toBookingDto(bookings);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerId(eq(userId), any())).thenReturn(bookings);
        List<BookingDto> result = bookingService.findAllBookingsByUser(State.ALL, userId, from, size);
        assertEquals(expectedBookingDtoList.size(), result.size());
    }

    @Test
    void findAllBookingsByUserCurrentState() {
        long userId = 1L;
        int from = 0;
        int size = 10;
        List<Booking> bookings = Collections.singletonList(booking);
        List<BookingDto> expectedBookingDtoList = BookingMapper.toBookingDto(bookings);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerIdAndEndIsAfterAndStartIsBefore(eq(userId), any(), any(), any())).thenReturn(bookings);

        List<BookingDto> result = bookingService.findAllBookingsByUser(State.CURRENT, userId, from, size);
        assertEquals(expectedBookingDtoList.size(), result.size());
    }

    @Test
    void findAllBookingsByUserPastState() {
        long userId = 1L;
        int from = 0;
        int size = 10;
        List<Booking> bookings = Collections.singletonList(booking);
        List<BookingDto> expectedBookingDtoList = BookingMapper.toBookingDto(bookings);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerIdAndEndIsBefore(eq(userId), any(), any())).thenReturn(bookings);

        List<BookingDto> result = bookingService.findAllBookingsByUser(State.PAST, userId, from, size);

        assertEquals(expectedBookingDtoList.size(), result.size());
    }

    @Test
    void findAllBookingsByUser_FutureState_ReturnsBookingDtoList() {
        long userId = 1L;
        int from = 0;
        int size = 10;
        List<Booking> bookings = Collections.singletonList(booking);
        List<BookingDto> expectedBookingDtoList = BookingMapper.toBookingDto(bookings);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerIdAndStartIsAfter(eq(userId), any(), any())).thenReturn(bookings);

        List<BookingDto> result = bookingService.findAllBookingsByUser(State.FUTURE, userId, from, size);

        assertEquals(expectedBookingDtoList.size(), result.size());
    }

    @Test
    void findAllBookingsByUser_WaitingState_ReturnsBookingDtoList() {
        long userId = 1L;
        int from = 0;
        int size = 10;
        List<Booking> bookings = Collections.singletonList(booking);
        List<BookingDto> expectedBookingDtoList = BookingMapper.toBookingDto(bookings);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerIdAndStartIsAfterAndStatusIs(eq(userId), any(), eq(BookingStatus.WAITING), any())).thenReturn(bookings);

        List<BookingDto> result = bookingService.findAllBookingsByUser(State.WAITING, userId, from, size);

        assertEquals(expectedBookingDtoList.size(), result.size());
    }

    @Test
    void findAllBookingsByUser_RejectedState_ReturnsBookingDtoList() {
        long userId = 1L;
        int from = 0;
        int size = 10;
        List<Booking> bookings = Collections.singletonList(booking);
        List<BookingDto> expectedBookingDtoList = BookingMapper.toBookingDto(bookings);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerIdAndStatusIs(eq(userId), eq(BookingStatus.REJECTED), any())).thenReturn(bookings);

        List<BookingDto> result = bookingService.findAllBookingsByUser(State.REJECTED, userId, from, size);

        assertEquals(expectedBookingDtoList.size(), result.size());
    }

    @Test
    void findAllBookingsByUserUnknownState() {
        long userId = 1L;
        int from = 0;
        int size = 10;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(NotAvailableException.class, () -> bookingService.findAllBookingsByUser(State.UNSUPPORTED_STATUS, userId, from, size));
    }

    @Test
    void findAllBookingsByUserInvalidUserId() {
        long userId = 1L;
        int from = 0;
        int size = 10;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.findAllBookingsByUser(State.ALL, userId, from, size));
    }

    @Test
    public void findAllBookingsByUserNotFoundTest() {
        long userId = 2L;
        int from = 0;
        int size = 10;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> {
            bookingService.findAllBookingsByUser(State.ALL, userId, from, size);
        });
        verify(userRepository).findById(userId);
    }

    @Test
    public void findAllBookingsByOwnerValidTest() {
        long ownerId = item.getOwnerId();
        int from = 0;
        int size = 10;
        State state = State.ALL;
        List<Booking> bookings = Collections.singletonList(booking);
        List<BookingDto> expectedBookingDtoList = BookingMapper.toBookingDto(bookings);
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(user));
        when(bookingRepository.findByItemOwnerId(anyLong(), any())).thenReturn(bookings);
        List<BookingDto> result = bookingService.findAllBookingsByOwner(state, ownerId, from, size);
        assertEquals(expectedBookingDtoList.size(), result.size());
    }

    @Test
    void findAllBookingsByOwnerCurrentState() {
        long userId = 1L;
        int from = 0;
        int size = 10;
        List<Booking> bookings = Collections.singletonList(booking);
        List<BookingDto> expectedBookingDtoList = BookingMapper.toBookingDto(bookings);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findCurrentBookingsOwner(eq(userId), any(), any())).thenReturn(bookings);

        List<BookingDto> result = bookingService.findAllBookingsByOwner(State.CURRENT, userId, from, size);
        assertEquals(expectedBookingDtoList.size(), result.size());
    }

    @Test
    void findAllBookingsByOwnerPastState() {
        long userId = 1L;
        int from = 0;
        int size = 10;
        List<Booking> bookings = Collections.singletonList(booking);
        List<BookingDto> expectedBookingDtoList = BookingMapper.toBookingDto(bookings);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findPastBookingsOwner(eq(userId), any(), any())).thenReturn(bookings);

        List<BookingDto> result = bookingService.findAllBookingsByOwner(State.PAST, userId, from, size);

        assertEquals(expectedBookingDtoList.size(), result.size());
    }

    @Test
    void findAllBookingsByOwnerFutureState() {
        long userId = 1L;
        int from = 0;
        int size = 10;
        List<Booking> bookings = Collections.singletonList(booking);
        List<BookingDto> expectedBookingDtoList = BookingMapper.toBookingDto(bookings);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findFutureBookingsOwner(eq(userId), any(), any())).thenReturn(bookings);

        List<BookingDto> result = bookingService.findAllBookingsByOwner(State.FUTURE, userId, from, size);

        assertEquals(expectedBookingDtoList.size(), result.size());
    }

    @Test
    void findAllBookingsByOwnerWaitingState() {
        long userId = 1L;
        int from = 0;
        int size = 10;
        List<Booking> bookings = Collections.singletonList(booking);
        List<BookingDto> expectedBookingDtoList = BookingMapper.toBookingDto(bookings);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findWaitingBookingsOwner(eq(userId), any(), eq(BookingStatus.WAITING), any())).thenReturn(bookings);

        List<BookingDto> result = bookingService.findAllBookingsByOwner(State.WAITING, userId, from, size);

        assertEquals(expectedBookingDtoList.size(), result.size());
    }

    @Test
    void findAllBookingsByOwnerRejectedState() {
        long userId = 1L;
        int from = 0;
        int size = 10;
        List<Booking> bookings = Collections.singletonList(booking);
        List<BookingDto> expectedBookingDtoList = BookingMapper.toBookingDto(bookings);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findRejectedBookingsOwner(eq(userId), eq(BookingStatus.REJECTED), any())).thenReturn(bookings);

        List<BookingDto> result = bookingService.findAllBookingsByOwner(State.REJECTED, userId, from, size);
        assertEquals(expectedBookingDtoList.size(), result.size());
    }

    @Test
    public void testApproveValidData() {
        long bookingId = booking.getId();
        long userId = user.getId();
        Boolean approve = true;
        booking.setStatus(BookingStatus.WAITING);
        BookingDto expectedBookingDto = BookingMapper.toBookingDto(booking);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.ofNullable(booking));
        when(itemService.findOwnerId(booking.getItem().getId())).thenReturn(item.getOwnerId());
        BookingDto result = bookingService.approve(bookingId, userId, approve);
        assertEquals(expectedBookingDto.getId(), result.getId());
        assertEquals(BookingStatus.APPROVED, result.getStatus());
    }

    @Test
    public void testApproveValidDataFalse() {
        long bookingId = booking.getId();
        long userId = user.getId();
        Boolean approve = false;
        booking.setStatus(BookingStatus.WAITING);
        BookingDto expectedBookingDto = BookingMapper.toBookingDto(booking);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.ofNullable(booking));
        when(itemService.findOwnerId(booking.getItem().getId())).thenReturn(item.getOwnerId());
        BookingDto result = bookingService.approve(bookingId, userId, approve);
        assertEquals(expectedBookingDto.getId(), result.getId());
        assertEquals(BookingStatus.REJECTED, result.getStatus());
    }
}