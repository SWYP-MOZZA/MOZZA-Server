package shop.mozza.app.login.user.domain;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;


@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    @CreatedDate
    private LocalDateTime createdAt;

    @Column
    private Boolean isMember;

    @Column
    private String role;

    @Builder
    public User(String name, Boolean isMember, String role) {
        this.name = name;
        this.isMember = isMember;
        this.role = role;
    }


}
