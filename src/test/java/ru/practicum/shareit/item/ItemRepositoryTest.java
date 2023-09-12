package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:clear-database.sql")
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:schema.sql")
public class ItemRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    public void testFindItemsByOwnerIdShouldReturnEmptyListWhenEmptyDatabase() {
        List<Item> items = itemRepository.findItemsByOwnerId(1L, PageRequest.of(0, 10));

        assertThat(items).isEmpty();
    }

    @Test
    public void testFindItemsByOwnerIdShouldReturnListWithOneItemWhenItemExists() {
        User user = new User(null, "user", "user@yandex.ru", LocalDateTime.of(2023, 1, 1, 12, 0));

        userRepository.save(user);

        Item item = new Item(null, user, "item", "firstItem", true, null, LocalDateTime.of(2023, 1, 1, 20, 0));

        itemRepository.save(item);

        List<Item> items = itemRepository.findItemsByOwnerId(1L, PageRequest.of(0, 10));

        Assertions.assertEquals(item, items.get(0));
    }

    @Test
    public void testSearchItemByNameOrDescriptionShouldReturnEmptyList() {
        User user = new User(null, "user", "user@yandex.ru", LocalDateTime.of(2023, 1, 1, 12, 0));

        userRepository.save(user);

        Item item = new Item(null, user, "item", "firstItem", true, null, LocalDateTime.of(2023, 1, 1, 20, 0));

        itemRepository.save(item);

        List<Item> items = itemRepository
                .findDistinctItemByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase("test",
                        "test", PageRequest.of(0, 10));

        assertThat(items).isEmpty();
    }

    @Test
    public void testSearchItemByNameOrDescriptionShouldReturnListWithOneItem() {
        User user = new User(null, "user", "user@yandex.ru", LocalDateTime.of(2023, 1, 1, 12, 0));

        userRepository.save(user);

        Item item = new Item(null, user, "item", "firstItem", true, null, LocalDateTime.of(2023, 1, 1, 20, 0));

        itemRepository.save(item);

        List<Item> items = itemRepository
                .findDistinctItemByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase("item",
                        "test", PageRequest.of(0, 10));

        Assertions.assertEquals(item, items.get(0));
    }

    @Test
    public void testFindItemByOwnerIdAndItemIdShouldReturnNothingWhenEmptyDatabase() {
        Optional<Item> item = itemRepository.findItemByOwnerIdAndItemId(1L, 1L);

        assertThat(item).isEmpty();
    }

    @Test
    public void testFindItemByOwnerIdAndItemIdShouldReturnItemWhenItemExists() {
        User user = new User(null, "user", "user@yandex.ru", LocalDateTime.of(2023, 1, 1, 12, 0));

        userRepository.save(user);

        Item item = new Item(null, user, "item", "firstItem", true, null, LocalDateTime.of(2023, 1, 1, 20, 0));

        itemRepository.save(item);

        Optional<Item> itemFromDb = itemRepository.findItemByOwnerIdAndItemId(1L, 1L);

        Assertions.assertEquals(Optional.of(item), itemFromDb);
    }

    @Test
    public void testFindItemsByRequestIdShouldReturnListWithOneItemWhenItemExists() {
        User user = new User(null, "user", "user@yandex.ru", LocalDateTime.of(2023, 1, 1, 12, 0));

        userRepository.save(user);

        Item item = new Item(null, user, "item", "firstItem", true, 2L, LocalDateTime.of(2023, 1, 1, 20, 0));

        itemRepository.save(item);

        List<Item> items = itemRepository.findItemsByRequestId(2L);

        Assertions.assertEquals(item, items.get(0));
    }

    @Test
    public void testFindItemsByRequestIdInShouldReturnListWithTwoItemsWhenItemsExist() {
        User user = new User(null, "user", "user@yandex.ru", LocalDateTime.of(2023, 1, 1, 12, 0));

        userRepository.save(user);

        Item item1 = new Item(null, user, "item1", "firstItem", true, 2L, LocalDateTime.of(2023, 1, 1, 20, 0));
        Item item2 = new Item(null, user, "item2", "secondItem", true, 4L, LocalDateTime.of(2023, 1, 1, 20, 0));

        itemRepository.save(item1);
        itemRepository.save(item2);

        List<Item> items = itemRepository.findItemsByRequestIdIn(List.of(2L, 4L));

        Assertions.assertEquals(item1, items.get(0));
        Assertions.assertEquals(item2, items.get(1));
    }

    @Test
    public void testDeleteByIdShouldDeleteItemIfExists() {
        User user = new User(null, "user", "user@yandex.ru", LocalDateTime.of(2023, 1, 1, 12, 0));

        userRepository.save(user);

        Item item = new Item(null, user, "item", "firstItem", true, null, LocalDateTime.of(2023, 1, 1, 20, 0));

        itemRepository.save(item);

        Optional<Item> itemSaved = itemRepository.findById(1L);

        itemRepository.deleteById(1L);

        Optional<Item> itemDeleted = itemRepository.findById(1L);

        Assertions.assertEquals(Optional.of(item), itemSaved);
        assertThat(itemDeleted).isEmpty();
    }
}