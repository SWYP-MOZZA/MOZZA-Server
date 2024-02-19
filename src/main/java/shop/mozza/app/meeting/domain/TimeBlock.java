package shop.mozza.app.meeting.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalTime;

@Entity
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TimeBlock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalTime submitTime;

    @Column(nullable = false)
    private String participantName;
}
