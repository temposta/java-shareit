package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemRequestWithItems;

import java.util.List;

@Repository
public interface ItemRequestWithItemsRepository extends JpaRepository<ItemRequestWithItems, Long> {

    List<ItemRequestWithItems> findAllByRequestorIdOrderByCreatedDateDesc(Long requestorId);

    List<ItemRequestWithItems> findAllByRequestorIdIsNotOrderByCreatedDateDesc(Long requestorId);
}
