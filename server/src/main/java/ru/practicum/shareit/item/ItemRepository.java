package ru.practicum.shareit.item;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findItemsByOwnerId(long userId, PageRequest page);

    List<Item> findDistinctItemByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String nameText,
                                                                                           String descriptionText,
                                                                                           PageRequest page);

    @Query("SELECT item " +
            "FROM Item AS item " +
            "JOIN item.owner AS o " +
            "WHERE o.id = ?1 " +
            "AND item.id = ?2")
    Optional<Item> findItemByOwnerIdAndItemId(long userId, long itemId);

    List<Item> findItemsByRequestId(long requestId);

    List<Item> findItemsByRequestIdIn(List<Long> requestIds);

    void deleteById(long itemId);
}