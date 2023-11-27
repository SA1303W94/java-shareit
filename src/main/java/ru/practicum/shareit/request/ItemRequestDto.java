package ru.practicum.shareit.request;

import lombok.*;
import ru.practicum.shareit.groups.Create;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.user.UserDto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ItemRequestDto {
    private Long id;

    @NotBlank(groups = {Create.class})
    private String description;
    private UserDto requester;
    private LocalDateTime created;
    private List<ItemDto> items;
}