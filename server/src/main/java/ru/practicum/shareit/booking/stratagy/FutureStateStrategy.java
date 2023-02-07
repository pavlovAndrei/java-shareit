package ru.practicum.shareit.booking.stratagy;

import static java.time.LocalDateTime.now;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import static org.springframework.data.domain.Sort.Direction.DESC;

import lombok.AllArgsConstructor;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.common.CustomPageRequest;

@Component
@AllArgsConstructor
public class FutureStateStrategy implements BookingStateFetchStrategy {

    private BookingRepository bookingRepository;

    @Override
    public Page<Booking> findBookingsByBooker(long userId, Integer offset, Integer size) {
        return bookingRepository.findByBookerIdAndStartDateAfter(userId, now(),
                getStrategyPageable(offset, size));
    }

    @Override
    public Page<Booking> findBookingsByOwner(long userId, Integer offset, Integer size) {
        return bookingRepository.findByItemOwnerIdAndStartDateAfter(userId, now(),
                getStrategyPageable(offset, size));
    }

    @Override
    public State getStrategyState() {
        return State.FUTURE;
    }

    @Override
    public Pageable getStrategyPageable(Integer offset, Integer size) {
        return CustomPageRequest.of(offset, size, DESC, "endDate");
    }
}
