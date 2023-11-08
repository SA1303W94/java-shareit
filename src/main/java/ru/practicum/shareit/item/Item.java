package ru.practicum.shareit.item;

import lombok.*;
import ru.practicum.shareit.booking.BookingDto;
import ru.practicum.shareit.item.comment.CommentDto;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "items")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "is_available", nullable = false)
    private Boolean available;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @Transient
    private BookingDto lastBooking;

    @Transient
    private BookingDto nextBooking;

    @Transient
    private List<CommentDto> comments;
}