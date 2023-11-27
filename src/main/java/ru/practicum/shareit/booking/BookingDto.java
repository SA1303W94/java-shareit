package ru.practicum.shareit.booking;

import lombok.*;
import ru.practicum.shareit.groups.Inpute;
import ru.practicum.shareit.groups.Output;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.user.UserDto;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BookingDto {
    @NotNull(groups = {Output.class})
    private Long id;
    @NotNull(groups = {Inpute.class, Output.class, Short.class})
    @FutureOrPresent(groups = {Inpute.class, Output.class, Short.class})
    private LocalDateTime start;
    @NotNull(groups = {Inpute.class, Output.class, Short.class})
    @Future(groups = {Inpute.class, Output.class, Short.class})
    private LocalDateTime end;
    @NotNull(groups = {Inpute.class, Output.class, Short.class})
    private Long itemId;
    private ItemDto item;
    private UserDto booker;
    private BookingStatus status;
    private Long bookerId;
}