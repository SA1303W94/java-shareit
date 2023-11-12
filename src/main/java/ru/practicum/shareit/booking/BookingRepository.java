package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.Item;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Modifying
    @Query("UPDATE Booking b "
            + "SET b.status = :status  "
            + "WHERE b.id = :bookingId")
    void save(@Param("status") BookingStatus status, @Param("bookingId") Long bookingId);

    List<Booking> findByBookerId(Long id, Sort sort);

    List<Booking> findByBookerIdAndStatusIs(Long id, BookingStatus status, Sort sort);

    List<Booking> findByBookerIdAndEndIsAfterAndStartIsBefore(Long id, LocalDateTime end,
                                                              LocalDateTime start, Sort sort);

    List<Booking> findByBookerIdAndEndIsBefore(Long id, LocalDateTime time, Sort sort);

    List<Booking> findByBookerIdAndStartIsAfter(Long id, LocalDateTime time, Sort sort);

    List<Booking> findByBookerIdAndStartIsAfterAndStatusIs(Long bookerId, LocalDateTime start,
                                                           BookingStatus status, Sort sort);

    @Query("SELECT b FROM Booking b " + "INNER JOIN Item i ON b.item.id = i.id "
            + "WHERE i.ownerId = :ownerId "
            + "ORDER BY b.start DESC")
    List<Booking> findByItemOwnerId(@Param("ownerId") Long ownerId);

    @Query("SELECT b FROM Booking b "
            + "INNER JOIN Item i ON b.item.id = i.id "
            + "WHERE i.ownerId = :ownerId "
            + "AND :time between b.start AND b.end "
            + "ORDER BY b.start DESC")
    List<Booking> findCurrentBookingsOwner(@Param("ownerId") Long ownerId, LocalDateTime time);

    @Query("SELECT b FROM Booking b "
            + "INNER JOIN Item i ON b.item.id = i.id "
            + "WHERE i.ownerId = :ownerId "
            + "AND b.end < :time "
            + "ORDER BY b.start DESC")
    List<Booking> findPastBookingsOwner(@Param("ownerId") Long ownerId, LocalDateTime time);

    @Query("SELECT b FROM Booking b "
            + "INNER JOIN Item i ON b.item.id = i.id "
            + "WHERE i.ownerId = :ownerId "
            + "AND b.start > :time "
            + "ORDER BY b.start DESC")
    List<Booking> findFutureBookingsOwner(@Param("ownerId") Long ownerId, LocalDateTime time);

    @Query("SELECT b FROM Booking b "
            + "INNER JOIN Item i ON b.item.id = i.id "
            + "WHERE i.ownerId = :ownerId "
            + "AND b.start > :time AND b.status = :status "
            + "ORDER BY b.start DESC")
    List<Booking> findWaitingBookingsOwner(@Param("ownerId") Long ownerId, LocalDateTime time, BookingStatus status);

    @Query("SELECT b FROM Booking b "
            + "INNER JOIN Item i ON b.item.id = i.id "
            + "WHERE i.ownerId = :ownerId "
            + "AND b.status = :status "
            + "ORDER BY b.start DESC")
    List<Booking> findRejectedBookingsOwner(@Param("ownerId") Long ownerId, BookingStatus status);

    @Query("SELECT b FROM Booking b "
            + "INNER JOIN Item i ON b.item.id = i.id "
            + "WHERE i.id = :itemId "
            + "ORDER BY b.start DESC")
    List<Booking> findBookingsItem(Long itemId);

    List<Booking> findByItemIdAndBookerIdAndStatusIsAndEndIsBefore(Long itemId,
                                                                   Long bookerId,
                                                                   BookingStatus status,
                                                                   LocalDateTime time);

    List<Booking> findByItemIn(List<Item> items, Sort sort);
}