package ru.practicum.shareit.booking.stratagy;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;

import static ru.practicum.shareit.booking.model.Status.REJECTED;

@Component
@AllArgsConstructor
public class RejectedStateStrategy implements BookingStateFetchStrategy {

    private BookingRepository bookingRepository;

    @Override
    public Page<Booking> findBookingsByBooker(long userId, Pageable pageable) {
        return bookingRepository.findAllByBookerIdAndStatusIsOrderByStartDateDesc(userId, REJECTED, pageable);
    }

    @Override
    public Page<Booking> findBookingsByOwner(long userId, Pageable pageable) {
        return bookingRepository.findByItemOwnerIdAndStatusIsOrderByStartDateDesc(userId, REJECTED, pageable);
    }

    @Override
    public State getStrategyState() {
        return State.REJECTED;
    }
}
