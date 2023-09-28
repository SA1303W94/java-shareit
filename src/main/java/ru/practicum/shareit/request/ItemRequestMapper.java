package ru.practicum.shareit.request;

import ru.practicum.shareit.request.ItemRequestDto;
import ru.practicum.shareit.request.ItemRequest;

public class ItemRequestMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .description(itemRequest.getDescription())
                .requesterName(itemRequest.getRequesterName())
                .created(itemRequest.getCreated())
                .build();
    }
}