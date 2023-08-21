package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("SELECT item " +
            "FROM Item AS item " +
            "JOIN item.owner AS o " +
            "WHERE o.id = ?1 " +
            "ORDER BY item.id ASC")
    List<Item> findItemsByOwnerId(long userId);

    List<Item> findDistinctItemByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String nameText,
                                                                                           String descriptionText);

    @Query("SELECT item " +
            "FROM Item AS item " +
            "JOIN item.owner AS o " +
            "WHERE o.id = ?1 " +
            "AND item.id = ?2")
    Optional<Item> findItemsByOwnerIdAndItemId(long userId, long itemId);

    void deleteById(long itemId);
}