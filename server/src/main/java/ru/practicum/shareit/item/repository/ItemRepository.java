package ru.practicum.shareit.item.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ru.practicum.shareit.item.model.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Page<Item> findByOwnerIdOrderByIdAsc(Long userId, Pageable pageable);

    @Query("select i from Item i " +
            "where (upper(i.name) like upper(concat('%', ?1, '%')) " +
            " or upper(i.description) like upper(concat('%', ?1, '%'))) " +
            " and i.available = true")
    Page<Item> search(String searchText, Pageable pageable);

    List<Item> findAllByRequestIdIn(List<Long> requestsId);
}
