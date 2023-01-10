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
public class PastStateStrategy implements BookingStateFetchStrategy {

    private BookingRepository bookingRepository;

    @Override
    public List<Booking> execute(long userId) {
        return bookingRepository.findAllByBookerIdAndEndDateBeforeOrderByStartDateDesc(userId, now());
    }

    @Override
    public State getStrategyState() {
        return State.PAST;
    }
}
