package ru.practicum.shareit.booking.stratagy;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;

import static ru.practicum.shareit.booking.model.Status.WAITING;

@Component
@AllArgsConstructor
public class WaitingStateStrategy implements BookingStateFetchStrategy {

    private BookingRepository bookingRepository;

    @Override
    public Page<Booking> findBookingsByBooker(long userId, Pageable pageable) {
        return bookingRepository.findAllByBookerIdAndStatusIsOrderByStartDateDesc(userId, WAITING, pageable);
    }

    @Override
    public Page<Booking> findBookingsByOwner(long userId, Pageable pageable) {
        return bookingRepository.findByItemOwnerIdAndStatusIsOrderByStartDateDesc(userId, WAITING, pageable);
    }

    @Override
    public State getStrategyState() {
        return State.WAITING;
    }
}
