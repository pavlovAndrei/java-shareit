package ru.practicum.shareit.booking;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.time.LocalDateTime.parse;
import static java.time.format.DateTimeFormatter.ofPattern;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.data.domain.Sort.Direction.DESC;

import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static ru.practicum.shareit.common.CustomPageRequest.of;

@AutoConfigureTestDatabase
@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingRepositoryTest {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    private Booking bookingInPastAndApprovedAndEndDateFiveDaysBefore;
    private Booking bookingInPastAndRejectedAndEndDateThreeDaysBefore;
    private Booking bookingInCurrentAndApprovedAndEndDateInFiveDays;
    private Booking bookingInFutureAndApprovedAndEndDateInSevenDays;
    private Booking bookingInFutureAndWaitingAndEndDateInFifteenDays;
    private Booking bookingByBookerTwoInFutureAndWaitingAndEndDateInTenDays;

    private Item itemOne;
    private Item itemTwo;
    private Item itemThree;

    private User owner;
    private User bookerOne;

    private final DateTimeFormatter formatter = ofPattern("yyyy-MM-dd:HH-mm");
    private final LocalDateTime localDateTimeNow = parse(LocalDateTime.now().format(formatter), formatter);

    @BeforeEach
    void setUp() {
        owner = userRepository.save(User.builder()
                .name("Test owner")
                .email("ownerOne@yandex.ru")
                .build());

        bookerOne = userRepository.save(User.builder()
                .name("Test booker one")
                .email("bookerOne@yandex.ru")
                .build());

        User bookerTwo = userRepository.save(User.builder()
                .name("Test booker two")
                .email("bookerTwo@yandex.ru")
                .build());

        itemOne = itemRepository.save(Item.builder()
                .name("Test item one")
                .description("Test description one")
                .available(true)
                .ownerId(owner.getId())
                .build());

        itemTwo = itemRepository.save(Item.builder()
                .name("Test item two")
                .description("Test description two")
                .available(true)
                .ownerId(owner.getId())
                .build());

        itemThree = itemRepository.save(Item.builder()
                .name("Test item three")
                .description("Test description three")
                .available(true)
                .ownerId(owner.getId())
                .build());

        bookingInPastAndApprovedAndEndDateFiveDaysBefore = bookingRepository.save(Booking.builder()
                .startDate(localDateTimeNow.minusDays(10))
                .endDate(localDateTimeNow.minusDays(5))
                .booker(bookerOne)
                .item(itemOne)
                .status(Status.APPROVED)
                .build());

        bookingInPastAndRejectedAndEndDateThreeDaysBefore = bookingRepository.save(Booking.builder()
                .startDate(localDateTimeNow.minusDays(10))
                .endDate(localDateTimeNow.minusDays(3))
                .booker(bookerOne)
                .item(itemTwo)
                .status(Status.REJECTED)
                .build());

        bookingInCurrentAndApprovedAndEndDateInFiveDays = bookingRepository.save(Booking.builder()
                .startDate(localDateTimeNow.minusDays(2))
                .endDate(localDateTimeNow.plusDays(5))
                .booker(bookerOne)
                .item(itemThree)
                .status(Status.APPROVED)
                .build());

        bookingInFutureAndApprovedAndEndDateInSevenDays = bookingRepository.save(Booking.builder()
                .startDate(localDateTimeNow.plusDays(2))
                .endDate(localDateTimeNow.plusDays(7))
                .booker(bookerOne)
                .item(itemOne)
                .status(Status.APPROVED)
                .build());

        bookingInFutureAndWaitingAndEndDateInFifteenDays = bookingRepository.save(Booking.builder()
                .startDate(localDateTimeNow.plusDays(10))
                .endDate(localDateTimeNow.plusDays(15))
                .booker(bookerOne)
                .item(itemTwo)
                .status(Status.WAITING)
                .build());

        bookingByBookerTwoInFutureAndWaitingAndEndDateInTenDays = bookingRepository.save(Booking.builder()
                .startDate(localDateTimeNow.plusDays(1))
                .endDate(localDateTimeNow.plusDays(10))
                .booker(bookerTwo)
                .item(itemOne)
                .status(Status.WAITING)
                .build());
    }

    @Test
    void findByBookerIdOrderByEndDateDesc() {
        Pageable pageable = of(1, 2, DESC, "endDate");

        var actualList = bookingRepository.findByBookerId(bookerOne.getId(), pageable).getContent();

        var expectedList = List.of(bookingInFutureAndWaitingAndEndDateInFifteenDays,
                bookingInFutureAndApprovedAndEndDateInSevenDays);

        assertThat(actualList).isEqualTo(expectedList);
    }

    @Test
    void findAllByBookerIdAndEndDateBeforeOrderByStartDateDesc() {
        Pageable pageable = of(0, 10, DESC, "startDate");

        var actualList = bookingRepository.findAllByBookerIdAndEndDateBefore(bookerOne.getId(),
                LocalDateTime.now(), pageable).getContent();

        var expectedList = List.of(bookingInPastAndApprovedAndEndDateFiveDaysBefore,
                bookingInPastAndRejectedAndEndDateThreeDaysBefore);

        assertThat(actualList).isEqualTo(expectedList);
    }

    @Test
    void findByBookerIdAndStartDateAfterOrderByEndDateDesc() {
        Pageable pageable = of(0, 10, DESC, "endDate");

        var actualList = bookingRepository.findByBookerIdAndStartDateAfter(bookerOne.getId(),
                LocalDateTime.now(), pageable).getContent();

        var expectedList = List.of(bookingInFutureAndWaitingAndEndDateInFifteenDays,
                bookingInFutureAndApprovedAndEndDateInSevenDays);

        assertThat(actualList).isEqualTo(expectedList);
    }

    @Test
    void findAllByBookerIdAndStatusIsOrderByStartDateDesc() {
        Pageable pageable = of(0, 10, DESC, "startDate");

        var actualList = bookingRepository.findAllByBookerIdAndStatusIs(bookerOne.getId(),
                Status.WAITING, pageable).getContent();

        assertThat(actualList).isEqualTo(List.of(bookingInFutureAndWaitingAndEndDateInFifteenDays));
    }

    @Test
    void findByBookerIdAndStartDateLessThanEqualAndEndDateGreaterThanOrderByEndDateDesc() {
        Pageable pageable = of(0, 10, DESC, "endDate");

        var actualList = bookingRepository
                .findByBookerIdAndStartDateLessThanEqualAndEndDateGreaterThan(bookerOne.getId(),
                        localDateTimeNow.minusDays(2), localDateTimeNow.minusDays(5), pageable).getContent();

        var expectedList = List.of(bookingInCurrentAndApprovedAndEndDateInFiveDays,
                bookingInPastAndRejectedAndEndDateThreeDaysBefore);

        assertThat(actualList).isEqualTo(expectedList);
    }

    @Test
    void findByItemOwnerIdOrderByEndDateDesc() {
        Pageable pageable = of(1, 2, DESC, "endDate");

        var actualList = bookingRepository.findByItemOwnerId(owner.getId(),
                pageable).getContent();

        var expectedList = List.of(bookingInFutureAndWaitingAndEndDateInFifteenDays,
                bookingByBookerTwoInFutureAndWaitingAndEndDateInTenDays);

        assertThat(actualList).isEqualTo(expectedList);
    }

    @Test
    void findByItemOwnerIdAndEndDateBeforeOrderByStartDateDesc() {
        Pageable pageable = of(0, 10, DESC, "startDate");

        var actualList = bookingRepository.findByItemOwnerIdAndEndDateBefore(owner.getId(),
                localDateTimeNow.minusDays(2), pageable).getContent();

        var expectedList = List.of(bookingInPastAndApprovedAndEndDateFiveDaysBefore,
                bookingInPastAndRejectedAndEndDateThreeDaysBefore);

        assertThat(actualList).isEqualTo(expectedList);
    }

    @Test
    void findByItemOwnerIdAndStartDateAfterOrderByEndDateDesc() {
        Pageable pageable = of(0, 10, DESC, "endDate");

        var actualList = bookingRepository.findByItemOwnerIdAndStartDateAfter(owner.getId(),
                localDateTimeNow.plusDays(2), pageable).getContent();

        assertThat(actualList).isEqualTo(List.of(bookingInFutureAndWaitingAndEndDateInFifteenDays));
    }

    @Test
    void findByItemOwnerIdAndStatusIsOrderByStartDateDesc() {
        Pageable pageable = of(0, 10, DESC, "startDate");

        var actualList = bookingRepository.findByItemOwnerIdAndStatusIs(owner.getId(),
                Status.WAITING, pageable).getContent();

        var expectedList = List.of(bookingInFutureAndWaitingAndEndDateInFifteenDays,
                bookingByBookerTwoInFutureAndWaitingAndEndDateInTenDays);

        assertThat(actualList).isEqualTo(expectedList);
    }

    @Test
    void findByItemOwnerIdAndStartDateLessThanEqualAndEndDateGreaterThanOrderByEndDateDesc() {
        Pageable pageable = of(0, 10, DESC, "endDate");

        var actualList = bookingRepository
                .findByItemOwnerIdAndStartDateLessThanEqualAndEndDateGreaterThan(owner.getId(),
                        localDateTimeNow.minusDays(3), localDateTimeNow.minusDays(6), pageable).getContent();

        var expectedList = List.of(bookingInPastAndRejectedAndEndDateThreeDaysBefore,
                bookingInPastAndApprovedAndEndDateFiveDaysBefore);

        assertThat(actualList).isEqualTo(expectedList);
    }

    @Test
    void findAllByItemIdAndStartDateIsAfterOrderByStartDateDesc() {
        var actualList = bookingRepository
                .findAllByItemIdAndStartDateIsAfterOrderByStartDateDesc(itemOne.getId(),
                        localDateTimeNow.minusDays(10));

        var expectedList = List.of(bookingInFutureAndApprovedAndEndDateInSevenDays,
                bookingByBookerTwoInFutureAndWaitingAndEndDateInTenDays);

        assertThat(actualList).isEqualTo(expectedList);
    }

    @Test
    void findAllByItemIdAndEndDateIsBeforeOrderByEndDateDesc() {
        var actualList = bookingRepository
                .findAllByItemIdAndEndDateIsBeforeOrderByEndDateDesc(itemThree.getId(),
                        localDateTimeNow.plusDays(8));

        assertThat(actualList).isEqualTo(List.of(bookingInCurrentAndApprovedAndEndDateInFiveDays));
    }

    @Test
    void findAllBookingsByItemIdAndBookerIdAndEndDateBeforeAndStatusOrderByStartDateDesc() {
        var actualList = bookingRepository
                .findAllBookingsByItemIdAndBookerIdAndEndDateBeforeAndStatusOrderByStartDateDesc(itemTwo.getId(),
                        bookerOne.getId(), localDateTimeNow.minusDays(2), Status.REJECTED);

        assertThat(actualList).isEqualTo(List.of(bookingInPastAndRejectedAndEndDateThreeDaysBefore));
    }

    @AfterEach
    void wipeData() {
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }
}
