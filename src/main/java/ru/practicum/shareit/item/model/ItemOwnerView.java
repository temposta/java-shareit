package ru.practicum.shareit.item.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import org.hibernate.annotations.Immutable;

import java.time.LocalDateTime;
import java.util.List;

@Immutable
@Entity
@Table(name = "items_owner_dto")
@Getter
public class ItemOwnerView {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "is_available")
    private Boolean available;

    @Column(name = "owner_id")
    private Long ownerId;

    @Column(name = "lastbooking")
    private LocalDateTime lastBooking;

    @Column(name = "nextbooking")
    private LocalDateTime nextBooking;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "item_id")
    private List<CommentSimple> comments;
}