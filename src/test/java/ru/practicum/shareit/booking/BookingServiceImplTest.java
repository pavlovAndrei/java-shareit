package ru.practicum.shareit.booking;

import java.util.List;
import java.util.Optional;

import static java.lang.String.format;
import static java.util.Collections.emptyList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.data.domain.Sort.Direction.DESC;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingPostDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.stratagy.AllStateStrategy;
import ru.practicum.shareit.booking.stratagy.BookingStateFetchStrategyFactory;
import ru.practicum.shareit.booking.stratagy.WaitingStateStrategy;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;

import static ru.practicum.shareit.booking.model.Status.WAITING;
import static ru.practicum.shareit.common.CustomPageRequest.of;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {

    @InjectMocks
    private BookingServiceImpl bookingService;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    ItemRepository itemRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    BookingMapper bookingMapper;
    @Mock
    BookingStateFetchStrategyFactory strategyFactory;

    private final Integer from = 0;
    private final Integer size = 10;

    @Test
    void addBooking() {
        long userId = 1L;
        long itemId = 1L;
        long ownerId = 2L;

        User user = User.builder().build();
        Booking booking = Booking.builder().build();
        BookingDto expectedBookingDto = BookingDto.builder().build();

        BookingPostDto bookingPostDto = BookingPostDto.builder()
                .itemId(itemId)
                .build();
        Item item = Item.builder()
                .ownerId(ownerId)
                .available(true)
                .build();

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(bookingMapper.toBooking(bookingPostDto, item, user))
                .thenReturn(booking);
        when(bookingMapper.toBookingDto(booking))
                .thenReturn(expectedBookingDto);

        var actualBookingDto = bookingService.add(userId, bookingPostDto);

        assertThat(actualBookingDto).isEqualTo(expectedBookingDto);
        verify(bookingRepository).save(booking);
    }

    @Test
    void addBooking_whenItemNotFound_throwNotFoundException() {
        long userId = 1L;
        long itemId = 1L;
        BookingPostDto bookingPostDto = BookingPostDto.builder()
                .itemId(itemId)
                .build();

        var exception = assertThrows(NotFoundException.class, () ->
                bookingService.add(userId, bookingPostDto));

        assertThat(exception.getMessage())
                .isEqualTo(format("User with id: '%d' does not exist", bookingPostDto.getItemId()));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void addBooking_whenItemNotAvailable_throwBadRequestException() {
        long userId = 1L;
        long itemId = 1L;
        long ownerId = 2L;

        BookingPostDto bookingPostDto = BookingPostDto.builder()
                .itemId(itemId)
                .build();
        Item item = Item.builder()
                .ownerId(ownerId)
                .available(false)
                .build();

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(User.builder().build()));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        var exception = assertThrows(BadRequestException.class, () ->
                bookingService.add(userId, bookingPostDto));

        assertThat(exception.getMessage())
                .isEqualTo(format("Item with id: '%d' is already booked", item.getId()));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void addBooking_whenRequestNotByOwner_throwNotFoundException() {
        long userId = 1L;
        long itemId = 1L;
        long ownerId = 1L;

        BookingPostDto bookingPostDto = BookingPostDto.builder()
                .itemId(itemId)
                .build();
        Item item = Item.builder()
                .ownerId(ownerId)
                .available(true)
                .build();

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(User.builder().build()));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        var exception = assertThrows(NotFoundException.class, () ->
                bookingService.add(userId, bookingPostDto));

        assertThat(exception.getMessage())
                .isEqualTo("Item cannot be booked by the owner");
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void approveBooking_whenParamIsTrue() {
        long bookingId = 1L;
        long userId = 1L;
        long ownerId = 1L;

        Item item = Item.builder()
                .ownerId(ownerId)
                .build();
        Booking booking = Booking.builder()
                .status(WAITING)
                .item(item)
                .build();
        ItemDto itemDto = ItemDto.builder()
                .ownerId(ownerId)
                .build();
        BookingDto expectedBookingDto = BookingDto.builder()
                .status(Status.APPROVED)
                .item(itemDto)
                .build();

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        when(bookingMapper.toBookingDto(any()))
                .thenReturn(expectedBookingDto);

        var actualBookingDto = bookingService.approveOrReject(bookingId, true, userId);

        assertThat(actualBookingDto)
                .isEqualTo(expectedBookingDto);
    }

    @Test
    void rejectBooking_whenParamIsFalse() {
        long bookingId = 1L;
        long userId = 1L;
        long ownerId = 1L;

        Item item = Item.builder()
                .ownerId(ownerId)
                .build();
        Booking booking = Booking.builder()
                .status(WAITING)
                .item(item)
                .build();
        ItemDto itemDto = ItemDto.builder()
                .ownerId(ownerId)
                .build();
        BookingDto expectedBookingDto = BookingDto.builder()
                .status(Status.REJECTED)
                .item(itemDto)
                .build();

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        when(bookingMapper.toBookingDto(any()))
                .thenReturn(expectedBookingDto);

        var actualBookingDto = bookingService.approveOrReject(bookingId, false, userId);

        assertThat(actualBookingDto)
                .isEqualTo(expectedBookingDto);
    }

    @Test
    void approveBooking_whenUserNotOwner_throwNotFoundException() {
        long bookingId = 1L;
        long userId = 2L;
        long ownerId = 1L;

        Item item = Item.builder()
                .ownerId(ownerId)
                .build();
        Booking booking = Booking.builder()
                .status(WAITING)
                .item(item)
                .build();

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        var exception = assertThrows(NotFoundException.class, () ->
                bookingService.approveOrReject(bookingId, true, userId));

        assertThat(exception.getMessage())
                .isEqualTo(format("User with id: '%d' is not owner of the item", userId));
    }

    @Test
    void approveBooking_whenBookingApproved_throwBadRequestException() {
        long bookingId = 1L;
        long userId = 1L;
        long ownerId = 1L;

        Item item = Item.builder()
                .ownerId(ownerId)
                .build();
        Booking booking = Booking.builder()
                .status(Status.APPROVED)
                .item(item)
                .build();

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        var exception = assertThrows(BadRequestException.class, () ->
                bookingService.approveOrReject(bookingId, true, userId));

        assertThat(exception.getMessage())
                .isEqualTo("This booking is already approved");
    }

    @Test
    void getBookingById() {
        long bookingId = 1L;
        long bookerId = 2L;
        long ownerId = 1L;
        long userId = 1L;

        Item item = Item.builder()
                .ownerId(ownerId)
                .build();
        User user = User.builder()
                .id(bookerId)
                .build();
        Booking booking = Booking.builder()
                .item(item)
                .booker(user)
                .build();
        ItemDto itemDto = ItemDto.builder()
                .ownerId(ownerId)
                .build();
        UserDto userDto = UserDto.builder()
                .id(bookerId)
                .build();
        BookingDto expectedBookingDto = BookingDto.builder()
                .item(itemDto)
                .booker(userDto)
                .build();

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        when(bookingMapper.toBookingDto(booking))
                .thenReturn(expectedBookingDto);

        var actualBookingDto = bookingService.getById(bookingId, userId);

        assertThat(actualBookingDto)
                .isEqualTo(expectedBookingDto);
    }

    @Test
    void getBookingById_whenUserNotOwnerOrBooker_throwNotFoundException() {
        long bookingId = 1L;
        long bookerId = 2L;
        long ownerId = 1L;
        long userId = 3L;

        Item item = Item.builder()
                .ownerId(ownerId)
                .build();
        User user = User.builder()
                .id(bookerId)
                .build();
        Booking booking = Booking.builder()
                .item(item)
                .booker(user)
                .build();

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        var exception = assertThrows(NotFoundException.class, () ->
                bookingService.getById(bookingId, userId));

        assertThat(exception.getMessage())
                .isEqualTo("User is not owner or booker");
    }

    @Test
    void getAllByBookerId_whenStateIsAll() {
        String state = State.ALL.name();
        long userId = 1L;

        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(bookingRepository.findByBookerId(userId, of(0, 10, DESC, "endDate")))
                .thenReturn(Page.empty());
        when(strategyFactory.findStrategy(any()))
                .thenReturn(new AllStateStrategy(bookingRepository));

        List<BookingDto> expectedBookingDtoList = emptyList();
        List<BookingDto> actualBookingDtoList = bookingService.findAllByBookerId(state, userId, from, size);

        assertThat(actualBookingDtoList)
                .isEqualTo(expectedBookingDtoList);
    }

    @Test
    void getAllByOwnerId_whenStateIsFuture() {

        String state = State.FUTURE.name();
        long userId = 1L;

        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(bookingRepository.findByItemOwnerIdAndStatusIs(userId,
                WAITING, of(0, 10, DESC, "startDate")))
                .thenReturn(Page.empty());
        when(strategyFactory.findStrategy(any()))
                .thenReturn(new WaitingStateStrategy(bookingRepository));

        List<BookingDto> expectedBookingDtoList = emptyList();
        List<BookingDto> actualBookingDtoList = bookingService.findAllByOwnerId(state, userId, from, size);

        assertThat(actualBookingDtoList)
                .isEqualTo(expectedBookingDtoList);
    }
}
