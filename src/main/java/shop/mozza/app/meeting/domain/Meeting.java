package shop.mozza.app.meeting.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import shop.mozza.app.login.user.domain.User;


@Entity
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Meeting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column(nullable = false)
    private Boolean isDeleted;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private User creator;

    @Column
    private String URL;

    //모임의 확정된 날짜/시간으로, null일 경우 not confirmed 모임이다.
    @Column
    private LocalDateTime ConfirmedDateTime;

    // 원하는 인원만큼 가능한 모임 시간을 제출하면 알림을 보내준다. null일 경우 알림을 보내지 않는다.
    @Column
    private Integer notification;

    @Column
    private Boolean onlyDate;

    @Column
    private Integer NumberofVoter;

    @OneToMany(mappedBy = "meeting", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DateTimeInfo> dateTimeInfos = new ArrayList<>();


}
