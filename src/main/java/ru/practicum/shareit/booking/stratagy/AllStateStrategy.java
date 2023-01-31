package ru.practicum.shareit.booking.stratagy;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public Page<Booking> findBookingsByBooker(long userId, Pageable pageable) {
        return bookingRepository.findByBookerIdOrderByEndDateDesc(userId, pageable);
    }

    @Override
    public Page<Booking> findBookingsByOwner(long userId, Pageable pageable) {
        return bookingRepository.findByItemOwnerIdOrderByEndDateDesc(userId, pageable);
    }

    @Override
    public State getStrategyState() {
        return State.ALL;
    }
}
