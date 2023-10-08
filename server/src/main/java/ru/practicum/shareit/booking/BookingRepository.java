package ru.practicum.shareit.booking;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT booking " +
            "FROM Booking AS booking " +
            "JOIN booking.item AS i " +
            "WHERE i.id = ?1 " +
            "ORDER BY booking.start ASC")
    List<Booking> findBookingsForItem(long itemId);

    @Query("SELECT booking " +
            "FROM Booking AS booking " +
            "JOIN booking.item AS i " +
            "WHERE i.id IN (?1) " +
            "ORDER BY booking.start ASC")
    List<Booking> findBookingsForItemIn(List<Long> itemId);

    @Query("SELECT booking " +
            "FROM Booking AS booking " +
            "JOIN booking.booker AS b " +
            "WHERE b.id = ?1 " +
            "ORDER BY booking.start DESC")
    List<Booking> findBookingsForUser(long userId, PageRequest page);

    @Query("SELECT booking " +
            "FROM Booking AS booking " +
            "JOIN booking.booker AS b " +
            "WHERE b.id = ?1 AND booking.status = ?2 " +
            "ORDER BY booking.start DESC")
    List<Booking> findBookingsByStatusForUser(long userId, BookingStatus status, PageRequest page);

    @Query("SELECT booking " +
            "FROM Booking AS booking " +
            "JOIN booking.item AS i " +
            "JOIN i.owner AS o " +
            "WHERE o.id = ?1 " +
            "ORDER BY booking.start DESC")
    List<Booking> findBookingsForOwner(long userId, PageRequest page);

    @Query("SELECT booking " +
            "FROM Booking AS booking " +
            "JOIN booking.item AS i " +
            "JOIN i.owner AS o " +
            "WHERE o.id = ?1 AND booking.status = ?2 " +
            "ORDER BY booking.start DESC")
    List<Booking> findBookingsByStatusForOwner(long ownerId, BookingStatus status, PageRequest page);
}