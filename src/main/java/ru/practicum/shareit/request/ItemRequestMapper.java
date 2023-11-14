package ru.practicum.shareit.request;


import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.UserMapper;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
public class ItemRequestMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest request) {
        return ItemRequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .requester(UserMapper.toUserDto(request.getRequester()))
                .items(request.getItems() != null ? request.getItems()
                        .stream()
                        .map(ItemMapper::toItemDto)
                        .collect(Collectors.toList())
                        : new ArrayList<>())
                .build();
    }

    public static ItemRequest toItemRequest(ItemRequestDto requestDto) {
        return ItemRequest.builder()
                .id(requestDto.getId())
                .description(requestDto.getDescription())
                .requester(UserMapper.toUser(requestDto.getRequester()))
                .created(requestDto.getCreated())
                .items(requestDto.getItems() != null ? requestDto.getItems()
                        .stream()
                        .map(ItemMapper::toItem)
                        .collect(Collectors.toList())
                        : new ArrayList<>())
                .build();
    }
}