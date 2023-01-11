package ru.practicum.shareit.booking.stratagy;

import java.util.List;

import static java.time.LocalDateTime.now;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;

@Component
@AllArgsConstructor
public class FutureStateStrategy implements BookingStateFetchStrategy {

    private BookingRepository bookingRepository;

    @Override
    public List<Booking> findBookingsByBooker(long userId) {
        return bookingRepository.findByBookerIdAndStartDateAfterOrderByEndDateDesc(userId, now());
    }

    @Override
    public List<Booking> findBookingsByOwner(long userId) {
        return bookingRepository.findByItemOwnerIdAndStartDateAfterOrderByEndDateDesc(userId, now());
    }

    @Override
    public State getStrategyState() {
        return State.FUTURE;
    }
}
