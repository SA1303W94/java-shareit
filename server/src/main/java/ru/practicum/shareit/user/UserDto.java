package ru.practicum.shareit.user;

import lombok.*;
import ru.practicum.shareit.groups.Create;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String name;
    private String email;
}