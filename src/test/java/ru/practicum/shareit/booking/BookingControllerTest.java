package ru.practicum.shareit.booking;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingPostDto;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;

@WebMvcTest({BookingController.class, ItemMapper.class, UserMapper.class})
@AutoConfigureMockMvc
class BookingControllerTest {

    private static final String X_SHARER_USER_ID_HEADER = "X-Sharer-User-Id";

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ItemMapper itemMapper;
    @Autowired
    UserMapper userMapper;

    @MockBean
    BookingService bookingService;

    BookingDto bookingDtoNew;
    Item item;
    User booker;
    User owner;

    @BeforeEach
    void setUp() {
        owner = User.builder()
                .id(1L)
                .name("Andrei")
                .email("andrei@yandex.ru")
                .build();

        item = Item.builder()
                .id(1L)
                .name("Item")
                .description("Test description")
                .ownerId(owner.getId())
                .available(true)
                .build();

        booker = User.builder()
                .id(2L)
                .name("Alex")
                .email("alex@yandex.ru")
                .build();

        bookingDtoNew = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(10))
                .item(itemMapper.toItemDto(item))
                .booker(userMapper.toUserDto(booker))
                .status(Status.WAITING)
                .build();

    }


    @Test
    void addBooking() throws Exception {
        BookingPostDto bookingPostDto = BookingPostDto.builder()
                .start(bookingDtoNew.getStart())
                .itemId(item.getId())
                .end(bookingDtoNew.getEnd())
                .build();

        when(bookingService.add(anyLong(), any()))
                .thenReturn(bookingDtoNew);

        String result = mockMvc.perform(post("/bookings")
                        .header(X_SHARER_USER_ID_HEADER, booker.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingPostDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingDtoNew), result);
    }

    @Test
    void approveBooking() throws Exception {
        bookingDtoNew.setStatus(Status.APPROVED);

        when(bookingService.approveOrReject(anyLong(), anyBoolean(), anyLong()))
                .thenReturn(bookingDtoNew);

        String result = mockMvc.perform(patch("/bookings/{bookingId}", bookingDtoNew.getId())
                        .param("approved", "true")
                        .header(X_SHARER_USER_ID_HEADER, owner.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingDtoNew), result);
    }

    @Test
    void getBookingById() throws Exception {
        when(bookingService.getById(anyLong(), anyLong()))
                .thenReturn(bookingDtoNew);

        mockMvc.perform(get("/bookings/{bookingId}", bookingDtoNew.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(X_SHARER_USER_ID_HEADER, booker.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookingDtoNew)));
    }


    @Test
    void getAllBookingsByBookerId() throws Exception {
        when(bookingService.findAllByBookerId(any(), anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDtoNew));

        mockMvc.perform(get("/bookings")
                        .header(X_SHARER_USER_ID_HEADER, booker.getId())
                        .param("state", State.ALL.name())
                        .param("from", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(bookingDtoNew))));
    }


    @Test
    void getAllBookingsByBookerId_whenInvalidState_thenThrowBadRequestException() throws Exception {
        var invalidState = "INVALID_STATE";

        when(bookingService.findAllByBookerId(any(), anyLong(), anyInt(), anyInt()))
                .thenThrow(BadRequestException.class);

        mockMvc.perform(get("/bookings")
                        .header(X_SHARER_USER_ID_HEADER, booker.getId())
                        .param("state", invalidState)
                        .param("from", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllBookingsByBookerId_whenNegativeFrom_thenThrowServerErrorException() throws Exception {
        var negativeFrom = "-1";

        mockMvc.perform(get("/bookings")
                        .header(X_SHARER_USER_ID_HEADER, booker.getId())
                        .param("state", State.ALL.name())
                        .param("from", negativeFrom)
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getAllBookingsByBookerId_whenNegativeSize_thenThrowServerErrorException() throws Exception {
        var negativeSize = "-1";

        mockMvc.perform(get("/bookings")
                        .header(X_SHARER_USER_ID_HEADER, booker.getId())
                        .param("state", State.ALL.name())
                        .param("from", "1")
                        .param("size", negativeSize)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getAllBookingsByOwnerId() throws Exception {
        when(bookingService.findAllByOwnerId(any(), anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDtoNew));

        mockMvc.perform(get("/bookings/owner")
                        .header(X_SHARER_USER_ID_HEADER, booker.getId())
                        .param("state", State.ALL.name())
                        .param("from", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(bookingDtoNew))));
    }

    @Test
    void getAllBookingsByOwnerId_whenNegativePositive_thenThrowServerErrorException() throws Exception {
        var negativeFrom = "-1";

        mockMvc.perform(get("/bookings/owner")
                        .header(X_SHARER_USER_ID_HEADER, booker.getId())
                        .param("state", State.ALL.name())
                        .param("from", negativeFrom)
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getAllBookingsByOwnerId_whenNegativeSize_thenThrowServerErrorException() throws Exception {
        var negativeSize = "-1";

        mockMvc.perform(get("/bookings/owner")
                        .header(X_SHARER_USER_ID_HEADER, booker.getId())
                        .param("state", State.ALL.name())
                        .param("from", "0")
                        .param("size", negativeSize)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }
}
