package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.groups.Inpute;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private static final String OWNER_ID_HEADER = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @PostMapping
    public BookingDto create(@Validated({Inpute.class}) @RequestBody BookingDto bookingDto,
                             @RequestHeader(OWNER_ID_HEADER) long userId) {
        log.info("Received a POST-request to the endpoint: '/bookings' to add a booking by the user with ID = {}", userId);
        return bookingService.create(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto save(@RequestHeader(OWNER_ID_HEADER) Long userId,
                           @PathVariable Long bookingId,
                           @RequestParam Boolean approved) {
        log.info("Received a PATCH-request to the endpoint: '/bookings' to update booking with ID = {}", bookingId);
        return bookingService.approve(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto findById(@RequestHeader(OWNER_ID_HEADER) Long userId,
                               @PathVariable Long bookingId) {
        log.info("Received a GET-request to the endpoint: '/bookings' to get a booking with ID = {}", bookingId);
        return bookingService.findBookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> findAllByUserId(@RequestHeader(OWNER_ID_HEADER) Long userId,
                                            @RequestParam(defaultValue = "ALL") State state,
                                            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                            @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Received a GET-request to the endpoint: '/bookings' to get all booking of user with ID = {}", userId);
        return bookingService.findAllBookingsByUser(state, userId, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> findAllByOwnerId(@RequestHeader(OWNER_ID_HEADER) Long userId,
                                             @RequestParam(defaultValue = "ALL") State state,
                                             @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                             @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Received a GET-request to the endpoint: '/bookings' to get all booking of owner with ID = {}", userId);
        return bookingService.findAllBookingsByOwner(state, userId, from, size);
    }
}