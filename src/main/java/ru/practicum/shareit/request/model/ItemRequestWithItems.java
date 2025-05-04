package ru.practicum.shareit.request.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.model.ItemSimple;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "requests")
public class ItemRequestWithItems {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 512)
    @Column(name = "description", length = 512)
    private String description;

    @Column(name = "requestor_id")
    private Long requestorId;

    @NotNull
    @Column(name = "created_date", nullable = false)
    private Timestamp createdDate;

    @OneToMany
    @JoinColumn(name = "request_id")
    private List<ItemSimple> items;

}