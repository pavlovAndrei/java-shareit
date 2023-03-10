package ru.practicum.shareit.booking.stratagy;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;

public interface BookingStateFetchStrategy {

    Page<Booking> findBookingsByBooker(long userId, Integer offset, Integer size);

    Page<Booking> findBookingsByOwner(long userId, Integer offset, Integer size);

    State getStrategyState();

    Pageable getStrategyPageable(Integer offset, Integer size);
}
