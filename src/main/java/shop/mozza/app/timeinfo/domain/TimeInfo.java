package shop.mozza.app.timeinfo.domain;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.*;

import java.time.LocalTime;


@Entity
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TimeInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private LocalTime time;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private TimeInfoParticipantName timeInfoParticipantNames;

    @Column
    private Long creatorId;

    @Column
    private String participants;
}
