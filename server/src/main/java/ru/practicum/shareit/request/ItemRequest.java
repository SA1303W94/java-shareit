package ru.practicum.shareit.request;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "requests")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "description", nullable = false)
    private String description;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id")
    private User requester;

    @CreationTimestamp
    private LocalDateTime created;

    @Transient
    private List<Item> items;
}