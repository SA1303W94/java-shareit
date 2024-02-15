package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.groups.Create;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private static final String OWNER_ID_HEADER = "X-Sharer-User-Id";
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(OWNER_ID_HEADER) long userId,
                                         @RequestBody @Validated({Create.class}) BookItemRequestDto requestDto) {
        log.info("Creating booking {}, userId={}", requestDto, userId);
        return bookingClient.create(userId, requestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getBookings(@RequestHeader(OWNER_ID_HEADER) long userId,
                                              @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getAllBookings(userId, state, from, size);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader(OWNER_ID_HEADER) long userId,
                                             @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBookingById(userId, bookingId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBooking(@PathVariable Long bookingId,
                                                @RequestHeader(OWNER_ID_HEADER) Long userId,
                                                @RequestParam @NotNull Boolean approved) {
        log.info("patch approved userId={} bookingId={}", userId, bookingId);
        return bookingClient.approve(bookingId, userId, approved);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllBookingsByOwner(@RequestParam(defaultValue = "ALL") String state,
                                                        @RequestHeader(OWNER_ID_HEADER) Long userId,
                                                        @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                        @Positive @RequestParam(defaultValue = "10") Integer size) {
        BookingState stateParam = BookingState.from(state)
                .orElseThrow(() -> new BadRequestException("Unknown state: " + state));
        log.info("Get booking with state {}, userId={}, from={}, size={}", state, userId, from, size);
        return bookingClient.getBookingsByOwner(userId, stateParam, from, size);
    }
}