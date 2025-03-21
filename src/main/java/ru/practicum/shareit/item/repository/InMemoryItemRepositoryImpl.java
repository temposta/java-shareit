package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Реализация в памяти DAO
 */
@Repository
public class InMemoryItemRepositoryImpl implements ItemRepository {

    private final Map<Long, Item> items = new HashMap<>();
    private long currentItemId = 0L;

    @Override
    public Item create(Item item) {
        item.setId(nextId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item save(Item patchItem) {
        return items.replace(patchItem.getId(), patchItem);
    }

    @Override
    public void deleteById(long itemId) {
        items.remove(itemId);
    }

    @Override
    public Optional<Item> findById(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public List<Item> findItemsByOwnerId(long ownerId) {
        return items.values()
                .stream()
                .filter(item -> item.getOwner() == ownerId)
                .toList();
    }

    @Override
    public List<Item> findAllWithTextFilter(String text) {
        if(text.isBlank()) return List.of();
        return items.values()
                .stream()
                .filter(item -> item.getAvailable() &&
                                (item.getName().toUpperCase().contains(text.toUpperCase()) ||
                                 item.getDescription().toUpperCase().contains(text.toUpperCase())))
                .toList();
    }

    private long nextId() {
        return ++currentItemId;
    }

}
