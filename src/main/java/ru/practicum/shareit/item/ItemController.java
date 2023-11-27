package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.groups.Create;
import ru.practicum.shareit.groups.Update;
import ru.practicum.shareit.item.comment.CommentDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/items")
public class ItemController {
    private static final String OWNER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@Validated({Create.class}) @RequestBody ItemDto itemDto,
                          @RequestHeader(OWNER_ID_HEADER) Long ownerId) {
        log.info("Received a POST-request to the endpoint: '/items' to add an item by the owner with ID = {}", ownerId);
        return itemService.create(ownerId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto save(@Validated({Update.class}) @RequestBody ItemDto itemDto,
                        @PathVariable Long itemId,
                        @RequestHeader(OWNER_ID_HEADER) Long ownerId) {
        log.info("Received a PATCH-request to the endpoint: '/items' to update item with ID = {}", itemId);
        return itemService.save(itemDto, itemId, ownerId);
    }

    @GetMapping("/{itemId}")
    public ItemDto findById(@RequestHeader(OWNER_ID_HEADER) Long userId,
                            @PathVariable Long itemId) {
        log.info("Received a GET-request to the endpoint: '/items' to get an item with ID = {}", itemId);
        return itemService.findItemById(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> findAll(@RequestHeader(OWNER_ID_HEADER) Long userId,
                                 @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                 @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Received a GET-request to the endpoint: '/items' to get all items of owner with ID = {}", userId);
        return itemService.findAllUsersItems(userId, from, size);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@PathVariable Long itemId,
                       @RequestHeader(OWNER_ID_HEADER) Long ownerId) {
        log.info("Received a DELETE-request to the endpoint: '/items' to delete item with ID = {}", itemId);
        itemService.deleteById(itemId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> search(@RequestParam String text,
                                      @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                      @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Received a GET-request to the endpoint: '/items/search' to search item with text = {}", text);
        return itemService.search(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader(OWNER_ID_HEADER) Long userId,
                                    @PathVariable Long itemId,
                                    @Valid @RequestBody CommentDto commentDto) {
        log.info("Received a POST-request to the endpoint: '/items/{itemId}/comment' to add a comment");
        return itemService.addComment(itemId, userId, commentDto);
    }
}