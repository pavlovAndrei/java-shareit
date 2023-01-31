package ru.practicum.shareit.booking;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Page<Booking> findByBookerIdOrderByEndDateDesc(long userId, Pageable pageable);

    Page<Booking> findAllByBookerIdAndEndDateBeforeOrderByStartDateDesc(long userId, LocalDateTime currentDateTime,
                                                                        Pageable pageable);

    Page<Booking> findByBookerIdAndStartDateAfterOrderByEndDateDesc(long userId, LocalDateTime currentDateTime,
                                                                    Pageable pageable);

    Page<Booking> findAllByBookerIdAndStatusIsOrderByStartDateDesc(long userId, Status status,
                                                                   Pageable pageable);

    Page<Booking> findByBookerIdAndStartDateLessThanEqualAndEndDateGreaterThanOrderByEndDateDesc(long userId,
                                                                                                 LocalDateTime currentDateTimeOne,
                                                                                                 LocalDateTime currentDateTimeTwo,
                                                                                                 Pageable pageable);

    Page<Booking> findByItemOwnerIdOrderByEndDateDesc(long userId, Pageable pageable);

    Page<Booking> findByItemOwnerIdAndEndDateBeforeOrderByStartDateDesc(long userId, LocalDateTime currentDateTime,
                                                                        Pageable pageable);

    Page<Booking> findByItemOwnerIdAndStartDateAfterOrderByEndDateDesc(long userId, LocalDateTime currentDateTime,
                                                                       Pageable pageable);

    Page<Booking> findByItemOwnerIdAndStatusIsOrderByStartDateDesc(long userId, Status status, Pageable pageable);

    Page<Booking> findByItemOwnerIdAndStartDateLessThanEqualAndEndDateGreaterThanOrderByEndDateDesc(long userId,
                                                                                                    LocalDateTime currentDateTimeOne,
                                                                                                    LocalDateTime currentDateTimeTwo,
                                                                                                    Pageable pageable);

    List<Booking> findAllByItemIdAndStartDateIsAfterOrderByStartDateDesc(long itemId, LocalDateTime currentDateTime);

    List<Booking> findAllByItemIdAndEndDateIsBeforeOrderByEndDateDesc(long itemId, LocalDateTime currentDateTime);

    List<Booking> findAllBookingsByItemIdAndBookerIdAndEndDateBeforeAndStatus(Long itemId, Long userId,
                                                                              LocalDateTime now, Status status,
                                                                              Sort sort);
}
