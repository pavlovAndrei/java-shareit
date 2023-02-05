package ru.practicum.shareit.booking.stratagy;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import static org.springframework.data.domain.Sort.Direction.DESC;

import lombok.AllArgsConstructor;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;

import static ru.practicum.shareit.booking.model.Status.WAITING;
import static ru.practicum.shareit.common.CustomPageRequest.of;

@Component
@AllArgsConstructor
public class WaitingStateStrategy implements BookingStateFetchStrategy {

    private BookingRepository bookingRepository;

    @Override
    public Page<Booking> findBookingsByBooker(long userId, Integer offset, Integer size) {
        return bookingRepository.findAllByBookerIdAndStatusIs(userId,
                WAITING, getStrategyPageable(offset, size));
    }

    @Override
    public Page<Booking> findBookingsByOwner(long userId, Integer offset, Integer size) {
        return bookingRepository.findByItemOwnerIdAndStatusIs(userId,
                WAITING, getStrategyPageable(offset, size));
    }

    @Override
    public State getStrategyState() {
        return State.WAITING;
    }

    @Override
    public Pageable getStrategyPageable(Integer offset, Integer size) {
        return of(offset, size, DESC, "startDate");
    }
}
