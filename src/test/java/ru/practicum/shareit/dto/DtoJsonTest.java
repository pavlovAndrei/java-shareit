package ru.practicum.shareit.dto;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import lombok.SneakyThrows;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserDto;

@JsonTest
public class DtoJsonTest {

    @Autowired
    private JacksonTester<BookingDto> jsonBookingDto;
    @Autowired
    private JacksonTester<ItemDto> jsonItemDto;
    @Autowired
    private JacksonTester<ItemRequestDto> jsonItemRequestDto;
    @Autowired
    private JacksonTester<UserDto> jsonUserDto;

    private final LocalDateTime startDate =
            LocalDateTime.of(1999, 9, 9, 9, 9, 9);
    private final LocalDateTime endDate = startDate.plusDays(9);

    private final ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("Test item")
            .ownerId(1L)
            .description("Test description")
            .available(true)
            .requestId(1L)
            .build();
    private final ItemRequestDto itemRequestDto = ItemRequestDto.builder()
            .id(1L)
            .description("Test description")
            .requestorId(1L)
            .created(startDate)
            .items(List.of(itemDto))
            .build();
    private final UserDto userDto = UserDto.builder()
            .id(1L)
            .name("Test username")
            .email("testuser@yandex.ru")
            .build();
    private final BookingDto bookingDto = BookingDto.builder()
            .id(1L)
            .start(startDate)
            .end(endDate)
            .item(itemDto)
            .booker(userDto)
            .status(Status.APPROVED)
            .build();

    @SneakyThrows
    @Test
    void testItemDto() {
        JsonContent<ItemDto> result = jsonItemDto.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.ownerId")
                .isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.requestId")
                .isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name")
                .isEqualTo("Test item");
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("Test description");
        assertThat(result).extractingJsonPathBooleanValue("$.available")
                .isEqualTo(true);
    }

    @SneakyThrows
    @Test
    void testUserDto() {
        JsonContent<UserDto> result = jsonUserDto.write(userDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Test username");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("testuser@yandex.ru");
    }

    @SneakyThrows
    @Test
    void testBookingDto() {
        JsonContent<BookingDto> result = jsonBookingDto.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.status")
                .isEqualTo(Status.APPROVED.toString());
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo("1999-09-09T09:09:09");
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo("1999-09-18T09:09:09");

        assertThat(result).extractingJsonPathNumberValue("$.item.id")
                .isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.item.name")
                .isEqualTo("Test item");
        assertThat(result).extractingJsonPathStringValue("$.item.description")
                .isEqualTo("Test description");
        assertThat(result).extractingJsonPathNumberValue("$.item.ownerId")
                .isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.item.requestId")
                .isEqualTo(1);
        assertThat(result).extractingJsonPathBooleanValue("$.item.available")
                .isEqualTo(true);

        assertThat(result).extractingJsonPathNumberValue("$.booker.id")
                .isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.booker.name")
                .isEqualTo("Test username");
        assertThat(result).extractingJsonPathStringValue("$.booker.email")
                .isEqualTo("testuser@yandex.ru");
    }

    @SneakyThrows
    @Test
    void testItemRequestDto() {
        JsonContent<ItemRequestDto> result = jsonItemRequestDto.write(itemRequestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("Test description");
        assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo(startDate.toString());
        assertThat(result).extractingJsonPathNumberValue("$.requestorId")
                .isEqualTo(1);

        assertThat(result).extractingJsonPathNumberValue("$.items[0].id")
                .isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.items[0].name")
                .isEqualTo("Test item");
        assertThat(result).extractingJsonPathStringValue("$.items[0].description")
                .isEqualTo("Test description");
        assertThat(result).extractingJsonPathBooleanValue("$.items[0].available")
                .isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.items[0].ownerId")
                .isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.items[0].requestId")
                .isEqualTo(1);
    }
}
