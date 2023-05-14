package ru.practicum.event.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import ru.practicum.category.model.Category;
import ru.practicum.enums.State;
import ru.practicum.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String annotation;
    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    Category category;
    @Transient
    Long confirmedRequests;
    @CreatedDate
    LocalDateTime createdOn;
    String description;
    LocalDateTime eventDate;
    @ManyToOne
    @JoinColumn(name = "initiator_id", referencedColumnName = "id")
    User initiator;
    @ManyToOne
    @JoinColumn(name = "location_id", referencedColumnName = "id")
    Location location;
    Boolean paid;
    Long participantLimit;
    LocalDateTime publishedOn;
    Boolean requestModeration;
    @Enumerated(EnumType.STRING)
    State state;
    String title;
    @Transient
    Long views = 0L;
    @Transient
    Long comments;
}
