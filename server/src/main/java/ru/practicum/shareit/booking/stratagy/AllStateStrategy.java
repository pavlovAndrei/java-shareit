package ru.practicum.shareit.booking.stratagy;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import static org.springframework.data.domain.Sort.Direction.DESC;

import lombok.AllArgsConstructor;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.common.CustomPageRequest;

@Component
@AllArgsConstructor
public class AllStateStrategy implements BookingStateFetchStrategy {

    private BookingRepository bookingRepository;

    @Override
    public Page<Booking> findBookingsByBooker(long userId, Integer offset, Integer size) {
        return bookingRepository.findByBookerId(userId, getStrategyPageable(offset, size));
    }

    @Override
    public Page<Booking> findBookingsByOwner(long userId, Integer offset, Integer size) {
        return bookingRepository.findByItemOwnerId(userId, getStrategyPageable(offset, size));
    }

    @Override
    public State getStrategyState() {
        return State.ALL;
    }

    @Override
    public Pageable getStrategyPageable(Integer offset, Integer size) {
        return CustomPageRequest.of(offset, size, DESC, "endDate");
    }
}
