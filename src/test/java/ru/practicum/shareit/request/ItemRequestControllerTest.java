package ru.practicum.shareit.request;

import java.time.LocalDateTime;
import java.util.List;

import static java.lang.String.valueOf;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.SneakyThrows;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestPostDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.User;

@WebMvcTest({ItemRequestController.class, ItemRequestMapper.class})
@AutoConfigureMockMvc
class ItemRequestControllerTest {

    private static final String X_SHARER_USER_ID_HEADER = "X-Sharer-User-Id";

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ItemRequestMapper itemRequestMapper;

    @MockBean
    ItemRequestService itemRequestService;

    ItemDto itemDto;
    ItemRequestDto itemRequestDto;
    ItemRequestPostDto itemRequestPostDto;
    User owner;
    User user;

    @BeforeEach
    void setUp() {
        owner = User.builder()
                .id(1L)
                .name("Andrei")
                .email("andrei@yandex.ru")
                .build();

        user = User.builder()
                .id(2L)
                .name("Requestor")
                .email("andrei@yandex.ru")
                .build();

        itemRequestPostDto = ItemRequestPostDto.builder()
                .description("Request")
                .build();

        itemDto = ItemDto.builder().build();

        itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("Request")
                .requestorId(user.getId())
                .created(LocalDateTime.now())
                .items(List.of(itemDto))
                .build();
    }

    @SneakyThrows
    @Test
    void createRequest() {
        when(itemRequestService.add(anyLong(), any()))
                .thenReturn(itemRequestDto);

        mockMvc.perform(post("/requests")
                        .header(X_SHARER_USER_ID_HEADER, user.getId())
                        .content(objectMapper.writeValueAsString(itemRequestPostDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequestDto.getId()))
                .andExpect(jsonPath("$.description").value(itemRequestDto.getDescription()))
                .andExpect(jsonPath("$.requestorId").value(itemRequestDto.getRequestorId()))
                .andExpect(jsonPath("$.created").isNotEmpty());
    }

    @SneakyThrows
    @Test
    void createRequest_whenEmptyDescription_thenThrowBadRequestException() {
        itemRequestPostDto.setDescription(null);

        mockMvc.perform(post("/requests")
                        .header(X_SHARER_USER_ID_HEADER, user.getId())
                        .content(objectMapper.writeValueAsString(itemRequestPostDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemRequestService, never()).add(anyLong(), any());
    }

    @SneakyThrows
    @Test
    void getAllItemRequestsByOwnerId() {
        when(itemRequestService.findAllByOwnerId(anyLong()))
                .thenReturn(List.of(itemRequestDto));

        mockMvc.perform(get("/requests")
                        .header(X_SHARER_USER_ID_HEADER, owner.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(itemRequestDto))));

        verify(itemRequestService, times(1))
                .findAllByOwnerId(owner.getId());
    }

    @SneakyThrows
    @Test
    void getAllItemRequests() {
        when(itemRequestService.getAll(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemRequestDto));

        mockMvc.perform(get("/requests/all")
                        .header(X_SHARER_USER_ID_HEADER, user.getId())
                        .param("from", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(itemRequestDto))));

        verify(itemRequestService, times(1))
                .getAll(user.getId(), 0, 10);
    }

    @SneakyThrows
    @Test
    void getAllRequests_whenNegativeFrom_thenThrowServerErrorException() {
        var negativeFrom = -1;

        mockMvc.perform(get("/requests/all")
                        .header(X_SHARER_USER_ID_HEADER, user.getId())
                        .param("from", valueOf(negativeFrom))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(itemRequestService, never())
                .getAll(user.getId(), negativeFrom, 10);
    }

    @SneakyThrows
    @Test
    void getAllRequests_whenNegativeSize_thenThrowServerErrorException() {
        var negativeSize = -1;

        when(itemRequestService.getAll(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemRequestDto));

        mockMvc.perform(get("/requests/all")
                        .header(X_SHARER_USER_ID_HEADER, user.getId())
                        .param("size", valueOf(negativeSize))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(itemRequestService, never()).getAll(user.getId(), 0, negativeSize);
    }

    @SneakyThrows
    @Test
    void getRequestById() {
        when(itemRequestService.getById(anyLong(), anyLong()))
                .thenReturn(itemRequestDto);

        mockMvc.perform(get("/requests/{requestId}", user.getId())
                        .header(X_SHARER_USER_ID_HEADER, itemRequestDto.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequestDto.getId()))
                .andExpect(jsonPath("$.description").value(itemRequestDto.getDescription()))
                .andExpect(jsonPath("$.requestorId").value(itemRequestDto.getRequestorId()))
                .andExpect(jsonPath("$.created").isNotEmpty());
    }
}
