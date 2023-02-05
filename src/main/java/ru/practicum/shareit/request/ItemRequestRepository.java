package ru.practicum.shareit.request;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findAllByRequestorIdOrderByCreatedDesc(Long userId);

    Page<ItemRequest> findAllByRequestorIdIsNotOrderByCreatedDesc(Long userId, Pageable pageable);
}
