package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.OperationAccessException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.request.ItemRequestDto;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static org.springframework.data.domain.Sort.Direction.DESC;


@Service
@RequiredArgsConstructor
@Transactional
public class ItemService {
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestService requestService;
    private final ItemRequestRepository itemRequestRepository;

    public ItemDto create(Long userId, ItemDto itemDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with ID = %d not found.", userId)));
        Item item = ItemMapper.toItem(itemDto);
        item.setOwnerId(userId);
        if (itemDto.getRequestId() != null) {
            ItemRequestDto itemRequest = requestService.findById(userId, itemDto.getRequestId());
            item.setItemRequest(ItemRequestMapper.toItemRequest(itemRequest));
        }
        return ItemMapper.toItemDto(itemRepository.save(item));
    }


    public ItemDto save(ItemDto itemDto, Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item with ID = %d not found.", itemId)));
        userService.findUserById(userId);
        if (!item.getOwnerId().equals(userId)) {
            throw new OperationAccessException(String.format("User with ID = %d is not an owner, update is not available.", userId));
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    public ItemDto findItemById(Long itemId, Long userId) {
        ItemDto result;
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item with ID = %d not found.", itemId)));
        result = ItemMapper.toItemDto(item);
        if (Objects.equals(item.getOwnerId(), userId)) {
            result = updateBookings(result);
        }
        List<Comment> comments = commentRepository.findAllByItemId(result.getId());
        result.setComments(CommentMapper.toDtoList(comments));
        return result;
    }

    public List<ItemDto> findAllUsersItems(Long userId, Integer from, Integer size) {
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        Pageable page = PageRequest.of(from / size, size, sort);
        List<Item> items = itemRepository.findAllByOwnerId(userId, page);
        return updateBookingsAndComments(items);
    }

    public List<ItemDto> updateBookingsAndComments(List<Item> items) {
        Map<Item, List<Comment>> comments = commentRepository.findByItemIn(items, Sort.by(DESC, "created"))
                .stream()
                .collect(Collectors.groupingBy(Comment::getItem, Collectors.toList()));
        Map<Item, List<Booking>> bookings = bookingRepository.findByItemIn(items, Sort.by("start"))
                .stream()
                .collect(Collectors.groupingBy(Booking::getItem, Collectors.toList()));

        LocalDateTime now = LocalDateTime.now();
        for (Item item : items) {
            List<Comment> itemComments = comments.get(item);
            if (itemComments != null) {
                item.setComments(CommentMapper.toDtoList(itemComments));
            }
            List<Booking> itemBookings = bookings.get(item);
            if (itemBookings != null) {
                Booking lastBooking = itemBookings.stream()
                        .filter(booking -> booking.getStart().isBefore(now))
                        .max(Comparator.comparing(Booking::getStart))
                        .orElse(null);
                Booking nextBooking = itemBookings.stream()
                        .filter(booking -> booking.getStart().isAfter(now))
                        .min(Comparator.comparing(Booking::getStart))
                        .orElse(null);
                if (lastBooking != null) {
                    item.setLastBooking(BookingMapper.toItemBookingDto(lastBooking));
                }
                if (nextBooking != null) {
                    item.setNextBooking(BookingMapper.toItemBookingDto(nextBooking));
                }
            }
        }
        return items.stream().map(ItemMapper::toItemDtoLong).collect(toList());
    }

    public ItemDto updateBookings(ItemDto itemDto) {
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = bookingRepository.findBookingsItem(itemDto.getId());
        Booking lastBooking = bookings.stream()
                .filter(obj -> !(obj.getStatus().equals(BookingStatus.REJECTED)))
                .filter(obj -> obj.getStart().isBefore(now))
                .min((obj1, obj2) -> obj2.getStart().compareTo(obj1.getStart())).orElse(null);
        Booking nextBooking = bookings.stream()
                .filter(obj -> !(obj.getStatus().equals(BookingStatus.REJECTED)))
                .filter(obj -> obj.getStart().isAfter(now))
                .min(Comparator.comparing(Booking::getStart)).orElse(null);
        if (lastBooking != null) {
            itemDto.setLastBooking(BookingMapper.toItemBookingDto(lastBooking));
        }
        if (nextBooking != null) {
            itemDto.setNextBooking(BookingMapper.toItemBookingDto(nextBooking));
        }
        return itemDto;
    }

    public void deleteById(Long itemId) {
        itemRepository.deleteById(itemId);
    }

    public List<ItemDto> search(String text, Integer from, Integer size) {
        Pageable page = PageRequest.of(from / size, size);
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.searchAvailableItems(text, page).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }


    public Long findOwnerId(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item with ID = %d not found.", itemId)))
                .getOwnerId();
    }

    public CommentDto addComment(Long itemId, Long userId, CommentDto commentDto) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item with ID = %d not found.", itemId)));
        User user = UserMapper.toUser(userService.findUserById(userId));
        List<Booking> bookings = bookingRepository
                .findByItemIdAndBookerIdAndStatusIsAndEndIsBefore(itemId, userId, BookingStatus.APPROVED, LocalDateTime.now());
        if (!bookings.isEmpty() && bookings.get(0).getStart().isBefore(LocalDateTime.now())) {
            Comment comment = CommentMapper.toComment(commentDto);
            comment.setItem(item);
            comment.setAuthor(user);
            comment.setCreated(LocalDateTime.now());
            return CommentMapper.toDto(commentRepository.save(comment));
        } else {
            throw new NotAvailableException(String.format("Booking for user with ID = %d and item with ID = %d not found.", userId, itemId));
        }
    }

}