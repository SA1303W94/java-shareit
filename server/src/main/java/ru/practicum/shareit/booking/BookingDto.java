package ru.practicum.shareit.booking;

import lombok.*;
import ru.practicum.shareit.groups.Inpute;
import ru.practicum.shareit.groups.Output;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.user.UserDto;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BookingDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Long itemId;
    private ItemDto item;
    private UserDto booker;
    private BookingStatus status;
    private Long bookerId;
}