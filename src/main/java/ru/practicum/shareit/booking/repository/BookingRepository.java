package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.enums.StatusEnum;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Collection<Booking> findAllByBookerIdOrderByStartDateDesc(Long id);

    Collection<Booking> findAllByBookerIdAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(Long booker_id, LocalDateTime now, LocalDateTime now1);

    Collection<Booking> findAllByBookerIdAndEndDateBeforeOrderByStartDateDesc(Long booker_id, LocalDateTime now);

    Collection<Booking> findAllByBookerIdAndStartDateAfterOrderByStartDateDesc(Long booker_id, LocalDateTime now);

    Collection<Booking> findAllByBookerIdAndStatusOrderByStartDateDesc(Long booker_id, StatusEnum status);

    Collection<Booking> findAllByItemOwnerIdOrderByStartDateDesc(Long owner_id);

    Collection<Booking> findAllByItemOwnerIdAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(Long owner_id, LocalDateTime now, LocalDateTime now1);

    Collection<Booking> findAllByItemOwnerIdAndEndDateBeforeOrderByStartDateDesc(Long owner_id, LocalDateTime now);

    Collection<Booking> findAllByItemOwnerIdAndStartDateAfterOrderByStartDateDesc(Long owner_id, LocalDateTime now);

    Collection<Booking> findAllByItemOwnerIdAndStatusOrderByStartDateDesc(Long owner_id, StatusEnum status);

    Optional<Booking> findByItemIdAndBookerIdAndStartDateBeforeAndStatus(Long item_id, Long booker_id, LocalDateTime endDate, StatusEnum status);
}
