package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserService;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;

    public ItemDto create(ItemDto itemDto, Long ownerId) {
        if (userService.getUserById(ownerId) == null) {
            throw new NotFoundException("User with ID = " + ownerId + " not found.");
        }
        return ItemMapper.toItemDto(itemRepository.create(ItemMapper.toItem(itemDto, ownerId)));
    }

    public ItemDto update(ItemDto itemDto, Long ownerId, Long itemId) {
        if (itemDto.getName() == null) {
            itemDto.setName(itemRepository.getItemById(itemId).getName());
        }
        if (itemDto.getDescription() == null) {
            itemDto.setDescription(itemRepository.getItemById(itemId).getDescription());
        }
        if (itemDto.getAvailable() == null) {
            itemDto.setAvailable(itemRepository.getItemById(itemId).getAvailable());
        }
        if (userService.getUserById(ownerId) == null) {
            throw new NotFoundException("User with ID = " + ownerId + " not found.");
        }
        if (itemDto.getId() == null) {
            itemDto.setId(itemId);
        }
        Item oldItem = itemRepository.getItemById(itemId);
        if (!oldItem.getOwnerId().equals(ownerId)) {
            throw new NotFoundException("User have no such item.");
        }
        return ItemMapper.toItemDto(itemRepository.update(ItemMapper.toItem(itemDto, ownerId)));
    }

    public ItemDto delete(Long itemId, Long ownerId) {
        Item item = itemRepository.getItemById(itemId);
        if (!item.getOwnerId().equals(ownerId)) {
            throw new NotFoundException("User have no such item.");
        }
        return ItemMapper.toItemDto(itemRepository.delete(itemId));
    }

    public List<ItemDto> getItemsByOwner(Long ownerId) {
        return itemRepository
                .getItemsByOwner(ownerId)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(toList());
    }

    public List<ItemDto> getItemsBySearchQuery(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        text = text.toLowerCase();
        return itemRepository.getItemsBySearchQuery(text)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(toList());
    }

    public ItemDto getItemById(Long itemId) {
        return ItemMapper.toItemDto(itemRepository.getItemById(itemId));
    }

    public void deleteItemsByOwner(Long ownerId) {
        itemRepository.deleteItemsByOwner(ownerId);
    }
}