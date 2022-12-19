package ru.practicum.shareit.item.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

import org.springframework.stereotype.Repository;

import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;

import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.item.Item;

@Repository
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {

    private final Map<Long, List<Item>> items = new HashMap<>();
    private final Map<Long, Item> storage = new LinkedHashMap<>();
    private int counter = 1;

    @Override
    public List<Item> findAllByUserId(long userId) {
        return items.getOrDefault(userId, emptyList());
    }

    @Override
    public Optional<Item> findById(long itemId) {
        return ofNullable(storage.get(itemId));
    }

    @Override
    public Item add(Item item, long userId) {
        item.setId(counter);
        item.setOwner(userId);
        counter++;

        items.compute(userId, (id, list) -> {
            if (isNull(list)) {
                list = new ArrayList<>();
            }
            list.add(item);
            return list;
        });

        storage.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item item, long itemId, long userId) {
        item.setId(itemId);
        item.setOwner(userId);

        items.get(userId).removeIf(i -> i.getId() == itemId);
        items.get(userId).add(item);

        storage.put(itemId, item);
        return item;
    }

    @Override
    public List<Item> search(String searchText) {
        return storage.values()
                .stream()
                .filter(item -> {
                    if (isTrue(item.getAvailable())) {
                        return containsIgnoreCase(item.getName(), searchText)
                                || containsIgnoreCase(item.getDescription(), searchText);
                    }
                    return false;
                })
                .collect(toList());
    }
}
