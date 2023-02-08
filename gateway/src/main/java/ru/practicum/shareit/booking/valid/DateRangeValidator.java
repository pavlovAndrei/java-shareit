package ru.practicum.shareit.booking.valid;

import ru.practicum.shareit.booking.dto.BookingPostDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class DateRangeValidator implements ConstraintValidator<ValidDateRange, BookingPostDto> {

    @Override
    public void initialize(ValidDateRange constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(BookingPostDto bookingPostDto, ConstraintValidatorContext constraintValidatorContext) {
        LocalDateTime start = bookingPostDto.getStart();
        LocalDateTime end = bookingPostDto.getEnd();
        return start.isBefore(end);
    }
}

