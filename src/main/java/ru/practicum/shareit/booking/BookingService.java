package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemService itemService;

    public BookingDto create(BookingDto bookingDto, long bookerId) {
        if (bookingDto.getEnd().isBefore(bookingDto.getStart()) ||
                bookingDto.getEnd().equals(bookingDto.getStart())) {
            throw new TimeDataException(String
                    .format("Invalid booking time start = %s  end = %s",
                            bookingDto.getStart(), bookingDto.getEnd()));
        }
        User booker = UserMapper.toUser(UserMapper.toUserDto(userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException(String.format("User with ID = %d not found.", bookerId)))));
        Item item = ItemMapper.toItem(itemService.findItemById(bookingDto.getItemId(), bookerId));
        if (itemService.findOwnerId(item.getId()) == bookerId) {
            throw new OperationAccessException("The owner cannot be a booker.");
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

    public BookingDto findBookingById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("Booking with ID = %d not found.", bookingId)));
        if (booking.getBooker().getId().equals(userId) || booking.getItem().getOwnerId().equals(userId)) {
            return BookingMapper.toBookingDto(booking);
        } else {
            throw new OperationAccessException(String.format("User with ID = %d is not the owner, no access to booking.", userId));
        }
    }

    public List<BookingDto> findAllBookingsByUser(State state, Long userId, int from, int size) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with ID = %d not found.", userId)));
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        Pageable page = PageRequest.of(from / size, size, sort);
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case ALL:
                return BookingMapper.toBookingDto(bookingRepository.findByBookerId(userId, page));
            case CURRENT:
                return BookingMapper.toBookingDto(bookingRepository
                        .findByBookerIdAndEndIsAfterAndStartIsBefore(userId, now, now, page));
            case PAST:
                return BookingMapper.toBookingDto(bookingRepository
                        .findByBookerIdAndEndIsBefore(userId, now, page));
            case FUTURE:
                return BookingMapper.toBookingDto(bookingRepository
                        .findByBookerIdAndStartIsAfter(userId, now, page));
            case WAITING:
                return BookingMapper.toBookingDto(bookingRepository
                        .findByBookerIdAndStartIsAfterAndStatusIs(userId, now,
                                BookingStatus.WAITING, page));
            case REJECTED:
                return BookingMapper.toBookingDto(bookingRepository
                        .findByBookerIdAndStatusIs(userId, BookingStatus.REJECTED, page));

        }
        throw new NotAvailableException(String.format("Unknown state: %s", state));
    }

    public List<BookingDto> findAllBookingsByOwner(State state, Long ownerId, int from, int size) {
        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException(String.format("User with ID = %d not found.", ownerId)));
        Pageable page = PageRequest.of(from / size, size);
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case ALL:
                return BookingMapper.toBookingDto(bookingRepository.findByItemOwnerId(ownerId, page));
            case CURRENT:
                return BookingMapper.toBookingDto(bookingRepository.findCurrentBookingsOwner(ownerId, now, page));
            case PAST:
                return BookingMapper.toBookingDto(bookingRepository.findPastBookingsOwner(ownerId, now, page));
            case FUTURE:
                return BookingMapper.toBookingDto(bookingRepository.findFutureBookingsOwner(ownerId, now, page));
            case WAITING:
                return BookingMapper.toBookingDto(bookingRepository
                        .findWaitingBookingsOwner(ownerId, now, BookingStatus.WAITING, page));
            case REJECTED:
                return BookingMapper.toBookingDto(bookingRepository
                        .findRejectedBookingsOwner(ownerId, BookingStatus.REJECTED, page));
        }
        throw new NotAvailableException(String.format("Unknown state: %s", state));
    }

    public BookingDto approve(long bookingId, long userId, Boolean approve) {
        BookingDto booking = findBookingById(bookingId, userId);
        Long ownerId = itemService.findOwnerId(booking.getItem().getId());
        if (ownerId.equals(userId)
                && !booking.getStatus().equals(BookingStatus.WAITING)) {
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