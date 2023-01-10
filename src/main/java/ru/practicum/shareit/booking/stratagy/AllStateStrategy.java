package ru.practicum.shareit.booking.stratagy;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;

@Component
@AllArgsConstructor
public class AllStateStrategy implements BookingStateFetchStrategy {

    private BookingRepository bookingRepository;

    @Override
    public List<Booking> findBookingsByBooker(long userId) {
        return bookingRepository.findByBookerIdOrderByEndDateDesc(userId);
    }

    @Override
    public List<Booking> findBookingsByOwner(long userId) {
        return bookingRepository.findByItemOwnerIdOrderByEndDateDesc(userId);
    }

    @Override
    public State getStrategyState() {
        return State.ALL;
    }
}