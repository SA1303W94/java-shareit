package ru.practicum.shareit.item;

import lombok.*;
import ru.practicum.shareit.groups.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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
    private Long requestId;
}