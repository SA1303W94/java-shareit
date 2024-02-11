package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.groups.Create;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookItemRequestDto {
    private long itemId;
    @FutureOrPresent(groups = {Create.class})
    @NotNull(groups = {Create.class})
    private LocalDateTime start;
    @Future(groups = {Create.class})
    @NotNull(groups = {Create.class})
    private LocalDateTime end;
}
