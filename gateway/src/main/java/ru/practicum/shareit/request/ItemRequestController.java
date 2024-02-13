package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.groups.Create;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemRequestController {
    private static final String OWNER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemRequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(OWNER_ID_HEADER) Long userId,
                                         @Validated(Create.class) @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Received a POST-request to the endpoint: '/requests' to add by the user with ID = {}", userId);
        return requestClient.createRequest(userId, itemRequestDto);
    }

    @GetMapping("{requestId}")
    public ResponseEntity<Object> findRequestById(@RequestHeader(OWNER_ID_HEADER) Long userId,
                                                  @PathVariable Long requestId) {
        log.info("Received a GET-request to the endpoint: '/requests' to get with ID = {}", requestId);
        return requestClient.getRequestById(userId, requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(
            @RequestHeader(OWNER_ID_HEADER) Long userId,
            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
            @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Received a GET-request to the endpoint: '/requests/all' to get all requests of user with ID = {}", userId);
        return requestClient.getAllRequest(userId, from, size);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUserRequest(
            @RequestHeader(OWNER_ID_HEADER) Long userId) {
        log.info("Received a GET-request to the endpoint: '/requests' to get all requests of user with ID = {}", userId);
        return requestClient.getAllUserRequest(userId);
    }
}