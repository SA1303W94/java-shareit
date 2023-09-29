package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private static final String OWNER_ID_HEADER = "X-Sharer-User-Id";
    @Autowired
    private ItemService itemService;

//    public ItemController(ItemService itemService) {
//        this.itemService = itemService;
//    }

    @PostMapping
    public ItemDto create(@Valid @RequestBody ItemDto itemDto, @RequestHeader(OWNER_ID_HEADER) Long ownerId) {
        log.info("Received a POST-request to the endpoint: '/items' to add an item by the owner with ID = {}", ownerId);
        return itemService.create(itemDto, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestBody ItemDto itemDto, @PathVariable Long itemId, @RequestHeader(OWNER_ID_HEADER) Long ownerId) {
        log.info("Received a PATCH-request to the endpoint: '/items' to update item with ID = {}", itemId);
        return itemService.update(itemDto, ownerId, itemId);
    }

    @DeleteMapping("/{itemId}")
    public ItemDto delete(@PathVariable Long itemId, @RequestHeader(OWNER_ID_HEADER) Long ownerId) {
        log.info("Received a DELETE-request to the endpoint: '/items' to delete item with ID = {}", itemId);
        return itemService.delete(itemId, ownerId);
    }

    @GetMapping
    public List<ItemDto> getItemsByOwner(@RequestHeader(OWNER_ID_HEADER) Long ownerId) {
        log.info("Received a GET-request to the endpoint: '/items' to get all items of owner with ID = {}", ownerId);
        return itemService.getItemsByOwner(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemsBySearchQuery(@RequestParam String text) {
        log.info("Received a GET-request to the endpoint: '/items/search' to search item with text = {}", text);
        return itemService.getItemsBySearchQuery(text);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Long itemId) {
        log.info("Received a GET-request to the endpoint: '/items' to get an item with ID = {}", itemId);
        return itemService.getItemById(itemId);
    }
}