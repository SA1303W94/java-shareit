package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.OperationAccessException;
import ru.practicum.shareit.exception.TimeDataException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Transactional
    public BookingDto create(BookingDto bookingDto, long bookerId) {
        if (bookingDto.getEnd().isBefore(bookingDto.getStart()) ||
                bookingDto.getEnd().equals(bookingDto.getStart())) {
            throw new TimeDataException(String
                    .format("Invalid booking time start = %s  end = %s",
                            bookingDto.getStart(), bookingDto.getEnd()));
        }
        User booker = UserMapper.toUser(userService.findUserById(bookerId));
        Item item = ItemMapper.toItem(itemService.findItemById(bookingDto.getItemId(), bookerId));
        if (itemService.findOwnerId(item.getId()) == bookerId) {
            throw new NotFoundException("The owner cannot be a booker.");
        }
        if (item.getAvailable()) {
            Booking booking = Booking.builder()
                    .start(bookingDto.getStart())
                    .end(bookingDto.getEnd())
                    .item(item)
                    .booker(booker)
                    .status(BookingStatus.WAITING)
                    .build();
            return BookingMapper.toBookingDto(bookingRepository.save(booking));
        } else {
            throw new NotAvailableException("Item with id = %d is not available.");
        }
    }

    @Transactional
    public BookingDto findBookingById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("Booking with ID = %d not found.", bookingId)));
        if (booking.getBooker().getId().equals(userId) || booking.getItem().getOwnerId().equals(userId)) {
            return BookingMapper.toBookingDto(booking);
        } else {
            throw new OperationAccessException(String.format("User with ID = %d is not the owner, no access to booking.", userId));
        }
    }

    @Transactional
    public List<BookingDto> findAllBookingsByUser(String state, Long userId) {
        userService.findUserById(userId);
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case "ALL":
                return BookingMapper.toBookingDto(bookingRepository.findByBookerIdOrderByStartDesc(userId));
            case "CURRENT":
                return BookingMapper.toBookingDto(bookingRepository
                        .findByBookerIdAndEndIsAfterAndStartIsBeforeOrderByStartDesc(userId, now, now));
            case "PAST":
                return BookingMapper.toBookingDto(bookingRepository
                        .findByBookerIdAndEndIsBeforeOrderByStartDesc(userId, now));
            case "FUTURE":
                return BookingMapper.toBookingDto(bookingRepository
                        .findByBookerIdAndStartIsAfterOrderByStartDesc(userId, now));
            case "WAITING":
                return BookingMapper.toBookingDto(bookingRepository
                        .findByBookerIdAndStartIsAfterAndStatusIsOrderByStartDesc(userId, now,
                                BookingStatus.WAITING));
            case "REJECTED":
                return BookingMapper.toBookingDto(bookingRepository
                        .findByBookerIdAndStatusIsOrderByStartDesc(userId, BookingStatus.REJECTED));

        }
        throw new NotAvailableException(String.format("Unknown state: %s", state));
    }

    @Transactional
    public List<BookingDto> findAllBookingsByOwner(String state, Long ownerId) {
        userService.findUserById(ownerId);
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case "ALL":
                return BookingMapper.toBookingDto(bookingRepository.findByItemOwnerId(ownerId));
            case "CURRENT":
                return BookingMapper.toBookingDto(bookingRepository.findCurrentBookingsOwner(ownerId, now));
            case "PAST":
                return BookingMapper.toBookingDto(bookingRepository.findPastBookingsOwner(ownerId, now));
            case "FUTURE":
                return BookingMapper.toBookingDto(bookingRepository.findFutureBookingsOwner(ownerId, now));
            case "WAITING":
                return BookingMapper.toBookingDto(bookingRepository
                        .findWaitingBookingsOwner(ownerId, now, BookingStatus.WAITING));
            case "REJECTED":
                return BookingMapper.toBookingDto(bookingRepository
                        .findRejectedBookingsOwner(ownerId, BookingStatus.REJECTED));
        }
        throw new NotAvailableException(String.format("Unknown state: %s", state));
    }

    @Transactional
    public BookingDto approve(long bookingId, long userId, Boolean approve) {
        BookingDto booking = findBookingById(bookingId, userId);
        Long ownerId = itemService.findOwnerId(booking.getItem().getId());
        if (ownerId.equals(userId)
                && booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new NotAvailableException("The booking decision has already been made.");
        }
        if (!ownerId.equals(userId)) {
            throw new OperationAccessException(String.format("User with ID = %d is not the owner, no access to booking.", userId));
        }
        if (approve) {
            booking.setStatus(BookingStatus.APPROVED);
            bookingRepository.save(BookingStatus.APPROVED, bookingId);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
            bookingRepository.save(BookingStatus.REJECTED, bookingId);
        }
        return booking;
    }
}