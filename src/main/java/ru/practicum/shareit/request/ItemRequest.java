package ru.practicum.shareit.request;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder(toBuilder = true)
@Data
public class ItemRequest {
    private Long id;
    private String description;
    private String requesterName;
    private LocalDateTime created;
}