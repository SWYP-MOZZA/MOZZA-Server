package shop.mozza.app.timeinfo.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.*;

import java.time.LocalDate;

@Entity
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DateInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private LocalDate date;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private TimeInfo timeInfo;
}
