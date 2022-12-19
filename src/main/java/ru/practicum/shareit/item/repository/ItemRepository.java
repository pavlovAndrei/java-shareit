package ru.practicum.shareit.item.repository;

import java.util.List;
import java.util.Optional;

import ru.practicum.shareit.item.Item;

public interface ItemRepository {

    List<Item> findAllByUserId(long userId);

    Optional<Item> findById(long itemId);

    Item add(Item item, long userId);

    Item update(Item item, long itemId, long userId);

    List<Item> search(String searchText);
}
