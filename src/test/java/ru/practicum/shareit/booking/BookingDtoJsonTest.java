package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.groups.Inpute;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.user.UserDto;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
public class BookingDtoJsonTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    public void testSerializeBookingDto() throws Exception {
        BookingDto bookingDto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(1))
                .itemId(1L)
                .item(ItemDto.builder().build())
                .booker(UserDto.builder().build())
                .status(BookingStatus.APPROVED)
                .bookerId(1L)
                .build();

        String expectedJson = json.write(bookingDto).getJson();
        assertEquals(expectedJson, json.write(bookingDto).getJson());
    }

    @Test
    public void testDeserializeBookingDto() throws Exception {
        String jsonStr = "{\"id\": 1, \"start\": \"2022-01-01T12:00:00\", \"end\": \"2022-01-01T13:00:00\", \"itemId\": 1, \"itemDto\": {}, \"bookerDto\": {}, \"status\": \"APPROVED\", \"bookerId\": 1}";
        BookingDto expectedBookingDto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.parse("2022-01-01T12:00:00"))
                .end(LocalDateTime.parse("2022-01-01T13:00:00"))
                .itemId(1L)
                .item(ItemDto.builder().build())
                .booker(UserDto.builder().build())
                .status(BookingStatus.APPROVED)
                .bookerId(1L)
                .build();
        BookingDto bookingDto = json.parse(jsonStr).getObject();
        assertEquals(expectedBookingDto.getId(), bookingDto.getId());
        assertEquals(expectedBookingDto.getStart(), bookingDto.getStart());
        assertEquals(expectedBookingDto.getEnd(), bookingDto.getEnd());
    }

    @Test
    public void testValidation() {
        BookingDto bookingDto = BookingDto.builder().build();
        Set<ConstraintViolation<BookingDto>> violations = validator.validate(bookingDto, Inpute.class);
        assertEquals(3, violations.size());
    }
}