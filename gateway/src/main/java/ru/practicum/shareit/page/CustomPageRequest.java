package ru.practicum.shareit.page;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class CustomPageRequest extends PageRequest {
    public CustomPageRequest(int from, int size, Sort sort) {
        super(from / size, size, sort);
    }
}