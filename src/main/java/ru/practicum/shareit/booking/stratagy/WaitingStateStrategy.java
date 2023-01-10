package ru.practicum.shareit.booking.stratagy;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;

import static ru.practicum.shareit.booking.model.Status.REJECTED;
import static ru.practicum.shareit.booking.model.Status.WAITING;

@Component
@AllArgsConstructor
public class WaitingStateStrategy implements BookingStateFetchStrategy {

    private BookingRepository bookingRepository;

    @Override
    public List<Booking> execute(long userId) {
        return bookingRepository.findAllByBookerIdAndStatusIsOrderByStartDateDesc(userId, WAITING);
    }

    @Override
    public State getStrategyState() {
        return State.WAITING;
    }
}
