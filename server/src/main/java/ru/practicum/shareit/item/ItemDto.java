package ru.practicum.shareit.item;

import lombok.*;
import ru.practicum.shareit.booking.BookingDto;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.request.ItemRequestDto;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ItemDto {
    private Long id;
    private String name;

    private String description;
    private Boolean available;
    private Long ownerId;
    private BookingDto lastBooking;
    private BookingDto nextBooking;
    private List<CommentDto> comments;
    private Long requestId;
    private ItemRequestDto itemRequestDto;
}