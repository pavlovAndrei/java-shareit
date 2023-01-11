package ru.practicum.shareit.booking.stratagy;

import java.util.List;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;

public interface BookingStateFetchStrategy {

    List<Booking> findBookingsByBooker(long userId);

    List<Booking> findBookingsByOwner(long userId);

    State getStrategyState();
}
