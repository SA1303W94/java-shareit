package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.groups.Create;
import ru.practicum.shareit.user.UserDto;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
public class ItemRequestDtoJsonTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    public void testSerializeItemRequestDto() throws IOException {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("Test description")
                .requester(new UserDto())
                .created(LocalDateTime.parse("2022-01-01T13:00:00"))
                .items(new ArrayList<>())
                .build();
        String expectedJson = json.write(itemRequestDto).getJson();
        assertEquals(expectedJson, json.write(itemRequestDto).getJson());
    }

    @Test
    public void testDeserializeItemRequestDto() throws IOException {
        String jsonStr = "{\"id\": 1, \"description\": \"Test description\", \"requester\": {}, \"created\": \"2022-01-01T13:00:00\", \"items\": []}";
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("Test description")
                .requester(new UserDto())
                .created(LocalDateTime.parse("2022-01-01T13:00:00"))
                .items(new ArrayList<>())
                .build();
        ItemRequestDto newItemRequestDto = json.parse(jsonStr).getObject();
        assertEquals(newItemRequestDto.getId(), itemRequestDto.getId());
        assertEquals(newItemRequestDto.getCreated(), itemRequestDto.getCreated());
    }

    @Test
    public void testValidation() {
        ItemRequestDto itemRequestDto = new ItemRequestDto();

        Set<ConstraintViolation<ItemRequestDto>> violations = validator.validate(itemRequestDto, Create.class);
        assertEquals(1, violations.size());
    }
}