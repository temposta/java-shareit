package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.ItemOwnerView;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemOwnerViewRepository extends ReadOnlyRepository<ItemOwnerView, Long> {

    List<ItemOwnerView> findAllByOwnerId(Long ownerId);

    Optional<ItemOwnerView> findById(Long itemId);
}
