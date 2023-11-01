package ru.practicum.shareit.item;

import lombok.*;
import ru.practicum.shareit.booking.BookingDto;
import ru.practicum.shareit.groups.Create;
import ru.practicum.shareit.item.comment.CommentDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ItemDto {
    private Long id;

    @NotBlank(groups = {Create.class})
    @NotNull(groups = {Create.class})
    private String name;

    @NotBlank(groups = {Create.class})
    @NotNull(groups = {Create.class})
    private String description;

    @NotNull(groups = {Create.class})
    private Boolean available;
    @NotNull(groups = {Short.class})
    private BookingDto lastBooking;
    @NotNull(groups = {Short.class})
    private BookingDto nextBooking;
    private List<CommentDto> comments;
}