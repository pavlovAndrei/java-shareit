package ru.practicum.shareit.item;

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

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemGetDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;

@WebMvcTest({ItemController.class, ItemMapper.class})
@AutoConfigureMockMvc
class ItemControllerTest {

    private static final String X_SHARER_USER_ID_HEADER = "X-Sharer-User-Id";

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ItemMapper itemMapper;

    @MockBean
    ItemService itemService;

    CommentDto commentDto;
    Item item;
    ItemDto itemDto;
    ItemGetDto itemGetDto;
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
                .name("Test item")
                .description("Test description")
                .available(true)
                .ownerId(owner.getId())
                .requestId(1L)
                .build();

        commentDto = CommentDto.builder()
                .id(1L)
                .text("Comment")
                .itemId(item.getId())
                .authorName(owner.getName())
                .created(LocalDateTime.now())
                .build();

        itemDto = itemMapper.toItemDto(item);

        itemGetDto = itemMapper.toItemGetDto(item);
    }

    @Test
    void addItem() throws Exception {
        when(itemService.add(any(), anyLong()))
                .thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header(X_SHARER_USER_ID_HEADER, owner.getId())
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()))
                .andExpect(jsonPath("$.ownerId").value(itemDto.getOwnerId()))
                .andExpect(jsonPath("$.requestId").value(itemDto.getRequestId()));
    }

    @Test
    void getAllItemsByUserId() throws Exception {
        when(itemService.findAllByUserId(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemGetDto));

        mockMvc.perform(get("/items")
                        .header(X_SHARER_USER_ID_HEADER, owner.getId())
                        .param("from", "1")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(itemGetDto))));
    }

    @Test
    void getItemById() throws Exception {
        when(itemService.getById(anyLong(), anyLong()))
                .thenReturn(itemGetDto);

        mockMvc.perform(get("/items/{itemId}", item.getId())
                        .header(X_SHARER_USER_ID_HEADER, owner.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()));
    }

    @Test
    void searchItem() throws Exception {
        when(itemService.search(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items/search")
                        .header(X_SHARER_USER_ID_HEADER, owner.getId())
                        .param("text", itemDto.getName())
                        .param("from", "1")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(itemDto))));
    }

    @Test
    void updateItem() throws Exception {
        var itemForUpdate = itemDto;
        itemForUpdate.setName("Updated name");

        when(itemService.update(any(), anyLong(), anyLong()))
                .thenReturn(itemDto);

        mockMvc.perform(patch("/items/{itemId}", item.getId())
                        .header(X_SHARER_USER_ID_HEADER, owner.getId())
                        .content(objectMapper.writeValueAsString(item))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemForUpdate.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()));
    }

    @Test
    void updateItem_whenUserIsNotOwner_thenThrowNotFoundException() throws Exception {
        var incorrectOwnerId = "222";

        when(itemService.update(any(), anyLong(), anyLong()))
                .thenThrow(NotFoundException.class);

        mockMvc.perform(patch("/items/{itemId}", item.getId())
                        .header(X_SHARER_USER_ID_HEADER, incorrectOwnerId)
                        .content(objectMapper.writeValueAsString(item))
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isNotFound());
    }

    @Test
    void createComment() throws Exception {
        when(itemService.addComment(anyLong(), anyLong(), any()))
                .thenReturn(commentDto);

        mockMvc.perform(post("/items/{itemId}/comment", item.getId())
                        .header(X_SHARER_USER_ID_HEADER, owner.getId())
                        .content(objectMapper.writeValueAsString(commentDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(commentDto)));
    }
}
