package ru.practicum.shareit.booking.stratagy;

import static java.time.LocalDateTime.now;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;

@Component
@AllArgsConstructor
public class PastStateStrategy implements BookingStateFetchStrategy {

    private BookingRepository bookingRepository;

    @Override
    public Page<Booking> findBookingsByBooker(long userId, Pageable pageable) {
        return bookingRepository.findAllByBookerIdAndEndDateBeforeOrderByStartDateDesc(userId, now(), pageable);
    }

    @Override
    public Page<Booking> findBookingsByOwner(long userId, Pageable pageable) {
        return bookingRepository.findByItemOwnerIdAndEndDateBeforeOrderByStartDateDesc(userId, now(), pageable);
    }

    @Override
    public State getStrategyState() {
        return State.PAST;
    }
}
