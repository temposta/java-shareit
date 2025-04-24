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

    Collection<Booking> findAllByBookerIdAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(Long bookerId, LocalDateTime now, LocalDateTime now1);

    Collection<Booking> findAllByBookerIdAndEndDateBeforeOrderByStartDateDesc(Long bookerId, LocalDateTime now);

    Collection<Booking> findAllByBookerIdAndStartDateAfterOrderByStartDateDesc(Long bookerId, LocalDateTime now);

    Collection<Booking> findAllByBookerIdAndStatusOrderByStartDateDesc(Long bookerId, StatusEnum status);

    Collection<Booking> findAllByItemOwnerIdOrderByStartDateDesc(Long ownerId);

    Collection<Booking> findAllByItemOwnerIdAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(Long ownerId, LocalDateTime now, LocalDateTime now1);

    Collection<Booking> findAllByItemOwnerIdAndEndDateBeforeOrderByStartDateDesc(Long ownerId, LocalDateTime now);

    Collection<Booking> findAllByItemOwnerIdAndStartDateAfterOrderByStartDateDesc(Long ownerId, LocalDateTime now);

    Collection<Booking> findAllByItemOwnerIdAndStatusOrderByStartDateDesc(Long ownerId, StatusEnum status);

    Optional<Booking> findByItemIdAndBookerIdAndStartDateBeforeAndStatus(Long itemId, Long bookerId, LocalDateTime endDate, StatusEnum status);
}
