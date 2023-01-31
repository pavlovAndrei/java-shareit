package ru.practicum.shareit.booking.service;

import java.util.List;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

import javax.validation.Valid;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingPostDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.stratagy.BookingStateFetchStrategy;
import ru.practicum.shareit.booking.stratagy.BookingStateFetchStrategyFactory;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static ru.practicum.shareit.booking.model.Status.APPROVED;
import static ru.practicum.shareit.booking.model.Status.REJECTED;
import static ru.practicum.shareit.booking.model.Status.WAITING;

@Validated
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;

    private final BookingStateFetchStrategyFactory strategyFactory;

    @Override
    @Transactional
    public @Valid BookingDto add(long userId, BookingPostDto bookingPostDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new NotFoundException(format("User with id: '%d' does not exist", userId)));

        Item item = itemRepository.findById(bookingPostDto.getItemId())
                .orElseThrow(() ->
                        new NotFoundException(format("Item with id: '%d' does not exist", bookingPostDto.getItemId())));

        if (!item.getAvailable()) {
            throw new BadRequestException(format("Item with id: '%d' is already booked", item.getId()));
        }

        if (item.getOwnerId().equals(userId)) {
            throw new NotFoundException("Item cannot be booked by the owner");
        }

        Booking booking = bookingMapper.toBooking(bookingPostDto, item, user);
        booking.setStatus(WAITING);

        bookingRepository.save(booking);

        return bookingMapper.toBookingDto(booking);
    }

    @Override
    @Transactional
    public @Valid BookingDto approveOrReject(long bookingId, boolean approved, long userId) {
        Booking booking = getBookingIfExist(bookingId);

        if (!booking.getItem().getOwnerId().equals(userId)) {
            throw new NotFoundException(format("User with id: '%d' is not owner of the item", userId));
        }

        if (booking.getStatus().equals(APPROVED) && approved) {
            throw new BadRequestException("This booking is already approved");
        }

        if (approved) {
            booking.setStatus(APPROVED);
        } else {
            booking.setStatus(REJECTED);
        }

        return bookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public @Valid BookingDto getById(long bookingId, long userId) {
        Booking booking = getBookingIfExist(bookingId);

        if (booking.getBooker().getId() != userId && booking.getItem().getOwnerId() != userId) {
            throw new NotFoundException("User is not owner or booker");
        }

        return bookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> findAllByBookerId(String state, long userId,
                                              Integer offset, Integer size) {
        State providedState = getStateOrThrow(state);
        checkUserExists(userId);

        Pageable pageable = PageRequest.of(offset / size, size);

        BookingStateFetchStrategy strategy = strategyFactory.findStrategy(providedState);
        var bookings = strategy.findBookingsByBooker(userId, pageable);

        return bookings.stream()
                .map(bookingMapper::toBookingDto)
                .collect(toList());
    }

    @Override
    public List<BookingDto> findAllByOwnerId(String state, long userId,
                                             Integer offset, Integer size) {
        State providedState = getStateOrThrow(state);
        checkUserExists(userId);

        Pageable pageable = PageRequest.of(offset / size, size);

        BookingStateFetchStrategy strategy = strategyFactory.findStrategy(providedState);
        var bookings = strategy.findBookingsByOwner(userId, pageable);

        return bookings.stream()
                .map(bookingMapper::toBookingDto)
                .collect(toList());
    }

    private Booking getBookingIfExist(long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new NotFoundException(format("Booking with id: %d does not exist", bookingId)));
    }

    private void checkUserExists(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(format("User with id: '%d' does not exist", userId));
        }
    }

    private State getStateOrThrow(String state) {
        try {
            return State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(format("Unknown state: %s", state));
        }
    }
}
