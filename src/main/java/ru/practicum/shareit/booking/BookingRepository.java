package ru.practicum.shareit.booking;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerIdOrderByEndDateDesc(long userId);

    List<Booking> findAllByBookerIdAndEndDateBeforeOrderByStartDateDesc(long userId, LocalDateTime currentDateTime);

    List<Booking> findByBookerIdAndStartDateAfterOrderByEndDateDesc(long userId, LocalDateTime currentDateTime);

    List<Booking> findAllByBookerIdAndStatusIsOrderByStartDateDesc(long userId, Status status);

    List<Booking> findByBookerIdAndStartDateLessThanEqualAndEndDateGreaterThanOrderByEndDateDesc(long userId,
                                                                                                 LocalDateTime currentDateTimeOne,
                                                                                                 LocalDateTime currentDateTimeTwo);

    List<Booking> findByItemOwnerIdOrderByEndDateDesc(long userId);

    List<Booking> findByItemOwnerIdAndEndDateBeforeOrderByStartDateDesc(long userId, LocalDateTime currentDateTime);

    List<Booking> findByItemOwnerIdAndStartDateAfterOrderByEndDateDesc(long userId, LocalDateTime currentDateTime);

    List<Booking> findByItemOwnerIdAndStatusIsOrderByStartDateDesc(long userId, Status status);

    List<Booking> findByItemOwnerIdAndStartDateLessThanEqualAndEndDateGreaterThanOrderByEndDateDesc(long userId,
                                                                                                    LocalDateTime currentDateTimeOne,
                                                                                                    LocalDateTime currentDateTimeTwo);

    List<Booking> findAllByItemIdAndStartDateIsAfterOrderByStartDateDesc(long itemId, LocalDateTime currentDateTime);

    List<Booking> findAllByItemIdAndEndDateIsBeforeOrderByEndDateDesc(long itemId, LocalDateTime currentDateTime);

    List<Booking> findAllBookingsByItemIdAndBookerIdAndEndDateBeforeAndStatus(Long itemId, Long userId,
                                                                              LocalDateTime now, Status status,
                                                                              Sort sort);
}
