package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private static final String OWNER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemRequestService requestService;

    @PostMapping
    public ItemRequestDto create(@RequestHeader(OWNER_ID_HEADER) Long userId,
                                 @RequestBody ItemRequestDto itemRequestDto) {
        return requestService.create(itemRequestDto, userId);
    }

    @GetMapping("{requestId}")
    public ItemRequestDto findRequestById(@RequestHeader(OWNER_ID_HEADER) Long userId,
                                          @PathVariable Long requestId) {
        return requestService.findById(userId, requestId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> findRequests(@RequestHeader(OWNER_ID_HEADER) Long userId,
                                             @RequestParam(defaultValue = "0") int from,
                                             @RequestParam(defaultValue = "10") int size) {
        return requestService.findRequests(userId, from, size);
    }

    @GetMapping
    public List<ItemRequestDto> getUserRequests(@RequestHeader(OWNER_ID_HEADER) Long userId) {
        return requestService.findUserRequests(userId);
    }
}