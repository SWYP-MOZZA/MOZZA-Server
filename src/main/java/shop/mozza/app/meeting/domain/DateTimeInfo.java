package shop.mozza.app.meeting.domain;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;



@Entity
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DateTimeInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private LocalDateTime datetime;

    @Column
    private LocalDate date;
    @Column
    private Boolean isConfirmed;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private Meeting meeting;

    public void updateIsConfirmed(Boolean isConfirmed){
        this.isConfirmed = isConfirmed;
    }
}
