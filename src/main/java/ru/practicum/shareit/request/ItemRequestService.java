package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
@AllArgsConstructor
public class ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Transactional
    public ItemRequestDto create(ItemRequestDto itemRequestDto, Long userId) {
        User user = checkUser(userId);
        ItemRequest itemRequest = ItemRequest.builder()
                .description(itemRequestDto.getDescription())
                .requester(user)
                .created(LocalDateTime.now())
                .build();
        ItemRequestDto res = null;
        if (itemRequest != null) {
            res = ItemRequestMapper.toItemRequestDto(requestRepository.save(itemRequest));
        }
        return res;
    }

    @Transactional
    public ItemRequestDto findById(Long userId, Long requestId) {
        UserDto user = UserMapper.toUserDto(checkUser(userId));
        ItemRequest itemRequest = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format("Request with id = %d not found.", requestId)));
        itemRequest.setItems(itemRepository.findAllByItemRequest(itemRequest));
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
        itemRequestDto.setRequester(user);
        return itemRequestDto;
    }

    @Transactional
    public List<ItemRequestDto> findRequests(Long userId, int from, int size) {
        checkUser(userId);
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        Pageable page = PageRequest.of(from / size, size, sort);
        List<ItemRequest> allItemRequest = requestRepository.findByRequesterIdIsNot(userId, page);
        Map<ItemRequest, List<Item>> items = itemRepository.findAllByItemRequestIn(allItemRequest)
                .stream()
                .collect(groupingBy(Item::getItemRequest, Collectors.toList()));
        for (ItemRequest itemRequest : allItemRequest) {
            List<Item> itemsNew = items.get(itemRequest);
            itemRequest.setItems(itemsNew);
        }
        return allItemRequest.stream().map(ItemRequestMapper::toItemRequestDto).collect(Collectors.toList());
    }

    @Transactional
    public List<ItemRequestDto> findUserRequests(Long userId) {
        checkUser(userId);
        List<ItemRequest> allItemRequest = requestRepository.findByRequesterIdOrderByCreatedDesc(userId);
        Map<ItemRequest, List<Item>> items = itemRepository.findAllByItemRequestIn(allItemRequest)
                .stream()
                .collect(groupingBy(Item::getItemRequest, Collectors.toList()));
        for (ItemRequest itemRequest : allItemRequest) {
            List<Item> itemsNew = items.get(itemRequest);
            itemRequest.setItems(itemsNew);
        }
        return allItemRequest.stream().map(ItemRequestMapper::toItemRequestDto).collect(Collectors.toList());
    }

    private User checkUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("пользователя c id " + userId + " нет"));
    }
}