package ru.practicum.shareit.item.service;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static java.time.LocalDateTime.now;
import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;


import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import static org.apache.commons.lang3.StringUtils.isNoneBlank;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.common.CustomPageRequest;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemGetDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static ru.practicum.shareit.booking.model.Status.APPROVED;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;
    private final CommentMapper commentMapper;
    private final ItemMapper itemMapper;

    @Override
    public List<ItemGetDto> findAllByUserId(long userId, Integer offset, Integer size) {
        throwIfUserDoesntExist(userId);

        Pageable pageable = CustomPageRequest.of(offset, size);

        List<Item> items = itemRepository.findByOwnerIdOrderByIdAsc(userId, pageable).getContent();
        List<ItemGetDto> foundItems = new ArrayList<>();

        items.forEach(item -> {
            ItemGetDto itemGetDto = itemMapper.toItemGetDto(item);

            BookingDtoForItem next = getNextBooking(itemGetDto.getId())
                    .stream()
                    .findFirst()
                    .orElse(null);
            BookingDtoForItem last = getLastBooking(itemGetDto.getId())
                    .stream()
                    .findFirst()
                    .orElse(null);

            itemGetDto.setNextBooking(next);
            itemGetDto.setLastBooking(last);
            addComments(item, itemGetDto);

            foundItems.add(itemGetDto);
        });

        return foundItems;
    }

    @Override
    public ItemGetDto getById(long itemId, long userId) {
        throwIfUserDoesntExist(userId);

        var item = getItemById(itemId);

        ItemGetDto itemGetDto = itemMapper.toItemGetDto(item);

        if (userId == item.getOwnerId()) {
            BookingDtoForItem next = getNextBooking(item.getId())
                    .stream()
                    .findFirst()
                    .orElse(null);
            BookingDtoForItem last = getLastBooking(item.getId())
                    .stream()
                    .findFirst()
                    .orElse(null);

            itemGetDto.setNextBooking(next);
            itemGetDto.setLastBooking(last);
        }

        log.info("Item '{}' is successfully retrieved", item.getName());
        return addComments(item, itemGetDto);
    }

    @Override
    public ItemDto add(ItemDto itemDto, long userId) {
        throwIfUserDoesntExist(userId);

        Item item = itemMapper.toItem(itemDto);
        item.setOwnerId(userId);

        itemRepository.save(item);
        log.info("Item '{}' is successfully added", item.getName());
        return itemMapper.toItemDto(item);
    }

    @Override
    public ItemDto update(ItemDto itemDto, long itemId, long userId) {
        var item = getItemById(itemId);

        if (item.getOwnerId() != userId) {
            throw new NotFoundException(format("User with id '%d' doesn't have an item '%s'", userId, item.getName()));
        }

        if (isNoneBlank(itemDto.getName())) {
            item.setName(itemDto.getName());
        }

        if (isNoneBlank(itemDto.getDescription())) {
            item.setDescription(itemDto.getDescription());
        }

        if (nonNull(itemDto.getAvailable())) {
            item.setAvailable(itemDto.getAvailable());
        }

        itemRepository.save(item);

        return itemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> search(long userId, String searchText,
                                Integer offset, Integer size) {
        List<Item> items;

        if (searchText.isBlank()) {
            log.warn("The empty search text was received for searching");
            return emptyList();
        }

        throwIfUserDoesntExist(userId);

        Pageable pageable = CustomPageRequest.of(offset, size);
        items = itemRepository.search(searchText.toLowerCase(), pageable).getContent();

        return items.stream()
                .map(itemMapper::toItemDto)
                .collect(toList());
    }

    @Override
    public CommentDto addComment(long userId, long itemId, CommentDto commentDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new NotFoundException(format("User with id: '%d' does not exist", userId)));

        var item = getItemById(itemId);

        List<Booking> bookings =
                bookingRepository.findAllBookingsByItemIdAndBookerIdAndEndDateBeforeAndStatusOrderByStartDateDesc(item.getId(), userId,
                        now(), APPROVED);

        if (isNull(bookings) || bookings.isEmpty()) {
            throw new BadRequestException("It's prohibited to leave the comment before the expiration date");
        }

        Comment comment = commentMapper.toComment(commentDto, item, user);
        comment.setCreated(now());
        comment = commentRepository.save(comment);

        return commentMapper.toCommentDto(comment);
    }

    public List<BookingDtoForItem> getNextBooking(Long itemId) {
        List<Booking> bookings =
                bookingRepository.findAllByItemIdAndStartDateIsAfterOrderByStartDateDesc(itemId, now());

        return getBookingDtoForItemList(bookings);
    }

    public List<BookingDtoForItem> getLastBooking(Long itemId) {
        List<Booking> bookings = bookingRepository.findAllByItemIdAndEndDateIsBeforeOrderByEndDateDesc(itemId, now());

        return getBookingDtoForItemList(bookings);
    }

    private Item getItemById(long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(format("Item with id '%d' does not exist", id)));
    }

    private void throwIfUserDoesntExist(long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException(format("User with id '%d' does not exist", id));
        }
    }

    private List<BookingDtoForItem> getBookingDtoForItemList(List<Booking> bookings) {
        return bookings.stream()
                .filter(booking -> booking.getStatus().equals(APPROVED))
                .map(bookingMapper::toBookingDtoForItem)
                .collect(toList());
    }


    private ItemGetDto addComments(Item item, ItemGetDto itemGetDto) {
        if (item.getComments().isEmpty()) {
            itemGetDto.setComments(emptyList());
        } else {
            itemGetDto.setComments(new ArrayList<>());
            for (Comment comment : item.getComments()) {
                if (comment.getItem().getId().equals(item.getId())) {
                    CommentDto commentDto = commentMapper.toCommentDto(comment);
                    commentDto.setAuthorName(comment.getAuthor().getName());
                    itemGetDto.getComments().add(commentDto);
                }
            }
        }
        return itemGetDto;
    }
}
