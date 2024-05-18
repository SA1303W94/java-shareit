package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.request.ItemRequestDto;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.user.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ItemRequestService requestService;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @InjectMocks
    private ItemService itemService;

    private Item item;
    private ItemDto itemDto;
    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {

        item = new Item();
        item.setId(1L);
        item.setName("Item");
        item.setDescription("Description");
        item.setOwnerId(1L);
        item.setAvailable(true);

        itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Item");
        itemDto.setDescription("Description");
        itemDto.setOwnerId(1L);
        itemDto.setAvailable(true);

        user = new User();
        user.setId(1L);
        user.setName("User");
        user.setEmail("user@example.com");

        userDto = UserMapper.toUserDto(user);
    }

    @Test
    void createItemTest() {
        Long userId = user.getId();
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setDescription("Description");
        itemRequestDto.setRequester(userDto);

        itemDto.setRequestId(1L);
        itemDto.setOwnerId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        when(requestService.findById(userId, itemDto.getRequestId())).thenReturn(itemRequestDto);

        ItemDto createdItemDto = itemService.create(userId, itemDto);

        assertNotNull(createdItemDto);
        assertEquals(itemDto.getName(), createdItemDto.getName());
        assertEquals(itemDto.getDescription(), createdItemDto.getDescription());

        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void createItemWithInvalidUserIdTest() {
        Long userId = 100L;
        ItemDto newItemDto = new ItemDto();
        newItemDto.setName("New Item");
        newItemDto.setDescription("New Description");
        newItemDto.setOwnerId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.create(userId, newItemDto));

        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void saveItemTest() {
        Long itemId = 1L;
        Long userId = 1L;
        ItemDto updatedItemDto = new ItemDto();
        updatedItemDto.setName("Updated Item");
        updatedItemDto.setDescription("Updated Description");
        updatedItemDto.setOwnerId(userId);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userService.findUserById(userId)).thenReturn(userDto);
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto savedItemDto = itemService.save(updatedItemDto, itemId, userId);

        assertNotNull(savedItemDto);
        assertEquals(updatedItemDto.getName(), savedItemDto.getName());
        assertEquals(updatedItemDto.getDescription(), savedItemDto.getDescription());

        verify(itemRepository, times(1)).findById(itemId);
        verify(userService, times(1)).findUserById(userId);
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void saveItemWithInvalidItemIdTest() {
        Long itemId = 100L;
        Long userId = 1L;
        ItemDto updatedItemDto = new ItemDto();
        updatedItemDto.setName("Updated Item");
        updatedItemDto.setDescription("Updated Description");
        updatedItemDto.setOwnerId(userId);

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.save(updatedItemDto, itemId, userId));

        verify(itemRepository, times(1)).findById(itemId);
        verify(userService, never()).findUserById(userId);
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void saveItemWithInvalidUserIdTest() {
        Long itemId = 1L;
        Long userId = 100L;
        ItemDto updatedItemDto = new ItemDto();
        updatedItemDto.setName("Updated Item");
        updatedItemDto.setDescription("Updated Description");
        updatedItemDto.setOwnerId(userId);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userService.findUserById(userId)).thenThrow(new NotFoundException("User not found"));

        assertThrows(NotFoundException.class, () -> itemService.save(updatedItemDto, itemId, userId));

        verify(itemRepository, times(1)).findById(itemId);
        verify(userService, times(1)).findUserById(userId);
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void findItemByIdTest() {
        Long itemId = 1L;
        Long userId = 1L;
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Comment");
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());

        List<Comment> comments = new ArrayList<>();
        comments.add(comment);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(itemId)).thenReturn(comments);

        ItemDto foundItemDto = itemService.findItemById(itemId, userId);

        assertNotNull(foundItemDto);
        assertEquals(itemDto.getId(), foundItemDto.getId());
        assertEquals(itemDto.getName(), foundItemDto.getName());
        assertEquals(itemDto.getDescription(), foundItemDto.getDescription());
        assertEquals(comments.size(), foundItemDto.getComments().size());

        verify(itemRepository, times(1)).findById(itemId);
        verify(commentRepository, times(1)).findAllByItemId(itemId);
    }

    @Test
    void findItemByIdWithInvalidItemIdTest() {
        Long itemId = 100L;
        Long userId = 1L;

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.findItemById(itemId, userId));

        verify(itemRepository, times(1)).findById(itemId);
        verify(commentRepository, never()).findAllByItemId(anyLong());
    }

    @Test
    void findAllUsersItemsTest() {
        Long userId = 1L;
        Integer from = 0;
        Integer size = 10;
        List<Item> itemList = new ArrayList<>();
        itemList.add(item);

        when(itemRepository.findAllByOwnerId(userId, PageRequest.of(from / size, size))).thenReturn(itemList);
        when(commentRepository.findByItemIn(eq(itemList), any(Sort.class))).thenReturn(new ArrayList<>());
        when(bookingRepository.findByItemIn(eq(itemList), any(Sort.class))).thenReturn(new ArrayList<>());

        List<ItemDto> foundItemDtoList = itemService.findAllUsersItems(userId, from, size);

        assertNotNull(foundItemDtoList);
        assertEquals(itemList.size(), foundItemDtoList.size());

        verify(itemRepository, times(1)).findAllByOwnerId(userId, PageRequest.of(from / size, size));
        verify(commentRepository, times(1)).findByItemIn(eq(itemList), any(Sort.class));
        verify(bookingRepository, times(1)).findByItemIn(eq(itemList), any(Sort.class));
    }

    @Test
    void updateBookingsAndCommentsTest() {
        List<Item> itemList = new ArrayList<>();
        itemList.add(item);

        when(commentRepository.findByItemIn(eq(itemList), any(Sort.class))).thenReturn(new ArrayList<>());
        when(bookingRepository.findByItemIn(eq(itemList), any(Sort.class))).thenReturn(new ArrayList<>());

        List<ItemDto> updatedItemDtoList = itemService.updateBookingsAndComments(itemList);

        assertNotNull(updatedItemDtoList);
        assertEquals(itemList.size(), updatedItemDtoList.size());

        verify(commentRepository, times(1)).findByItemIn(eq(itemList), any(Sort.class));
        verify(bookingRepository, times(1)).findByItemIn(eq(itemList), any(Sort.class));
    }

    @Test
    void updateBookingsTest() {
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = new ArrayList<>();

        Booking lastBooking = new Booking();
        lastBooking.setId(1L);
        lastBooking.setStatus(BookingStatus.REJECTED);
        lastBooking.setStart(now.minusDays(1));
        lastBooking.setBooker(user);

        Booking nextBooking = new Booking();
        nextBooking.setId(2L);
        nextBooking.setStatus(BookingStatus.REJECTED);
        nextBooking.setStart(now.plusDays(1));
        nextBooking.setBooker(user);

        bookings.add(lastBooking);
        bookings.add(nextBooking);
        itemDto.setLastBooking(BookingMapper.toItemBookingDto(lastBooking));
        itemDto.setNextBooking(BookingMapper.toItemBookingDto(nextBooking));

        when(bookingRepository.findBookingsItem(itemDto.getId())).thenReturn(bookings);

        ItemDto updatedItemDto = itemService.updateBookings(itemDto);

        assertNotNull(updatedItemDto);
        assertEquals(lastBooking.getId(), updatedItemDto.getLastBooking().getId());
        assertEquals(nextBooking.getId(), updatedItemDto.getNextBooking().getId());

        verify(bookingRepository, times(1)).findBookingsItem(itemDto.getId());
    }

    @Test
    void deleteByIdTest() {
        Long itemId = 1L;

        itemService.deleteById(itemId);

        verify(itemRepository, times(1)).deleteById(itemId);
    }

    @Test
    void searchTest() {
        String text = "item";
        Integer from = 0;
        Integer size = 10;
        List<Item> itemList = new ArrayList<>();
        itemList.add(item);

        when(itemRepository.searchAvailableItems(text, PageRequest.of(from / size, size))).thenReturn(itemList);

        List<ItemDto> foundItemDtoList = itemService.search(text, from, size);

        assertNotNull(foundItemDtoList);
        assertEquals(itemList.size(), foundItemDtoList.size());

        verify(itemRepository, times(1)).searchAvailableItems(text, PageRequest.of(from / size, size));
    }

    @Test
    void findOwnerIdTest() {
        Long itemId = 1L;
        Long ownerId = 1L;

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        Long foundOwnerId = itemService.findOwnerId(itemId);

        assertEquals(ownerId, foundOwnerId);

        verify(itemRepository, times(1)).findById(itemId);
    }

    @Test
    void addCommentTest() {
        LocalDateTime fixedDateTime = LocalDateTime.now();
        Long itemId = 1L;
        Long userId = 1L;
        Comment comment = Comment.builder().id(1L).text("Text").item(item).author(user).build();
        CommentDto commentDto = CommentDto.builder().id(1L).text("Text").item(itemDto).authorName("User").build();

        List<Booking> bookings = List.of(Booking.builder()
                        .id(1L).item(item).booker(user)
                        .start(fixedDateTime.minusHours(2L))
                        .end(fixedDateTime.minusHours(1L))
                        .status(BookingStatus.WAITING).build(),
                Booking.builder()
                        .id(2L).item(item).booker(user)
                        .start(fixedDateTime.plusHours(1L))
                        .end(fixedDateTime.plusHours(2L))
                        .status(BookingStatus.WAITING).build());
        item.setLastBooking(BookingMapper.toItemBookingDto(bookings.get(0)));
        item.setNextBooking(BookingMapper.toItemBookingDto(bookings.get(1)));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.findByItemIdAndBookerIdAndStatusIsAndEndIsBefore(
                eq(itemId), eq(userId), eq(BookingStatus.APPROVED), any(LocalDateTime.class))).thenReturn(bookings);
        when(userService.findUserById(userId)).thenReturn(userDto);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDto addedCommentDto = itemService.addComment(itemId, userId, commentDto);

        assertNotNull(addedCommentDto);
        verify(itemRepository, times(1)).findById(itemId);
        verify(userService, times(1)).findUserById(userId);
        verify(bookingRepository, times(1)).findByItemIdAndBookerIdAndStatusIsAndEndIsBefore(
                eq(itemId), eq(userId), eq(BookingStatus.APPROVED), any(LocalDateTime.class)
        );
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void addCommentWithInvalidItemIdTest() {
        Long itemId = 100L;
        Long userId = 1L;
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Test Comment");

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.addComment(itemId, userId, commentDto));

        verify(itemRepository, times(1)).findById(itemId);
        verify(userService, never()).findUserById(userId);
        verify(bookingRepository, never()).findByItemIdAndBookerIdAndStatusIsAndEndIsBefore(anyLong(), anyLong(), any(BookingStatus.class), any(LocalDateTime.class));
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void addCommentWithInvalidUserIdTest() {
        Long itemId = 1L;
        Long userId = 100L;
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Test Comment");

        Item item = new Item();
        item.setId(itemId);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userService.findUserById(userId)).thenThrow(new NotFoundException("User not found"));

        assertThrows(NotFoundException.class, () -> itemService.addComment(itemId, userId, commentDto));

        verify(itemRepository, times(1)).findById(itemId);
        verify(userService, times(1)).findUserById(userId);
        verify(bookingRepository, never()).findByItemIdAndBookerIdAndStatusIsAndEndIsBefore(anyLong(), anyLong(), any(BookingStatus.class), any(LocalDateTime.class));
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void addCommentWithNoApprovedBookingTest() {
        Long itemId = 1L;
        Long userId = 1L;
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Test Comment");

        Item item = new Item();
        item.setId(itemId);

        User user = new User();
        user.setId(userId);

        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = new ArrayList<>();
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.WAITING);
        booking.setStart(now.plusHours(1));
        booking.setEnd(now.plusHours(2));
        bookings.add(booking);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userService.findUserById(userId)).thenReturn(userDto);
        when(bookingRepository.findByItemIdAndBookerIdAndStatusIsAndEndIsBefore(eq(itemId), eq(userId), eq(BookingStatus.APPROVED), any(LocalDateTime.class))).thenReturn(bookings);

        assertThrows(NotAvailableException.class, () -> itemService.addComment(itemId, userId, commentDto));

        verify(itemRepository, times(1)).findById(itemId);
        verify(userService, times(1)).findUserById(userId);
        verify(bookingRepository, times(1)).findByItemIdAndBookerIdAndStatusIsAndEndIsBefore(eq(itemId), eq(userId), eq(BookingStatus.APPROVED), any(LocalDateTime.class));
        verify(commentRepository, never()).save(any(Comment.class));
    }
}