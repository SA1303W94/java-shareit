package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.groups.Create;
import ru.practicum.shareit.groups.Update;
import ru.practicum.shareit.item.comment.CommentDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/items")
public class ItemController {
    private static final String OWNER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> create(@Validated({Create.class}) @RequestBody ItemDto itemDto,
                                         @RequestHeader(OWNER_ID_HEADER) Long ownerId) {
        log.info("Received a POST-request to the endpoint: '/items' to add an item by the owner with ID = {}", ownerId);
        return itemClient.createItem(ownerId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> save(@Validated({Update.class}) @RequestBody ItemDto itemDto,
                                       @PathVariable Long itemId,
                                       @RequestHeader(OWNER_ID_HEADER) Long ownerId) {
        log.info("Received a PATCH-request to the endpoint: '/items' to update item with ID = {}", itemId);
        return itemClient.updateItem(itemDto, itemId, ownerId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findById(@RequestHeader(OWNER_ID_HEADER) Long userId,
                                           @PathVariable Long itemId) {
        log.info("Received a GET-request to the endpoint: '/items' to get an item with ID = {}", itemId);
        return itemClient.getItemById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> findAll(@RequestHeader(OWNER_ID_HEADER) Long ownerId,
                                          @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                          @RequestParam(value = "size", defaultValue = "10") @Positive Integer size) {
        log.info("Received a GET-request to the endpoint: '/items' to get all items of owner with ID = {}", ownerId);
        return itemClient.getAllUsersItems(ownerId, from, size);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@PathVariable Long itemId,
                       @RequestHeader(OWNER_ID_HEADER) Long ownerId) {
        log.info("Received a DELETE-request to the endpoint: '/items' to delete item with ID = {}", itemId);
        itemClient.deleteItem(itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestHeader(OWNER_ID_HEADER) Long userId,
                                         @RequestParam String text,
                                         @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                         @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Received a GET-request to the endpoint: '/items/search' to search item with text = {}", text);
        return itemClient.searchItems(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader(OWNER_ID_HEADER) Long userId,
                                                @PathVariable Long itemId,
                                                @Valid @RequestBody CommentDto commentDto) {
        log.info("Received a POST-request to the endpoint: '/items/{itemId}/comment' to add a comment");
        return itemClient.addComment(itemId, userId, commentDto);
    }
}