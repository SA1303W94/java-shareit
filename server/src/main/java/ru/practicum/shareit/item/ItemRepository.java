package ru.practicum.shareit.item;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.ItemRequest;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerId(Long userId, Pageable page);

    @Query("SELECT i FROM Item i "
            + "WHERE upper(i.name) like upper(concat('%', ?1, '%')) "
            + "OR upper(i.description) like upper(concat('%', ?1, '%')) "
            + "AND i.available = true")
    List<Item> searchAvailableItems(String text, Pageable page);

    List<Item> findAllByItemRequest(ItemRequest itemRequest);

    List<Item> findAllByItemRequestIn(List<ItemRequest> itemRequestList);
}