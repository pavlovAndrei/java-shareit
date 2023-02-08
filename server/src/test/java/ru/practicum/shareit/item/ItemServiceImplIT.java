package ru.practicum.shareit.item;

import java.util.Collections;
import java.util.List;

import static java.time.LocalDateTime.now;
import static java.time.temporal.ChronoUnit.MILLIS;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemGetDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceImplIT {

    private final ItemService itemService;

    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    private final BookingMapper bookingMapper;
    private final CommentMapper commentMapper;

    private User userOne;
    private User userTwo;
    private Item itemOne;
    private Item itemTwo;
    private Item itemThree;
    private ItemGetDto itemGetDtoOne;
    private ItemGetDto itemGetDtoTwo;

    @BeforeEach
    void setUp() {
        var localDateTime = now().truncatedTo(MILLIS);

        userOne = userRepository.save(User.builder()
                .name("Test user one")
                .email("userOne@yandex.ru")
                .build());

        userTwo = userRepository.save(User.builder()
                .name("Test user two")
                .email("userTwo@yandex.ru")
                .build());

        itemOne = itemRepository.save(Item.builder()
                .name("Test item one")
                .description("Test desc one")
                .available(true)
                .ownerId(userOne.getId())
                .build());

        itemTwo = itemRepository.save(Item.builder()
                .name("Test item two")
                .description("Test desc two")
                .available(true)
                .ownerId(userOne.getId())
                .build());

        itemThree = itemRepository.save(Item.builder()
                .name("Test item three")
                .description("Test desc three")
                .available(true)
                .ownerId(userTwo.getId())
                .build());

        var comment = commentRepository.save(Comment.builder()
                .text("search_text")
                .item(itemOne)
                .author(userTwo)
                .created(localDateTime)
                .build());

        var bookingOne = bookingRepository.save(Booking.builder()
                .startDate(localDateTime.minusDays(5))
                .endDate(localDateTime.minusDays(1))
                .item(itemTwo)
                .booker(userTwo)
                .status(Status.APPROVED)
                .build());

        var bookingTwo = bookingRepository.save(Booking.builder()
                .startDate(localDateTime.plusDays(2))
                .endDate(localDateTime.plusDays(5))
                .item(itemTwo)
                .booker(userTwo)
                .status(Status.APPROVED)
                .build());

        itemGetDtoOne = ItemGetDto.builder()
                .id(itemOne.getId())
                .name("Test item one")
                .description("Test desc one")
                .available(true)
                .comments(List.of(commentMapper.toCommentDto(comment)))
                .build();

        itemGetDtoTwo = ItemGetDto.builder()
                .id(itemTwo.getId())
                .name("Test item two")
                .description("Test desc two")
                .available(true)
                .lastBooking(bookingMapper.toBookingDtoForItem(bookingOne))
                .nextBooking(bookingMapper.toBookingDtoForItem(bookingTwo))
                .comments(Collections.emptyList())
                .build();
    }

    @Test
    void getAllUserItems_whenFromAndSizeAreMissing_thenReturnedAllItemsOfUser() {
        assertThat(itemService.findAllByUserId(userOne.getId(), 0, 10))
                .isEqualTo(List.of(itemGetDtoOne, itemGetDtoTwo));
    }

    @Test
    void getAllUserItems_whenFromAndSizeAreValid_thenReturnedContentOfPage() {
        assertThat(itemService.findAllByUserId(userOne.getId(), 0, 1))
                .isEqualTo(List.of(itemGetDtoOne));
    }

    @Test
    void getById_whenParamsAreValid_thenReturnedOwnerItemWithBookings() {
        assertThat(itemService.getById(itemTwo.getId(), userOne.getId()))
                .isEqualTo(itemGetDtoTwo);
    }

    @Test
    void getById_whenParamsAreValid_thenReturnedItemWithoutBookingsForNotOwner() {
        itemGetDtoTwo.setNextBooking(null);
        itemGetDtoTwo.setLastBooking(null);

        assertThat(itemService.getById(itemTwo.getId(), userTwo.getId()))
                .isEqualTo(itemGetDtoTwo);
    }

    @Test
    void getById_whenItemIsNotExist_thenReturnedNotFoundExceptionThrown() {
        var exception = assertThrows(NotFoundException.class, () ->
                itemService.getById(1000L, userOne.getId()));

        assertThat("Item with id '1000' does not exist")
                .isEqualTo(exception.getMessage());
    }

    @Test
    void addComment_whenAllParamsAreValid_thenSavedAndReturnedExceptedObjectFromDb() {
        long userId = userTwo.getId();
        long itemId = itemTwo.getId();
        var commentDto = CommentDto.builder().build();
        commentDto.setText("comment");

        var actualCommentDto = itemService.addComment(userId, itemId, commentDto);

        var savedCommentDto = commentMapper
                .toCommentDto(commentRepository.findById(actualCommentDto.getId())
                        .orElseThrow(() -> new NotFoundException("Comment hasn't found in DB")));

        actualCommentDto.setCreated(actualCommentDto.getCreated().truncatedTo(MILLIS));
        savedCommentDto.setCreated(savedCommentDto.getCreated().truncatedTo(MILLIS));

        assertThat(actualCommentDto)
                .isEqualTo(savedCommentDto);
    }

    @Test
    void addComment_whenUserHasNeverBeenBookedInThePast_thenReturnedBadRequestException() {
        long userId = userOne.getId();
        long itemId = itemThree.getId();
        var commentDto = CommentDto.builder().build();

        var exception = assertThrows(BadRequestException.class, () ->
                itemService.addComment(userId, itemId, commentDto));

        assertThat("It's prohibited to leave the comment before the expiration date")
                .isEqualTo(exception.getMessage());
    }

    @Test
    void addComment_whenItemHasNeverBeenBookedFromUser_thenReturnedBadRequestException() {
        long userId = userTwo.getId();
        long itemId = itemOne.getId();
        var commentDto = CommentDto.builder().build();

        BadRequestException exception = assertThrows(BadRequestException.class, () ->
                itemService.addComment(userId, itemId, commentDto));

        assertThat("It's prohibited to leave the comment before the expiration date")
                .isEqualTo(exception.getMessage());
    }

    @AfterEach
    void wipeData() {
        commentRepository.deleteAll();
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }
}
