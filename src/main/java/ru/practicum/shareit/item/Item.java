package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Builder(toBuilder = true)
@Data
@AllArgsConstructor
public class Item {
    private Long id;
    @NotBlank
    private String name;
    private String description;
    private Boolean available;
    private Long ownerId;
    private Long request;
}