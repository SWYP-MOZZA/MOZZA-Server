package shop.mozza.app.meeting.domain;

import jakarta.persistence.*;
import lombok.*;
import shop.mozza.app.user.domain.User;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Meeting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Boolean isConfirmed;

    @Column(nullable = false)
    private Boolean isDeleted;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private User creatorId;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private List<User> member;

    @Column
    private LocalTime confirmedTime;

    @Column
    private LocalDate confirmedDate;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private TimeBlock timeblock;

    @Column
    private String URL;

    @Column
    private Long submitUserNumber;

    @Column
    private String availableUser;

    @Column
    private Boolean ableKaKaoNotification;

}
