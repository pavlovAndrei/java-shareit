package ru.practicum.shareit.booking;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Page<Booking> findByBookerId(long userId, Pageable pageable);

    Page<Booking> findAllByBookerIdAndEndDateBefore(long userId, LocalDateTime currentDateTime,
                                                    Pageable pageable);

    Page<Booking> findByBookerIdAndStartDateAfter(long userId, LocalDateTime currentDateTime,
                                                  Pageable pageable);

    Page<Booking> findAllByBookerIdAndStatusIs(long userId, Status status,
                                               Pageable pageable);

    Page<Booking> findByBookerIdAndStartDateLessThanEqualAndEndDateGreaterThan(long userId,
                                                                               LocalDateTime currentDateTimeOne,
                                                                               LocalDateTime currentDateTimeTwo,
                                                                               Pageable pageable);

    Page<Booking> findByItemOwnerId(long userId, Pageable pageable);

    Page<Booking> findByItemOwnerIdAndEndDateBefore(long userId, LocalDateTime currentDateTime,
                                                    Pageable pageable);

    Page<Booking> findByItemOwnerIdAndStartDateAfter(long userId, LocalDateTime currentDateTime,
                                                     Pageable pageable);

    Page<Booking> findByItemOwnerIdAndStatusIs(long userId, Status status, Pageable pageable);

    Page<Booking> findByItemOwnerIdAndStartDateLessThanEqualAndEndDateGreaterThan(long userId,
                                                                                  LocalDateTime currentDateTimeOne,
                                                                                  LocalDateTime currentDateTimeTwo,
                                                                                  Pageable pageable);

    List<Booking> findAllByItemIdAndStartDateIsAfterOrderByStartDateDesc(long itemId, LocalDateTime currentDateTime);

    List<Booking> findAllByItemIdAndEndDateIsBeforeOrderByEndDateDesc(long itemId, LocalDateTime currentDateTime);

    List<Booking> findAllBookingsByItemIdAndBookerIdAndEndDateBeforeAndStatusOrderByStartDateDesc(Long itemId, Long userId,
                                                                                                  LocalDateTime now, Status status);
}
