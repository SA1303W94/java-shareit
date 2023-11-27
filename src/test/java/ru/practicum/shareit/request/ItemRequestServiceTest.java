package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ItemRequestServiceTest {

    @Mock
    private ItemRequestRepository requestRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ItemRequestService itemRequestService;

    private User user;
    private UserDto userDto;
    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User(1L, "User", "user@example.com");
        userDto = UserMapper.toUserDto(user);

        itemRequest = ItemRequest.builder().id(1L).description("description").requester(user)
                .created(LocalDateTime.now()).build();

        itemRequestDto = ItemRequestDto.builder().id(1L).description("description").requester(userDto)
                .items(new ArrayList<>()).build();
    }

    @Test
    void createItemRequestTest() {
        Long userId = user.getId();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(requestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);

        ItemRequestDto createdItemRequestDto = itemRequestService.create(itemRequestDto, userId);

        assertNotNull(createdItemRequestDto);
        assertEquals(itemRequestDto.getDescription(), createdItemRequestDto.getDescription());

        verify(userRepository, times(1)).findById(userId);
        verify(requestRepository, times(1)).save(any(ItemRequest.class));
    }

    @Test
    void createItemRequestNotValidUserIdTest() {
        Long userId = 100L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.create(itemRequestDto, userId));

        verify(userRepository, times(1)).findById(userId);
        verify(requestRepository, never()).save(any(ItemRequest.class));
    }

    @Test
    void findByIdTest() {
        Long userId = 1L;
        Long requestId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(requestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findAllByItemRequest(itemRequest)).thenReturn(new ArrayList<>());

        ItemRequestDto foundItemRequestDto = itemRequestService.findById(userId, requestId);

        assertNotNull(foundItemRequestDto);
        assertEquals(itemRequestDto.getDescription(), foundItemRequestDto.getDescription());

        verify(userRepository, times(1)).findById(userId);
        verify(requestRepository, times(1)).findById(requestId);
        verify(itemRepository, times(1)).findAllByItemRequest(itemRequest);
    }

    @Test
    void findByIdNotValidUserIdTest() {
        Long userId = 100L;
        Long requestId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.findById(userId, requestId));

        verify(userRepository, times(1)).findById(userId);
        verify(requestRepository, never()).findById(requestId);
        verify(itemRepository, never()).findAllByItemRequest(any(ItemRequest.class));
    }

    @Test
    void findByIdNotValidRequestIdTest() {
        Long userId = 1L;
        Long requestId = 100L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(requestRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.findById(userId, requestId));

        verify(userRepository, times(1)).findById(userId);
        verify(requestRepository, times(1)).findById(requestId);
        verify(itemRepository, never()).findAllByItemRequest(any(ItemRequest.class));
    }

    @Test
    void findUserRequestsValidUserIdTest() {
        Long userId = user.getId();
        Item item = new Item();
        item.setId(1L);
        item.setItemRequest(itemRequest);
        List<Item> itemList = new ArrayList<>();
        itemList.add(item);
        itemRequest.setItems(itemList);
        List<ItemRequest> itemRequests = new ArrayList<>();
        itemRequests.add(itemRequest);
        List<Item> listItems = itemRequest.getItems();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(requestRepository.findByRequesterIdOrderByCreatedDesc(userId)).thenReturn(itemRequests);
        when(itemRepository.findAllByItemRequestIn(itemRequests)).thenReturn(listItems);

        List<ItemRequestDto> itemRequestDtoList = itemRequestService.findUserRequests(userId);
        assertNotNull(itemRequestDtoList);
        assertEquals(1, itemRequestDtoList.size());
        assertEquals(itemRequest.getDescription(), itemRequestDtoList.get(0).getDescription());
        verify(userRepository, times(1)).findById(userId);
        verify(requestRepository, times(1)).findByRequesterIdOrderByCreatedDesc(userId);
    }

    @Test
    void findUserRequestsNotValidUserIdTest() {
        Long userId = 100L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemRequestService.findUserRequests(userId));
        verify(userRepository, times(1)).findById(userId);
        verify(requestRepository, never()).findByRequesterIdOrderByCreatedDesc(anyLong());
    }

    @Test
    void findRequestsValidUserIdTest() {
        Long userId = user.getId();
        int from = 0;
        int size = 10;
        Item item = new Item();
        item.setId(1L);
        item.setItemRequest(itemRequest);
        List<Item> itemList = new ArrayList<>();
        itemList.add(item);
        itemRequest.setItems(itemList);
        List<ItemRequest> itemRequests = new ArrayList<>();
        itemRequests.add(itemRequest);
        List<Item> listItems = itemRequest.getItems();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(requestRepository.findByRequesterIdIsNot(eq(userId), any())).thenReturn(itemRequests);
        when(itemRepository.findAllByItemRequestIn(itemRequests)).thenReturn(listItems);

        List<ItemRequestDto> itemRequestDtoList = itemRequestService.findRequests(userId, from, size);
        assertNotNull(itemRequestDtoList);
        assertEquals(1, itemRequestDtoList.size());
        assertEquals(itemRequest.getDescription(), itemRequestDtoList.get(0).getDescription());
        verify(userRepository, times(1)).findById(userId);
        verify(requestRepository, times(1)).findByRequesterIdIsNot(eq(userId), any());
    }
}