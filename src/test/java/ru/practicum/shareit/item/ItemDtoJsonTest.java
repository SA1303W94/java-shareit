package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.groups.Create;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.IOException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
public class ItemDtoJsonTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    public void testSerializeItemDto() throws IOException {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("Test Item")
                .description("Test description")
                .available(true)
                .build();
        String expectedJson = objectMapper.writeValueAsString(itemDto);
        assertEquals(expectedJson, json.write(itemDto).getJson());
    }

    @Test
    public void testDeserializeItemDto() throws IOException {
        String jsonStr = "{\"id\": 1, \"name\": \"Test Item\", \"description\": \"Test description\", \"available\": true}";
        ItemDto expectedItemDto = ItemDto.builder()
                .id(1L)
                .name("Test Item")
                .description("Test description")
                .available(true)
                .build();
        ItemDto itemDto = json.parse(jsonStr).getObject();
        assertEquals(expectedItemDto.getId(), itemDto.getId());
        assertEquals(expectedItemDto.getDescription(), itemDto.getDescription());
    }

    @Test
    public void testValidation() {
        ItemDto itemDto = ItemDto.builder()
                .build();
        Set<ConstraintViolation<ItemDto>> violations = validator.validate(itemDto, Create.class);
        assertEquals(5, violations.size());
    }
}