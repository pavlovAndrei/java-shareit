package ru.practicum.shareit.item;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.data.domain.Pageable.unpaged;

import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

@AutoConfigureTestDatabase
@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRepositoryTest {

    private final ItemRepository itemRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    private Item itemOne;
    private Item itemTwo;
    private Item itemUpperCase;
    private Item itemLowerCase;

    private ItemRequest itemRequest;

    private User userOne;

    @BeforeEach
    void setUp() {
        userOne = userRepository.save(User.builder()
                .name("Test user one")
                .email("userOne@yandex.ru")
                .build());

        User userTwo = userRepository.save(User.builder()
                .name("Test user two")
                .email("userTwo@yandex.ru")
                .build());

        itemRequest = itemRequestRepository.save(ItemRequest.builder()
                .description("Test item request")
                .requestorId(userOne.getId())
                .build());

        itemOne = itemRepository.save(Item.builder()
                .name("Test item one")
                .description("Test description one")
                .available(true)
                .ownerId(userOne.getId())
                .build());

        itemTwo = itemRepository.save(Item.builder()
                .name("Test item two")
                .description("Test description two")
                .available(true)
                .ownerId(userOne.getId())
                .build());

        itemUpperCase = itemRepository.save(Item.builder()
                .name("SEARCH")
                .description("SEARCH ITEM")
                .available(true)
                .ownerId(userTwo.getId())
                .build());

        itemLowerCase = itemRepository.save(Item.builder()
                .name("search")
                .description("search item")
                .available(false)
                .ownerId(userTwo.getId())
                .requestId(itemRequest.getId())
                .build());
    }

    @Test
    void findByOwnerIdOrderByIdAsc() {
        var actualList = itemRepository.findByOwnerIdOrderByIdAsc(userOne.getId(), unpaged()).getContent();

        var expectedList = List.of(itemOne, itemTwo);
        assertThat(actualList).isEqualTo(expectedList);
    }

    @Test
    void findByOwnerIdOrderByIdAsc_wrongOwner() {
        var wrongUserId = 999L;

        var actualList = itemRepository.findByOwnerIdOrderByIdAsc(wrongUserId, unpaged()).getContent();

        assertThat(actualList).isEmpty();
    }

    @Test
    void searchByText() {
        var actualList = itemRepository.search("search", unpaged()).getContent();

        assertThat(actualList).isEqualTo(List.of(itemUpperCase));
    }

    @Test
    void findAllByRequestIdIn() {
        var actualList = itemRepository.findAllByRequestIdIn(List.of(itemRequest.getId()));

        assertThat(actualList).isEqualTo(List.of(itemLowerCase));
    }

    @AfterEach
    void wipeData() {
        itemRepository.deleteAll();
        itemRequestRepository.deleteAll();
        userRepository.deleteAll();
    }
}
